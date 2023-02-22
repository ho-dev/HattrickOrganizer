// %4118680904:hoplugins.teamAnalyzer.ui.lineup%
package module.teamAnalyzer.ui.lineup;

import java.awt.*;
import java.io.Serial;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * An abstract Lineup jpanel
 * Jan 2011 - Modified by blaghaid for 553 changes
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public abstract class LineupStylePanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = 6857727877436754893L;

	/** The main formation panel */
    protected FormationPanel mainPanel;

    /** The main panel */
    protected JPanel centerPanel = new javax.swing.JPanel();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructor
     *
     * @param _mainPanel the main formation panel
     */
    public LineupStylePanel(FormationPanel _mainPanel) {
        super();
        mainPanel = _mainPanel;

//        constraints.insets = new Insets(5, 5, 5, 5);
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.weightx = 1;

    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Sets the layout for 2 teams  displayed
     */
    public abstract void initCompare();

    /**
     * Sets the layout for only 1 team displayed
     */
    public abstract void initSingle();

    /**
     * Sets the rating bar in the proper line on the grid bag grid
     *
     */
    protected void setMidfieldRatingBar() {
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
        centerPanel.add(mainPanel.midfield);
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setMyAttack() {
        JPanel m_clLinkerSturm;
        JPanel m_clRechterSturm;
        JPanel m_clCentralSturm;
        m_clLinkerSturm = mainPanel.getMyTeam().getLeftForwardPanel();
        m_clLinkerSturm.setOpaque(false);
        centerPanel.add(new JLabel());
        centerPanel.add(m_clLinkerSturm);
        m_clCentralSturm = mainPanel.getMyTeam().getCentralForwardPanel();
        m_clCentralSturm.setOpaque(false);
        centerPanel.add(m_clCentralSturm);
        m_clRechterSturm = mainPanel.getMyTeam().getRightForwardPanel();
        m_clRechterSturm.setOpaque(false);
        centerPanel.add(m_clRechterSturm);
        centerPanel.add(new JLabel());
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setMyDefence() {
        JPanel m_clLinkeAussenVerteidiger = mainPanel.getMyTeam().getLeftWingbackPanel();
        m_clLinkeAussenVerteidiger.setOpaque(false);
        centerPanel.add(m_clLinkeAussenVerteidiger);

        JPanel m_clLinkeInnenVerteidiger = mainPanel.getMyTeam().getLeftCentralDefenderPanel();
        m_clLinkeInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clLinkeInnenVerteidiger);

        JPanel m_clMiddleInnenVerteidiger = mainPanel.getMyTeam().getMiddleCentralDefenderPanel();
        m_clMiddleInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clMiddleInnenVerteidiger);

        JPanel m_clRechteInnenVerteidiger = mainPanel.getMyTeam().getRightCentralDefenderPanel();
        m_clRechteInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clRechteInnenVerteidiger);

        JPanel m_clRechteAussenVerteidiger = mainPanel.getMyTeam().getRightWingbackPanel();
        m_clRechteAussenVerteidiger.setOpaque(false);
        centerPanel.add(m_clRechteAussenVerteidiger);
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setMyKeeper() {
    	JPanel m_clTorwart;
        m_clTorwart = mainPanel.getMyTeam().getKeeperPanel();
        m_clTorwart.setOpaque(false);
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
        centerPanel.add(m_clTorwart);
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setMyMidfield() {
    	JPanel m_clRechteFluegel;
    	JPanel m_clRechteMittelfeld;
    	JPanel m_clCentralMittelfeld;
    	JPanel m_clLinkeMittelfeld;
    	JPanel m_clLinkeFluegel;

        m_clLinkeFluegel = mainPanel.getMyTeam().getLeftWingPanel();
        m_clLinkeFluegel.setOpaque(false);
        centerPanel.add(m_clLinkeFluegel);
        m_clLinkeMittelfeld = mainPanel.getMyTeam().getLeftMidfieldPanel();
        m_clLinkeMittelfeld.setOpaque(false);
        centerPanel.add(m_clLinkeMittelfeld);
        m_clCentralMittelfeld = mainPanel.getMyTeam().getCentralMidfieldPanel();
        m_clCentralMittelfeld.setOpaque(false);
        centerPanel.add(m_clCentralMittelfeld);
        m_clRechteMittelfeld = mainPanel.getMyTeam().getRightMidfieldPanel();
        m_clRechteMittelfeld.setOpaque(false);
        centerPanel.add(m_clRechteMittelfeld);
        m_clRechteFluegel = mainPanel.getMyTeam().getRightWingPanel();
        m_clRechteFluegel.setOpaque(false);
        centerPanel.add(m_clRechteFluegel);
    }

    /**
     * Sets the user team title panel on the grid
     *
     */
    protected JLabel getOwnTeamNamePanel() {
        JLabel m_jlTeamName;
        m_jlTeamName = mainPanel.getMyTeam().getTeamPanel();
        m_jlTeamName.setOpaque(false);
        m_jlTeamName.setForeground(Color.white);
        m_jlTeamName.setFont(m_jlTeamName.getFont().deriveFont(Font.BOLD,
                                                               core.model.UserParameter.instance().fontSize
                                                               + 3));
        return m_jlTeamName;
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setOpponentAttack() {
        centerPanel.add(new JLabel());
        JPanel m_clRechterSturm = mainPanel.getOpponentTeam().getRightForwardPanel();
        m_clRechterSturm.setOpaque(false);
        centerPanel.add(m_clRechterSturm);
        JPanel m_clCentralSturm = mainPanel.getOpponentTeam().getCentralForwardPanel();
        m_clCentralSturm.setOpaque(false);
        centerPanel.add(m_clCentralSturm);
        JPanel m_clLinkerSturm = mainPanel.getOpponentTeam().getLeftForwardPanel();
        m_clLinkerSturm.setOpaque(false);
        centerPanel.add(m_clLinkerSturm);
        centerPanel.add(new JLabel());
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setOpponentDefence() {
        JPanel m_clRechteAussenVerteidiger = mainPanel.getOpponentTeam().getRightWingbackPanel();
        m_clRechteAussenVerteidiger.setOpaque(false);
        centerPanel.add(m_clRechteAussenVerteidiger);
        JPanel m_clRechteInnenVerteidiger = mainPanel.getOpponentTeam().getRightCentralDefenderPanel();
        m_clRechteInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clRechteInnenVerteidiger);
        JPanel m_clCentralInnenVerteidiger = mainPanel.getOpponentTeam().getMiddleCentralDefenderPanel();
        m_clCentralInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clCentralInnenVerteidiger);
        JPanel m_clLinkeInnenVerteidiger = mainPanel.getOpponentTeam().getLeftCentralDefenderPanel();
        m_clLinkeInnenVerteidiger.setOpaque(false);
        centerPanel.add(m_clLinkeInnenVerteidiger);
        JPanel m_clLinkeAussenVerteidiger = mainPanel.getOpponentTeam().getLeftWingbackPanel();
        m_clLinkeAussenVerteidiger.setOpaque(false);
        centerPanel.add(m_clLinkeAussenVerteidiger);
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setOpponentKeeper() {
        JPanel m_clTorwart = mainPanel.getOpponentTeam().getKeeperPanel();
        m_clTorwart.setOpaque(false);
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
        centerPanel.add(m_clTorwart);
        centerPanel.add(new JLabel());
        centerPanel.add(new JLabel());
    }

    /**
     * Sets a lineup line in the proper line on the grid bag grid
     *
     */
    protected void setOpponentMidfield() {
        JPanel m_clRechteFluegel = mainPanel.getOpponentTeam().getRightWingPanel();
        m_clRechteFluegel.setOpaque(false);
        centerPanel.add(m_clRechteFluegel);
        JPanel m_clRechteMittelfeld = mainPanel.getOpponentTeam().getRightMidfieldPanel();
        m_clRechteMittelfeld.setOpaque(false);
        centerPanel.add(m_clRechteMittelfeld);
        JPanel m_clCentralMittelfeld = mainPanel.getOpponentTeam().getCentralMidfieldPanel();
        m_clCentralMittelfeld.setOpaque(false);
        centerPanel.add(m_clCentralMittelfeld);
        JPanel m_clLinkeMittelfeld = mainPanel.getOpponentTeam().getLeftMidfieldPanel();
        m_clLinkeMittelfeld.setOpaque(false);
        centerPanel.add(m_clLinkeMittelfeld);
        JPanel m_clLinkeFluegel = mainPanel.getOpponentTeam().getLeftWingPanel();
        m_clLinkeFluegel.setOpaque(false);
        centerPanel.add(m_clLinkeFluegel);
    }

    /**
     * Sets the opponent team title panel on the grid
     *
     */
    protected JLabel getOpponentTeamNamePanel() {
        JLabel m_jlTeamName = mainPanel.getOpponentTeam().getTeamPanel();
        m_jlTeamName.setOpaque(false);
        m_jlTeamName.setForeground(Color.white);
        m_jlTeamName.setFont(m_jlTeamName.getFont().deriveFont(Font.BOLD,
                                                               core.model.UserParameter.instance().fontSize
                                                               + 3));
        return m_jlTeamName;
    }

    /**
     * Sets the rating bar in the proper line on the grid bag grid
     *
     */
    protected void setRatingBar1() {
        centerPanel.add(new JLabel());
        centerPanel.add(mainPanel.rightDef);
        centerPanel.add(mainPanel.midDef);
        centerPanel.add(mainPanel.leftDef);
        centerPanel.add(new JLabel());
    }

    /**
     * Sets the rating bar in the proper line on the grid bag grid
     *
     */
    protected void setRatingBar2() {
        centerPanel.add(new JLabel());
        centerPanel.add(mainPanel.rightAtt);
        centerPanel.add(mainPanel.midAtt);
        centerPanel.add(mainPanel.leftAtt);
        centerPanel.add(new JLabel());
    }
}
