package module.teamAnalyzer.ui.lineup;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ImageUtilities;
import core.module.config.ModuleConfig;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.RatingUtil;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

class RatingBox extends JPanel {
	private static final long serialVersionUID = 7739872564097601073L;

    private JLabel arrow = new JLabel();
    private JLabel myValue = new JLabel();
    private JLabel opponentValue = new JLabel();


    RatingBox() {
        super();
        jbInit();
    }

    void reload(int r1, int r2) {
        myValue.setText(RatingUtil.getRating(r1, ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
        		ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING)));
        opponentValue.setText(RatingUtil.getRating(r2, ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
        		ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING)));
        arrow.setIcon( ImageUtilities.getImageIcon4Veraenderung(r1 - r2,true));
    }

    private void jbInit() {
        JPanel mainPanel = new ImagePanel();

        mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        mainPanel.add(myValue);
        mainPanel.add(arrow);
        mainPanel.add(opponentValue);
        add(mainPanel);

        myValue.setForeground(Color.BLACK);
        opponentValue.setForeground(Color.BLACK);
        setForeground(Color.BLACK);
        setOpaque(false);
        setFont(getFont().deriveFont(Font.BOLD, core.model.UserParameter.instance().schriftGroesse + 3));
    }
}
