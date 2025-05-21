import cv2

img = cv2.imread("cropped.png")
icon_size = 800

top = 60
left = 250
cropped = img[top : top + icon_size, left : left + icon_size]


cv2.imwrite("icon.png", cropped)

from transparent import make_background_transparent

make_background_transparent("icon.png", "icon_transparent.png")
