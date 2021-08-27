package core.option;

import core.datatype.CBItem;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.Theme;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.DateTimeUtils;
import core.util.HOLogger;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public final class GeneralSettingsPanel extends ImagePanel implements ChangeListener, ItemListener {

    public static CBItem[] NB_DECIMALS = {new CBItem("1 (1.2)", 1), new CBItem("2 (1.23)", 2)};

    public CBItem[] DEFAULT_SORTING = {new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.name"), core.model.UserParameter.SORT_NAME),
            new CBItem(HOVerwaltung.instance().getLanguageString("BestePosition"), core.model.UserParameter.SORT_BESTPOS),
            new CBItem(HOVerwaltung.instance().getLanguageString("Aufgestellt"), core.model.UserParameter.SORT_AUFGESTELLT),
            new CBItem(HOVerwaltung.instance().getLanguageString("Gruppe"), core.model.UserParameter.SORT_GRUPPE),
            new CBItem(HOVerwaltung.instance().getLanguageString("Rating"), core.model.UserParameter.SORT_BEWERTUNG),};


    private ComboBoxPanel m_jcbNbDecimals;
    private ComboBoxPanel m_jcbDefaultSorting;
    private ComboBoxPanel m_jcbSkin;
    private ComboBoxPanel m_jcbLanguage;
    private ComboBoxPanel m_jcbTimeZone;
    private JCheckBox m_jchShowSkillNumericalValue;
    private SliderPanel m_jslFontSize;
    private SliderPanel m_jslAlternativePositionsTolerance;
    private JCheckBox m_jcbPromotionStatusTest;

    /**
     * Creates a new SonstigeOptionenPanel object.
     */
    public GeneralSettingsPanel() {
        initComponents();
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        // No  Selected Event!
        core.model.UserParameter.temp().zahlenFuerSkill = m_jchShowSkillNumericalValue.isSelected();
        UserParameter.temp().promotionManagerTest = m_jcbPromotionStatusTest.isSelected();

        if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            core.model.UserParameter.temp().TimeZoneDifference = ((CBItem) m_jcbTimeZone.getSelectedItem()).getId();
            core.model.UserParameter.temp().nbDecimals = ((CBItem) m_jcbNbDecimals.getSelectedItem()).getId();
            core.model.UserParameter.temp().sprachDatei = ((String) m_jcbLanguage.getSelectedItem());
            core.model.UserParameter.temp().standardsortierung = ((CBItem) m_jcbDefaultSorting.getSelectedItem()).getId();
            core.model.UserParameter.temp().skin = ((String) m_jcbSkin.getSelectedItem());
        }
        if (!core.model.UserParameter.temp().sprachDatei.equals(core.model.UserParameter.instance().sprachDatei)
                || (core.model.UserParameter.temp().TimeZoneDifference != core.model.UserParameter.instance().TimeZoneDifference)
                || (UserParameter.temp().promotionManagerTest != UserParameter.instance().promotionManagerTest))
            OptionManager.instance().setRestartNeeded();
        if (core.model.UserParameter.temp().zahlenFuerSkill != core.model.UserParameter.instance().zahlenFuerSkill)
            OptionManager.instance().setReInitNeeded();
        if (!core.model.UserParameter.temp().skin.equals(core.model.UserParameter.instance().skin)) {
            OptionManager.instance().setSkinChanged();
            OptionManager.instance().setRestartNeeded();
        }
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        core.model.UserParameter.temp().fontSize = (int) m_jslFontSize.getValue();
        core.model.UserParameter.temp().alternativePositionsTolerance = m_jslAlternativePositionsTolerance.getValue();

        if (core.model.UserParameter.temp().fontSize != core.model.UserParameter.instance().fontSize) {
            OptionManager.instance().setSkinChanged();
            OptionManager.instance().setRestartNeeded();
        }
        if (core.model.UserParameter.temp().alternativePositionsTolerance != core.model.UserParameter.instance().alternativePositionsTolerance) {
            OptionManager.instance().setReInitNeeded();
        }
    }

    /**
     * Init components
     */
    private void initComponents() {
        setLayout(new GridLayout(10, 1, 4, 4));

        add (new JLabel(" "));

        m_jslFontSize = new SliderPanel(HOVerwaltung.instance().getLanguageString("Schriftgroesse"), 16, 8, 1, 1.0f, 120);
        m_jslFontSize.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Schriftgroesse"));
        m_jslFontSize.setValue(core.model.UserParameter.temp().fontSize);
        m_jslFontSize.addChangeListener(this);
        add(m_jslFontSize);

        List<Theme> registeredThemes = ThemeManager.instance().getRegisteredThemes();
        m_jcbSkin = new ComboBoxPanel(
                HOVerwaltung.instance().getLanguageString("options.misc.skin"),
                registeredThemes.stream().map(Theme::getName).toArray(),
                120
        );
        m_jcbSkin.setSelectedItem(core.model.UserParameter.temp().skin);
        m_jcbSkin.addItemListener(this);
        add(m_jcbSkin);

        final String[] sprachdateien = HOVerwaltung.getLanguageFileNames();
        try {
            java.util.Arrays.sort(sprachdateien);
        } catch (Exception e) {
        }
        m_jcbLanguage = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("ls.core.preferences.misc.language"), sprachdateien, 120);
        m_jcbLanguage.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Sprachdatei"));
        m_jcbLanguage.setSelectedItem(core.model.UserParameter.temp().sprachDatei);
        m_jcbLanguage.addItemListener(this);
        add(m_jcbLanguage);

        // TimeZone selection =========================================================
        CBItem[] allZoneIdsAndItsOffSet = getAllZoneIdsAndItsOffSet();
        m_jcbTimeZone = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("ls.core.preferences.misc.timezone"), allZoneIdsAndItsOffSet, 120);
        var sZoneIDs = ZoneId.getAvailableZoneIds();
        var sZoneIDCodes = sZoneIDs.stream().map(String::hashCode).collect(Collectors.toSet());
        if (sZoneIDs.size() != sZoneIDCodes.size()){
            HOLogger.instance().error(getClass(), "Error: non unique TimeZone detected, another approach should be found for initilization");
        }

        if(! sZoneIDCodes.contains(core.model.UserParameter.temp().TimeZoneDifference)){
            ZoneId zoneId;
            try {
                zoneId = ZoneId.systemDefault();
                HOLogger.instance().info(getClass(), "Setting Timezone to system default: '" + zoneId.getId() + "'");
            } catch (Exception e) {
                zoneId = ZoneId.of("Europe/Stockholm");
                HOLogger.instance().error(getClass(), "System Timezone could not be identified, setting it to 'Europe/Stockholm'");
            }

            int iZoneIDCode = zoneId.getId().hashCode();
            core.model.UserParameter.temp().TimeZoneDifference = iZoneIDCode;

        }

        m_jcbTimeZone.setSelectedId(core.model.UserParameter.temp().TimeZoneDifference);

        m_jcbTimeZone.addItemListener(this);
        add(m_jcbTimeZone);

        m_jcbDefaultSorting = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("Defaultsortierung"), DEFAULT_SORTING, 120);
        m_jcbDefaultSorting.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Defaultsortierung"));
        m_jcbDefaultSorting.setSelectedId(core.model.UserParameter.temp().standardsortierung);
        m_jcbDefaultSorting.addItemListener(this);
        add(m_jcbDefaultSorting);

        m_jcbNbDecimals = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("ls.core.preferences.misc.nb_decimals"), NB_DECIMALS, 120);
        m_jcbNbDecimals.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Nachkommastellen"));
        m_jcbNbDecimals.setSelectedId(core.model.UserParameter.temp().nbDecimals);
        m_jcbNbDecimals.addItemListener(this);
        add(m_jcbNbDecimals);

        m_jchShowSkillNumericalValue = new JCheckBox(HOVerwaltung.instance().getLanguageString("SkillZahlen") + " : "
                + HOVerwaltung.instance().getLanguageString("ls.player.skill.value.passable") + " (6)");
        m_jchShowSkillNumericalValue.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_SkillZahlen"));
        m_jchShowSkillNumericalValue.setOpaque(false);
        m_jchShowSkillNumericalValue.setSelected(core.model.UserParameter.temp().zahlenFuerSkill);
        m_jchShowSkillNumericalValue.addItemListener(this);
        add(m_jchShowSkillNumericalValue);

        m_jslAlternativePositionsTolerance = new SliderPanel(HOVerwaltung.instance().getLanguageString("options.Alternative_Position_Tolerance"), 100, -1, 100, 1f, 120);
        m_jslAlternativePositionsTolerance.setToolTipText(HOVerwaltung.instance().getLanguageString("options.tt_Alternative_Position_Tolerance"));
        m_jslAlternativePositionsTolerance.setValue(UserParameter.temp().alternativePositionsTolerance);
        m_jslAlternativePositionsTolerance.addChangeListener(this);
        add(m_jslAlternativePositionsTolerance);

        m_jcbPromotionStatusTest = new JCheckBox(HOVerwaltung.instance().getLanguageString("PMStatusTest"));
        m_jcbPromotionStatusTest.setOpaque(false);
        m_jcbPromotionStatusTest.setSelected(UserParameter.temp().promotionManagerTest);
        m_jcbPromotionStatusTest.addItemListener(this);
        add(m_jcbPromotionStatusTest);
    }

    private static CBItem[] getAllZoneIdsAndItsOffSet() {
        Map<String, String> zones = DateTimeUtils.getAvailableZoneIds();

        List<CBItem> result = new ArrayList<>();
        zones.forEach((k, v) -> result.add(new CBItem(k + " " + v, k.hashCode())));

        return result.toArray(CBItem[]::new);
    }
}
