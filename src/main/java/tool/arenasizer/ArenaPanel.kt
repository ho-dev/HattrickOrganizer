package tool.arenasizer

import core.gui.comp.entry.ColorLabelEntry
import core.gui.comp.entry.DoubleLabelEntries
import core.gui.comp.entry.IHOTableEntry
import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.model.HOVerwaltung
import core.util.Helper
import tool.updater.TableModel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingConstants

/**
 * Panel for Stadium display and testing
 */
class ArenaPanel : JPanel() {
    //~ Instance fields ----------------------------------------------------------------------------
    private val arenaSizer = ArenaSizer()
    private val arenaTable = JTable()

    //Teststadium
    private var stadium: Stadium? = null
    private lateinit var stadiumArray: Array<Stadium>
    private lateinit var values: Array<Array<IHOTableEntry?>>

    private val HEADERS = arrayOf(
        "",
        HOVerwaltung.instance().getLanguageString("Aktuell"),
        HOVerwaltung.instance().getLanguageString("Maximal"),
        HOVerwaltung.instance().getLanguageString("Durchschnitt"),
        HOVerwaltung.instance().getLanguageString("Minimal")
    )


    //~ Constructors -------------------------------------------------------------------------------
    init {
        setLayout(BorderLayout())
        add(JScrollPane(arenaTable))
        arenaTable.setDefaultRenderer(Any::class.java, HODefaultTableCellRenderer())
        arenaTable.tableHeader.setReorderingAllowed(false)
        initTable()
        reInit()
    }

    //-------Refresh---------------------------------
    fun reInit() {
        val model = HOVerwaltung.instance().model
        stadium = model.getStadium()
        stadiumArray = arenaSizer.calcConstructionArenas(stadium!!, model.getClub().fans)

        reinitTable()
    }

    private fun initTable() {
        values = Array(9) { arrayOfNulls(5) }
        val hoV = HOVerwaltung.instance()
        val columnText = arrayOf(
            "ls.club.arena.terraces",
            "ls.club.arena.basicseating",
            "ls.club.arena.seatsunderroof",
            "ls.club.arena.seatsinvipboxes",
            "Gesamt",
            "Einnahmen",
            "Unterhalt",
            "Gewinn",
            "Baukosten"
        )

        for (i in columnText.indices) {
            values[i][0] = ColorLabelEntry(
                hoV.getLanguageString(columnText[i]),
                ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, SwingConstants.LEFT
            )
        }

        //Platzwerte
        for (i in 0..8) {
            for (j in 1..4) {
                if (i < 4) values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES)
                else if (i == 4) values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES)
                else values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_SINGLEPLAYERVALUES)
            }
        }
        arenaTable.setModel(TableModel(values, HEADERS))
        val columnModel = arenaTable.columnModel
        columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(150))
        columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(160))
        columnModel.getColumn(2).setMinWidth(Helper.calcCellWidth(160))
        columnModel.getColumn(3).setMinWidth(Helper.calcCellWidth(160))
        columnModel.getColumn(4).setMinWidth(Helper.calcCellWidth(160))
    }

    /**
     * create a new DoppelLabelEntry with default values
     * @param background
     * @return
     */
    private fun createDoppelLabelEntry(background: Color): DoubleLabelEntries {
        return DoubleLabelEntries(
            ColorLabelEntry(
                "",
                ColorLabelEntry.FG_STANDARD,
                background, SwingConstants.RIGHT
            ),
            ColorLabelEntry(
                "",
                ColorLabelEntry.FG_STANDARD,
                background, SwingConstants.RIGHT
            )
        )
    }

    fun reinitArena(currentArena: Stadium, maxSupporter: Int, normalSupporter: Int, minSupporter: Int) {
        stadium = currentArena
        stadiumArray = arenaSizer.calcConstructionArenas(currentArena, maxSupporter, normalSupporter, minSupporter)

        //Entrys mit Werten füllen
        reinitTable()
    }

    private fun reinitTable() {
        val stadium = HOVerwaltung.instance().model.getStadium()
        val currentStadium = this.stadium!!

        (values[0][1] as DoubleLabelEntries).left.setText("${currentStadium.standing}")
        (values[0][1] as DoubleLabelEntries).right.setSpecialNumber(
            currentStadium.standing - stadium.standing,
            false
        )

        (values[1][1] as DoubleLabelEntries).left.setText("${currentStadium.basicSeating}")
        (values[1][1] as DoubleLabelEntries).right.setSpecialNumber(
            currentStadium.basicSeating - stadium.basicSeating,
            false
        )

        (values[2][1] as DoubleLabelEntries?)!!.left.setText("${currentStadium.seatingUnderRoof}")
        (values[2][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            currentStadium.seatingUnderRoof - stadium.seatingUnderRoof,
            false
        )

        (values[3][1] as DoubleLabelEntries?)!!.left.setText("${currentStadium.vip}")
        (values[3][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            currentStadium.vip - stadium.vip,
            false
        )

        (values[4][1] as DoubleLabelEntries?)!!.left.setText("${currentStadium.totalSize()}")
        (values[4][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            currentStadium.totalSize() - stadium.totalSize(),
            false
        )

        (values[5][1] as DoubleLabelEntries?)!!.left.setSpecialNumber(
            arenaSizer.calcMaxIncome(currentStadium),
            true
        )
        (values[5][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            arenaSizer.calcMaxIncome(currentStadium) - arenaSizer.calcMaxIncome(
                stadium
            ), true
        )

        (values[6][1] as DoubleLabelEntries?)!!.left.setSpecialNumber(
            -arenaSizer.calcMaintenance(currentStadium),
            true
        )
        (values[6][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            -(arenaSizer.calcMaintenance(currentStadium) - arenaSizer.calcMaintenance(
                stadium
            )), true
        )

        (values[7][1] as DoubleLabelEntries?)!!.left.setSpecialNumber(
            arenaSizer.calcMaxIncome(currentStadium) - arenaSizer.calcMaintenance(currentStadium),
            true
        )
        (values[7][1] as DoubleLabelEntries?)!!.right.setSpecialNumber(
            arenaSizer.calcMaxIncome(currentStadium) - arenaSizer.calcMaintenance(currentStadium) - (arenaSizer.calcMaxIncome(stadium)
                    - arenaSizer.calcMaintenance(stadium)), true
        )

        (values[8][1] as DoubleLabelEntries?)!!.left.setSpecialNumber(
            -arenaSizer.calcConstructionCosts(
                (currentStadium.standing - stadium.standing).toFloat(),
                (currentStadium.basicSeating - stadium.basicSeating).toFloat(),
                (currentStadium.seatingUnderRoof - stadium.seatingUnderRoof).toFloat(),
                (currentStadium.vip - stadium.vip).toFloat()
            ), true
        )
        (values[8][1] as DoubleLabelEntries?)!!.right.setText("")
        for (i in 2..4) {
            (values[0][i] as DoubleLabelEntries?)!!.left.setText(
                stadiumArray[i - 2].standing.toString() + ""
            )
            (values[0][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                stadiumArray[i - 2].standing - currentStadium.standing,
                false
            )
            (values[1][i] as DoubleLabelEntries?)!!.left.setText(
                stadiumArray[i - 2].basicSeating.toString() + ""
            )
            (values[1][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                stadiumArray[i - 2].basicSeating - currentStadium.basicSeating,
                false
            )
            (values[2][i] as DoubleLabelEntries?)!!.left.setText(
                stadiumArray[i - 2].seatingUnderRoof.toString() + ""
            )
            (values[2][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                stadiumArray[i - 2].seatingUnderRoof - currentStadium.seatingUnderRoof,
                false
            )
            (values[3][i] as DoubleLabelEntries?)!!.left.setText(stadiumArray[i - 2].vip.toString() + "")
            (values[3][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                stadiumArray[i - 2].vip - currentStadium.vip,
                false
            )
            (values[4][i] as DoubleLabelEntries?)!!.left.setText(
                stadiumArray[i - 2].totalSize().toString() + ""
            )
            (values[4][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                stadiumArray[i - 2].totalSize() - currentStadium.totalSize(),
                false
            )
            (values[5][i] as DoubleLabelEntries?)!!.left.setSpecialNumber(
                arenaSizer.calcMaxIncome(
                    stadiumArray[i - 2]
                ), true
            )
            (values[5][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                arenaSizer.calcMaxIncome(
                    stadiumArray[i - 2]
                ) - arenaSizer.calcMaxIncome(currentStadium), true
            )
            (values[6][i] as DoubleLabelEntries?)!!.left.setSpecialNumber(
                -arenaSizer.calcMaintenance(
                    stadiumArray[i - 2]
                ), true
            )
            (values[6][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                -(arenaSizer.calcMaintenance(
                    stadiumArray[i - 2]
                )
                        - arenaSizer.calcMaintenance(currentStadium)), true
            )
            (values[7][i] as DoubleLabelEntries?)!!.left.setSpecialNumber(
                arenaSizer.calcMaxIncome(
                    stadiumArray[i - 2]
                ) - arenaSizer.calcMaintenance(stadiumArray[i - 2]), true
            )
            (values[7][i] as DoubleLabelEntries?)!!.right.setSpecialNumber(
                arenaSizer.calcMaxIncome(
                    stadiumArray[i - 2]
                ) - arenaSizer.calcMaintenance(stadiumArray[i - 2])
                        - (arenaSizer.calcMaxIncome(currentStadium) - arenaSizer.calcMaintenance(currentStadium)),
                true
            )
            (values[8][i] as DoubleLabelEntries?)!!.left.setSpecialNumber(
                -stadiumArray[i - 2].expansionCosts,
                true
            )
        }

        arenaTable.setModel(TableModel(values, HEADERS))

        arenaTable.columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(150))
        arenaTable.columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(160))
        arenaTable.columnModel.getColumn(2).setMinWidth(Helper.calcCellWidth(160))
        arenaTable.columnModel.getColumn(3).setMinWidth(Helper.calcCellWidth(160))
        arenaTable.columnModel.getColumn(4).setMinWidth(Helper.calcCellWidth(160))
    }
}
