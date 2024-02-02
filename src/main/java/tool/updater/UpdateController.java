package tool.updater;

import com.install4j.api.launcher.Variables;
import core.HO;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.net.MyConnector;
import core.util.BrowserLauncher;
import core.util.HOLogger;
import core.util.Updater;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public final class UpdateController {

    private static final String RELEASE_NOTES_DEV_URL = "https://github.com/ho-dev/HattrickOrganizer/releases/download/dev/release_notes.html";
    private static final String RELEASE_NOTES_BETA_URL = "https://github.com/ho-dev/HattrickOrganizer/releases/download/beta/release_notes.html";
    private static final String RELEASE_NOTES_STABLE_URL = "https://github.com/ho-dev/HattrickOrganizer/releases/download/tag_stable/release_notes.html";

    /**
     * Check the external site for the latest version according to user preference regarding release channel
     */
    public static void check4update(boolean showNoUpdateAvailableDialog) {
        VersionInfo updateVersion = getUpdateVersion();

        // a version has been found and auto update is allowed
        if (updateVersion != null) {
            showUpdateDialog(updateVersion);
        }

        // no update available
        else if (showNoUpdateAvailableDialog) {
            showNoUpdateAvailableDialog();
        }
    }

    @Nullable
    private static VersionInfo getUpdateVersion() {
        VersionInfo updateVersion = null;

        // check if version available based on channel
        switch (UserParameter.temp().ReleaseChannel) {
            case "Dev":
                VersionInfo devVersion = MyConnector.instance().getLatestVersion();
                if (compareToCurrentVersions(devVersion)) updateVersion = devVersion;
                // no break; to check if there is a newer beta release
            case "Beta":
                VersionInfo betaVersion = MyConnector.instance().getLatestBetaVersion();
                if (compareToCurrentVersions(betaVersion)) {
                    if (compareTwoVersions(betaVersion, updateVersion)) {
                        updateVersion = betaVersion;
                        UserParameter.temp().ReleaseChannel = "Beta";
                    }
                }
                // no break to check if there is a newer stable release
            default:
            case "Stable":
                VersionInfo stableVersion = MyConnector.instance().getLatestStableVersion();
                if (compareToCurrentVersions(stableVersion)) {
                    if (compareTwoVersions(stableVersion, updateVersion)) {
                        updateVersion = stableVersion;
                        UserParameter.temp().ReleaseChannel = "Stable";
                    }
                }
        }
        return updateVersion;
    }

    private static void showUpdateDialog(VersionInfo updateVersion) {
        String versionType = updateVersion.getVersionType();
        String updateAvailable;
        String releaseNoteUrl;
        switch (versionType) {
            case "DEV" -> {
                updateAvailable = HOVerwaltung.instance().getLanguageString("updateDEVavailable");
                releaseNoteUrl = RELEASE_NOTES_DEV_URL;
            }
            case "BETA" -> {
                updateAvailable = HOVerwaltung.instance().getLanguageString("updateBETAavailable");
                releaseNoteUrl = RELEASE_NOTES_BETA_URL;
            }
            default -> {
                updateAvailable = HOVerwaltung.instance().getLanguageString("updateStableavailable");
                releaseNoteUrl = RELEASE_NOTES_STABLE_URL;
            }
        }

        int update = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                new UpdaterPanel("<html><body>" + updateAvailable + "<br/><br/>"
                        + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("ls.version") + ":</font>"
                        + updateVersion.getVersionString() + "<br/>"
                        + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("Released") + ":</font>"
                        + updateVersion.getReleaseDate() + "<br/><br/>"
                        + HOVerwaltung.instance().getLanguageString("ls.button.update") + "?</body></html>",
                        releaseNoteUrl),
                HOVerwaltung.instance().getLanguageString("confirmation.title"),
                JOptionPane.YES_NO_OPTION);

        // Warning, if install via package, ask user to confirmation
        if (update == JOptionPane.YES_OPTION &&
                System.getProperty("install.mode","").equalsIgnoreCase("pkg") &&
                versionType.equals("RELEASE")) {
            update = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                    HOVerwaltung.instance().getLanguageString("ls.button.update.linux.pkg.warning") + "?",
                    HOVerwaltung.instance().getLanguageString("confirmation.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        }

        if (update == JOptionPane.YES_OPTION) {
            updateHO(updateVersion, versionType);
        }
    }

    private static void showNoUpdateAvailableDialog() {
        final int currRev = HO.getRevisionNumber();
        JOptionPane.showMessageDialog(HOMainFrame.instance(), HOVerwaltung.instance()
                .getLanguageString("updatenotavailable")
                + "\n\n"
                + HOVerwaltung.instance().getLanguageString("ls.version")
                + ": "
                + HO.VERSION
                + (currRev > 1 ? " (Build " + currRev + ")" : ""), HOVerwaltung.instance()
                .getLanguageString("ls.menu.file.update") + " - "+ HOVerwaltung.instance()
                .getLanguageString("ls.menu.file.update.ho"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static String getHOZipDownloadUrl(VersionInfo versionInfo, String versionType) {

        // https://github.com/ho-dev/HattrickOrganizer/releases/download/dev/HO-8.0-portable-win-32bits--DEV.zip
        String full_version = versionInfo.getFullVersion();
        return switch (versionType) {
            case "DEV" -> "https://github.com/ho-dev/HattrickOrganizer/releases/download/dev/HO-" + full_version + "-portable-win-DEV-JRE.zip";
            case "BETA" -> "https://github.com/ho-dev/HattrickOrganizer/releases/download/beta/HO-" + full_version + "-portable-win-BETA-JRE.zip";
            default -> "https://github.com/ho-dev/HattrickOrganizer/releases/download/tag_stable/HO-" + full_version + "-portable-win-JRE.zip";
        };
    }

    public static void updateHO(final VersionInfo versionInfo, String versionType) {
        boolean manualUpdate = false;
        try {
            String mediaId = Variables.getCompilerVariable("mediaID");
            if (mediaId != null) {
                // making update via install4J
                Updater.instance().update();
            } else {
                manualUpdate = true;
            }
        } catch (IOException e) {
            HOLogger.instance().warning(UpdateController.class, "Error retrieving compiler var mediaID: " +
                    e.getMessage());
            manualUpdate = true;
        }

        if (manualUpdate) {
            String urlString = getHOZipDownloadUrl(versionInfo, versionType);
            try {
                HOLogger.instance().info(UpdateController.class,
                        "Launching browser to download update manually: " + urlString);
                BrowserLauncher.openURL(urlString);
            } catch (Exception ee) {
                HOLogger.instance().error(UpdateController.class, "Error opening URL: "
                        + urlString + ": " + ee.getMessage());
            }
        }
    }

    public static boolean compareTwoVersions(VersionInfo a, VersionInfo b) {
        if (a == null) return false; // NO version is NOT greater than any version
        if (b == null) return true;  // each not null version is greater than NO version
        return a.getVersion() > b.getVersion() ||
                (a.getVersion() == b.getVersion() && a.getBuild() > b.getBuild());
    }

    // returns true if version is more recent than current version
    public static boolean compareToCurrentVersions(VersionInfo version) {
        if (version == null) return false; // NO version is NOT greater than current version
        return version.getVersion() > HO.VERSION ||
                ((version.getVersion() == HO.VERSION) && (version.getBuild() > HO.getRevisionNumber()));
    }

}
