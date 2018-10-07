// %815048719:hoplugins.teamAnalyzer.ui.lineup%
package module.teamAnalyzer.ui.lineup;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


/**
 * This is an empty panel to display a lineup in mixed style
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class MixedLineupPanel extends LineupStylePanel {
    //~ Constructors -------------------------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = -5452888995528327059L;

	/**
     * Constructor
     *
     * @param _mainPanel the main formation planel
     */
    public MixedLineupPanel(FormationPanel _mainPanel) {
        super(_mainPanel);
    }

    //~ Methods ------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------

    /**
     * Setup the layout, with 2 teams displayed
     */
    @Override
	public void initCompare() {
        centerPanel.removeAll();
        setLayout(new BorderLayout());
        setOpaque(false);
        centerPanel.setOpaque(false);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, 0, 0, 0);
        centerPanel.setLayout(layout);

        setOpponentPanel(0);

        setOpponentKeeper(1);
        setOpponentDefence(2);
        setRatingBar1(3);
        setMyAttack(4);

        setOpponentMidfield(5);
        setMidfieldRatingBar(6);
        setMyMidfield(7);

        setOpponentAttack(8);
        setRatingBar2(9);
        setMyDefence(10);
        setMyKeeper(11);

        setMyPanel(12);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Setup the layout, with only 1 team displayed
     */
    @Override
	public void initSingle() {
        centerPanel.removeAll();

        setLayout(new BorderLayout());
        setOpaque(false);
        centerPanel.setOpaque(false);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);
        centerPanel.setLayout(layout);

        setOpponentPanel(0);
        setOpponentKeeper(1);
        setOpponentDefence(2);
        setOpponentMidfield(3);
        setOpponentAttack(4);

        add(centerPanel, BorderLayout.CENTER);
    }
}
