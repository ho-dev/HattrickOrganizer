package module.nthrf;

import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class NtTeamChooser extends JDialog implements ActionListener {

	private long selectedTeamId = -1;

	public NtTeamChooser(List<String[]> teams) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		add(new JLabel(TranslationFacility.tr("Favourite.SelectTeam")), BorderLayout.NORTH);
		JPanel teamPanel = new JPanel();
		for (String[] t : teams) {
			JButton btn = new JButton(t[1] + " (" + t[0] + ")");
			btn.addActionListener(this);
			teamPanel.add(btn);
		}
		add(teamPanel, BorderLayout.CENTER);
		setMinimumSize(new Dimension(220, 180));
	}

	public void actionPerformed(ActionEvent ev) {
		if (ev != null && ev.getSource() instanceof JButton) {
			String btnTxt = ((JButton)ev.getSource()).getText();
			System.out.println("btnTxt: " + btnTxt);
			btnTxt = btnTxt.substring(btnTxt.lastIndexOf("(")+1, btnTxt.lastIndexOf(")"));
			selectedTeamId = Long.parseLong(btnTxt);
			System.out.println("selectedTeamId: " + selectedTeamId);
			this.dispose();
		}
	}

	public long getSelectedTeamId() {
		return selectedTeamId;
	}

	/**
	 * @param args
	 *//*
	public static void main(String[] args) {
		var teams = new ArrayList<String[]>();
		teams.add(new String[]{"526156", "Club Team"});
		teams.add(new String[]{"3216", "National Team"});
		NtTeamChooser chooser = new NtTeamChooser(teams);
		chooser.setModal(true);
		chooser.setVisible(true);
		//JOptionPane.showMessageDialog(new JLabel("test"), chooser, "Choose team", JOptionPane.QUESTION_MESSAGE);
		System.out.println("Result is: " + chooser.getSelectedTeamId());
		chooser.dispose();
	}*/
}
