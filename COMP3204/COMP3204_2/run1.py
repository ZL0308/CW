import os
import cv2
import numpy as np
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import KFold
from sklearn.metrics import accuracy_score
from sklearn.decomposition import PCA
import joblib
import re



def extract_tiny_image(image, square_side_length, output_size=(16, 16)):
    """Crop center square, resize to fixed size, and normalize pixel values."""
    h, w = image.shape[:2]  # Get image dimensions
    square_side_length = min(square_side_length, h, w)  # Ensure square size fits image

    # Calculate crop coordinates
    top = max(0, h // 2 - square_side_length // 2)
    left = max(0, w // 2 - square_side_length // 2)

    # Crop, resize, and normalize the image
    square_image = image[top:top + square_side_length, left:left + square_side_length]
    resized_image = cv2.resize(square_image, output_size, interpolation=cv2.INTER_AREA)
    normalized_image = resized_image.flatten().astype(np.float32)  # Flatten and convert to float
    return (normalized_image - normalized_image.mean()) / (normalized_image.std() + 1e-8)  # Normalize


def load_data_and_features(data, square_side_length):
    """Load images and extract tiny image features."""
    X, y, class_labels = [], [], sorted(os.listdir(data))  # Initialize variables
    for label, categories_name in enumerate(class_labels):  # Iterate through classes
        categories_folder = os.path.join(data, categories_name)
        if os.path.isdir(categories_folder):
            for filename in os.listdir(categories_folder):  # Iterate through images in class folder
                image_path = os.path.join(categories_folder, filename)
                image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)  # Read image in grayscale
                if image is not None:  # Ensure valid image
                    X.append(extract_tiny_image(image, square_side_length))  # Extract features
                    y.append(label)  # Assign label
    return np.array(X), np.array(y), class_labels  # Return features, labels, and class names


def perform_cross_validation(X, y, k_values, n_splits=10):
    """Evaluate different k values for KNN using cross-validation."""
    kf = KFold(n_splits=n_splits, shuffle=True, random_state=42)  # Initialize K-Fold cross-validator
    best_k, best_accuracy = None, 0  # Track best k and accuracy

    for k in k_values:  # Iterate through k values
        mean_accuracy = np.mean([
            accuracy_score(y[val], KNeighborsClassifier(n_neighbors=k, weights='distance', p=2)
                           .fit(X[train], y[train]).predict(X[val]))  # Train and evaluate KNN
            for train, val in kf.split(X)  # Split data for cross-validation
        ])
        if mean_accuracy > best_accuracy:  # Update best k if current k is better
            best_k, best_accuracy = k, mean_accuracy

    print(f"Best k={best_k}, Cross-Validation Accuracy={best_accuracy:.4f}")  # Output best k and accuracy
    return best_k, best_accuracy  # Return best k and accuracy

def natural_key(filename):
    """Helper function to extract numerical parts for natural sorting."""
    return [int(part) if part.isdigit() else part for part in re.split(r'(\d+)', filename)]

def predict_and_save(model, test_data, pca, class_labels, output_file, square_side_length):
    """Predict test image classes and save results."""
    predictions, image_files = [], []  # Initialize lists for predictions and filenames
    for filename in sorted(os.listdir(test_data)):  # Iterate through test images
        image_path = os.path.join(test_data, filename)
        image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)  # Read image in grayscale
        if image is not None:  # Ensure valid image
            # Extract features and apply PCA transformation
            feature = pca.transform(extract_tiny_image(image, square_side_length).reshape(1, -1))
            predictions.append(model.predict(feature)[0])  # Predict class
            image_files.append(filename)  # Record filename
    results = sorted(zip(image_files, predictions), key=lambda x: natural_key(x[0]))

    # Save predictions to the output file
    with open(output_file, "w") as f:
        for img_file, pred in results:
            f.write(f"{img_file} {class_labels[pred]}\n")
    print(f"Predictions successfully saved to {output_file}")



if __name__ == "__main__":
    # Define paths and parameters
    train_data, test_data, prediction = "training", "testing", "run1.txt"  # File paths
    square_side_length, k_values = 450, [1, 3, 5, 7, 9, 11, 13, 15, 17]  # Parameters

    # Load training data and extract features
    X, y, class_labels = load_data_and_features(train_data, square_side_length)  # Load data

    # Apply PCA for dimensionality reduction
    pca = PCA(n_components=35, random_state=42)  # Initialize PCA
    X_pca = pca.fit_transform(X)  # Fit and transform data

    # Perform cross-validation to select the best k
    best_k, _ = perform_cross_validation(X_pca, y, k_values)  # Cross-validate

    # Train the final KNN model with the best k
    knn = KNeighborsClassifier(n_neighbors=best_k, weights='distance', p=2).fit(X_pca, y)  # Train KNN
    joblib.dump(knn, "best_knn_model.pkl")  # Save model to file

    # Predict test data and save the results
    predict_and_save(knn, test_data, pca, class_labels, prediction, square_side_length)  # Predict and save
    print(f"Predictions saved to {prediction}")  # Notify completion
