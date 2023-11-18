package core.gui.comp.table

import core.model.HOVerwaltung
import javax.swing.table.TableColumn

abstract class UserColumn {
    //~ Instance fields ----------------------------------------------------------------------------
    /**
     * return  id
     * @return int
     */
    /** unique column id  */
    @JvmField
    var id = 0

    /** columnName properties representation, not display!!  */
    protected var columnName: String? = null
        get () {
            return if (field == "TSI" || field == " ")
                field
            else
                HOVerwaltung.instance().getLanguageString(field)
        }

    /** tooltip properties representation, not display!!  */
    protected var tooltip: String? = null
        get() {
            return if (columnName == "TSI" || field == " ") field
            else HOVerwaltung.instance().getLanguageString(field)
        }

    /** mininmum width of the column  */
    @JvmField
    var minWidth = 0
    /**
     * set preferredWidth for saving to DB
     * @param width int
     */
    /** preferred width of the column  */
    @JvmField
    var preferredWidth = 0
    /**
     * return the current index of column
     * only actual if user don´t move the column !!
     * @return int
     */
    /**
     * set index
     * if columnModel should be saved index will set, or column is loaded
     * @param index int
     */
    /** index of the column in the JTable. position */
    @JvmField
    var index = 0

    /** if a column is shown in the jtable. Only displayed columns are saved in db.  */
    @JvmField
    protected var display = false

    protected constructor(id: Int, name: String?, tooltip: String? = name) {
        this.id = id
        columnName = name
        this.tooltip = tooltip
    }

    /**
     * constructor is used by AbstractTable
     */
    constructor()

    /**
     * Should a column be shown
     * @return boolean
     */
    open fun isDisplay(): Boolean {
        return display
    }

    /**
     * set a column to be showed
     * @param display boolean
     */
    fun setDisplay(display: Boolean) {
        this.display = display
        if (!display) {
            index = 0
        }
    }

    /**
     * String representation
     * use in UserColumnsPanel in OptionsPanel
     */
    override fun toString(): String {
        return tooltip!!
    }

    /*
* Some columns must be displayed, so some columns are not editable
* @return boolean
*/
    open val isEditable: Boolean
        get() = true

    /**
     * set minWidth and prefWidth in the TableColumn
     * @param column TableColumn
     */
    open fun setSize(column: TableColumn) {
        column.setMinWidth(minWidth)
        column.setPreferredWidth(preferredWidth)
    }
}
