package core.net;

import core.model.HOVerwaltung;

import javax.swing.tree.DefaultMutableTreeNode;

public class DownloadFilter extends DefaultMutableTreeNode {

    private static HOVerwaltung hov = HOVerwaltung.instance();
    private DefaultMutableTreeNode currentMatches = new DefaultMutableTreeNode(hov.getLanguageString("download.currentmatches"));
    private DefaultMutableTreeNode seriesData = new DefaultMutableTreeNode(hov.getLanguageString("download.seriesdata"));
    private DefaultMutableTreeNode teamData = new DefaultMutableTreeNode(hov.getLanguageString("download.teamdata"));
    private DefaultMutableTreeNode officialMatches = new DefaultMutableTreeNode("Official Matches");
    private DefaultMutableTreeNode integratedMatches = new DefaultMutableTreeNode("Integrated Matches");
    private DefaultMutableTreeNode singleMatches = new DefaultMutableTreeNode("Single Matches");
    private DefaultMutableTreeNode ladderMatches = new DefaultMutableTreeNode("Ladder Matches");
    private DefaultMutableTreeNode tournamentGroupMatches = new DefaultMutableTreeNode("Tournament Group Matches");
    private DefaultMutableTreeNode tournamentPlayoffMatches = new DefaultMutableTreeNode("Tournament Playoff Matches");
    private DefaultMutableTreeNode divisionBattleMatches = new DefaultMutableTreeNode("Division Battle Matches");

    public DownloadFilter() {
        super("DownloadFilter");

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
