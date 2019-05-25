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


class MatchOrder(Enum): #TODO: put correct data
    NORMAL = 0
    OFFENSIVE = 1
    DEFENSIVE = 2
    TOWARDS_MIDDLE = 3
    TOWARDS_WING = 4


def validateLineup(requiredLineup):

    for position, order, bPresent in requiredLineup:
        if bPresent:
            if position == Position.GK:
                assert order == MatchOrder.NORMAL, f"{position}  can't receive  {order} order"
            elif position in [Position.WBr, Position.WBl, Position.WIr, Position.WIl]:
                assert order != MatchOrder.TOWARDS_WING, f"{position} can't receive  {order} order"
            elif position in [Position.CDr, Position.CDl]:
                assert order in [MatchOrder.NORMAL, MatchOrder.TOWARDS_WING, MatchOrder.OFFENSIVE], f"{position} can't receive  {order} order"
            elif position == Position.CD:
                assert order in [MatchOrder.NORMAL, MatchOrder.OFFENSIVE], f"{position} can't receive  {order} order"
            elif position in [Position.IMr, Position.IMl]:
                assert order != MatchOrder.TOWARDS_MIDDLE, f"{position} can't receive  {order} order"
            elif position == Position.IM:
                assert order in [MatchOrder.NORMAL, MatchOrder.OFFENSIVE, MatchOrder.DEFENSIVE], f"{position} can't receive  {order} order"
            elif position in [Position.FWl, Position.FWr]:
                assert order in [MatchOrder.NORMAL, MatchOrder.TOWARDS_WING, MatchOrder.DEFENSIVE], f"{position} can't receive {order} order"
            elif position == Position.FW:
                assert order in [MatchOrder.NORMAL, MatchOrder.DEFENSIVE], f"{position} can't receive {order} order"
            else:
                raise ValueError(f"position {position} is not yet handled")


def createJson(lineupName, requiredLineup, attitude, tactic):
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

    # with open(r"D:\TEMP\feedback.json", "w") as write_file:
    #     json.dump(json_data, write_file, indent=4)
    with open(r"docs/feedback.json", "w") as write_file:
        json.dump(json_data, write_file, indent=4)


requiredLineup = []

requiredLineup.append((Position.GK, MatchOrder.NORMAL, True))
requiredLineup.append((Position.WBr, MatchOrder.DEFENSIVE, False))
requiredLineup.append((Position.WIl, MatchOrder.TOWARDS_MIDDLE, False))
requiredLineup.append((Position.FWl, MatchOrder.TOWARDS_WING, False))

createJson("GK", requiredLineup, "Normal", "Normal")





