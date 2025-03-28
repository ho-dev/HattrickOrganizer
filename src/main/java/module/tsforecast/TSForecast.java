package module.tsforecast;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.enums.MatchType;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.module.config.ModuleConfig;
import core.util.HOLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;

public class TSForecast extends LazyImagePanel implements ActionListener, ItemListener {
	private final DBManager dbManager;

	static final String TS_SHOWCUPMATCHES = "TS_ShowCupMatches";
	static final String TS_SHOWQUALIFICATIONMATCH = "TS_ShowQualificationMatch";
	static final String TS_HISTORY = "TS_History";
	static final String TS_LOEPIFORECAST = "TS_LoepiForecast";
	static final String TS_LOEPIHISTORY = "TS_LoepiHistory";
	static final String TS_CONFIDENCE = "TS_Confidence";
	static final String TS_GENERALSPIRIT = "TS_GeneralSpirit";

	private JPanel m_jpSettingsPanel;
	private JPanel m_jpGamesPanel;
	private JCheckBox m_jtCupMatches;
	private JCheckBox m_jtRelegationMatch;
	private CheckBox m_jtHistory;
	private CheckBox m_jtLoepiHist;
	private CheckBox m_jtLoepiFore;
	private CheckBox m_jtConfidence;
	private TSPanel m_jpGraphics;
	private HistoryCurve m_History;
	private LoepiCurve m_LoepiForecast;
	private LoepiCurve m_LoepiHist;
	private TrainerCurve m_Trainer;
	private ConfidenceCurve m_Confidence;

	TSForecast(DBManager dbManager) {
		this.dbManager = dbManager;
	}

	@Override
	protected void initialize() {
		initializeConfig();
		initComponents();
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		ModuleConfig config = ModuleConfig.instance();
		config.setBoolean(TS_SHOWCUPMATCHES, isInCup());
		if (hasQualificationMatch())
			config.setBoolean(TS_SHOWQUALIFICATIONMATCH, true);

		try {
			createCurves();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		createGamesPanel(m_jpSettingsPanel);
		m_jpGraphics.repaint();
	}

	private void initComponents() {
		GridBagLayout gridbaglayout = new GridBagLayout();
		setLayout(gridbaglayout);

		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.fill = GridBagConstraints.NONE;
		gridbagconstraints.insets = new Insets(5, 5, 5, 5);

		m_jpSettingsPanel = new ImagePanel();
		m_jpSettingsPanel.setOpaque(true);
		m_jpSettingsPanel.setLayout(new BoxLayout(m_jpSettingsPanel, BoxLayout.Y_AXIS));

		createSettingsPanel(m_jpSettingsPanel);
		createCurvesPanel(m_jpSettingsPanel);
		createGamesPanel(m_jpSettingsPanel);

		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridheight = 2;
		gridbagconstraints.anchor = 18;
		add(m_jpSettingsPanel, gridbagconstraints);
		m_jpGraphics = new TSPanel();
		gridbagconstraints.gridx = 1;
		gridbagconstraints.gridy = 0;
		gridbagconstraints.fill = 1;
		gridbagconstraints.anchor = 11;
		gridbagconstraints.weightx = 1.0D;
		gridbagconstraints.weighty = 1.0D;
		gridbagconstraints.gridheight = -1;
		add(m_jpGraphics, gridbagconstraints);

		initCurves();
	}

	/**
	 * @return true if team is still in cup
	 */
	private boolean isInCup() {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		var matches = dbManager.getMatchesKurzInfo(
				" WHERE ( GastID = ? OR HeimID = ? ) AND MatchTyp = ? AND Status <> ? LIMIT 1", teamId, teamId,MatchType.CUP.getId(),MatchKurzInfo.FINISHED);
		return !matches.isEmpty();
	}

	/**
	 * @return true if team has qualification match scheduled
	 */
	private boolean hasQualificationMatch() {
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		var matches = dbManager.getMatchesKurzInfo(
				" WHERE ( GastID = ? OR HeimID = ?) AND MatchTyp = ? AND Status <> ? LIMIT 1", teamId, teamId, MatchType.QUALIFICATION.getId(), MatchKurzInfo.FINISHED);
		return !matches.isEmpty();
	}

	private void initializeConfig() {
		ModuleConfig config = ModuleConfig.instance();
		if (!config.containsKey(TS_SHOWCUPMATCHES)) {
			config.setBoolean(TS_SHOWQUALIFICATIONMATCH, false);
			config.setBoolean(TS_HISTORY, true);
			config.setBoolean(TS_LOEPIFORECAST, true);
			config.setBoolean(TS_LOEPIHISTORY, false);
			config.setBoolean(TS_CONFIDENCE, true);
			config.setBigDecimal(TS_GENERALSPIRIT, new BigDecimal("4.50"));
			ModuleConfig.instance().save();
		}
		config.setBoolean(TS_SHOWCUPMATCHES, isInCup());
		if (hasQualificationMatch())
			config.setBoolean(TS_SHOWQUALIFICATIONMATCH, true);
	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		Cursor cursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			if (actionevent.getSource() instanceof JRadioButton) {
				int iButton = Integer.parseInt(actionevent.getActionCommand().substring(1));

				switch (actionevent.getActionCommand().charAt(0)) {
				case 80: // 'P'
					m_LoepiForecast.setAttitude(iButton, IMatchDetails.EINSTELLUNG_PIC);
					break;
				case 77: // 'M'
					m_LoepiForecast.setAttitude(iButton, IMatchDetails.EINSTELLUNG_MOTS);
					break;
				case 78: // 'N'
				case 79: // 'O'
				default:
					m_LoepiForecast.setAttitude(iButton, IMatchDetails.EINSTELLUNG_NORMAL);
					break;
				}
				m_jpGraphics.repaint();
			}
		} catch (Exception ex) {
			HOLogger.instance().error(this.getClass(), ex);
		}
		setCursor(cursor);
	}

	@Override
	public void itemStateChanged(ItemEvent itemevent) {
		ModuleConfig config = ModuleConfig.instance();
		boolean selected = itemevent.getStateChange() == ItemEvent.SELECTED;
		if (itemevent.getSource() == m_jtCupMatches) {
			config.setBoolean(TS_SHOWCUPMATCHES, selected);
			createGamesPanel(m_jpSettingsPanel);
		} else if (itemevent.getSource() == m_jtRelegationMatch) {
			config.setBoolean(TS_SHOWQUALIFICATIONMATCH, selected);
			createGamesPanel(m_jpSettingsPanel);
		} else if (itemevent.getSource() == m_jtHistory.getCheckBox()) {
			config.setBoolean(TS_HISTORY, selected);
			if (selected) {
				m_jpGraphics.addCurve(m_History, true);
				m_jpGraphics.addCurve(m_Trainer);
			} else {
				m_jpGraphics.removeCurve(m_History);
				m_jpGraphics.removeCurve(m_Trainer);
			}
		} else if (itemevent.getSource() == m_jtLoepiFore.getCheckBox()) {
			config.setBoolean(TS_LOEPIFORECAST, selected);
			if (selected) {
				m_jpGraphics.addCurve(m_LoepiForecast);
			} else {
				m_jpGraphics.removeCurve(m_LoepiForecast);
			}
		} else if (itemevent.getSource() == m_jtLoepiHist.getCheckBox()) {
			config.setBoolean(TS_LOEPIHISTORY, selected);
			if (selected) {
				m_jpGraphics.addCurve(m_LoepiHist);
			} else {
				m_jpGraphics.removeCurve(m_LoepiHist);
			}
		} else if (itemevent.getSource() == m_jtConfidence.getCheckBox()) {
			config.setBoolean(TS_CONFIDENCE, selected);
			if (selected) {
				m_jpGraphics.addCurve(m_Confidence);
			} else {
				m_jpGraphics.removeCurve(m_Confidence);
			}
		}
		ModuleConfig.instance().save();

		m_jpGraphics.showConfidenceScale(config.getBoolean(TS_CONFIDENCE));

		// check whether it is necessary to draw teamspirit scale
		m_jpGraphics.showTeamspiritScale(config.getBoolean(TS_LOEPIHISTORY) || config.getBoolean(TS_LOEPIFORECAST)
				|| config.getBoolean(TS_HISTORY));

		m_jpGraphics.repaint();
	}

	private void createSettingsPanel(JPanel jpanel) {
		ModuleConfig config = ModuleConfig.instance();
		m_jtCupMatches = new JCheckBox(TranslationFacility.tr("CupMatches"),
				config.getBoolean(TS_SHOWCUPMATCHES));
		m_jtCupMatches.setToolTipText(TranslationFacility.tr("ShowCupMatches"));
		m_jtCupMatches.setAlignmentX(0.0F);
		m_jtCupMatches.addItemListener(this);
		jpanel.add(m_jtCupMatches);

		m_jtRelegationMatch = new JCheckBox(TranslationFacility.tr(
				"ls.match.matchtype.qualification"), config.getBoolean(TS_SHOWQUALIFICATIONMATCH));
		m_jtRelegationMatch.setToolTipText(TranslationFacility.tr("ShowQMatch"));
		m_jtRelegationMatch.setAlignmentX(0.0F);
		m_jtRelegationMatch.addItemListener(this);
		jpanel.add(m_jtRelegationMatch);
	}

	private void createCurvesPanel(JPanel jpanel) {
		ModuleConfig config = ModuleConfig.instance();
		try {
			createCurves();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		m_jtHistory = new CheckBox(TranslationFacility.tr("ls.team.teamspirit"),
				m_History.getColor(), config.getBoolean(TS_HISTORY));
		m_jtHistory.setAlignmentX(0.0F);
		m_jtHistory.addItemListener(this);
		jpanel.add(m_jtHistory);

		m_jtConfidence = new CheckBox(TranslationFacility.tr(
				"ls.team.confidence"), m_Confidence.getColor(), config.getBoolean(TS_CONFIDENCE));
		m_jtConfidence.setAlignmentX(0.0F);
		m_jtConfidence.addItemListener(this);
		jpanel.add(m_jtConfidence);

		m_jtLoepiHist = new CheckBox(TranslationFacility.tr("LoepiCurve"),
				m_LoepiHist.getColor(), config.getBoolean(TS_LOEPIHISTORY));
		m_jtLoepiHist.setAlignmentX(0.0F);
		m_jtLoepiHist.addItemListener(this);
		jpanel.add(m_jtLoepiHist);

		m_jtLoepiFore = new CheckBox(TranslationFacility.tr("TSForecast"),
				m_LoepiForecast.getColor(), config.getBoolean(TS_LOEPIFORECAST));
		m_jtLoepiFore.setAlignmentX(0.0F);
		m_jtLoepiFore.addItemListener(this);
		jpanel.add(m_jtLoepiFore);
	}

	private void createGamesPanel(JPanel jpanel) {
		if (m_jpGamesPanel == null) {
			m_jpGamesPanel = new ImagePanel();
			m_jpGamesPanel.setLayout(new GridBagLayout());
		}
		jpanel.remove(m_jpGamesPanel);
		m_jpGamesPanel.removeAll();

		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		gridbagconstraints.gridwidth = 1;
		gridbagconstraints.anchor = GridBagConstraints.NORTHWEST;

		int iCmdID = 0;

		gridbagconstraints.gridy = 0;
		gridbagconstraints.insets = new Insets(10, 0, 10, 0);

		boolean bshowCupMatches = ModuleConfig.instance().getBoolean(TS_SHOWCUPMATCHES);
		boolean bshowQualMatches = ModuleConfig.instance().getBoolean(TS_SHOWQUALIFICATIONMATCH);
		for (boolean flag = m_LoepiForecast.first() && m_LoepiForecast.next(); flag;) {
			if (m_LoepiForecast.getAttitude() != IMatchDetails.EINSTELLUNG_UNBEKANNT) {
				if (m_LoepiForecast.getMatchType() == MatchType.LEAGUE
						|| (m_LoepiForecast.getMatchType() == MatchType.CUP && bshowCupMatches)
						|| (m_LoepiForecast.getMatchType() == MatchType.QUALIFICATION && bshowQualMatches)) {

					TeamAttitudeEditor teamAttitudeEditor = new TeamAttitudeEditor(m_LoepiForecast.getDate().toLocaleDate(),
							m_LoepiForecast.getTooltip(), iCmdID, m_LoepiForecast.getAttitude(),
							m_LoepiForecast.getMatchType());
					teamAttitudeEditor.addActionListener(this);
					gridbagconstraints.gridy++;
					m_jpGamesPanel.add(teamAttitudeEditor, gridbagconstraints);
				}
				// indicate end of season
				if (m_LoepiForecast.getMatchType() == MatchType.QUALIFICATION) {
					gridbagconstraints.gridy++;
					m_jpGamesPanel.add(
							new JLabel("  "
									+ TranslationFacility.tr("EndOFSeason")),
							gridbagconstraints);
				}
			}
			else if ( m_LoepiForecast.isTrainingUpdate()){
				var trainingIntensityEditor = new TrainingIntensityEditor(m_LoepiForecast);
				trainingIntensityEditor.addChangeListener(e -> {
                    if ( e.getSource() instanceof JSlider slider) {
						if ( slider.getParent() instanceof TrainingIntensityEditor editor ) {
							editor.updatePoint();
							m_jpGraphics.repaint();
						}
					}
                });
				gridbagconstraints.gridy++;
				m_jpGamesPanel.add(trainingIntensityEditor, gridbagconstraints);
			}
			flag = m_LoepiForecast.next();
			iCmdID++;
		}

		m_jpGamesPanel.setAlignmentX(0.0F);
		jpanel.add(m_jpGamesPanel);
		jpanel.revalidate();
	}

	private void createCurves() throws Exception {
		if (m_Trainer != null && m_jpGraphics.removeCurve(m_Trainer)) {
			m_Trainer = new TrainerCurve(dbManager);
			m_jpGraphics.addCurve(m_Trainer);
		} else {
			m_Trainer = new TrainerCurve(dbManager);
		}

		if (m_History != null && m_jpGraphics.removeCurve(m_History)) {
			m_History = new HistoryCurve(dbManager);
			m_jpGraphics.addCurve(m_History, true);
		} else {
			m_History = new HistoryCurve(dbManager);
		}
		m_History.setColor(Color.black);
		m_History.first();
		m_History.next();

		if (m_Confidence != null && m_jpGraphics.removeCurve(m_Confidence)) {
			m_Confidence = new ConfidenceCurve(dbManager);
			m_jpGraphics.addCurve(m_Confidence);
		} else {
			m_Confidence = new ConfidenceCurve(dbManager);
		}
		m_Confidence.setColor(Color.blue);

		if (m_LoepiHist != null && m_jpGraphics.removeCurve(m_LoepiHist)) {
			m_LoepiHist = new LoepiCurve(dbManager, m_Trainer, false);
			m_jpGraphics.addCurve(m_LoepiHist);
		} else {
			m_LoepiHist = new LoepiCurve(dbManager, m_Trainer, false);
		}
		m_LoepiHist.setSpirit(0, m_History.getSpirit());
		m_LoepiHist.setColor(Color.orange);

		if (m_LoepiForecast != null && m_jpGraphics.removeCurve(m_LoepiForecast)) {
			m_LoepiForecast = new LoepiCurve(dbManager, m_Trainer, true);
			m_jpGraphics.addCurve(m_LoepiForecast);
		} else {
			m_LoepiForecast = new LoepiCurve(dbManager, m_Trainer, true);
		}
		m_LoepiForecast.setStartPoint(m_History.getLastPoint());
		m_LoepiForecast.forecast(0);
		m_LoepiForecast.setColor(Color.red);
	}

	private void initCurves() {

		ModuleConfig config = ModuleConfig.instance();

		if (config.getBoolean(TS_HISTORY)) {
			m_jpGraphics.addCurve(m_History, true);
			m_jpGraphics.addCurve(m_Trainer);
		}

		if (config.getBoolean(TS_LOEPIFORECAST)) {
			m_jpGraphics.addCurve(m_LoepiForecast);
		}

		if (config.getBoolean(TS_LOEPIHISTORY)) {
			m_jpGraphics.addCurve(m_LoepiHist);
		}

		if (config.getBoolean(TS_CONFIDENCE)) {
			m_jpGraphics.addCurve(m_Confidence);

		}
	}
}