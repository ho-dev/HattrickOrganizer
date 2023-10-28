package core.gui.language;

import core.gui.HOMainFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

/**
 * Class to implement a language resource file editor.
 * @author edswifa
 *
 */
public class LanguageEditorDialog extends JDialog {

	private static final long serialVersionUID = -7898520389349419704L;
	private JPanel toolBarPanel;
	private JTable destinationTable;
	private JComboBox languageComboBox;
	private JButton saveButton;
	private LanguageTableModel destinationTableModel;

	public LanguageEditorDialog() {
        super(HOMainFrame.INSTANCE, "Language File Editor");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
		pack();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout());
		initToolBar();
		initTables();
	}

	private void initTables() {
		destinationTable = new JTable();
		destinationTable.setAutoCreateRowSorter(false);
		add(new JScrollPane(destinationTable), BorderLayout.CENTER);
	}

	private void initToolBar() {
		toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		add(toolBarPanel, BorderLayout.NORTH);
		
		languageComboBox = new JComboBox(new LanguageComboBoxModel());
		languageComboBox.setToolTipText("Select a language to be modified");
		languageComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				destinationTableModel = new LanguageTableModel((String) languageComboBox.getSelectedItem());
				destinationTable.setModel(destinationTableModel);
				saveButton.setEnabled(true);
			}
		});
		toolBarPanel.add(languageComboBox);

		saveButton = new JButton("Save");
		saveButton.setToolTipText("Save the modifed language file");
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(destinationTableModel != null) {
					destinationTableModel.save();
				}
			}
		});
		toolBarPanel.add(saveButton);
		
		toolBarPanel.add(initHintText());
	}

	private JTextPane initHintText() {
		JTextPane hintText = new JTextPane();
		StringBuffer sb = new StringBuffer();
		
		sb.append("1. Select language to edit using drop down box.\n");
		sb.append("2. The chosen language will appear in the right hand table. All missing keys will be added with an English value\n");
		sb.append("3. Double click in the value cell on the right hand table to edit the value.\n");
		sb.append("4. After clicking the save button then the changed properties file needs to be passed to a developer to commit into the code repository.");
		
		hintText.setEditable(false);
		hintText.setText(sb.toString());
		return hintText;
	}
}
