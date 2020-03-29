package core.net;

import core.model.HOVerwaltung;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class DownloadFilter extends DefaultMutableTreeNode {

    private static HOVerwaltung hov = HOVerwaltung.instance();

    public DownloadFilter()
    {
        super("DownloadFilter");
        DefaultMutableTreeNode dCurrentmatches = new DefaultMutableTreeNode( hov.getLanguageString("download.currentmatches") );
        DefaultMutableTreeNode dSeriesdata= new DefaultMutableTreeNode( hov.getLanguageString("download.seriesdata") );
        DefaultMutableTreeNode dTeamdata = new DefaultMutableTreeNode( hov.getLanguageString("download.teamdata") );

        this.add(dCurrentmatches);
        this.add(dSeriesdata);
        this.add(dTeamdata);

        DefaultMutableTreeNode dOfficialmatches = new DefaultMutableTreeNode("Official Matches");
        DefaultMutableTreeNode dIntegratedmatches = new DefaultMutableTreeNode("Intgrated Matches");
        dCurrentmatches.add(dOfficialmatches);
        dCurrentmatches.add(dIntegratedmatches);

        dIntegratedmatches.add(new DefaultMutableTreeNode("Single"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Ladder"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Tournament group"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Tournament playoff"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("division battle"));
    }
}
