package module.playeroverview;

import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import core.model.player.Player;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TeamSummaryModelTest {

    @Test
    void computeTeamStatistics() {
        // given
        HOVerwaltung.instance().setModel(createHOModel());
        final var players = List.of(createPlayer(1), createPlayer(2));
        final var expectedTeamStatistics = new TeamSummaryModel.TeamStatistics();
        expectedTeamStatistics.numPlayers = 2;
        expectedTeamStatistics.averageAge = 19.63392857142857;
        expectedTeamStatistics.averageSalary = new AmountOfMoney(BigDecimal.valueOf(150000).setScale(2, RoundingMode.HALF_UP));
        expectedTeamStatistics.totalTsi = 3000;
        expectedTeamStatistics.averageTsi = 1500;
        expectedTeamStatistics.averageStamina = 1.5;
        expectedTeamStatistics.averageForm = 1.5;

        // when
        final var teamStatistics = TeamSummaryModel.computeTeamStatistics(players);

        // then
        assertThat(teamStatistics).isEqualTo(expectedTeamStatistics);
    }

    private static Player createPlayer(int n) {
        Player player = new Player();
        player.setTsi(1000 * n);
        player.setAge(18 + n);
        player.setAgeDays(10 * n);
        player.setWage(new AmountOfMoney(100000L * n));
        player.setStamina(n);
        player.setForm(n);
        return player;
    }

    private static HOModel createHOModel() {
        HOModel hoModel = new HOModel(HODateTime.now());
        hoModel.setBasics(createBasis());
        return hoModel;
    }

    private static Basics createBasis() {
        Basics basics = new Basics();
        basics.setDatum(HODateTime.now());
        return basics;
    }
}
