package core.net;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.DefaultMutableTreeNode;

public class DownloadFilter extends DefaultMutableTreeNode {

    private static final HOVerwaltung hov = HOVerwaltung.instance();
    private final DefaultMutableTreeNode currentMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.currentmatches"));
    private final DefaultMutableTreeNode currentSeriesData = new DefaultMutableTreeNode(TranslationFacility.tr("download.currentseriesdata"));
    private final DefaultMutableTreeNode previousSeriesData = new DefaultMutableTreeNode(TranslationFacility.tr("download.oldseriesdata"));
    private final DefaultMutableTreeNode teamData = new DefaultMutableTreeNode(TranslationFacility.tr("download.teamdata"));
    private final DefaultMutableTreeNode officialMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.OfficialMatches"));
    private final DefaultMutableTreeNode singleMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.SingleMatches"));
    private final DefaultMutableTreeNode ladderMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.LadderMatches"));
    private final DefaultMutableTreeNode tournamentGroupMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.TournamentGroupMatches"));
    private final DefaultMutableTreeNode tournamentPlayoffMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.TournamentPlayoffMatches"));
    private final DefaultMutableTreeNode divisionBattleMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.DivisionBattleMatches"));

    public DownloadFilter() {
        super(TranslationFacility.tr("download.Filter"));

        this.add(currentMatches);
        DefaultMutableTreeNode seriesData = new DefaultMutableTreeNode(TranslationFacility.tr("download.seriesdata"));
        this.add(seriesData);
        this.add(teamData);

        currentMatches.add(officialMatches);
        DefaultMutableTreeNode integratedMatches = new DefaultMutableTreeNode(TranslationFacility.tr("download.IntegratedMatches"));
        currentMatches.add(integratedMatches);

        integratedMatches.add(singleMatches);
        integratedMatches.add(ladderMatches);
        integratedMatches.add(tournamentGroupMatches);
        integratedMatches.add(tournamentPlayoffMatches);
        integratedMatches.add(divisionBattleMatches);

        seriesData.add(currentSeriesData);
        seriesData.add(previousSeriesData);

        fillOldFixturesList(previousSeriesData);
    }

    private void fillOldFixturesList(DefaultMutableTreeNode previousSeriesData) {
        final int currentSeason = hov.getModel().getBasics().getSeason();
        var activationDate = hov.getModel().getBasics().getActivationDate();
        var htWeek = activationDate.toLocaleHTWeek();
        var seasons = DBManager.instance().getAllSpielplaene(false);
        for (int i = currentSeason; i >= htWeek.season; i--) {
            int finalI = i;
            var season = seasons.stream().filter(f -> f.getSaison() == finalI).findFirst();
            var itemText = new StringBuilder(TranslationFacility.tr("Season")).append(" ").append(i);
            season.ifPresent(fixture -> itemText.append(" / ").append(fixture.getLigaName()));
            previousSeriesData.add(new DefaultMutableTreeNode(Pair.of(itemText.toString(), finalI)));
        }
    }

    public DefaultMutableTreeNode getCurrentMatches() {
        return this.currentMatches;
    }

    public DefaultMutableTreeNode getTeamData() {
        return this.teamData;
    }

    public DefaultMutableTreeNode getCurrentSeriesData() { return this.currentSeriesData; }
    public DefaultMutableTreeNode getPreviousSeriesData() { return this.previousSeriesData; }

    public DefaultMutableTreeNode getOfficialMatches() {
        return this.officialMatches;
    }

    public DefaultMutableTreeNode getSingleMatches() {
        return this.singleMatches;
    }

    public DefaultMutableTreeNode getLadderMatches() {
        return this.ladderMatches;
    }

    public DefaultMutableTreeNode getTournamentGroupMatches() {
        return this.tournamentGroupMatches;
    }

    public DefaultMutableTreeNode getTournamentPlayoffMatches() {
        return this.tournamentPlayoffMatches;
    }

    public DefaultMutableTreeNode getDivisionBattleMatches() {
        return this.divisionBattleMatches;
    }
}
