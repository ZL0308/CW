import numpy as np
from MyConvolution import convolve
from MyHybridImages import myHybridImages
from MyHybridImages import makeGaussianKernel
from PIL import Image


def check_difference(expected, actual):
    differences = []
    for x in range(expected.shape[0]):
        for y in range(expected.shape[1]):
            if not np.allclose(expected[x, y], actual[x, y], atol=1e-10):
                diff = np.abs(expected[x, y] - actual[x, y])
                differences.append((x, y, expected[x, y], actual[x, y], diff))
    return differences


def display_chart(differences):
    print("pixel position(x,y) | expected (R,G,B) | actual (R,G,B) | difference R | difference G | difference B")
    for x, y, exp, act, diff in differences:
        print(f"({x}, {y}) | {tuple(exp)} | {tuple(act)} | {diff[0]} | {diff[1]} | {diff[2]}")



def check(low_image_path, high_image_path):
    low_image = Image.open(low_image_path).convert("RGB")
    high_image = Image.open(high_image_path).convert("RGB")

    low_image_array = np.array(low_image)
    high_image_array = np.array(high_image)

    high_sigma = 5.0
    low_sigma = 10.0
    hybrid_image_array = np.zeros_like(low_image_array)
    for i in range(3):  # i=0 对应 R，i=1 对应 G，i=2 对应 B
        hybrid_image_array[:, :, i] = myHybridImages(
            low_image_array[:, :, i], low_sigma,
            high_image_array[:, :, i], high_sigma
        )
    hybrid_image = Image.fromarray(np.uint8(hybrid_image_array))
    output_filename = f'hybrid_image_highSigma_{high_sigma}_lowSigma_{low_sigma}.jpg'
    hybrid_image.save(output_filename)
    print(f"Generated {output_filename}")
    return hybrid_image_array


if __name__ == "__main__":
    low_image_path = 'file/dog.bmp'
    high_image_path = 'file/cat.bmp'
    testAns1 = check(low_image_path, high_image_path)

    #low_image_path = './data/einstein.bmp'
    #high_image_path = './data/marilyn.bmp'
    #testAns2 = check(low_image_path, high_image_path)

    data = np.load('ans.npz')
    ans1 = data["ans1"]
    #ans2 = data["ans2"]

    differences1 = check_difference(ans1, testAns1)
    #differences2 = check_difference(ans2, testAns2)

    if differences1:
        print("Differences for first image:")
        display_chart(differences1)

    #if differences2:
        #print("Differences for second image:")
        #display_chart(differences2)
