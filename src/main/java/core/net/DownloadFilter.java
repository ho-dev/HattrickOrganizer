package core.net;

import core.model.HOVerwaltung;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class DownloadFilter extends DefaultMutableTreeNode {

    private static HOVerwaltung hov = HOVerwaltung.instance();
    private DefaultMutableTreeNode dCurrentmatches = new DefaultMutableTreeNode( hov.getLanguageString("download.currentmatches") );
    private DefaultMutableTreeNode dSeriesdata= new DefaultMutableTreeNode( hov.getLanguageString("download.seriesdata") );
    private DefaultMutableTreeNode dTeamdata = new DefaultMutableTreeNode( hov.getLanguageString("download.teamdata") );
    private DefaultMutableTreeNode dOfficialmatches = new DefaultMutableTreeNode("Official Matches");
    private DefaultMutableTreeNode dIntegratedmatches = new DefaultMutableTreeNode("Integrated Matches");

    public DownloadFilter()
    {
        super("DownloadFilter");

        this.add(dCurrentmatches);
        this.add(dSeriesdata);
        this.add(dTeamdata);

        dCurrentmatches.add(dOfficialmatches);
        dCurrentmatches.add(dIntegratedmatches);

        dIntegratedmatches.add(new DefaultMutableTreeNode("Single"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Ladder"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Tournament group"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("Tournament playoff"));
        dIntegratedmatches.add(new DefaultMutableTreeNode("division battle"));
    }

    public DefaultMutableTreeNode getCurrentMatchPath() {
        return this.dCurrentmatches;
    }

    public DefaultMutableTreeNode getTeamDataPath() {
        return this.dTeamdata;
    }

    public DefaultMutableTreeNode getSeriesDataPath() {
        return this.dSeriesdata;
    }
}
