/*
 * MatchPredictionSpieleTableModel.java
 *
 * Created on 4. Januar 2005, 13:19
 */
package core.prediction;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.ImageUtilities;
import core.model.TranslationFacility;
import core.prediction.engine.MatchResult;

import javax.swing.SwingConstants;
import javax.swing.*;

public class MatchResultTableModel extends AbstractMatchTableModel {
	//~ Instance fields ----------------------------------------------------------------------------
	public static final String[] columnNames = {
		TranslationFacility.tr("ls.match.result"),
		TranslationFacility.tr("frequency"),
		"" };

	//~ Constructors -------------------------------------------------------------------------------
	/**
	 * Creates a new MatchPredictionSpieleTableModel object.
	 */
	public MatchResultTableModel(MatchResult matchresults,boolean isHome) {
		super(matchresults,isHome);
	}

	//~ Methods ------------------------------------------------------------------------------------


	/**
	 * Erzeugt einen Data[][] aus dem Spielervector
	 */
	@Override
	protected void initData() {
		m_clData = new Object[25][getColumnNames().length];
		double number = matchResult.getMatchNumber();

		if (number == 0.0) {
			number = 1.0;
		}

		for (int home = 0; home < 5; home++) {
			for (int away = 0; away < 5; away++) {
				final int res = matchResult.getResultDetail()[(home * 5) + away];

				// result
				m_clData[(home * 5) + away][0] = new ColorLabelEntry(home + " - " + away, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);

				//Ergebnis
				m_clData[(home * 5) + away][1] = getProgressBar(res / number);

				m_clData[(home * 5) + away][2] = new ColorLabelEntry(1, "", ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);

				if (home > away) {
					((ColorLabelEntry) m_clData[(home * 5) + away][2]).setIcon(ImageUtilities.getStarIcon());
				} else if (home < away) {
					((ColorLabelEntry) m_clData[(home * 5) + away][2]).setIcon(ImageUtilities.NOIMAGEICON);
				} else {
					((ColorLabelEntry) m_clData[(home * 5) + away][2]).setIcon(ImageUtilities.getStarIcon());
				}
			}
		}
	}

	@Override
	public String[] getColumnNames() {
		return columnNames;
	}


}
