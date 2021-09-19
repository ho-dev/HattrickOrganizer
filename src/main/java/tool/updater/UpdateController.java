package tool.updater;

import core.HO;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.net.MyConnector;
import core.util.HOLogger;
import core.util.Updater;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public final class UpdateController {

    private static final String RELEASE_NOTES_DEV_URL = "https://github.com/akasolace/HO/releases/download/dev/release_notes.html";
    private static final String RELEASE_NOTES_BETA_URL = "https://github.com/akasolace/HO/releases/download/beta/release_notes.html";
    private static final String RELEASE_NOTES_STABLE_URL = "https://github.com/akasolace/HO/releases/download/tag_stable/release_notes.html";

    /**
     * Check the external site for the latest version according to user preference regarding release channel
     */
    public static void check4update(boolean showNoUpdateAvailableDialog) {
        VersionInfo updVersion = getUpdateVersion();

        // a version has been found and auto update is allowed
        if (updVersion != null) {
            showUpdateDialog(updVersion);
        }

        // no update available
        else if (showNoUpdateAvailableDialog) {
//            if (updVersion == null){
            // This condition is always true
            showNoUpdateAvailableDialog();

            // This part can be never reached
//            } else {
//                    JOptionPane.showMessageDialog(HOMainFrame.instance(), "auto update not possible, a fresh install is required !",    // TODO: put this as a language string
//                            HOVerwaltung.instance().getLanguageString("ls.menu.file.update") + " - "+ HOVerwaltung.instance().getLanguageString("ls.menu.file.update.ho"),
//                            JOptionPane.INFORMATION_MESSAGE);
//            }
        }
    }

    @Nullable
    private static VersionInfo getUpdateVersion() {
        VersionInfo updVersion = null;

        // check if version available based on channel
        switch (core.model.UserParameter.temp().ReleaseChannel) {
            case "Dev":
                VersionInfo devVersion = MyConnector.instance().getLatestVersion();
                if (compareToCurrentVersions(devVersion)) updVersion = devVersion;
                // no break; to check if there is a newer beta release
            case "Beta":
                VersionInfo betaVersion = MyConnector.instance().getLatestBetaVersion();
                if (compareToCurrentVersions(betaVersion)) {
                    if (compareTwoVersions(betaVersion, updVersion)) {
                        updVersion = betaVersion;
                    }
                }
                // no break to check if there is a newer stable release
            default:
            case "Stable":
                VersionInfo stableVersion = MyConnector.instance().getLatestStableVersion();
                if (compareToCurrentVersions(stableVersion)) {
                    if (compareTwoVersions(stableVersion, updVersion)) {
                        updVersion = stableVersion;
                    }
                }
        }
        return updVersion;
    }

    private static void showUpdateDialog(VersionInfo updVersion) {
        String versionType = updVersion.getVersionType();
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
                        + updVersion.getVersionString() + "<br/>"
                        + "<font color=gray>" + HOVerwaltung.instance().getLanguageString("Released") + ":</font>"
                        + updVersion.getReleaseDate() + "<br/><br/>"
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
            updateHO(updVersion.getFullVersion(), updVersion.getVersion(), versionType);
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

    public static String get_HO_zip_download_url(String full_version, double version, String versionType) {

        return switch (versionType) {
            case "DEV" -> "https://github.com/akasolace/HO/releases/download/dev/HO-" + full_version + "-portable-win-DEV.zip";
            case "BETA" -> "https://github.com/akasolace/HO/releases/download/beta/HO-" + full_version + "-portable-win-BETA.zip";
            default -> "https://github.com/akasolace/HO/releases/download/tag_stable/HO-" + full_version + "-portable-win.zip";
        };
    }

    public static void updateHO(String full_version, double version, String versionType) {
        updateHO(get_HO_zip_download_url(full_version, version, versionType));
    }

    public static void updateHO(final String urlString) {
        if (HO.isPortableVersion()) {
            // HO! manage the (partial) update
            File tmp = new File("update.piz");
            HOMainFrame.instance().setWaitInformation(0);
            if (!UpdateHelper.download(urlString, tmp)) {
                HOMainFrame.instance().resetInformation();
                HOLogger.instance().error(UpdateController.class, "Could not download: " + urlString);
                return;
            }
            HOMainFrame.instance().resetInformation();

            JOptionPane.showMessageDialog(null,
                    HOVerwaltung.instance().getLanguageString("NeustartErforderlich"), HOVerwaltung.instance()
                            .getLanguageString("ls.menu.file.update") + " - "+ HOVerwaltung.instance()
                            .getLanguageString("ls.menu.file.update.ho"),
                    JOptionPane.INFORMATION_MESSAGE);

            HOMainFrame.instance().shutdown();
        }
        else {
            // making update via install4J
            Updater.instance().update();
        }

    }

    public static boolean compareTwoVersions(VersionInfo a, VersionInfo b) {
        if (a == null) return false; // NO version is NOT greater than any version
        if (b == null) return true;  // every version is greater or equal to NO version
        return a.getVersion() > b.getVersion() ||
                (a.getVersion() == b.getVersion() && a.getBuild() > b.getBuild());
    }

    // returns true is a more recent than current version
    public static boolean compareToCurrentVersions(VersionInfo a) {
        if (a == null) return false; // NO version is NOT greater than current version
        return a.getVersion() > HO.VERSION ||
                ((a.getVersion() == HO.VERSION) && (a.getBuild() > HO.getRevisionNumber()));
    }

}
