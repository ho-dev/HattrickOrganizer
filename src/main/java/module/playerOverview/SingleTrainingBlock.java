package module.playerOverview;

import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;


/**
 * Represents a single training block with start and end date
 * 
 * @author flattermann <HO@flattermann.net>
 *
 */
final class SingleTrainingBlock extends JPanel {

	private static final long serialVersionUID = 1831752381188613287L;
	
	//~ Instance fields ----------------------------------------------------------------------------
	private JDateChooser m_jdcBlockStart;
	private JDateChooser m_jdcBlockEnd;
	private SpielerTrainingBlockDialog parent;
	
 //   private JButton m_jbRemove = new JButton(new ImageIcon(ImageUtilities.getImageDurchgestrichen(new BufferedImage(
 //   													15, 15, BufferedImage.TYPE_INT_ARGB),
 //   													Color.RED, new Color(200, 0, 0))));
	private JButton m_jbRemove = new JButton(ThemeManager.getIcon(HOIconName.REMOVE)); 
    //~ Constructors -------------------------------------------------------------------------------
	protected SingleTrainingBlock(SpielerTrainingBlockDialog parent) {
		this (parent, HOVerwaltung.instance().getModel().getBasics().getDatum());
	}

	protected SingleTrainingBlock(SpielerTrainingBlockDialog parent, Date startDate) {
		this (parent, startDate, new GregorianCalendar(2099, 11, 31).getTime());
	}

	protected SingleTrainingBlock(SpielerTrainingBlockDialog parent, Date startDate, Date endDate) {
		super();
		this.parent = parent;
		m_jdcBlockStart = new JDateChooser(startDate);
		m_jdcBlockEnd = new JDateChooser(endDate);
		initComponents();
	}

	/**
	 * Init the GUI components
	 */
	private void initComponents() {
		String fromString = HOVerwaltung.instance().getLanguageString("TrainingBlock.from");
		String toString = HOVerwaltung.instance().getLanguageString("TrainingBlock.to");

		m_jbRemove.setToolTipText(HOVerwaltung.instance().getLanguageString("TrainingBlock.remove"));

		m_jbRemove.addActionListener(parent);
		this.setLayout(new FlowLayout());
		this.add (new JLabel(fromString));
		this.add (m_jdcBlockStart);
		this.add (new JLabel(toString));
		this.add (m_jdcBlockEnd);
		this.add (m_jbRemove);
	}
	
	/**
	 * Get the training block start date
	 */
	protected Date getBlockStart () {
		// Set the BlockStart to the start of the day (00h00m00s)
		Date startDate = m_jdcBlockStart.getDate();
		Calendar startCal = new GregorianCalendar();
		startCal.setTime(startDate);
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		return startCal.getTime();
	}

	/**
	 * Get the training block end date
	 */
	protected Date getBlockEnd () {
		// Set the BlockEnd to the end of the day (23h59m59s)
		Date endDate = m_jdcBlockEnd.getDate();
		Calendar endCal = new GregorianCalendar();
		endCal.setTime(endDate);
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		return endCal.getTime();
	}
	
	/**
	 * Get the remove button
	 * (will be called by the parent [SpielerTrainingDialog] 
	 * to check which remove button was pressed)
	 * 
	 * @return	the remove button
	 */
	protected JButton getRemoveButton () {
		return m_jbRemove;
	}
}
