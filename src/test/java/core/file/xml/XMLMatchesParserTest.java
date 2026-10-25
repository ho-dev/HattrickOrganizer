package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.ResourceUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static core.model.match.MatchKurzInfo.FINISHED;
import static core.model.match.MatchKurzInfo.UPCOMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XMLMatchesParserTest {

    private static ArrayList<MatchKurzInfo> createExpectedMatches() {
        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(768443696, 977497, "Hotzenwälder SC", 520472, "Juventus Brenk", "2026-09-26 18:00:00", 1, 0, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(771662522, 520472, "Juventus Brenk", 2318194, "Ruti88", "2026-09-29 18:25:00", 9, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443702, 1247727, "FC Zwietracht Cuxhaven", 520472, "Juventus Brenk", "2026-10-03 18:00:00", 1, 0, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(771747669, 2825742, "Macarrisme Català", 520472, "Juventus Brenk", "2026-10-07 15:15:00", 9, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443705, 520472, "Juventus Brenk", 130796, "Galatasaray-SC", "2026-10-10 18:00:00", 1, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(771832586, 2189893, "Balón a Tierra", 520472, "Juventus Brenk", "2026-10-14 12:25:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443710, 824658, "SK Lation 16", 520472, "Juventus Brenk", "2026-10-17 18:00:00", 1, 0, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(771917205, 2832566, "De Soepsterren", 520472, "Juventus Brenk", "2026-10-21 11:15:00", 9, 0, 0, 0, 0, 5, FINISHED, true));
        expectedMatches.add(createMatch(768443715, 520472, "Juventus Brenk", 339078, "Haake-Becker Jungs", "2026-10-24 18:00:00", 1, 0, 0, 0, -1, -1, UPCOMING, true));
        expectedMatches.add(createMatch(772000965, 520472, "Juventus Brenk", 3445201, "Grebenčeva team", "2026-10-27 18:25:00", 9, 0, 0, 0, -1, -1, UPCOMING, false));

        return expectedMatches;
    }

    private static ArrayList<MatchKurzInfo> createExpectedMatchesWithCup() {
        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(42217559, 601338, "Gusy's Champions", 333027, "Fortuna Fippes Freiburg", "2026-09-29 13:42:00", 50, 7139879, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(771641277, 333586, "Offenbach am Main Kickers", 333027, "Fortuna Fippes Freiburg", "2026-09-29 18:00:00", 3, 4, 1, 1, 0, 1, FINISHED, true));
        expectedMatches.add(createMatch(768429927, 333027, "Fortuna Fippes Freiburg", 128858, "FC Renaissance Ried", "2026-10-03 18:00:00", 1, 427, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(771726780, 6903, "TSV Sace", 333027, "Fortuna Fippes Freiburg", "2026-10-06 18:00:00", 3, 4, 1, 1, 2, 3, FINISHED, true));
        expectedMatches.add(createMatch(768429931, 1286887, "Hertha SCB", 333027, "Fortuna Fippes Freiburg", "2026-10-10 18:00:00", 1, 427, 0, 0, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(42298460, 601338, "Gusy's Champions", 333027, "Fortuna Fippes Freiburg", "2026-10-12 11:13:00", 50, 7139879, 0, 0, 4, 1, FINISHED, true));
        expectedMatches.add(createMatch(42301387, 601338, "Gusy's Champions", 333027, "Fortuna Fippes Freiburg", "2026-10-12 22:29:00", 50, 7139879, 0, 0, 3, 5, FINISHED, true));
        expectedMatches.add(createMatch(771811846, 644914, "Eintracht Leipzig 05", 333027, "Fortuna Fippes Freiburg", "2026-10-13 18:00:00", 3, 4, 1, 1, 3, 4, FINISHED, true));
        expectedMatches.add(createMatch(768429935, 333027, "Fortuna Fippes Freiburg", 1002098, "FC Superwixens", "2026-10-17 18:00:00", 1, 427, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(771896643, 334857, "Lila-Weiß Wolkenkuckucksheim", 333027, "Fortuna Fippes Freiburg", "2026-10-20 18:00:00", 3, 4, 1, 1, 2, 3, FINISHED, true));
        expectedMatches.add(createMatch(768429939, 820764, "FC Wieselhausen", 333027, "Fortuna Fippes Freiburg", "2026-10-24 18:00:00", 1, 427, 0, 0, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(42360679, 333027, "Fortuna Fippes Freiburg", 367379, "DenkesFC", "2026-10-26 07:05:00", 50, 3116071, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(772006738, 333027, "Fortuna Fippes Freiburg", 820764, "FC Wieselhausen", "2026-10-27 18:20:00", 5, 0, 0, 0, -1, -1, UPCOMING, false));
        return expectedMatches;
    }

    private static ArrayList<MatchKurzInfo> createExpectedMatchesWithRubinCup() {
        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(771641196, 824579, "Barmbeggers", 520805, "Hansa Wittstock", "2026-09-29 18:30:00", 3, 643, 2, 2, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(768430542, 1136812, "Viktoria Appenweier", 520805, "Hansa Wittstock", "2026-10-03 18:00:00", 1, 438, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(771726750, 979564, "FC Sensederbfuss", 520805, "Hansa Wittstock", "2026-10-06 18:30:00", 3, 643, 2, 2, 2, 5, FINISHED, true));
        expectedMatches.add(createMatch(768430545, 520805, "Hansa Wittstock", 523612, "Latin Vikings", "2026-10-10 18:00:00", 1, 438, 0, 0, 2, 2, FINISHED, true));
        expectedMatches.add(createMatch(771811831, 1196135, "Fanfaria Scofield FC.", 520805, "Hansa Wittstock", "2026-10-13 18:30:00", 3, 643, 2, 2, 4, 5, FINISHED, true));
        expectedMatches.add(createMatch(768430552, 1137230, "FC Hajde Hamburg", 520805, "Hansa Wittstock", "2026-10-17 18:00:00", 1, 438, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(771896635, 520805, "Hansa Wittstock", 130343, "Tantris Shanghai", "2026-10-20 18:30:00", 3, 643, 2, 2, 4, 1, FINISHED, true));
        expectedMatches.add(createMatch(768430555, 520805, "Hansa Wittstock", 129528, "Gladiators Cologne FC 03", "2026-10-24 18:00:00", 1, 438, 0, 0, 6, 2, FINISHED, true));
        expectedMatches.add(createMatch(771981300, 1196481, "Village Bombers United", 520805, "Hansa Wittstock", "2026-10-27 18:30:00", 3, 643, 2, 2, -1, -1, UPCOMING, false));
        return expectedMatches;
    }

    private static MatchKurzInfo createMatch(int matchID, int homeTeamID, String homeTeam, int awayTeamID, String awayTeam, String matchDate, int matchType, int matchContextID, int cupLevel, int cupLevelIndex, int homeGoals, int awayGoals, int status, boolean isMatchOrderSet) {
        var m = new MatchKurzInfo();
        m.setMatchID(matchID);
        m.setHomeTeamID(homeTeamID);
        m.setHomeTeamName(homeTeam);
        m.setGuestTeamID(awayTeamID);
        m.setGuestTeamName(awayTeam);
        m.setMatchSchedule(HODateTime.fromHT(matchDate));
        m.setMatchType(MatchType.getById(matchType));
        m.setMatchContextId(matchContextID);
        m.setCupLevel(CupLevel.fromInt(cupLevel));
        m.setCupLevelIndex(CupLevelIndex.fromInt(cupLevelIndex));
        m.setHomeTeamGoals(homeGoals);
        m.setGuestTeamGoals(awayGoals);
        m.setMatchStatus(status);
        m.setOrdersGiven(isMatchOrderSet);
        return m;
    }

    private static ArrayList<MatchKurzInfo> createExpectedMatchesWithTournament() {
        ArrayList<MatchKurzInfo> expectedMatches = new ArrayList<>();
        expectedMatches.add(createMatch(42188432, 263273, "Amiga Janow", 669787, "FC Ruja", "2026-09-25 10:54:00", 62, 1255256, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(42164578, 669787, "FC Ruja", 673239, "FC Žaliakalnis", "2026-09-25 19:20:00", 50, 3686848, 0, 0, 3, 2, FINISHED, true));
        expectedMatches.add(createMatch(42207386, 484324, "Argentina", 669787, "FC Ruja", "2026-09-26 11:13:00", 50, 7153345, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(768137612, 670092, "FC Kvieciai", 669787, "FC Ruja", "2026-09-26 15:20:00", 1, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42207718, 2137810, "Caribbean Hammers", 669787, "FC Ruja", "2026-09-27 13:51:00", 62, 3140692, 0, 0, 4, 5, FINISHED, true));
        expectedMatches.add(createMatch(42208834, 2871154, "Leprae", 669787, "FC Ruja", "2026-09-27 21:21:00", 62, 3140761, 0, 0, 4, 0, FINISHED, true));
        expectedMatches.add(createMatch(42177804, 669787, "FC Ruja", 2131566, "Athloi United", "2026-09-28 11:22:00", 51, 7094081, 0, 0, 8, 6, FINISHED, true));
        expectedMatches.add(createMatch(42217467, 484324, "Argentina", 669787, "FC Ruja", "2026-09-29 12:49:00", 50, 7153345, 0, 0, 4, 2, FINISHED, true));
        expectedMatches.add(createMatch(42218201, 484324, "Argentina", 669787, "FC Ruja", "2026-09-29 17:17:00", 50, 7153345, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42220118, 484324, "Argentina", 669787, "FC Ruja", "2026-09-30 08:40:00", 50, 7153345, 0, 0, 4, 4, FINISHED, true));
        expectedMatches.add(createMatch(42217171, 932038, "Byrde Boldklub", 669787, "FC Ruja", "2026-09-30 10:39:00", 62, 3140761, 0, 0, 1, 4, FINISHED, true));
        expectedMatches.add(createMatch(42220411, 484324, "Argentina", 669787, "FC Ruja", "2026-09-30 10:44:00", 50, 7153345, 0, 0, 4, 0, FINISHED, true));
        expectedMatches.add(createMatch(42220983, 484324, "Argentina", 669787, "FC Ruja", "2026-09-30 12:47:00", 50, 7153345, 0, 0, 4, 2, FINISHED, true));
        expectedMatches.add(createMatch(42221679, 484324, "Argentina", 669787, "FC Ruja", "2026-09-30 17:51:00", 50, 7153345, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(771672468, 669787, "FC Ruja", 614767, "Tourinorum FC", "2026-09-30 19:20:00", 9, 0, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42223596, 484324, "Argentina", 669787, "FC Ruja", "2026-10-01 07:39:00", 50, 7153345, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42224032, 484324, "Argentina", 669787, "FC Ruja", "2026-10-01 10:40:00", 50, 7153345, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(42224415, 669787, "FC Ruja", 59972, "Sami Panda", "2026-10-01 17:53:00", 62, 1255256, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(42224409, 669787, "FC Ruja", 1069888, "Kåravikjo Bulldogs", "2026-10-02 13:09:00", 62, 3140761, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42224427, 669787, "FC Ruja", 856980, "Real Tuorloduovo", "2026-10-02 13:16:00", 62, 3140692, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42203024, 669787, "FC Ruja", -1, "Zakrock Rooch", "2026-10-02 19:20:00", 50, 3686848, 0, 0, 13, 0, FINISHED, true));
        expectedMatches.add(createMatch(42228024, 669787, "FC Ruja", 1983262, "rabotnishki", "2026-10-03 08:05:00", 62, 1255256, 0, 0, 0, 1, FINISHED, true));
        expectedMatches.add(createMatch(768137620, 483189, "Ukmergės Squier", 669787, "FC Ruja", "2026-10-03 15:20:00", 1, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(42249739, 669787, "FC Ruja", 2800474, "2LOS LEONES", "2026-10-06 04:10:00", 62, 3140761, 0, 0, 1, 7, FINISHED, true));
        expectedMatches.add(createMatch(42249709, 669787, "FC Ruja", 1016713, "FC Perpetuo Servaturum", "2026-10-06 08:06:00", 62, 3140692, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42249728, 669787, "FC Ruja", 2061041, "Wise Guys", "2026-10-06 08:12:00", 62, 1255256, 0, 0, 4, 1, FINISHED, true));
        expectedMatches.add(createMatch(42249731, 669787, "FC Ruja", 274092, "Bitacora F.C.", "2026-10-06 08:14:00", 62, 1258033, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42253354, 484321, "Cabo Verde", 669787, "FC Ruja", "2026-10-06 08:40:00", 50, 7164836, 0, 0, 0, 0, FINISHED, true));
        expectedMatches.add(createMatch(42254243, 484321, "Cabo Verde", 669787, "FC Ruja", "2026-10-06 12:23:00", 50, 7164836, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42256392, 484322, "Norge", 669787, "FC Ruja", "2026-10-06 18:24:00", 50, 7164836, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42257413, 484322, "Norge", 669787, "FC Ruja", "2026-10-06 21:17:00", 50, 7164836, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42259720, 484323, "England", 669787, "FC Ruja", "2026-10-07 09:55:00", 50, 7164836, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42260711, 484323, "England", 669787, "FC Ruja", "2026-10-07 12:13:00", 50, 7164836, 0, 0, 0, 1, FINISHED, true));
        expectedMatches.add(createMatch(42261471, 484324, "Argentina", 669787, "FC Ruja", "2026-10-07 14:54:00", 50, 7164836, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42262136, 484323, "England", 669787, "FC Ruja", "2026-10-07 17:04:00", 50, 7164836, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(771733518, 669787, "FC Ruja", 2071147, "zYxJaG_Fc", "2026-10-07 19:20:00", 9, 0, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42262937, 484324, "Argentina", 669787, "FC Ruja", "2026-10-07 19:34:00", 50, 7164836, 0, 0, 5, 1, FINISHED, true));
        expectedMatches.add(createMatch(42263902, 484324, "Argentina", 669787, "FC Ruja", "2026-10-07 21:55:00", 50, 7164836, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(42265578, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 06:02:00", 50, 7164836, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42266038, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 08:15:00", 50, 7164836, 0, 0, 2, 2, FINISHED, true));
        expectedMatches.add(createMatch(42266757, 484326, "The Thinkers", 669787, "FC Ruja", "2026-10-08 10:52:00", 50, 7167376, 0, 0, 0, 10, FINISHED, true));
        expectedMatches.add(createMatch(42266769, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 10:54:00", 50, 7164836, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42260137, 669787, "FC Ruja", 327894, "Colonia Avellaneda", "2026-10-08 11:09:00", 62, 3140692, 0, 0, 5, 1, FINISHED, true));
        expectedMatches.add(createMatch(42260163, 669787, "FC Ruja", 135139, "Güngi United", "2026-10-08 11:16:00", 62, 1255256, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42260171, 669787, "FC Ruja", 2021208, "Sparta Cvrcek Oman", "2026-10-08 11:17:00", 62, 3140761, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(42260185, 669787, "FC Ruja", 1174259, "Vendetta23", "2026-10-08 11:21:00", 62, 1258033, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42267692, 484328, "Symphony FC", 669787, "FC Ruja", "2026-10-08 13:13:00", 50, 7167376, 0, 0, 0, 10, FINISHED, true));
        expectedMatches.add(createMatch(42267717, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 13:17:00", 50, 7164836, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42268514, 484329, "Science Lab", 669787, "FC Ruja", "2026-10-08 16:03:00", 50, 7167376, 0, 0, 0, 7, FINISHED, true));
        expectedMatches.add(createMatch(42268517, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 16:04:00", 50, 7164836, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(42269194, 484330, "Literary Club", 669787, "FC Ruja", "2026-10-08 18:13:00", 50, 7167376, 0, 0, 0, 9, FINISHED, true));
        expectedMatches.add(createMatch(42269197, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 18:13:00", 50, 7164836, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42270270, 484332, "The Brushstrokes", 669787, "FC Ruja", "2026-10-08 21:01:00", 50, 7167376, 0, 0, 0, 7, FINISHED, true));
        expectedMatches.add(createMatch(42270283, 484324, "Argentina", 669787, "FC Ruja", "2026-10-08 21:04:00", 50, 7164836, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42271297, 484334, "Silver Screen Stars", 669787, "FC Ruja", "2026-10-09 02:28:00", 50, 7167376, 0, 0, 0, 6, FINISHED, true));
        expectedMatches.add(createMatch(42271298, 484324, "Argentina", 669787, "FC Ruja", "2026-10-09 02:29:00", 50, 7164836, 0, 0, 0, 1, FINISHED, true));
        expectedMatches.add(createMatch(42271528, 484333, "The Greatest Hits", 669787, "FC Ruja", "2026-10-09 05:53:00", 50, 7167376, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(42271532, 484325, "España", 669787, "FC Ruja", "2026-10-09 05:57:00", 50, 7164836, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42272813, 669787, "FC Ruja", 2353321, "- Trotter C&F Club -", "2026-10-09 11:01:00", 62, 1255256, 0, 0, 5, 0, FINISHED, true));
        expectedMatches.add(createMatch(42268560, 932038, "Byrde Boldklub", 669787, "FC Ruja", "2026-10-09 16:07:00", 62, 3140761, 0, 0, 3, 5, FINISHED, true));
        expectedMatches.add(createMatch(42272804, 669787, "FC Ruja", 107690, "FC Pine Box", "2026-10-10 10:30:00", 62, 3140692, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42272886, 669787, "FC Ruja", 1901514, "Chelsea Addict", "2026-10-10 10:35:00", 62, 1258033, 0, 0, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(768137622, 669787, "FC Ruja", 675264, "Timeteams", "2026-10-10 15:20:00", 1, 0, 0, 0, 4, 1, FINISHED, true));
        expectedMatches.add(createMatch(42294534, 669787, "FC Ruja", 1961030, "The owls are not what they seem", "2026-10-12 14:44:00", 62, 3140692, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42294559, 669787, "FC Ruja", 581729, "Józsi csapata", "2026-10-12 14:49:00", 62, 1255256, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42294562, 669787, "FC Ruja", 1901514, "Chelsea Addict", "2026-10-12 14:50:00", 62, 1258033, 0, 0, 0, 3, FINISHED, true));
        expectedMatches.add(createMatch(42297732, 669787, "FC Ruja", 342895, "dandush", "2026-10-13 07:44:00", 62, 3140761, 0, 0, 6, 0, FINISHED, true));
        expectedMatches.add(createMatch(42303531, 593920, "Mattias's Sunsets", 669787, "FC Ruja", "2026-10-13 12:50:00", 50, 7172108, 0, 0, 0, 9, FINISHED, true));
        expectedMatches.add(createMatch(42304075, 594042, "Dimitris's Designerds", 669787, "FC Ruja", "2026-10-13 15:01:00", 50, 7172108, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42304603, 594207, "Daniel's Surfers", 669787, "FC Ruja", "2026-10-13 17:38:00", 50, 7172108, 0, 0, 0, 5, FINISHED, true));
        expectedMatches.add(createMatch(42305646, 594241, "Jens's Runners", 669787, "FC Ruja", "2026-10-13 21:26:00", 50, 7172108, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42306810, 594424, "Aartspam's Falcons", 669787, "FC Ruja", "2026-10-14 07:38:00", 50, 7172108, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42301773, 669787, "FC Ruja", 223280, "Fighting Donkey's FC", "2026-10-14 08:32:00", 50, 7146394, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42307373, 594432, "Bodin's Boys", 669787, "FC Ruja", "2026-10-14 09:38:00", 50, 7172108, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(42303290, 669787, "FC Ruja", 1906472, "Verte collineSF", "2026-10-14 11:30:00", 62, 1255256, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42307736, 594495, "Johan's Olympians", 669787, "FC Ruja", "2026-10-14 11:40:00", 50, 7172108, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(42308190, 595952, "Tasos's Deus Ex Machina", 669787, "FC Ruja", "2026-10-14 13:48:00", 50, 7172108, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(42308826, 596055, "Chase's All Stars", 669787, "FC Ruja", "2026-10-14 15:56:00", 50, 7172108, 0, 0, 0, 1, FINISHED, true));
        expectedMatches.add(createMatch(42309265, 597783, "Juan's Morphy FC", 669787, "FC Ruja", "2026-10-14 17:56:00", 50, 7172108, 0, 0, 0, 0, FINISHED, true));
        expectedMatches.add(createMatch(771820886, 669787, "FC Ruja", 282180, "Whitehead Nigeria", "2026-10-14 19:20:00", 9, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42309660, 597783, "Juan's Morphy FC", 669787, "FC Ruja", "2026-10-14 20:00:00", 50, 7172108, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(42310086, 597783, "Juan's Morphy FC", 669787, "FC Ruja", "2026-10-14 22:13:00", 50, 7172108, 0, 0, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(42301787, 3463215, "Fc Bussikuskit", 669787, "FC Ruja", "2026-10-15 08:32:00", 50, 7146394, 0, 0, 0, 3, FINISHED, true));
        expectedMatches.add(createMatch(42311410, 597926, "TaelS's Dark Illusions", 669787, "FC Ruja", "2026-10-15 09:02:00", 50, 7172108, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(42311789, 597926, "TaelS's Dark Illusions", 669787, "FC Ruja", "2026-10-15 11:07:00", 50, 7172108, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42307722, 669787, "FC Ruja", 1961030, "The owls are not what they seem", "2026-10-15 11:18:00", 62, 3140692, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42307732, 669787, "FC Ruja", 1901514, "Chelsea Addict", "2026-10-15 11:23:00", 62, 1258033, 0, 0, 8, 9, FINISHED, true));
        expectedMatches.add(createMatch(42307764, 669787, "FC Ruja", 160152, "Odd-Grenland", "2026-10-15 11:40:00", 62, 3140761, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42312380, 597926, "TaelS's Dark Illusions", 669787, "FC Ruja", "2026-10-15 13:35:00", 50, 7172108, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42313030, 597926, "TaelS's Dark Illusions", 669787, "FC Ruja", "2026-10-15 17:03:00", 50, 7172108, 0, 0, 1, 2, FINISHED, true));
        expectedMatches.add(createMatch(42313930, 594424, "Aartspam's Falcons", 669787, "FC Ruja", "2026-10-15 21:53:00", 50, 7172108, 0, 0, 0, 6, FINISHED, true));
        expectedMatches.add(createMatch(42314934, 595952, "Tasos's Deus Ex Machina", 669787, "FC Ruja", "2026-10-16 07:38:00", 50, 7172108, 0, 0, 3, 3, FINISHED, true));
        expectedMatches.add(createMatch(42301789, 669787, "FC Ruja", 649745, "Die Op!umbrigade", "2026-10-16 08:32:00", 50, 7146394, 0, 0, 1, 2, FINISHED, true));
        expectedMatches.add(createMatch(42315361, 595952, "Tasos's Deus Ex Machina", 669787, "FC Ruja", "2026-10-16 10:16:00", 50, 7172108, 0, 0, 3, 4, FINISHED, true));
        expectedMatches.add(createMatch(42316026, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-16 12:34:00", 50, 7172108, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42316940, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-16 17:12:00", 50, 7172108, 0, 0, 4, 0, FINISHED, true));
        expectedMatches.add(createMatch(42317449, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-16 20:09:00", 50, 7172108, 0, 0, 3, 1, FINISHED, true));
        expectedMatches.add(createMatch(42317821, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-16 22:24:00", 50, 7172108, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42314969, 669787, "FC Ruja", 2012237, "Abrakadabra FC", "2026-10-17 07:42:00", 62, 1255256, 0, 0, 5, 0, FINISHED, true));
        expectedMatches.add(createMatch(42301799, 2268079, "FC ShaChri", 669787, "FC Ruja", "2026-10-17 08:32:00", 50, 7146394, 0, 0, 0, 8, FINISHED, true));
        expectedMatches.add(createMatch(42319532, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-17 13:37:00", 50, 7172108, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42319565, 484321, "Cabo Verde", 669787, "FC Ruja", "2026-10-17 13:49:00", 50, 7175115, 0, 0, 0, 4, FINISHED, true));
        expectedMatches.add(createMatch(768137626, 674699, "Moons", 669787, "FC Ruja", "2026-10-17 15:20:00", 1, 0, 0, 0, 0, 3, FINISHED, true));
        expectedMatches.add(createMatch(42319852, 484322, "Norge", 669787, "FC Ruja", "2026-10-17 16:03:00", 50, 7175115, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(42319857, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-17 16:07:00", 50, 7172108, 0, 0, 4, 1, FINISHED, true));
        expectedMatches.add(createMatch(42320286, 484323, "England", 669787, "FC Ruja", "2026-10-17 19:22:00", 50, 7175115, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42320311, 601338, "Gusy's Champions", 669787, "FC Ruja", "2026-10-17 19:23:00", 50, 7172108, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42320954, 484323, "England", 669787, "FC Ruja", "2026-10-17 22:49:00", 50, 7175115, 0, 0, 2, 2, FINISHED, true));
        expectedMatches.add(createMatch(42301800, 669787, "FC Ruja", 2330514, "RCD edps", "2026-10-18 08:32:00", 50, 7146394, 0, 0, 4, 0, FINISHED, true));
        expectedMatches.add(createMatch(42318719, 527644, "FC Kosiv", 669787, "FC Ruja", "2026-10-18 09:01:00", 62, 3140692, 0, 0, 1, 3, FINISHED, true));
        expectedMatches.add(createMatch(42319053, 1743898, "Caballeros de Primavera", 669787, "FC Ruja", "2026-10-18 10:49:00", 62, 1255256, 0, 0, 4, 5, FINISHED, true));
        expectedMatches.add(createMatch(42290210, 669787, "FC Ruja", 421188, "UnlikelyFC", "2026-10-19 11:00:00", 51, 6535502, 0, 0, 1, 2, FINISHED, true));
        expectedMatches.add(createMatch(42325729, 669787, "FC Ruja", 1901514, "Chelsea Addict", "2026-10-19 21:37:00", 62, 1258033, 0, 0, 0, 3, FINISHED, true));
        expectedMatches.add(createMatch(42325734, 669787, "FC Ruja", 1805694, "Poznański Czerwiec 1956", "2026-10-19 21:40:00", 62, 3140692, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(42325742, 669787, "FC Ruja", 397010, "Malleus hereticorum", "2026-10-19 21:42:00", 62, 1255256, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42325750, 669787, "FC Ruja", 1108437, "Cubura Srbija", "2026-10-19 21:44:00", 62, 3140761, 0, 0, 8, 2, FINISHED, true));
        expectedMatches.add(createMatch(42301812, 669787, "FC Ruja", 1063974, "CSM Victoria Carei", "2026-10-21 08:32:00", 50, 7146394, 0, 0, 4, 0, FINISHED, true));
        expectedMatches.add(createMatch(42334691, 669787, "FC Ruja", 1682845, "FC Michálkovice", "2026-10-21 11:44:00", 62, 1255256, 0, 0, 5, 1, FINISHED, true));
        expectedMatches.add(createMatch(771921852, 669787, "FC Ruja", 33756, "Railay Beach-Boys", "2026-10-21 19:20:00", 9, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(42301908, 2316933, "Leyre1998", 669787, "FC Ruja", "2026-10-22 08:32:00", 50, 7146394, 0, 0, 0, 5, FINISHED, true));
        expectedMatches.add(createMatch(42334666, 669787, "FC Ruja", 1901514, "Chelsea Addict", "2026-10-22 08:37:00", 62, 1258033, 0, 0, 2, 1, FINISHED, true));
        expectedMatches.add(createMatch(42334688, 669787, "FC Ruja", 793659, "Purpleway", "2026-10-22 08:46:00", 62, 3140761, 0, 0, 5, 0, FINISHED, true));
        expectedMatches.add(createMatch(42334742, 669787, "FC Ruja", 939789, "Hiro_BG-san", "2026-10-22 08:49:00", 62, 3140692, 0, 0, 11, 12, FINISHED, true));
        expectedMatches.add(createMatch(42301822, 669787, "FC Ruja", 2351824, "Real Sociedad de San Cristóbal", "2026-10-23 08:32:00", 50, 7146394, 0, 0, 2, 0, FINISHED, true));
        expectedMatches.add(createMatch(42301917, 3234951, "the roiters", 669787, "FC Ruja", "2026-10-24 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42342803, 669787, "FC Ruja", 1602482, "Erbil Sport Club", "2026-10-24 10:13:00", 62, 1258033, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(768137632, 669787, "FC Ruja", 482976, "-=TXT=-", "2026-10-24 15:20:00", 1, 0, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301835, 669787, "FC Ruja", 3225770, "los tronchamuros", "2026-10-25 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301838, 52030, "Berliner Elbkicker", 669787, "FC Ruja", "2026-10-28 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(771989634, 669787, "FC Ruja", 2622805, "FCPakistan", "2026-10-28 19:20:00", 9, 0, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301842, 669787, "FC Ruja", 52030, "Berliner Elbkicker", "2026-10-29 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301851, 3225770, "los tronchamuros", 669787, "FC Ruja", "2026-10-30 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301857, 669787, "FC Ruja", 3234951, "the roiters", "2026-10-31 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301864, 2351824, "Real Sociedad de San Cristóbal", 669787, "FC Ruja", "2026-11-01 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301868, 669787, "FC Ruja", 2316933, "Leyre1998", "2026-11-04 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301876, 1063974, "CSM Victoria Carei", 669787, "FC Ruja", "2026-11-05 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301877, 2330514, "RCD edps", 669787, "FC Ruja", "2026-11-06 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301889, 669787, "FC Ruja", 2268079, "FC ShaChri", "2026-11-07 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301891, 649745, "Die Op!umbrigade", 669787, "FC Ruja", "2026-11-08 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301900, 669787, "FC Ruja", 3463215, "Fc Bussikuskit", "2026-11-11 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));
        expectedMatches.add(createMatch(42301904, 223280, "Fighting Donkey's FC", 669787, "FC Ruja", "2026-11-12 08:32:00", 50, 7146394, 0, 0, -1, -1, UPCOMING, false));

        return expectedMatches;
    }

    private static Stream<Arguments> test_parseMatchesFromString() {
        return Stream.of(
            arguments("chpp/matches/matches.xml", createExpectedMatches()),
            arguments("chpp/matches/matchesWithTournament.xml", createExpectedMatchesWithTournament()),
            arguments("chpp/matches/matchesWithCup.xml", createExpectedMatchesWithCup()),
            arguments("chpp/matches/matchesWithRubinCup.xml", createExpectedMatchesWithRubinCup())
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_parseMatchesFromString(String xmlFilePath, ArrayList<MatchKurzInfo> expectedMatches) throws IOException {
        var xmlFileMatches = ResourceUtils.getResourceFileAsString(xmlFilePath);

        // when
        final var matches = XMLMatchesParser.parseMatchesFromString(xmlFileMatches);
        assertThat(matches).isNotNull();
        assertThat(matches.size()).isEqualTo(expectedMatches.size());
        for (int i = 0; i < matches.size(); i++) {
            var expectedMatch = expectedMatches.get(i);
            var actualMatch = matches.get(i);

            HOLogger.instance().debug(this.getClass(), String.format("Test MatchID %d", actualMatch.getMatchID()));
            assertThat(actualMatch.getMatchID()).isEqualTo(expectedMatch.getMatchID());
            assertThat(actualMatch.getHomeTeamID()).isEqualTo(expectedMatch.getHomeTeamID());
            assertThat(actualMatch.getHomeTeamName()).isEqualTo(expectedMatch.getHomeTeamName());
            assertThat(actualMatch.getGuestTeamID()).isEqualTo(expectedMatch.getGuestTeamID());
            assertThat(actualMatch.getGuestTeamName()).isEqualTo(expectedMatch.getGuestTeamName());
            assertThat(actualMatch.getMatchSchedule()).isEqualTo(expectedMatch.getMatchSchedule());
            assertThat(actualMatch.getMatchType()).isEqualTo(expectedMatch.getMatchType());
            assertThat(actualMatch.getMatchContextId()).isEqualTo(expectedMatch.getMatchContextId());
            assertThat(actualMatch.getCupLevel()).isEqualTo(expectedMatch.getCupLevel());
            assertThat(actualMatch.getCupLevelIndex()).isEqualTo(expectedMatch.getCupLevelIndex());
            assertThat(actualMatch.getHomeTeamGoals()).isEqualTo(expectedMatch.getHomeTeamGoals());
            assertThat(actualMatch.getGuestTeamGoals()).isEqualTo(expectedMatch.getGuestTeamGoals());
            assertThat(actualMatch.getMatchStatus()).isEqualTo(expectedMatch.getMatchStatus());
            assertThat(actualMatch.isOrdersGiven()).isEqualTo(expectedMatch.isOrdersGiven());
        }
    }
}
