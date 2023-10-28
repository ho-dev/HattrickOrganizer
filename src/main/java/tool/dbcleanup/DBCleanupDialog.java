// %198737965:de.hattrickorganizer.gui.menu.option%
package tool.dbcleanup;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;



/**
 * Database Cleanup Dialog
 *
 * @author flattermann <HO@flattermann.net>
 */
class DBCleanupDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 3533368597781557223L;
	private JButton m_jbCleanupNow = new JButton(HOVerwaltung.instance().getLanguageString("dbcleanup.cleanupnow"));
	private JButton m_jbCancel = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
	private DBCleanupTool cleanupTool;

	private JTextArea textIntro = new JTextArea (HOVerwaltung.instance().getLanguageString("dbcleanup.intro"));

	private JLabel labelOwnMatches = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.ownMatches"));
	private JLabel labelOwnFriendlies = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.ownFriendlies"));
	private JLabel labelOtherMatches = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.otherMatches"));
	private JLabel labelOtherFriendlies = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.otherFriendlies"));

	private WeekSelectionPanel wsp_OwnMatches =	new WeekSelectionPanel (DBCleanupTool.REMOVE_NONE);
	private WeekSelectionPanel wsp_OwnFriendlies = new WeekSelectionPanel (DBCleanupTool.REMOVE_NONE);
	private WeekSelectionPanel wsp_OtherMatches = new WeekSelectionPanel (16);
	private WeekSelectionPanel wsp_OtherFriendlies = new WeekSelectionPanel (8);

	private JLabel labelHrf = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.hrf"));
//	private WeekSelectionPanel wsp_Hrf = new WeekSelectionPanel (DBCleanupTool.REMOVE_NONE, false);

//	private JLabel labelHrfAutoremove = new JLabel (HOVerwaltung.instance().getLanguageString("dbcleanup.hrfAutoremove"));
	private JCheckBox m_jcbHrfAutoremove = new JCheckBox(HOVerwaltung.instance().getLanguageString("dbcleanup.hrfAutoremove"));

	//~ Constructors -------------------------------------------------------------------------------

	/**
	 * Creates a new DBCleanupDialog object.
	 */
	DBCleanupDialog(JFrame owner, DBCleanupTool cleanupTool) {
		super(owner,
				HOVerwaltung.instance().getLanguageString("ls.menu.file.database.databasecleanup"),
				true);
		this.cleanupTool = cleanupTool;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initComponents();
	}

	//~ Methods ------------------------------------------------------------------------------------

	private void initComponents() {
//		setContentPane(new de.hattrickorganizer.gui.templates.ImagePanel());
		getContentPane().setLayout(new BorderLayout());

		textIntro.setAlignmentX(CENTER_ALIGNMENT);
		textIntro.setEditable(false);
		textIntro.setWrapStyleWord(true);
		textIntro.setLineWrap(true);
		JPanel weekSelectionPanel = new JPanel(new GridBagLayout());

		labelOwnMatches.setFont(labelOwnMatches.getFont().deriveFont(Font.BOLD));
		labelOwnFriendlies.setFont(labelOwnFriendlies.getFont().deriveFont(Font.BOLD));
		labelOtherMatches.setFont(labelOtherMatches.getFont().deriveFont(Font.BOLD));
		labelOtherFriendlies.setFont(labelOtherFriendlies.getFont().deriveFont(Font.BOLD));
		labelHrf.setFont(labelHrf.getFont().deriveFont(Font.BOLD));
//		labelHrfAutoremove.setFont(labelHrfAutoremove.getFont().deriveFont(Font.BOLD));

		m_jcbHrfAutoremove.setSelected(true);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx=0;
		c.gridy=0;
		c.anchor=GridBagConstraints.LINE_START;
		c.ipadx=20;

		weekSelectionPanel.add (labelOwnMatches, c);
		c.gridy++;
		weekSelectionPanel.add (labelOwnFriendlies, c);
		c.gridy++;
		weekSelectionPanel.add (labelOtherMatches, c);
		c.gridy++;
		weekSelectionPanel.add (labelOtherFriendlies, c);
		c.gridy++;
//		weekSelectionPanel.add (labelHrfAutoremove, c);
//		c.gridy++;
		weekSelectionPanel.add (labelHrf, c);

		c.gridx=1;
		c.gridy=0;
		weekSelectionPanel.add (wsp_OwnMatches, c);
		c.gridy++;
		weekSelectionPanel.add (wsp_OwnFriendlies, c);
		c.gridy++;
		weekSelectionPanel.add (wsp_OtherMatches, c);
		c.gridy++;
		weekSelectionPanel.add (wsp_OtherFriendlies, c);
		c.gridy++;
		weekSelectionPanel.add(m_jcbHrfAutoremove, c);
//		c.gridy++;
//		weekSelectionPanel.add (wsp_Hrf, c);


		ImagePanel m_jpButtonPanel = new ImagePanel();
		// Add Buttons
		m_jpButtonPanel.add(m_jbCleanupNow);
		m_jbCleanupNow.setFont(m_jbCleanupNow.getFont().deriveFont(Font.BOLD));
		m_jpButtonPanel.add(m_jbCancel);

		m_jbCleanupNow.addActionListener(this);
		m_jbCancel.addActionListener(this);

		getContentPane().add(textIntro, BorderLayout.NORTH);
		getContentPane().add(weekSelectionPanel, BorderLayout.CENTER);
		getContentPane().add(m_jpButtonPanel, BorderLayout.SOUTH);

		pack();

        final Dimension size = HOMainFrame.INSTANCE.getToolkit().getScreenSize();

        if (size.width > this.getSize().width) {
            //Mittig positionieren
            this.setLocation((size.width / 2) - (this.getSize().width / 2),
                             (size.height / 2) - (this.getSize().height / 2));
        }

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(m_jbCleanupNow)) {
			cleanupTool.cleanupMatches (wsp_OwnMatches.getWeeks(), wsp_OwnFriendlies.getWeeks(), wsp_OtherMatches.getWeeks(), wsp_OtherFriendlies.getWeeks());
			cleanupTool.cleanupHRFs (DBCleanupTool.REMOVE_NONE, m_jcbHrfAutoremove.isSelected());
			setVisible(false);
		} else if (e.getSource().equals(m_jbCancel)) {
			setVisible(false);
		}
	}
}
