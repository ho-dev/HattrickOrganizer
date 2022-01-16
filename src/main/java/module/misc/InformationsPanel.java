package module.misc;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;

import java.awt.*;
import javax.swing.*;

import static core.util.Helper.getTranslation;

/**
 * Zeigt die allgemeinen Informationen
 */
public class InformationsPanel extends LazyImagePanel {

	private TeamPanel m_jpBasics;
	private FinancePanel m_jpAktuelleFinanzen;
	private FinancePanel m_jpVorwochenFinanzen;
	private MiscPanel m_jpSonstiges;
	private StaffPanel m_jpTrainerStab;
	private boolean initialized = false;
	private boolean needsRefresh = false;

	@Override
	protected void initialize() {
		registerRefreshable(true);
		initComponents();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		m_jpBasics.setLabels();
		m_jpAktuelleFinanzen.setLabels();
		m_jpVorwochenFinanzen.setLabels();
		m_jpSonstiges.setLabels();
		m_jpTrainerStab.setLabels();
	}

	private void initComponents() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4, 4, 4, 4);

		setLayout(new BorderLayout());

		final JPanel mainPanel = new ImagePanel();
		mainPanel.setLayout(new GridLayout(2, 1, 4, 4));

		JPanel panel = new ImagePanel();
		panel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		m_jpBasics = new TeamPanel();
		layout.setConstraints(m_jpBasics, constraints);
		panel.add(m_jpBasics);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		m_jpTrainerStab = new StaffPanel();
		layout.setConstraints(m_jpTrainerStab, constraints);
		panel.add(m_jpTrainerStab);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		m_jpSonstiges = new MiscPanel();
		layout.setConstraints(m_jpSonstiges, constraints);
		panel.add(m_jpSonstiges);

		mainPanel.add(panel);

		panel = new ImagePanel();
		panel.setLayout(layout);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		JLabel label = new JLabel(getTranslation("Finanzen"),SwingConstants.CENTER);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setBackground(ColorLabelEntry.BG_STANDARD.darker());
		label.setOpaque(true);
		label.setForeground(ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG));
		panel.add(label, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		m_jpAktuelleFinanzen = new FinancePanel(true);
		layout.setConstraints(m_jpAktuelleFinanzen, constraints);
		panel.add(m_jpAktuelleFinanzen);

		constraints.gridx = 1;
		constraints.gridwidth = 1;
		m_jpVorwochenFinanzen = new FinancePanel(false);
		layout.setConstraints(m_jpVorwochenFinanzen, constraints);
		panel.add(m_jpVorwochenFinanzen);

		mainPanel.add(panel);
		add(new JScrollPane(mainPanel));

	}

}
