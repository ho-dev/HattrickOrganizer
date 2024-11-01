// %2591273278:de.hattrickorganizer.gui.matches%
package module.matches;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupPosition;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.Helper;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Zeigt den Player an der Position an und dessen Sterne
 */
final class SpielerSternePanel extends ImagePanel implements ActionListener {

	private final int PANEL_WIDTH = Helper.calcCellWidth(120);
	private final int PANEL_HEIGHT = Helper.calcCellWidth(69);
	private final int PANEL_HEIGHT_REDUCED = Helper.calcCellWidth(44);
	private final int m_iPositionsID;

	private final JLabel position = new JLabel();
	private final JButton playerButton = new JButton();
	private final JLabel specialty = new JLabel();
	private final JLabel name = new JLabel();
	private final JLabel shirt = new JLabel();

	private MatchLineup m_clMatchLineup;
	private MatchLineupPosition m_clMatchPlayer;
	private final RatingTableEntry m_jpSterne = new RatingTableEntry();
	private final Box m_jpDummy = new Box(BoxLayout.X_AXIS);
	private final JPanel m_jpParent;
	private boolean m_bOnScreen = false;
	private final GridBagConstraints m_gbcConstraints = new GridBagConstraints();

	SpielerSternePanel(int positionsID, boolean print, JPanel parent, int x, int y) {
		super(print);

		m_iPositionsID = positionsID;
		m_jpParent = parent;

		initComponents();
		initLabel(positionsID, (byte) 0); // Tactic does not matter anymore...

		// This one size fits all will be bad whenever it is decided to
		// remove subs/captain/setpiece-taker panels when empty
		m_jpDummy.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

		// Init the constraints object, and add this panel to the parent
		m_gbcConstraints.anchor = GridBagConstraints.NORTH;
		m_gbcConstraints.fill = GridBagConstraints.NONE;
		m_gbcConstraints.weightx = 0.0;
		m_gbcConstraints.weighty = 0.0;
		m_gbcConstraints.insets = new Insets(2, 2, 2, 2);
		m_gbcConstraints.gridx = x;
		m_gbcConstraints.gridy = y;
		m_gbcConstraints.gridwidth = 1;

		addPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new SpielerDetailDialog(core.gui.HOMainFrame.instance(), m_clMatchPlayer,
				m_clMatchLineup);
	}

	void clear() {

		// We want empty frames in the on field positions hidden when empty
		if ((m_bOnScreen)
				&& ((m_iPositionsID >= IMatchRoleID.startLineup) && (m_iPositionsID < IMatchRoleID.startReserves))) {
			removePanel();
		}

		// lets leave the position text, right? - Blaghaid
		// m_jlPosition.setText("");
		playerButton.setText("");
		playerButton.setEnabled(false);
		m_jpSterne.clear();
		specialty.setIcon(null);
		shirt.setIcon(null);
		repaint();
	}

	/**
	 * Erzeugt die Komponenten
	 */
	private void initComponents() {
		var fontSize = UserParameter.instance().fontSize;
		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = new Insets(1, 1, 1, 1);

		setLayout(layout);

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		constraints.gridx = 0;
		constraints.gridy = 0;
		layout.setConstraints(position, constraints);
		add(position);

		playerButton.setLayout(new BorderLayout());
		playerButton.setToolTipText(TranslationFacility.tr("tt_Spiel_Spielerdetails"));
		playerButton.setHorizontalAlignment(SwingConstants.LEFT);
		playerButton.setMargin(new Insets(0, 1, 0, 1));
		playerButton.setFocusPainted(false);
		playerButton.setEnabled(false);
		playerButton.addActionListener(this);
		playerButton.setBackground(ColorLabelEntry.BG_STANDARD);
		playerButton.setOpaque(false);
		playerButton.setBorder(BorderFactory.createEmptyBorder());

		shirt.setPreferredSize(new Dimension(2*fontSize, 2*fontSize));
		playerButton.add(shirt, BorderLayout.WEST);
		playerButton.add(name, BorderLayout.CENTER);
		playerButton.add(specialty, BorderLayout.EAST);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 0.5; // Give extra vertical space to the player button
		constraints.gridwidth = 1;
		add(playerButton, constraints);

		switch (m_iPositionsID) {
		case IMatchRoleID.keeper:
		case IMatchRoleID.rightBack:
		case IMatchRoleID.leftBack:
		case IMatchRoleID.rightCentralDefender:
		case IMatchRoleID.middleCentralDefender:
		case IMatchRoleID.leftCentralDefender:
		case IMatchRoleID.rightInnerMidfield:
		case IMatchRoleID.centralInnerMidfield:
		case IMatchRoleID.leftInnerMidfield:
		case IMatchRoleID.leftWinger:
		case IMatchRoleID.rightWinger:
		case IMatchRoleID.rightForward:
		case IMatchRoleID.centralForward:
		case IMatchRoleID.leftForward:
		case IMatchRoleID.redCardedPlayer1:
		case IMatchRoleID.FirstPlayerReplaced: {
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.weighty = 0; // No vertical stretch for this one

			final JComponent component = m_jpSterne.getComponent(false);
			layout.setConstraints(component, constraints);
			add(component);

			setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
			break;
		}

		default:
			setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT_REDUCED));
		}
	}

	public void refresh(MatchLineup lineup, MatchLineupPosition matchLineupPosition, Set<String> duplicateLastNames) {
		m_clMatchLineup = lineup;
		m_clMatchPlayer = matchLineupPosition;

		if (matchLineupPosition != null) {
			// Make sure this is on screen, we got a player to display
			if (!m_bOnScreen) {
				addPanel();
			}

			final boolean unique = !duplicateLastNames.contains(matchLineupPosition.getLastName());
            final String displayName = Player.getFullNameSmart(matchLineupPosition.getFirstName(),
                matchLineupPosition.getLastName(),
                matchLineupPosition.getNickName(),
                unique);
			name.setText(displayName);

			int trickotnummer = 0;
			final Player player = core.model.HOVerwaltung.instance().getModel().getCurrentPlayer(matchLineupPosition.getPlayerId());

			if (player != null) {
				trickotnummer = player.getShirtNumber();
				specialty.setIcon(ImageUtilities.getPlayerSpecialtyIcon(HOIconName.SPECIALTIES[player.getSpecialty()], UserParameter.instance().fontSize));
			} else {
				specialty.setIcon(null);
			}

			if (matchLineupPosition.getPlayerId() > 0) {
				shirt.setIcon(ImageUtilities.getImage4Position(matchLineupPosition.getRoleId(), matchLineupPosition.getBehaviour(), trickotnummer));
			}
			else {
				shirt.setIcon(null);
			}
			playerButton.setEnabled(matchLineupPosition.getPlayerId() > 0);
			m_jpSterne.setRating((float) matchLineupPosition.getRating() * 2f, true);
			initLabel(matchLineupPosition.getRoleId(), matchLineupPosition.getBehaviour());

		} else {
			clear();
		}
		repaint();
	}

	private void removePanel() {
		m_jpParent.remove(this);
		m_jpParent.add(m_jpDummy, m_gbcConstraints);
		m_bOnScreen = false;
		m_jpParent.repaint();
	}

	private void addPanel() {
		m_jpParent.remove(m_jpDummy);
		m_jpParent.add(this, m_gbcConstraints);
		m_bOnScreen = true;
		m_jpParent.repaint();
	}

	private void initLabel(int posid, byte taktik) {

		switch (posid) {

		case IMatchRoleID.setPieces: {
			position.setText(TranslationFacility.tr("match.setpiecestaker"));
			break;
		}
		case IMatchRoleID.captain: {
			position.setText(TranslationFacility.tr("Spielfuehrer"));
			break;
		}
		case IMatchRoleID.substCD1: {
			position.setText(TranslationFacility.tr("Reserve") + " "
					+ TranslationFacility.tr("defender"));
			break;
		}
		case IMatchRoleID.substFW1: {
			position.setText(TranslationFacility.tr("Reserve") + " "
					+ TranslationFacility.tr("ls.player.position.forward"));
			break;
		}
		case IMatchRoleID.substWI1: {
			position.setText(TranslationFacility.tr("Reserve") + " "
					+ TranslationFacility.tr("ls.player.position.winger"));
			break;
		}
		case IMatchRoleID.substIM1: {
			position.setText(TranslationFacility.tr("Reserve")
					+ " "
					+ TranslationFacility.tr(
							"ls.player.position.innermidfielder"));
			break;
		}
		case IMatchRoleID.substGK1: {
			position.setText(TranslationFacility.tr("Reserve") + " "
					+ TranslationFacility.tr("ls.player.position.keeper"));
			break;
		}
		default: {
			// Special check here for the replaced players, we got a range of at
			// least 3...
			if ((posid >= IMatchRoleID.FirstPlayerReplaced)
					&& (posid <= IMatchRoleID.ThirdPlayerReplaced)) {
				position.setText(TranslationFacility.tr("Ausgewechselt"));
				break;
			} else {
				position.setText(MatchRoleID
						.getNameForPosition(MatchRoleID.getPosition(posid,
								taktik)));
			}
		}
		}
	}
}
