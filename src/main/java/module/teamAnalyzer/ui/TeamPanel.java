package module.teamAnalyzer.ui;

import core.gui.comp.panel.RasenPanel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.module.config.ModuleConfig;
import core.util.HOLogger;
import module.lineup.Lineup;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.ui.lineup.FormationPanel;
import module.teamAnalyzer.vo.TeamLineup;
import module.teamAnalyzer.vo.UserTeamSpotLineup;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

public class TeamPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------
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

    ManMarkingOrderDisplay manMarkingOrderDisplay;

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

    public void reload(TeamLineup teamLineup, int week, int season) {
        if (teamLineup != null) {
            lineupPanel.getOpponentTeam().setTeamName(SystemManager.getActiveTeamName() + " ("
                                                      + SystemManager.getActiveTeamId() + ")");
            keeper.reload(teamLineup.getSpotLineup(IMatchRoleID.keeper), week, season);
            leftBack.reload(teamLineup.getSpotLineup(IMatchRoleID.leftBack), week, season);
            leftCentral.reload(teamLineup.getSpotLineup(IMatchRoleID.leftCentralDefender), week, season);
            rightCentral.reload(teamLineup.getSpotLineup(IMatchRoleID.rightCentralDefender), week, season);
            rightBack.reload(teamLineup.getSpotLineup(IMatchRoleID.rightBack), week, season);
            leftWinger.reload(teamLineup.getSpotLineup(IMatchRoleID.leftWinger), week, season);
            leftMidfielder.reload(teamLineup.getSpotLineup(IMatchRoleID.leftInnerMidfield), week, season);
            rightMidfielder.reload(teamLineup.getSpotLineup(IMatchRoleID.rightInnerMidfield), week, season);
            rightWinger.reload(teamLineup.getSpotLineup(IMatchRoleID.rightWinger), week, season);
            leftAttacker.reload(teamLineup.getSpotLineup(IMatchRoleID.leftForward), week, season);
            rightAttacker.reload(teamLineup.getSpotLineup(IMatchRoleID.rightForward), week, season);
            centralAttacker.reload(teamLineup.getSpotLineup(IMatchRoleID.centralForward), week, season);
            centralMidfielder.reload(teamLineup.getSpotLineup(IMatchRoleID.centralInnerMidfield), week, season);
            middleCentral.reload(teamLineup.getSpotLineup(IMatchRoleID.middleCentralDefender), week, season);
            
            lineupPanel.getOpponentTeam().setLeftAttack(teamLineup.getRating().getLeftAttack());
            lineupPanel.getOpponentTeam().setLeftDefence(teamLineup.getRating().getLeftDefense());
            lineupPanel.getOpponentTeam().setRightAttack(teamLineup.getRating().getRightAttack());
            lineupPanel.getOpponentTeam().setRightDefence(teamLineup.getRating().getRightDefense());
            lineupPanel.getOpponentTeam().setMiddleAttack(teamLineup.getRating().getCentralAttack());
            lineupPanel.getOpponentTeam().setMiddleDefence(teamLineup.getRating().getCentralDefense());
            lineupPanel.getOpponentTeam().setMidfield(teamLineup.getRating().getMidfield());

            setMyTeam();

            if ( this.lineupPanel.displayBothTeams() ) {

                // Display man marking order
                Lineup ownLineup = getOwnLineup();
                var manMarkingOrder = ownLineup.getManMarkingOrder();
                if (manMarkingOrder != null) {
                    var manMarker = manMarkingOrder.getSubjectPlayerID();
                    var manMarkerPos = ownLineup.getPositionBySpielerId(manMarker).getId();
                    var manMarkedPos = teamLineup.getPositionByPlayerId(manMarkingOrder.getObjectPlayerID());
                    var from = lineupPanel.getMyTeam().getPanel(manMarkerPos);
                    if (manMarkingOrderDisplay == null) {
                        manMarkingOrderDisplay = new ManMarkingOrderDisplay(grassPanel);
                    }
                    if (manMarkedPos > 0) {
                        var to = lineupPanel.getOpponentTeam().getPanel(manMarkedPos);
                        manMarkingOrderDisplay.set(from, to);
                    } else {
                        // TODO: Display warning about failed man marking order
                        manMarkingOrderDisplay.set(from, from);
                    }
                } else if (manMarkingOrderDisplay != null) {
                    manMarkingOrderDisplay = null;
                }
            }

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
    	HashMap<Integer, UserTeamPlayerPanel> list = new HashMap<>();
        Lineup lineup = getOwnLineup();

        for (int spot : IMatchRoleID.aFieldMatchRoleID) {
            Player player = lineup.getPlayerByPositionID(spot);
            UserTeamPlayerPanel pp = new UserTeamPlayerPanel();

            if (player != null) {
                UserTeamSpotLineup spotLineup = new UserTeamSpotLineup();
                spotLineup.setAppearance(0);
                spotLineup.setName(" " + player.getShortName());
                spotLineup.setPlayerId(player.getPlayerID());
                spotLineup.setSpecialEvent(player.getPlayerSpecialty());
                spotLineup.setTacticCode(lineup.getTactic4PositionID(spot));
                spotLineup.setPosition(lineup.getEffectivePos4PositionID(spot));
                spotLineup.setRating(player.calcPosValue(lineup.getEffectivePos4PositionID(spot),
                                                          true, null, false));
                if (player.isInjured() > 0) {
                	spotLineup.setStatus(PlayerDataManager.INJURED);
                } else if (player.isRedCarded()) {
                	spotLineup.setStatus(PlayerDataManager.SUSPENDED);
                }
                spotLineup.setSpot(spot);
                spotLineup.setTactics(new ArrayList<>());
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
        
        lineupPanel.getMyTeam().setLeftAttack(convertRating(lineup.getRatings().getLeftAttack().get(-90d)));
        lineupPanel.getMyTeam().setLeftDefence(convertRating(lineup.getRatings().getLeftDefense().get(-90d)));
        lineupPanel.getMyTeam().setRightAttack(convertRating(lineup.getRatings().getRightAttack().get(-90d)));
        lineupPanel.getMyTeam().setRightDefence(convertRating(lineup.getRatings().getRightDefense().get(-90d)));
        lineupPanel.getMyTeam().setMiddleAttack(convertRating(lineup.getRatings().getCentralAttack().get(-90d)));
        lineupPanel.getMyTeam().setMiddleDefence(convertRating(lineup.getRatings().getCentralDefense().get(-90d)));
        lineupPanel.getMyTeam().setMidfield(convertRating(lineup.getRatings().getMidfield().get(-90d)));
    }

    private int convertRating(double rating) {
        return RatingUtil.getIntValue4Rating(rating);
    }

    private void fillPanel(JPanel panel, PlayerPanel playerPanel) {
        panel.removeAll();
        
        // Don't add the panel of an empty position.
        if (playerPanel.getContainsPlayer()) {
            playerPanel.setPreferredSize(playerPanel.getDefaultSize());
        	panel.add(playerPanel);
        } else {
        	// But leave a box the size of a player panel...
        	Box box = new Box(BoxLayout.X_AXIS);
        	box.setPreferredSize(playerPanel.getDefaultSize());
        	panel.add(box);
        }
    }

    public Lineup getOwnLineup() {
        return HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
    }

    private class ManMarkingOrderDisplay extends JPanel {
        private final int ARR_SIZE = 8;
        int xfrom, yfrom, xto, yto;
        JComponent parent;
        public ManMarkingOrderDisplay(JPanel grassPanel) {
            parent = grassPanel;
            parent.add(this,0);
        }

        public void set(JPanel from, JPanel to) {
            xfrom = x(from) + (int) (from.getWidth() * 0.15);
            yfrom = y(from) + (int) (from.getHeight() * 0.25);
            xto = x(to) + (int) (to.getWidth() * 0.85);
            yto = y(to) + (int) (to.getHeight() * 0.75);
        }

        private int y(Container component) {
            var ret = component.getY();
            if ( component.getParent() != parent) ret += y(component.getParent());
            return ret;
        }

        private int x(Container component) {
            var ret = component.getX();
            if ( component.getParent() != parent) ret += x(component.getParent());
            return ret;
        }

        @Override
        protected void paintComponent(Graphics gIn) {
            //super.paintComponent(gIn);
            // adapted from aioobe, https://stackoverflow.com/questions/4112701/drawing-a-line-with-arrow-in-java
            double dx = xto - xfrom, dy = yto - yfrom;
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.sqrt(dx*dx + dy*dy);
            AffineTransform at = AffineTransform.getTranslateInstance(xfrom, yfrom);
            at.concatenate(AffineTransform.getRotateInstance(angle));

            Graphics2D g = (Graphics2D) gIn.create();
            g.transform(at);

            g.setPaint(Color.RED);
            g.setStroke(new BasicStroke(5));
            // Draw horizontal arrow starting in (0, 0)
            g.drawLine(0, 0, len-ARR_SIZE, 0);
            g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                    new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
        }

    }
}
