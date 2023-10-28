/*
 * Created on 23.10.2005
 */
package module.teamAnalyzer.ui;

import core.constants.player.PlayerAbility;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class PlayerInfoPanel extends JPanel {

	private static final long serialVersionUID = 5338615169669058973L;
	protected JLabel ageLabel = new JLabel("");
	protected JLabel expLabel = new JLabel("");
	protected JLabel formLabel = new JLabel("");
	protected JLabel tsiLabel = new JLabel("");

	public PlayerInfoPanel() {
		this.setLayout(new GridBagLayout());

		addInfo(HOVerwaltung.instance().getLanguageString("ls.player.age"), ageLabel, 0, 0);
		addInfo(HOVerwaltung.instance().getLanguageString("ls.player.tsi"), tsiLabel, 0, 1);
		addInfo(HOVerwaltung.instance().getLanguageString("ls.player.form"), formLabel, 1, 0);
		addInfo(HOVerwaltung.instance().getLanguageString("ls.player.short_experience"), expLabel,
				1, 1);
	}

	public void setValue(PlayerInfo oldPlayer) {
		clearData();
		expLabel.setText(PlayerAbility.getNameForSkill(oldPlayer.getExperience(), false));
		tsiLabel.setText("" + oldPlayer.getTSI());
		ageLabel.setText("" + oldPlayer.getAge());
		formLabel.setText(PlayerAbility.getNameForSkill(oldPlayer.getForm(), false));

		PlayerInfo actual = PlayerDataManager.getLatestPlayerInfo(oldPlayer.getPlayerId());

		if (actual.getForm() == 0) {
			return;
		}
	}

	public void clearData() {
		ageLabel.setText("");
		formLabel.setText("");
		expLabel.setText("");
		tsiLabel.setText("");
		expLabel.setIcon(null);
		formLabel.setIcon(null);
		tsiLabel.setToolTipText("");
	}

	private GridBagConstraints getConstraint(int gridx, int gridy, int gridwidth, int gridheight) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = 0.5D;
		c.weighty = 0.5D;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;

		return c;
	}

	private void addInfo(String title, JLabel label, int x, int y) {
		GridBagConstraints c = getConstraint(x, y, 1, 1);

		JLabel titleLabel = new JLabel(title);

		titleLabel.setOpaque(false);
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
		titleLabel.setHorizontalTextPosition(SwingConstants.LEFT);

		label.setOpaque(false);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setHorizontalTextPosition(SwingConstants.RIGHT);

		JPanel panel = new ImagePanel();

		panel.setOpaque(false);
		panel.setLayout(new BorderLayout());
		panel.setBorder(javax.swing.BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		panel.add(titleLabel, BorderLayout.WEST);
		panel.add(label, BorderLayout.CENTER);

		this.add(panel, c);
	}
}
