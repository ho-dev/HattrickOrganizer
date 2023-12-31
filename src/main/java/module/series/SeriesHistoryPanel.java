// %3066887473:de.hattrickorganizer.gui.league%
package module.series;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.series.TabellenVerlaufEintrag;

import javax.swing.*;
import java.awt.*;

/**
 * Shows the history of the league table as a graph.
 *
 * <p>The graph is drawn using {@link Graphics} primitives.</p>
 */
final class SeriesHistoryPanel extends JPanel {

	private final Color[] COLOR4LINES = {
			ThemeManager.getColor(HOColorName.PALETTE13_0),
			ThemeManager.getColor(HOColorName.PALETTE13_1),
			ThemeManager.getColor(HOColorName.PALETTE13_2),
			ThemeManager.getColor(HOColorName.PALETTE13_8),
			ThemeManager.getColor(HOColorName.PALETTE13_4),
			ThemeManager.getColor(HOColorName.PALETTE13_5),
			ThemeManager.getColor(HOColorName.PALETTE13_6),
			ThemeManager.getColor(HOColorName.PALETTE13_9)
	};

	private final Color STANDARD_FOREGROUND = ThemeManager.getColor(HOColorName.LEAGUE_FG);
	private TabellenVerlaufEintrag[] m_clVerlaufeintraege;
    private final Model model;

	/**
	 * Creates a new TabellenverlaufStatistikPanel object.
	 */
    SeriesHistoryPanel(Model model) {
		this.model = model;
		setPreferredSize(new Dimension(700, 240));
		initValues();
	}

	@Override
	public void paint(Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;

		// Antialiasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Hintergrund
		g2d.setColor(ThemeManager.getColor(HOColorName.LEAGUE_BG));// Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if ((m_clVerlaufeintraege != null) && (m_clVerlaufeintraege.length > 0)
				&& (m_clVerlaufeintraege[0] != null)) {
			final int aktuelleTeamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();

			// Wegen Überschrift!
			final int anzahlPlaetze = m_clVerlaufeintraege.length + 1;
			final int anzahlSpieltage = m_clVerlaufeintraege[0].getPlatzierungen().length;
			final int abstandVertikal = getHeight() / (anzahlPlaetze + 1);
            int TEAM_NAME_WIDTH = 180;
            final int abstandHorizontal = (getWidth() - TEAM_NAME_WIDTH) / anzahlSpieltage;

			final int fontsize = UserParameter.instance().fontSize;
			final Font fettFont = new Font("sansserif", Font.BOLD, fontsize);
			final Font normalFont = new Font("sansserif", Font.PLAIN, fontsize);

			// Koordinatenkreuz
			g2d.setColor(ThemeManager.getColor(HOColorName.LEAGUEHISTORY_CROSS_FG));

			// Vertikal
			g2d.drawLine(getWidth() - TEAM_NAME_WIDTH, 0, getWidth() - TEAM_NAME_WIDTH,
					getHeight());
			g2d.drawLine(getWidth() - TEAM_NAME_WIDTH + 1, 0, getWidth() - TEAM_NAME_WIDTH
					+ 1, getHeight());

			// Horizontal
			g2d.drawLine(0, getHeight() / anzahlPlaetze, getWidth(), getHeight() / anzahlPlaetze);
			g2d.drawLine(0, (getHeight() / anzahlPlaetze) + 1, getWidth(),
					(getHeight() / anzahlPlaetze) + 1);

			// Hilfslinien
			g2d.setColor(ThemeManager.getColor(HOColorName.LEAGUEHISTORY_CROSS_FG));

			// Horizontal
			for (int i = 1; i < anzahlPlaetze; i++) {
				g2d.drawLine(0, getHeight() / anzahlPlaetze * i, getWidth(), getHeight()
						/ anzahlPlaetze * i);
			}

			// Vertikal
			for (int i = 1; i < anzahlSpieltage; i++) {
				g2d.drawLine(abstandHorizontal * i, 0, abstandHorizontal * i, getHeight());
			}

			// Position and Team name
			for (int i = 0; i < m_clVerlaufeintraege.length; i++) {
				// Platzierung
				if (m_clVerlaufeintraege[i].getTeamId() == aktuelleTeamId) {
					g2d.setColor(ThemeManager.getColor(HOColorName.HOME_TEAM_FG));
				} else if (i < COLOR4LINES.length) {
					g2d.setColor(COLOR4LINES[i]);
				} else {
					g2d.setColor(STANDARD_FOREGROUND);
				}

				g2d.setFont(fettFont);
				g2d.drawString((i + 1) + ".", (getWidth() + 7) - TEAM_NAME_WIDTH, (getHeight()
						/ anzahlPlaetze * (i + 1))
						+ abstandVertikal - (fontsize / 2));

				// Eigenes Team blau machen
				if (m_clVerlaufeintraege[i].getTeamId() == aktuelleTeamId) {
					g2d.setColor(ThemeManager.getColor(HOColorName.HOME_TEAM_FG));
				} else {
					g2d.setColor(STANDARD_FOREGROUND);
				}

				// Maximal 30 Zeichen als Namen
				g2d.drawString(
						m_clVerlaufeintraege[i].getTeamName().substring(0,
								Math.min(30, m_clVerlaufeintraege[i].getTeamName().length())),
						(getWidth() + 20) - TEAM_NAME_WIDTH,
						(getHeight() / anzahlPlaetze * (i + 1)) + abstandVertikal - (fontsize / 2));
			}

			// Spieltage
			for (int i = 1; i <= anzahlSpieltage; i++) {
				g2d.setFont(normalFont);
				g2d.setColor(STANDARD_FOREGROUND);
				g2d.drawString(String.valueOf(i), ((abstandHorizontal * (i - 0.5f)) - 2), abstandVertikal - 2);
			}

			final Stroke thinkStroke = new BasicStroke();

			// Linien zeichnen
			for (int i = 0; i < m_clVerlaufeintraege.length; i++) {
				final int[] platzierungen = m_clVerlaufeintraege[i].getPlatzierungen();

				g2d.setStroke(thinkStroke);
				if (m_clVerlaufeintraege[i].getTeamId() == aktuelleTeamId) {
					g2d.setColor(ThemeManager.getColor(HOColorName.HOME_TEAM_FG));
					g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
				} else if (i < COLOR4LINES.length) {
					g2d.setColor(COLOR4LINES[i]);
				} else {
					g2d.setColor(STANDARD_FOREGROUND);
				}

				// Erste Linie vom Namen
				g2d.drawLine(
						getWidth() - TEAM_NAME_WIDTH - 5,
						(int) ((getHeight() / anzahlPlaetze * (i + 0.7f)) + abstandVertikal),
						(int) (abstandHorizontal * (platzierungen.length - 0.5)),
						(int) ((getHeight() / anzahlPlaetze * (platzierungen[platzierungen.length - 1] - 0.3f)) + abstandVertikal));

				for (int j = 0; j < (platzierungen.length - 1); j++) {
					g2d.drawLine(
							(int) (abstandHorizontal * (j + 0.5)),
							(int) ((getHeight() / anzahlPlaetze * (platzierungen[j] - 0.3f)) + abstandVertikal),
							(int) (abstandHorizontal * (j + 1.5)),
							(int) ((getHeight() / anzahlPlaetze * (platzierungen[j + 1] - 0.3f)) + abstandVertikal));
				}

				// Letzte Linie bis zu Ende
				g2d.drawLine(
						5,
						(int) ((getHeight() / anzahlPlaetze * (platzierungen[0] - 0.3f)) + abstandVertikal),
						(int) (abstandHorizontal * 0.5),
						(int) ((getHeight() / anzahlPlaetze * (platzierungen[0] - 0.3f)) + abstandVertikal));
			}
		}
	}

	void changeSaison() {
		initValues();
	}

	private void initValues() {
		if (this.model.getCurrentSeries() != null) {
			m_clVerlaufeintraege = this.model.getCurrentSeries().getVerlauf().getEintraege();
		}

		repaint();
	}
}
