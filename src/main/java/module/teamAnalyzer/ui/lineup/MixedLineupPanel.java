// %815048719:hoplugins.teamAnalyzer.ui.lineup%
package module.teamAnalyzer.ui.lineup;

import java.awt.*;
import java.io.Serial;


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
	@Serial
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
        centerPanel.setLayout(new GridLayout(13, 5));

        setOpponentKeeper();
        setOpponentDefence();
        setRatingBar1();
        setMyAttack();
        setOpponentMidfield();
        setMidfieldRatingBar();
        setMyMidfield();
        setOpponentAttack();
        setRatingBar2();
        setMyDefence();
        setMyKeeper();

        add(getOpponentTeamNamePanel(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(getOwnTeamNamePanel(), BorderLayout.SOUTH);
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
        centerPanel.setLayout(new GridLayout(5,5));

        setOpponentKeeper();
        setOpponentDefence();
        setOpponentMidfield();
        setOpponentAttack();

        add(getOpponentTeamNamePanel(), BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
}
