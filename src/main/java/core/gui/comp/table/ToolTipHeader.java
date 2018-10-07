// %3174669501:de.hattrickorganizer.gui.utils%
package core.gui.comp.table;

import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;


public class ToolTipHeader extends JTableHeader {

	private static final long serialVersionUID = -3459959650680988134L;

    String[] toolTips;

    public ToolTipHeader(TableColumnModel model) {
        super(model);
    }

    public final void setToolTipStrings(String[] toolTips) {
        this.toolTips = toolTips;
    }

    @Override
	public final String getToolTipText(MouseEvent e) {
        final int col = columnAtPoint(e.getPoint());
        final int modelCol = getTable().convertColumnIndexToModel(col);
        String retStr;

        try {
            retStr = toolTips[modelCol];
        } catch (NullPointerException ex) {
            retStr = "";
        } catch (ArrayIndexOutOfBoundsException ex) {
            retStr = "";
        }

        if (retStr == null || retStr.length() < 1) {
            retStr = super.getToolTipText(e);
        }

        return retStr;
    }
}
