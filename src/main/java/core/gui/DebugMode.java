package core.gui;

import core.db.frontend.SQLDialog;
import core.net.Connector;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class DebugMode {

	private DebugMode() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static JMenu getDeveloperMenu() {
		JMenu menu = new JMenu("Debug");
		menu.add(getSQLDialogMenuItem());
		menu.add(getLookAndFeelDialogMenuItem());
		menu.add(getSaveXMLMenuItem());
		return menu;
	}

	private static JMenuItem getLookAndFeelDialogMenuItem() {
		JMenuItem newItem = new JMenuItem("Look and Feel");
		newItem.addActionListener(e -> new LookAndFeelDialog().setVisible(true));
		return newItem;
	}

	private static JMenuItem getSQLDialogMenuItem() {
		JMenuItem newItem = new JMenuItem("SQL Editor");
		newItem.addActionListener(e -> new SQLDialog().setVisible(true));
		return newItem;
	}

	private static JMenuItem getSaveXMLMenuItem() {
		JMenuItem newItem = new JCheckBoxMenuItem("Save downloaded XML");
		newItem.addItemListener(e -> Connector.setDebugSave(e.getStateChange() == ItemEvent.SELECTED));
		return newItem;
	}
}
