// %3415157064:de.hattrickorganizer.gui.keepertool%
package tool.keepertool;


import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**
 * Main KeeperTool dialog
 *
 * @author draghetto
 */
public class KeeperToolDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = -7475169046243634752L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private JPanel cards = new JPanel(new CardLayout());
    private JRadioButton rosterButton;
    private JRadioButton scoutButton;
    private ManualPanel manualPanel;
    private ResultPanel resultPanel;
    private RosterPanel rosterPanel;
    private ScoutPanel scoutPanel;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new KeeperToolDialog object.
     *
     * @param owner the HO main frame
     */
    public KeeperToolDialog(JFrame owner) {
        super(owner, false);
        setTitle(HOVerwaltung.instance().getLanguageString("KeeperTool"));

        resultPanel = new ResultPanel(this);
        initComponents();
        //reload();
        setSize(400, 250);
        reload();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Action Listener, reacts to type of Keeper Selection method
     *
     * @param ae action event
     */
    public final void actionPerformed(ActionEvent ae) {
        final Object compo = ae.getSource();
        final CardLayout cLayout = (CardLayout) (cards.getLayout());
        resultPanel.reset();
        scoutPanel.reset();
        rosterPanel.reset();

        if (compo == rosterButton) {
            cLayout.show(cards, "Roster");
        } else if (compo == scoutButton) {
            cLayout.show(cards, "Scout");
        } else {
            cLayout.show(cards, "Manual");
        }
    }

    /**
     * Forces a reset of the dialog
     */
    public final void reload() {
        resultPanel.reset();
        rosterPanel.reload();
        scoutPanel.reload();
        manualPanel.reset();

        final CardLayout cLayout = (CardLayout) (cards.getLayout());
        cLayout.show(cards, "Roster");

        rosterButton.setSelected(true);
    }

    /**
     * Initialize the GUI components
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());

        final JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        rosterButton = new JRadioButton(core.model.HOVerwaltung.instance().getLanguageString("Spieleruebersicht"));
        rosterButton.setSelected(true);
        rosterButton.addActionListener(this);
        rosterButton.setOpaque(false);

        scoutButton = new JRadioButton(core.model.HOVerwaltung.instance().getLanguageString("TransferScout"));
        scoutButton.addActionListener(this);
        scoutButton.setOpaque(false);

        final JRadioButton manualButton = new JRadioButton(core.model.HOVerwaltung.instance().getLanguageString("Manual"));
        manualButton.addActionListener(this);
        manualButton.setOpaque(false);

        final ButtonGroup groupRadio = new ButtonGroup();
        groupRadio.add(rosterButton);
        groupRadio.add(scoutButton);
        groupRadio.add(manualButton);

        final JPanel buttonPanel = new ImagePanel();
        buttonPanel.setLayout(new GridLayout(3, 1));
        buttonPanel.add(rosterButton);
        buttonPanel.add(scoutButton);
        buttonPanel.add(manualButton);

        main.add(buttonPanel, BorderLayout.WEST);

        rosterPanel = new RosterPanel(resultPanel);
        scoutPanel = new ScoutPanel(resultPanel);
        manualPanel = new ManualPanel(resultPanel);

        cards.add(rosterPanel, "Roster");
        cards.add(scoutPanel, "Scout");
        cards.add(manualPanel, "Manual");
        main.add(cards, BorderLayout.CENTER);

        main.add(resultPanel, BorderLayout.SOUTH);

        getContentPane().add(main, BorderLayout.CENTER);
    }
    
	@Override
	public void setSize(int width, int height) {  
	   super.setSize(width, height);  
		    
	   Dimension screenSize = getParent().getSize();  
	   int x = (screenSize.width - getWidth()) / 2;  
	   int y = (screenSize.height - getHeight()) / 2;  
	    
	   setLocation(getParent().getX()+x, getParent().getY()+y);     
	}
}
