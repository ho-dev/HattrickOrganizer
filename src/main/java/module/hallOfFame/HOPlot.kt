package module.hallOfFame

import org.jetbrains.letsPlot.*
import org.jetbrains.letsPlot.awt.plot.component.DefaultPlotContentPane
import org.jetbrains.letsPlot.awt.plot.component.PlotComponentProvider
import org.jetbrains.letsPlot.jfx.plot.component.DefaultPlotPanelJfx
import org.jetbrains.letsPlot.jfx.plot.component.PlotViewerWindowJfx
import java.awt.BorderLayout
import java.awt.EventQueue
import javax.swing.*
import kotlin.random.Random

class HOPlot {

    fun createAndShowUI() {
        // Main frame
        val frame = JFrame("Lets-Plot Live Update Example")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 600)

        val pr = DefaultPlotContentPane( )
        val p = DefaultPlotPanelJfx()
4        // Panel to hold the plot
        val plotPanel = JPanel(BorderLayout())
        frame.add(plotPanel, BorderLayout.CENTER)

        // Initial plot
        var data = generateData()
        var plot = createPlot(data)
        var plotComponent = DefaultPlotPanel(plot, preserveAspectRatio = false)
        plotPanel.add(plotComponent, BorderLayout.CENTER)

        // Button to update plot
        val updateButton = JButton("Update Plot")
        updateButton.addActionListener {
            // Generate new data
            data = generateData()
            plot = createPlot(data)

            // Remove old plot and add new one
            plotPanel.remove(plotComponent)
            plotComponent = DefaultPlotPanel(plot, preserveAspectRatio = false)
            plotPanel.add(plotComponent, BorderLayout.CENTER)

            // Refresh UI
            plotPanel.revalidate()
            plotPanel.repaint()
        }
        frame.add(updateButton, BorderLayout.SOUTH)

        frame.isVisible = true
    }

    // Generate random data
    fun generateData(): Map<String, Any> {
        val x = (1..10).toList()
        val y = x.map { Random.nextInt(1, 100) }
        return mapOf("x" to x, "y" to y)
    }

    // Create a simple plot
    fun createPlot(data: Map<String, Any>): Plot {
        return letsPlot(data) + geomLine { x = "x"; y = "y" } + geomPoint { x = "x"; y = "y" }
    }


}