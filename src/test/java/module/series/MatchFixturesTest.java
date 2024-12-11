package module.series;

import core.file.xml.TeamStats;
import core.model.series.Paarung;
import core.util.HODateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MatchFixturesTest {

    @Test
    void createSeriesFixtures() {

        var teams = List.of(
                new TeamStats(),
                new TeamStats(),
                new TeamStats(),
                new TeamStats(),
                new TeamStats(),
                new TeamStats(),
                new TeamStats(),
                new TeamStats()
        );

        int i = 1;
        for (var team : teams) {
            team.setTeamId(i);
            ++i;
        }

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
        Assertions.assertTrue(ContainsFixtures(fixtures, 1, 1, 2));
        Assertions.assertTrue(ContainsFixtures(fixtures, 1, 3, 4));
        Assertions.assertTrue(ContainsFixtures(fixtures, 1, 5, 6));
        Assertions.assertTrue(ContainsFixtures(fixtures, 1, 7, 8));
        Assertions.assertTrue(ContainsFixtures(fixtures, 2, 4, 1));
        Assertions.assertTrue(ContainsFixtures(fixtures, 2, 2, 7));
        Assertions.assertTrue(ContainsFixtures(fixtures, 2, 6, 3));
        Assertions.assertTrue(ContainsFixtures(fixtures, 2, 8, 5));
        Assertions.assertTrue(ContainsFixtures(fixtures, 3, 1, 8));
        Assertions.assertTrue(ContainsFixtures(fixtures, 3, 3, 5));
        Assertions.assertTrue(ContainsFixtures(fixtures, 3, 4, 2));
        Assertions.assertTrue(ContainsFixtures(fixtures, 3, 7, 6));
        Assertions.assertTrue(ContainsFixtures(fixtures, 4, 6, 1));
        Assertions.assertTrue(ContainsFixtures(fixtures, 4, 2, 3));
        Assertions.assertTrue(ContainsFixtures(fixtures, 4, 5, 7));
        Assertions.assertTrue(ContainsFixtures(fixtures, 4, 8, 4));
        Assertions.assertTrue(ContainsFixtures(fixtures, 5, 1, 7));
        Assertions.assertTrue(ContainsFixtures(fixtures, 5, 4, 5));
        Assertions.assertTrue(ContainsFixtures(fixtures, 5, 3, 8));
        Assertions.assertTrue(ContainsFixtures(fixtures, 5, 2, 6));
        Assertions.assertTrue(ContainsFixtures(fixtures, 6, 5, 1));
        Assertions.assertTrue(ContainsFixtures(fixtures, 6, 7, 3));
        Assertions.assertTrue(ContainsFixtures(fixtures, 6, 6, 4));
        Assertions.assertTrue(ContainsFixtures(fixtures, 6, 8, 2));
        Assertions.assertTrue(ContainsFixtures(fixtures, 7, 1, 3));
        Assertions.assertTrue(ContainsFixtures(fixtures, 7, 2, 5));
        Assertions.assertTrue(ContainsFixtures(fixtures, 7, 4, 7));
        Assertions.assertTrue(ContainsFixtures(fixtures, 7, 6, 8));

    }
    
    private boolean ContainsFixtures(List<Paarung> fixtures, int matchDay, int home, int away) {
        return fixtures.stream().anyMatch(m->m.getSpieltag()==matchDay && m.getHeimId()==home && m.getGastId()== away) &&
                fixtures.stream().anyMatch(m->m.getSpieltag()==15-matchDay && m.getHeimId()==away && m.getGastId() == home);
    }

}
