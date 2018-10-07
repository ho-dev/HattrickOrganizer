// %2333829392:de.hattrickorganizer.gui.menu%
package core.net;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;



/**
 * Auswahl der Liga zu einer Season, um den richtigen Spielplan zu ziehen
 */
public class LigaAuswahlDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 6165662981528850898L;

    //~ Instance fields ----------------------------------------------------------------------------

	private JButton m_jbAbbrechen = new JButton(core.model.HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
    private JButton m_jbOk = new JButton(core.model.HOVerwaltung.instance().getLanguageString("ls.button.download"));
    private JComboBox m_jcbLiga;
    private JRadioButton m_jrbLigaAktuell = new JRadioButton(core.model.HOVerwaltung.instance().getLanguageString("AktuelleLiga"),
                                                             true);
    private JRadioButton m_jrbLigaAndere = new JRadioButton(core.model.HOVerwaltung.instance().getLanguageString("AndereLiga"),
                                                            false);
    private int m_iLigaId = -2;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new LigaAuswahlDialog object.
     */
    public LigaAuswahlDialog(JDialog owner, int seasonid) {
        super(owner,
              core.model.HOVerwaltung.instance().getLanguageString("Liga"),
              true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        initComponents(seasonid);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Gibt die Ligaid zurück oder -1 für aktulleLigaid oder -2 für abbruch
     */
    public final int getLigaID() {
        return m_iLigaId;
    }

    @Override
	public final void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(m_jbOk)) {
            //Ist es eine Zahl
            if (m_jrbLigaAktuell.isSelected()) {
                //Aktuelle Liga benutzen
                m_iLigaId = HOVerwaltung.instance().getModel().getXtraDaten().getLeagueLevelUnitID();
                setVisible(false);
            }
            //Andere Liga
            else if (m_jcbLiga.getSelectedItem() != null) {
                m_iLigaId = parseInt(this, m_jcbLiga.getSelectedItem().toString(), false);

                if (m_iLigaId > -1) {
                    setVisible(false);
                }
            }
        } else if (e.getSource().equals(m_jbAbbrechen)) {
            setVisible(false);
        } else if ((e.getSource().equals(m_jrbLigaAktuell))
                   || (e.getSource().equals(m_jrbLigaAndere))) {
            if (m_jrbLigaAndere.isSelected()) {
                m_jcbLiga.setEnabled(true);
            } else {
                m_jcbLiga.setEnabled(false);
            }
        }
    }

    private Integer[] fillCB() {
        //Alle möglichen LigaIDs holen
        return core.db.DBManager.instance().getAllLigaIDs();
    }

    private void initComponents(int seasonid) {
        setContentPane(new ImagePanel(new GridLayout(4, 2, 4, 4)));

        JLabel label = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Season"));
        getContentPane().add(label);

        final JTextField textfield = new JTextField(seasonid + "");
        textfield.setEditable(false);
        getContentPane().add(textfield);

        final ButtonGroup bg = new ButtonGroup();

        m_jrbLigaAktuell.setToolTipText(core.model.HOVerwaltung.instance().getLanguageString("tt_LigaDownload_Aktuell"));
        m_jrbLigaAktuell.setOpaque(false);
        m_jrbLigaAktuell.addActionListener(this);
        bg.add(m_jrbLigaAktuell);
        getContentPane().add(m_jrbLigaAktuell);

        //Platzhalter
        label = new JLabel();
        getContentPane().add(label);

        m_jrbLigaAndere.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_LigaDownload_Andere"));
        m_jrbLigaAndere.setOpaque(false);
        m_jrbLigaAndere.addActionListener(this);
        bg.add(m_jrbLigaAndere);
        getContentPane().add(m_jrbLigaAndere);

        m_jcbLiga = new JComboBox(fillCB());
        m_jcbLiga.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_LigaDownload_LigaID"));
        m_jcbLiga.setEnabled(false);
        m_jcbLiga.setSelectedItem(Integer.valueOf(DBManager.instance().getLigaID4SaisonID(seasonid)));
        m_jcbLiga.setEditable(true);
        getContentPane().add(m_jcbLiga);

        m_jbOk.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Download_Start"));
        m_jbOk.addActionListener(this);
        getContentPane().add(m_jbOk);

        m_jbAbbrechen.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Download_Abbrechen"));
        m_jbAbbrechen.addActionListener(this);
        getContentPane().add(m_jbAbbrechen);

        setSize(250, 150);

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
                message = core.model.HOVerwaltung.instance().getLanguageString("negativVerboten");
                throw new NumberFormatException();
            }

            return temp;
        } catch (NumberFormatException nfe) {
            if (message.equals("")) {
                message = core.model.HOVerwaltung.instance().getLanguageString("keineZahl");
            }

            core.util.Helper.showMessage(parent, message,
                                                          core.model.HOVerwaltung.instance().getLanguageString("Fehler"),
                                                          javax.swing.JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
}
