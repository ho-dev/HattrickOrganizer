// %1645704922:de.hattrickorganizer.gui.matches%
package module.matches;

import core.db.DBManager;
import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.util.HOLogger;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog um ein Spiel mit allen Infos zu Drucken
 */
public class SpielePrintDialog extends JDialog {

	private static final long serialVersionUID = 1259449929345060213L;
	private AufstellungsSternePanel m_jpAufstellungGastPanel;
	private AufstellungsSternePanel m_jpAufstellungHeimPanel;
	private ManschaftsBewertungsPanel m_jpManschaftsBewertungsPanel;
	private SpielHighlightPanel m_jpSpielHighlightPanel;
	private StaerkenvergleichPanel m_jpStaerkenvergleichsPanel;
	private MatchesModel matchesModel;

	public SpielePrintDialog(MatchesModel matchesModel) {
		this.matchesModel = matchesModel;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		initValues();

		Toolkit kit = Toolkit.getDefaultToolkit();
		setLocation(kit.getScreenSize().width, kit.getScreenSize().height);
		pack();
		setVisible(true);
	}

	public final void doPrint(String titel) {
		try {
			final core.gui.print.PrintController printController = core.gui.print.PrintController
					.getInstance();

			printController.add(new core.gui.print.ComponentPrintObject(printController.getPf(),
					titel, getContentPane(), core.gui.print.ComponentPrintObject.SICHTBAR));

			printController.print();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), e);
		}
	}

	private void initComponents() {
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(0, 0, 0, 0);
		getContentPane().setLayout(layout);
		// background important for printing, so static white
		getContentPane().setBackground(Color.WHITE);

		// Allgemein
		m_jpStaerkenvergleichsPanel = new StaerkenvergleichPanel(this.matchesModel, true);
		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(m_jpStaerkenvergleichsPanel, constraints);
		getContentPane().add(m_jpStaerkenvergleichsPanel);

		// Bewertung
		m_jpManschaftsBewertungsPanel = new ManschaftsBewertungsPanel(this.matchesModel, true);
		constraints.gridx = 0;
		constraints.gridy = 1;
		layout.setConstraints(m_jpManschaftsBewertungsPanel, constraints);
		getContentPane().add(m_jpManschaftsBewertungsPanel);

		// Highlights
		m_jpSpielHighlightPanel = new SpielHighlightPanel(this.matchesModel, true);
		constraints.gridx = 0;
		constraints.gridy = 2;
		layout.setConstraints(m_jpSpielHighlightPanel, constraints);
		getContentPane().add(m_jpSpielHighlightPanel);

		// Aufstellung
		JPanel aufstellungsPanel = new JPanel(new GridLayout(2, 1));
		m_jpAufstellungHeimPanel = new AufstellungsSternePanel(true, true);
		aufstellungsPanel.add(m_jpAufstellungHeimPanel);
		m_jpAufstellungGastPanel = new AufstellungsSternePanel(false, true);
		aufstellungsPanel.add(m_jpAufstellungGastPanel);
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 3;
		layout.setConstraints(aufstellungsPanel, constraints);
		getContentPane().add(aufstellungsPanel);
	}

	private void initValues() {
		MatchKurzInfo info = this.matchesModel.getMatch();
		if (info != null) {
			// Selektiertes Spiel des Models holen und alle 3 Panel informieren
			try {
				final Matchdetails details = info.getMatchdetails();

				if (info.getMatchStatus() == MatchKurzInfo.FINISHED) {
					m_jpAufstellungHeimPanel.refresh(info.getMatchID(), info.getHeimID());
					m_jpAufstellungGastPanel.refresh(info.getMatchID(), info.getGastID());
				} else {
					m_jpAufstellungHeimPanel.clearAll();
					m_jpAufstellungGastPanel.clearAll();
				}
			} catch (Exception e) {
				m_jpAufstellungHeimPanel.clearAll();
				m_jpAufstellungGastPanel.clearAll();
				HOLogger.instance().log(
						getClass(),
						"SpielePanel.newSelectionInform: Keine Match zum Eintrag in der Tabelle gefunden! "
								+ e);
			}
		} else {
			// Alle Panels zurücksetzen
			m_jpAufstellungHeimPanel.clearAll();
			m_jpAufstellungGastPanel.clearAll();
		}
	}
}
