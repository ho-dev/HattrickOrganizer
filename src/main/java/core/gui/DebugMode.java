package core.gui;

import core.db.frontend.SQLDialog;
import core.net.Connector;

import javax.swing.*;
import java.awt.event.ItemEvent;

public final class DebugMode {

	private DebugMode() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static JMenu getDeveloperMenu() {
		JMenu menu = new JMenu("Debug");
		menu.add(createSqlDialogMenuItem());
		menu.add(createLookAndFeelDialogMenuItem());
		menu.add(createSaveDownloadedXmlMenuItem());
		return menu;
	}

	private static JMenuItem createLookAndFeelDialogMenuItem() {
		JMenuItem newItem = new JMenuItem("Look and Feel");
		newItem.addActionListener(e -> new LookAndFeelDialog().setVisible(true));
		return newItem;
	}

	private static JMenuItem createSqlDialogMenuItem() {
		JMenuItem newItem = new JMenuItem("SQL Editor");
		newItem.addActionListener(e -> new SQLDialog().setVisible(true));
		return newItem;
	}

	private static JMenuItem createSaveDownloadedXmlMenuItem() {
		JMenuItem newItem = new JCheckBoxMenuItem("Save downloaded XML", Connector.isSaveDownloadedXml());
		newItem.addItemListener(e -> Connector.setSaveDownloadedXml(e.getStateChange() == ItemEvent.SELECTED));
		return newItem;
	}
}
