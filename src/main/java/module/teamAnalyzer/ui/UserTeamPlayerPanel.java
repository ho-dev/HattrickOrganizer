package module.teamAnalyzer.ui;

import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.model.player.MatchRoleID;
import module.teamAnalyzer.vo.UserTeamSpotLineup;
import java.util.ArrayList;

public class UserTeamPlayerPanel extends PlayerPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    //~ Methods ------------------------------------------------------------------------------------
//    @Override
//    protected Color getBackGround() {
//        return ThemeManager.getColor(HOColorName.PANEL_BG);
//    }


    public void reload(UserTeamSpotLineup lineup) {
        if (lineup != null) {
        	
        	containsPlayer = true;
        	
            nameField.setText(lineup.getName());
            positionImage.setIcon(ImageUtilities.getImage4Position(
                    lineup.getSpot(),
                    (byte) lineup.getTacticCode(),
                    0
            ));
            jlSpecialty.setIcon(ImageUtilities.getLargePlayerSpecialtyIcon(HOIconName.SPECIALTIES[lineup.getSpecialEvent()]));
            positionField.setText(MatchRoleID.getNameForPosition((byte) lineup.getPosition()));
            updateRatingPanel(lineup.getRating());
            tacticPanel.reload(new ArrayList<>());
        } else {
        	containsPlayer = false;
        	
            nameField.setText("");
            positionField.setText("");
            updateRatingPanel(0);
            positionImage.setIcon(ImageUtilities.getImage4Position(0, (byte) 0,0));
            jlSpecialty.setIcon(null);
            tacticPanel.reload(new ArrayList<>());
        }
    }
}
