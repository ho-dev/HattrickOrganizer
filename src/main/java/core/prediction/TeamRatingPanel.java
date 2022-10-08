// %1301666028:de.hattrickorganizer.gui.matchprediction%
package core.prediction;

import core.constants.player.PlayerAbility;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.Matchdetails;
import core.prediction.engine.TeamData;
import core.prediction.engine.TeamRatings;
import module.teamAnalyzer.ui.RatingUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



class TeamRatingPanel extends JPanel implements ItemListener {
	private static final long serialVersionUID = -6238120571629957579L;
    //~ Instance fields ----------------------------------------------------------------------------
	private final GridBagConstraints m_clConstraints;
    private List<RatingItem> levels;
    private List<RatingItem> subLevels;
    private List<RatingItem> tactics;
    private String teamName;
    private final JComboBox[][] values = new JComboBox[8][2];
    private int row;

    private boolean ratingsChanged;

    //~ Constructors -------------------------------------------------------------------------------

    TeamRatingPanel(TeamData team) {
        super();
        teamName = team.getTeamName();
        initLevel(25);
        initSubLevel();
        initTactics();
        GridBagLayout m_clLayout = new GridBagLayout();
        m_clConstraints = new GridBagConstraints();
        m_clConstraints.fill = GridBagConstraints.HORIZONTAL;
        m_clConstraints.weightx = 0.0;
        m_clConstraints.weighty = 0.0;
        m_clConstraints.insets = new Insets(1, 1, 1, 1);
        setLayout(m_clLayout);

        final TeamRatings tr = team.getRatings();
        final core.model.HOVerwaltung verwaltung = core.model.HOVerwaltung
                                                                   .instance();
        addLine(tr.getMidfield(), verwaltung.getLanguageString("ls.match.ratingsector.midfield"));
        addLine(tr.getRightDef(), verwaltung.getLanguageString("ls.match.ratingsector.rightdefence"));
        addLine(tr.getMiddleDef(), verwaltung.getLanguageString("ls.match.ratingsector.centraldefence"));
        addLine(tr.getLeftDef(), verwaltung.getLanguageString("ls.match.ratingsector.leftdefence"));
        addLine(tr.getRightAttack(), verwaltung.getLanguageString("ls.match.ratingsector.rightattack"));
        addLine(tr.getMiddleAttack(), verwaltung.getLanguageString("ls.match.ratingsector.centralattack"));
        addLine(tr.getLeftAttack(), verwaltung.getLanguageString("ls.match.ratingsector.leftattack"));

        m_clConstraints.gridx = 0;
        m_clConstraints.gridy = row;
        m_clConstraints.gridwidth = 3;

        final JPanel taktikpanel = new JPanel();
        taktikpanel.setOpaque(false);

        values[row][0] = new JComboBox(tactics.toArray());

        int tactIndex = team.getTacticType();

        // Creative is 7 and not 5.. why????
        if (tactIndex == IMatchDetails.TAKTIK_CREATIVE) {
            tactIndex = 5;
        }

        // LongShots is 8 and not 6.. Why? I don't know, either. :-)
        if (tactIndex == IMatchDetails.TAKTIK_LONGSHOTS) {
            tactIndex = 6;
        }

        values[row][0].setSelectedIndex(tactIndex);
        values[row][0].addItemListener(this);
        taktikpanel.add(values[row][0]);

        values[row][1] = new JComboBox(levels.toArray());
        values[row][1].setSelectedIndex(Math.min(team.getTacticLevel(), 19)); // limit tactic strength to divine
        values[row][1].addItemListener(this);

        if (team.getTacticType() == IMatchDetails.TAKTIK_NORMAL) {
            values[row][1].setEnabled(false);
        }

        taktikpanel.add(values[row][1]);

        add(taktikpanel, m_clConstraints);

        setOpaque(false);

        ratingsChanged = false;
    }

    final void setTeamData(TeamData teamdata) {
        teamName = teamdata.getTeamName();

        final TeamRatings ratings = teamdata.getRatings();
        int lvl;
        int subLvl;

        lvl = ((int) ratings.getMidfield() - 1) / 4;
        subLvl = ((int) ratings.getMidfield() - 1) - (lvl * 4);
        values[0][0].setSelectedIndex(lvl);
        values[0][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getRightDef() - 1) / 4;
        subLvl = ((int) ratings.getRightDef() - 1) - (lvl * 4);
        values[1][0].setSelectedIndex(lvl);
        values[1][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getMiddleDef() - 1) / 4;
        subLvl = ((int) ratings.getMiddleDef() - 1) - (lvl * 4);
        values[2][0].setSelectedIndex(lvl);
        values[2][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getLeftDef() - 1) / 4;
        subLvl = ((int) ratings.getLeftDef() - 1) - (lvl * 4);
        values[3][0].setSelectedIndex(lvl);
        values[3][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getRightAttack() - 1) / 4;
        subLvl = ((int) ratings.getRightAttack() - 1) - (lvl * 4);
        values[4][0].setSelectedIndex(lvl);
        values[4][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getMiddleAttack() - 1) / 4;
        subLvl = ((int) ratings.getMiddleAttack() - 1) - (lvl * 4);
        values[5][0].setSelectedIndex(lvl);
        values[5][1].setSelectedIndex(subLvl);

        lvl = ((int) ratings.getLeftAttack() - 1) / 4;
        subLvl = ((int) ratings.getLeftAttack() - 1) - (lvl * 4);
        values[6][0].setSelectedIndex(lvl);
        values[6][1].setSelectedIndex(subLvl);

        ratingsChanged = false;
    }

    final TeamData getTeamData() {
        final TeamRatings rat = new TeamRatings();
        rat.setMidfield(getValue(0));
        rat.setRightDef(getValue(1));
        rat.setMiddleDef(getValue(2));
        rat.setLeftDef(getValue(3));
        rat.setRightAttack(getValue(4));
        rat.setMiddleAttack(getValue(5));
        rat.setLeftAttack(getValue(6));

        int tactic = values[7][0].getSelectedIndex();
        if (tactic > 4) tactic += 2; // special values for longshot and creativ

        return new TeamData(teamName, rat, tactic, values[7][1].getSelectedIndex() + 1);
    }

    public final void itemStateChanged(ItemEvent e) {
        if ( e.getSource() == values[7][0]){
            //Taktik
            values[7][1].setEnabled(!(values[7][0].getSelectedItem() instanceof RatingItem)
                    || (((RatingItem) values[7][0].getSelectedItem()).getValue() != IMatchDetails.TAKTIK_NORMAL));
        }
        ratingsChanged = true;
    }

    private double getValue(int row) {
        final int lvl = values[row][0].getSelectedIndex();
        final int subLvl = values[row][1].getSelectedIndex();
        return (lvl * 4) + subLvl + 1;
    }

    private void addLine(double d, String zone) {
        m_clConstraints.gridx = 0;
        m_clConstraints.gridy = row;
        add(new JLabel(zone), m_clConstraints);

        final int lvl = (int)RatingUtil.getDoubleValue4Rating((int)d);
        if ( lvl >= levels.size()){
            initLevel(lvl+1);
        }
        values[row][0] = new JComboBox(levels.toArray());
        values[row][0].setSelectedIndex(lvl);
        m_clConstraints.gridx = 1;
        add(values[row][0], m_clConstraints);

        final int subLvl = RatingUtil.getSubLevel((int)d);
        values[row][1] = new JComboBox(subLevels.toArray());
        values[row][1].setSelectedIndex(subLvl);
        m_clConstraints.gridx = 2;
        add(values[row][1], m_clConstraints);

        values[row][0].addItemListener(this);
        values[row][1].addItemListener(this);

        row++;
    }

    private void initSubLevel() {
        subLevels = new ArrayList<>();

        final HOVerwaltung verwaltung = HOVerwaltung.instance();
        subLevels.add(new RatingItem(verwaltung.getLanguageString("verylow"), 0));
        subLevels.add(new RatingItem(verwaltung.getLanguageString("low"), 1));
        subLevels.add(new RatingItem(verwaltung.getLanguageString("high"), 2));
        subLevels.add(new RatingItem(verwaltung.getLanguageString("veryhigh"), 3));
    }

    private void initLevel(int n) {
        levels = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            levels.add(new RatingItem(PlayerAbility.getNameForSkill(i, false), i + 1));
        }
    }

    private void initTactics() {
        tactics = new ArrayList<>();
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_NORMAL),IMatchDetails.TAKTIK_NORMAL));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_PRESSING),IMatchDetails.TAKTIK_PRESSING));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_KONTER), IMatchDetails.TAKTIK_KONTER));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_MIDDLE),IMatchDetails.TAKTIK_MIDDLE));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_WINGS), IMatchDetails.TAKTIK_WINGS));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_CREATIVE),IMatchDetails.TAKTIK_CREATIVE));
        tactics.add(new RatingItem(Matchdetails.getNameForTaktik(IMatchDetails.TAKTIK_LONGSHOTS), IMatchDetails.TAKTIK_LONGSHOTS));
    }

    public boolean isRatingsChanged() {
        return ratingsChanged;
    }
}
