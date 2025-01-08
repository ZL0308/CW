import os
from PIL import Image
from sklearn.metrics import accuracy_score
import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader
from torchvision import transforms, models
from sklearn.model_selection import train_test_split
from torch.optim import lr_scheduler
from torch.cuda.amp import GradScaler, autocast
import time
import copy
import warnings
import re

# Suppress UserWarnings and FutureWarnings to reduce console clutter, especially those related to fonts or deprecated features
warnings.filterwarnings("ignore", category=UserWarning)
warnings.filterwarnings("ignore", category=FutureWarning)

# Define the target image size as a tuple (height, width)
IMG_SIZE = (224, 224)

# Data transformations for the training set, including data augmentation techniques to improve model generalization
train_transforms = transforms.Compose([
    transforms.Grayscale(num_output_channels=3),
    # Convert grayscale images to 3-channel images by duplicating the single channel
    transforms.RandomResizedCrop(224),  # Randomly crop the image to 224x224 pixels with resizing
    transforms.RandomHorizontalFlip(),  # Randomly flip the image horizontally
    transforms.RandomRotation(20),  # Randomly rotate the image by up to 20 degrees
    transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2, hue=0.1),
    # Randomly change brightness, contrast, saturation, and hue
    transforms.ToTensor(),  # Convert the PIL Image to a PyTorch tensor
    transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])
    # Normalize the tensor with mean and standard deviation of 0.5 for each channel
])

# Data transformations for validation and test sets, without augmentation to evaluate performance on original data distribution
val_transforms = transforms.Compose([
    transforms.Grayscale(num_output_channels=3),  # Convert grayscale images to 3-channel images
    transforms.Resize(256),  # Resize the shorter side of the image to 256 pixels while maintaining aspect ratio
    transforms.CenterCrop(224),  # Crop the center 224x224 pixels from the image
    transforms.ToTensor(),  # Convert the PIL Image to a PyTorch tensor
    transforms.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])  # Normalize the tensor
])

# Assign the same transformations for the test set as the validation set
test_transforms = val_transforms


# Custom Dataset class to handle image data
class ImageDataset(Dataset):
    def __init__(self, file_paths, labels=None, transform=None):
        """
        Initializes the dataset with file paths, optional labels, and transformations.
        """
        self.file_paths = file_paths  # List of image file paths
        self.labels = labels  # List of labels (if available)
        self.transform = transform  # Transformations to apply to images

    def __len__(self):
        """
        Returns the total number of samples in the dataset.
        """
        return len(self.file_paths)

    def __getitem__(self, idx):
        """
        Retrieves the image and label (if available) at the specified index.
        """
        img_path = self.file_paths[idx]  # Get the image path at the specified index
        try:
            image = Image.open(img_path).convert('L')  # Open the image and convert it to grayscale ('L' mode)
        except Exception as e:
            print(f"Cannot open image: {img_path}. Error: {e}")  # Print error message if image cannot be opened
            image = Image.new('L', IMG_SIZE)  # Create a blank grayscale image if there's an error

        if self.transform:
            image = self.transform(image)  # Apply transformations if provided

        if self.labels is not None:
            label = torch.tensor(self.labels[idx], dtype=torch.long)  # Convert label to a tensor
            return image, label  # Return image and its label
        else:
            return image  # Return only the image for test datasets


# Define the training function with support for mixed precision and cosine annealing learning rate scheduling
def train_model(model, criterion, optimizer, scheduler, dataloaders, dataset_sizes, device, patience, num_epochs):
    """
    Trains the model and returns the best model based on validation accuracy.
    """
    since = time.time()  # Record the start time
    best_model_wts = copy.deepcopy(model.state_dict())  # Deep copy of the model's initial weights
    best_acc = 0.0  # Initialize the best accuracy
    scaler = GradScaler()  # Initialize GradScaler for mixed precision training
    epochs_no_improve = 0  # Counter for early stopping

    for epoch in range(num_epochs):
        print(f"\nEpoch {epoch + 1}/{num_epochs}")  # Print current epoch
        print("-" * 10)

        # Iterate over both training and validation phases
        for phase in ['train', 'val']:
            if phase == 'train':
                model.train()  # Set model to training mode
            else:
                model.eval()  # Set model to evaluation mode

            running_loss = 0.0  # Accumulate loss
            running_corrects = 0  # Accumulate correct predictions

            # Iterate over data in the current phase
            for inputs, labels in dataloaders[phase]:
                inputs, labels = inputs.to(device), labels.to(device)  # Move data to the specified device

                optimizer.zero_grad()  # Zero the parameter gradients

                # Forward pass with autocast for mixed precision
                with autocast():
                    outputs = model(inputs)  # Get model outputs
                    _, preds = torch.max(outputs, 1)  # Get predicted class indices
                    loss = criterion(outputs, labels)  # Compute loss

                # Backward pass and optimization only in training phase
                if phase == 'train':
                    scaler.scale(loss).backward()  # Scale loss and perform backpropagation
                    scaler.step(optimizer)  # Update model parameters
                    scaler.update()  # Update the scaler for next iteration

                running_loss += loss.item() * inputs.size(0)  # Accumulate loss
                running_corrects += torch.sum(preds == labels)  # Accumulate correct predictions

            if phase == 'train':
                scheduler.step()  # Update the learning rate

            epoch_loss = running_loss / dataset_sizes[phase]  # Calculate average loss
            epoch_acc = running_corrects.double() / dataset_sizes[phase]  # Calculate accuracy

            print(f"{phase.capitalize()} Loss: {epoch_loss:.4f} Acc: {epoch_acc:.4f}")  # Print loss and accuracy

            # Deep copy the model if it has the best accuracy so far
            if phase == 'val' and epoch_acc > best_acc:
                best_acc = epoch_acc  # Update the best accuracy
                best_model_wts = copy.deepcopy(model.state_dict())  # Update the best model weights
                epochs_no_improve = 0  # Reset the counter
            elif phase == 'val':
                epochs_no_improve += 1  # Increment the counter if no improvement

        # Check for early stopping
        if epochs_no_improve >= patience:
            print("Early stopping triggered!")  # Notify early stopping
            break  # Exit the training loop

    time_elapsed = time.time() - since  # Calculate total training time
    print(f"Training complete in {int(time_elapsed // 60)}m {int(time_elapsed % 60)}s")  # Print training duration
    print(f"Best Validation Accuracy: {best_acc:.4f}")  # Print the best validation accuracy

    model.load_state_dict(best_model_wts)  # Load the best model weights
    return model  # Return the trained model


def run_classifier(train_dir, test_dir, device, num_epochs=25, batch_size=32, learning_rate=1e-4):
    """
    Orchestrates the entire classification process: data loading, model training, evaluation, and prediction.
    """
    # Load data from the specified directories
    X, y, X_test, test_file_names, categories = load_data(train_dir, test_dir)
    num_classes = len(categories)  # Determine the number of classes

    # Split the training data into training and validation sets with stratification to maintain class distribution
    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y)

    # Create Dataset objects for training, validation, and testing
    train_dataset = ImageDataset(X_train, y_train, transform=train_transforms)
    val_dataset = ImageDataset(X_val, y_val, transform=val_transforms)
    test_dataset = ImageDataset(X_test, transform=test_transforms)  # No labels for test dataset

    # Create DataLoaders for training and validation datasets
    dataloaders = {
        'train': DataLoader(train_dataset, batch_size=batch_size, shuffle=True, num_workers=8, pin_memory=True),
        'val': DataLoader(val_dataset, batch_size=batch_size, shuffle=False, num_workers=8, pin_memory=True)
    }
    # Create DataLoader for the test dataset
    test_loader = DataLoader(test_dataset, batch_size=batch_size, shuffle=False, num_workers=8, pin_memory=True)

    # Store the sizes of training and validation datasets
    dataset_sizes = {
        'train': len(train_dataset),
        'val': len(val_dataset)
    }

    model = models.resnet101(pretrained=True)  # Load a pre-trained ResNet-101 model
    num_ftrs = model.fc.in_features  # Get the number of input features to the fully connected layer
    model.fc = nn.Linear(num_ftrs, num_classes)  # Replace the fully connected layer for the number of classes
    model = model.to(device)  # Move the model to the specified device (CPU or GPU)

    # Define the loss function (Cross-Entropy Loss for multi-class classification)
    criterion = nn.CrossEntropyLoss()
    # Define the optimizer (Adam optimizer with specified learning rate and weight decay)
    optimizer = torch.optim.Adam(model.parameters(), lr=learning_rate, weight_decay=1e-4)
    # Define the learning rate scheduler (Cosine Annealing scheduler)
    scheduler = lr_scheduler.CosineAnnealingLR(optimizer, T_max=num_epochs)

    # Train the model using the defined training function
    model = train_model(model, criterion, optimizer, scheduler, dataloaders, dataset_sizes, device=device,
                        patience=7, num_epochs=num_epochs)

    # Evaluate the trained model on the validation set
    evaluate_model(model, dataloaders['val'], device=device)

    # Predict classes for the test dataset
    predictions = predict_test_data(model, test_loader, categories, device=device)
    # Sort the results based on natural sorting of filenames
    results = sorted(zip(test_file_names, predictions), key=lambda x: natural_key(x[0]))

    # Save the predictions to a text file
    with open("run3.txt", "w", encoding='utf-8') as f:
        for file_name, pred in results:
            f.write(f"{file_name} {pred}\n")
    print("Predictions saved")  # Notify that predictions have been saved


def load_data(train_dir, test_dir):
    """
    Loads training and testing data from the specified directories.
    """
    X, y = [], []  # Initialize lists to store training image paths and labels

    # Get all valid categories by listing non-hidden subdirectories in the training directory
    categories = sorted([d for d in os.listdir(train_dir)
                         if os.path.isdir(os.path.join(train_dir, d)) and not d.startswith('.')])
    # Create a mapping from class name to numerical index
    class_to_idx = {cls_name: idx for idx, cls_name in enumerate(categories)}

    print("Scanning training data categories...")
    valid_categories = categories  # All non-hidden subdirectories are considered valid categories
    num_classes = len(valid_categories)  # Number of classes
    print(f"Number of valid categories: {num_classes}")
    print("Categories:", valid_categories)

    # Iterate over each category to collect image paths and their corresponding labels
    for cls_name in valid_categories:
        cls_path = os.path.join(train_dir, cls_name)  # Path to the current class directory
        for img_name in os.listdir(cls_path):
            # Only process files with .jpg or .jpeg extensions (case-insensitive)
            if img_name.lower().endswith(('.jpg', '.jpeg')):
                # Append the full path to the image
                X.append(os.path.join(cls_path, img_name))
                # Append the numerical label corresponding to the class
                y.append(class_to_idx[cls_name])

    # Collect testing image paths and their file names
    X_test, test_file_names = [], []
    for img_name in sorted(os.listdir(test_dir)):
        if img_name.lower().endswith(('.jpg', '.jpeg')):
            X_test.append(os.path.join(test_dir, img_name))  # Append full path
            test_file_names.append(img_name)  # Append file name

    return X, y, X_test, test_file_names, valid_categories  # Return all collected data


def evaluate_model(model, val_loader, device='cpu'):
    """
    Evaluates the model on the validation set and displays accuracy.
    """
    model.eval()  # Set model to evaluation mode
    y_true, y_pred = [], []  # Initialize lists to store true and predicted labels

    with torch.no_grad():  # Disable gradient computation for evaluation
        for images, labels in val_loader:
            images, labels = images.to(device), labels.to(device)  # Move data to the specified device
            outputs = model(images)  # Get model outputs
            _, preds = torch.max(outputs, 1)  # Get predicted class indices
            y_true.extend(labels.cpu().numpy())  # Collect true labels
            y_pred.extend(preds.cpu().numpy())  # Collect predicted labels

    # Calculate accuracy using sklearn's accuracy_score
    accuracy = accuracy_score(y_true, y_pred)
    print(f"\nValidation Accuracy: {accuracy:.4f}")  # Print validation accuracy


def predict_test_data(model, test_loader, class_labels, device):
    """
    Predicts classes for the test dataset.
    """
    model.eval()  # Set model to evaluation mode
    predictions = []  # Initialize list to store predictions

    with torch.no_grad():  # Disable gradient computation for prediction
        for images in test_loader:
            images = images.to(device)  # Move images to the specified device
            outputs = model(images)  # Get model outputs
            _, preds = torch.max(outputs, 1)  # Get predicted class indices
            predictions.extend(preds.cpu().numpy())  # Collect predictions

    # Map predicted indices to class names
    return [class_labels[pred] for pred in predictions]


def natural_key(filename):
    """
    Helper function to extract numerical parts from filenames for natural sorting.
    """
    # Split the filename into numeric and non-numeric parts using regular expressions
    return [int(part) if part.isdigit() else part for part in re.split(r'(\d+)', filename)]


# Main execution block
if __name__ == "__main__":
    # Define the paths to the training and testing directories
    train_dir = "training"  # Update this path to your actual training directory
    test_dir = "testing"  # Update this path to your actual testing directory

    # Set device to GPU if available, else default to CPU
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    print(f"Using device: {device}")  # Print the device being used

    # Run the high-performance PyTorch classifier with specified hyperparameters
    run_classifier(train_dir, test_dir, device, num_epochs=25, batch_size=32, learning_rate=1e-4)
