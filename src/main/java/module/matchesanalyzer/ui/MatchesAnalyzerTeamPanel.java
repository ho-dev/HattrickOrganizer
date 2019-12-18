package module.matchesanalyzer.ui;

import core.model.HOVerwaltung;
import module.matchesanalyzer.data.MatchesAnalyzerLineup;
import module.matchesanalyzer.data.MatchesAnalyzerMatch;
import module.matchesanalyzer.data.MatchesAnalyzerTeam;
import module.matchesanalyzer.ui.cbox.MatchesAnalyzerComboBoxRenderer;
import module.matchesanalyzer.ui.table.MatchesAnalyzerTable;
import module.matchesanalyzer.ui.table.MatchesAnalyzerTableModel;
import module.matchesanalyzer.ui.table.cell.MatchesAnalyzerCellType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class MatchesAnalyzerTeamPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;

	private final static String LOC_LEAGUE = HOVerwaltung.instance().getLanguageString("matchesanalyzer.league");
	private final static String LOC_CUP = HOVerwaltung.instance().getLanguageString("matchesanalyzer.cup");
	private final static String LOC_FRIENDLY = HOVerwaltung.instance().getLanguageString("matchesanalyzer.friendly");
	private final static String LOC_UNOFFICIAL = HOVerwaltung.instance().getLanguageString("matchesanalyzer.unofficial");
	private final static String LOC_NATIONALTEAM = HOVerwaltung.instance().getLanguageString("matchesanalyzer.national_team");
	
	private class ObservableImpl extends Observable {
		@Override
		public synchronized void setChanged() {
			super.setChanged();
		}
	}
	
	private final ObservableImpl observable;

	private GridBagLayout loPanel;
	private JComboBox cbxTeam;
	private JCheckBox chkLeague;
	private JCheckBox chkCup;
	private JCheckBox chkFriendly;
	private JCheckBox chkUnofficial;
	private JCheckBox chkNationalTeam;
	private MatchesAnalyzerTable tblMatches;
	
	private final int orientation;
	private final MatchesAnalyzerTeam team;

	public MatchesAnalyzerTeamPanel(MatchesAnalyzerTeam team, int orientation) {
		this.orientation = orientation;
		this.team = team;

		// people wants cbox always on top of the table
		orientation = MatchesAnalyzerPanel.TOPDOWN;
		
		int size = 21;
		for(MatchesAnalyzerCellType type : MatchesAnalyzerCellType.values()) {
			size += type.getStyle().getWidth();
		}

		loPanel = new GridBagLayout();
		loPanel.columnWidths = new int[] {0, 0, 0, 0, 0};
		loPanel.rowHeights = new int[] {0, 0, 0};
		loPanel.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		loPanel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		setLayout(loPanel);

		cbxTeam = new JComboBox();
		cbxTeam.setRenderer(new MatchesAnalyzerComboBoxRenderer());
		cbxTeam.setPreferredSize(new Dimension(size, 25));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 5, 5 * orientation, 5);
		constraints.gridx = 0;
		constraints.gridy = orientation;
		add(cbxTeam, constraints);

		chkLeague = new JCheckBox(LOC_LEAGUE);
		constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 0, 5 * orientation, 5);
		constraints.gridx = 1;
		constraints.gridy = orientation;
		add(chkLeague, constraints);

		chkCup = new JCheckBox(LOC_CUP);
		constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 0, 5 * orientation, 5);
		constraints.gridx = 2;
		constraints.gridy = orientation;
		add(chkCup, constraints);

		chkFriendly = new JCheckBox(LOC_FRIENDLY);
		constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 0, 5 * orientation, 5);
		constraints.gridx = 3;
		constraints.gridy = orientation;
		add(chkFriendly, constraints);
		
		chkUnofficial = new JCheckBox(LOC_UNOFFICIAL);
		constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 0, 5 * orientation, 5);
		constraints.gridx = 4;
		constraints.gridy = orientation;
		add(chkUnofficial, constraints);
		
		chkNationalTeam = new JCheckBox(LOC_NATIONALTEAM);
		constraints = new GridBagConstraints();
		constraints.weighty = 0.0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5 * ((orientation + 1) % 2), 0, 5 * orientation, 5);
		constraints.gridx = 5;
		constraints.gridy = orientation;
		add(chkNationalTeam, constraints);

		tblMatches = new MatchesAnalyzerTable();
		JScrollPane lscMatches = new JScrollPane(tblMatches, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lscMatches.setMinimumSize(new Dimension(size, Integer.MAX_VALUE));
		lscMatches.setMaximumSize(new Dimension(size, Integer.MAX_VALUE));
		constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 0);
		constraints.gridwidth = 6;
		constraints.weighty = 1.0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = (orientation + 1) % 2;
		add(lscMatches, constraints);

		updateTeamsList();
		
		observable = new ObservableImpl();
		
		chkLeague.setSelected(true);
		chkCup.setSelected(true);
		chkFriendly.setSelected(true);
		chkUnofficial.setSelected(true);
		chkNationalTeam.setSelected(true);
		
		cbxTeam.addActionListener(this);
		chkLeague.addItemListener(this);
		chkCup.addItemListener(this);
		chkFriendly.addItemListener(this);
		tblMatches.getSelectionModel().addListSelectionListener(this);

		updateMatchesTable();
	}

	private void updateTeamsList() {
		List<MatchesAnalyzerTeam> teams = team.getTeams();

		int i = -1;
		cbxTeam.removeAll();
		for(MatchesAnalyzerTeam t : teams) {
			cbxTeam.addItem(t);
			if((orientation == MatchesAnalyzerPanel.TOPDOWN && t.isMine()) || (orientation == MatchesAnalyzerPanel.BOTTOMUP && t.isNext())) {
				i = cbxTeam.getItemCount();
			}
		}

		if(i >= 0) {
			cbxTeam.setSelectedIndex(i - 1);
		}
	}

	private void updateMatchesTable() {
		MatchesAnalyzerTeam team = (MatchesAnalyzerTeam)cbxTeam.getSelectedItem();
		if(team != null) {
			MatchesAnalyzerTableModel model = (MatchesAnalyzerTableModel)tblMatches.getModel();
			model.updateTable(team.getMatches(chkLeague.isSelected(), chkCup.isSelected(), chkFriendly.isSelected(), chkUnofficial.isSelected(), chkNationalTeam.isSelected()));
			model.fireTableDataChanged();
			if(tblMatches.getRowCount() > 0) tblMatches.setRowSelectionInterval(0, 0);
			tblMatches.invalidate();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateMatchesTable();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		updateMatchesTable();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting()) return;
		updateLineup();
	}
	
	private void updateLineup() {
		MatchesAnalyzerLineup lineup = null;
		ListSelectionModel sModel = tblMatches.getSelectionModel();
		if(!sModel.isSelectionEmpty()) {
			int row = sModel.getMinSelectionIndex();
			MatchesAnalyzerTableModel tModel = (MatchesAnalyzerTableModel)tblMatches.getModel();
			MatchesAnalyzerMatch match = tModel.getRows().get(row);
			lineup = match.getLineup();
		}
		observable.setChanged();
		observable.notifyObservers(lineup);
	}

	public MatchesAnalyzerTeam getTeam() {
		return((MatchesAnalyzerTeam)cbxTeam.getSelectedItem());
	}

	public void addObserver(Observer observer) {
		observable.addObserver(observer);
		updateLineup();
	}

}
