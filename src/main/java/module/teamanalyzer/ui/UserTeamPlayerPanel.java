package module.teamanalyzer.ui;

import module.teamanalyzer.vo.UserTeamSpotLineup;

public class UserTeamPlayerPanel extends PlayerPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    //~ Methods ------------------------------------------------------------------------------------
//    @Override
//    protected Color getBackGround() {
//        return ThemeManager.getColor(HOColorName.PANEL_BG);
//    }


    public void reload(UserTeamSpotLineup lineup)
    {
        super.reload(lineup, 0, 0);
    }
}
