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

