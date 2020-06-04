from PIL import Image
import requests

im = Image.open(requests.get("https://www95.hattrick.org/Img/Flags/flags.gif", stream=True).raw)
width, height = im.size
nb_flags = width // 20

for i in range(nb_flags):
    icon = im.crop((i*20, 0, i*20+20, 12))
    if i != 0:
        icon.save(f"D:/Perso/Code/HO/src/main/resources/flags/{i}flag.png")
    else:
        icon.save(f"D:/Perso/Code/HO/src/main/resources/flags/{1_000}flag.png")

print(im.size)
#
#
#
# from PIL import Image
# from os import mkdir
#
# mkdir("assets/icons")
# sheet = Image.open("assets/icons.png")
# count = 0
#
# for x in range(12):
#     for y in range(97):
#         a = (x + 1) * 40
#         b = (y + 1) * 30
#         icon = sheet.crop((a - 40, b - 30, a, b))  # Problem here
#         icon.save("assets/icons/{}.png".format(count))
#         count += 1