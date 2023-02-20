package module.teamAnalyzer.ui;

import core.gui.comp.entry.RatingTableEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.player.MatchRoleID;
import core.util.HelperWrapper;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.SpotLineup;

import java.awt.*;
import java.util.Map;
import java.util.ArrayList;

import javax.swing.*;

import static core.gui.theme.HOIconName.*;

public class PlayerPanel extends JPanel {

	private final Color PANEL_BG = ThemeManager.getColor(HOColorName.PANEL_BG);
	private final Color LABEL_FG = ThemeManager.getColor(HOColorName.LABEL_FG);

	protected JLabel appearanceField = new JLabel("", SwingConstants.RIGHT);
	protected JLabel nameField = new JLabel("", SwingConstants.LEFT);
	protected JLabel positionField = createLabel("", LABEL_FG, 0);
	protected JLabel positionImage = new JLabel();
	protected JLabel jlSpecialty = new JLabel();
	protected JLabel jlInjuryStatus = new JLabel();
	protected JLabel jlBookingstatus = new JLabel();
	protected JLabel jlTransferListedstatus = new JLabel();
	protected JPanel ratingPanel = new JPanel();
	protected TacticPanel tacticPanel = new TacticPanel();
	//private final JPanel mainPanel;
	private final PlayerInfoPanel infoPanel = new PlayerInfoPanel();

	protected boolean containsPlayer = false;

	/**
	 * Creates a new PlayerPanel object.
	 */
	public PlayerPanel() {
		//setLayout(new BorderLayout());

		Font nFont = new Font(nameField.getFont().getFontName(), Font.BOLD, nameField.getFont().getSize());

		nameField.setFont(nFont);

		JPanel details = new JPanel();

		details.setBorder(BorderFactory.createEtchedBorder());
		details.setBackground(getBackGround());
		details.setLayout(new BorderLayout());

		JPanel images = new JPanel();
		images.setLayout(new BoxLayout(images, BoxLayout.X_AXIS));
		images.setBackground(getBackGround());

		images.add(positionImage, BorderLayout.WEST);
		images.add(jlSpecialty, BorderLayout.EAST);
		images.add(jlInjuryStatus, BorderLayout.EAST);
		images.add(jlBookingstatus, BorderLayout.EAST);
		images.add(jlTransferListedstatus, BorderLayout.EAST);
		details.add(images, BorderLayout.WEST);
		details.add(nameField, BorderLayout.CENTER);
		details.add(appearanceField, BorderLayout.EAST);

		JPanel centerPanel = new JPanel();

		centerPanel.setBorder(BorderFactory.createEtchedBorder());
		centerPanel.setBackground(getBackGround());
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(details, BorderLayout.NORTH);
		centerPanel.add(ratingPanel, BorderLayout.WEST);

		if (!(this instanceof UserTeamPlayerPanel)) {
			centerPanel.add(infoPanel, BorderLayout.SOUTH);
		}

//		mainPanel = new ImagePanel();
//		mainPanel.setLayout(new BorderLayout());
//		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new GridBagLayout());
		var c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
//		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
//		mainPanel.setPreferredSize(getDefaultSize());
		add(positionField, c);
		c.gridy++;
		add(centerPanel,c);
		c.gridy++;
		add(tacticPanel, c);
		//add(mainPanel, BorderLayout.CENTER);
	}

	protected Color getBackGround() {
		return PANEL_BG;
	}

	public boolean getContainsPlayer() {
		return containsPlayer;
	}

	/**
	 * Reload and refresh data for a certain spot/player.
	 */
	public void reload(SpotLineup lineup, int week, int season) {
		tacticPanel.setVisible(SystemManager.isTacticDetail.isSet());
		//mainPanel.setPreferredSize(getDefaultSize());

		if (lineup != null) {
			containsPlayer = true;			

			nameField.setText(lineup.getName());
			appearanceField.setText("" + lineup.getAppearance());

			if (SystemManager.isShowPlayerInfo.isSet()) {
				PlayerInfo pi = PlayerDataManager.getPlayerInfo(lineup.getPlayerId(), week, season);

				if (pi.getAge() != 0) {
					infoPanel.setValue(pi);
				} else {
					infoPanel.clearData();
				}

				infoPanel.setVisible(true);
			}

			int posCode = HelperWrapper.instance().getPosition(lineup.getPosition());
			positionImage.setIcon(ImageUtilities.getImage4Position(posCode, (byte) 0, 0));

			var specialEvent = lineup.getSpecialEvent();

			if (lineup.getPlayerId() == 0) {
				jlSpecialty.setIcon(null);
				jlInjuryStatus.setIcon(null);
				jlBookingstatus.setIcon(null);
				jlTransferListedstatus.setIcon(null);
			} else {
//				HOLogger.instance().debug(this.getClass(), lineup.getName() + ":" +lineup.getStatus() + " " + lineup.getInjuryStatus() + " " +  lineup.getBookingStatus() + " " + lineup.getTransferListedStatus());
				jlSpecialty.setIcon(ImageUtilities.getLargePlayerSpecialtyIcon(HOIconName.SPECIALTIES[specialEvent]));
				jlInjuryStatus.setIcon(getInjuryStatusIcon(lineup.getInjuryStatus()));
				jlBookingstatus.setIcon(getBookingStatusIcon(lineup.getBookingStatus()));
				jlTransferListedstatus.setIcon(getTransferListedStatusIcon(lineup.getTransferListedStatus()));
			}

			positionField.setText(MatchRoleID.getNameForPosition((byte) lineup.getPosition()));
			updateRatingPanel(lineup.getRating());
			tacticPanel.reload(lineup.getTactics());
		} else {
			containsPlayer = false;
			nameField.setText(" ");
			appearanceField.setText(" ");
			positionField.setText(" ");
			infoPanel.setVisible(false);
			infoPanel.clearData();
			updateRatingPanel(0);
			positionImage.setIcon(ImageUtilities.getImage4Position(0, (byte) 0, 0));
			jlSpecialty.setIcon(null);
			jlInjuryStatus.setIcon(null);
			jlBookingstatus.setIcon(null);
			jlTransferListedstatus.setIcon(null);
			tacticPanel.reload(new ArrayList<>());
		}
	}

	protected Icon getInjuryStatusIcon(int injuryStatus) {
		return switch (injuryStatus) {
			case PlayerDataManager.BRUISED -> ImageUtilities.getPlasterIcon(12, 12);
			case PlayerDataManager.INJURED -> ImageUtilities.getInjuryIcon(12, 12);
			default -> null;
		};
	}

	protected Icon getBookingStatusIcon(int bookingStatus) {
		return switch (bookingStatus) {
			case PlayerDataManager.YELLOW -> ImageUtilities.getSvgIcon(ONEYELLOW_TINY, 12, 12);
			case PlayerDataManager.DOUBLE_YELLOW -> ImageUtilities.getSvgIcon(TWOYELLOW_TINY, 12, 12);
			case PlayerDataManager.SUSPENDED -> ImageUtilities.getSvgIcon(SUSPENDED_TINY, 12, 12);
			default -> null;
		};
	}

	protected Icon getTransferListedStatusIcon(int transferStatus) {
		if(transferStatus == PlayerDataManager.TRANSFER_LISTED) {
			return ImageUtilities.getSvgIcon(TRANSFERLISTED_TINY, Map.of("foregroundColor", ThemeManager.getColor(HOColorName.PLAYER_SPECIALTY_COLOR)), 14, 14);
		}
		else{
			return null;
		}
	}

	protected void updateRatingPanel(double rating) {
		ratingPanel.removeAll();
		ratingPanel.setLayout(new BorderLayout());

		JPanel starPanel = (JPanel) new RatingTableEntry((int) Math.round(rating * 2))
				.getComponent(false);
		ratingPanel.add(starPanel, BorderLayout.WEST);
		ratingPanel.setOpaque(true);
	}

	private JLabel createLabel(String text, Color farbe, int Bordertype) {
		JLabel bla = new JLabel(text);

		bla.setHorizontalAlignment(JLabel.HORIZONTAL);
		bla.setForeground(farbe);
		bla.setBorder(BorderFactory.createEtchedBorder(Bordertype));

		return bla;
	}
}
