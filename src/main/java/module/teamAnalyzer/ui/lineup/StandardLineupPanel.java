// %2408129540:hoplugins.teamAnalyzer.ui.lineup%
package module.teamAnalyzer.ui.lineup;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.Serial;


/**
 * This is an empty panel to display a lineup with standard style
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class StandardLineupPanel extends LineupStylePanel {

	@Serial
    private static final long serialVersionUID = -4628631085016401394L;

	/**
     * Constructor
     *
     * @param _mainPanel Sequence
     */
    public StandardLineupPanel(FormationPanel _mainPanel) {
        super(_mainPanel);
    }

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
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(5, 5, 5, 5);
        centerPanel.setLayout(layout);

        setOpponentPanel(0);
        setOpponentKeeper(1);
        setOpponentDefence(2);
        setOpponentMidfield(3);
        setOpponentAttack(4);
        setMyAttack(5);
        setMyMidfield(6);
        setMyDefence(7);
        setMyKeeper(8);
        setMyPanel(9);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Setup the layout, with only one team displayed
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
