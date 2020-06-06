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

/**
 *
 * @author  michael.roux
 */

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.MatchType;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

final class FutureMatchBox extends ImagePanel {

	private static final long serialVersionUID = 1L;
	private JRadioButton m_rbPIC = null;
	private JRadioButton m_rbNORM = null;
	private JRadioButton m_rbMOTS = null;

	public FutureMatchBox(String text, String tooltip, int iCmd, int iSelected, MatchType iType) {
		m_rbPIC = new JRadioButton();
		m_rbPIC.setActionCommand("P" + iCmd);
		m_rbPIC.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude.playitcool"));

		m_rbNORM = new JRadioButton();
		m_rbNORM.setActionCommand("N" + iCmd);
		m_rbNORM.setToolTipText(HOVerwaltung.instance().getLanguageString(
				"ls.team.teamattitude.normal"));

		m_rbMOTS = new JRadioButton();
		m_rbMOTS.setActionCommand("M" + iCmd);
		m_rbMOTS.setToolTipText(HOVerwaltung.instance().getLanguageString(
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

	public final int isSelected() {
		if (m_rbMOTS.isSelected())
			return IMatchDetails.EINSTELLUNG_MOTS;
		else if (m_rbPIC.isSelected())
			return IMatchDetails.EINSTELLUNG_PIC;
		return IMatchDetails.EINSTELLUNG_NORMAL;
	}

	public final void setSelected(int i) {
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

	public final void addActionListener(ActionListener actionlistener) {
		m_rbPIC.addActionListener(actionlistener);
		m_rbNORM.addActionListener(actionlistener);
		m_rbMOTS.addActionListener(actionlistener);
	}

}