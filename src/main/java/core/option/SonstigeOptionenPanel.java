// %1942107811:de.hattrickorganizer.gui.menu.option%
package core.option;

import core.datatype.CBItem;
import core.datatype.GeldFaktorCBItem;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Alle weiteren Optionen, die Keine Formeln sind
 */
public final class SonstigeOptionenPanel extends ImagePanel implements ChangeListener, ItemListener {

    private static final long serialVersionUID = 1L;

    public static GeldFaktorCBItem[] WAEHRUNGEN = {new GeldFaktorCBItem("Sverige kr", 1f, 1),
            new GeldFaktorCBItem("Great Britain £", 15f, 2), new GeldFaktorCBItem("Euro €", 10f, 3),
            new GeldFaktorCBItem("México Pesos", 1f, 4), new GeldFaktorCBItem("Argentina Pesos", 10f, 5),
            new GeldFaktorCBItem("USA $", 10f, 6), new GeldFaktorCBItem("Norge Kroner", 1f, 7), new GeldFaktorCBItem("Oceania AU$", 5f, 8),
            new GeldFaktorCBItem("Canada C$", 5f, 9), new GeldFaktorCBItem("Colombia Dollar", 1f, 10),
            new GeldFaktorCBItem("South Africa Rand", 1.25f, 11), new GeldFaktorCBItem("Venezuela VZ$", 10f, 12),
            new GeldFaktorCBItem("Prathet Thai Baht", 0.25f, 13), new GeldFaktorCBItem("Egypt E£", 2.5f, 14),
            new GeldFaktorCBItem("Rossiya Roubel", 0.25f, 15), new GeldFaktorCBItem("România Lei", 0.5f, 16),
            new GeldFaktorCBItem("Island Ikr", 0.1f, 17), new GeldFaktorCBItem("Schweiz CHF", 5f, 18),
            new GeldFaktorCBItem("Magyarország hFt", 5f, 19), new GeldFaktorCBItem("Latvija Lats", 20f, 20),
            new GeldFaktorCBItem("Indonesia RP", 1f, 21), new GeldFaktorCBItem("Pilipinas Pesos", 0.25f, 22),
            new GeldFaktorCBItem("Eesti Krooni", 0.5f, 23), new GeldFaktorCBItem("Srbija i Crna Gora New Dinar", 1f, 24),
            new GeldFaktorCBItem("Hrvatska Kuna", 1f, 25), new GeldFaktorCBItem("Hong Kong HKD", 1f, 26),
            new GeldFaktorCBItem("Taiwan $", 10f, 27), new GeldFaktorCBItem("Bulgaria Lev", 5f, 28),
            new GeldFaktorCBItem("Israel NIS", 2f, 29), new GeldFaktorCBItem("Danmark Kroner", 1f, 30),
            new GeldFaktorCBItem("Brasil Real", 5f, 31), new GeldFaktorCBItem("Chile Pesos", 0.05f, 32),
            new GeldFaktorCBItem("India Rupees", 0.25f, 33), new GeldFaktorCBItem("Nippon Yen", 0.1f, 34),
            new GeldFaktorCBItem("Polska Zloty", 2.5f, 35), new GeldFaktorCBItem("Uruguay Pesos", 1f, 36),
            new GeldFaktorCBItem("South Korea Won", 0.01f, 37), new GeldFaktorCBItem("Türkiye Lira", 10f, 38),
            new GeldFaktorCBItem("China Yuan", 1f, 39), new GeldFaktorCBItem("Malaysia MYR", 2.5f, 40),
            new GeldFaktorCBItem("Singapore SG$", 5f, 41), new GeldFaktorCBItem("Ceska republika Koruny", 0.25f, 42)};

    public static CBItem[] NACHKOMMASTELLEN = {new CBItem("1 (1.2)", 1), new CBItem("2 (1.23)", 2)};

    public static CBItem[] TIMEZONES = {new CBItem("-24:00", -24), new CBItem("-23:00", -23), new CBItem("-22:00", -22),
            new CBItem("-21:00", -21), new CBItem("-20:00", -20), new CBItem("-19:00", -19), new CBItem("-18:00", -18),
            new CBItem("-17:00", -17), new CBItem("-16:00", -16), new CBItem("-15:00", -15), new CBItem("-14:00", -14),
            new CBItem("-13:00", -13), new CBItem("-12:00", -12), new CBItem("-11:00", -11), new CBItem("-10:00", -10),
            new CBItem("-09:00", -9), new CBItem("-08:00", -8), new CBItem("-07:00", -7), new CBItem("-06:00", -6),
            new CBItem("-05:00", -5), new CBItem("-04:00", -4), new CBItem("-03:00", -3), new CBItem("-02:00", -2),
            new CBItem("-01:00", -1), new CBItem("00:00", 0), new CBItem("+01:00", 1), new CBItem("+02:00", 2), new CBItem("+03:00", 3),
            new CBItem("+04:00", 4), new CBItem("+05:00", 5), new CBItem("+06:00", 6), new CBItem("+07:00", 7), new CBItem("+08:00", 8),
            new CBItem("+09:00", 9), new CBItem("+10:00", 10), new CBItem("+11:00", 11), new CBItem("+12:00", 12),
            new CBItem("+13:00", 13), new CBItem("+14:00", 14), new CBItem("+15:00", 15), new CBItem("+16:00", 16),
            new CBItem("+17:00", 17), new CBItem("+18:00", 18), new CBItem("+19:00", 19), new CBItem("+20:00", 20),
            new CBItem("+21:00", 21), new CBItem("+22:00", 22), new CBItem("+23:00", 23), new CBItem("+24:00", 24),};

    public CBItem[] SORTIERUNG = {new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.name"), core.model.UserParameter.SORT_NAME),
            new CBItem(HOVerwaltung.instance().getLanguageString("BestePosition"), core.model.UserParameter.SORT_BESTPOS),
            new CBItem(HOVerwaltung.instance().getLanguageString("Aufgestellt"), core.model.UserParameter.SORT_AUFGESTELLT),
            new CBItem(HOVerwaltung.instance().getLanguageString("Gruppe"), core.model.UserParameter.SORT_GRUPPE),
            new CBItem(HOVerwaltung.instance().getLanguageString("Rating"), core.model.UserParameter.SORT_BEWERTUNG),};

    private ComboBoxPanel m_jcbNachkomma;
    private ComboBoxPanel m_jcbSortierung;
    private ComboBoxPanel m_jcbSkin;
    private ComboBoxPanel m_jcbSprachdatei;
    private ComboBoxPanel m_jcbTimeZoneDifference;
    private ComboBoxPanel m_jcbWaehrung;
    private JCheckBox m_jchZahlenBewertung;
    private SliderPanel m_jslDeadline;
    private SliderPanel m_jslMinStaerke;
    private SliderPanel m_jslSchriftgroesse;
    private SliderPanel m_jslWetterEffekt;
    private SliderPanel m_jslFutureWeeks;
    private SliderPanel m_jslAlternativePositionsTolerance;

    /**
     * Creates a new SonstigeOptionenPanel object.
     */
    public SonstigeOptionenPanel() {
        initComponents();
    }

    public static float getFaktorGeld4WaehrungsID(int id) {
        for (int i = 0; i < SonstigeOptionenPanel.WAEHRUNGEN.length; i++) {
            if (id == SonstigeOptionenPanel.WAEHRUNGEN[i].getId()) {
                return SonstigeOptionenPanel.WAEHRUNGEN[i].getFaktor();
            }
        }

        // nix gefunden
        return 1.0f;
    }

    @Override
    public final void itemStateChanged(ItemEvent itemEvent) {
        // Kein Selected Event!
        core.model.UserParameter.temp().zahlenFuerSkill = m_jchZahlenBewertung.isSelected();
        if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            core.model.UserParameter.temp().faktorGeld = ((GeldFaktorCBItem) m_jcbWaehrung.getSelectedItem()).getFaktor();
            core.model.UserParameter.temp().TimeZoneDifference = ((CBItem) m_jcbTimeZoneDifference.getSelectedItem()).getId();
            core.model.UserParameter.temp().nbDecimals = ((CBItem) m_jcbNachkomma.getSelectedItem()).getId();
            core.model.UserParameter.temp().sprachDatei = ((String) m_jcbSprachdatei.getSelectedItem());
            core.model.UserParameter.temp().standardsortierung = ((CBItem) m_jcbSortierung.getSelectedItem()).getId();
            core.model.UserParameter.temp().skin = ((String) m_jcbSkin.getSelectedItem());
        }
        if ((!core.model.UserParameter.temp().sprachDatei.equals(core.model.UserParameter.instance().sprachDatei))
                || (core.model.UserParameter.temp().TimeZoneDifference != core.model.UserParameter.instance().TimeZoneDifference))
            OptionManager.instance().setRestartNeeded();
        if (core.model.UserParameter.temp().zahlenFuerSkill != core.model.UserParameter.instance().zahlenFuerSkill)
            OptionManager.instance().setReInitNeeded();
        if (!core.model.UserParameter.temp().skin.equals(core.model.UserParameter.instance().skin)) {
            OptionManager.instance().setSkinChanged();
            OptionManager.instance().setRestartNeeded();
        }
    }

    @Override
    public final void stateChanged(ChangeEvent changeEvent) {
        core.model.UserParameter.temp().deadlineFrist = (int) m_jslDeadline.getValue();
        core.model.UserParameter.temp().MinIdealPosStk = m_jslMinStaerke.getValue();
        core.model.UserParameter.temp().WetterEffektBonus = m_jslWetterEffekt.getValue();
        core.model.UserParameter.temp().futureWeeks = (int) m_jslFutureWeeks.getValue();
        core.model.UserParameter.temp().schriftGroesse = (int) m_jslSchriftgroesse.getValue();
        core.model.UserParameter.temp().alternativePositionsTolerance = m_jslAlternativePositionsTolerance.getValue();

        if (core.model.UserParameter.temp().schriftGroesse != core.model.UserParameter.instance().schriftGroesse) {
            OptionManager.instance().setSkinChanged();
            OptionManager.instance().setRestartNeeded();
        }
        if (core.model.UserParameter.temp().futureWeeks != core.model.UserParameter.instance().futureWeeks) {
            OptionManager.instance().setReInitNeeded();
        }
        if (core.model.UserParameter.temp().alternativePositionsTolerance != core.model.UserParameter.instance().alternativePositionsTolerance) {
            OptionManager.instance().setReInitNeeded();
        }
    }

    /**
     * Init components
     */
    private void initComponents() {
        setLayout(new GridLayout(12, 1, 4, 4));

        m_jslDeadline = new SliderPanel(HOVerwaltung.instance().getLanguageString("TransferWecker"), 60, 0, 1f / 60000f, 1.0f, 120);
        m_jslDeadline.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_TransferWecker"));
        m_jslDeadline.setValue((float) core.model.UserParameter.temp().deadlineFrist);
        m_jslDeadline.addChangeListener(this);
        add(m_jslDeadline);

        m_jslMinStaerke = new SliderPanel(HOVerwaltung.instance().getLanguageString("MinStaerkeIdealPos"), 100, 0, 10, 0.1f, 120);
        m_jslMinStaerke.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_MinStaerkeIdealPos"));
        m_jslMinStaerke.setValue(core.model.UserParameter.temp().MinIdealPosStk);
        m_jslMinStaerke.addChangeListener(this);
        add(m_jslMinStaerke);

        m_jslWetterEffekt = new SliderPanel(HOVerwaltung.instance().getLanguageString("Wettereffekt"), 100, 0, 100, 1f, 120);
        m_jslWetterEffekt.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Wettereffekt"));
        m_jslWetterEffekt.setValue(core.model.UserParameter.temp().WetterEffektBonus);
        m_jslWetterEffekt.addChangeListener(this);
        add(m_jslWetterEffekt);

        m_jslFutureWeeks = new SliderPanel(HOVerwaltung.instance().getLanguageString("futureWeeks"), 80, 0, 1, 1f, 120);
        m_jslFutureWeeks.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_futureWeeks"));
        m_jslFutureWeeks.setValue(core.model.UserParameter.temp().futureWeeks);
        m_jslFutureWeeks.addChangeListener(this);
        add(m_jslFutureWeeks);

        m_jslSchriftgroesse = new SliderPanel(HOVerwaltung.instance().getLanguageString("Schriftgroesse"), 16, 8, 1, 1.0f, 120);
        m_jslSchriftgroesse.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Schriftgroesse"));
        m_jslSchriftgroesse.setValue(core.model.UserParameter.temp().schriftGroesse);
        m_jslSchriftgroesse.addChangeListener(this);
        add(m_jslSchriftgroesse);

        m_jcbSkin = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("options.misc.skin"), new String[]{"Nimbus", "Classic", "JGoodies Green", //
                "JGoodies Silver", "JGoodies Sky", "JGoodies Blue", "JGoodies Royale", "System"}, 120);
        m_jcbSkin.setSelectedItem(core.model.UserParameter.temp().skin);
        m_jcbSkin.addItemListener(this);
        add(m_jcbSkin);

        final String[] sprachdateien = HOVerwaltung.getLanguageFileNames();
        try {
            java.util.Arrays.sort(sprachdateien);
        } catch (Exception e) {
        }
        m_jcbSprachdatei = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("pref.misc.language"), sprachdateien, 120);
        m_jcbSprachdatei.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Sprachdatei"));
        m_jcbSprachdatei.setSelectedItem(core.model.UserParameter.temp().sprachDatei);
        m_jcbSprachdatei.addItemListener(this);
        add(m_jcbSprachdatei);

        m_jcbTimeZoneDifference = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("options_TimeZone"), TIMEZONES, 120);
        m_jcbTimeZoneDifference.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Options_TimeZone"));
        m_jcbTimeZoneDifference.setSelectedId(core.model.UserParameter.temp().TimeZoneDifference);
        m_jcbTimeZoneDifference.addItemListener(this);
        add(m_jcbTimeZoneDifference);

        java.util.Arrays.sort(WAEHRUNGEN);
        m_jcbWaehrung = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("Waehrungsfaktor"), WAEHRUNGEN, 120);
        m_jcbWaehrung.setSelectedId(core.model.UserParameter.temp().waehrungsID);
        m_jcbWaehrung.addItemListener(this);
        // add( m_jcbWaehrung );

        m_jcbSortierung = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("Defaultsortierung"), SORTIERUNG, 120);
        m_jcbSortierung.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Defaultsortierung"));
        m_jcbSortierung.setSelectedId(core.model.UserParameter.temp().standardsortierung);
        m_jcbSortierung.addItemListener(this);
        add(m_jcbSortierung);

        m_jcbNachkomma = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("Nachkommastellen"), NACHKOMMASTELLEN, 120);
        m_jcbNachkomma.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_Nachkommastellen"));
        m_jcbNachkomma.setSelectedId(core.model.UserParameter.temp().nbDecimals);
        m_jcbNachkomma.addItemListener(this);
        add(m_jcbNachkomma);

        m_jchZahlenBewertung = new JCheckBox(HOVerwaltung.instance().getLanguageString("SkillZahlen") + " : "
                + HOVerwaltung.instance().getLanguageString("ls.player.skill.value.passable") + " (6)");
        m_jchZahlenBewertung.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Optionen_SkillZahlen"));
        m_jchZahlenBewertung.setOpaque(false);
        m_jchZahlenBewertung.setSelected(core.model.UserParameter.temp().zahlenFuerSkill);
        m_jchZahlenBewertung.addItemListener(this);
        add(m_jchZahlenBewertung);

        m_jslAlternativePositionsTolerance = new SliderPanel(HOVerwaltung.instance().getLanguageString("Tolleranza Posi Alternative (%)"), 100, 0, 100, 1f, 120);
        m_jslAlternativePositionsTolerance.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Tolleranza Posi Alternative"));
        m_jslAlternativePositionsTolerance.setValue(UserParameter.temp().alternativePositionsTolerance);
        m_jslAlternativePositionsTolerance.addChangeListener(this);
        add(m_jslAlternativePositionsTolerance);

    }
}
