package module.playerOverview;

import core.db.DBManager;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.player.Spieler;
import core.util.HOLogger;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 * Dialog to enter the training block dates for a player
 *
 * @author flattermann <HO@flattermann.net>
 *
 */
final class SpielerTrainingBlockDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -1455575065158209100L;

	//~ Instance fields ----------------------------------------------------------------------------

	/* The buttons */
	private JButton m_jbCancel;
	private JButton m_jbOK;
    private JButton m_jbAdd;

    /* A list of <SingleTrainingBlock> */
	private List<SingleTrainingBlock> allTrainingBlocks;

	/* current player */
	private Spieler m_clPlayer;

	/* the panels with the training blocks */
	private JPanel panel;

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new SpielerTrainingBlockDialog object.
	 */
	protected SpielerTrainingBlockDialog(javax.swing.JFrame owner, Spieler player) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(
			HOVerwaltung.instance().getLanguageString(
				"TrainingBlock")
				+ " "
				+ player.getName());

		m_clPlayer = player;

		initTrainingBlockList();

		initComponents();

		pack();

		final Dimension size =
			core.gui.HOMainFrame.instance().getToolkit().getScreenSize();

		if (size.width > this.getSize().width) {
			//Mittig positionieren
			this.setLocation(
				(size.width / 2) - (this.getSize().width / 2),
				(size.height / 2) - (this.getSize().height / 2));
		}
	}

	//~ Methods ------------------------------------------------------------------------------------

	/**
	 * Init the training block list
	 * i.e. check all HRFs for an active training block
	 */
	private void initTrainingBlockList() {
		allTrainingBlocks = new ArrayList<SingleTrainingBlock>();
		int playerId = m_clPlayer.getSpielerID();
		Timestamp timestampFirstPlayerHRF = DBManager.instance().getTimestamp4FirstPlayerHRF(playerId);
		int hrfId = DBManager.instance().getHRFID4Date(timestampFirstPlayerHRF);
		boolean lastBlock = false;
		Timestamp startDate = null;
		Timestamp lastDate = null;

		// Iterate through all HRFs for this player
		do {
			// Fetch the player from that HRF
			Spieler player = DBManager.instance().getSpielerFromHrf(hrfId, playerId);
			if (player == null) {
				HOLogger.instance().log(getClass(), "Warning! Player "+playerId+" at hrf "+ hrfId+" is NULL");
				continue;
			}
			boolean curBlock = player.hasTrainingBlock();
			// training block state change
			if (lastBlock != curBlock) {
				if (curBlock) {
					// block exists now
					// set start date
					startDate = player.getHrfDate();
				} else {
					// block ends here
					// add the block interval to the list
					SingleTrainingBlock newBlock = new SingleTrainingBlock(this, startDate, lastDate);
					allTrainingBlocks.add (newBlock);
				}
				lastBlock = curBlock;
			}
			lastDate = player.getHrfDate();
		} while ((hrfId = DBManager.instance().getFollowingHRF(hrfId)) > 0);

		// Block is still active
        if (lastBlock) {
			SingleTrainingBlock newBlock = new SingleTrainingBlock(this, startDate);
			allTrainingBlocks.add (newBlock);
        }
	}

	/**
	 * Save the training blocks to the player table (for every HRF)
	 */
	private void saveBlocks() {
		int playerId = m_clPlayer.getSpielerID();

		Timestamp timestampFirstPlayerHRF = DBManager.instance().getTimestamp4FirstPlayerHRF(playerId);
		int hrfId = DBManager.instance().getHRFID4Date(timestampFirstPlayerHRF);

		// Iterate through all HRFs for this player
		do {
			// Fetch the player from that HRF
			Spieler player = DBManager.instance().getSpielerFromHrf(hrfId, playerId);
			if (player == null) {
				HOLogger.instance().log(getClass(), "Warning@SaveBlocks! Player "+playerId+" at hrf "+ hrfId+" is NULL");
				continue;
			}
			Timestamp curTimestamp = player.getHrfDate();
			boolean curBlock = player.hasTrainingBlock();
			boolean newBlock = isBlockedAt (curTimestamp);
			if (curBlock != newBlock) {
				player.setTrainingBlock(newBlock);
				DBManager.instance().saveSpieler(hrfId, player, curTimestamp);
			}
		} while ((hrfId = DBManager.instance().getFollowingHRF(hrfId)) > 0);
	}

	/**
	 * Does a training block exist at this date
	 *
	 * @param date		the date to check
	 */
	private boolean isBlockedAt (Timestamp date) {
		for (int i=0; i < allTrainingBlocks.size(); i++) {
			SingleTrainingBlock curBlock = allTrainingBlocks.get(i);
			Date blockStart = curBlock.getBlockStart();
			Date blockEnd = curBlock.getBlockEnd();

			if (date.after(blockStart) && date.before(blockEnd)) {
				HOLogger.instance().log(getClass(), "Player " + m_clPlayer.getSpielerID()+" is blocked at "+date.toString());
				return true;
			}
		}
		HOLogger.instance().log(getClass(), "Player " + m_clPlayer.getSpielerID()+" is NOT blocked at "+date.toString());
		return false;
	}

	/**
	 * Button pressed
	 */
	public final void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource().equals(m_jbOK)) {
			for (int i=0; i<allTrainingBlocks.size(); i++) {
				SingleTrainingBlock curBlock = allTrainingBlocks.get(i);
				HOLogger.instance().log(getClass(),
						"Block found from: "+curBlock.getBlockStart().toString()+" to:"+curBlock.getBlockEnd().toString());
			}
			saveBlocks();
			setVisible(false);
			dispose();
		} else if (actionEvent.getSource().equals(m_jbCancel)) {
			setVisible(false);
			dispose();
		} else if (actionEvent.getSource().equals(m_jbAdd)) {
			SingleTrainingBlock newBlock = new SingleTrainingBlock(this);
			allTrainingBlocks.add(newBlock);
			addBlock (newBlock);
		} else {
			// Check for remove event
			for (int i=0; i < allTrainingBlocks.size(); i++) {
				SingleTrainingBlock curBlock = allTrainingBlocks.get(i);
				if (actionEvent.getSource().equals(curBlock.getRemoveButton())) {
					allTrainingBlocks.remove(curBlock);
					removeBlock (curBlock);
					// Leave for loop
					break;
				}
			}
		}
	}

	/**
	 * Add a block to the panel
	 *
	 * @param block
	 */
	private void addBlock (SingleTrainingBlock block) {
		panel.add(block);
		pack();
	}

	/**
	 * Remove a block from the panel
	 *
	 * @param block
	 */
	private void removeBlock (SingleTrainingBlock block) {
		panel.remove(block);
		pack();
	}

	/**
	 * Init the GUI components
	 */
	private void initComponents() {
		setContentPane(new core.gui.comp.panel.ImagePanel());

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4, 4, 4, 4);

		getContentPane().setLayout(layout);

		panel = new ImagePanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

		for (int i=0; i < allTrainingBlocks.size(); i++) {
			addBlock(allTrainingBlocks.get(i));
		}

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		layout.setConstraints(panel, constraints);
		getContentPane().add(panel);

		m_jbAdd =
			new JButton(HOVerwaltung.instance().getLanguageString("TrainingBlock.add"));
		m_jbAdd.addActionListener(this);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(m_jbAdd, constraints);
		getContentPane().add(m_jbAdd);

		m_jbOK =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
		m_jbOK.addActionListener(this);

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(m_jbOK, constraints);
		getContentPane().add(m_jbOK);

		m_jbCancel =
			new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
		m_jbCancel.addActionListener(this);

		constraints.anchor = GridBagConstraints.EAST;
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(m_jbCancel, constraints);
		getContentPane().add(m_jbCancel);
	}
}
