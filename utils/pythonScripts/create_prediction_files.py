import xlwings as xw
import pandas as pd

output_folder = "D:/Temp"

df = xw.Book(r'HT_prediction_rating.xlsb').sheets("Factors 2").range("C7:S77").options(pd.DataFrame, index=2, header=2).value
print(df)

def create_midfield():
    f = open(f"{output_folder}/midfield.dat", 'w')
    lines = []
    lines.append("# analysis performed by akasolace")
    lines.append("")
    lines.append("[general]")
    lines.append("delta                = 0.75")
    lines.append("power                = 1.165")
    lines.append("squareMod            = 0")
    lines.append("cubeMod              = 0")
    lines.append("home                 = 1.199529")
    lines.append("awayDerby            = 1.113699")
    lines.append("pic                  = 0.839949")
    lines.append("mots                 = 1.109650")
    lines.append("teamSpiritPreMulti   = 0.147832")
    lines.append("teamSpiritPower      = 0.417779")
    lines.append("extraMulti           = 1.000")
    lines.append("tacticCounter        = 0.930000")
    lines.append("tacticLongshots      = 0.950323")
    lines.append("")
    lines.append("[playmaking_allsides]")
    lines.append(f"CD_norm    = {df.loc[('CD(Middle)', '#1')][('MID', 'PM')]}")
    lines.append(f"CD_off     = {df.loc[('CD(Middle) off', '#1')][('MID', 'PM')]}")
    lines.append(f"CD_tw      = {df.loc[('CD(Left) tw', '#1')][('MID', 'PM')]}")
    lines.append(f"WB_norm    = {df.loc[('WB(Left)', '#1')][('MID', 'PM')]}")
    lines.append(f"WB_off     = {df.loc[('WB(Left) off', '#1')][('MID', 'PM')]}")
    lines.append(f"WB_def     = {df.loc[('WB(Left) def', '#1')][('MID', 'PM')]}")
    lines.append(f"WB_tm      = {df.loc[('WB(Left) tm', '#1')][('MID', 'PM')]}")
    lines.append(f"IM_norm    = {df.loc[('IM', '#1')][('MID', 'PM')]}")
    lines.append(f"IM_off     = {df.loc[('IM off', '#1')][('MID', 'PM')]}")
    lines.append(f"IM_def     = {df.loc[('IM def', '#1')][('MID', 'PM')]}")
    lines.append(f"IM_tw      = {df.loc[('IM(Left) tw', '#1')][('MID', 'PM')]}")
    lines.append(f"WI_norm    = {df.loc[('WI(Left)', '#1')][('MID', 'PM')]}")
    lines.append(f"WI_off     = {df.loc[('WI(Left) off', '#1')][('MID', 'PM')]}")
    lines.append(f"WI_def     = {df.loc[('WI(Left) def', '#1')][('MID', 'PM')]}")
    lines.append(f"WI_tm      = {df.loc[('WI(Left) tm', '#1')][('MID', 'PM')]}")
    lines.append(f"FW_norm    = {df.loc[('FW', '#1')][('MID', 'PM')]}")
    lines.append(f"FW_def     = {df.loc[('FW_def', '#1')][('MID', 'PM')]}")
    lines.append(f"FW_tw      = {df.loc[('FW(Left) tw', '#1')][('MID', 'PM')]}")
    # lines.append("")
    f.write("\n".join(lines))
    f.close()
    print(f"{output_folder}/midfield.dat  has been created")
    print("\n".join(lines))


def create_central_defense():
    f = open(f"{output_folder}/centraldefense.dat", 'w')
    lines = []
    lines.append("# analysis performed by akasolace")
    lines.append("")
    lines.append("[general]")
    lines.append("delta                = 0.75")
    lines.append("power                = 1.165")
    lines.append("squareMod            = 0")
    lines.append("cubeMod              = 0")
    lines.append("extraMulti           = 1.000")
    lines.append("trainerOff           = 0.9")
    lines.append("trainerDef           = 1.15")
    lines.append("trainerNeutral       = 1.0")
    lines.append("tacticAOW            = 0.858029")
    lines.append("tacticCreative       = 0.930999")
    lines.append("defleadPercentDef    = 0.0")
    lines.append("pullback             = 0.125")

    lines.append("")
    lines.append("[goalkeeping_allsides]")
    lines.append(f"keeper    = {df.loc[('GK', '#1')][('CD', 'GK')]}")

    lines.append("")
    lines.append("[defending_allsides]")

    lines.append(f"keeper      = {df.loc[('GK', '#1')][('CD', 'DEF')]}")

    lines.append(f"CD_norm     = {df.loc[('CD(Middle)', '#1')][('CD', 'DEF')]}")
    lines.append(f"CD_off      = {df.loc[('CD(Middle) off', '#1')][('CD', 'DEF')]}")
    lines.append(f"CD_tw       = {df.loc[('CD(Left) tw', '#1')][('CD', 'DEF')]}")

    lines.append(f"WB_norm     = {df.loc[('WB(Left)', '#1')][('CD', 'DEF')]}")
    lines.append(f"WB_off      = {df.loc[('WB(Left) off', '#1')][('CD', 'DEF')]}")
    lines.append(f"WB_tm       = {df.loc[('WB(Left) tm', '#1')][('CD', 'DEF')]}")
    lines.append(f"WB_def      = {df.loc[('WB(Left) def', '#1')][('CD', 'DEF')]}")

    lines.append(f"IM_norm     = {df.loc[('IM(Left)', '#1')][('CD', 'DEF')]}")
    lines.append(f"IM_off      = {df.loc[('IM(Left) off', '#1')][('CD', 'DEF')]}")
    lines.append(f"IM_def      = {df.loc[('IM(Left) def', '#1')][('CD', 'DEF')]}")
    lines.append(f"IM_tw       = {df.loc[('IM(Left) tw', '#1')][('CD', 'DEF')]}")

    lines.append(f"WI_norm     = {df.loc[('WI(Left)', '#1')][('CD', 'DEF')]}")
    lines.append(f"WI_off      = {df.loc[('WI(Left) off', '#1')][('CD', 'DEF')]}")
    lines.append(f"WI_def      = {df.loc[('WI(Left) def', '#1')][('CD', 'DEF')]}")
    lines.append(f"WI_tm       = {df.loc[('WI(Left) tm', '#1')][('CD', 'DEF')]}")
    # lines.append("")
    f.write("\n".join(lines))
    f.close()
    print(f"{output_folder}/centraldefense.dat  has been created")
    print("\n".join(lines))


def create_side_defense():
    f = open(f"{output_folder}/sidedefense.dat", 'w')
    lines = []
    lines.append("# analysis performed by akasolace")
    lines.append("")
    lines.append("[general]")
    lines.append("delta                = 0.75")
    lines.append("power                = 1.165")
    lines.append("squareMod            = 0")
    lines.append("cubeMod              = 0")
    lines.append("extraMulti           = 1.000")
    lines.append("trainerOff           = 0.9")
    lines.append("trainerDef           = 1.15")
    lines.append("trainerNeutral       = 1.0")
    lines.append("tacticAIM            = 0.853911")
    lines.append("tacticCreative       = 0.930999")
    lines.append("defleadPercentDef    = 0.0")
    lines.append("pullback             = 0.125")

    lines.append("")
    lines.append("[goalkeeping_allsides]")
    lines.append(f"keeper    = {df.loc[('GK', '#1')][('LD', 'GK')]}")

    lines.append("")
    lines.append("[defending_allsides]")
    lines.append(f"keeper      = {df.loc[('GK', '#1')][('LD', 'DEF')]}")

    lines.append("")
    lines.append("[defending_thisside]")
    lines.append(f"CD_norm     = {df.loc[('CD(Left)', '#1')][('LD', 'DEF')]}")
    lines.append(f"CD_off      = {df.loc[('CD(Left) off', '#1')][('LD', 'DEF')]}")
    lines.append(f"CD_tw       = {df.loc[('CD(Left) tw', '#1')][('LD', 'DEF')]}")

    lines.append(f"WB_norm     = {df.loc[('WB(Left)', '#1')][('LD', 'DEF')]}")
    lines.append(f"WB_off      = {df.loc[('WB(Left) off', '#1')][('LD', 'DEF')]}")
    lines.append(f"WB_tm       = {df.loc[('WB(Left) tm', '#1')][('LD', 'DEF')]}")
    lines.append(f"WB_def      = {df.loc[('WB(Left) def', '#1')][('LD', 'DEF')]}")

    lines.append(f"IM_norm     = {df.loc[('IM(Left)', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_off      = {df.loc[('IM(Left) off', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_def      = {df.loc[('IM(Left) def', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_tw       = {df.loc[('IM(Left) tw', '#1')][('LD', 'DEF')]}")

    lines.append(f"WI_norm     = {df.loc[('WI(Left)', '#1')][('LD', 'DEF')]}")
    lines.append(f"WI_off      = {df.loc[('WI(Left) off', '#1')][('LD', 'DEF')]}")
    lines.append(f"WI_def      = {df.loc[('WI(Left) def', '#1')][('LD', 'DEF')]}")
    lines.append(f"WI_tm       = {df.loc[('WI(Left) tm', '#1')][('LD', 'DEF')]}")

    lines.append("")
    lines.append("[defending_middle]")
    lines.append(f"CD_norm     = {df.loc[('CD(Middle)', '#1')][('LD', 'DEF')]}")
    lines.append(f"CD_off      = {df.loc[('CD(Middle) off', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_norm     = {df.loc[('IM', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_off      = {df.loc[('IM off', '#1')][('LD', 'DEF')]}")
    lines.append(f"IM_def      = {df.loc[('IM def', '#1')][('LD', 'DEF')]}")

    # lines.append("")
    f.write("\n".join(lines))
    f.close()
    print(f"{output_folder}/sidedefense.dat  has been created")
    print("\n".join(lines))


def create_central_attack():
    f = open(f"{output_folder}/centralattack.dat", 'w')
    lines = []
    lines.append("# analysis performed by akasolace")
    lines.append("")
    lines.append("[general]")
    lines.append("delta                = 0.75")
    lines.append("power                = 1.165")
    lines.append("trainerOff           = 1.1")
    lines.append("trainerDef           = 0.9")
    lines.append("confidence           = 0.0525")
    lines.append("trainerNeutral       = 1.0")
    lines.append("tacticLongshots      = 0.970577")
    lines.append("pullback             = -0.25")


    lines.append("")
    lines.append("[passing_allsides]")

    lines.append(f"IM_norm               = {df.loc[('IM(Left)', '#1')][('CA', 'Passing')]}")
    lines.append(f"IM_off                = {df.loc[('IM(Left) off', '#1')][('CA', 'Passing')]}")
    lines.append(f"IM_def                = {df.loc[('IM(Left) def', '#1')][('CA', 'Passing')]}")
    lines.append(f"IM_tw                 = {df.loc[('IM(Left) tw', '#1')][('CA', 'Passing')]}")

    lines.append(f"WI_norm               = {df.loc[('WI(Left)', '#1')][('CA', 'Passing')]}")
    lines.append(f"WI_off                = {df.loc[('WI(Left) off', '#1')][('CA', 'Passing')]}")
    lines.append(f"WI_def                = {df.loc[('WI(Left) def', '#1')][('CA', 'Passing')]}")
    lines.append(f"WI_tm                 = {df.loc[('WI(Left) tm', '#1')][('CA', 'Passing')]}")

    lines.append(f"FW_norm               = {df.loc[('FW(Left)', '#1')][('CA', 'Passing')]}")
    lines.append(f"FW_def                = {df.loc[('FW(Left) def', '#1')][('CA', 'Passing')]}")
    lines.append(f"FW_def.technical      = {df.loc[('FW(Left) def (tech)', '#1')][('CA', 'Passing')]}")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('CA', 'Passing')]}")

    lines.append("")
    lines.append("[scoring_allsides]")

    lines.append(f"IM_norm               = {df.loc[('IM(Left)', '#1')][('CA', 'Scoring')]}")
    lines.append(f"IM_off                = {df.loc[('IM(Left) off', '#1')][('CA', 'Scoring')]}")
    lines.append(f"IM_def                = {df.loc[('IM(Left) def', '#1')][('CA', 'Scoring')]}")

    lines.append(f"FW_norm               = {df.loc[('FW(Left)', '#1')][('CA', 'Scoring')]}")
    lines.append(f"FW_def                = {df.loc[('FW(Left) def', '#1')][('CA', 'Scoring')]}")
    lines.append(f"FW_def.technical      = {df.loc[('FW(Left) def (tech)', '#1')][('CA', 'Scoring')]}")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('CA', 'Scoring')]}")

    # lines.append("")
    f.write("\n".join(lines))
    f.close()
    print(f"{output_folder}/centralattack.dat  has been created")
    print("\n".join(lines))


def create_side_attack():
    f = open(f"{output_folder}/sideattack.dat", 'w')
    lines = []
    lines.append("# analysis performed by akasolace")
    lines.append("")
    lines.append("[general]")
    lines.append("delta                = 0.75")
    lines.append("power                = 1.165")
    lines.append("trainerOff           = 1.1")
    lines.append("trainerDef           = 0.9")
    lines.append("trainerNeutral       = 1.0")
    lines.append("confidence           = 0.0525")
    lines.append("tacticLongshots       = 0.972980")
    lines.append("pullback             = -0.25")

    lines.append("")
    lines.append("[passing_middle]")
    lines.append(f"IM_norm               = {df.loc[('IM', '#1')][('LA', 'Passing')]}")
    lines.append(f"IM_off                = {df.loc[('IM off', '#1')][('LA', 'Passing')]}")
    lines.append(f"IM_def                = {df.loc[('IM def', '#1')][('LA', 'Passing')]}")

    lines.append("")
    lines.append("[passing_allsides]")
    lines.append(f"FW_norm               = {df.loc[('FW', '#1')][('LA', 'Passing')]}")
    lines.append(f"FW_def                = {df.loc[('FW_def', '#1')][('LA', 'Passing')]}")
    lines.append(f"FW_def.technical      = {df.loc[('FW_def(tech)', '#1')][('LA', 'Passing')]}")


    lines.append("")
    lines.append("[passing_thisside]")

    lines.append(f"IM_norm               = {df.loc[('IM(Left)', '#1')][('LA', 'Passing')]}")
    lines.append(f"IM_off                = {df.loc[('IM(Left) off', '#1')][('LA', 'Passing')]}")
    lines.append(f"IM_def                = {df.loc[('IM(Left) def', '#1')][('LA', 'Passing')]}")
    lines.append(f"IM_tw                 = {df.loc[('IM(Left) tw', '#1')][('LA', 'Passing')]}")

    lines.append(f"WI_norm               = {df.loc[('WI(Left)', '#1')][('LA', 'Passing')]}")
    lines.append(f"WI_off                = {df.loc[('WI(Left) off', '#1')][('LA', 'Passing')]}")
    lines.append(f"WI_def                = {df.loc[('WI(Left) def', '#1')][('LA', 'Passing')]}")
    lines.append(f"WI_tm                 = {df.loc[('WI(Left) tm', '#1')][('LA', 'Passing')]}")

    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('LA', 'Passing')]}")

    lines.append("")
    lines.append("[passing_otherside]")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('RA', 'Passing')]}")


    lines.append("")
    lines.append("[winger_thisside]")
    lines.append(f"CD_tw                  = {df.loc[('CD(Left) tw', '#1')][('LA', 'Winger')]}")

    lines.append(f"WB_norm                = {df.loc[('WB(Left)', '#1')][('LA', 'Winger')]}")
    lines.append(f"WB_off                 = {df.loc[('WB(Left) off', '#1')][('LA', 'Winger')]}")
    lines.append(f"WB_tm                  = {df.loc[('WB(Left) tm', '#1')][('LA', 'Winger')]}")
    lines.append(f"WB_def                 = {df.loc[('WB(Left) def', '#1')][('LA', 'Winger')]}")

    lines.append(f"IM_tw                 = {df.loc[('IM(Left) tw', '#1')][('LA', 'Winger')]}")

    lines.append(f"WI_norm               = {df.loc[('WI(Left)', '#1')][('LA', 'Winger')]}")
    lines.append(f"WI_off                = {df.loc[('WI(Left) off', '#1')][('LA', 'Winger')]}")
    lines.append(f"WI_def                = {df.loc[('WI(Left) def', '#1')][('LA', 'Winger')]}")
    lines.append(f"WI_tm                 = {df.loc[('WI(Left) tm', '#1')][('LA', 'Winger')]}")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('LA', 'Winger')]}")

    lines.append("")
    lines.append("[winger_allsides]")
    lines.append(f"FW_norm               = {df.loc[('FW(Left)', '#1')][('LA', 'Winger')]}")
    lines.append(f"FW_def                = {df.loc[('FW(Left) def', '#1')][('LA', 'Winger')]}")
    lines.append(f"FW_def.technical      = {df.loc[('FW(Left) def (tech)', '#1')][('LA', 'Winger')]}")


    lines.append("")
    lines.append("[winger_otherside]")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('RA', 'Winger')]}")

    lines.append("")
    lines.append("[scoring_allsides]")
    lines.append(f"FW_norm               = {df.loc[('FW', '#1')][('LA', 'Scoring')]}")
    lines.append(f"FW_def                = {df.loc[('FW_def', '#1')][('LA', 'Scoring')]}")
    lines.append(f"FW_def.technical      = {df.loc[('FW_def(tech)', '#1')][('LA', 'Scoring')]}")

    lines.append("")
    lines.append("[scoring_otherside]")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('RA', 'Scoring')]}")

    lines.append("")
    lines.append("[scoring_thisside]")
    lines.append(f"FW_tw                 = {df.loc[('FW(Left) tw', '#1')][('LA', 'Scoring')]}")


    # lines.append("")
    f.write("\n".join(lines))
    f.close()
    print(f"{output_folder}/sideattack.dat  has been created")
    print("\n".join(lines))

create_midfield()
create_central_defense()
create_side_defense()
create_central_attack()
create_side_attack()