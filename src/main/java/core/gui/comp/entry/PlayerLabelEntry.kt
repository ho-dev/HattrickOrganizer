package core.gui.comp.entry

import core.constants.player.PlayerSpeciality.getWeatherEffect
import core.gui.HOMainFrame.weather
import core.gui.comp.renderer.HODefaultTableCellRenderer
import core.gui.theme.*
import core.model.player.MatchRoleID
import core.model.player.Player
import core.training.TrainingPreviewPlayers
import java.awt.*
import java.util.function.Consumer
import javax.swing.*

class PlayerLabelEntry : IHOTableEntry {
    var spieler: Player?
        private set
    private var m_clComponent: JPanel? = null
    private val m_jlTeam = JLabel()
    private val m_jlName = JLabel()
    private val m_jlSkill = JLabel()
    private val m_jlPositionWarning = JLabel()
    private val m_jlSpecialty = JLabel()
    private val m_jlWeatherEffect = JLabel()
    private val m_jlTrainUp = JLabel()
    private var m_clPlayerMatchRoleID: MatchRoleID?
    private val m_bshowJersey: Boolean
    private val m_bShowWeatherEffect: Boolean
    private var m_bCustomName = false
    private var m_sCustomNameString = ""
    private var m_rating: Float
    private var m_IsOneOfBestPositions: Boolean
    private var m_bMultiLine = false
    private var m_bSelect = false
    private var m_bAssit = false
    private var transferlistedLabel: JLabel? = null
    private var injuredLabel: JLabel? = null
    private var bruisedLabel: JLabel? = null
    private var suspendedLabel: JLabel? = null
    private var twoYellowCardsLabel: JLabel? = null
    private var oneYellowCardLabel: JLabel? = null

    // Label for the player name (depending on status)
    constructor(
        player: Player?, playerMatchRoleID: MatchRoleID?,
        rating: Float, showJersey: Boolean, showWeatherEffect: Boolean
    ) {
        spieler = player
        m_clPlayerMatchRoleID = playerMatchRoleID
        m_rating = rating
        m_IsOneOfBestPositions = false
        m_bshowJersey = showJersey
        m_bShowWeatherEffect = showWeatherEffect
        createComponent()
    }

    // Label for the player name (depending on status)
    constructor(
        player: Player?,
        playerMatchRoleID: MatchRoleID?,
        rating: Float,
        showJersey: Boolean,
        showWeatherEffect: Boolean,
        customName: Boolean,
        customNameText: String,
        multiLine: Boolean
    ) {
        spieler = player
        m_clPlayerMatchRoleID = playerMatchRoleID
        m_rating = rating
        m_IsOneOfBestPositions = false
        m_bshowJersey = showJersey
        m_bShowWeatherEffect = showWeatherEffect
        m_bCustomName = customName
        m_sCustomNameString = customNameText
        m_bMultiLine = multiLine
        createComponent()
    }

    //~ Methods ------------------------------------------------------------------------------------
    override fun getComponent(isSelected: Boolean): JComponent {
        return getComponent(isSelected, false)!!
    }

    fun getComponent(isSelected: Boolean, forceDefaultBackground: Boolean): JComponent? {
        m_clComponent!!.setOpaque(true)
        if (forceDefaultBackground) {
            m_clComponent!!.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER))
        } else if (m_bSelect) {
            m_clComponent!!.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SELECTED))
        } else if (m_bAssit) {
            m_clComponent!!.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SUB))
        } else {
            m_clComponent!!.setBackground(if (isSelected) HODefaultTableCellRenderer.SELECTION_BG else ColorLabelEntry.BG_STANDARD)
        }
        if (TrainingPreviewPlayers.instance().getTrainPreviewPlayer(spieler).getText() != null) {
            m_clComponent!!.setToolTipText(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(spieler).getText())
        }
        m_jlName.setFont(if (isSelected) m_jlName.font.deriveFont(Font.BOLD) else m_jlName.font.deriveFont(Font.PLAIN))
        m_jlSkill.setFont(if (isSelected) m_jlSkill.font.deriveFont(Font.BOLD) else m_jlSkill.font.deriveFont(Font.PLAIN))
        return m_clComponent
    }

    fun setIsSelect(isSelect: Boolean) {
        m_bSelect = isSelect
    }

    fun setIsAssit(isSelect: Boolean) {
        m_bAssit = isSelect
    }

    override fun clear() {
        spieler = null
        m_clPlayerMatchRoleID = null
        m_rating = 0f
        m_IsOneOfBestPositions = false
        updateComponent()
    }

    override fun compareTo(other: IHOTableEntry): Int {
        return if (other is PlayerLabelEntry) {
            if (spieler == null) {
                0
            } else compareValuesBy(this, other) { spieler!!.getFullName() }
        } else 0
    }

    override fun compareToThird(other: IHOTableEntry): Int {
        if (other is PlayerLabelEntry) {
            val num1 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(spieler).getSortIndex()
            val num2 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(other.spieler).getSortIndex()
            return if (num1 < num2) {
                -1
            } else if (num1 > num2) {
                1
            } else {
                other.spieler?.getLastName()!!.compareTo(spieler?.getLastName()!!)
            }
        }
        return 0
    }

    override fun createComponent() {
        m_clComponent = JPanel()
        val layout = GridBagLayout()
        val constraints = GridBagConstraints()
        m_clComponent?.setLayout(layout)
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.anchor = GridBagConstraints.WEST
        constraints.weightx = 1.0
        constraints.gridx = 1
        constraints.insets = Insets(0, 0, 0, 0)
        if (!m_bMultiLine) {
            m_clComponent?.add(m_jlTrainUp)
        }
        m_jlName.setIconTextGap(1)
        layout.setConstraints(m_jlName, constraints)
        m_clComponent?.add(m_jlName)
        val spezPanel = JPanel()
        spezPanel.isDoubleBuffered = false
        spezPanel.setLayout(BoxLayout(spezPanel, BoxLayout.X_AXIS))
        spezPanel.setBackground(ColorLabelEntry.BG_STANDARD)
        spezPanel.setOpaque(false)
        if (!m_bMultiLine) {

            // Used in lineup panel ==================

            // Weather effect
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlWeatherEffect.setOpaque(false)
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlWeatherEffect)

            // Speciality
            m_jlSpecialty.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlSpecialty.setOpaque(false)
            spezPanel.add(m_jlSpecialty)

            // Rating
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlSkill.setOpaque(false)
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlSkill)

            //Team
            m_jlTeam.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlTeam.setVerticalAlignment(SwingConstants.BOTTOM)
            m_jlTeam.setOpaque(false)
            m_jlTeam.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlTeam)
            constraints.fill = GridBagConstraints.NONE
            constraints.anchor = GridBagConstraints.EAST
            constraints.weightx = 0.0
            constraints.gridx = 3
        } else {

            // Used in player overview ==================
            //Training
            m_jlTrainUp.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlTrainUp.setVerticalAlignment(SwingConstants.BOTTOM)
            m_jlTrainUp.setOpaque(false)
            m_jlTrainUp.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1))
            spezPanel.add(m_jlTrainUp)

            //speciality
            m_jlSpecialty.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlSpecialty.setOpaque(false)
            spezPanel.add(m_jlSpecialty)
            addPlayerStatusIcons(spezPanel)

            // Weather effect
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlWeatherEffect.setOpaque(false)
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlWeatherEffect)

            // Rating
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlSkill.setOpaque(false)
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlSkill)

            // Team
            m_jlTeam.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlTeam.setVerticalAlignment(SwingConstants.BOTTOM)
            m_jlTeam.setOpaque(false)
            m_jlTeam.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
            spezPanel.add(m_jlTeam)

            // Position warning
            m_jlPositionWarning.setBackground(ColorLabelEntry.BG_STANDARD)
            m_jlPositionWarning.setOpaque(false)
            m_jlPositionWarning.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1))
            spezPanel.add(m_jlPositionWarning)
            constraints.fill = GridBagConstraints.NONE
            constraints.anchor = GridBagConstraints.WEST
            constraints.weightx = 0.0
            constraints.gridx = 1
            constraints.gridy = 1
        }
        layout.setConstraints(spezPanel, constraints)
        m_clComponent?.add(spezPanel)
        if (spieler != null) {
            //Name
            m_jlName.setText(spieler!!.getFullName())
            m_jlName.setOpaque(false)
            showJersey()
            updateDisplay(spieler!!)
        }
        m_clComponent?.setPreferredSize(Dimension(PLAYER_LABEL_ENTRY_WIDTH, PLAYER_LABEL_ENTRY_HEIGHT))
    }

    private fun addPlayerStatusIcons(infoPanel: JPanel) {
        transferlistedLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(HOIconName.TRANSFERLISTED_TINY, 12, 12))
        infoPanel.add(transferlistedLabel)
        injuredLabel = createPlayerStatusLabel(ImageUtilities.getInjuryIcon(12, 12))
        infoPanel.add(injuredLabel)
        bruisedLabel = createPlayerStatusLabel(ImageUtilities.getPlasterIcon(12, 12))
        infoPanel.add(bruisedLabel)
        suspendedLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(HOIconName.SUSPENDED_TINY, 12, 12))
        infoPanel.add(suspendedLabel)
        twoYellowCardsLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(HOIconName.TWOYELLOW_TINY, 12, 12))
        infoPanel.add(twoYellowCardsLabel)
        oneYellowCardLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(HOIconName.ONEYELLOW_TINY, 12, 12))
        infoPanel.add(oneYellowCardLabel)
    }

    private fun createPlayerStatusLabel(icon: Icon): JLabel {
        val playerStatusLabel = JLabel(icon)
        playerStatusLabel.setBackground(ColorLabelEntry.BG_STANDARD)
        playerStatusLabel.setOpaque(false)
        playerStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0))
        return playerStatusLabel
    }

    override fun updateComponent() {
        if (spieler != null) {
            showJersey()
            updateDisplay(spieler!!)
        } else {
            setEmptyLabel()
        }
    }

    /**
     * Update the entry.
     */
    fun updateComponent(
        player: Player?, positionAktuell: MatchRoleID?,
        rating: Float, alternativePosition: Boolean, nameText: String
    ) {
        spieler = player
        m_clPlayerMatchRoleID = positionAktuell
        m_rating = rating
        m_IsOneOfBestPositions = alternativePosition
        m_sCustomNameString = nameText
        if (spieler != null) {
            if (m_bCustomName) {
                m_jlName.setText(m_sCustomNameString)
            } else {
                m_jlName.setText(spieler!!.getLastName())
            }
            showJersey()
            updateDisplay(spieler!!)
        } else {
            setEmptyLabel()
            m_jlTeam.setIcon(null)
        }
    }

    private fun showJersey() {
        // Jersey
        if (m_bshowJersey) {
            m_jlName.setIcon(
                ImageUtilities.getJerseyIcon(m_clPlayerMatchRoleID, spieler?.shirtNumber ?: -1)
            )
            showGroupIcon()
        }
    }

    private fun showGroupIcon() {
        val teamInfoSmiley = spieler?.getTeamGroup()

        if (teamInfoSmiley == null || teamInfoSmiley.trim().isEmpty()) {
            m_jlTeam.setIcon(ImageUtilities.MINILEER)
        } else {
            m_jlTeam.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(teamInfoSmiley, 15))
        }
    }

    private fun setEmptyLabel() {
        m_jlName.setText("")
        m_jlName.setIcon(null)
        m_jlWeatherEffect.setIcon(null)
        m_jlSpecialty.setIcon(null)
        m_jlTrainUp.setIcon(null)
        m_jlPositionWarning.setText("")
        m_jlSkill.setText("")
    }

    private fun updateDisplay(player: Player) {
        // weatherEffect
        m_jlWeatherEffect.setIcon(null)
        if (m_bShowWeatherEffect) {
            val effect = getWeatherEffect(weather, player.specialty)
            if (effect != 0) {
                val wettericon = ThemeManager.getIcon("weather.effect.$effect")
                m_jlWeatherEffect.setIcon(wettericon)
            }
        }
        m_jlSpecialty.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[player.specialty]))

        // positionValue
        if (m_rating != 0f) {
            m_jlSkill.setText(String.format("(%.2f)", m_rating))
        } else {
            m_jlSkill.setText("")
        }

        // warning icon iin case of non optimal placement
        if (!m_IsOneOfBestPositions) {
            m_jlPositionWarning.setIcon(
                ImageUtilities.getSvgIcon(
                    HOIconName.WARNING_ICON,
                    mapOf("foregroundColor" to ThemeManager.getColor(HOColorName.WARNING_ICON_CB_COLOR)),
                    15,
                    15
                )
            )
        } else {
            m_jlPositionWarning.setIcon(null)
        }
        m_jlTrainUp.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon())
        if (m_bMultiLine) {
            listOf(
                injuredLabel,
                bruisedLabel,
                transferlistedLabel,
                suspendedLabel,
                twoYellowCardsLabel,
                oneYellowCardLabel
            ).forEach(Consumer { label: JLabel? -> label!!.setIcon(null) })
            if (player.injuryWeeks > 0) {
                injuredLabel!!.setIcon(ImageUtilities.getInjuryIcon(12, 12))
            } else if (player.injuryWeeks == 0) {
                bruisedLabel!!.setIcon(ImageUtilities.getPlasterIcon(12, 12))
            }
            if (player.transferListed > 0) {
                transferlistedLabel!!.setIcon(ImageUtilities.getSvgIcon(HOIconName.TRANSFERLISTED_TINY, 12, 12))
            }
            if (player.isRedCarded()) {
                suspendedLabel!!.setIcon(ImageUtilities.getSvgIcon(HOIconName.SUSPENDED_TINY, 12, 12))
            } else if (player.totalCards == 2) {
                twoYellowCardsLabel!!.setIcon(ImageUtilities.getSvgIcon(HOIconName.TWOYELLOW_TINY, 12, 12))
            } else if (player.totalCards == 1) {
                oneYellowCardLabel!!.setIcon(ImageUtilities.getSvgIcon(HOIconName.ONEYELLOW_TINY, 12, 12))
            }
            suspendedLabel!!.parent.repaint()
        }
    }

    companion object {
        private const val PLAYER_LABEL_ENTRY_HEIGHT = 18
        private const val PLAYER_LABEL_ENTRY_WIDTH = 130
    }
}
