package core.util;

import com.install4j.api.launcher.ApplicationLauncher;
import core.gui.HOMainFrame;
import core.option.ReleaseChannelPanel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Updater {

    private static final String DEV_UPDATE_XML_URL = "https://github.com/akasolace/HO/releases/download/dev/updatesDEV.xml";
    private static final String BETA_UPDATE_XML_URL = "http://www.updatesBETA.xml";
    private static final String STABLE_UPDATE_XML_URL = "http://www.updates.xml";
    private static final String UPDATER_APPLICATION_ID = "814";

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

        private ReleaseChannel(String label, int code, String xmlURL) {
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

    public static void saveReleaseChannelPreference(ReleaseChannel rc){
        try {
            com.install4j.api.launcher.Variables.saveToPreferenceStore(Map.of("updatesUrl", rc.xmlURL), true);
        }
        catch (IOException e) {
            HOLogger.instance().error(ReleaseChannelPanel.class, "can't store release channel preference in java store" + e.toString());
        }
    }

    public static void update() {
        ApplicationLauncher.launchApplicationInProcess(UPDATER_APPLICATION_ID, null, new ApplicationLauncher.Callback() {
                    public void exited(int exitValue) {
                        HOLogger.instance().log(ReleaseChannelPanel.class,"installer exited with value: " + exitValue);
                    }

                    public void prepareShutdown() {
                        HOMainFrame.instance().shutdown();
                    }
                }, ApplicationLauncher.WindowMode.FRAME, null
        );
    }
}
