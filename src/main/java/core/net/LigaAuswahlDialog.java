// %2333829392:de.hattrickorganizer.gui.menu%
package core.net;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Auswahl der Liga zu einer Season, um den richtigen Spielplan zu ziehen
 */
public class LigaAuswahlDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6165662981528850898L;

    //~ Instance fields ----------------------------------------------------------------------------

	private JButton m_jbAbbrechen = new JButton(TranslationFacility.tr("ls.button.cancel"));
    private JButton m_jbOk = new JButton(TranslationFacility.tr("ls.button.download"));
    private JComboBox m_jcbLiga;
    private JRadioButton m_jrbLigaAktuell = new JRadioButton(TranslationFacility.tr("AktuelleLiga"),true);
    private JRadioButton m_jrbLigaAndere = new JRadioButton(TranslationFacility.tr("AndereLiga"),false);
    private JCheckBox m_jcbReuseSelection = new JCheckBox(TranslationFacility.tr("ls.selection.reuse"));
    private int m_iLigaId = -2;
    private int ownLeagueId;
    private boolean reuseEnabled;
    private boolean isAborted=false;
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new LigaAuswahlDialog object.
     */
    public LigaAuswahlDialog(JDialog owner, int seasonid, int leagueId, boolean reuseEnabled) {
        super(owner, TranslationFacility.tr("Liga"),true);
        this.reuseEnabled = reuseEnabled;
        ownLeagueId = leagueId;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents(seasonid);
    }

    public final int getLigaID() {
        return m_iLigaId;
    }

    @Override
	public final void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(m_jbOk)) {
            if (m_jrbLigaAktuell.isSelected()) {
                m_iLigaId = ownLeagueId;
                setVisible(false);
            }
            else if (m_jcbLiga.getSelectedItem() != null) {
                m_iLigaId = parseInt(this, m_jcbLiga.getSelectedItem().toString(), false);

                if (m_iLigaId > -1) {
                    setVisible(false);
                }
            }
        } else if (e.getSource().equals(m_jbAbbrechen)) {
            this.isAborted = true;
            setVisible(false);
        } else if ((e.getSource().equals(m_jrbLigaAktuell))
                   || (e.getSource().equals(m_jrbLigaAndere))) {
            m_jcbLiga.setEnabled(m_jrbLigaAndere.isSelected());
        }
    }

    private Integer[] fillCB() {
        //Alle möglichen LigaIDs holen
        return core.db.DBManager.instance().getAllLigaIDs();
    }

    private void initComponents( int seasonid) {
        setContentPane(new ImagePanel(new GridLayout(5, 2, 4, 4)));

        JLabel label = new JLabel(TranslationFacility.tr("Season"));
        getContentPane().add(label);

        final JTextField textfield = new JTextField(seasonid + "");
        textfield.setEditable(false);
        getContentPane().add(textfield);

        final ButtonGroup bg = new ButtonGroup();

        m_jrbLigaAktuell.setToolTipText(TranslationFacility.tr("tt_LigaDownload_Aktuell"));
        m_jrbLigaAktuell.setOpaque(false);
        m_jrbLigaAktuell.addActionListener(this);
        bg.add(m_jrbLigaAktuell);
        getContentPane().add(m_jrbLigaAktuell);

        label = new JLabel(""+ ownLeagueId);
        getContentPane().add(label);

        m_jrbLigaAndere.setToolTipText(TranslationFacility.tr("tt_LigaDownload_Andere"));
        m_jrbLigaAndere.setOpaque(false);
        m_jrbLigaAndere.addActionListener(this);
        bg.add(m_jrbLigaAndere);
        getContentPane().add(m_jrbLigaAndere);

        m_jcbLiga = new JComboBox(fillCB());
        m_jcbLiga.setToolTipText(TranslationFacility.tr("tt_LigaDownload_LigaID"));
        m_jcbLiga.setEnabled(false);
        m_jcbLiga.setSelectedItem(DBManager.instance().getLigaID4SaisonID(seasonid));
        m_jcbLiga.setEditable(true);
        getContentPane().add(m_jcbLiga);

        m_jcbReuseSelection.setToolTipText(TranslationFacility.tr("ls.tt.selection.reuse"));
        m_jcbReuseSelection.setEnabled(this.reuseEnabled);
        getContentPane().add(m_jcbReuseSelection);
        getContentPane().add(new JLabel("")); // placeholder

        m_jbOk.setToolTipText(TranslationFacility.tr("tt_Download_Start"));
        m_jbOk.addActionListener(this);
        getContentPane().add(m_jbOk);

        m_jbAbbrechen.setToolTipText(TranslationFacility.tr("tt_Download_Abbrechen"));
        m_jbAbbrechen.addActionListener(this);
        getContentPane().add(m_jbAbbrechen);

        setSize(250, 180);

        final Dimension size = getToolkit().getScreenSize();

        if (size.width > this.getSize().width) {
            //Mittig positionieren
            this.setLocation((size.width / 2) - (this.getSize().width / 2),
                             (size.height / 2) - (this.getSize().height / 2));
        }

        setVisible(true);
    }

    //-------------------------------------------------------------------
    //Quick and Dirty!
    private int parseInt(Window parent, String text, boolean negativErlaubt) {
        String message = "";

        try {
            final int temp = Integer.parseInt(text);

            if (!negativErlaubt && (temp < 0)) {
                message = TranslationFacility.tr("negativVerboten");
                throw new NumberFormatException();
            }

            return temp;
        } catch (NumberFormatException nfe) {
            if (message.equals("")) {
                message = TranslationFacility.tr("keineZahl");
            }

            core.util.Helper.showMessage(parent, message,
                                                          TranslationFacility.tr("Fehler"),
                                                          javax.swing.JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    public boolean getReuseSelection() {
        return this.m_jcbReuseSelection.isSelected();
    }

    public boolean isOwnLeagueSelected() {
        return this.m_jrbLigaAktuell.isSelected();
    }

    public boolean isAborted() {
        return isAborted;
    }
}
