package module.teamAnalyzer.ui.lineup;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RatingUtil;

import java.awt.Color;
import java.awt.Font;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

class RatingBox extends JPanel {
	@Serial
    private static final long serialVersionUID = 7739872564097601073L;

    private final JLabel arrow = new JLabel();
    private final JLabel myValue = new JLabel();
    private final JLabel opponentValue = new JLabel();


    RatingBox() {
        super();
        jbInit();
    }

    void reload(int r1, int r2) {
        myValue.setText(RatingUtil.getRating(r1, SystemManager.isNumericRating.isSet(), SystemManager.isDescriptionRating.isSet()));
        opponentValue.setText(RatingUtil.getRating(r2, SystemManager.isNumericRating.isSet(), SystemManager.isDescriptionRating.isSet()));
        arrow.setIcon( ImageUtilities.getImageIcon4Change(r1 - r2,true));
    }

    private void jbInit() {
        JPanel mainPanel = new ImagePanel();

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        mainPanel.add(myValue);
        mainPanel.add(arrow);
        mainPanel.add(opponentValue);
        add(mainPanel);

        final Color foreground = ThemeManager.getColor(HOColorName.LABEL_FG);

        myValue.setForeground(foreground);
        opponentValue.setForeground(foreground);
        setForeground(foreground);
        setOpaque(false);
        setFont(getFont().deriveFont(Font.BOLD, core.model.UserParameter.instance().fontSize + 3));
    }
}
