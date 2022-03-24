package core.file.xml;

import core.datatype.CBItem;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

public class TeamSelectionDialog extends JDialog{
	
	private JComboBox teamComboBox;
	private JButton m_jbOK = new JButton();
	private JButton m_jbCancel = new JButton();
	
	private JLabel teamName = new JLabel("");
	private JLabel teamCountry = new JLabel("");
	private JLabel teamSeries = new JLabel("");
	
	private boolean cancel = false;

	private List<TeamInfo> infos;
	
	public TeamSelectionDialog(HOMainFrame mainFrame, List<TeamInfo> infos) {
		super(mainFrame, HOVerwaltung.instance().getLanguageString("teamSelect.header"), true);
		this.infos = infos;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		init(mainFrame);
		addListeners();
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	
	private void init(HOMainFrame mainFrame) {

		setContentPane(new ImagePanel(new FlowLayout()));

		var cbItems = new ArrayList<CBItem>();
		int i = 0;
		for (var info : infos) {
			cbItems.add(new CBItem(info.getName(), i++));
		}

		teamComboBox = new JComboBox(cbItems.toArray());
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(300, 200));
		mainPanel.add(teamComboBox, BorderLayout.NORTH);

		JPanel selectedPanel = new JPanel();
		selectedPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);

		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.teamName")), gbc);
		gbc.gridx = 1;
		selectedPanel.add(teamName, gbc);
		gbc.gridx=0;
		gbc.gridy++;
		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.series")),gbc);
		gbc.gridx=1;
		selectedPanel.add(teamSeries, gbc);
		gbc.gridx=0;
		gbc.gridy++;
		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.country")), gbc);
		gbc.gridx=1;
		selectedPanel.add(teamCountry, gbc);
		initTeam(0);

		mainPanel.add(selectedPanel, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		m_jbOK.setText(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		buttonPanel.add(m_jbOK);
		m_jbCancel.setText(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		buttonPanel.add(m_jbCancel);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.getContentPane().add(mainPanel);

		Dimension size = mainFrame.getToolkit().getScreenSize();
		if (size.width > this.getSize().width) { // open dialog in the middle of the screen
			this.setLocation((size.width / 2) - (this.getSize().width / 2), (size.height / 2) - (this.getSize().height / 2));
		}
		pack();
	}
	
	private void addListeners() {
		teamComboBox.addActionListener(e -> {
			// team changed
			initTeam(teamComboBox.getSelectedIndex());
		});

		m_jbOK.addActionListener(e->{
			var i = teamComboBox.getSelectedIndex();
			if ( i >= 0) {
				setVisible(false);
			}
			else {
				JOptionPane.showMessageDialog(null, HOVerwaltung.instance()
								.getLanguageString("teamSelect.doChoose"), HOVerwaltung
								.instance().getLanguageString("teamSelect.doChooseHeader"),
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		m_jbCancel.addActionListener(e->{
			cancel = true;
			dispose();
		});
	}

	private void initTeam(int i) {
		if (i < 0) return;
		var info = infos.get(i);
		teamCountry.setText(info.getCountry());
		teamSeries.setText(info.getLeague());
		teamName.setText(info.getName());
	}

	public TeamInfo getSelectedTeam() {
		var i = teamComboBox.getSelectedIndex();
		if ( i >= 0) {
			return infos.get(i);
		}
		return null;
	}
	
	public boolean getCancel() {
		return cancel;
	}
}
