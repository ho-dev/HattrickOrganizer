package module.playerOverview;

import core.model.UserParameter;
import core.model.player.Player;

import java.util.Vector;

public class TeamSummaryModel {

    class TeamStatistics {
        int numPlayers;
        double averageAge;
        double averageSalary;
        long totalTsi;
        double averageTsi;
        double averageStamina;
        double averageForm;
    }

    private Vector<Player> players;
    private Vector<Player> comparisonPlayers;

    public Vector<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Vector<Player> players) {
        this.players = players;
    }

    public void setComparisonPlayers(Vector<Player> comparisonPlayers) {
        this.comparisonPlayers = comparisonPlayers;
    }

    TeamStatistics getTeamStatistics() {
        return computeTeamStatistics(this.players);
    }

    /**
     * Computes the delta between the current team statistics, and the comparison ones.
     *
     * @return TeamStatistics – bean containing the delta for each stat.
     */
    TeamStatistics getComparisonTeamStatistics() {

        TeamStatistics deltaStats = new TeamStatistics();

        if (this.comparisonPlayers != null) {
            TeamStatistics comparison = computeTeamStatistics(this.comparisonPlayers);
            TeamStatistics current = getTeamStatistics();

            deltaStats.numPlayers = current.numPlayers - comparison.numPlayers;
            deltaStats.totalTsi = current.totalTsi - comparison.totalTsi;
            deltaStats.averageTsi = current.averageTsi - comparison.averageTsi;
            deltaStats.averageAge = current.averageAge - comparison.averageAge;
            deltaStats.averageSalary = current.averageSalary - comparison.averageSalary;
            deltaStats.averageStamina = current.averageStamina - comparison.averageStamina;
            deltaStats.averageForm = current.averageForm - comparison.averageForm;
        }

        return deltaStats;
    }

    private TeamStatistics computeTeamStatistics(Vector<Player> players) {
        TeamStatistics stats = new TeamStatistics();

        stats.numPlayers = players.size();
        stats.totalTsi = players.stream().mapToLong(Player::getTSI).sum();
        stats.averageTsi = players.stream().mapToDouble(Player::getTSI).average().orElse(0.0);
        stats.averageAge = players.stream().mapToDouble(Player::getAlterWithAgeDays).average().orElse(0.0);
        stats.averageSalary = players.stream().mapToDouble(Player::getGehalt).average().orElse(0.0) / UserParameter.instance().faktorGeld;
        stats.averageStamina = players.stream().mapToDouble(Player::getKondition).average().orElse(0.0);
        stats.averageForm = players.stream().mapToDouble(Player::getForm).average().orElse(0.0);

        return stats;
    }
}
