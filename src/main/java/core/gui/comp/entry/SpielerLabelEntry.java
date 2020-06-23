package core.gui.comp.entry;

import core.constants.player.PlayerSpeciality;
import core.gui.HOMainFrame;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.training.TrainingPreviewPlayers;
import core.util.Helper;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class SpielerLabelEntry implements IHOTableEntry {

    private Player m_clPlayer;
    private JComponent m_clComponent;
    private final JLabel m_jlGroup = new JLabel();
    private final JLabel m_jlName = new JLabel();
    private final JLabel m_jlSkill = new JLabel();
    private final JLabel m_jlSpezialitaet = new JLabel();
    private final JLabel m_jlWeatherEffect = new JLabel();
    private final JLabel m_jlTrainUp = new JLabel();
    private MatchRoleID m_clCurrentPlayerPosition;
    private boolean m_bShowTrikot;
    private boolean m_bShowWeatherEffect = true;
    private boolean m_bCustomName = false;
    private String m_sCustomNameString = "";
    private float m_fPositionsbewertung;
    private boolean m_bAlternativePosition;
    private boolean m_bMultiLine = false;
    private boolean m_bSelect = false;
    private boolean m_bAssit = false;


     // Label for the player name (depending on status)
    public SpielerLabelEntry(Player player, MatchRoleID positionAktuell,
                             float positionsbewertung, boolean showTrikot, boolean showWetterwarnung) {
        m_clPlayer = player;
        m_clCurrentPlayerPosition = positionAktuell;
        m_fPositionsbewertung = positionsbewertung;
        m_bAlternativePosition = false;
        m_bShowTrikot = showTrikot;
        m_bShowWeatherEffect = showWetterwarnung;
        createComponent();
    }

    // Label for the player name (depending on status)
    public SpielerLabelEntry(Player player, MatchRoleID positionAktuell,
                             float positionsbewertung, boolean showTrikot, boolean showWetterwarnung, boolean customName, String customNameText, boolean multiLine) {
        m_clPlayer = player;
        m_clCurrentPlayerPosition = positionAktuell;
        m_fPositionsbewertung = positionsbewertung;
        m_bAlternativePosition = false;
        m_bShowTrikot = showTrikot;
        m_bShowWeatherEffect = showWetterwarnung;
        m_bCustomName = customName;
        m_sCustomNameString = customNameText;
        m_bMultiLine = multiLine;
        createComponent();
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final JComponent getComponent(boolean isSelected) {
        if (m_bSelect) {
            m_clComponent.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SELECTED));
        } else if (m_bAssit) {
            m_clComponent.setBackground(ThemeManager.getColor(HOColorName.LINEUP_PLAYER_SUB));
        } else {
            m_clComponent.setBackground(isSelected ? HODefaultTableCellRenderer.SELECTION_BG : ColorLabelEntry.BG_STANDARD);
        }

        if (TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getText() != null) {
            m_clComponent.setToolTipText(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getText());
        }

        m_jlName.setForeground(getForegroundForSpieler(m_clPlayer));
        m_jlName.setFont(isSelected ? m_jlName.getFont().deriveFont(Font.BOLD) : m_jlName.getFont().deriveFont(Font.PLAIN));

        m_jlSkill.setForeground(getForegroundForSpieler(m_clPlayer));
        m_jlSkill.setFont(isSelected ? m_jlSkill.getFont().deriveFont(Font.BOLD) : m_jlSkill.getFont().deriveFont(Font.PLAIN));

        return m_clComponent;
    }

    public void setIsSelect(boolean isSelect) {
        m_bSelect = isSelect;
    }

    public void setIsAssit(boolean isSelect) {
        m_bAssit = isSelect;
    }


    public final Player getSpieler() {
        return m_clPlayer;
    }

    public final void clear() {
        m_clPlayer = null;
        m_clCurrentPlayerPosition = null;
        m_fPositionsbewertung = 0f;
        m_bAlternativePosition = false;
        updateComponent();
    }


    public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof SpielerLabelEntry) {
            final SpielerLabelEntry entry = (SpielerLabelEntry) obj;

            return m_clPlayer.getFullName().compareTo(entry.getSpieler().getFullName());
        }

        return 0;
    }

    public final int compareToThird(IHOTableEntry obj) {
        if (obj instanceof SpielerLabelEntry) {
            final SpielerLabelEntry entry = (SpielerLabelEntry) obj;
            int num1 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getSortIndex();
            int num2 = TrainingPreviewPlayers.instance().getTrainPreviewPlayer(entry.m_clPlayer).getSortIndex();

            if (num1 < num2) {
                return -1;
            } else if (num1 > num2) {
                return 1;
            } else {
                return entry.getSpieler().getLastName().compareTo(m_clPlayer.getLastName());
            }
        }
        return 0;
    }
    //-------------------------------------------------------------    

    /**
     * Erstellt eine passende Komponente
     */
    public final void createComponent() {
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
            //Wetterwarnung
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlWeatherEffect.setOpaque(false);
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlWeatherEffect);

            //Spezialität
            m_jlSpezialitaet.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSpezialitaet.setOpaque(false);
            spezPanel.add(m_jlSpezialitaet);

            //Bewertung
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSkill.setOpaque(false);
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlSkill);

            //MiniGruppe
            m_jlGroup.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlGroup.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlGroup.setOpaque(false);
            m_jlGroup.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            spezPanel.add(m_jlGroup);

            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.EAST;
            constraints.weightx = 0.0;
            constraints.gridx = 3;
        } else {
            //Training
            m_jlTrainUp.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlTrainUp.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlTrainUp.setOpaque(false);
            m_jlTrainUp.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            spezPanel.add(m_jlTrainUp);

            //speciality
            m_jlSpezialitaet.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSpezialitaet.setOpaque(false);
            spezPanel.add(m_jlSpezialitaet);

            //Wetterwarnung
            m_jlWeatherEffect.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlWeatherEffect.setOpaque(false);
            m_jlWeatherEffect.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlWeatherEffect);

            //skill
            m_jlSkill.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlSkill.setOpaque(false);
            m_jlSkill.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
            spezPanel.add(m_jlSkill);

            //MiniGruppe
            m_jlGroup.setBackground(ColorLabelEntry.BG_STANDARD);
            m_jlGroup.setVerticalAlignment(SwingConstants.BOTTOM);
            m_jlGroup.setOpaque(false);
            m_jlGroup.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
            spezPanel.add(m_jlGroup);

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
            m_jlName.setForeground(getForegroundForSpieler(m_clPlayer));
            showJersey();
            updateDisplay(m_clPlayer);
        }

        m_clComponent.setPreferredSize(new Dimension(Helper.calcCellWidth(150), Helper.calcCellWidth(18)));
    }

    public final void updateComponent() {
        if (m_clPlayer != null) {
            m_jlName.setForeground(getForegroundForSpieler(m_clPlayer));
            showJersey();
            updateDisplay(m_clPlayer);

        } else {
            setEmptyLabel();
        }
    }

    /**
     * Aktualisierung des Entrys
     */
    public final void updateComponent(Player player, MatchRoleID positionAktuell,
                                      float positionsbewertung, boolean alternativePosition, String nameText) {
        m_clPlayer = player;
        m_clCurrentPlayerPosition = positionAktuell;
        m_fPositionsbewertung = positionsbewertung;
        m_bAlternativePosition = alternativePosition;
        m_sCustomNameString = nameText;

        if (m_clPlayer != null) {
            if (m_clPlayer.isOld()) {
                m_jlName.setForeground(ThemeManager.getColor(HOColorName.PLAYER_OLD_FG));//Color.GRAY);
            } else {
                m_jlName.setForeground(getForegroundForSpieler(m_clPlayer));
            }

            if (m_bCustomName) {
                m_jlName.setText(m_sCustomNameString);
            } else {
                m_jlName.setText(m_clPlayer.getShortName());
            }

            showJersey();
            updateDisplay(m_clPlayer);

        } else {
            setEmptyLabel();
            m_jlGroup.setIcon(null);
        }

        m_clComponent.setPreferredSize(new Dimension(Helper.calcCellWidth(130), Helper.calcCellWidth(18)));  // Was 150,18 - setting lower solved lineup problem
    }

    private void showJersey() {
        // Jersey
        if (m_bShowTrikot) {
            m_jlName.setIcon(ImageUtilities.getJerseyIcon(
                    m_clCurrentPlayerPosition,
                    m_clPlayer.getTrikotnummer()
            ));
            showGroupIcon();
        }
    }


    private void showGroupIcon() {
        String teamInfoSmilie = m_clPlayer.getTeamInfoSmilie();

        if (teamInfoSmilie.trim().equals(""))
            m_jlGroup.setIcon(ImageUtilities.MINILEER);
        else
            m_jlGroup.setIcon(ThemeManager.getScaledIcon(teamInfoSmilie, 10, 10));
    }

    private void setEmptyLabel() {
        m_jlName.setText("");
        m_jlName.setIcon(null);
        m_jlWeatherEffect.setIcon(null);
        m_jlSpezialitaet.setIcon(null);
        m_jlTrainUp.setIcon(null);
        m_jlSkill.setText("");
    }

    private void updateDisplay(Player player) {
        // weatherEffect
        m_jlWeatherEffect.setIcon(null);
        if (m_bShowWeatherEffect) {
            int effect = PlayerSpeciality.getWeatherEffect(HOMainFrame.getWetter(), player.getPlayerSpecialty());
            if (effect != 0) {
                final ImageIcon wettericon = ThemeManager.getIcon("weather.effect." + effect);
                m_jlWeatherEffect.setIcon(wettericon);
            }
        }

        m_jlSpezialitaet.setIcon(ThemeManager.getIcon(HOIconName.SPECIALTIES_SMALL[player.getPlayerSpecialty()]));

        // positionValue
        if (m_bShowTrikot && (m_fPositionsbewertung != 0f) && !m_bAlternativePosition) {
            m_jlSkill.setText("(" + m_fPositionsbewertung + ")");
        } else if (m_bShowTrikot && (m_fPositionsbewertung != 0f) && m_bAlternativePosition) {
            m_jlSkill.setText("(" + m_fPositionsbewertung + ") *");
        } else {
            m_jlSkill.setText("");
        }

        m_jlTrainUp.setIcon(TrainingPreviewPlayers.instance().getTrainPreviewPlayer(m_clPlayer).getIcon());
    }

    //--------------static------------------------------
    // Color player name depending of status (injured, warned, ...)
    public static Color getForegroundForSpieler(Player player) {
        Color color;
        UserParameter userParameter = core.model.UserParameter.instance();

        //On transfert market
        if (player.getTransferlisted() > 0) {
            color = userParameter.FG_TRANSFERMARKT;
        }
        //Injured
        else if (player.getVerletzt() > 0) {
            color = userParameter.FG_INJURED;
        }
        //Disabled
        else if (player.isGesperrt()) {
            color = userParameter.FG_RED_CARD;
        }
        //Yellow card
        else if (player.getVerletzt() == 0) {
            color = userParameter.FG_BRUISED;
        }
        //Red card
        else if (player.getGelbeKarten() == 2) {
            color = userParameter.FG_TWO_YELLOW_CARDS;
        } else {
            color = ThemeManager.getColor(HOColorName.TABLEENTRY_FG);
        }

        return color;
    }
}
