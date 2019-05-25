package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.MatchRating;


import javax.swing.JFrame;
import javax.swing.JPanel;

public class RatingComparisonDialog extends JFrame {

	JPanel content = new JPanel();
	MatchRating toHO;
	MatchRating toHT;

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
		RatingComparisonPanel DiffRating = new RatingComparisonPanel("Diff", toHO.minus(toHT));
		content.add(HORating);
		content.add(HTRating);
		content.add(DiffRating);
		pack();
		setVisible(true);
	}
}
