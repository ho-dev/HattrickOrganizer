package core.gui.theme;

import com.github.weisj.darklaf.properties.icons.DerivableImageIcon;
import com.github.weisj.darklaf.properties.icons.IconLoader;
import com.github.weisj.darklaf.util.LogUtil;
import core.db.DBManager;
import core.db.user.UserManager;
import core.gui.HOMainFrame;
import core.gui.theme.dark.DarculaDarkTheme;
import core.gui.theme.dark.SolarizedDarkTheme;
import core.gui.theme.gnome.GnomeTheme;
import core.gui.theme.ho.HOClassicSchema;
import core.gui.theme.light.SolarizedLightTheme;
import core.gui.theme.nimbus.NimbusTheme;
import core.model.UserParameter;
import core.model.player.PlayerAvatar;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.OSUtils;
import tool.updater.UpdateHelper;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Manages all the HO Themes.
 */
public final class ThemeManager {

	/** Name of the default theme. */
	public final static String DEFAULT_THEME_NAME = NimbusTheme.THEME_NAME;

	private final static Path tempImgPath = Paths.get(UserManager.INSTANCE.getDbParentFolder() , "img");
	private final static Path teamLogoPath = tempImgPath.resolve("clubLogos");
	private final static File teamLogoDir = new File(String.valueOf(teamLogoPath));
	private final static Path playerAvatarPath = tempImgPath.resolve("playersAvatar");
	private final static File playerAvatarDir = new File(String.valueOf(playerAvatarPath));
	private final static Map<String, Theme> themes = new LinkedHashMap<>();
	private final static ThemeManager MANAGER = new ThemeManager();

	HOClassicSchema classicSchema = new HOClassicSchema();


	private ThemeManager(){
		initialize();
	}

	public static ThemeManager instance(){
		return MANAGER;
	}

	private void initialize() {

		themes.put(NimbusTheme.THEME_NAME, new NimbusTheme());
		themes.put(DarculaDarkTheme.THEME_NAME, new DarculaDarkTheme());
// Comment out those themes for now as they are not ready yet.
//		themes.put(HighContrastTheme.THEME_NAME, new HighContrastTheme());
		themes.put(SolarizedDarkTheme.THEME_NAME, new SolarizedDarkTheme());
		themes.put(SolarizedLightTheme.THEME_NAME, new SolarizedLightTheme());

		if (OSUtils.isLinux()) {
			themes.put(GnomeTheme.THEME_NAME, new GnomeTheme());
		}

		if (!teamLogoDir.exists()) {
			try {
				Files.createDirectories(teamLogoPath);
			} catch (IOException e) {
				HOLogger.instance().log(this.getClass(),"Failed to create directory for team logos: " + e.getMessage());
			}
		}

		if (!playerAvatarDir.exists()) {
			try {
				Files.createDirectories(playerAvatarPath);
			} catch (IOException e) {
				HOLogger.instance().log(this.getClass(),"Failed to create directory for player Avatars: " + e.getMessage());
			}
		}

		IconLoader.updateThemeStatus(new Object());

		// TODO: Workaround some warnings which are issued incorrectly. To silence them you can call
		LogUtil.getLogger(IconLoader.class).setLevel(Level.SEVERE);
		Logger.getLogger("com.github.weisj.jsvg.parser.SVGLoader").setLevel(Level.SEVERE);
	}

	/**
	 * Returns the list of registered themes.
	 *
	 * @return List<Theme> – List of registered themes
	 */
	public List<Theme> getRegisteredThemes() {
		return new ArrayList<>(themes.values());
	}

	public static Color getColor(String key) {
		Object obj;

		obj = instance().classicSchema.getThemeColor(key);
		if(obj instanceof Color)
			return (Color)obj;
		if(obj instanceof String)
			return getColor(obj.toString());

		if(obj == null)
			obj = UIManager.getColor(key);

		if(obj == null)
			return instance().classicSchema.getDefaultColor(key);

		return (Color)obj;
	}

	public boolean isSet(String key) {
		Boolean tmp = (Boolean)classicSchema.get(key);
		if(tmp == null)
			tmp = Boolean.FALSE;
		return tmp;
	}

	public void put(String key, Object value){
		classicSchema.put(key, value);
	}

	private  <T> T get(String key, Class<T> type) {
		Object obj = classicSchema.get(key);
		if (type.isInstance(obj)) {
			return type.cast(obj);
		}
		return null;
	}

	public Object get(String key){
		Object tmp = classicSchema.get(key);

		if(tmp == null)
			tmp = UIManager.get(key);

		return tmp;
	}

	private Icon getIconImpl(String key) {
		Object tmp = classicSchema.get(key);
		if(tmp == null)
			return null;
		if(tmp instanceof Icon)
			return (Icon)tmp;
		return classicSchema.loadImageIcon(tmp.toString());
	}

	private Icon getScaledIconImpl(String key, int x, int y){
		String scaledKey = key + "(" + x + "," + y + ")";
		Icon icon = get(scaledKey, Icon.class);
		if (icon == null) {
			icon = ImageUtilities.getScaledIcon(getIconImpl(key), x, y);
			if (icon != null) put(scaledKey, icon);
		}
		return icon;
	}

	public static Icon getIcon(String key) {
		return instance().getIconImpl(key);
	}

	public static Object getIconPath(String key){
		return instance().get(key);
	}

	public static Icon getScaledIcon(String key, int x,int y) {
		final Icon scaledIcon = instance().getScaledIconImpl(key, x, y);

		// If darklaf, retrieve image, and use as icon.
		if (scaledIcon instanceof DerivableImageIcon) {
			return new ImageIcon(((DerivableImageIcon)scaledIcon).getImage());
		}

		return scaledIcon;
	}

	public Icon getSmallClubLogo(int teamID){
		return getClubLogo(teamID, 18);
	}

	public Icon getClubLogo(int teamID){
		return getClubLogo(teamID, 36);
	}

	public String getTeamLogoFilename(int teamID){
		var info = DBManager.instance().loadTeamLogoInfo(teamID);
		if ( info != null){
			return teamLogoPath.resolve(info.getFilename()).toString();
		}
		return null;
	}

	public Icon getClubLogo(int teamID, int width) {
		int height = Math.round(width * 260f / 210f);
		String logoPath = null;
		var info = DBManager.instance().loadTeamLogoInfo(teamID);
		if ( info != null ) {
			var url = info.getUrl();
			if (url != null && !url.isEmpty() && !url.equals("null")) {
				// Check if the logo has already been downloaded
				var filename = teamLogoPath.resolve(info.getFilename()).toString();
				File logo = new File(filename);
				if (logo.exists()) {
					logoPath = filename;
				} else {
					// we try to download the logo from HT servers
					boolean bSuccess = UpdateHelper.download(url, logo);
					if (bSuccess) {
						logoPath = filename;
					}
					else {
						HOLogger.instance().error(this.getClass(), "error when trying to download logo of team ID: " + teamID + "\n" + url);
					}
				}
				//we update LAST_ACCESS value
				info.setLastAccess(HODateTime.now());
				DBManager.instance().storeTeamLogoInfo(info);
			}
		}

		if (logoPath == null) {
			// default logo is used for teams without logo
			HOLogger.instance().debug(this.getClass(), "logo not found for team " + teamID);
			return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
		}

		String scaledKey = "team_logo_" + teamID + "_(" + width + "," + height + ")";
		Icon scaledIcon = get(scaledKey, Icon.class);
		if (scaledIcon == null) {
			BufferedImage img;
			try {
				var logoFile = new File(logoPath);
				img = ImageIO.read(logoFile);
				if ( img != null) {
					ImageIcon iconOriginal = new ImageIcon(img);
					scaledIcon = ImageUtilities.getScaledIcon(iconOriginal, width, height);
				}
				else {
					// remove damaged icon file
					if ( !logoFile.delete() ) {
						HOLogger.instance().debug(this.getClass(), "damaged logo file can not be deleted: " + logoPath);
					}
					return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			if (scaledIcon != null) put(scaledKey, scaledIcon);
		}

		return scaledIcon;
	}

	/**
	 * Function called during data download
	 * <p>
	 * During users access to internet download all missing avatars
	 *
	 * @param playersAvatar list of player avatars fetched avatars CSV
	 */
	public void generateAllPlayerAvatar(List<PlayerAvatar> playersAvatar, int progress){

		String avatarPath;
		File avatarImg;

		//1. estimate number of avatar to download
		List<PlayerAvatar> missingAvatars = new ArrayList<>();

		for (var avatar : playersAvatar){

			avatarPath = playerAvatarPath.resolve(avatar.getPlayerID() + ".png").toString();
			avatarImg = new File(avatarPath);

			if (!avatarImg.exists()) {
				missingAvatars.add(avatar);
			}
		}

		//2. Download missing avatar
		int i=1;
		int iMax = missingAvatars.size();

		for (var avatar:missingAvatars) {
			HOLogger.instance().info(this.getClass(), "Downloading player's avatar: %s/%s".formatted(i, iMax));
			HOMainFrame.instance().setInformation("Downloading player's avatar: %s/%s".formatted(i, iMax), progress);
			try {
				avatar.generateAvatar(playerAvatarPath);
			} catch (IOException e) {
				HOLogger.instance().error(ThemeManager.class, "Error processing Player Avatar for player: " + avatar.getPlayerID());
			}
			i++;
		}
	}

	public Icon getPlayerAvatar(int playerID){
		return getPlayerAvatar(playerID, 92);
	}

	public Icon getPlayerAvatar(int playerID, int width) {
		int height = Math.round(width * 123f / 92f);

		String avatarPath = playerAvatarPath.resolve(playerID + ".png").toString();
		File avatarImg = new File(avatarPath);

		if (!avatarImg.exists()) {
			HOLogger.instance().log(this.getClass(), "avatar for player " + playerID + " not found locally. It will be downloaded");
			return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
		}

		String scaledKey = "team_logo_" + playerID + "_(" + width + "," + height + ")";
		Icon scaledIcon = get(scaledKey, Icon.class);
		if (scaledIcon == null) {
			BufferedImage img;
			try {
				img = ImageIO.read(new File(avatarPath));
				if ( img != null) {
					ImageIcon iconOriginal = new ImageIcon(img);
					scaledIcon = ImageUtilities.getScaledIcon(iconOriginal, width, height);
				}
				else {
					return getScaledIcon(HOIconName.NO_CLUB_LOGO, width, height);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			if (scaledIcon != null) put(scaledKey, scaledIcon);
		}

		return scaledIcon;
	}

	public void setCurrentTheme() {

		try {
			boolean success = false;

			Theme theme = themes.get(UserParameter.instance().skin);
			if (theme != null) {
				success = theme.loadTheme();
			}

			if (!success) {
				Theme classicTheme = themes.get(DEFAULT_THEME_NAME);
				success = classicTheme.loadTheme();
			}

			initializeMacKeyBindings(success);

		} catch (Exception e) {
			HOLogger.instance().log(HOMainFrame.class, e);
		}
	}

	private void initializeMacKeyBindings(boolean success) {
		// #177 Standard shortcuts for copy/cut/paste don't work in MacOSX if LookAndFeel changes
		if (success && OSUtils.isMac()) {
			InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK), DefaultEditorKit.selectAllAction);
		}
	}
}
