from PIL import Image
from os import path

imagesource = "D:\\Perso\\Code\\HO\\src\\main\\resources\\img\\icons\\official\\HTwebsite\\png\\master_icons.png"
path_to_folder = "D:\\Perso\\Code\\HO\\src\\main\\resources\\gui\\bilder\\match_types\\"

combinations = {"matchtype-Friendly.png": ((0, 22), (18, 18)),
                "matchtype-League.png": ((36, 22), (18, 18)),
                "matchtype-matchMasters.png": ((54, 22), (18, 18)),
                "matchtype-qualification.png": ((72, 22), (18, 18)),
                "matchtype-tournament.png":  ((308, 22), (18, 18)),
                "matchtype-single-match.png":  ((326, 22), (18, 18)),
                "matchtype-tournament-ladder.png":  ((344, 22), (18, 18)),
                "matchtype-matchCupA.png": ((361, 22), (18, 18)), # regular cup
                "matchtype-matchCupB1.png": ((379, 22), (18, 18)), # emerald cup
                "matchtype-matchCupB2.png": ((397, 22), (18, 18)), # ruby cup
                "matchtype-matchCupB3.png": ((415, 22), (18, 18)), # saphir cup
                "matchtype-matchCupC.png": ((433, 22), (18, 18)), # consolante cup
                }



def extract(imgSource, coordinates, out):
    im1 = imgSource.crop(coordinates)
    im1.save(path_to_folder+out, "PNG")



im = Image.open(imagesource)

for k, v in combinations.items():
    topLeft, size = v

    if not path.exists(path_to_folder+k):
        try:
            top, left = topLeft
            bottom, right = top+size[0], left+size[1]
            extract(im, (top, left, bottom, right), k)
            print(f"{k} has been generated")
        except Exception as e:
            print("operation failed")
            print(k)
            print(v)
            print(e)
