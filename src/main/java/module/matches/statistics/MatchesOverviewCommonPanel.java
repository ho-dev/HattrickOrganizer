package module.matches.statistics;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.util.StringUtils;
import module.matches.SpielePanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;



public class MatchesOverviewCommonPanel extends ImagePanel {

	private static final long serialVersionUID = -7478328578730608662L;
	final GridBagLayout layout = new GridBagLayout();
    final GridBagConstraints constraints = new GridBagConstraints();
	public static final int HighestVictory 		= 0;
	public static final int HighestDefeat 		= 1;
	public static final int WonWithoutOppGoal 	= 2;
	public static final int LostWithoutOwnGoal 	= 3;
	public static final int FiveGoalsDiffWin	= 4;
	public static final int FiveGoalsDiffDefeat	= 5;
	public static final int TrailingHTWinningFT = 6;
	public static final int LeadingHTLosingFT 	= 7;

    
	private int matchtypes;
	private JLabel[] resultLabels = new JLabel[8];
	private JLabel[] teamNames = new JLabel[2];
	
    public MatchesOverviewCommonPanel(int matchtypes){
    	this.matchtypes = matchtypes;
		initialize();
	}
	
	private void initialize() {
		for (int i = 0; i < resultLabels.length; i++) {
			resultLabels[i] = new JLabel("0");
			resultLabels[i].setHorizontalTextPosition(SwingConstants.RIGHT);
			//resultLabels[i].setBorder(BorderFactory.createLineBorder(Color.RED));
		}
		for (int i = 0; i < teamNames.length; i++) {
			teamNames[i] = new JLabel();
		}
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 0.0;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(5, 3, 2, 2);
		
        setLayout(layout);
        
        JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString("HighestVictory"));
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.FIRST_LINE_START;
        add(label,1,1,2);
        add(teamNames[HighestVictory],1,2,1);
        add(resultLabels[HighestVictory],2,2,1);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("HighestDefeat")),1,3,2);
        add(teamNames[HighestDefeat],1,4,1);
        add(resultLabels[HighestDefeat],2,4,1);
        add(new JLabel(" "),1,5,2);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("WonWithoutOppGoal")),1,6,1);
        add(resultLabels[WonWithoutOppGoal],2,6,1);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("LostWithoutOwnGoal")),1,7,1);
        add(resultLabels[LostWithoutOwnGoal],2,7,1);
        add(new JLabel(" "),1,8,2);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("5GoalsDiffWin")),1,9,1);
        add(resultLabels[FiveGoalsDiffWin],2,9,1);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("5GoalsDiffDefeat")),1,10,1);
        add(resultLabels[FiveGoalsDiffDefeat],2,10,1);
        add(new JLabel(" "),1,11,2);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("TrailingHTWinningFT")),1,12,1);
        add(resultLabels[TrailingHTWinningFT],2,12,1);
        add(new JLabel(HOVerwaltung.instance().getLanguageString("LeadingHTLosingFT")),1,13,1);
        add(resultLabels[LeadingHTLosingFT],2,13,1);
//        add(new JLabel(" "),1,14,2);
//        add(new JLabel(HOVerwaltung.instance().getLanguageString("highlight_yellowcard")),1,15,1);
//        add(resultLabels[YELLOW_CARDS],2,15,1);
//        add(new JLabel(HOVerwaltung.instance().getLanguageString("highlight_redcard")),1,16,1);
//        add(resultLabels[RED_CARDS],2,16,1);
        
        refresh(matchtypes);
	}
	
	private void add(JComponent comp,int x, int y, int width){
		constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = width;
        constraints.gridheight = 1;
        layout.setConstraints(comp, constraints);
        add(comp);
	}

	
	
	public void refresh(int matchtypes) {
		 if(matchtypes == SpielePanel.ALLE_SPIELE || matchtypes == SpielePanel.NUR_FREMDE_SPIELE){
			 clear();
			 return;
		 }
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo home = DBManager.instance().getMatchesKurzInfo(teamId, matchtypes, HighestVictory, true);
		MatchKurzInfo away = DBManager.instance().getMatchesKurzInfo(teamId, matchtypes, HighestVictory, false);
		MatchKurzInfo info = getHighestMatch(home, away);
		if(info != null){
			teamNames[HighestVictory].setText(info.getHeimName()+" - "+info.getGastName());
			resultLabels[HighestVictory].setText(StringUtils.getResultString(info.getHeimTore(),info.getGastTore()));
		} else {
			teamNames[HighestVictory].setText("");
			resultLabels[HighestVictory].setText(StringUtils.getResultString(-1,-1));
		}
		home = DBManager.instance().getMatchesKurzInfo(teamId, matchtypes, HighestDefeat, true);
		away = DBManager.instance().getMatchesKurzInfo(teamId, matchtypes, HighestDefeat, false);
		info = getHighestMatch(home, away);

		if(info != null){
			teamNames[HighestDefeat].setText(info.getHeimName()+" - "+info.getGastName());
			resultLabels[HighestDefeat].setText(StringUtils.getResultString(info.getHeimTore(),info.getGastTore()));
		}else {
			teamNames[HighestDefeat].setText("");
			resultLabels[HighestDefeat].setText(StringUtils.getResultString(-1,-1));
		}
		for (int i = 2; i < resultLabels.length; i++) {
			resultLabels[i].setText(""+DBManager.instance().getMatchesKurzInfoStatisticsCount(teamId, matchtypes, i));
		}

	}
	
	private void clear(){
		for (int i = 0; i < resultLabels.length; i++) {
			resultLabels[i].setText("0");
		}
		for (int i = 0; i < teamNames.length; i++) {
			teamNames[i].setText("");
		}
	}
	private MatchKurzInfo getHighestMatch(MatchKurzInfo home,MatchKurzInfo away){
		if(home != null ){
			if(away != null){
				int homeDif = home.getHeimTore()>home.getGastTore()?home.getHeimTore()-home.getGastTore():home.getGastTore()-home.getHeimTore();
				int awayDif = away.getHeimTore()>away.getGastTore()?away.getHeimTore()-away.getGastTore():away.getGastTore()-away.getHeimTore();
				if(homeDif >= awayDif)
					return home;
				else
					return away;
			} else
				return home;
		} else
			return away;
	}
}
