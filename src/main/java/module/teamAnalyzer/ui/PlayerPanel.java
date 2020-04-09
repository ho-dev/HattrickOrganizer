// %2588915486:hoplugins.teamAnalyzer.ui%
package module.teamAnalyzer.ui;

import core.gui.comp.entry.RatingTableEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.UserParameter;
import core.model.player.MatchRoleID;
import core.module.config.ModuleConfig;
import core.util.HelperWrapper;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.manager.PlayerDataManager;
import module.teamAnalyzer.vo.PlayerInfo;
import module.teamAnalyzer.vo.SpotLineup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PlayerPanel extends JPanel {

	private final Color PANEL_BG = ThemeManager.getColor(HOColorName.PANEL_BG);
	private final Color LABEL_FG = ThemeManager.getColor(HOColorName.LABEL_FG);

	private static final long serialVersionUID = 1838357704496299083L;
	protected JLabel appearanceField = new JLabel("", SwingConstants.RIGHT);
	protected JLabel nameField = new JLabel("", SwingConstants.LEFT);
	protected JLabel positionField = createLabel("", LABEL_FG, 0);
	protected JLabel positionImage = new JLabel();
	protected JLabel specialEventImage = new JLabel();
	protected JPanel ratingPanel = new JPanel();
	protected TacticPanel tacticPanel = new TacticPanel();
	private final JPanel mainPanel;
	private final PlayerInfoPanel infoPanel = new PlayerInfoPanel();

	protected boolean containsPlayer = false;

	/**
	 * Creates a new PlayerPanel object.
	 */
	public PlayerPanel() {
		setLayout(new BorderLayout());

		Font nFont = new Font(nameField.getFont().getFontName(), Font.BOLD, nameField.getFont().getSize());

		nameField.setFont(nFont);

		JPanel details = new JPanel();

		details.setBorder(BorderFactory.createEtchedBorder());
		details.setBackground(getBackGround());
		details.setLayout(new BorderLayout());

		JPanel images = new JPanel();

		images.setBackground(getBackGround());
		images.setLayout(new BorderLayout());
		images.add(positionImage, BorderLayout.WEST);
		images.add(specialEventImage, BorderLayout.EAST);
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

		mainPanel = new ImagePanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		mainPanel.setPreferredSize(getDefaultSize());
		mainPanel.add(positionField, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(tacticPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);
	}

	protected Color getBackGround() {
		return PANEL_BG;
	}

	public Dimension getDefaultSize() {
		int height = 60;

		if (!(this instanceof UserTeamPlayerPanel)) {
			if (ModuleConfig.instance().getBoolean(SystemManager.ISSHOWPLAYERINFO)) {
				height = height + 50;
			}
		}

		if (ModuleConfig.instance().getBoolean(SystemManager.ISTACTICDETAIL)) {
			height = height + 50;
		}

		// return new Dimension(180, height); - Blaghaid
		return new Dimension(150, height);
	}

	public boolean getContainsPlayer() {
		return containsPlayer;
	}

	/**
	 * Reload and refresh data for a certain spot/player.
	 */
	public void reload(SpotLineup lineup, int week, int season) {
		tacticPanel.setVisible(ModuleConfig.instance().getBoolean(SystemManager.ISTACTICDETAIL));
		mainPanel.setPreferredSize(getDefaultSize());

		if (lineup != null) {
			containsPlayer = true;			

			nameField.setText(getPlayerName(lineup.getName()));
			setPlayerStatus(lineup.getStatus());

			appearanceField.setText("" + lineup.getAppearance());

			if (ModuleConfig.instance().getBoolean(SystemManager.ISSHOWPLAYERINFO)) {
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
			int specialEvent = PlayerDataManager.getLatestPlayerInfo(lineup.getPlayerId())
					.getSpecialEvent();

			if (lineup.getPlayerId() == 0) {
				specialEventImage.setIcon(null);
			} else {
				specialEventImage.setIcon(ThemeManager.getIcon(HOIconName.SPECIALTIES[specialEvent]));
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
			specialEventImage.setIcon(null);
			tacticPanel.reload(new ArrayList<>());
		}
	}

	protected void setPlayerStatus(int status) {
		switch (status) {
		case PlayerDataManager.INJURED:
			nameField.setForeground(UserParameter.instance().FG_INJURED);
			break;
		case PlayerDataManager.SUSPENDED:
			nameField.setForeground(UserParameter.instance().FG_RED_CARD);
			break;
		case PlayerDataManager.SOLD:
			nameField.setForeground(UserParameter.instance().FG_TRANSFERMARKT);
			break;
		default:
			nameField.setForeground(LABEL_FG);
			break;
		}
	}

	private String getPlayerName(String name) {
		return " " + name.substring(0, 1) + "." + name.substring(name.indexOf(" ") + 1);
	}

	protected void updateRatingPanel(double rating) {
		ratingPanel.removeAll();
		ratingPanel.setLayout(new BorderLayout());

		JPanel starPanel = (JPanel) new RatingTableEntry((int) Math.round(rating * 2), true)
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
