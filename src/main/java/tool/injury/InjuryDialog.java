package tool.injury;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Main Dialog for Injury Calculator
 *
 * @author draghetto
 */
public class InjuryDialog extends JDialog {

	private static final long serialVersionUID = 5194730460165995230L;

    //~ Instance fields ----------------------------------------------------------------------------
    DoctorPanel doctorPanel;
    UpdatePanel updatePanel;
    UpdateTSIPanel tsiPanel;
    private InjuryDetailPanel detail = new InjuryDetailPanel();

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new KeeperToolDialog object.
     *
     * @param owner the main HO Frame
     */
    public InjuryDialog(JFrame owner) {
        super(owner, false);
        setTitle(HOVerwaltung.instance().getLanguageString("InjuryCalculator"));

        initComponents();

        setSize(700, 300);
        reload();
        //setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the Detail Panel for use of calculator
     *
     * @return the Detail Panel
     */
    public final InjuryDetailPanel getDetail() {
        return detail;
    }

    /**
     * Method that force a reload of the dialog
     */
    public final void reload() {
        detail.reload();
        doctorPanel.reset();
        updatePanel.reset();
        tsiPanel.reset();
    }

    /**
     * Initialize the GUI components
     */
    private void initComponents() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(detail, BorderLayout.CENTER);

        final JPanel p = new ImagePanel();
        p.setLayout(new GridLayout(3, 1));

        doctorPanel = new DoctorPanel(this);
        p.add(doctorPanel);

        updatePanel = new UpdatePanel(this);
        p.add(updatePanel);

        tsiPanel = new UpdateTSIPanel(this);
        p.add(tsiPanel);
        getContentPane().add(p, BorderLayout.SOUTH);
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
