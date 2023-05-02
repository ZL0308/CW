#include <stdio.h>
#include <stdlib.h>

struct item {
    int code;
    int quantity;
};

int size = 0;

struct item *space(int n) {
    if (n < 0) {
        return NULL;
    }
    struct item *p = malloc(n * sizeof(struct item));
    size = n;
    return p;
}

int store(int pos, struct item *arr, struct item str) {
    if (arr == NULL || pos < 0 || pos >= size) {
        return -1;
    }
    arr[pos] = str;
    return 0;
}

double average(struct item *arr, int m) {
    if (arr == NULL || m > size || m <=0 ) {
        return -1;
    }
    double sum = 0;
    for (int i = 0; i < m; ++i) {
        sum += arr[i].quantity;
    }
    return sum / m;
}

