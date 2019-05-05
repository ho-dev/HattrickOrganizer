package module.teamAnalyzer.ui;

import core.gui.comp.panel.RasenPanel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.module.config.ModuleConfig;
import module.lineup.Lineup;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.report.TacticReport;
import module.teamAnalyzer.ui.lineup.FormationPanel;
import module.teamAnalyzer.vo.TeamLineup;
import module.teamAnalyzer.vo.UserTeamSpotLineup;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class TeamPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
	private static final long serialVersionUID = -4778556838500877114L;
	private FormationPanel lineupPanel = new FormationPanel();
    private PlayerPanel keeper = new PlayerPanel();
    private PlayerPanel leftAttacker = new PlayerPanel();
    private PlayerPanel leftBack = new PlayerPanel();
    private PlayerPanel leftCentral = new PlayerPanel();
    private PlayerPanel leftMidfielder = new PlayerPanel();
    private PlayerPanel leftWinger = new PlayerPanel();
    private PlayerPanel rightAttacker = new PlayerPanel();
    private PlayerPanel rightBack = new PlayerPanel();
    private PlayerPanel rightCentral = new PlayerPanel();
    private PlayerPanel rightMidfielder = new PlayerPanel();
    private PlayerPanel rightWinger = new PlayerPanel();
    private PlayerPanel middleCentral = new PlayerPanel();
    private PlayerPanel centralMidfielder = new PlayerPanel();
    private PlayerPanel centralAttacker = new PlayerPanel ();
    JPanel grassPanel = new RasenPanel();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamPanel object.
     */
    public TeamPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public TeamLineupData getMyTeamLineupPanel() {
        return lineupPanel.getMyTeam();
    }

    public TeamLineupData getOpponentTeamLineupPanel() {
        return lineupPanel.getOpponentTeam();
    }

    public void jbInit() {
        grassPanel.setLayout(new BorderLayout());
        grassPanel.add(lineupPanel, BorderLayout.CENTER);
        setMyTeam();
        setLayout(new BorderLayout());
        fillPanel(lineupPanel.getOpponentTeam().getKeeperPanel(), keeper);
        fillPanel(lineupPanel.getOpponentTeam().getLeftWingbackPanel(), leftBack);
        fillPanel(lineupPanel.getOpponentTeam().getLeftCentralDefenderPanel(), leftCentral);
        fillPanel(lineupPanel.getOpponentTeam().getRightCentralDefenderPanel(), rightCentral);
        fillPanel(lineupPanel.getOpponentTeam().getRightWingbackPanel(), rightBack);
        fillPanel(lineupPanel.getOpponentTeam().getLeftWingPanel(), leftWinger);
        fillPanel(lineupPanel.getOpponentTeam().getLeftMidfieldPanel(), leftMidfielder);
        fillPanel(lineupPanel.getOpponentTeam().getRightMidfieldPanel(), rightMidfielder);
        fillPanel(lineupPanel.getOpponentTeam().getRightWingPanel(), rightWinger);
        fillPanel(lineupPanel.getOpponentTeam().getLeftForwardPanel(), leftAttacker);
        fillPanel(lineupPanel.getOpponentTeam().getRightForwardPanel(), rightAttacker);
        fillPanel(lineupPanel.getOpponentTeam().getCentralForwardPanel(), centralAttacker);
        fillPanel(lineupPanel.getOpponentTeam().getCentralMidfieldPanel(), centralMidfielder);
        fillPanel(lineupPanel.getOpponentTeam().getMiddleCentralDefenderPanel(), middleCentral);
        

        JScrollPane scrollPane = new JScrollPane(grassPanel);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void reload(TeamLineup lineup, int week, int season) {
        if (lineup != null) {
            lineupPanel.getOpponentTeam().setTeamName(SystemManager.getActiveTeamName() + " ("
                                                      + SystemManager.getActiveTeamId() + ")");
            keeper.reload(lineup.getSpotLineup(IMatchRoleID.keeper), week, season);
            leftBack.reload(lineup.getSpotLineup(IMatchRoleID.leftBack), week, season);
            leftCentral.reload(lineup.getSpotLineup(IMatchRoleID.leftCentralDefender), week, season);
            rightCentral.reload(lineup.getSpotLineup(IMatchRoleID.rightCentralDefender), week, season);
            rightBack.reload(lineup.getSpotLineup(IMatchRoleID.rightBack), week, season);
            leftWinger.reload(lineup.getSpotLineup(IMatchRoleID.leftWinger), week, season);
            leftMidfielder.reload(lineup.getSpotLineup(IMatchRoleID.leftInnerMidfield), week, season);
            rightMidfielder.reload(lineup.getSpotLineup(IMatchRoleID.rightInnerMidfield), week, season);
            rightWinger.reload(lineup.getSpotLineup(IMatchRoleID.rightWinger), week, season);
            leftAttacker.reload(lineup.getSpotLineup(IMatchRoleID.leftForward), week, season);
            rightAttacker.reload(lineup.getSpotLineup(IMatchRoleID.rightForward), week, season);
            centralAttacker.reload(lineup.getSpotLineup(IMatchRoleID.centralForward), week, season);
            centralMidfielder.reload(lineup.getSpotLineup(IMatchRoleID.centralInnerMidfield), week, season);
            middleCentral.reload(lineup.getSpotLineup(IMatchRoleID.middleCentralDefender), week, season);
            
            lineupPanel.getOpponentTeam().setLeftAttack(lineup.getRating().getLeftAttack());
            lineupPanel.getOpponentTeam().setLeftDefence(lineup.getRating().getLeftDefense());
            lineupPanel.getOpponentTeam().setRightAttack(lineup.getRating().getRightAttack());
            lineupPanel.getOpponentTeam().setRightDefence(lineup.getRating().getRightDefense());
            lineupPanel.getOpponentTeam().setMiddleAttack(lineup.getRating().getCentralAttack());
            lineupPanel.getOpponentTeam().setMiddleDefence(lineup.getRating().getCentralDefense());
            lineupPanel.getOpponentTeam().setMidfield(lineup.getRating().getMidfield());

            setMyTeam();
        } else {
            lineupPanel.getOpponentTeam().setTeamName(HOVerwaltung.instance().getLanguageString("TeamPanel.TeamMessage")); //$NON-NLS-1$

            keeper.reload(null, 0, 0);
            leftBack.reload(null, 0, 0);
            leftCentral.reload(null, 0, 0);
            rightCentral.reload(null, 0, 0);
            rightBack.reload(null, 0, 0);
            leftWinger.reload(null, 0, 0);
            leftMidfielder.reload(null, 0, 0);
            rightMidfielder.reload(null, 0, 0);
            rightWinger.reload(null, 0, 0);
            leftAttacker.reload(null, 0, 0);
            rightAttacker.reload(null, 0, 0);
            centralAttacker.reload(null, 0, 0);
            centralMidfielder.reload(null, 0, 0);
            middleCentral.reload(null, 0, 0);
            
            
            lineupPanel.getOpponentTeam().setLeftAttack(0);
            lineupPanel.getOpponentTeam().setLeftDefence(0);
            lineupPanel.getOpponentTeam().setRightAttack(0);
            lineupPanel.getOpponentTeam().setRightDefence(0);
            lineupPanel.getOpponentTeam().setMiddleAttack(0);
            lineupPanel.getOpponentTeam().setMiddleDefence(0);
            lineupPanel.getOpponentTeam().setMidfield(0);
        }
        fillPanel(lineupPanel.getOpponentTeam().getKeeperPanel(), keeper);
        fillPanel(lineupPanel.getOpponentTeam().getLeftWingbackPanel(), leftBack);
        fillPanel(lineupPanel.getOpponentTeam().getLeftCentralDefenderPanel(), leftCentral);
        fillPanel(lineupPanel.getOpponentTeam().getRightCentralDefenderPanel(), rightCentral);
        fillPanel(lineupPanel.getOpponentTeam().getRightWingbackPanel(), rightBack);
        fillPanel(lineupPanel.getOpponentTeam().getLeftWingPanel(), leftWinger);
        fillPanel(lineupPanel.getOpponentTeam().getLeftMidfieldPanel(), leftMidfielder);
        fillPanel(lineupPanel.getOpponentTeam().getRightMidfieldPanel(), rightMidfielder);
        fillPanel(lineupPanel.getOpponentTeam().getRightWingPanel(), rightWinger);
        fillPanel(lineupPanel.getOpponentTeam().getLeftForwardPanel(), leftAttacker);
        fillPanel(lineupPanel.getOpponentTeam().getRightForwardPanel(), rightAttacker);
        fillPanel(lineupPanel.getOpponentTeam().getCentralForwardPanel(), centralAttacker);            
        fillPanel(lineupPanel.getOpponentTeam().getMiddleCentralDefenderPanel(), middleCentral);
        fillPanel(lineupPanel.getOpponentTeam().getCentralMidfieldPanel(), centralMidfielder);

        lineupPanel.reload(ModuleConfig.instance().getBoolean(SystemManager.ISLINEUP),
        		ModuleConfig.instance().getBoolean(SystemManager.ISMIXEDLINEUP));
        grassPanel.repaint();
    }

    private void setMyTeam() {
        //List<UserTeamPlayerPanel> list = new ArrayList<UserTeamPlayerPanel>();
    	HashMap<Integer, UserTeamPlayerPanel> list = new HashMap<Integer, UserTeamPlayerPanel>();
    	Lineup lineup = HOVerwaltung.instance().getModel().getLineup();

        for (int spot = IMatchRoleID.startLineup; spot < IMatchRoleID.startReserves; spot++) {
            Player player = lineup.getPlayerByPositionID(spot);
            UserTeamPlayerPanel pp = new UserTeamPlayerPanel();

            if (player != null) {
                UserTeamSpotLineup spotLineup = new UserTeamSpotLineup();

                spotLineup.setAppearance(0);
                spotLineup.setName(getPlayerName(player.getName()));
                spotLineup.setPlayerId(player.getSpielerID());
                spotLineup.setSpecialEvent(player.getPlayerSpecialty());
                spotLineup.setTacticCode(lineup.getTactic4PositionID(spot));
                spotLineup.setPosition(lineup.getEffectivePos4PositionID(spot));
                spotLineup.setRating(player.calcPosValue(lineup.getEffectivePos4PositionID(spot),
                                                          true));
                if (player.getVerletzt() > 0) {
                	spotLineup.setStatus(PlayerDataManager.INJURED);
                } else if (player.isGesperrt()) {
                	spotLineup.setStatus(PlayerDataManager.SUSPENDED);
                }
                spotLineup.setSpot(spot);
                spotLineup.setTactics(new ArrayList<TacticReport>());
                pp.reload(spotLineup);
                
            } else {
                pp.reload(null);
            }
            list.put(spot, pp);
        }

        lineupPanel.getMyTeam().setTeamName(HOVerwaltung.instance().getModel().getBasics().getTeamName() + " ("
                                            + HOVerwaltung.instance().getModel().getBasics().getTeamId() + ")");
        fillPanel(lineupPanel.getMyTeam().getKeeperPanel(), list.get(IMatchRoleID.keeper));
        fillPanel(lineupPanel.getMyTeam().getLeftWingbackPanel(), list.get(IMatchRoleID.leftBack));
        fillPanel(lineupPanel.getMyTeam().getLeftCentralDefenderPanel(), list.get(IMatchRoleID.leftCentralDefender));
        fillPanel(lineupPanel.getMyTeam().getRightCentralDefenderPanel(), list.get(IMatchRoleID.rightCentralDefender));
        fillPanel(lineupPanel.getMyTeam().getRightWingbackPanel(), list.get(IMatchRoleID.rightBack));
        fillPanel(lineupPanel.getMyTeam().getLeftWingPanel(), list.get(IMatchRoleID.leftWinger));
        fillPanel(lineupPanel.getMyTeam().getLeftMidfieldPanel(), list.get(IMatchRoleID.leftInnerMidfield));
        fillPanel(lineupPanel.getMyTeam().getRightMidfieldPanel(), list.get(IMatchRoleID.rightInnerMidfield));
        fillPanel(lineupPanel.getMyTeam().getRightWingPanel(), list.get(IMatchRoleID.rightWinger));
        fillPanel(lineupPanel.getMyTeam().getLeftForwardPanel(), list.get(IMatchRoleID.leftForward));
        fillPanel(lineupPanel.getMyTeam().getRightForwardPanel(), list.get(IMatchRoleID.rightForward));
        fillPanel(lineupPanel.getMyTeam().getCentralForwardPanel(), list.get(IMatchRoleID.centralForward));
        fillPanel(lineupPanel.getMyTeam().getCentralMidfieldPanel(), list.get(IMatchRoleID.centralInnerMidfield));
        fillPanel(lineupPanel.getMyTeam().getMiddleCentralDefenderPanel(), list.get(IMatchRoleID.middleCentralDefender));
        
        lineupPanel.getMyTeam().setLeftAttack(convertRating(lineup.getRatings().getLeftAttack().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setLeftDefence(convertRating(lineup.getRatings().getLeftDefense().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setRightAttack(convertRating(lineup.getRatings().getRightAttack().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setRightDefence(convertRating(lineup.getRatings().getRightDefense().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setMiddleAttack(convertRating(lineup.getRatings().getCentralAttack().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setMiddleDefence(convertRating(lineup.getRatings().getCentralDefense().get(0))); //FIXME: for compatibility here we should set 90 minutea average rating....
        lineupPanel.getMyTeam().setMidfield(convertRating(lineup.getRatings().getMidfield().get(0)));//FIXME: for compatibility here we should set 90 minutea average rating....
    }

    private String getPlayerName(String name) {
        return " " + name.substring(0, 1) + "." +name.substring(name.indexOf(" ")+1);
    }
    
    private int convertRating(double rating) {
        return RatingUtil.getIntValue4Rating(rating);
    }

    private void fillPanel(JPanel panel, PlayerPanel playerPanel) {
        panel.removeAll();
        
        // Don't add the panel of an empty position.
        if (playerPanel.getContainsPlayer() == true) {
        	panel.add(playerPanel);
        } else {
        	// But leave a box the size of a player panel...
        	Box box = new Box(BoxLayout.X_AXIS);
        	box.setPreferredSize(playerPanel.getDefaultSize());
        	panel.add(new javax.swing.Box(BoxLayout.X_AXIS));
        }
    }
}
