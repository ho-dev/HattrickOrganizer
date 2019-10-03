from enum import Enum
import json


class Position(Enum):
    GK = 100
    WBr = 101
    CDr = 102
    CD = 103
    CDl = 104
    WBl = 105
    WIr = 106
    IMr = 107
    IM = 108
    IMl = 109
    WIl = 110
    FWr = 111
    FW = 112
    FWl = 113


class MatchOrder(Enum):
    NORMAL = 0
    OFFENSIVE = 1
    DEFENSIVE = 2
    TOWARDS_MIDDLE = 3
    TOWARDS_WING = 4


class Attitute(Enum):
    NORMAL = "normal"
    PIC = "playitcool"
    MOTS = "matchoftheseason"


class Tactic(Enum):
    NORMAL = "normal"
    PRESSING = "pressing"
    CA = "counter-attacks"
    AIM = "attackinthemiddle"
    AOW = "attackonwings"
    PC = "playcreatively"
    LS = "longshots"

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

def validateLineup(requiredLineup):
    for position, order, bPresent in requiredLineup:
        if bPresent:
            if position == Position.GK:
                assert order == MatchOrder.NORMAL, f"{position}  can't receive  {order} order"
            elif position in [Position.WBr, Position.WBl, Position.WIr, Position.WIl]:
                assert order != MatchOrder.TOWARDS_WING, f"{position} can't receive  {order} order"
            elif position in [Position.CDr, Position.CDl]:
                assert order in [MatchOrder.NORMAL, MatchOrder.TOWARDS_WING,
                                 MatchOrder.OFFENSIVE], f"{position} can't receive  {order} order"
            elif position == Position.CD:
                assert order in [MatchOrder.NORMAL, MatchOrder.OFFENSIVE], f"{position} can't receive  {order} order"
            elif position in [Position.IMr, Position.IMl]:
                assert order != MatchOrder.TOWARDS_MIDDLE, f"{position} can't receive  {order} order"
            elif position == Position.IM:
                assert order in [MatchOrder.NORMAL, MatchOrder.OFFENSIVE,
                                 MatchOrder.DEFENSIVE], f"{position} can't receive  {order} order"
            elif position in [Position.FWl, Position.FWr]:
                assert order in [MatchOrder.NORMAL, MatchOrder.TOWARDS_WING,
                                 MatchOrder.DEFENSIVE], f"{position} can't receive {order} order"
            elif position == Position.FW:
                assert order in [MatchOrder.NORMAL, MatchOrder.DEFENSIVE], f"{position} can't receive {order} order"
            else:
                raise ValueError(f"position {position} is not yet handled")


def validate_answer(_answer):
    try:
        _answer = _answer.lower()
    except:
        print("Answer was not understood")
        return None
    if (_answer == 'y') or (_answer == "yes"):
        return "yes"
    elif (_answer == 'no') or (_answer == 'no'):
        return "no"
    else:
        print("Answer was not understood")
        return None

def createJson(lineupName, requiredLineup, attitude, tactic, bPRODUCTION):
    attitude, tactic = attitude.value, tactic.value
    validateLineup(requiredLineup)
    json_data = {}
    lineup = {}
    for position, order, bPresent in requiredLineup:
        if bPresent:
            lineup[str(position.value)] = order.value

    json_data["lineupName"] = lineupName
    json_data["lineup"] = lineup
    json_data["attitude"] = attitude
    json_data["tatic"] = tactic

    path = r"D:\TEMP\feedback.json"
    answer = None

    if not bPRODUCTION:
        while (answer != "yes" and answer != "no"):
            answer = input("Pushing to " + bcolors.OKGREEN + "TEST" + bcolors.ENDC + ", do you want to continue, (y)es / (n)o ? ")
            answer = validate_answer(answer)
    else:
        while (answer != "yes" and answer != "no"):
            answer = input("Pushing to " + bcolors.WARNING + "PRODUCTION" + bcolors.ENDC + ", do you want to continue, (y)es / (n)o ?  ")
            answer = validate_answer(answer)

    if answer == "yes":
        if bPRODUCTION:
            path = r"docs/feedback.json"
        with open(path, "w") as write_file:
            json.dump(json_data, write_file, indent=4)



requiredLineup = []

# requiredLineup.append((Position.GK, MatchOrder.NORMAL, True))
requiredLineup.append((Position.CD, MatchOrder.NORMAL, True))
# requiredLineup.append((Position.WBr, MatchOrder.DEFENSIVE, False))
# requiredLineup.append((Position.WIl, MatchOrder.TOWARDS_MIDDLE, False))
# requiredLineup.append((Position.FWl, MatchOrder.TOWARDS_WING, False))

createJson("CDc-Normal", requiredLineup, Attitute.NORMAL, Tactic.NORMAL, bPRODUCTION = True)
