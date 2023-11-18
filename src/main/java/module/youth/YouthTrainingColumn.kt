package module.youth

import core.gui.comp.entry.ColorLabelEntry
import core.gui.comp.entry.IHOTableEntry
import core.gui.comp.table.UserColumn

open class YouthTrainingColumn : UserColumn {
    protected constructor(id: Int, name: String?) : super(id, name) {
        setDisplay(true)
    }

    constructor(id: Int, name: String?, minWidth: Int) : this(id, name, name, minWidth)
    constructor(id: Int, name: String?, tooltip: String?, minWidth: Int) : super(id, name, tooltip) {
        this.minWidth = minWidth
        preferredWidth = minWidth
        setDisplay(true)
    }

    open fun getTableEntry(youthTraining: YouthTraining): IHOTableEntry? {
        return ColorLabelEntry(getValue(youthTraining).toDouble(), ColorLabelEntry.BG_STANDARD, false, 0)
    }

    fun getValue(youthTraining: YouthTraining): Int {
        return youthTraining.youthMatchId
    }

    override val isEditable: Boolean
        get() = false
}
