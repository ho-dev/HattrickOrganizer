import pandas as pd
import xlwings as xw
import json


TRANSLATOR_FILE = "D:/TEMP/Translators.xlsx"
OUTPUT_FILE = "D:/Perso/Code/foxtrick/res/staff/ho.json"

developers = [("13441219", "capitaineFro"), ("13458976", "Mistermax80"), ("11370368", "yaute"), ("10764047", "brokenelevator"),
              ("6992417", "tychobrailleur"), ("1757369","wsbrenk"), ("5199830", "xxPhAIxx"), ("13609150", "stefats")]


helpers = [("9100817", "DavidatorusF")]

ho_json_str = """
{
	"type": "ho",
	"url": "https://ho-dev.github.io/HattrickOrganizer/",
	"duties" : {
		"developer" : {
			"alt": "HO developer"
		},
		"translator" : {
			"alt": "HO translator"
		},
		"helper" : {
			"alt": "HO helper"
		}
	}
}
"""

ho_json = json.loads(ho_json_str)

ll = []


# TRANSLATORS ==============================================================================
wb = xw.Book(TRANSLATOR_FILE)
sht = wb.sheets[0]
df = sht.range('A1').options(pd.DataFrame, expand='table', header=1, index=1).value

for index, row in df.iterrows():
    d = {}
    d["id"] = str(int(row["HT Manager ID"]))
    d["name"] = row["HT Manager name"]
    d["duty"] = "translator"
    ll.append(d)



# helpers =======================================================
for helperID, helperName in helpers:
    d = {}
    d["id"] = helperID
    d["name"] = helperName
    d["duty"] = "helper"
    ll.append(d)



# developers =======================================================
for devID, devName in developers:
    d = {}
    d["id"] = devID
    d["name"] = devName
    d["duty"] = "developer"
    ll.append(d)

ho_json["list"] = ll

with open(OUTPUT_FILE, "w") as write_file:
    json.dump(ho_json, write_file, indent=4)
