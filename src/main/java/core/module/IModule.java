package core.module;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public interface IModule {

    int STATUS_DEACTIVATED = 0;
    int STATUS_ACTIVATED = 1;
    int STATUS_STARTUP = 2;

    int PLAYEROVERVIEW = 1;
    int LINEUP = 2;
    int SERIES = 3;
    int MATCHES = 4;
    int PLAYERANALYSIS = 5;
    int STATISTICS = 6;
    int TRANSFERS = 7;
    int TRAINING = 8;
    int MISC = 9;
    int TEAMANALYZER = 10;
    int TSFORECAST = 11;
    int SPECIALEVENTS = 12;
    int TEAM_OF_THE_WEEK = 13;
    int NTHRF = 14;
    int EVIL_CARD = 15;
    int IFA = 16;
    int FLAGSCOLLECTOR = 17;
    //public static final int MATCHESANALYZER	= 18;
    //int OPPONENTSPY = 19;
    int YOUTH = 20;

    int getModuleId();

    String getDescription();

    boolean hasMainTab();

    JPanel createTabPanel();

    boolean hasConfigPanel();

    JPanel createConfigPanel();

    boolean isStartup();

    boolean isActive();

    KeyStroke getKeyStroke();

    boolean hasMenu();

    JMenu getMenu();

    int getStatus();

    void setStatus(int statusId);

    void storeUserSettings();
}
