package module.lineup;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;

import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RatingComparisonDialog extends JFrame {

	JPanel content = new JPanel();
	HashMap<String, Double> toHO;
	HashMap<String, Double> toHT;
	HashMap<String, Double> diff = new HashMap<String, Double>();

	public RatingComparisonDialog(HashMap<String, Double> dPredictionRatingT0HO, HashMap<String, Double> dPredictionRatingT0HT) {
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
		diff.put("DC", toHO.get("DC")-toHT.get("DC"));
		diff.put("DR", toHO.get("DR")-toHT.get("DR"));
		diff.put("DL", toHO.get("DL")-toHT.get("DL"));
		diff.put("M", toHO.get("M")-toHT.get("M"));
		diff.put("FC", toHO.get("FC")-toHT.get("FC"));
		diff.put("FR", toHO.get("FR")-toHT.get("FR"));
		diff.put("FL", toHO.get("FL")-toHT.get("FL"));
		diff.put("HatStats", toHO.get("HatStats")-toHT.get("HatStats"));
		diff.put("Loddar", toHO.get("Loddar")-toHT.get("Loddar"));
		RatingComparisonPanel DiffRating = new RatingComparisonPanel("Diff", diff);
		content.add(HORating);
		content.add(HTRating);
		content.add(DiffRating);
		pack();
		setVisible(true);
	}
}
