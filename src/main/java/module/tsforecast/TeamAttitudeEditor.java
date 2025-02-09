package module.tsforecast;

/*
 * FutureMatchBox.java
 *
 * Created on 25.March 2006, 22:04
 *
 *Version 0.11
 *history :
 *25.03.06  Version 0.1
 *26.08.06  Version 0.11 rebuilt
 *21.02.07  Version 0.2  added tooltip
 */

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.model.enums.MatchType;
import core.model.match.IMatchDetails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

final class TeamAttitudeEditor extends ImagePanel {

	private JRadioButton m_rbPIC;
	private JRadioButton m_rbNORM;
	private JRadioButton m_rbMOTS;

	public TeamAttitudeEditor(String matchDate, String tooltip, int iCmd, int iSelected, MatchType iType) {
		super(new BorderLayout());
		var label  = new JLabel( TranslationFacility.tr("ls.team.teamattitude") + " "+ matchDate);
		label.setToolTipText(tooltip);
		this.add( label, BorderLayout.NORTH);

		m_rbPIC = new JRadioButton();
		m_rbPIC.setActionCommand("P" + iCmd);
		m_rbPIC.setToolTipText(TranslationFacility.tr("ls.team.teamattitude.playitcool"));

		m_rbNORM = new JRadioButton();
		m_rbNORM.setActionCommand("N" + iCmd);
		m_rbNORM.setToolTipText(TranslationFacility.tr(
				"ls.team.teamattitude.normal"));

		m_rbMOTS = new JRadioButton();
		m_rbMOTS.setActionCommand("M" + iCmd);
		m_rbMOTS.setToolTipText(TranslationFacility.tr(
				"ls.team.teamattitude.matchoftheseason"));

		ButtonGroup buttongroup = new ButtonGroup();
		buttongroup.add(m_rbPIC);
		buttongroup.add(m_rbNORM);
		buttongroup.add(m_rbMOTS);
		setSelected(iSelected);

		GridBagLayout gridbaglayout = new GridBagLayout();
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		var gridPanel = new JPanel(gridbaglayout);
		gridbagconstraints.insets = new Insets(0, 5, 0, 5);
		gridbagconstraints.anchor = GridBagConstraints.CENTER;
		gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;

		gridbagconstraints.gridy = 0;
		gridbagconstraints.gridx = 0;
		gridPanel.add(m_rbPIC, gridbagconstraints);

		gridbagconstraints.gridx = 1;
		gridPanel.add(m_rbNORM, gridbagconstraints);

		gridbagconstraints.gridx = 2;
		gridPanel.add(m_rbMOTS, gridbagconstraints);

		gridbagconstraints.gridx = 3;
		JLabel lIcon = new JLabel(ThemeManager.getIcon(HOIconName.MATCHICONS[iType.getIconArrayIndex()]));
		lIcon.setToolTipText(iType.getName());
		gridPanel.add(lIcon, gridbagconstraints);

		gridbagconstraints.gridy = 1;
		gridbagconstraints.gridx = 0;
		gridPanel.add(new JLabel("PIC", null, SwingConstants.CENTER), gridbagconstraints);
		gridbagconstraints.gridx++;
		gridPanel.add(new JLabel("N", null, SwingConstants.CENTER), gridbagconstraints);
		gridbagconstraints.gridx++;
		gridPanel.add(new JLabel("MOTS", null, SwingConstants.CENTER), gridbagconstraints);

		this.add(gridPanel, BorderLayout.CENTER);
	}

	public void setSelected(int i) {
		switch (i) {
		case IMatchDetails.EINSTELLUNG_MOTS:
			m_rbMOTS.setSelected(true);
			break;
		case IMatchDetails.EINSTELLUNG_PIC:
			m_rbPIC.setSelected(true);
			break;
		case IMatchDetails.EINSTELLUNG_NORMAL: // '\0'
		default:
			m_rbNORM.setSelected(true);
			break;
		}
	}

	public void addActionListener(ActionListener actionlistener) {
		m_rbPIC.addActionListener(actionlistener);
		m_rbNORM.addActionListener(actionlistener);
		m_rbMOTS.addActionListener(actionlistener);
	}

}