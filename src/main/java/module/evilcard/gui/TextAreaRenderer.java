package module.evilcard.gui;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


class TextAreaRenderer extends JEditorPane implements TableCellRenderer {

	private static final long serialVersionUID = -1819164140033081924L;

    public TextAreaRenderer() {
        super();
        this.setContentType("text/html");
        this.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.setMargin(new Insets(0, 1, 0, 1));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        // Set text/html.
        this.setText((String) object);

        // Set background color.
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }

        return this;
    }
}
