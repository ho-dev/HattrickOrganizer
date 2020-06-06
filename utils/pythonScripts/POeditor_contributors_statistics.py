import xlwings as xw
import pandas as pd

dbLocation = "D:\\TEMP\\"

# bookName = "stats_by_languages.xls"
# wb = xw.Book(dbLocation+bookName)
# ws = wb.sheets[0]
# rng = ws.range("A4").expand("table")
# df_stats_by_languages = rng.options(pd.DataFrame).value

bookName = "stats_by_contributors.xls"
wb = xw.Book(dbLocation+bookName)
ws = wb.sheets[0]
rng = ws.range("A4").expand("table")
df_stats_by_contributors = rng.options(pd.DataFrame).value

for i, row in df_stats_by_contributors.iterrows():
    contributor = i.split(" ")[0]
    if contributor not in ["akasolace", "Total"]:
        print (contributor, int(row["Translations"]))


# print(df_stats_by_languages)
#
# print(df_stats_by_contributors)