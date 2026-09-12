package module.series;

import core.file.xml.TeamStats;
import core.model.series.LigaTabelle;
import core.model.series.Paarung;
import core.util.HODateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

class MatchFixturesTest {

    @Test
    void matchFixturesReplaceTeamsAfterRound1Test() {
        for (int replaceTeams = 1; replaceTeams <= 8; replaceTeams++) {
            var fixtures = createFixtures();

            // Set replacements
            for (int i = 0; i < replaceTeams; ++i) {
                var replacedTeamId = 9 + i;
                var fixture = fixtures.get(i / 2);
                if (i % 2 == 0) {
                    fixture.setHeimId(replacedTeamId); // replaced Team is replaced by pair.getHeimId after round 1
                } else {
                    fixture.setGastId(replacedTeamId); // replaced Team is replaced by pair.getGatd after round 1
                }
            }

            // Set match results of round 1
            for (int i = 0; i < 4; i++) {
                // All matches in round 1 ended 1-0
                var fixture = fixtures.get(i);
                fixture.setToreHeim(1);
                fixture.setToreGast(0);
            }

            // Calculate series table
            var matchFixtures = new MatchFixtures();
            matchFixtures.addFixtures(shuffleFixtures(fixtures));
            var table = matchFixtures.getTable();
            Assertions.assertNotNull(table);

            // Compare with expected table
            var expectedTable = List.of(
                List.of(1, 1, 1, 0, 3),
                List.of(3, 1, 1, 0, 3),
                List.of(5, 1, 1, 0, 3),
                List.of(7, 1, 1, 0, 3),
                List.of(2, 1, 0, 1, 0),
                List.of(4, 1, 0, 1, 0),
                List.of(6, 1, 0, 1, 0),
                List.of(8, 1, 0, 1, 0)
            );
            assertTableEquals(expectedTable, table);
        }
    }

    /**
     * Fixture list order created by Hattrick does not correspond to the order given in MatchFixtures.fixtureEntryIndices.
     * To reflect this behavior, we shuffle the order of our list.
     * @return List of Paarung
     */
    private static List<Paarung> shuffleFixtures(List<Paarung> fixtures) {
        var ret = new ArrayList<Paarung>();
        var round = new ArrayList<Paarung>();
        for (var pair : fixtures) {
            round.add(pair);
            if (round.size() == 4) {
                Collections.shuffle(round);
                ret.addAll(round);
                round.clear();
            }
        }
        return ret;
    }

    private static List<Paarung> createFixtures() {
        var teams = create8Teams();
        return MatchFixtures.createFixtures(HODateTime.fromHTWeek(new HODateTime.HTWeek(89, 1)), teams);
    }

    private static void assertTableEquals(List<List<Integer>> expectedTable, LigaTabelle table) {
        int i = 0;
        for (var expected: expectedTable){
            var tableEntry = table.getEntries().get(i++);
            Assertions.assertEquals(expected.get(0), tableEntry.getTeamId());
            Assertions.assertEquals(expected.get(1), tableEntry.getAnzSpiele());
            Assertions.assertEquals(expected.get(2), tableEntry.getGoalsFor());
            Assertions.assertEquals(expected.get(3), tableEntry.getGoalsAgainst());
            Assertions.assertEquals(expected.get(4), tableEntry.getPoints());
        }
    }

    @Test
    void matchFixturesReplaceTwoTeamsAfterRound2Test() {
        var fixtures = createFixtures();
        // Team9 is replaced by Team1 after round 2
        fixtures.get(0).setHeimId(9);
        fixtures.get(4).setGastId(9);
        // Team10 is replaced by Team2 after round 1
        fixtures.get(0).setGastId(10);
        fixtures.get(5).setHeimId(10);

        for (int i = 0; i < 8; i++) {
            // All matches in round 1 and 2 ended 1-0
            var pair = fixtures.get(i);
            pair.setToreHeim(1);
            pair.setToreGast(0);
        }
        var matchFixtures = new MatchFixtures();
        matchFixtures.addFixtures(shuffleFixtures(fixtures));
        var table = matchFixtures.getTable();
        Assertions.assertNotNull(table);

        // Compare with expected table
        var expectedTable = List.of(
            List.of(1, 2, 1, 1, 3),
            List.of(2, 2, 1, 1, 3),
            List.of(3, 2, 1, 1, 3),
            List.of(4, 2, 1, 1, 3),
            List.of(5, 2, 1, 1, 3),
            List.of(6, 2, 1, 1, 3),
            List.of(7, 2, 1, 1, 3),
            List.of(8, 2, 1, 1, 3)
        );
        assertTableEquals(expectedTable, table);
    }

    @Test
    void matchFixturesReplaceTeamEnteredInRound2() {
        var fixtures = createFixtures();

        // Team9 is replaced by Team10 after round 1
        fixtures.get(0).setHeimId(9); // 1
        fixtures.get(4).setGastId(10); // 2
        fixtures.get(8).setHeimId(10);  // 3
        fixtures.get(12).setGastId(10); // 4
        fixtures.get(16).setHeimId(10); // 5
        fixtures.get(20).setGastId(10); // 6
        fixtures.get(24).setHeimId(10); // 7
        fixtures.get(28).setGastId(10); // Team10 is replaced by Team1 after round 8

        for (int i = 0; i < 14*4; i++) {
            // All matches ended 1-0
            var pair = fixtures.get(i);
            pair.setToreHeim(1);
            pair.setToreGast(0);
        }
        var matchFixtures = new MatchFixtures();
        matchFixtures.addFixtures(shuffleFixtures(fixtures));
        var table = matchFixtures.getTable();
        Assertions.assertNotNull(table);

        // Compare with expected table
        var expectedTable = List.of(
            List.of(1, 14, 7, 7, 21),
            List.of(2, 14, 7, 7, 21),
            List.of(3, 14, 7, 7, 21),
            List.of(4, 14, 7, 7, 21),
            List.of(5, 14, 7, 7, 21),
            List.of(6, 14, 7, 7, 21),
            List.of(7, 14, 7, 7, 21),
            List.of(8, 14, 7, 7, 21)
        );
        assertTableEquals(expectedTable, table);
    }

    private static List<TeamStats> create8Teams() {
        return IntStream.rangeClosed(1, 8).mapToObj(teamId -> {
                var teamStats = new TeamStats();
                teamStats.setTeamId(teamId);
                return teamStats;
            }
        ).toList();
    }

    @Test
    void createSeriesFixtures() {
        var teams = create8Teams();
        var fixtures = MatchFixtures.createFixtures(HODateTime.fromHTWeek(new HODateTime.HTWeek(89, 1)), teams);

        /*
        Spielplan
            Woche 1	1 - 2	3 - 4	5 - 6	7 - 8
            Woche 2	4 - 1	2 - 7	6 - 3	8 - 5
            Woche 3	1 - 8	3 - 5	4 - 2	7 - 6
            Woche 4	6 - 1	2 - 3	5 - 7	8 - 4
            Woche 5	1 - 7	4 - 5	3 - 8	2 - 6
            Woche 6	5 - 1	7 - 3	6 - 4	8 - 2
            Woche 7	1 - 3	2 - 5	4 - 7	6 - 8
            Woche 8	3 - 1	5 - 2	7 - 4	8 - 6
            Woche 9	1 - 5	3 - 7	4 - 6	2 - 8
            Woche 10	7 - 1	5 - 4	8 - 3	6 - 2
            Woche 11	1 - 6	3 - 2	7 - 5	4 - 8
            Woche 12	8 - 1	5 - 3	2 - 4	6 - 7
            Woche 13	1 - 4	7 - 2	3 - 6	5 - 8
            Woche 14	2 - 1	4 - 3	6 - 5	8 - 7
         */

        Assertions.assertEquals(14 * 4, fixtures.size());
        Assertions.assertTrue(containsFixtures(fixtures, 1, 1, 2));
        Assertions.assertTrue(containsFixtures(fixtures, 1, 3, 4));
        Assertions.assertTrue(containsFixtures(fixtures, 1, 5, 6));
        Assertions.assertTrue(containsFixtures(fixtures, 1, 7, 8));
        Assertions.assertTrue(containsFixtures(fixtures, 2, 4, 1));
        Assertions.assertTrue(containsFixtures(fixtures, 2, 2, 7));
        Assertions.assertTrue(containsFixtures(fixtures, 2, 6, 3));
        Assertions.assertTrue(containsFixtures(fixtures, 2, 8, 5));
        Assertions.assertTrue(containsFixtures(fixtures, 3, 1, 8));
        Assertions.assertTrue(containsFixtures(fixtures, 3, 3, 5));
        Assertions.assertTrue(containsFixtures(fixtures, 3, 4, 2));
        Assertions.assertTrue(containsFixtures(fixtures, 3, 7, 6));
        Assertions.assertTrue(containsFixtures(fixtures, 4, 6, 1));
        Assertions.assertTrue(containsFixtures(fixtures, 4, 2, 3));
        Assertions.assertTrue(containsFixtures(fixtures, 4, 5, 7));
        Assertions.assertTrue(containsFixtures(fixtures, 4, 8, 4));
        Assertions.assertTrue(containsFixtures(fixtures, 5, 1, 7));
        Assertions.assertTrue(containsFixtures(fixtures, 5, 4, 5));
        Assertions.assertTrue(containsFixtures(fixtures, 5, 3, 8));
        Assertions.assertTrue(containsFixtures(fixtures, 5, 2, 6));
        Assertions.assertTrue(containsFixtures(fixtures, 6, 5, 1));
        Assertions.assertTrue(containsFixtures(fixtures, 6, 7, 3));
        Assertions.assertTrue(containsFixtures(fixtures, 6, 6, 4));
        Assertions.assertTrue(containsFixtures(fixtures, 6, 8, 2));
        Assertions.assertTrue(containsFixtures(fixtures, 7, 1, 3));
        Assertions.assertTrue(containsFixtures(fixtures, 7, 2, 5));
        Assertions.assertTrue(containsFixtures(fixtures, 7, 4, 7));
        Assertions.assertTrue(containsFixtures(fixtures, 7, 6, 8));

    }

    private static boolean containsFixtures(List<Paarung> fixtures, int matchDay, int homeTeamId, int guestTeamId) {
        return fixtures.stream().anyMatch(m->m.getSpieltag()==matchDay && m.getHeimId()==homeTeamId && m.getGastId()== guestTeamId) &&
                fixtures.stream().anyMatch(m->m.getSpieltag()==15-matchDay && m.getHeimId()==guestTeamId && m.getGastId() == homeTeamId);
    }

}
