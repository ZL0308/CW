
import numpy as np
from MyConvolution import convolve
from MyHybridImages import myHybridImages
from MyHybridImages import makeGaussianKernel

data = np.load('kernels.npz')
def show_difference(expected, actual, label):
    diff = expected - actual
    non_zero_diff = np.where(diff != 0)

    output = f"Differences for {label}:\n"
    output += "different pixel(x,y) | expected | actual | difference\n"
    output += "-" * 80 + "\n"
    for i in range(len(non_zero_diff[0])):
        x, y = non_zero_diff[0][i], non_zero_diff[1][i]
        value_diff = diff[x, y]
        if abs(value_diff) < 1e-8:
            value_diff = 0
        output += f"({x},{y}) | {expected[x, y]} | {actual[x, y]} | {value_diff}\n"

    return output


kernel1 = data["kernel1"]
kernel2 = data["kernel2"]
kernel3 = data["kernel3"]

testKernel1 = makeGaussianKernel(1.0)
testKernel2 = makeGaussianKernel(2.0)
testKernel3 = makeGaussianKernel(5.5)

image = data["image"]
kernel = data["kernel"]
ans = data["ans"]
testans = convolve(image, kernel)

imageA = data["imageA"]
kernelA = data["kernelA"]
ansA = data["ansA"]
testansA = convolve(imageA, kernelA)

if(np.array_equal(testKernel1, kernel1)):
    print("kernel1 accept")
else:
    print(show_difference(kernel1, testKernel1, "kernel1"))

if(np.array_equal(testKernel2, kernel2)):
    print("kernel2 accept")
else:
    print(show_difference(kernel2, testKernel2, "kernel2"))

if(np.array_equal(testKernel3, kernel3)):
    print("kernel3 accept")
else:
    print(show_difference(kernel3, testKernel3, "kernel3"))

print("\n"
      "-------------------------------------"
      "\n")
if(np.array_equal(ans, testans)):
    print("convolve accept")
else:
    print("expected:\n", ans, "\nactual:\n", testans, "\nconvolve differ")

if(np.array_equal(ansA, testansA)):
    print("convolveA accept")
else:
    print("expected:\n", ansA, "\nactual:\n", testansA, "\nconvolveA differ")
