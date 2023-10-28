package core.net;

import core.model.HOVerwaltung;

import javax.swing.tree.DefaultMutableTreeNode;

public class DownloadFilter extends DefaultMutableTreeNode {

    private static HOVerwaltung hov = HOVerwaltung.instance();
    private DefaultMutableTreeNode currentMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.currentmatches"));
    private DefaultMutableTreeNode seriesData = new DefaultMutableTreeNode(hov.getLanguageString("download.seriesdata"));
    private DefaultMutableTreeNode teamData = new DefaultMutableTreeNode(hov.getLanguageString("download.teamdata"));
    private DefaultMutableTreeNode officialMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.OfficialMatches"));
    private DefaultMutableTreeNode integratedMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.IntegratedMatches"));
    private DefaultMutableTreeNode singleMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.SingleMatches"));
    private DefaultMutableTreeNode ladderMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.LadderMatches"));
    private DefaultMutableTreeNode tournamentGroupMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.TournamentGroupMatches"));
    private DefaultMutableTreeNode tournamentPlayoffMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.TournamentPlayoffMatches"));
    private DefaultMutableTreeNode divisionBattleMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.DivisionBattleMatches"));

    public DownloadFilter() {
        super(hov.getLanguageString("download.Filter"));

        this.add(currentMatches);
        this.add(seriesData);
        this.add(teamData);

        currentMatches.add(officialMatches);
        currentMatches.add(integratedMatches);

        integratedMatches.add(singleMatches);
        integratedMatches.add(ladderMatches);
        integratedMatches.add(tournamentGroupMatches);
        integratedMatches.add(tournamentPlayoffMatches);
        integratedMatches.add(divisionBattleMatches);
    }

    public DefaultMutableTreeNode getCurrentMatches() {
        return this.currentMatches;
    }

    public DefaultMutableTreeNode getTeamData() {
        return this.teamData;
    }

    public DefaultMutableTreeNode getSeriesData() {
        return this.seriesData;
    }

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
