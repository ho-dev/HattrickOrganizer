package module.transfer.scout;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

/**
 * The TransferScout main Panel
 */
public class TransferScoutPanel extends ImagePanel implements MouseListener, KeyListener {

	//~ Instance fields ----------------------------------------------------------------------------
    private JSplitPane verticalSplitPane;
    private ScoutThread m_clScoutThread;
    private TransferEingabePanel m_jpTransferEingabePanel;
    private TransferScoutingTable m_jtTransferTable;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TransferScoutPanel object.
     */
    public TransferScoutPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final int getDividerLocation() {
        return verticalSplitPane.getDividerLocation();
    }

    public final TransferScoutingTable getTransferTable() {
        return m_jtTransferTable;
    }

    //------------------------------------------------------------------------
    public final void addScoutEintrag(ScoutEintrag scouteintrag) {
        //Wird nur durchgeführt, wenn der Eintrag schon vorhanden ist
        removeScoutEintrag(scouteintrag);
        m_jtTransferTable.getTransferTableModel().addScoutEintrag(scouteintrag);

        //Thread aktualisieren
        if (m_clScoutThread != null) {
            //m_clScoutThread.addEintrag ( scouteintrag );
            m_clScoutThread.setVector(m_jtTransferTable.getTransferTableModel().getScoutListe());

            //neuer Thread
        } else {
            ScoutThread.start(m_jtTransferTable.getTransferTableModel().getScoutListe());
        }

        m_jtTransferTable.refresh();
    }

    public final void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getSource().equals(m_jtTransferTable)) {
            newSelectionInform();
        }
    }

    public final void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getSource().equals(m_jtTransferTable)) {
            newSelectionInform();
        }
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    //------------Listener---------------------------------------------------
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public final void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().equals(m_jtTransferTable)) {
            newSelectionInform();
        }
    }

    //------------------------------------------------------
    public final void newSelectionInform() {
        final int row = m_jtTransferTable.getSelectedRow();
        if (row > -1) {
            var model = m_jtTransferTable.getTransferTableModel();
            var modelIndex = m_jtTransferTable.convertRowIndexToModel(row);
            m_jpTransferEingabePanel.setScoutEintrag(model.getScoutListe().get(modelIndex).duplicate());
        }
    }

    /**
     * Removes one entry from transfer table
     *
     * @param scouteintrag the scout entry which should be removed
     */
    public final void removeScoutEintrag(ScoutEintrag scouteintrag) {
        m_jtTransferTable.getTransferTableModel().removeScoutEintrag(scouteintrag);
        m_clScoutThread.setVector(m_jtTransferTable.getTransferTableModel().getScoutListe());
        m_jtTransferTable.refresh();
    }
    
    /**
     * Removes all entries from transfer table and scout thread
     */
    public final void removeScoutEntries() {
        m_jtTransferTable.getTransferTableModel().removeScoutEntries();
        m_clScoutThread.setVector(null);
        m_jtTransferTable.refresh();
    }

    public final void saveScoutListe() {
        DBManager.instance().saveScoutList(m_jtTransferTable.getTransferTableModel().getScoutListe());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, initTransferTable(), initTransferEingabePanel());
        add(verticalSplitPane, BorderLayout.CENTER);
        verticalSplitPane.setDividerLocation(core.model.UserParameter.instance().transferScoutPanel_horizontalSplitPane);
        m_clScoutThread = ScoutThread.start(DBManager.instance().getScoutList());
    }

    private Component initTransferEingabePanel() {
        m_jpTransferEingabePanel = new TransferEingabePanel(this);
        return new JScrollPane(m_jpTransferEingabePanel);
    }

    private Component initTransferTable() {
        m_jtTransferTable = new TransferScoutingTable();
        m_jtTransferTable.addMouseListener(this);
        m_jtTransferTable.addKeyListener(this);

//        var panel = new JPanel(new BorderLayout());
//        panel.add(m_jtTransferTable.getContainerComponent(), BorderLayout.CENTER);
//
//        final JScrollPane scrollpane = new JScrollPane(panel);
////        scrollpane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        return m_jtTransferTable.getContainerComponent();
    }

    public void storeUserSettings() {
         m_jtTransferTable.getTransferTableModel().storeUserSettings();
    }
}
