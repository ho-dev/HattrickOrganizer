package core.option;


import core.gui.comp.panel.ImagePanel;
import core.model.TranslationFacility;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;


/**
 * Checkboxes in Download Dialog checked or not
 *
 * (DownloadPanel is not used anymore - the download options are now stored in DownloadDialog)
 */
public final class DownloadPanel extends ImagePanel
    implements javax.swing.event.ChangeListener, java.awt.event.ItemListener
{
    //~ Static fields/initializers -----------------------------------------------------------------
	private JCheckBox m_jchXMLDownload;
	private JCheckBox m_jchCurrentMatchlist;
    private JCheckBox m_jchFixtures;
    private JCheckBox m_jchShowSaveDialog;

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
        core.model.UserParameter.temp().downloadCurrentMatchlist = m_jchCurrentMatchlist.isSelected();
        core.model.UserParameter.temp().showHRFSaveDialog = m_jchShowSaveDialog.isSelected();
    }

	public void stateChanged(ChangeEvent arg0) {

	}

    private void initComponents() {
        setLayout(new GridBagLayout());

		GridBagConstraints placement = new GridBagConstraints();
		placement.anchor = GridBagConstraints.NORTHWEST;
		placement.fill = GridBagConstraints.BOTH;
		placement.insets = new Insets(20, 4, 20, 4);
		placement.gridx = 0;

		m_jchXMLDownload = new JCheckBox(TranslationFacility.tr("download.teamdata"));
		m_jchXMLDownload.setToolTipText(TranslationFacility.tr("download.teamdata.tt"));
		m_jchXMLDownload.setOpaque(false);
		m_jchXMLDownload.setSelected(core.model.UserParameter.temp().xmlDownload);
		m_jchXMLDownload.addItemListener(this);
		placement.gridy = 0;
		add(m_jchXMLDownload, placement);

		m_jchCurrentMatchlist = new JCheckBox(TranslationFacility.tr("download.currentmatches"));
		m_jchCurrentMatchlist.setToolTipText(TranslationFacility.tr("download.currentmatches.tt"));
		m_jchCurrentMatchlist.setOpaque(false);
		m_jchCurrentMatchlist.setSelected(core.model.UserParameter.temp().downloadCurrentMatchlist);
		m_jchCurrentMatchlist.addItemListener(this);
		placement.gridy = 1;
		add(m_jchCurrentMatchlist, placement);


		m_jchFixtures = new JCheckBox(TranslationFacility.tr("download.seriesdata"));
		m_jchFixtures.setToolTipText(TranslationFacility.tr("download.seriesdata.tt"));
		m_jchFixtures.setOpaque(false);
		m_jchFixtures.setSelected(core.model.UserParameter.temp().fixtures);
		m_jchFixtures.addItemListener(this);
		placement.gridy = 2;
		add(m_jchFixtures, placement);

		placement.gridy = 3;
		add(new JSeparator(), placement);

		m_jchShowSaveDialog = new JCheckBox(TranslationFacility.tr("Show_SaveHRF_Dialog"));
		m_jchShowSaveDialog.setToolTipText(TranslationFacility.tr("tt_Optionen_Show_SaveHRF_Dialog"));
		m_jchShowSaveDialog.setOpaque(false);
		m_jchShowSaveDialog.setSelected(core.model.UserParameter.temp().showHRFSaveDialog);
		m_jchShowSaveDialog.addItemListener(this);
		placement.gridy = 4;
		add(m_jchShowSaveDialog, placement);

		placement.gridy = 5;
		placement.weightx = 1;
		placement.weighty = 1;
		add(new JLabel(""), placement);
    }

}
