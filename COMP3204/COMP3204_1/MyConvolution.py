import numpy as np


def convolve(image: np.ndarray, kernel: np.ndarray) -> np.ndarray:
    if image.ndim == 2:
        image = image[:, :, None]

    kernel = kernel[::-1, ::-1]

    iheight, iwidth, ichannels = image.shape
    kheight, kwidth = kernel.shape

    pheight = kheight // 2
    pwidth = kwidth // 2

    padded_image = np.zeros((iheight + 2 * pheight, iwidth + 2 * pwidth, ichannels))
    padded_image[pheight:pheight + iheight, pwidth:pwidth + iwidth, :] = image

    output = np.zeros(image.shape)

    # perform convolve
    for c in range(ichannels):
        for i in range(iheight):
            for j in range(iwidth):
                region = padded_image[i:i + kheight, j:j + kwidth, c]
                output[i, j, c] = np.sum(region * kernel)

    # check dimension
    if output.shape[2] == 1:
        return output[:, :, 0]

    return output
