import cv2

original_size = 1536
padding = (original_size - 1280) / 2
img = cv2.imread("original.png")

cropped = img[
    int(padding) : int(original_size - padding),
    int(padding) : int(original_size - padding),
]

cv2.imwrite("cropped.png", cropped)
