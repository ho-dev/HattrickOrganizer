package core.file.xml;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class TeamSelectionDialog extends JDialog{
	
	private static final long serialVersionUID = 1L;

	private TeamRadioButton team1;
	private TeamRadioButton team2;
	private TeamRadioButton team3;
	private TeamRadioButton selected;
	
	private JButton m_jbOK = new JButton();
	private JButton m_jbCancel = new JButton();
	
	private JLabel teamName = new JLabel("");
	private JLabel teamCountry = new JLabel("");
	private JLabel teamSeries = new JLabel("");
	
	private boolean cancel = false;
	
	public TeamSelectionDialog(HOMainFrame mainFrame, List<TeamInfo> infos) {
			super(mainFrame, HOVerwaltung.instance().getLanguageString("teamSelect.header"), true);
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		init(mainFrame, infos);
		addListeners();
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}
	
	private void init(HOMainFrame mainFrame, List<TeamInfo> infos) {
		
		setContentPane(new ImagePanel(new FlowLayout()));
			
		ButtonGroup group = new ButtonGroup();
		team1 = new TeamRadioButton(infos.get(0));
		team2 = new TeamRadioButton(infos.get(1));
		group.add(team1);
		group.add(team2);

		if (infos.size() > 2) {
			team3 = new TeamRadioButton(infos.get(2));
			group.add(team3);
		}
		
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BorderLayout());
		int xDimension = 370;
		if (team3 != null) {
			xDimension = 560;
		}
		mainPanel.setSize(xDimension, 470);
		
		JPanel upperPanel = new JPanel();
		upperPanel.setLayout(new GridLayout(1,0));
		upperPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		upperPanel.add(team1);
		upperPanel.add(team2);
		if (team3 != null) {
			upperPanel.add(team3);
		}
		
		mainPanel.add(upperPanel, BorderLayout.NORTH);
		
		JPanel selectedPanel = new JPanel();
		selectedPanel.setLayout(new GridLayout(0,2));
		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.teamName")));
		selectedPanel.add(teamName);
		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.series")));
		selectedPanel.add(teamSeries);
		selectedPanel.add(new JLabel(HOVerwaltung.instance().getLanguageString("teamSelect.country")));
		selectedPanel.add(teamCountry);
	
		mainPanel.add(selectedPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		m_jbOK.setText(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		buttonPanel.add(m_jbOK);
		
		m_jbCancel.setText(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		buttonPanel.add(m_jbCancel);
		
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		this.getContentPane().add(mainPanel);
		this.setSize(400, 500);
		
		Dimension size = mainFrame.getToolkit().getScreenSize();
		if (size.width > this.getSize().width) { // open dialog in the middle of
													// the screen
			this.setLocation((size.width / 2) - (this.getSize().width / 2),
					(size.height / 2) - (this.getSize().height / 2));
		}

		pack();
	}
	
	private void addListeners() {
		ActionListener actionListener = new ActionListener() {

			@Override
			public final void actionPerformed(ActionEvent event) {

				if ((event.getSource() == team1) || (event.getSource() == team2) || ((team3 != null) && (event.getSource() == team3))) {
					TeamRadioButton source = (TeamRadioButton) event.getSource();
					if (source.isSelected()) {
						selected = source;
						TeamInfo info = source.getTeamInfo();
						teamCountry.setText(info.getCountry());
						teamSeries.setText(info.getLeague());
						teamName.setText(info.getName());
					}
				
				} else if (event.getSource()== m_jbOK) {
					
					if (selected != null) {
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, HOVerwaltung.instance()
								.getLanguageString("teamSelect.doChoose"), HOVerwaltung
								.instance().getLanguageString("teamSelect.doChooseHeader"),
								JOptionPane.INFORMATION_MESSAGE);
					}
					
				} else if (event.getSource() == m_jbCancel) {
					cancel = true;
					dispose();
				}
			}
		};
		
		team1.addActionListener(actionListener);
		team2.addActionListener(actionListener);
		if (team3 != null) {
			team3.addActionListener(actionListener);
		}
		
		
		m_jbOK.addActionListener(actionListener);
		m_jbCancel.addActionListener(actionListener);

	}
	
	
	
	private class TeamRadioButton extends JRadioButton {
		private static final long serialVersionUID = 1L;
		
		TeamInfo team = null;
		
		private TeamRadioButton (TeamInfo info) {
			super(info.getName());
			team = info;
		}
		
		protected TeamInfo getTeamInfo() {
			return team;
		}
			
	}

	
	public TeamInfo getSelectedTeam() {
		return selected.getTeamInfo();
	}
	
	public boolean getCancel() {
		return cancel;
	}
}
