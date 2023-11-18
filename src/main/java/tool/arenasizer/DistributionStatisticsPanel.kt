package tool.arenasizer

import core.db.DBManager
import core.gui.comp.entry.ColorLabelEntry
import core.gui.comp.entry.DoubleLabelEntries
import core.gui.comp.entry.IHOTableEntry
import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.HOColorName
import core.gui.theme.HOIconName
import core.gui.theme.ThemeManager
import core.model.HOVerwaltung
import core.util.Helper
import module.matches.MatchesPanel
import tool.updater.TableModel
import java.awt.BorderLayout
import java.math.BigDecimal
import java.math.RoundingMode
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingConstants

internal open class DistributionStatisticsPanel : JPanel() {
    init {
        initialize()
    }

    private fun initialize() {
        setLayout(BorderLayout())
        add(createTable(), BorderLayout.CENTER)
    }

    private fun createTable(): JScrollPane {
        val table = JTable(model)
        table.setDefaultRenderer(Any::class.java, HODefaultTableCellRenderer())
        table.tableHeader.setReorderingAllowed(false)
        val columnModel = table.columnModel
        columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(50))
        columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(50))
        return JScrollPane(table)
    }

    protected val model: TableModel
        get() {
            val hoV = HOVerwaltung.instance()
            val columnNames = arrayOf(
                hoV.getLanguageString("ls.match.id"),
                hoV.getLanguageString("ls.match.weather"),
                hoV.getLanguageString("Zuschauer"),
                hoV.getLanguageString("ls.club.arena.terraces") + " ( %)",
                hoV.getLanguageString("ls.club.arena.basicseating") + " ( %)",
                hoV.getLanguageString("ls.club.arena.seatsunderroof") + " ( %)",
                hoV.getLanguageString("ls.club.arena.seatsinvipboxes") + " ( %)",
                hoV.getLanguageString("Fans")
            )
            val matches = DBManager.getArenaStatistikModel(MatchesPanel.OWN_LEAGUE_GAMES).matches
            val value =
                Array(matches.size) {
                    arrayOfNulls<IHOTableEntry>(columnNames.size)
                }
            for (i in matches.indices) {
                value[i][0] = ColorLabelEntry(
                    matches[i].matchID.toString() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
                )
                value[i][1] = ColorLabelEntry(
                    ThemeManager.getIcon(HOIconName.WEATHER[matches[i].wetter]),
                    0.0,
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD,
                    SwingConstants.CENTER
                )
                value[i][2] = ColorLabelEntry(
                    matches[i].zuschaueranzahl.toString() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
                )
                val tmp = BigDecimal(matches[i].zuschaueranzahl).setScale(1)
                value[i][3] = createDoppelLabelEntry(
                    matches[i].soldTerraces,
                    BigDecimal(matches[i].soldTerraces * 100).setScale(1).divide(tmp, RoundingMode.HALF_DOWN)
                        .toString()
                )
                value[i][4] = createDoppelLabelEntry(
                    matches[i].soldBasics,
                    BigDecimal(matches[i].soldBasics * 100).setScale(1).divide(tmp,  RoundingMode.HALF_DOWN)
                        .toString()
                )
                value[i][5] = createDoppelLabelEntry(
                    matches[i].soldRoof,
                    BigDecimal(matches[i].soldRoof * 100).setScale(1).divide(tmp,  RoundingMode.HALF_DOWN)
                        .toString()
                )
                value[i][6] = createDoppelLabelEntry(
                    matches[i].soldVip,
                    BigDecimal(matches[i].soldVip * 100).setScale(1).divide(tmp,  RoundingMode.HALF_DOWN)
                        .toString()
                )
                value[i][7] = createFansDoppelLabelEntry(
                    matches[i].fans,
                    tmp.divide(BigDecimal(matches[i].fans),  RoundingMode.HALF_DOWN).setScale(1).toString()
                )
            }
            return TableModel(value, columnNames)
        }

    private fun createDoppelLabelEntry(leftValue: Int, rightValue: String): DoubleLabelEntries {
        return DoubleLabelEntries(
            ColorLabelEntry(
                "$leftValue",
                ColorLabelEntry.FG_STANDARD,
                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
            ),
            ColorLabelEntry(
                "$rightValue %",
                ThemeManager.getColor(HOColorName.PLAYER_OLD_FG),
                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
            )
        )
    }

    private fun createFansDoppelLabelEntry(leftValue: Int, rightValue: String): DoubleLabelEntries {
        return DoubleLabelEntries(
            ColorLabelEntry(
                leftValue.toString() + "",
                ColorLabelEntry.FG_STANDARD,
                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
            ),
            ColorLabelEntry(
                rightValue + "",
                ThemeManager.getColor(HOColorName.PLAYER_OLD_FG),
                ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT
            )
        )
    }
}
