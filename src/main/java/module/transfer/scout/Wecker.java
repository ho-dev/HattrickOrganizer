package module.transfer.scout;

import core.gui.HOMainFrame;
import core.model.TranslationFacility;

import javax.swing.*;

/**
 * @author thomas.werth
 */
class Wecker extends javax.swing.JFrame implements java.awt.event.ActionListener {

	//~ Instance fields ----------------------------------------------------------------------------
    private JButton m_jbOK = new JButton();

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new instance of Wecker
     */
    Wecker(String text) {
        final JTextArea ta = new JTextArea();

        ta.setEditable(false);
        ta.setText(text);
        m_jbOK.setText(TranslationFacility.tr("ls.button.ok"));
        m_jbOK.addActionListener(this);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle(TranslationFacility.tr("TransferScout"));
        this.setIconImage(HOMainFrame.instance().getIconImage());

        //this.setContentPane(temp);
        this.getContentPane().setLayout(new java.awt.BorderLayout());

        this.getContentPane().add(ta, java.awt.BorderLayout.CENTER /*new JLabel( text )*/);
        this.getContentPane().add(m_jbOK, java.awt.BorderLayout.SOUTH);
        pack();
        this.setLocation((int) ((this.getToolkit().getScreenSize().getWidth() / 2)
                         - (this.getSize().getWidth() / 2)),
                         (int) ((this.getToolkit().getScreenSize().getHeight() / 2)
                         - (this.getSize().getHeight() / 2)));
        this.setVisible(true);
        this.setResizable(false);
        this.toFront();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(m_jbOK)) {
            setVisible(false);
            this.dispose();
        }
    }
}
