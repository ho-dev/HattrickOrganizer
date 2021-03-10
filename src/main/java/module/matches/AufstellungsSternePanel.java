// %2675300316:de.hattrickorganizer.gui.matches%
package module.matches;

import core.db.DBManager;
import core.gui.comp.panel.RasenPanel;
import core.model.UserParameter;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.model.match.SourceSystem;
import core.model.player.IMatchRoleID;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Zeigt die Stärken und Aufstellung einer Mannschaft an
 */
public class AufstellungsSternePanel extends RasenPanel {

	private static final long serialVersionUID = 7568934875331878466L;
	private JLabel m_jlTeamName;
	private SpielerSternePanel m_clAusgewechselt1;
	private SpielerSternePanel m_clAusgewechselt2;
	private SpielerSternePanel m_clAusgewechselt3;
	private SpielerSternePanel m_clLeftBack;
	private SpielerSternePanel m_clLeftWinger;
	private SpielerSternePanel m_clLeftCentralDefender;
	private SpielerSternePanel m_clLeftInnerMidfielder;
	private SpielerSternePanel m_clLeftForward;
	private SpielerSternePanel m_clRightBack;
	private SpielerSternePanel m_clRightWinger;
	private SpielerSternePanel m_clRightCentralDefender;
	private SpielerSternePanel m_clRightInnerMidfielder;
	private SpielerSternePanel m_clRightForward;
	private SpielerSternePanel m_clMiddleCentralDefender;
	private SpielerSternePanel m_clCentralInnerMidfielder;
	private SpielerSternePanel m_clCentralForward;
	private SpielerSternePanel m_clReserveWinger;
	private SpielerSternePanel m_clReserveMidfielder;
	private SpielerSternePanel m_clReserveForward;
	private SpielerSternePanel m_clReserveKeeper;
	private SpielerSternePanel m_clReserveDefender;
	private SpielerSternePanel m_clCaptain;
	private SpielerSternePanel m_clSetPieces;
	private SpielerSternePanel m_clKeeper;
	private boolean m_bHeim;
	private boolean m_bPrint;

	public AufstellungsSternePanel(boolean heim) {
		this(heim, false);
	}

	public AufstellungsSternePanel(boolean heim, boolean print) {
		super(print);

		m_bPrint = print;
		m_bHeim = heim;
		initComponentes();
	}

	public final void clearAll() {
		m_jlTeamName.setText(" ");
		m_clKeeper.clear();
		m_clLeftBack.clear();
		m_clLeftCentralDefender.clear();
		m_clMiddleCentralDefender.clear();
		m_clRightCentralDefender.clear();
		m_clRightBack.clear();
		m_clLeftWinger.clear();
		m_clLeftInnerMidfielder.clear();
		m_clCentralInnerMidfielder.clear();
		m_clRightInnerMidfielder.clear();
		m_clRightWinger.clear();
		m_clLeftForward.clear();
		m_clCentralForward.clear();
		m_clRightForward.clear();

		m_clReserveKeeper.clear();
		m_clReserveDefender.clear();
		m_clReserveMidfielder.clear();
		m_clReserveWinger.clear();
		m_clReserveForward.clear();

		m_clCaptain.clear();
		m_clSetPieces.clear();

		m_clAusgewechselt1.clear();
		m_clAusgewechselt2.clear();
		m_clAusgewechselt3.clear();
	}

	/**
	 * Get match lineup and refresh this SpielerSternePanels.
	 */
	public final void refresh(int matchid, int teamid) {
		final MatchLineup lineup = DBManager.instance().loadMatchLineup(SourceSystem.HATTRICK.getValue(), matchid);
		MatchLineupTeam lineupteam = null;

		if (lineup.getHomeTeamId() == teamid) {
			lineupteam = (MatchLineupTeam) lineup.getHomeTeam();
		} else {
			lineupteam = (MatchLineupTeam) lineup.getGuestTeam();
		}

		clearAll();

		if (lineupteam != null) {
			m_jlTeamName.setText(lineupteam.getTeamName() + " (" + lineupteam.getTeamID() + ")");
			List<MatchLineupPlayer> aufstellung = lineupteam.getLineup();

			for (MatchLineupPlayer player : aufstellung) {
				switch (player.getRoleId()) {
				case IMatchRoleID.keeper: {
					m_clKeeper.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.rightBack: {
					m_clRightBack.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.leftBack: {
					m_clLeftBack.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.rightCentralDefender: {
					m_clRightCentralDefender.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.middleCentralDefender: {
					m_clMiddleCentralDefender.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.leftCentralDefender: {
					m_clLeftCentralDefender.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.rightInnerMidfield: {
					m_clRightInnerMidfielder.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.centralInnerMidfield: {
					m_clCentralInnerMidfielder.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.leftInnerMidfield: {
					m_clLeftInnerMidfielder.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.leftWinger: {
					m_clLeftWinger.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.rightWinger: {
					m_clRightWinger.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.rightForward: {
					m_clRightForward.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.centralForward: {
					m_clCentralForward.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.leftForward: {
					m_clLeftForward.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.substCD1: {
					m_clReserveDefender.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.substFW1: {
					m_clReserveForward.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.substIM1: {
					m_clReserveMidfielder.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.substGK1: {
					m_clReserveKeeper.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.substWI1: {
					m_clReserveWinger.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.captain: {
					m_clCaptain.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.setPieces: {
					m_clSetPieces.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.FirstPlayerReplaced: {
					m_clAusgewechselt1.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.SecondPlayerReplaced: {
					m_clAusgewechselt2.refresh(lineup, player);
					break;
				}

				case IMatchRoleID.ThirdPlayerReplaced: {
					m_clAusgewechselt3.refresh(lineup, player);
					break;
				}

				default:
					// HOLogger.instance().log(getClass(), getClass().getName()
					// + ": Unknown player position: " + player.getPosition());
				}
			}
		}
	}

	/**
	 * Erstellt die Komponenten
	 */
	private void initComponentes() {
		setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel();
		centerPanel.setOpaque(false);

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		int gridx;
		int gridy;

		// The adding of the SpielerSternePanels is moved into the each panel,
		// to facilitate them appearing
		// and disappearing depending on the presence of a player.

		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(2, 2, 2, 2);

		centerPanel.setLayout(layout);

		if (m_bHeim) {
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 5;
		} else {
			constraints.gridx = 0;
			constraints.gridy = 16;
			constraints.gridwidth = 5;
		}

		m_jlTeamName = new JLabel(" ");
		m_jlTeamName.setOpaque(false);
		m_jlTeamName.setForeground(Color.white);
		m_jlTeamName.setFont(m_jlTeamName.getFont().deriveFont(Font.BOLD,
				UserParameter.instance().fontSize + 3));
		layout.setConstraints(m_jlTeamName, constraints);
		centerPanel.add(m_jlTeamName);

		Box midFieldGap = new Box(BoxLayout.X_AXIS);
		midFieldGap.setPreferredSize(new Dimension(20, 6));
		constraints.gridx = 2;
		constraints.gridy = 8;
		centerPanel.add(midFieldGap, constraints);

		if (m_bHeim) {
			gridx = 2;
			gridy = 4;
		} else {
			gridx = 2;
			gridy = 12;
		}

		m_clKeeper = new SpielerSternePanel(IMatchRoleID.keeper, m_bPrint, centerPanel, gridx,
				gridy);

		if (m_bHeim) {
			gridx = 0;
			gridy = 5;
		} else {
			gridx = 4;
			gridy = 11;
		}

		m_clRightBack = new SpielerSternePanel(IMatchRoleID.rightBack, m_bPrint, centerPanel,
				gridx, gridy);

		if (m_bHeim) {
			gridx = 1;
			gridy = 5;
		} else {
			gridx = 3;
			gridy = 11;
		}

		m_clRightCentralDefender = new SpielerSternePanel(IMatchRoleID.rightCentralDefender,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 2;
			gridy = 5;
		} else {
			gridx = 2;
			gridy = 11;
		}

		m_clMiddleCentralDefender = new SpielerSternePanel(IMatchRoleID.middleCentralDefender,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 3;
			gridy = 5;
		} else {
			gridx = 1;
			gridy = 11;
		}

		m_clLeftCentralDefender = new SpielerSternePanel(IMatchRoleID.leftCentralDefender,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 4;
			gridy = 5;
		} else {
			gridx = 0;
			gridy = 11;
		}

		m_clLeftBack = new SpielerSternePanel(IMatchRoleID.leftBack, m_bPrint, centerPanel,
				gridx, gridy);

		if (m_bHeim) {
			gridx = 0;
			gridy = 6;
		} else {
			gridx = 4;
			gridy = 10;
		}

		m_clRightWinger = new SpielerSternePanel(IMatchRoleID.rightWinger, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 1;
			gridy = 6;
		} else {
			gridx = 3;
			gridy = 10;
		}

		m_clRightInnerMidfielder = new SpielerSternePanel(IMatchRoleID.rightInnerMidfield,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 2;
			gridy = 6;
		} else {
			gridx = 2;
			gridy = 10;
		}

		m_clCentralInnerMidfielder = new SpielerSternePanel(IMatchRoleID.centralInnerMidfield,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 3;
			gridy = 6;
		} else {
			gridx = 1;
			gridy = 10;
		}

		m_clLeftInnerMidfielder = new SpielerSternePanel(IMatchRoleID.leftInnerMidfield,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 4;
			gridy = 6;
		} else {
			gridx = 0;
			gridy = 10;
		}

		m_clLeftWinger = new SpielerSternePanel(IMatchRoleID.leftWinger, m_bPrint, centerPanel,
				gridx, gridy);

		if (m_bHeim) {
			gridx = 1;
			gridy = 7;
		} else {
			gridx = 3;
			gridy = 9;
		}

		m_clRightForward = new SpielerSternePanel(IMatchRoleID.rightForward, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 2;
			gridy = 7;
		} else {
			gridx = 2;
			gridy = 9;
		}

		m_clCentralForward = new SpielerSternePanel(IMatchRoleID.centralForward, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 3;
			gridy = 7;
		} else {
			gridx = 1;
			gridy = 9;
		}

		m_clLeftForward = new SpielerSternePanel(IMatchRoleID.leftForward, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 0;
			gridy = 3;
		} else {
			gridx = 0;
			gridy = 13;
		}

		m_clReserveKeeper = new SpielerSternePanel(IMatchRoleID.substGK1, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 1;
			gridy = 3;
		} else {
			gridx = 1;
			gridy = 13;
		}

		m_clReserveDefender = new SpielerSternePanel(IMatchRoleID.substCD1, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 2;
			gridy = 3;
		} else {
			gridx = 2;
			gridy = 13;
		}

		m_clReserveMidfielder = new SpielerSternePanel(IMatchRoleID.substIM1,
				m_bPrint, centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 3;
			gridy = 3;
		} else {
			gridx = 3;
			gridy = 13;
		}

		m_clReserveForward = new SpielerSternePanel(IMatchRoleID.substFW1, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 4;
			gridy = 3;
		} else {
			gridx = 4;
			gridy = 13;
		}

		m_clReserveWinger = new SpielerSternePanel(IMatchRoleID.substWI1, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 0;
			gridy = 2;
		} else {
			gridx = 0;
			gridy = 14;
		}

		m_clCaptain = new SpielerSternePanel(IMatchRoleID.captain, m_bPrint, centerPanel,
				gridx, gridy);

		if (m_bHeim) {
			gridx = 1;
			gridy = 2;
		} else {
			gridx = 1;
			gridy = 14;
		}

		m_clSetPieces = new SpielerSternePanel(IMatchRoleID.setPieces, m_bPrint, centerPanel,
				gridx, gridy);

		if (m_bHeim) {
			gridx = 2;
			gridy = 2;
		} else {
			gridx = 2;
			gridy = 14;
		}

		m_clAusgewechselt1 = new SpielerSternePanel(IMatchRoleID.FirstPlayerReplaced, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 3;
			gridy = 2;
		} else {
			gridx = 3;
			gridy = 14;
		}

		m_clAusgewechselt2 = new SpielerSternePanel(IMatchRoleID.SecondPlayerReplaced, m_bPrint,
				centerPanel, gridx, gridy);

		if (m_bHeim) {
			gridx = 4;
			gridy = 2;
		} else {
			gridx = 4;
			gridy = 14;
		}

		m_clAusgewechselt3 = new SpielerSternePanel(IMatchRoleID.ThirdPlayerReplaced, m_bPrint,
				centerPanel, gridx, gridy);

		final JLabel label = new JLabel();
		label.setOpaque(true);
		layout.setConstraints(label, constraints);
		centerPanel.add(label);

		if (m_bHeim) {
			constraints.gridx = 1;
			constraints.gridy = 1;
			constraints.gridwidth = 1;
		} else {
			constraints.gridx = 1;
			constraints.gridy = 7;
			constraints.gridwidth = 1;
		}

		add(centerPanel, BorderLayout.CENTER);
	}
}
