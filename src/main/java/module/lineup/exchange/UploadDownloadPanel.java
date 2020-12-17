package module.lineup.exchange;

import core.db.DBManager;
import core.gui.CursorToolkit;
import core.gui.RefreshManager;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.renderer.DateTimeTableCellRenderer;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.Ratings;
import core.model.match.IMatchDetails;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchType;
import core.model.match.Weather;
import core.net.OnlineWorker;
import core.util.GUIUtils;
import core.util.HOLogger;
import core.util.XMLUtils;
import module.lineup.Lineup;
import module.lineup.ratings.RatingComparisonDialog;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import module.teamAnalyzer.vo.MatchRating;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static core.gui.HOMainFrame.instance;

public class UploadDownloadPanel extends LazyPanel {

	private static final long serialVersionUID = -5314050322847463180L;
	private JTable matchesTable;
	private JButton uploadButton;
	private JButton downloadButton;
	private JButton refreshButton;
	private JButton getRatingsPredictionButton;
	private boolean supporter;

	@Override
	protected void initialize() {
		this.supporter = HOVerwaltung.instance().getModel().getBasics().isHasSupporter();
		initComponents();
		addListeners();
		setNeedsRefresh(true);
		registerRefreshable(true);
	}

	@Override
	protected void update() {
		((MatchesTableModel) this.matchesTable.getModel()).setData(getMatchesFromDB());
	}
	
	private void initComponents() {
		this.refreshButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.refresh"));
		this.refreshButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.refresh.tooltip"));
		this.downloadButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.download"));
		this.downloadButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.download.tooltip"));
		this.downloadButton.setEnabled(false);
		this.getRatingsPredictionButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"lineup.getRatingsPrediction.btn.label"));
		this.getRatingsPredictionButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"lineup.getRatingsPrediction.btn.tooltip"));
		this.getRatingsPredictionButton.setEnabled(false);
		this.uploadButton = new JButton(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.upload"));
		this.uploadButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"lineup.upload.btn.upload.tooltip"));
		this.uploadButton.setEnabled(false);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 8, 4, 10);
		buttonPanel.add(this.refreshButton, gbc);

		gbc.gridy = 1;
		gbc.insets = new Insets(4, 8, 4, 10);
		buttonPanel.add(this.uploadButton, gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(4, 8, 10, 10);
		gbc.weightx = 1.0;
		buttonPanel.add(this.downloadButton, gbc);

		gbc.gridy = 3;
		gbc.insets = new Insets(4, 8, 10, 10);
		gbc.weightx = 1.0;
		buttonPanel.add(this.getRatingsPredictionButton, gbc);

		GUIUtils.equalizeComponentSizes(this.refreshButton, this.uploadButton, this.downloadButton, this.getRatingsPredictionButton);

		MatchesTableModel model = new MatchesTableModel();
		this.matchesTable = new JTable();
		this.matchesTable.setModel(model);
		this.matchesTable.setAutoCreateRowSorter(true);
		this.matchesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// as default, sort by date
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		this.matchesTable.getRowSorter().setSortKeys(sortKeys);

		// TODO use column identifiers instead of index
		TableColumn dateColumn = this.matchesTable.getColumnModel().getColumn(0);
		dateColumn.setCellRenderer(new DateTimeTableCellRenderer());

		// TODO use column identifiers instead of index
		TableColumn matchTypeColumn = this.matchesTable.getColumnModel().getColumn(1);
		matchTypeColumn.setCellRenderer(new MatchTypeCellRenderer());
		matchTypeColumn.setMaxWidth(25);

		// TODO use column identifiers instead of index
		TableColumn ordersSetColumn = this.matchesTable.getColumnModel().getColumn(5);
		ordersSetColumn.setCellRenderer(new OrdersSetCellRenderer());
		ordersSetColumn.setMaxWidth(25);

		setLayout(new GridBagLayout());
		if (!this.supporter) {
			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.anchor = GridBagConstraints.NORTHWEST;
			JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString(
					"lineup.upload.noSupporter"));
			label.setFont(label.getFont().deriveFont(label.getFont().getStyle() ^ Font.BOLD));
			add(label, gbc);
		}

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new JScrollPane(this.matchesTable), gbc);

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.NORTH;
		add(buttonPanel, gbc);
	}

	private List<MatchKurzInfo> getMatchesFromDB() {
		MatchKurzInfo[] matches = DBManager.instance().getMatchesKurzInfo(
				HOVerwaltung.instance().getModel().getBasics().getTeamId());

		Timestamp today = new Timestamp(System.currentTimeMillis());
		List<MatchKurzInfo> data = new ArrayList<MatchKurzInfo>();
		for (MatchKurzInfo match : matches) {
			if (match.getMatchDateAsTimestamp().after(today)) {
				data.add(match);
			}
		}
		return data;
	}

	private void getRatingsPrediction()
	{
		MatchRating HTmatchRating;

		CursorToolkit.startWaitCursor(this);
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo match = getSelectedMatch();
		try {
			HTmatchRating = OnlineWorker.getPredictionRatingbyMatchId(match.getMatchID(), match.getMatchTyp(), teamId);
			}
         finally {
			CursorToolkit.stopWaitCursor(this);
		 }

		Lineup currLineup =  HOVerwaltung.instance().getModel().getLineup();
		download(false);

		Ratings oRatings = HOVerwaltung.instance().getModel().getLineup().getRatings();
		double LD = oRatings.getLeftDefense().get(0d);
		double CD = oRatings.getCentralDefense().get(0d);
		double RD = oRatings.getRightDefense().get(0d);
		double MF = oRatings.getMidfield().get(0d);
		double LA = oRatings.getLeftAttack().get(0d);
		double CA = oRatings.getCentralAttack().get(0d);
		double RA = oRatings.getRightAttack().get(0d);
		int tacticType = 0;
		int tacticSkill = 0;

		MatchRating HOmatchRating = new MatchRating(LD, CD, RD, MF, LA, CA, RA, tacticType, tacticSkill);

		// restore previous Lineup
		HOVerwaltung.instance().getModel().setLineup(currLineup);
		instance().getLineupPanel().update();

		new RatingComparisonDialog(HOmatchRating, HTmatchRating);

		return;

	}

	private void upload() {
		MatchKurzInfo match = getSelectedMatch();
		Lineup lineup = HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc();
		if (!LineupCheck.doUpload(match, lineup)) {
			return;
		}

		String result = null;
		CursorToolkit.startWaitCursor(this);
		try {
			result = OnlineWorker.uploadMatchOrder(match.getMatchID(), match.getMatchTyp(), lineup);
		} finally {
			CursorToolkit.stopWaitCursor(this);
		}

		int messageType;
		boolean success = false;
		String message;
		try {
			Document doc = XMLUtils.createDocument(result);
			String successStr = XMLUtils.getAttributeValueFromNode(doc, "MatchData", "OrdersSet");
			if (successStr != null) {
				success = Boolean.parseBoolean(successStr);
				if (success) {
					messageType = JOptionPane.PLAIN_MESSAGE;
					message = HOVerwaltung.instance().getLanguageString("lineup.upload.success");
				} else {
					messageType = JOptionPane.ERROR_MESSAGE;
					message = HOVerwaltung.instance().getLanguageString("lineup.upload.fail")
							+ "\n" + XMLUtils.getTagData(doc, "Reason");
				}
			} else {
				messageType = JOptionPane.ERROR_MESSAGE;
				message = HOVerwaltung.instance().getLanguageString(
						"lineup.upload.result.parseerror");
				HOLogger.instance().log(UploadDownloadPanel.class, message + "\n" + result);
			}
		} catch (SAXException e) {
			messageType = JOptionPane.ERROR_MESSAGE;
			message = HOVerwaltung.instance().getLanguageString("lineup.upload.result.parseerror");
			HOLogger.instance().log(UploadDownloadPanel.class, message + "\n" + result);
			HOLogger.instance().log(UploadDownloadPanel.class, e);
		}

		if (success) {
			MatchKurzInfo refreshed;
			CursorToolkit.startWaitCursor(this);
			try {
				refreshed = OnlineWorker.updateMatch(HOVerwaltung.instance().getModel().getBasics()
						.getTeamId(), match);
				if (refreshed != null) {
					match.merge(refreshed);
					((MatchesTableModel) this.matchesTable.getModel()).fireTableDataChanged();
					selectMatch(match);
				}
			} finally {
				CursorToolkit.stopWaitCursor(this);
			}
		}

		JOptionPane.showMessageDialog(instance(), message, HOVerwaltung.instance()
				.getLanguageString("lineup.upload.title"), messageType);
	}


	private void download(boolean showDialog) {
		download(showDialog, Weather.NULL);
	}

	private void download(boolean showDialog, Weather weather) {
		Lineup lineup;
		CursorToolkit.startWaitCursor(this);
		int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		MatchKurzInfo match = getSelectedMatch();
		try {			
			lineup = OnlineWorker.getLineupbyMatchId(match.getMatchID(), match.getMatchTyp());
			MatchKurzInfo refreshed = OnlineWorker.updateMatch(teamId, match);
			if (refreshed != null) {
				match.merge(refreshed);
				((MatchesTableModel) this.matchesTable.getModel()).fireTableDataChanged();
				selectMatch(match);
			}
			DBManager.instance().updateMatchOrder(lineup, match.getMatchID());
		}
		finally {
			CursorToolkit.stopWaitCursor(this);
		}
		if (lineup != null) {
			int messageType = JOptionPane.PLAIN_MESSAGE;
			if (match.getHeimID() == teamId) {
				lineup.setLocation(IMatchDetails.LOCATION_HOME);
			}
			else {
				lineup.setLocation(IMatchDetails.LOCATION_AWAY);
			}


			// in case of tournament match, set location neither to home or away but to special tournament settings
			if (match.getMatchTyp().isTournament()){
				lineup.setLocation(IMatchDetails.LOCATION_TOURNAMENT);
			    }

			// weather
			if (weather == Weather.NULL) instance().getLineupPanel().getLineupSettingsPanel().setWeather(Weather.PARTIALLY_CLOUDY);

			RefreshManager.instance().doRefresh();

			if (showDialog) {
			JOptionPane.showMessageDialog(instance(),  HOVerwaltung.instance().getLanguageString("lineup.download.success"), HOVerwaltung.instance()
					.getLanguageString("lineup.download.title"), messageType);}
			HOVerwaltung.instance().getModel().setLineup(lineup);
			instance().getLineupPanel().update();
		}
	}

	private void refreshMatchListFromHT() {
		OnlineWorker.getMatches(HOVerwaltung.instance().getModel().getBasics().getTeamId(), true,
				true, true);
		((MatchesTableModel) this.matchesTable.getModel()).setData(getMatchesFromDB());
	}

	private MatchKurzInfo getSelectedMatch() {
		int tableIndex = this.matchesTable.getSelectedRow();
		if (tableIndex != -1) {
			int modelIndex = this.matchesTable.convertRowIndexToModel(tableIndex);
			return ((MatchesTableModel) this.matchesTable.getModel()).getMatch(modelIndex);
		}
		return null;
	}

	private void selectMatch(MatchKurzInfo match) {
		MatchesTableModel model = (MatchesTableModel) this.matchesTable.getModel();
		int modelIndex = model.getRowIndex(match);
		int viewIndex = this.matchesTable.convertRowIndexToView(modelIndex);
		this.matchesTable.getSelectionModel().setSelectionInterval(viewIndex, viewIndex);
	}

	private void addListeners() {
		this.uploadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				upload();
			}
		});

		this.refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshMatchListFromHT();
			}
		});

		this.downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				download(true);
			}
		});

		this.getRatingsPredictionButton.addActionListener(e -> getRatingsPrediction());

		this.matchesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					boolean ordersSet = false;
					int tableIndex = matchesTable.getSelectedRow();
					boolean enableButtons = tableIndex != -1;
					if (enableButtons) {
						int modelIndex = matchesTable.convertRowIndexToModel(tableIndex);
						ordersSet = ((MatchesTableModel) matchesTable.getModel()).getMatch(modelIndex).isOrdersGiven();
					}
					uploadButton.setEnabled(supporter && enableButtons);
					downloadButton.setEnabled(enableButtons);
					getRatingsPredictionButton.setEnabled(ordersSet && enableButtons);
				}
			}
		});
	}

	private class MatchesTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 7917970964575188677L;
		private List<MatchKurzInfo> data;
		private String[] columns;

		public MatchesTableModel() {
			initColumnNames();
			this.data = new ArrayList<MatchKurzInfo>();
		}

		public MatchesTableModel(List<MatchKurzInfo> list) {
			initColumnNames();
			this.data = new ArrayList<MatchKurzInfo>(list);
		}

		public MatchKurzInfo getMatch(int modelRowIndex) {
			return this.data.get(modelRowIndex);
		}

		public int getRowIndex(MatchKurzInfo match) {
			return this.data.indexOf(match);
		}

		public void setData(List<MatchKurzInfo> list) {
			this.data = new ArrayList<MatchKurzInfo>(list);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MatchKurzInfo match = this.data.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return match.getMatchDateAsTimestamp();
			case 1:
				return match.getMatchTyp();
			case 2:
				return match.getHeimName();
			case 3:
				return match.getGastName();
			case 4:
				return match.getMatchID();
			case 5:
				return match.isOrdersGiven();
			default:
				return null;
			}
		}

		private void initColumnNames() {
			this.columns = new String[6];
			this.columns[0] = HOVerwaltung.instance().getLanguageString(
					"Datum");
			this.columns[1] = "";
			this.columns[2] = HOVerwaltung.instance().getLanguageString(
					"lineup.upload.colheadline.hometeam");
			this.columns[3] = HOVerwaltung.instance().getLanguageString(
					"lineup.upload.colheadline.awayteam");
			this.columns[4] = HOVerwaltung.instance().getLanguageString(
					"ls.match.id");
			this.columns[5] = "";
		}
	}

	private class MatchTypeCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6068887874289410058L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel component = (JLabel) super.getTableCellRendererComponent(table, value,
					isSelected, hasFocus, row, column);
			component.setText(null);
			MatchType type = (MatchType) value;
			Icon icon = ThemeManager.getIcon(HOIconName.MATCHICONS[type.getIconArrayIndex()]);
			component.setIcon(icon);
			return component;
		}
	}

	private class OrdersSetCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -6068887874289410058L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel component = (JLabel) super.getTableCellRendererComponent(table, "", isSelected,
					hasFocus, row, column);
			if ((Boolean) value) {
				component.setIcon(null);
			} else {
				component.setIcon(null);
			}
			return component;
		}
	}
}
