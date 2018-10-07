// %2840227020:de.hattrickorganizer.tools.updater%
/*
 * Created on 27.10.2004
 *
 */
package tool.updater;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;

import core.model.HOVerwaltung;
import core.util.HOLogger;

import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;


/**
 * Abstract class with contains methods for all UpdaterDialogs
 *
 * @author Thorsten Dietz
 *
 * @since 1.35
 */
abstract class UpdaterDialog extends JDialog implements ActionListener {
    //~ Instance fields ----------------------------------------------------------------------------

	private static final long serialVersionUID = -991600939074866793L;
	protected JTable table;
	protected String ACT_SHOW_INFO = "ShowInfo";
	protected String okButtonLabel;
    protected String[] columnNames;
    protected Object[] object;
    protected boolean defaultSelected;

    private String ACT_CANCEL = "CANCEL";
    private String ACT_FIND = "FIND";

    //~ Constructors -------------------------------------------------------------------------------

	protected UpdaterDialog(Object data, String title) {
		super(HOMainFrame.instance(), title);

		int dialogWidth = 500;
		int dialogHeight = 400;

		int with = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
		int height = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
		setLocation((with - dialogWidth) / 2, (height - dialogHeight) / 2);
		setSize(dialogWidth, dialogHeight);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

    //~ Methods ------------------------------------------------------------------------------------

    public void actionPerformed(ActionEvent e) {
        String comand = e.getActionCommand();
        UpdaterDialog dialog = (UpdaterDialog) ((JButton) e.getSource()).getTopLevelAncestor();

        if (comand.equals(ACT_CANCEL)) {
            dialog.dispose();
        }

        if (comand.equals(ACT_FIND)) {
            dialog.action();
        }
    }

    protected abstract TableModel getModel(boolean selected, String[] columnNames2);

    protected abstract void action();

    protected JCheckBox getCheckbox(boolean isSelected, boolean isEnabled) {
        JCheckBox tmp = new JCheckBox();
        tmp.setOpaque(false);
        tmp.setEnabled(isEnabled);
        tmp.setSelected(isSelected);
        tmp.setHorizontalAlignment(SwingConstants.CENTER);
        return tmp;
    }

    protected JLabel getLabel(boolean isEnabled, String txt) {
        JLabel tmp = new JLabel(txt);
        tmp.setEnabled(isEnabled);
        return tmp;
    }

    protected JPanel createButtons() {
        JPanel buttonPanel = new ImagePanel();
        ((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.RIGHT);

        JButton okButton = new JButton(okButtonLabel);
        okButton.setActionCommand(ACT_FIND);
        okButton.addActionListener(this);

        JButton cancelButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
        cancelButton.setActionCommand(ACT_CANCEL);
        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

	protected JScrollPane createTable() {
		table = new JTable(getModel(defaultSelected, columnNames));
		table.setDefaultRenderer(Object.class, new UpdaterCellRenderer());
		table.getTableHeader().setReorderingAllowed(false);

		if (table.getColumnCount() == 5) {
			table.getColumn(HOVerwaltung.instance().getLanguageString("Notizen")).setCellEditor(new TableEditor());
		}

		table.getColumn(columnNames[0]).setCellEditor(new TableEditor());

		JScrollPane scroll = new JScrollPane(table);
		return scroll;
	}

    protected void handleException(Exception e, String txt) {
        showException(e, txt);
    }

    protected void showException(Exception ex, String itxt) {
       HOLogger.instance().error(this.getClass(), ex);
    }

    private void setAll(boolean value) {
        for (int i = 0; i < table.getRowCount(); i++) {
            JCheckBox tmp = (JCheckBox) table.getValueAt(i, 0);

            if (tmp.isEnabled()) {
                tmp.setSelected(value);
            }
        }
        table.repaint();
    }
}
