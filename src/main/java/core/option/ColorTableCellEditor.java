package core.option;

import core.gui.theme.HOColor;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ColorTableCellEditor handles color table cells of the UserColorsPanel
 * Source: <a href="https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/TableDialogEditDemoProject/src/components/ColorEditor.java">...</a>
 */
public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private final UserColorsPanel userColorsPanel;
    HOColor currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    public static final String EDIT = "edit";
    public static final String RESET_DEFAULT = "reset";

    /**
     * Create ColorTableCellEditor
     * The color column of the user colors panel creates editor with "edit" action
     * The default column creates editor with "reset" action
     * @param userColorsPanel Link to the color context
     * @param action Either "edit" or "reset"
     */
    public ColorTableCellEditor(UserColorsPanel userColorsPanel, String action) {
        this.userColorsPanel = userColorsPanel;
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(action);
        button.addActionListener(this);
        button.setBorderPainted(false);

        if (action.equals(EDIT)) {
            //Set up the dialog that the button brings up.
            colorChooser = new JColorChooser();
            dialog = JColorChooser.createDialog(button,
                    "Pick a Color",
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
        }
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        var action = e.getActionCommand();
        if (EDIT.equals(action)) {
            //The user has clicked the cell, so
            //bring up the dialog.
            var color = currentColor.getColor(userColorsPanel.getColors());
            button.setBackground(color);
            colorChooser.setColor(color);
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();
        } else if (RESET_DEFAULT.equals(action)){
            // current color is the default color
            userColorsPanel.resetRow(currentColor);
        } else { //User pressed dialog's "OK" button.
            var theme = ThemeManager.getTheme(currentColor.getTheme());
            theme.initDefaultValue(currentColor);
            currentColor.setColor( colorChooser.getColor());
            currentColor.setTheme(userColorsPanel.getSelectedTheme());
            userColorsPanel.updateRow(currentColor);
        }
    }

    /**
     * Implement the one CellEditor method that AbstractCellEditor doesn't.
     */
    public Object getCellEditorValue() {
        return currentColor;
    }

    /**
     *   Implement the one method defined by TableCellEditor.
     */
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (HOColor)value;
        return button;
    }
}