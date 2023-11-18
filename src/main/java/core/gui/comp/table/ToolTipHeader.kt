package core.gui.comp.table

import java.awt.event.MouseEvent
import javax.swing.table.JTableHeader
import javax.swing.table.TableColumnModel

class ToolTipHeader(model: TableColumnModel?) : JTableHeader(model) {
    lateinit var toolTips: Array<String>
    fun setToolTipStrings(toolTips: Array<String>) {
        this.toolTips = toolTips
    }

    override fun getToolTipText(e: MouseEvent): String {
        val col = columnAtPoint(e.getPoint())
        val modelCol = getTable().convertColumnIndexToModel(col)
        var retStr: String
        retStr = try {
            toolTips[modelCol]
        } catch (ex: NullPointerException) {
            ""
        } catch (ex: ArrayIndexOutOfBoundsException) {
            ""
        }
        if (retStr.isEmpty()) {
            retStr = super.getToolTipText(e)
        }
        return retStr
    }
}
