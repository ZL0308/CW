import shutil

import cv2
import numpy as np
import os

from MyHybridImages import myHybridImages

image1_path = "file/jet.bmp"
image2_path = "file/Bird.bmp"

image1 = cv2.imread(image1_path)
image2 = cv2.imread(image2_path)

#image1 = cv2.cvtColor(image1, cv2.COLOR_BGR2RGB)
#image2 = cv2.cvtColor(image2, cv2.COLOR_BGR2RGB)

# Create the hybrid directory if it doesn't exist
output_dir = "single"
if os.path.exists(output_dir):
    shutil.rmtree(output_dir)

if not os.path.exists(output_dir):
    os.makedirs(output_dir)

lowSigma = 8
highSigma = 1

hybrid_image_np = myHybridImages(image1, lowSigma, image2, highSigma)

sizes = [2,1.5,1,0.5]
images = [cv2.resize(hybrid_image_np, (0, 0), fx=size, fy=size) for size in sizes]


total_width = sum(image.shape[1] for image in images)
max_height = max(image.shape[0] for image in images)


new_image = np.zeros((max_height, total_width, 3), dtype=np.uint8)

x_offset = 0
for image in images:
    new_image[:image.shape[0], x_offset:x_offset + image.shape[1]] = image
    x_offset += image.shape[1]

# Construct filename
filename = f"low{lowSigma}high{highSigma}_composite.jpg"
filepath = os.path.join(output_dir, filename)

        # Save the image
cv2.imwrite(filepath, new_image)
