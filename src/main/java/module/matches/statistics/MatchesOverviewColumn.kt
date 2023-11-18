package module.matches.statistics

import core.gui.comp.table.UserColumn

class MatchesOverviewColumn : UserColumn {
    protected constructor(id: Int, name: String?) : super(id, name) {
        setDisplay(true)
    }

    constructor(id: Int, name: String?, tooltip: String?, minWidth: Int) : super(id, name, tooltip) {
        this.minWidth = minWidth
        preferredWidth = minWidth
    }
}
