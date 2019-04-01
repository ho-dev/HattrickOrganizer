package module.lineup.exchange;

import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import module.lineup.Lineup;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class LineupCheck {

	public static boolean doUpload(MatchKurzInfo match, Lineup lineup) {
		List<JLabel> problems = new ArrayList<JLabel>();
		if (hasFreePosition(lineup)) {
			problems.add(getErrorLabel("lineup.upload.check.lineupIncomplete"));
		}
		if (hasFreeReserves(lineup)) {
			problems.add(getWarningLabel("lineup.upload.check.reservesIncomplete"));
		}
		if (lineup.getKapitaen() <= 0) {
			problems.add(getWarningLabel("lineup.upload.check.captainNotSet"));
		}
		if (lineup.getKicker() <= 0) {
			problems.add(getWarningLabel("lineup.upload.check.setPiecesNotSet"));
		}
		if (!penaltyTakersOK(match, lineup)) {
			problems.add(getWarningLabel("lineup.upload.check.lessThan11PenaltytakersSet"));
		}
		if (problems.size() > 0) {
			JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString("lineup.upload.check.uploadAnywayQ"));
			label.setBorder(BorderFactory.createEmptyBorder(10, 20, 2, 10));
			problems.add(label);
		} else {
			return true;
		}

		String title = HOVerwaltung.instance().getLanguageString("lineup.upload.check.title");
		int result = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
				problems.toArray(), title, JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		return result == JOptionPane.YES_OPTION;
	}

	public static boolean hasFreePosition(Lineup lineup) {
		return lineup.hasFreePosition();
	}

	public static boolean hasFreeReserves(Lineup lineup) {
		for (int subPos : IMatchRoleID.aBackupssMatchRoleID) {
			if(isFree(lineup, subPos)) return true;
		}
		return false;
	}

	private static boolean penaltyTakersOK(MatchKurzInfo match, Lineup lineup) {
		if (!match.getMatchTyp().isCupRules()) {
			return true;
		}
		return lineup.getPenaltyTakers().size() >= 11;
	}
	
	private static boolean isFree(Lineup lineup, int positionId) {
		MatchRoleID pos = lineup.getPositionById(positionId);
		return pos == null || pos.getSpielerId() == 0;
	}

	private static JLabel getWarningLabel(String key) {
		JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString(key));
		label.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION));
		return label;
	}

	private static JLabel getErrorLabel(String key) {
		JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString(key));
		label.setIcon(ThemeManager.getIcon(HOIconName.EXCLAMATION_RED));
		return label;
	}
}
