package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.MatchRating;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RatingComparisonDialog extends JFrame {

	JPanel content = new JPanel();
	MatchRating toHO;
	MatchRating toHT;
	MatchRating diff = new MatchRating();

	public RatingComparisonDialog(MatchRating dPredictionRatingT0HO, MatchRating dPredictionRatingT0HT) {
		super(HOVerwaltung.instance().getLanguageString("RatingComparisonDialog"));
		setIconImage(HOMainFrame.instance().getIconImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		toHO = dPredictionRatingT0HO;
		toHT = dPredictionRatingT0HT;
		initComponents();
	}

	private void initComponents() {
		add(content);
		RatingComparisonPanel HORating = new RatingComparisonPanel("HO", toHO);
		RatingComparisonPanel HTRating = new RatingComparisonPanel("HT", toHT);
		diff.setCentralDefense(toHO.getCentralDefense()-toHT.getCentralDefense());
		diff.setRightDefense(toHO.getRightDefense()-toHT.getRightDefense());
		diff.setLeftDefense(toHO.getLeftDefense()-toHT.getLeftDefense());
		diff.setMidfield(toHO.getMidfield()-toHT.getMidfield());
		diff.setCentralAttack(toHO.getCentralAttack()-toHT.getCentralAttack());
		diff.setRightAttack(toHO.getRightAttack()-toHT.getRightAttack());
		diff.setLeftAttack(toHO.getLeftAttack()-toHT.getLeftAttack());
		diff.setHatStats(toHO.getHatStats()-toHT.getHatStats());
		diff.setLoddarStats(toHO.getLoddarStats()-toHT.getLoddarStats());
		RatingComparisonPanel DiffRating = new RatingComparisonPanel("Diff", diff);
		content.add(HORating);
		content.add(HTRating);
		content.add(DiffRating);
		pack();
		setVisible(true);
	}
}
