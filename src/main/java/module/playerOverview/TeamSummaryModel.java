package module.playerOverview;

import core.model.UserParameter;
import core.model.player.Player;
import core.util.AmountOfMoney;

import java.util.List;

public class TeamSummaryModel {

    static class TeamStatistics {
        int numPlayers;
        double averageAge;
        AmountOfMoney averageSalary;
        long totalTsi;
        double averageTsi;
        double averageStamina;
        double averageForm;
    }

    private List<Player> players;
    private List<Player> comparisonPlayers;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setComparisonPlayers(List<Player> comparisonPlayers) {
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
            deltaStats.averageSalary = current.averageSalary.minus(comparison.averageSalary);
            deltaStats.averageStamina = current.averageStamina - comparison.averageStamina;
            deltaStats.averageForm = current.averageForm - comparison.averageForm;
        }

        return deltaStats;
    }

    private TeamStatistics computeTeamStatistics(List<Player> players) {
        TeamStatistics stats = new TeamStatistics();

        stats.numPlayers = players.size();
        stats.totalTsi = players.stream().mapToLong(Player::getTsi).sum();
        stats.averageTsi = players.stream().mapToDouble(Player::getTsi).average().orElse(0.0);
        stats.averageAge = players.stream().mapToDouble(Player::getAlterWithAgeDays).average().orElse(0.0);
        var average = players.stream().mapToDouble(p->p.getWage().toLocale()).average().orElse(0);
        stats.averageSalary = new AmountOfMoney((long)average) ;
        stats.averageStamina = players.stream().mapToDouble(Player::getStamina).average().orElse(0.0);
        stats.averageForm = players.stream().mapToDouble(Player::getForm).average().orElse(0.0);

        return stats;
    }
}
