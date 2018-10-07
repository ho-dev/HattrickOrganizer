package core.option;



import core.gui.comp.panel.ImagePanel;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;


/**
 * Checkboxes in Download Dialog checked or not 
 */
public final class DownloadPanel extends ImagePanel
    implements javax.swing.event.ChangeListener, java.awt.event.ItemListener
{
    //~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private JCheckBox m_jchXMLDownload;
	private JCheckBox m_jchCurrentMatchlist;    
    private JCheckBox m_jchFixtures;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new DownloadPanel object.
     */
    public DownloadPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void itemStateChanged(java.awt.event.ItemEvent itemEvent) {        
        core.model.UserParameter.temp().xmlDownload = m_jchXMLDownload.isSelected();
        core.model.UserParameter.temp().fixtures = m_jchFixtures.isSelected();
        core.model.UserParameter.temp().currentMatchlist = m_jchCurrentMatchlist.isSelected();
    }

	public void stateChanged(ChangeEvent arg0) {
				
	}

    private void initComponents() {
        setLayout(new GridLayout(10, 1, 4, 4));

		m_jchXMLDownload = new JCheckBox(core.model.HOVerwaltung.instance().getLanguageString("download.teamdata"));
		m_jchXMLDownload.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("download.teamdata.tt"));
		m_jchXMLDownload.setOpaque(false);
		m_jchXMLDownload.setSelected(core.model.UserParameter.temp().xmlDownload);
		m_jchXMLDownload.addItemListener(this);
		add(m_jchXMLDownload);

		m_jchCurrentMatchlist = new JCheckBox(core.model.HOVerwaltung.instance().getLanguageString("download.currentmatches"));
		m_jchCurrentMatchlist.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("download.currentmatches.tt"));
		m_jchCurrentMatchlist.setOpaque(false);
		m_jchCurrentMatchlist.setSelected(core.model.UserParameter.temp().currentMatchlist);
		m_jchCurrentMatchlist.addItemListener(this);
		add(m_jchCurrentMatchlist);


        m_jchFixtures = new JCheckBox(core.model.HOVerwaltung.instance().getLanguageString("download.seriesdata"));
        m_jchFixtures.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("download.seriesdata.tt"));
        m_jchFixtures.setOpaque(false);
        m_jchFixtures.setSelected(core.model.UserParameter.temp().fixtures);
        m_jchFixtures.addItemListener(this);
        add(m_jchFixtures);


        for(int i = 0; i < 3; i++) {
        	add(new JLabel(""));
        }
    }

}