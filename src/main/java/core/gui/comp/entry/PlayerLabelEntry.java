package core.gui.comp.entry;

import core.constants.player.PlayerSpeciality;
import core.gui.HOMainFrame;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.*;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.TrainingPreviewPlayers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static core.gui.theme.HOIconName.*;

public final class PlayerLabelEntry implements IHOTableEntry {

    private @Nullable Player m_clPlayer;
    private JComponent m_clComponent;
    private final JLabel m_jlTeam = new JLabel();
    private final JLabel m_jlName = new JLabel();
    private final JLabel m_jlSkill = new JLabel();
    private final JLabel m_jlPositionWarning = new JLabel();
    private final JLabel m_jlSpecialty = new JLabel();
    private final JLabel m_jlWeatherEffect = new JLabel();
    private final JLabel m_jlTrainUp = new JLabel();

    private @Nullable MatchRoleID m_clPlayerMatchRoleID;
    private final boolean m_bshowJersey;
    private final boolean m_bShowWeatherEffect;
    private boolean m_bCustomName = false;
    private String m_sCustomNameString = "";
    private float m_rating;
    private boolean m_IsOneOfBestPositions;
    private boolean m_bMultiLine = false;
    private boolean m_bSelect = false;
    private boolean m_bAssit = false;

    private JLabel transferlistedLabel;
    private JLabel injuredLabel;
    private JLabel bruisedLabel;
    private JLabel suspendedLabel;
    private JLabel twoYellowCardsLabel;
    private JLabel oneYellowCardLabel;

    private static final int PLAYER_LABEL_ENTRY_HEIGHT = 18;
    private static final int PLAYER_LABEL_ENTRY_WIDTH = 130;

    // Label for the player name (depending on status)
    public PlayerLabelEntry(@Nullable Player player, @Nullable MatchRoleID playerMatchRoleID,
                            float rating, boolean showJersey, boolean showWeatherEffect) {
        m_clPlayer = player;
        m_clPlayerMatchRoleID = playerMatchRoleID;
        m_rating = rating;
        m_IsOneOfBestPositions = false;
        m_bshowJersey = showJersey;
        m_bShowWeatherEffect = showWeatherEffect;
        createComponent();
    }

    // Label for the player name (depending on status)
    public PlayerLabelEntry(@Nullable Player player, @Nullable MatchRoleID playerMatchRoleID,
                            float rating, boolean showJersey, boolean showWeatherEffect, boolean customName, String customNameText, boolean multiLine) {
        m_clPlayer = player;
        m_clPlayerMatchRoleID = playerMatchRoleID;
        m_rating = rating;
        m_IsOneOfBestPositions = false;
        m_bshowJersey = showJersey;
        m_bShowWeatherEffect = showWeatherEffect;
        m_bCustomName = customName;
        m_sCustomNameString = customNameText;
        m_bMultiLine = multiLine;
        createComponent();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public JComponent getComponent(boolean isSelected) {
        return getComponent(isSelected, false);
    }

    public JComponent getComponent(boolean isSelected, boolean forceDefaultBackground) {
        m_clComponent.setOpaque(true);
        if (forceDefaultBackground) {
            m_clComponent.setBackground(ThemeManager.getColor(HOColorName.BACKGROUND_CONTAINER));
        }
        else if (m_bSelect) {
            m_clComponent.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SELECTED));
        }
        else if (m_bAssit) {
            m_clComponent.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SUB));
        }
        else {
            m_clComponent.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG : ColorLabelEntry.BG_STANDARD);
        }

        if (TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getText() != null) {
            m_clComponent.setToolTipText(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getText());
        }

        m_jlName.setFont(isSelected ? m_jlName.getFont().deriveFont(Font.BOLD) : m_jlName.getFont().deriveFont(Font.PLAIN));
        m_jlSkill.setFont(isSelected ? m_jlSkill.getFont().deriveFont(Font.BOLD) : m_jlSkill.getFont().deriveFont(Font.PLAIN));

        return m_clComponent;
    }

    public void setIsSelect(boolean isSelect) {
        m_bSelect = isSelect;
    }

    public void setIsAssit(boolean isSelect) {
        m_bAssit = isSelect;
    }


    public @Nullable Player getSpieler() {
        return m_clPlayer;
    }

    public void clear() {
        m_clPlayer = null;
        m_clPlayerMatchRoleID = null;
        m_rating = 0f;
        m_IsOneOfBestPositions = false;
        updateComponent();
    }

    public int compareTo(@NotNull IHOTableEntry obj) {
        if (obj instanceof PlayerLabelEntry entry) {
            return Objects.requireNonNull(m_clPlayer).getFullName().compareTo(Objects.requireNonNull(entry.getSpieler()).getFullName());
        }

        return 0;
    }

    public int compareToThird(IHOTableEntry obj) {
        if (obj instanceof PlayerLabelEntry entry) {
            int num1 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getSortIndex();
            int num2 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(entry.m_clPlayer).getSortIndex();

            if (num1 < num2) {
                return -1;
            } else if (num1 > num2) {
                return 1;
            } else {
                return Objects.requireNonNull(entry.getSpieler()).getLastName().compareTo(Objects.requireNonNull(m_clPlayer).getLastName());
            }
        }
        return 0;
    }

    public void createComponent() {
        m_clComponent = new JPanel();

        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        m_clComponent.setLayout(layout);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        constraints.gridx = 1;
        constraints.insets = new Insets(0, 0, 0, 0);

        if (!m_bMultiLine) {
            m_clComponent.add(m_jlTrainUp);
        }

        m_jlName.setIconTextGap(1);
        layout.setConstraints(m_jlName, constraints);
        m_clComponent.add(m_jlName);

        final JPanel spezPanel = new JPanel();
        spezPanel.setDoubleBuffered(false);
        spezPanel.setLayout(new BoxLayout(spezPanel, BoxLayout.X_AXIS));
        spezPanel.setBackground(ColorLabelEntry.BG_STANDARD);
        spezPanel.setOpaque(false);

        if (!m_bMultiLine) {

            // Used in lineup panel ==================

            // Weather effect
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlWeatherEffect.setOpaque(false);
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlWeatherEffect);

            // Speciality
            m_jlSpecialty.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSpecialty.setOpaque(false);
            spezPanel.add(m_jlSpecialty);

            // Rating
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSkill.setOpaque(false);
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlSkill);

            //Team
            m_jlTeam.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlTeam.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlTeam.setOpaque(false);
            m_jlTeam.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlTeam);

            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0.0;
            constraints.gridx = 3;
        }
        else {

            // Used in player overview ==================
            //Training
            m_jlTrainUp.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlTrainUp.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlTrainUp.setOpaque(false);
            m_jlTrainUp.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            spezPanel.add(m_jlTrainUp);

            //speciality
            m_jlSpecialty.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSpecialty.setOpaque(false);
            spezPanel.add(m_jlSpecialty);

            addPlayerStatusIcons(spezPanel);

            // Weather effect
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlWeatherEffect.setOpaque(false);
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlWeatherEffect);

            // Rating
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSkill.setOpaque(false);
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlSkill);

            // Team
            m_jlTeam.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlTeam.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlTeam.setOpaque(false);
            m_jlTeam.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlTeam);

            // Position warning
            m_jlPositionWarning.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlPositionWarning.setOpaque(false);
            m_jlPositionWarning.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            spezPanel.add(m_jlPositionWarning);

            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.weightx = 0.0;
            constraints.gridx = 1;
            constraints.gridy = 1;
        }
        layout.setConstraints(spezPanel, constraints);
        m_clComponent.add(spezPanel);

        if (m_clPlayer != null) {
            //Name
            m_jlName.setText(m_clPlayer.getFullName());
            m_jlName.setOpaque(false);
            showJersey();
            updateDisplay(m_clPlayer);
        }

        m_clComponent.setPreferredSize(new Dimension(PLAYER_LABEL_ENTRY_WIDTH, PLAYER_LABEL_ENTRY_HEIGHT));
    }

    private void addPlayerStatusIcons(JPanel infoPanel) {
        transferlistedLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, 12, 12));
        infoPanel.add(transferlistedLabel);

        injuredLabel = createPlayerStatusLabel(ImageUtilities.getInjuryIcon(12,12));
        infoPanel.add(injuredLabel);

        bruisedLabel = createPlayerStatusLabel(ImageUtilities.getPlasterIcon(12,12));
        infoPanel.add(bruisedLabel);

        suspendedLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(SUSPENDED_TINY, 12, 12));
        infoPanel.add(suspendedLabel);

        twoYellowCardsLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(TWOYELLOW_TINY, 12, 12));
        infoPanel.add(twoYellowCardsLabel);

        oneYellowCardLabel = createPlayerStatusLabel(ImageUtilities.getSvgIcon(ONEYELLOW_TINY, 12, 12));
        infoPanel.add(oneYellowCardLabel);
    }

    private JLabel createPlayerStatusLabel(Icon icon) {
        final JLabel playerStatusLabel = new JLabel(icon);
        playerStatusLabel.setBackground(ColorLabelEntry.BG_STANDARD);
        playerStatusLabel.setOpaque(false);
        playerStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));

        return playerStatusLabel;
    }

    public void updateComponent() {
        if (m_clPlayer != null) {
            showJersey();
            updateDisplay(m_clPlayer);
        } else {
            setEmptyLabel();
        }
    }

    /**
     * Update the entry.
     */
    public void updateComponent(Player player, MatchRoleID positionAktuell,
                                float rating, boolean alternativePosition, String nameText) {
        m_clPlayer = player;
        m_clPlayerMatchRoleID = positionAktuell;
        m_rating = rating;
        m_IsOneOfBestPositions = alternativePosition;
        m_sCustomNameString = nameText;

        if (m_clPlayer != null) {
            if (m_bCustomName) {
                m_jlName.setText(m_sCustomNameString);
            } else {
                m_jlName.setText(m_clPlayer.getLastName());
            }

            showJersey();
            updateDisplay(m_clPlayer);

        } else {
            setEmptyLabel();
            m_jlTeam.setIcon(null);
        }

//        m_clComponent.setPreferredSize(new Dimension(PLAYER_LABEL_ENTRY_WIDTH, PLAYER_LABEL_ENTRY_HEIGHT));
    }

    private void showJersey() {
        // Jersey
        if (m_bshowJersey) {
            m_jlName.setIcon(ImageUtilities.getJerseyIcon(
                    m_clPlayerMatchRoleID,
                    Objects.requireNonNull(m_clPlayer).getShirtNumber()
            ));
            showGroupIcon();
        }
    }

    private void showGroupIcon() {
        String teamInfoSmilie = Objects.requireNonNull(m_clPlayer).getTeamGroup();

        if (teamInfoSmilie.trim().isEmpty())
            m_jlTeam.setIcon(ImageUtilities.MINILEER);
        else
            m_jlTeam.setIcon(GroupTeamFactory.instance().getActiveGroupIcon(teamInfoSmilie, 15));
    }

    private void setEmptyLabel() {
        m_jlName.setText("");
        m_jlName.setIcon(null);
        m_jlWeatherEffect.setIcon(null);
        m_jlSpecialty.setIcon(null);
        m_jlTrainUp.setIcon(null);
        m_jlPositionWarning.setText("");
        m_jlSkill.setText("");
    }

    private void updateDisplay(Player player) {
        // weatherEffect
        m_jlWeatherEffect.setIcon(null);
        if (m_bShowWeatherEffect) {
            int effect = PlayerSpeciality.getWeatherEffect(HOMainFrame.getWeather(), player.getSpecialty());
            if (effect != 0) {
                final Icon wettericon = ThemeManager.getIcon("weather.effect." + effect);
                m_jlWeatherEffect.setIcon(wettericon);
            }
        }

        m_jlSpecialty.setIcon(ImageUtilities.getSmallPlayerSpecialtyIcon(HOIconName.SPECIALTIES[player.getSpecialty()]));

        // positionValue
        if (m_rating != 0f) {
            m_jlSkill.setText(String.format("(%.2f)",  m_rating ));
        } else {
            m_jlSkill.setText("");
        }

        // warning icon iin case of non optimal placement
        if (!m_IsOneOfBestPositions) {
            m_jlPositionWarning.setIcon(ImageUtilities.getSvgIcon(WARNING_ICON, Map.of("foregroundColor", ThemeManager.getColor(HOColorName.WARNING_ICON_CB_COLOR)), 15, 15));
        }
        else{
            m_jlPositionWarning.setIcon(null);
        }

        m_jlTrainUp.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(player).getIcon());

        if (m_bMultiLine) {
            List.of(injuredLabel,
                    bruisedLabel,
                    transferlistedLabel,
                    suspendedLabel,
                    twoYellowCardsLabel,
                    oneYellowCardLabel)
                    .forEach(label -> label.setIcon(null));

            if (player.getInjuryWeeks() > 0) {
                injuredLabel.setIcon(ImageUtilities.getInjuryIcon(12,12));
            } else if (player.getInjuryWeeks() == 0) {
                bruisedLabel.setIcon(ImageUtilities.getPlasterIcon(12,12));
            }
            if (player.getTransferListed() > 0) {
                transferlistedLabel.setIcon(ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, 12, 12));
            }
            if (player.isRedCarded()) {
                suspendedLabel.setIcon(ImageUtilities.getSvgIcon(SUSPENDED_TINY, 12, 12));
            } else if (player.getTotalCards() == 2) {
                twoYellowCardsLabel.setIcon(ImageUtilities.getSvgIcon(TWOYELLOW_TINY, 12, 12));
            } else if (player.getTotalCards() == 1) {
                oneYellowCardLabel.setIcon(ImageUtilities.getSvgIcon(ONEYELLOW_TINY, 12, 12));
            }

            suspendedLabel.getParent().repaint();
        }
    }
}
