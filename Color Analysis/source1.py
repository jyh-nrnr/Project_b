import cv2
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import time
start = time.time()

src = cv2.imread("img04.png")
r = 10
src = cv2.resize(src, dsize=(int(src.shape[0]/r), int(src.shape[1]/r)), interpolation=cv2.INTER_AREA)

hsv = cv2.cvtColor(src, cv2.COLOR_BGR2HSV)

height, width, channel = src.shape
h_histogram = np.zeros((181, 1))

for i in range(height):
    for j in range(width):
        val = hsv[i, j, 0]
        h_histogram[val, 0] += 1

h_histogram = pd.DataFrame(h_histogram, columns=["histogram"])
h_histogram = h_histogram.sort_values(["histogram"], ascending=[False])

dist = 10
H = []
H.append(h_histogram.head(1).index.values.astype(int)[-1])
max_idx = 1
for i in range(1, 181):
    if abs(
        h_histogram.head(i).index.values.astype(int)[-1] - h_histogram.head(max_idx).index.values.astype(int)[-1]) > dist:
        H.append(h_histogram.head(i).index.values.astype(int)[-1])
        max_idx = i

    if len(H) >= 5:
        break

S, V = [], []
for k in range(5):
    s_histogram = np.zeros((256, 1))
    v_histogram = np.zeros((256, 1))
    for i in range(height):
        for j in range(width):
            if hsv[i,j,0] == H[k]:
                s, v = hsv[i, j, 1], hsv[i, j, 2]
                s_histogram[s, 0] += 1
                v_histogram[v, 0] += 1
    S.append(s_histogram.argmax())
    V.append(v_histogram.argmax())

mat = hsv.copy()
cv2.circle(mat, (20, int(height/2)), 20, (int(H[0]), int(S[0]), int(V[0])), -1)
cv2.circle(mat, (80, int(height/2)), 20, (int(H[1]), int(S[1]), int(V[1])), -1)
cv2.circle(mat, (140, int(height/2)), 20, (int(H[2]), int(S[2]), int(V[2])), -1)
cv2.circle(mat, (200, int(height/2)), 20, (int(H[3]), int(S[3]), int(V[3])), -1)
cv2.circle(mat, (260, int(height/2)), 20, (int(H[4]), int(S[4]), int(V[4])), -1)


dst = cv2.cvtColor(mat, cv2.COLOR_HSV2BGR)
print(time.time() - start)
# cv2.imshow("src", dst)
cv2.imwrite("beta test.png", dst)
cv2.waitKey(0)
