package core.option;

import core.datatype.CBItem;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public final class LineupSettingsPanel extends ImagePanel implements ItemListener {

    private static CBItem[] orientationSetting = {
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.goalkeeperattop"), UserParameter.GOALKEEPER_AT_TOP),
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.goalkeeperatbottom"), UserParameter.GOALKEEPER_AT_BOTTOM)
    };

    private static CBItem[] positionNameSetting = {
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.positionnames.short"), UserParameter.POSITIONNAMES_SHORT),
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.positionnames.long"), UserParameter.POSITIONNAMES_LONG)
    };

    /* TODO: check usability (option settings versus toggle box in lineup rating panel
             prediction list needs to be dynamic (loaded from prediction/predictionTypes.conf)
    private static CBItem[] ratingPredictionModelSetting = {
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.ratingpredictionmodel.new"), UserParameter.RATINGPREDICTIONMODEL_NEW),
            new CBItem(HOVerwaltung.instance().getLanguageString("ls.lineupsettings.ratingpredictionmodel.old"), UserParameter.RATINGPREDICTIONMODEL_OLD)
    };
    */

    private ComboBoxPanel m_jcbOrientationSetting;
    private ComboBoxPanel m_jcbPositionNameSetting;
    // private ComboBoxPanel m_jcbRatingPredictionModelSetting;

    /**
     * Creates a new SonstigeOptionenPanel object.
     */
    public LineupSettingsPanel() {
        initComponents();
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
            UserParameter.temp().lineupOrientationSetting = ((CBItem) m_jcbOrientationSetting.getSelectedItem()).getId();
            UserParameter.temp().lineupPositionNamesSetting = ((CBItem) m_jcbPositionNameSetting.getSelectedItem()).getId();
            //UserParameter.temp().lineupRatingPredictionModelSetting = ((CBItem) m_jcbRatingPredictionModelSetting.getSelectedItem()).getId();
        }
        if (UserParameter.temp().lineupPositionNamesSetting != UserParameter.instance().lineupPositionNamesSetting) {
            OptionManager.instance().setReInitNeeded();
        }
        /*if ((UserParameter.temp().lineupRatingPredictionModelSetting != UserParameter.instance().lineupRatingPredictionModelSetting)) {
            OptionManager.instance().setReInitNeeded();
        }*/

        if ( UserParameter.temp().lineupOrientationSetting != UserParameter.instance().lineupOrientationSetting){
            OptionManager.instance().setRestartNeeded();
        }
    }

    /**
     * Init components
     */
    private void initComponents() {
        setLayout(new GridLayout(10, 1, 4, 4));

        add (new JLabel(" "));
        m_jcbOrientationSetting = addLineupSettingComboBox("orientation", orientationSetting, UserParameter.temp().lineupOrientationSetting);
        m_jcbPositionNameSetting = addLineupSettingComboBox("positionnames", positionNameSetting, UserParameter.temp().lineupPositionNamesSetting);
        //m_jcbRatingPredictionModelSetting = addLineupSettingComboBox( "ratingpredictionmodel", ratingPredictionModelSetting, UserParameter.temp().lineupRatingPredictionModelSetting);
        add (new JLabel(" "));
        add (new JLabel(" "));
        add (new JLabel(" "));
        add (new JLabel(" "));
        add (new JLabel(" "));
        add (new JLabel(" "));
        add (new JLabel(" "));

    }

    private ComboBoxPanel addLineupSettingComboBox( String languageKey, CBItem[] cbItems, int settingValue) {
        var comboBoxPanel = new ComboBoxPanel(HOVerwaltung.instance().getLanguageString("ls.lineup."+ languageKey), cbItems, 120 );
        comboBoxPanel.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.lineup."+ languageKey + ".tooltip"));
        comboBoxPanel.setSelectedId(settingValue);
        comboBoxPanel.addItemListener(this);
        add(comboBoxPanel);
        return comboBoxPanel;
    }
}
