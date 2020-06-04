from PIL import Image
from os import path

path_to_folder = "D:\\Perso\\Code\\HO\\src\\main\\resources\\gui\\bilder\\match_events\\"

combinations = {"missed_penalty.png": (("miss.png", "penalty.png"), "v"),
                "converted_penalty.png": (("goal.png", "penalty.png"), "v"),
                "me_goal_and_technical.png": (("goal.png", "speciality-1.png"), "v"),    #55
                "me_no_goal_and_technical.png": (("miss.png", "speciality-1.png"), "v"),
                "me_goal_and_unpredictable.png": (("goal.png", "speciality-4.png"), "v"),    #106, 105, 108
                "me_goal_longshot.png": (("goal.png", "longshot.png"), "v"),   #107, 187
                "me_goal_and_quick.png": (("goal.png", "speciality-2.png"), "v"),    #115, 116
                "me_109.png": (("goal.png", "speciality-4-negative.png"), "h"),
                "me_117.png": (("tired.png", "goal.png"), "h"),
                "me_118.png": (("goal.png", "corner.png"), "v"),
                "me_goal_and_head_spec.png": (("goal.png", "speciality-5.png"), "v"),   #119
                "me_135.png": (("goal.png", "experience.png"), "v"),
                "me_136.png": (("experience.png", "goal.png"), "h"),
                "me_137.png": (("goal.png", "winger.png", "speciality-5.png"), "v3"),
                "me_138.png": (("goal.png", "winger.png"), "v"),
                "me_139.png": (("me_goal_and_technical.png", "speciality-5-negative-high.png"), "h"),
                "me_140.png": (("goal.png", "counter_attack.png", "whistle.png"), "v3"),
                "me_141.png": (("goal_C.png", "counter_attack.png"), "v"),
                "me_142.png": (("goal_L.png", "counter_attack.png"), "v"),
                "me_143.png": (("goal_R.png", "counter_attack.png"), "v"),
                "me_goal_and_whistle.png": (("goal.png", "whistle.png"), "v"),   #150, 170
                "me_184.png": (("goal.png", "penalty.png"), "v"),
                "me_185.png": (("goal.png", "indirect.png"), "v"),
                "me_186.png": (("goal.png", "counter_attack.png", "indirect.png"), "v3"),
                "me_190.png": (("goal.png", "speciality-3.png"), "v"),
                "me_no_goal_unpredictable.png": (("miss.png", "speciality-4.png"), "v"),   #205, 206, 208
                "me_miss_longshot.png": (("miss.png", "longshot.png"), "v"),   #207, 287
                "me_209.png": (("miss.png", "speciality-4-negative.png"), "h"),
                "me_miss_and_quick.png": (("miss.png", "speciality-2.png"), "v"),   #215, 216
                "me_217.png": (("miss.png", "tired.png"), "h"),
                "me_no_goal_and_head_spec.png": (("miss.png", "speciality-5.png"), "v"),   #219
                "missed_corner.png": (("miss.png", "corner.png"), "v"),
                "me_miss_and_whistle.png": (("miss.png", "whistle.png"), "v"),    #260, 230, 280
                "me_235.png": (("miss.png", "experience.png"), "v"),
                "me_236.png": (("miss.png", "experience.png"), "h"),
                "me_237.png": (("miss.png", "winger.png"), "v"),
                "me_239.png": (("speciality-5-negative-high.png", "me_no_goal_and_technical.png"), "h"),
                "me_240.png": (("miss.png", "counter_attack.png", "whistle.png"), "v3"),
                "me_241.png": (("miss_C.png", "counter_attack.png"), "v"),
                "me_242.png": (("miss_L.png", "counter_attack.png"), "v"),
                "me_243.png": (("miss_R.png", "counter_attack.png"), "v"),
                "me_285.png": (("miss.png", "indirect.png"), "v"),
                "me_286.png": (("miss.png", "counter_attack.png", "indirect.png"), "v3"),
                "me_289.png": (("speciality-2-negative.png", "speciality-2.png"), "h"),
                "me_290.png": (("miss.png", "speciality-3.png"), "v"),
                "me_YellowThenRed.png": (("yellow-card(yellow).png", "red-card(red).png"), "v")   #512, 513
                }


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
