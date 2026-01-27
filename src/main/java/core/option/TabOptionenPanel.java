// %3158012967:de.hattrickorganizer.gui.menu.option%
package core.option;


import core.gui.comp.panel.ImagePanel;
import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;


/**
 * Alle weiteren Optionen, die Keine Formeln sind
 */
final class TabOptionenPanel extends ImagePanel implements java.awt.event.ItemListener {
    //~ Instance fields ----------------------------------------------------------------------------

//	private JCheckBox m_jchArenasizer;
    private JCheckBox m_jchAufstellung;
    private JCheckBox m_jchInformation;
    private JCheckBox m_jchLigatabelle;
    private JCheckBox m_jchSpiele;
    private JCheckBox m_jchSpielerAnalyse;

    //private ComboBoxPanel       m_jcbHTIP           = null;
    private JCheckBox m_jchSpieleruebersicht;
    private JCheckBox m_jchStatistik;


    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TabOptionenPanel object.
     */
    protected TabOptionenPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void itemStateChanged(ItemEvent itemEvent) {

    	// New Tab can not be shown immediately
    	if (itemEvent.getStateChange()== ItemEvent.SELECTED)
    		OptionManager.instance().setRestartNeeded();
    	// ReInit deletes the deselected Tab
    	if (itemEvent.getStateChange()== ItemEvent.DESELECTED)
    		OptionManager.instance().setReInitNeeded();

        core.model.UserParameter.temp().tempTabSpieleruebersicht = !m_jchSpieleruebersicht.isSelected();
        core.model.UserParameter.temp().tempTabAufstellung = !m_jchAufstellung.isSelected();
        core.model.UserParameter.temp().tempTabLigatabelle = !m_jchLigatabelle.isSelected();
        core.model.UserParameter.temp().tempTabSpiele = !m_jchSpiele.isSelected();
        core.model.UserParameter.temp().tempTabSpieleranalyse = !m_jchSpielerAnalyse.isSelected();
        core.model.UserParameter.temp().tempTabStatistik = !m_jchStatistik.isSelected();
        core.model.UserParameter.temp().tempTabInformation = !m_jchInformation.isSelected();
    }

    private void initComponents() {
        setLayout(new GridLayout(9, 1, 4, 4));

        //        m_jcbHTIP= new ComboBoxPanel( model.TranslationFacility.tr( "Hattrick" ), HT_IP_ADRESSEN, 120 );
        //        m_jcbHTIP.setSelectedItem ( gui.UserParameter.temp ().htip );
        //        m_jcbHTIP.addItemListener ( this );
        //        add( m_jcbHTIP );
        m_jchSpieleruebersicht = new JCheckBox(TranslationFacility.tr("Spieleruebersicht"));
        m_jchSpieleruebersicht.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchSpieleruebersicht.setOpaque(false);
        m_jchSpieleruebersicht.setSelected(!core.model.UserParameter.temp().tempTabSpieleruebersicht);
        m_jchSpieleruebersicht.addItemListener(this);
        add(m_jchSpieleruebersicht);

        m_jchAufstellung = new JCheckBox(TranslationFacility.tr("Aufstellung"));
        m_jchAufstellung.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchAufstellung.setOpaque(false);
        m_jchAufstellung.setSelected(!core.model.UserParameter.temp().tempTabAufstellung);
        m_jchAufstellung.addItemListener(this);
        add(m_jchAufstellung);

        m_jchLigatabelle = new JCheckBox(TranslationFacility.tr("Ligatabelle"));
        m_jchLigatabelle.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchLigatabelle.setOpaque(false);
        m_jchLigatabelle.setSelected(!core.model.UserParameter.temp().tempTabLigatabelle);
        m_jchLigatabelle.addItemListener(this);
        add(m_jchLigatabelle);

        m_jchSpiele = new JCheckBox(TranslationFacility.tr("Tab_Title_Matches"));
        m_jchSpiele.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchSpiele.setOpaque(false);
        m_jchSpiele.setSelected(!core.model.UserParameter.temp().tempTabSpiele);
        m_jchSpiele.addItemListener(this);
        add(m_jchSpiele);

        m_jchSpielerAnalyse = new JCheckBox(TranslationFacility.tr("SpielerAnalyse"));
        m_jchSpielerAnalyse.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchSpielerAnalyse.setOpaque(false);
        m_jchSpielerAnalyse.setSelected(!core.model.UserParameter.temp().tempTabSpieleranalyse);
        m_jchSpielerAnalyse.addItemListener(this);
        add(m_jchSpielerAnalyse);

        m_jchStatistik = new JCheckBox(TranslationFacility.tr("Statistik"));
        m_jchStatistik.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchStatistik.setOpaque(false);
        m_jchStatistik.setSelected(!core.model.UserParameter.temp().tempTabStatistik);
        m_jchStatistik.addItemListener(this);
        add(m_jchStatistik);

        m_jchInformation = new JCheckBox(TranslationFacility.tr("Verschiedenes"));
        m_jchInformation.setToolTipText(TranslationFacility.tr("tt_Optionen_TabManagement"));
        m_jchInformation.setOpaque(false);
        m_jchInformation.setSelected(!core.model.UserParameter.temp().tempTabInformation);
        m_jchInformation.addItemListener(this);
        add(m_jchInformation);

    }
}
