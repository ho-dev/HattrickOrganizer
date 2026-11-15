package module.youth;

import core.db.AbstractTable;
import core.util.HODateTime;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The youth league (series) of one season: youthleaguedetails 1.1 with its standings, and
 * youthleaguefixtures 1.0 with its matches. Current state, replaced on every download.
 */
@Getter
@Setter
public class YouthLeague extends AbstractTable.Storable {

    private int youthLeagueId;
    private int season;
    private String youthLeagueName;
    /** YouthLeagueType: regional, national or international. */
    private Integer youthLeagueType;
    private Integer lastMatchRound;
    private Integer nrOfTeamsInLeague;
    /** 1.1 */
    private Integer leagueSystemId;

    private List<Team> teams = new ArrayList<>();
    private List<Fixture> fixtures = new ArrayList<>();

    /** One row of the standings. */
    @Getter
    @Setter
    public static class Team extends AbstractTable.Storable {
        private int youthLeagueId;
        private int season;
        private int teamId;
        private String teamName;
        private Integer position;
        /** positionChange */
        private Integer positionChange;
        private Integer matches;
        private Integer goalsFor;
        private Integer goalsAgainst;
        private Integer points;
        private Integer won;
        private Integer draws;
        private Integer lost;
    }

    /** One match of the season. */
    @Getter
    @Setter
    public static class Fixture extends AbstractTable.Storable {
        private int matchId;
        private int youthLeagueId;
        private int season;
        private Integer matchRound;
        /** MatchStatus */
        private String status;
        private Integer homeTeamId;
        private String homeTeamName;
        private Integer awayTeamId;
        private String awayTeamName;
        private HODateTime matchDate;
        /** Sent only for finished matches. */
        private Integer homeGoals;
        private Integer awayGoals;
    }
}
