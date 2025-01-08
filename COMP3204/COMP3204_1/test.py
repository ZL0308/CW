import shutil
import numpy as np

import cv2
import os

from MyHybridImages import myHybridImages

image2_path = "file/cat.bmp"
image1_path = "file/dog.bmp"

image1 = cv2.imread(image1_path)
image2 = cv2.imread(image2_path)

#image1 = cv2.cvtColor(image1, cv2.COLOR_BGR2RGB)
#image2 = cv2.cvtColor(image2, cv2.COLOR_BGR2RGB)

# Create the hybrid directory if it doesn't exist
output_dir = "data"
if os.path.exists(output_dir):
    shutil.rmtree(output_dir)

if not os.path.exists(output_dir):
    os.makedirs(output_dir)

for lowSigma in range(1, 10):  # Start from 1 to avoid division by zero
    for highSigma in range(1, 10):
        hybrid_image_np = myHybridImages(image1, lowSigma, image2, highSigma)

        # Create scaled versions of the hybrid image
        sizes = [1,0.3]
        images = [cv2.resize(hybrid_image_np, (0, 0), fx=size, fy=size) for size in sizes]

        # Compute the total width and the maximum height
        total_width = sum(image.shape[1] for image in images)
        max_height = max(image.shape[0] for image in images)

        # Create a new blank image with the total width and maximum height
        new_image = np.zeros((max_height, total_width, 3), dtype=np.uint8)

        # Copy images next to each other
        x_offset = 0
        for image in images:
            new_image[:image.shape[0], x_offset:x_offset + image.shape[1]] = image
            x_offset += image.shape[1]

        # Construct filename
        filename = f"low{lowSigma}high{highSigma}_composite.jpg"
        filepath = os.path.join(output_dir, filename)

        # Save the image
        cv2.imwrite(filepath, new_image)
