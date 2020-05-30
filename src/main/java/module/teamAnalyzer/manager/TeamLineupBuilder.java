package module.teamAnalyzer.manager;

import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.prediction.engine.TeamData;
import core.prediction.engine.TeamRatings;
import module.teamAnalyzer.report.PositionReport;
import module.teamAnalyzer.report.SpotReport;
import module.teamAnalyzer.report.TacticReport;
import module.teamAnalyzer.report.TeamReport;
import module.teamAnalyzer.vo.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class TeamLineupBuilder {
    //~ Instance fields ----------------------------------------------------------------------------

    private TeamLineup teamLineup = new TeamLineup();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamLineupBuilder object.
     */
    public TeamLineupBuilder(TeamReport teamReport) {
        teamLineup.setRating(teamReport.getRating());
        teamLineup.setStars(teamReport.getStars());
        teamLineup.setSpecialEventsPrediction(teamReport.getSpecialEventsPredictionManager());

        for (int spot = IMatchRoleID.startLineup; spot < IMatchRoleID.startReserves; spot++) {
        	
            SpotReport spotReport = teamReport.getSpotReport(spot);

            if (spotReport != null) {
                SpotLineup spotLineup = buildSpotLineup(spotReport);

                teamLineup.setSpotLineup(spotLineup, spot);
            }
        }
    }

    public TeamLineupBuilder setMatchDetail(MatchDetail m){
        this.teamLineup.setMatchDetail(m);
        return this;
    }

    public TeamLineupBuilder setName(String name){
        this.teamLineup.setName(name);
        return this;
    }

    public TeamLineupBuilder setTeamData(TeamData teamData) {
        MatchRating rating = new MatchRating();
        TeamRatings tr = teamData.getRatings();
        rating.setCentralAttack(tr.getMiddleAttack());
        rating.setCentralDefense(tr.getMiddleDef());
        rating.setLeftAttack(tr.getLeftAttack());
        rating.setLeftDefense(tr.getLeftDef());
        rating.setMidfield(tr.getMidfield());
        rating.setRightAttack(tr.getRightAttack());
        rating.setRightDefense(tr.getRightDef());
        rating.setHatStats(rating.computeHatStats());
        rating.setLoddarStats(rating.computeLoddarStats());
        this.teamLineup.setRating(rating);
        this.teamLineup.setAdjustedTacticCode(teamData.getTacticType());
        this.teamLineup.setAdjustedTacticLevel(teamData.getTacticLevel());
        return this;
    }

    //~ Methods ------------------------------------------------------------------------------------
    public TeamLineup build() {
        return teamLineup;
    }

    private Collection<TacticReport> getAllTactics(Collection<PositionReport> positions) {
        Collection<TacticReport> tactics = new ArrayList<TacticReport>();

        for (Iterator<PositionReport> iter = positions.iterator(); iter.hasNext();) {
            PositionReport positionReport = iter.next();

            tactics.addAll(positionReport.getTacticReports());
        }

        return tactics;
    }

    private PlayerAppearance getPlayer(Collection<PlayerAppearance> collection) {
        PlayerAppearance[] appearances = getSortedAppearance(collection);

        if (appearances.length == 1) {
            return appearances[0];
        }

        if (appearances[0].getAppearance() > appearances[1].getAppearance()) {
            return appearances[0];
        }

        PlayerAppearance app = new PlayerAppearance();

        if ((appearances.length > 2)
            && (appearances[2].getAppearance() == appearances[0].getAppearance())) {
            app.setName(HOVerwaltung.instance().getLanguageString("TeamLineupBuilder.Unknown")); //$NON-NLS-1$
        } else {
            //			String status1 = (appearances[0].getStatus()!=PlayerManager.AVAILABLE)? "*":"";			
            //			String status2 = (appearances[1].getStatus()!=PlayerManager.AVAILABLE)? "*":"";
            app.setName(appearances[0].getName() + "/" + appearances[1].getName());
        }

        app.setApperarence(appearances[0].getAppearance());

        return app;
    }

    private int getPosition(TacticReport[] tacticsReport) {
        if (isSingle(tacticsReport)) {
            return tacticsReport[0].getTacticCode();
        }

        if (tacticsReport[0].getPosition() == tacticsReport[1].getPosition()) {
            return tacticsReport[0].getTacticCode();
        }

        return -1;
    }

    private boolean isSingle(TacticReport[] tacticsReport) {
        if (tacticsReport.length == 1) {
            return true;
        }

        if (tacticsReport[0].getAppearance() > tacticsReport[1].getAppearance()) {
            return true;
        }

        return false;
    }

    private PlayerAppearance[] getSortedAppearance(Collection<PlayerAppearance> appearance) {
        SortedSet<PlayerAppearance> sorted = getSortedSet(appearance, new AppearanceComparator());
        int size = sorted.size();
        PlayerAppearance[] array = new PlayerAppearance[size];
        int i = 0;

        for (Iterator<PlayerAppearance> iter = sorted.iterator(); iter.hasNext();) {
            PlayerAppearance element = iter.next();

            array[i] = element;
            i++;
        }

        return array;
    }

    private static<T> SortedSet<T> getSortedSet(Collection<T> beans, Comparator<T> comparator) {
        final SortedSet<T> set = new TreeSet<T>(comparator);

        if ((beans != null) && (beans.size() > 0)) {
            set.addAll(beans);
        }

        return set;
    }
    private TacticReport[] getSortedTactics(Collection<TacticReport> tactics) {
        SortedSet<TacticReport> sorted = getSortedSet(tactics, new PerformanceComparator());
        int size = sorted.size();
        TacticReport[] tacticsReport = new TacticReport[size];
        int i = 0;

        for (Iterator<TacticReport> iter = sorted.iterator(); iter.hasNext();) {
            TacticReport element = iter.next();

            tacticsReport[i] = element;
            i++;
        }

        return tacticsReport;
    }

    private SpotLineup buildSpotLineup(SpotReport spotReport) {
        SpotLineup spotLineup = new SpotLineup(spotReport);

        spotLineup.setSpot(spotReport.getSpot());

        PlayerAppearance appearance = getPlayer(spotReport.getPlayerAppearance());

        spotLineup.setName(appearance.getName());
        spotLineup.setPlayerId(appearance.getPlayerId());
        spotLineup.setAppearance(appearance.getAppearance());
        spotLineup.setStatus(appearance.getStatus());

        Collection<TacticReport> tacticsReports = getAllTactics(spotReport.getPositionReports());
        TacticReport[] tacticsReport = getSortedTactics(tacticsReports);

        spotLineup.setTactics(Arrays.asList(tacticsReport));
        spotLineup.setPosition(getPosition(tacticsReport));

        return spotLineup;
    }

}
