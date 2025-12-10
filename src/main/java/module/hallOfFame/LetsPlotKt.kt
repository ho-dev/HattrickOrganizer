package module.hallOfFame

import org.jetbrains.letsPlot.intern.Plot

object LetsPlotKt {
    fun letsPlot(data: MutableMap<String?, Any?>?): Plot {
        return Plot(data)
    }
}
