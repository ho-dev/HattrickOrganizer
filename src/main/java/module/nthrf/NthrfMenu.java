package module.nthrf;

import core.gui.HOMainFrame;
import core.model.HOVerwaltung;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


/**
 * A simple menu for the nthrf plugin.
 *
 * @author aik
 */
public class NthrfMenu extends JMenu {

	private static final long serialVersionUID = 1L;
	
	NthrfMenu(){
		super("Nthrf");
		initialize();
	}
	/**
	 * Create a new Feedback menu.
	 */
	private void initialize() {
    	JMenuItem about = new JMenuItem(HOVerwaltung.instance().getLanguageString("HRFDownload")+"(Nthrf)");
        about.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ev) {
        		JOptionPane.showMessageDialog(HOMainFrame.instance(), MainPanel.getInstance(),
        				HOVerwaltung.instance().getLanguageString("HRFDownload"), JOptionPane.PLAIN_MESSAGE);
        	}
        });
        add(about);
	}

}
