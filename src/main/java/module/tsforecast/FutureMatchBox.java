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

final class FutureMatchBox extends ImagePanel {

	private JRadioButton m_rbPIC;
	private JRadioButton m_rbNORM;
	private JRadioButton m_rbMOTS;

	public FutureMatchBox(String text, String tooltip, int iCmd, int iSelected, MatchType iType) {
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
		setLayout(gridbaglayout);
		gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;

		gridbagconstraints.gridx = 0;
		add(m_rbPIC, gridbagconstraints);

		gridbagconstraints.gridx = 1;
		add(m_rbNORM, gridbagconstraints);

		gridbagconstraints.gridx = 2;
		add(m_rbMOTS, gridbagconstraints);

		gridbagconstraints.gridx = 3;
		JLabel lIcon = new JLabel(ThemeManager.getIcon(HOIconName.MATCHICONS[iType.getIconArrayIndex()]));
		lIcon.setToolTipText(iType.getName());
		add(lIcon, gridbagconstraints);

		gridbagconstraints.gridx = 4;
		JLabel lText = new JLabel("  " + text + " ", SwingConstants.LEFT);
		lText.setToolTipText(tooltip);
		add(lText, gridbagconstraints);
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