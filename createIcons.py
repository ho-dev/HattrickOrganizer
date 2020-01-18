from PIL import Image
from os import path

path_to_folder = "D:\\Perso\\Code\\HO\\src\\main\\resources\\gui\\bilder\\match_events\\"

combinations = {"missed_penalty.png": (("miss.png", "penalty.png"), "v"),
                "converted_penalty.png": (("goal.png", "penalty.png"), "v"),
                "me_116.png": (("goal.png", "speciality-2.png"), "v"),
                "me_118.png": (("goal.png", "corner.png"), "v"),
                "me_135.png": (("goal.png", "experience.png"), "v"),
                "me_136.png": (("experience.png", "goal.png"), "h"),
                "me_141.png": (("goal_C.png", "counter_attack.png"), "v"),
                "me_142.png": (("goal_L.png", "counter_attack.png"), "v"),
                "me_170.png": (("goal.png", "whistle.png"), "v"),
                "me_184.png": (("goal.png", "penalty.png"), "v"),
                "me_185.png": (("goal.png", "indirect.png"), "v"),
                "me_no_goal_unpredictable.png": (("miss.png", "speciality-4.png"), "v"),   #206, 208
                "me_207.png": (("miss.png", "longshot.png"), "v"),
                "me_237.png": (("miss.png", "winger.png"), "v"),
                "me_280.png": (("miss.png", "whistle.png"), "v"),
                "me_285.png": (("miss.png", "indirect.png"), "v"),
                "me_286.png": (("miss.png", "counter_attack.png", "indirect.png"), "v3")}

def get_concat_h(im1, im2, out):
    dst = Image.new("RGBA", (im1.width + im2.width, im1.height), (255, 0, 0, 0))
    dst.paste(im1, (0, 0))
    dst.paste(im2, (im1.width, 0))
    dst.save(path_to_folder+out, "PNG")

def get_concat_v(im1, im2, out):
    dst = Image.new("RGBA", (im1.width, im1.height + im2.height), (255, 0, 0, 0))
    dst.paste(im1, (0, 0))
    dst.paste(im2, (0, im1.height))
    dst.save(path_to_folder+out, "PNG")

def get_concat_v3(im1, im2, im3, out):
    dst = Image.new("RGBA", (im1.width, im1.height + im2.height + im3.height), (255, 0, 0, 0))
    dst.paste(im1, (0, 0))
    dst.paste(im2, (0, im1.height))
    dst.paste(im3, (0, im1.height + im2.height))
    dst.save(path_to_folder+out, "PNG")



for k, v in combinations.items():
    images, operation = v

    if not path.exists(path_to_folder+k):
        try:
            im1 = Image.open(path_to_folder + images[0])
            im2 = Image.open(path_to_folder + images[1])
            if operation == "v":
                get_concat_v(im1, im2, k)
            elif operation == "v3":
                im3 = Image.open(path_to_folder + images[2])
                get_concat_v3(im1, im2, im3, k)
            else:
                get_concat_h(im1, im2, k)
            print(f"{k} has been generated")
        except Exception as e:
            print("operation failed")
            print(k)
            print(v)
            print(e)
