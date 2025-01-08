import numpy as np

from MyConvolution import convolve


def myHybridImages(lowImage: np.ndarray, lowSigma: float, highImage: np.ndarray, highSigma: float) -> np.ndarray:

    lowKernel = makeGaussianKernel(lowSigma)
    highKernel = makeGaussianKernel(highSigma)

    lowImage = convolve(lowImage, lowKernel)

    highImage = highImage - convolve(highImage, highKernel)

    hybridImage = lowImage + highImage

    # clip the image
    hybridImage[hybridImage < 0] = 0
    hybridImage[hybridImage > 255] = 255

    return hybridImage



def makeGaussianKernel(sigma: float) -> np.ndarray:

    # Determine the size of the kernel
    size = int(8.0 * sigma + 1.0)
    if size % 2 == 0:
        size += 1  # Ensure size is odd

    # Create a grid of (x, y) coordinates using basic operations
    center = size // 2
    x = np.zeros((size, size))
    y = np.zeros((size, size))

    for i in range(size):
        for j in range(size):
            x[i, j] = j - center
            y[i, j] = i - center

    # Calculate the Gaussian function
    gaussianKernel = np.exp(-(x ** 2 + y ** 2) / (2 * sigma ** 2)) / (2 * np.pi * sigma ** 2)
    gaussianKernel /= np.sum(gaussianKernel)

    return gaussianKernel

print(makeGaussianKernel(1))