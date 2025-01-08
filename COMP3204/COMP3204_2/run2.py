import os

# Replace MiniBatchKMeans with standard KMeans to meet the requirement of "using KMeans instead of MiniBatchKMeans"
# Other processes remain the same, including mean centering and standard deviation normalization of patches,
# using 8x8 patches, and dense sampling with stride=4

import cv2
import numpy as np
from sklearn.cluster import KMeans  # Modification point: use KMeans instead of MiniBatchKMeans
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import TruncatedSVD
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score
from sklearn.model_selection import StratifiedKFold
from scipy.sparse import csr_matrix
from joblib import Parallel, delayed, dump
from skimage.util import view_as_windows
import gc
import re


def extract_patches_efficient(image, patch_size, stride):
    """
    Extract image patches of size patch_size with a given stride.
    After extraction, normalize each patch by subtracting the mean and dividing by the standard deviation.
    """
    windows = view_as_windows(image, patch_size, step=stride)  # Extract sliding windows
    patches = windows.reshape(-1, patch_size[0] * patch_size[1]).astype(np.float32)  # Reshape to (num_patches, patch_size)
    patches = (patches - patches.mean(axis=1, keepdims=True)) / (patches.std(axis=1, keepdims=True) + 1e-8)  # Normalize patches
    return patches

def process_image(file_path, patch_size, stride):
    """
    Load a grayscale image and extract patches using extract_patches_efficient.
    If the image fails to load, return None.
    """
    image = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)  # Read image in grayscale
    if image is not None:
        patches = extract_patches_efficient(image, patch_size, stride)  # Extract and normalize patches
        return patches
    print(f"WARNING: Failed to load image: {file_path}")  # Warning if image loading fails
    return None  # Return None if image loading fails

def load_data_parallel(data_path, patch_size, stride, is_train=True, n_jobs=-1):
    """
    Load training or testing data in parallel, extracting patches from all images.
    If is_train=True, traverse each class folder and get labels;
    If is_train=False, only read all images in the test folder and record their filenames.
    Finally, return features (list of patches) and corresponding labels or filenames.
    """
    features, labels, filenames = [], [], []

    if is_train:
        # Get all class names
        class_labels = sorted([d for d in os.listdir(data_path) if os.path.isdir(os.path.join(data_path, d))])
        for label, class_name in enumerate(class_labels):
            class_folder = os.path.join(data_path, class_name)
            image_files = [f for f in sorted(os.listdir(class_folder)) if f.lower().endswith(('.jpg', '.jpeg'))]
            # Process images in parallel
            results = Parallel(n_jobs=n_jobs)(
                delayed(process_image)(os.path.join(class_folder, f), patch_size, stride)
                for f in image_files
            )
            for patches in results:
                if patches is not None:
                    features.append(patches)  # Append patches
                    labels.append(label)  # Append corresponding label
        return features, np.array(labels), class_labels
    else:
        # Process test images
        image_files = [f for f in sorted(os.listdir(data_path)) if f.lower().endswith(('.jpg', '.jpeg'))]
        results = Parallel(n_jobs=n_jobs)(
            delayed(process_image)(os.path.join(data_path, f), patch_size, stride)
            for f in image_files
        )
        for patches, filename in zip(results, image_files):
            if patches is not None:
                features.append(patches)  # Append patches
                filenames.append(filename)  # Append filename
        return features, filenames

def sample_features(features, labels, sample_size):
    """
    To train KMeans, uniformly sample a certain number of patches from each class for clustering.
    The number of patches sampled per class is approximately sample_size / number of classes.
    If there are not enough patches, use all available patches.
    """
    unique_labels = np.unique(labels)  # Get unique class labels
    samples_per_class = sample_size // len(unique_labels)  # Calculate samples per class
    sampled = []

    for cls in unique_labels:
        # Stack all patches belonging to the current class
        class_patches = np.vstack([features[i] for i in np.where(labels == cls)[0]])
        if len(class_patches) >= samples_per_class:
            # Randomly select samples_per_class patches without replacement
            sampled_patches = class_patches[np.random.choice(len(class_patches), samples_per_class, replace=False)]
        else:
            sampled_patches = class_patches  # Use all patches if not enough
            print(f"Class {cls}: Not enough patches. Using all {len(class_patches)} patches.")
        sampled.append(sampled_patches)

    total_sampled = np.vstack(sampled)
    print(f"Total sampled patches for KMeans: {len(total_sampled)}")
    return total_sampled  # Combine sampled patches from all classes

def create_bovw_sparse(features, kmeans, num_words):
    """
    Based on the trained KMeans, map each patch to a visual word ID, then count the frequency of each word in each image.
    This results in a Bag-of-Visual-Words (BoVW) histogram, which is converted to a sparse format.
    """
    histograms = []
    for idx, patches in enumerate(features, 1):
        words = kmeans.predict(patches)  # Assign patches to clusters
        histogram = np.bincount(words, minlength=num_words).astype(np.float32)  # Count word frequencies
        histograms.append(histogram)
    return csr_matrix(histograms)  # Convert to sparse matrix

def train_kmeans(sampled_features, num_words):
    """
    Train KMeans on the sampled patches to create a visual vocabulary of the specified size.
    Compared to MiniBatchKMeans, standard KMeans is slower and uses more memory but meets the problem requirements.
    """
    print("Starting KMeans clustering...")
    kmeans = KMeans(
        n_clusters=num_words,
        random_state=42,
        max_iter=300,
        n_init=10
    )
    kmeans.fit(sampled_features)  # Fit KMeans
    return kmeans

def train_single_svm(X, y, cls, random_state=42):
    """
    Train a one-vs-all SVM classifier, treating cls as the positive class and all other classes as negative.
    """
    binary_labels = (y == cls).astype(int)  # Create binary labels
    svm = SVC(kernel='linear', probability=True, random_state=random_state)  # Initialize SVM
    svm.fit(X, binary_labels)  # Train SVM
    return svm

def train_svms_parallel(X, y, num_classes, n_jobs=-1):
    """
    Train a one-vs-all SVM classifier for each class in parallel to speed up the process.
    """
    classifiers = Parallel(n_jobs=n_jobs)(
        delayed(train_single_svm)(X, y, cls) for cls in range(num_classes)
    )
    return classifiers

def predict_single_svm_proba(X, clf):
    """
    Get the prediction probability of the SVM for the positive class.
    """
    return clf.predict_proba(X)[:, 1]

def predict_svms_parallel(X, classifiers, n_jobs=-1):
    """
    Compute the positive class probabilities for all SVMs in parallel and select the class with the highest probability as the final prediction.
    """
    probabilities = Parallel(n_jobs=n_jobs)(
        delayed(predict_single_svm_proba)(X, clf) for clf in classifiers
    )
    probabilities = np.array(probabilities).T  # Transpose to shape (num_samples, num_classes)
    predictions = np.argmax(probabilities, axis=1)  # Select class with highest probability
    return predictions

def cross_validate_svms(X, y, num_classes, class_labels, k_folds, n_jobs=-1):
    """
    Perform K-fold cross-validation using StratifiedKFold, record accuracy for each fold, and output the average cross-validation accuracy.
    """
    print(f"Starting {k_folds}-fold cross-validation...")
    skf = StratifiedKFold(n_splits=k_folds, shuffle=True, random_state=42)
    accuracies = []
    all_y_true = []
    all_y_pred = []

    for fold, (train_idx, val_idx) in enumerate(skf.split(X, y), 1):
        X_train, X_val = X[train_idx], X[val_idx]  # Split data
        y_train, y_val = y[train_idx], y[val_idx]

        classifiers = train_svms_parallel(X_train, y_train, num_classes, n_jobs=n_jobs)  # Train classifiers
        y_pred = predict_svms_parallel(X_val, classifiers, n_jobs=n_jobs)  # Predict on validation set
        acc = accuracy_score(y_val, y_pred)  # Calculate accuracy
        print(f"Fold {fold} Accuracy: {acc:.4f}")  # Print fold accuracy
        accuracies.append(acc)
        all_y_true.extend(y_val)  # Collect true labels
        all_y_pred.extend(y_pred)  # Collect predicted labels

    mean_accuracy = np.mean(accuracies)  # Calculate mean accuracy
    print(f"Average Cross-Validation Accuracy: {mean_accuracy:.4f}")  # Print average accuracy
    return mean_accuracy

def save_model(model, filename):
    """
    Save the trained model to disk (can include KMeans or list of SVM classifiers).
    """
    dump(model, filename)
    print(f"INFO: Model saved to {filename}.")

def natural_key(filename):
    """Helper function to extract numerical parts for natural sorting."""
    return [int(part) if part.isdigit() else part for part in re.split(r'(\d+)', filename)]


def main():
    """
    The main function handles the complete workflow:
    1. Load training data and extract patches, sample patches, and train KMeans to build the visual vocabulary.
    2. Construct BoVW features, then perform standardization and SVD for dimensionality reduction.
    3. Perform K-fold cross-validation and train the final SVM classifiers on all training data.
    4. Load test data, apply the same preprocessing and dimensionality reduction, then perform predictions and output the results.
    """
    train_path = "training"  # Path to training data
    test_path = "testing"  # Path to testing data
    patch_size = (5, 5)  # Size of each patch
    stride = 4  # Stride for patch extraction
    num_words = 700  # Number of visual words
    sample_size = 15000  # Number of patches to sample for KMeans
    output_file = "run2.txt"  # Output file for predictions
    components = 45  # Number of components for SVD
    k_folds = 10  # Number of folds for cross-validation
    n_jobs = -1  # Number of parallel jobs (-1 uses all available cores)

    np.random.seed(42)  # Set random seed for reproducibility

    print("Loading training data...")
    features, labels, class_labels = load_data_parallel(train_path, patch_size, stride, is_train=True, n_jobs=n_jobs)

    sampled = sample_features(features, labels, sample_size)  # Sampling features for KMeans clustering

    print("Training visual vocabulary using standard KMeans...")
    kmeans = train_kmeans(sampled, num_words)
    save_model(kmeans, "kmeans_model.joblib")  # Save KMeans model

    print("Creating BoVW sparse histograms for training data...")
    X = create_bovw_sparse(features, kmeans, num_words)

    del features  # Free memory
    del sampled
    gc.collect()

    print("Standardizing features and applying TruncatedSVD for dimensionality reduction...")
    scaler = StandardScaler(with_mean=False)  # Initialize scaler
    X_scaled = scaler.fit_transform(X)  # Fit and transform data
    svd = TruncatedSVD(n_components=components, random_state=42)  # Initialize SVD
    X_pca = svd.fit_transform(X_scaled)  # Fit and transform data

    del X  # Free memory
    del X_scaled
    gc.collect()

    print("Starting cross-validation...")
    cross_validate_svms(X_pca, labels, len(class_labels), class_labels, k_folds, n_jobs=n_jobs)

    print("Training final one-vs-all SVM classifiers on all training data...")
    classifiers = train_svms_parallel(X_pca, labels, len(class_labels), n_jobs=n_jobs)
    save_model(classifiers, "svm_classifiers.joblib")  # Save SVM classifiers

    print("Loading and processing test data...")
    test_features, filenames = load_data_parallel(test_path, patch_size, stride, is_train=False, n_jobs=n_jobs)
    X_test = create_bovw_sparse(test_features, kmeans, num_words)

    del test_features  # Free memory
    gc.collect()

    print("Standardizing test features and applying TruncatedSVD for dimensionality reduction...")
    X_test_scaled = scaler.transform(X_test)  # Transform test data using the same scaler
    X_test_pca = svd.transform(X_test_scaled)  # Transform test data using the same SVD

    del X_test  # Free memory
    del X_test_scaled
    gc.collect()

    print("Predicting on test data...")
    y_test_pred = predict_svms_parallel(X_test_pca, classifiers, n_jobs=n_jobs)  # Get predictions

    # Sort (filename, label) pairs in natural order
    sorted_pairs = sorted(zip(filenames, y_test_pred), key=lambda x: natural_key(x[0]))
    with open(output_file, "w") as f:
        for filename, label in sorted_pairs:
            f.write(f"{filename} {class_labels[label]}\n")  # Write filename and predicted class
    print(f"Prediction results have been saved to {output_file}")

    del X_test_pca  # Free memory
    del classifiers
    gc.collect()

if __name__ == "__main__":
    main()
