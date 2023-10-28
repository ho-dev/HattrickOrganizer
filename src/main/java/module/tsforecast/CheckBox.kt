package module.tsforecast;

/*
 * CheckBox.java
 *
 * Created on 23.March 2006, 22:04
 *
 *Version 0.11
 *history :
 *23.03.06  Version 0.1
 *24.08.06  Version 0.11 rebuilt
 */

/**
 *
 * @author  michael.roux
 */

import core.gui.comp.panel.ImagePanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

final class CheckBox extends ImagePanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox m_checkBox;
	private JLabel m_label;

	CheckBox(String s, Color color, boolean selected) {
		m_checkBox = new JCheckBox();
		m_label = new JLabel(s, square(color), JLabel.LEFT);
		GridBagLayout gridbaglayout = new GridBagLayout();
		GridBagConstraints gridbagconstraints = new GridBagConstraints();
		setLayout(gridbaglayout);

		gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
		gridbagconstraints.weightx = 0.0D;
		gridbagconstraints.weighty = 0.0D;
		gridbagconstraints.gridx = 0;
		gridbagconstraints.gridy = 0;
		gridbagconstraints.insets = new Insets(0, 0, 0, 0);
		m_checkBox.setSelected(selected);
		m_checkBox.setOpaque(false);
		gridbaglayout.setConstraints(m_checkBox, gridbagconstraints);
		add(m_checkBox);

		gridbagconstraints.gridx = 1;
		gridbagconstraints.gridy = 0;
		gridbagconstraints.weightx = 1.0D;
		m_label.setOpaque(false);
		gridbaglayout.setConstraints(m_label, gridbagconstraints);
		add(m_label);
	}

	final JCheckBox getCheckBox() {
		return m_checkBox;
	}

	final boolean isSelected() {
		return m_checkBox.isSelected();
	}

	final void setSelected(boolean flag) {
		m_checkBox.setSelected(flag);
	}

	final void addItemListener(ItemListener itemlistener) {
		m_checkBox.addItemListener(itemlistener);
	}

	private static ImageIcon square(Color color) {
		BufferedImage bufferedimage = new BufferedImage(14, 14, 2);
		Graphics2D graphics2d = (Graphics2D) bufferedimage.getGraphics();
		graphics2d.setColor(color);
		graphics2d.fillRect(0, 0, 13, 13);
		return new ImageIcon(bufferedimage);
	}

}