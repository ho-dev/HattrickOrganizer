#!/usr/bin/env python
"""Update League_Status_Details table on HO server for promotion/demotion module
"""

import json
import requests

__author__ = "Akasolace"
__license__ = "GPL"
__version__ = "1.0.0"


class Country:
    def __init__(self, _name, _id, _leagueStructure):
        self.name = _name
        self.ID = _id
        self.leagueStructure = _leagueStructure


# Yield successive n-sized chunks from l.
def divide_chunks(l, n):
    # looping till length l
    for i in range(0, len(l), n):
        yield l[i:i + n]


# nb leagues for each level
SIZE = [1, 4, 16, 64, 256, 1_024, 1_024]


# full list available here:  https://github.com/tychobrailleur/HO/commit/0ad373fb044a91bc1049f37daca85e51d3979ac5
COUNTRIES = []
COUNTRIES.append(Country("Albania", 98, [88340, 88346, 88350, 208326, 252057]))
COUNTRIES.append(Country("Algeria", 118, [123069, 123070, 123074, 225756, 252975]))
COUNTRIES.append(Country("Andorra", 105, [88385, 88391, 88415, 238619]))
COUNTRIES.append(Country("Angola", 130, [209686, 209688, 209692]))
COUNTRIES.append(Country("Argentina", 7, [342, 343, 347, 363, 13220, 26313]))
COUNTRIES.append(Country("Armenia", 122, [123133, 123138, 123158]))
COUNTRIES.append(Country("Austria", 39, [4205, 8755, 8759, 9639, 17220, 37013]))
COUNTRIES.append(Country("Azerbaijan", 129, [201137, 201138, 201142, 238555]))
COUNTRIES.append(Country("Bahrain", 123, [123188, 123189, 123193, 253231]))
COUNTRIES.append(Country("Bangladesh", 132, [209729, 209730, 209734]))
COUNTRIES.append(Country("Barbados", 124, [123209, 123212, 123224]))
COUNTRIES.append(Country("Belarus", 91, [60146, 60152, 60240, 115240, 245615]))
COUNTRIES.append(Country("Belgium", 44, [8714, 8715, 8719, 9703, 12708, 22985]))
COUNTRIES.append(Country("Benin", 139, [238747, 238749, 238757]))
COUNTRIES.append(Country("Bolivia", 74, [34840, 34866, 34901, 48704, 66096]))
COUNTRIES.append(Country("Bosnia and Herzegovina", 69, [29726, 29727, 29731, 48768, 66352]))
COUNTRIES.append(Country("Brazil", 16, [3229, 3230, 3234, 3250, 11047, 52757]))
COUNTRIES.append(Country("Brunei", 136, [229917, 229918, 229926]))
COUNTRIES.append(Country("Bulgaria", 62, [14234, 14235, 14239, 18953, 65840]))
COUNTRIES.append(Country("Cambodia", 138, [237126, 237127, 237131]))
COUNTRIES.append(Country("Cameroon", 146, [252313, 252317, 252321]))
COUNTRIES.append(Country("Canada", 17, [3314, 3315, 3319, 8630, 12964]))
COUNTRIES.append(Country("Cape Verde", 125, [123210, 123216, 123240]))
COUNTRIES.append(Country("Chile", 18, [3335, 3336, 3340, 28361, 33351, 34965]))
COUNTRIES.append(Country("Chinese Taipei", 60, [13531, 13532, 13536, 98452, 116520]))
COUNTRIES.append(Country("Colombia", 19, [3377, 3378, 3382, 33287, 34071]))
COUNTRIES.append(Country("Comoros", 151, [258136, 258137, 258141, 258157]))
COUNTRIES.append(Country("Costa Rica", 81, [56879, 56881, 56889, 76288]))
COUNTRIES.append(Country("Côte d’Ivoire", 126, [123211, 123220, 123256]))
COUNTRIES.append(Country("Croatia", 58, [11387, 11388, 11392, 14255, 19017]))
COUNTRIES.append(Country("Cuba", 147, [252358, 252359, 252383]))
COUNTRIES.append(Country("Curaçao", 153, [258115, 258116, 258120, 258221]))
COUNTRIES.append(Country("Cyprus", 89, [57560, 57561, 57565, 115368, 218006]))
COUNTRIES.append(Country("Czech Republic", 52, [11303, 11304, 11308, 14085, 59629, 111144]))
COUNTRIES.append(Country("Denmark", 11, [1769, 1770, 1774, 1790, 1854, 24009]))
COUNTRIES.append(Country("Dominican Republic", 88, [57539, 57540, 57544]))
COUNTRIES.append(Country("DR Congo", 155, [258477, 258478, 258482]))
COUNTRIES.append(Country("Ecuador", 73, [34841, 34862, 34885, 116840]))
COUNTRIES.append(Country("Egypt", 33, [3398, 3399, 9367, 57434, 238171]))
COUNTRIES.append(Country("El Salvador", 100, [88256, 88260, 88292, 238791]))
COUNTRIES.append(Country("England", 2, [512, 513, 517, 533, 6348, 6604]))
COUNTRIES.append(Country("Estonia", 56, [11366, 11367, 11371, 11492, 13829]))
COUNTRIES.append(Country("Ethiopia", 156, [258498, 258499, 258503]))
COUNTRIES.append(Country("Faroe Islands", 76, [34871, 34877, 34933, 48832, 208406]))
COUNTRIES.append(Country("Finland", 12, [2280, 2281, 2285, 2301, 8839, 31069]))
COUNTRIES.append(Country("France", 5, [703, 704, 708, 5450, 21577, 35989]))
COUNTRIES.append(Country("Georgia", 104, [88382, 88386, 88431, 238683, 249113]))
COUNTRIES.append(Country("Germany", 3, [427, 428, 432, 448, 6092, 15343, 41109]))
COUNTRIES.append(Country("Ghana", 137, [229916, 229922, 229942]))
COUNTRIES.append(Country("Greece", 50, [11345, 11346, 11350, 13765, 34327]))
COUNTRIES.append(Country("Guam", 154, [258073, 258074, 258078, 258413]))
COUNTRIES.append(Country("Guatemala", 107, [88447, 88448, 88452, 203462]))
COUNTRIES.append(Country("Hattrick International", 1000, [256687, 256688, 256692, 256708, 256772, 257028]))
COUNTRIES.append(Country("Honduras", 99, [88257, 88264, 88276, 256495]))
COUNTRIES.append(Country("Hong Kong", 59, [13508, 13509, 13513, 13616, 98516]))
COUNTRIES.append(Country("Hungary", 51, [11324, 11325, 11329, 11556, 19273, 71792]))
COUNTRIES.append(Country("Iceland", 38, [4200, 4201, 8038, 18500]))
COUNTRIES.append(Country("India", 20, [3488, 3489, 3493, 3509, 249689]))
COUNTRIES.append(Country("Indonesia", 54, [11408, 11409, 11413, 13701, 57177]))
COUNTRIES.append(Country("Iran", 85, [57518, 57519, 57523, 203526, 239151]))
COUNTRIES.append(Country("Iraq", 128, [200092, 200093, 200097, 252463]))
COUNTRIES.append(Country("Ireland", 21, [3573, 3574, 3578, 8775, 34583]))
COUNTRIES.append(Country("Israel", 63, [13680, 13681, 13685, 18569, 18633, 42155]))
COUNTRIES.append(Country("Italy", 4, [724, 725, 729, 5772, 5836, 28702]))
COUNTRIES.append(Country("Jamaica", 94, [60148, 60160, 60224, 249049]))
COUNTRIES.append(Country("Japan", 22, [3594, 3595, 3599, 65456, 117160]))
COUNTRIES.append(Country("Jordan", 106, [88390, 88395, 88399, 252847]))
COUNTRIES.append(Country("Kazakhstan", 112, [98814, 98815, 98819, 238427]))
COUNTRIES.append(Country("Kenya", 95, [60149, 60164, 60208, 249625]))
COUNTRIES.append(Country("Kuwait", 127, [200087, 200088, 208390, 252527]))
COUNTRIES.append(Country("Kyrgyzstan", 102, [88341, 88342, 88366]))
COUNTRIES.append(Country("Latvia", 53, [11450, 11451, 11455, 14149, 28446]))
COUNTRIES.append(Country("Lebanon", 120, [123111, 123112, 123116, 253359]))
COUNTRIES.append(Country("Liechtenstein", 117, [123048, 123049, 123053, 203590]))
COUNTRIES.append(Country("Lithuania", 66, [29747, 29748, 29752, 33687, 59885]))
COUNTRIES.append(Country("Luxembourg", 84, [57433, 57498, 57502, 115304]))
COUNTRIES.append(Country("Malaysia", 45, [4213, 8735, 8739, 16900, 49429]))
COUNTRIES.append(Country("Maldives", 144, [245935, 245937, 245945]))
COUNTRIES.append(Country("Malta", 101, [88258, 88268, 88324, 88788, 203654]))
COUNTRIES.append(Country("Mexico", 6, [682, 683, 687, 2944, 25033]))
COUNTRIES.append(Country("Moldova", 103, [88259, 88272, 88308, 116776, 238863]))
COUNTRIES.append(Country("Mongolia", 119, [123090, 123091, 123095]))
COUNTRIES.append(Country("Montenegro", 131, [209708, 209709, 209713, 216918]))
COUNTRIES.append(Country("Morocco", 77, [34870, 34881, 34949, 208262, 252591]))
COUNTRIES.append(Country("Mozambique", 135, [225734, 225736, 225740]))
COUNTRIES.append(Country("Netherlands", 14, [2195, 2196, 2200, 2216, 8118, 17476]))
COUNTRIES.append(Country("Nicaragua", 111, [98793, 98794, 98798]))
COUNTRIES.append(Country("Nigeria", 75, [34872, 34873, 34917, 251993]))
COUNTRIES.append(Country("North Macedonia", 97, [60147, 60156, 60256, 115432, 218262]))
COUNTRIES.append(Country("Northern Ireland", 93, [60150, 60168, 60192, 88468]))
COUNTRIES.append(Country("Norway", 9, [2110, 2111, 2115, 2131, 7628, 19529]))
COUNTRIES.append(Country("Oceania", 15, [3208, 3209, 3213, 4214, 9095]))
COUNTRIES.append(Country("Oman", 134, [225713, 225714, 225718, 253423]))
COUNTRIES.append(Country("Pakistan", 71, [32093, 32094, 32098, 256431]))
COUNTRIES.append(Country("Palestine", 148, [252357, 252363, 252367, 252399]))
COUNTRIES.append(Country("Panama", 96, [60151, 60172, 60176, 76416, 116904]))
COUNTRIES.append(Country("Paraguay", 72, [42133, 42135, 42139, 76224, 203910]))
COUNTRIES.append(Country("People's Republic of China", 34, [3356, 3357, 3361, 13552, 98196]))
COUNTRIES.append(Country("Peru", 23, [3615, 3616, 13492, 33223, 49173]))
COUNTRIES.append(Country("Philippines", 55, [11429, 11430, 11434, 88852]))
COUNTRIES.append(Country("Poland", 24, [3620, 3621, 3625, 3641, 9383, 32114, 58605]))
COUNTRIES.append(Country("Portugal", 25, [3705, 3706, 3710, 3726, 9767, 10023]))
COUNTRIES.append(Country("Qatar", 141, [238789, 238855, 239119, 253295]))
COUNTRIES.append(Country("Romania", 37, [3854, 3855, 3859, 3875, 3939, 21961]))
COUNTRIES.append(Country("Russia", 35, [3187, 3188, 3192, 21897, 76480]))
COUNTRIES.append(Country("São Tomé e Príncipe", 149, [258094, 258095, 258099, 258285]))
COUNTRIES.append(Country("Saudi Arabia", 79, [48896, 48897, 48901, 245871, 253487]))
COUNTRIES.append(Country("Scotland", 26, [3166, 3167, 3171, 8054, 29789]))
COUNTRIES.append(Country("Senegal", 121, [123132, 123134, 123142, 256367]))
COUNTRIES.append(Country("Serbia", 57, [11471, 11472, 11476, 33751, 76736]))
COUNTRIES.append(Country("Singapore", 47, [4211, 4278, 4282, 4298, 16644]))
COUNTRIES.append(Country("Slovakia", 67, [29768, 29769, 29773, 65392, 88532]))
COUNTRIES.append(Country("Slovenia", 64, [14213, 14214, 14218, 18889, 65584]))
COUNTRIES.append(Country("South Africa", 27, [3161, 3162, 9351, 68656]))
COUNTRIES.append(Country("South Korea", 30, [3140, 3141, 3145, 98132, 117416]))
COUNTRIES.append(Country("Spain", 36, [3403, 3404, 3408, 3424, 5514, 14319, 38037]))
COUNTRIES.append(Country("Sri Lanka", 152, [258052, 258053, 258057, 258349]))
COUNTRIES.append(Country("Suriname", 113, [98835, 98836, 98840]))
COUNTRIES.append(Country("Sweden", 1, [1, 2, 6, 22, 86, 745]))
COUNTRIES.append(Country("Switzerland", 46, [4206, 8694, 8698, 11620, 16367, 20553, 30045]))
COUNTRIES.append(Country("Syria", 140, [238748, 238753, 238773, 253743]))
COUNTRIES.append(Country("Tanzania", 142, [238790, 238859, 239135]))
COUNTRIES.append(Country("Thailand", 31, [3119, 3120, 3124, 3790, 16964]))
COUNTRIES.append(Country("Trinidad & Tobago", 110, [98772, 98773, 98777]))
COUNTRIES.append(Country("Tunisia", 80, [53781, 53782, 53786, 238491, 253807]))
COUNTRIES.append(Country("Turkey", 32, [3098, 3099, 3103, 4362, 48917, 81088]))
COUNTRIES.append(Country("Uganda", 143, [245936, 245941, 245961]))
COUNTRIES.append(Country("Ukraine", 68, [33138, 33139, 33143, 65520, 122792]))
COUNTRIES.append(Country("United Arab Emirates", 83, [56880, 56885, 56905, 239407, 254063]))
COUNTRIES.append(Country("Uruguay", 28, [3013, 3014, 3018, 33159, 33815]))
COUNTRIES.append(Country("USA", 8, [597, 598, 602, 618, 8374, 27337]))
COUNTRIES.append(Country("Uzbekistan", 145, [252316, 252337, 252341]))
COUNTRIES.append(Country("Wales", 61, [16623, 16624, 16628, 21833, 203206]))
COUNTRIES.append(Country("Venezuela", 29, [3008, 3009, 13476, 33607, 56921]))
COUNTRIES.append(Country("Vietnam", 70, [28425, 28426, 28430, 76352, 249369]))
COUNTRIES.append(Country("Yemen", 133, [225688, 225693, 225697, 252911]))


for country in COUNTRIES:
    ids = []
    for division, LeagueIDstart in enumerate(country.leagueStructure):
        for i in range(SIZE[division]):
            ids.append(LeagueIDstart+i)
    ids = list(divide_chunks(ids, 100))
    dDivStructure = {}
    for blockID, block in enumerate(ids):
        dDivStructure[f"{blockID+1}"] = block

    data = {}
    data["leagueID"] = country.ID
    data["leagueName"] = country.name
    data["nbLeagues"] = len(country.leagueStructure)
    data["leagueStructure"] = dDivStructure
    datajson = json.dumps(data)

    r = requests.post("https://UNF6X7OJB7PFLVEQ.anvil.app/_/private_api/HN4JZ6UMWUM7I4PTILWZTJFD/create-league", json=datajson)
    print(r.text)

print("process complete !")

