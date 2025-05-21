from PIL import Image


def similar(c1, c2, tolerance=10):
    return all(abs(a - b) <= tolerance for a, b in zip(c1, c2))


def make_background_transparent(
    image_path, output_path, bg_color=(240, 240, 240), tolerance=10
):
    img = Image.open(image_path).convert("RGBA")
    datas = img.getdata()

    new_data = []
    for item in datas:
        if similar(item[:3], bg_color, tolerance):
            new_data.append((255, 255, 255, 0))  # 透明
        else:
            new_data.append(item)

    img.putdata(new_data)
    img.save(output_path, "PNG")


# 使用方法
make_background_transparent("cropped.png", "transparent.png")
