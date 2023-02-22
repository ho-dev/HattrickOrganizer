// %2408129540:hoplugins.teamAnalyzer.ui.lineup%
package module.teamAnalyzer.ui.lineup;

import java.awt.*;
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
        centerPanel.setLayout(new GridLayout(8,5));

        setOpponentKeeper();
        setOpponentDefence();
        setOpponentMidfield();
        setOpponentAttack();
        setMyAttack();
        setMyMidfield();
        setMyDefence();
        setMyKeeper();

        add(getOpponentTeamNamePanel(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(getOwnTeamNamePanel(), BorderLayout.SOUTH);
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
        centerPanel.setLayout(new GridLayout(4, 5));

        setOpponentKeeper();
        setOpponentDefence();
        setOpponentMidfield();
        setOpponentAttack();

        add(getOpponentTeamNamePanel(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
