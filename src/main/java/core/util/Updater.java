package core.util;

import com.install4j.api.launcher.ApplicationLauncher;
import core.HO;
import core.gui.HOMainFrame;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Updater {

    public enum ReleaseChannel {
        DEV("Dev", 0, DEV_UPDATE_XML_URL),
        BETA("Beta", 1, BETA_UPDATE_XML_URL),
        STABLE("Stable", 2, STABLE_UPDATE_XML_URL);

        private static final Map<String, ReleaseChannel> BY_LABEL = new HashMap<>();
        private static final Map<Integer, ReleaseChannel> BY_CODE = new HashMap<>();
        private static final Map<String, ReleaseChannel> BY_XML_URL = new HashMap<>();

        static {
            for (ReleaseChannel e : values()) {
                BY_LABEL.put(e.label, e);
                BY_CODE.put(e.code, e);
                BY_XML_URL.put(e.xmlURL, e);
            }
        }

        public final String label;
        public final int code;
        public final String xmlURL;

        ReleaseChannel(String label, int code, String xmlURL) {
            this.label = label;
            this.code = code;
            this.xmlURL = xmlURL;
        }

        public static ReleaseChannel byLabel(String label) {
            return BY_LABEL.get(label);
        }

        public static ReleaseChannel byCode(int code) {
            return BY_CODE.get(code);
        }

        public static ReleaseChannel byXmlURL(String xmlURL) {
            return BY_XML_URL.get(xmlURL);
        }
    }

    private static final String DEV_UPDATE_XML_URL = "https://github.com/akasolace/HO/releases/download/dev/updates.xml";
    private static final String BETA_UPDATE_XML_URL = "https://github.com/akasolace/HO/releases/download/beta/updates.xml";
    private static final String STABLE_UPDATE_XML_URL = "https://github.com/akasolace/HO/releases/download/tag_stable/updates.xml";
    private static final String UPDATER_APPLICATION_ID = "814";
    private @Nullable String mediaID = null;
    private static Updater clUpdater;


    /**
     * Creates a new instance of ReleaseChannel
     */
    private Updater() {
        try {
            mediaID = com.install4j.api.launcher.Variables.getCompilerVariable("mediaID");
        } catch (IOException e) {
            HOLogger.instance().error(Updater.class, "can't fetch updater variables" + e.toString());
        }
    }

    public static Updater instance() {
        if (clUpdater == null) {
            clUpdater = new Updater();
        }
        return clUpdater;
    }

    public void saveReleaseChannelPreference(ReleaseChannel rc){
        try {
            if (HO.getRevisionNumber() == 0) {mediaID = "HO_IDE_MEDIA_ID";} // we are testing from the IDE and media is not set by install4j
            com.install4j.api.launcher.Variables.saveToPreferenceStore(Map.of("updatesUrl", rc.xmlURL), mediaID, true);
        }
        catch (IOException e) {
            HOLogger.instance().error(Updater.class, "can't store release channel preference in java store" + e.toString());
        }
    }

    public void update() {

        Boolean bValidregisteredMediaID = false;

        try {
            Map<String, Object> vPrefsStore = com.install4j.api.launcher.Variables.loadFromPreferenceStore(mediaID, true);
            if ((vPrefsStore != null) && (vPrefsStore.containsKey("updatesUrl"))){
                String registeredMediaID = vPrefsStore.get("updatesUrl").toString();
                bValidregisteredMediaID = Arrays.stream(new String[]{DEV_UPDATE_XML_URL, BETA_UPDATE_XML_URL, STABLE_UPDATE_XML_URL}).anyMatch(registeredMediaID::equalsIgnoreCase);
            }

            if ((vPrefsStore == null) || (! vPrefsStore.containsKey("updatesUrl")) || (! bValidregisteredMediaID))
            {
                // user has never changed release channel via preference tab, hence no information available in java preference store
                switch (core.model.UserParameter.temp().ReleaseChannel) {
                    case "Stable" -> com.install4j.api.launcher.Variables.saveToPreferenceStore(Map.of("updatesUrl", STABLE_UPDATE_XML_URL), mediaID, true);
                    case "Beta" -> com.install4j.api.launcher.Variables.saveToPreferenceStore(Map.of("updatesUrl", BETA_UPDATE_XML_URL), mediaID, true);
                    default -> com.install4j.api.launcher.Variables.saveToPreferenceStore(Map.of("updatesUrl", DEV_UPDATE_XML_URL), mediaID, true);
                }
                if (bValidregisteredMediaID) {
                    HOLogger.instance().log(Updater.class, "release channel preference written for the first time in java store");
                }
                else{
                    HOLogger.instance().error(Updater.class, "preference store was corrupted it has been reset !");
                }
            }
        } catch (IOException e) {
            HOLogger.instance().error(Updater.class, "error while fetching java store" + e.toString());
        }

        ApplicationLauncher.launchApplicationInProcess(UPDATER_APPLICATION_ID, null, new ApplicationLauncher.Callback() {
                    public void exited(int exitValue) {
                        if (exitValue != 0) {
                            HOLogger.instance().error(Updater.class,"installer exited with value: " + exitValue);
                        }
                    }

                    public void prepareShutdown() {
                        HOLogger.instance().info(Updater.class,"prepare to shutdown !");
                        HOMainFrame.instance().shutdown();
                    }
                }, ApplicationLauncher.WindowMode.FRAME, null
        );

    }
}
