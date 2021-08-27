package core.model;

import core.HO;
import core.db.DBManager;
import core.util.GUIUtils;
import module.lineup.assistant.LineupAssistant;
import module.matches.MatchLocation;

import java.util.HashMap;
import java.util.Map;


/**
 * User configuration. Loaded when HO starts and saved when HO! exits.
 */
public final class UserParameter extends Configuration {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static UserParameter m_clUserParameter;
    private static UserParameter m_clTemp;

    //------Konstanten-----------------------------------------------
    public static final int SORT_NAME = 0;
    public static final int SORT_BESTPOS = 1;
    public static final int SORT_AUFGESTELLT = 2;
    public static final int SORT_GRUPPE = 3;
    public static final int SORT_BEWERTUNG = 4;
    public static final int GOALKEEPER_AT_TOP = 0;
    public static final int GOALKEEPER_AT_BOTTOM = 1;
    public static final int POSITIONNAMES_SHORT = 0;
    public static final int POSITIONNAMES_LONG = 1;
    public static final int RATINGPREDICTIONMODEL_NEW = 0;
    public static final int RATINGPREDICTIONMODEL_OLD = 1;

    //~ Instance fields ----------------------------------------------------------------------------

    public String AccessToken = "";
    public String TokenSecret = "";

    public String ProxyAuthName = "";
    public String ProxyAuthPassword = "";

    //Proxy
    public String ProxyHost = "";
    public String ProxyPort = "";

    //AufstellungsAssistentPanel
    public String aufstellungsAssistentPanel_gruppe = "";

    //Path to stored HRFs
    public String hrfImport_HRFPath = "";
    //    public String htip = "www1.hattrick.org";
    public String matchLineupImport_Path = "";
    public String spielPlanImport_Path = "";

    /**
     * Name of language
     */
    public String sprachDatei = "English";
    /**
     * is proxy activ
     */
    public boolean ProxyAktiv;
    /**
     * is proxy authentication activ
     */
    public boolean ProxyAuthAktiv;
    /**
     * is linup groupfilter activ
     */
    public boolean aufstellungsAssistentPanel_cbfilter;
    public boolean aufstellungsAssistentPanel_form = true;
    public boolean aufstellungsAssistentPanel_gesperrt;
    public boolean aufstellungsAssistentPanel_idealPosition;
    public boolean aufstellungsAssistentPanel_notLast;
    public boolean aufstellungsAssistentPanel_verletzt;
    public boolean lineupAssistentPanel_include_group = true;

    //Dialog, wo mit welchem Namen das HRF gespeichert werden soll

    /**
     * option parameter
     */
    public boolean showHRFSaveDialog = true;

    //Download Options
    /**
     * XML Download checked in Download Dialog?
     */
    public boolean xmlDownload = true;
    /**
     * Fixtures Download checked in Download Dialog?
     */
    public boolean fixtures = true;
    /**
     * Current Matchlist Download checked in Download Dialog?
     */
    public boolean downloadCurrentMatchlist = true;
    public boolean downloadSingleMatches = true;
    public boolean downloadLadderMatches = true;
    public boolean downloadTournamentGroupMatches = true;
    public boolean downloadTournamentPlayoffMatches = true;
    public boolean downloadDivisionBattleMatches = true;

    //Release Channel options
    public String ReleaseChannel = null;

    //Whether or not the player analysis is packed vertically one below the other
    public boolean spieleranalyseVertikal = true;
    public boolean statistikAlleBeschriftung = true;
    public boolean statistikAlleErfahrung = true;
    public boolean statistikAlleFluegel;
    public boolean statistikAlleForm = true;
    public boolean statistikAlleFuehrung = true;
    public boolean statistikAlleHilfslinien = true;
    public boolean statistikAlleKondition = true;
    public int statisticsTeamSumOrAverage = 0;
    public int statisticsFinanceChartType = 0;
    public boolean statistikAllLoyalty;
    public boolean statistikAllTSI;
    public boolean statistikAllWages;

    public boolean statistikAllePasspiel;
    public boolean statistikAlleSpielaufbau;
    public boolean statistikAlleStandards;
    public boolean statistikAlleTorschuss;
    public boolean statistikAlleTorwart;
    public boolean statistikAlleVerteidigung;
    public boolean statistikBeschriftung = true;
    public boolean statistikBewertung = true;
    public boolean statistikErfahrung = true;
    public boolean statistikFananzahl;
    public boolean statistikFans;
    public boolean statistikFinanzenHilfslinien = true;
    public boolean statisticsFinanceIncludeTransfers = true;
    public boolean statisticsClubHelpLines = true;
    public boolean statisticsClubAssistantTrainersLevel = true;
    public boolean statisticsClubFinancialDirectorsLevel = true;
    public boolean statisticsClubFormCoachsLevel = true;
    public boolean statisticsClubDoctorsLevel = true;
    public boolean statisticsClubSpokePersonsLevel = true;
    public boolean statisticsClubSportPsychologistLevels = true;
    public boolean statisticsClubTacticalAssistantLevels = true;
    public boolean statisticsClubYouthSquadLevel = true;
    public boolean statisticsClubYouthSquadInvestment = true;
    public boolean statisticsClubFanClubSize = true;
    public boolean statisticsClubGlobalRanking = true;
    public boolean statisticsClubLeagueRanking = true;
    public boolean statisticsClubRegionRanking = true;
    public boolean statisticsClubPowerRating = true;
    public boolean statistikFluegel;
    public boolean statistikForm = true;
    public boolean statistikFuehrung = true;
    public boolean statistikGesamtAusgaben = true;
    public boolean statistikGesamtEinnahmen = true;
    public boolean statistikGewinnVerlust = true;
    public boolean statistikHilfslinien = true;
    public boolean statistikJugend;
    public boolean statistikKondition = true;
    public boolean statistikKontostand = true;
    public boolean statistikLoyalty;
    public boolean statistikMarktwert;
    public boolean statistikPasspiel;
    public boolean statistikSonstigeAusgaben;
    public boolean statistikSonstigeEinnahmen;
    public boolean statistikSpielaufbau;
    public boolean statistikSpieleAbwehrzentrum = true;
    public boolean statistikSpieleAngriffszentrum = true;
    public boolean statistikSpieleBewertung = true;
    public boolean statistikSpieleGesamt = true;
    public boolean statistikSpieleLinkeAbwehr = true;
    public boolean statistikSpieleLinkerAngriff = true;
    public boolean statistikSpieleMittelfeld = true;
    public boolean statistikSpieleRechteAbwehr = true;
    public boolean statistikSpieleRechterAngriff = true;
    public boolean statistikSpieleSelbstvertrauen;
    public boolean statistikSpieleStimmung;
    public boolean statistikSpieleHatStats;
    public boolean statistikSpieleLoddarStats;
    public boolean statistikSpielerFinanzenBeschriftung = true;

    //Unused
    public boolean statistikSpielerFinanzenGehalt = true;
    public boolean statistikSpielerFinanzenHilfslinien = true;

    //Unused
    public boolean statistikSpielerFinanzenMarktwert = true;

    public boolean statistikSpielergehaelter;
    public boolean statistikSponsoren;
    public boolean statistikStadion;
    public boolean statistikStandards;
    public boolean statistikTorschuss;
    public boolean statistikTorwart;
    public boolean statistikTrainerstab;
    public boolean statistikVerteidigung;
    public boolean statistikZinsaufwendungen;
    public boolean statistikZinsertraege;
    public boolean statistikZuschauer;


    //    public boolean tempTabArenasizer = true;
    public boolean tempTabAufstellung;
    public boolean tempTabInformation = true;
    public boolean tempTabLigatabelle;
    public boolean tempTabSpiele;
    public boolean tempTabSpieleranalyse;

    //Temporäre Tabs
    public boolean tempTabSpieleruebersicht;
    public boolean tempTabStatistik;


    //Update
    public boolean updateCheck = false;

    //Zahlen hinter den Bewertungen anzeigen
    public boolean zahlenFuerSkill = true;

    public float TrainerFaktor = 1.0f;


    //Sonstiges
    //currency factor
    public float faktorGeld = 1f;
    //Faktor für Zeilenbreite in den Tabellen, Wird nicht gespeichert, sondern berechnet
    public float zellenbreitenFaktor = 1.0f;


    // These defaults are never used anywhere, they are read from db,
    // and are initialized from the config update routine.
    public float TRAINING_OFFSET_SCORING = 0f;
    public float TRAINING_OFFSET_WINGER = 0f;
    public float TRAINING_OFFSET_STAMINA = 0f;
    public float TRAINING_OFFSET_PASSING = 0f;
    public float TRAINING_OFFSET_PLAYMAKING = 0f;
    public float TRAINING_OFFSET_SETPIECES = 0f;
    public float TRAINING_OFFSET_GOALKEEPING = 0f;
    public float TRAINING_OFFSET_DEFENDING = 0f;
    public float TRAINING_OFFSET_OSMOSIS = 0f;
    //Faktoren
    public float TRAINING_OFFSET_AGE = 1.0f;
    public float TRAINING_OFFSET_ASSISTANTS = 1.0f;
    public float TRAINING_OFFSET_INTENSITY = 1.0f;
    //Timezone
    public int TimeZoneDifference;
    public boolean TRAINING_SHOW_SKILLDROPS = true;

    //Number of decimals
    public int nbDecimals = 2;
    public int aufstellungsAssistentPanel_reihenfolge = LineupAssistant.AW_MF_ST;
    public int lineupPanel_horizontalSplitLocation = 600;
    public int lineupPanel_verticalSplitLocation = GUIUtils.getLocationFromRight(400, 800);

    public int transferHistoryPane_splitPane = 200;
    public int transferTypePane_splitPane = 200;

    public int training_splitPane = 300;
    public int training_bottomSplitPane = 400;
    public int training_rightSplitPane = 700;
    public int training_mainSplitPane = 300;
    public int training_lowerLeftSplitPane = 200;
    public int training_pastFutureTrainingsSplitPane = 200;

    public int teamAnalyzer_SimButtonSplitPane = 667;
    public int teamAnalyzer_RatingPanelSplitPane = 143;
    public int teamAnalyzer_FilterPanelSplitPane = 237;
    public int teamAnalyzer_MainPanelSplitPane = 532;
    public int teamAnalyzer_BottomSplitPane = 500;

    public int hoMainFrame_PositionX;
    public int hoMainFrame_PositionY;
    public int hoMainFrame_height = 740;

    //------Werte----------------------------------------------------
    //HOMainFrame
    public int hoMainFrame_width = 1024;

    //MiniScout
    public int miniscout_PositionX = 50;
    public int miniscout_PositionY = 50;

    //Textgroesse
    public int fontSize = 11;
    public String skin = "Nimbus";

    //Spiele
    public int spieleFilter = 1;

    public MatchLocation matchLocation = MatchLocation.ALL;

    //Spiele
    public int spielePanel_horizontalLeftSplitPane = 400;
    public int spielePanel_horizontalRightSplitPane = 310;
    public int spielePanel_verticalSplitPane = 440;

    //SpielerAnalyse
    public int spielerAnalysePanel_horizontalSplitPane = 400;

    //Position Player Details
    public int spielerDetails_PositionX = 50;
    public int spielerDetails_PositionY = 50;
    public int futureWeeks = 16;
    public int spielerUebersichtsPanel_horizontalLeftSplitPane = 700;
    public int spielerUebersichtsPanel_horizontalRightSplitPane = GUIUtils.getLocationFromRight(300, 750);

    public int spielerUebersichtsPanel_verticalSplitPane = 400;
    public int playerTablePanel_horizontalSplitPane = 200;

    //Standardsortierung
    public int standardsortierung = SORT_BESTPOS;
    //AlleSpielerstatistik
    public int statistikAlleAnzahlHRF = 50;
    //Spielerstatistik
    public int statistikAnzahlHRF = 50;
    //Finanzstatistik
    public int statistikFinanzenAnzahlHRF = 50;
    //Club Tab in Statisctics Panel
    public int statisticsClubNbWeeks = 50;
    //Alle eigenen
    public int statistikSpieleFilter = 11;
    //SpielerFinanzenStatistikPanel
    //Wird nun für SpieleStatistik verwendet!
    public int statistikSpielerFinanzenAnzahlHRF = 50;
    //TransferScoutPanel
    public int transferScoutPanel_horizontalSplitPane = 300;
    //Id der Währung

    //veraltet!!
    public int waehrungsID = 3;

    public int simulatorMatches = 0;

    // Lineup Assistant Position Filters
    public boolean assistantSaved = false;
    public boolean assistant101 = true;
    public boolean assistant102 = true;
    public boolean assistant103 = true;
    public boolean assistant104 = true;
    public boolean assistant105 = true;
    public boolean assistant106 = true;
    public boolean assistant107 = true;
    public boolean assistant108 = true;
    public boolean assistant109 = true;
    public boolean assistant110 = true;
    public boolean assistant111 = true;
    public boolean assistant112 = true;
    public boolean assistant113 = true;

    public String theme = "Classic";

    //Lineup Rating Chart
    public boolean RatingChartFrame_Combined = false;
    public boolean RatingChartFrame_Multiple = false;
    public boolean RatingChartFrame_ET = false;
    public boolean MultipleRatingChartsPanel_Values = false;
    public boolean MultipleRatingChartsPanel_HelpLines = false;
    public boolean CombinedRatingChartPanel_LeftDefense = false;
    public boolean CombinedRatingChartPanel_CentralDefense = false;
    public boolean CombinedRatingChartPanel_RightDefense = false;
    public boolean CombinedRatingChartPanel_Midfield = false;
    public boolean CombinedRatingChartPanel_LeftAttack = false;
    public boolean CombinedRatingChartPanel_CentralAttack = false;
    public boolean CombinedRatingChartPanel_RightAttack = false;
    public boolean CombinedRatingChartPanel_HatStats = false;
    public boolean CombinedRatingChartPanel_Loddar = false;
    public boolean CombinedRatingChartPanel_Values = false;
    public boolean CombinedRatingChartPanel_HelpLines = false;

    // Promotion/Demotion test
    public boolean promotionManagerTest = false;

    //Alternative Positions
    public float alternativePositionsTolerance = .03f;

    // Youth training
    public double youthtrainingFactorPrimary = 1.0;
    public double youthtrainingFactorSecondary = 0.66;

    public int lineupOrientationSetting = GOALKEEPER_AT_TOP;
    public int lineupPositionNamesSetting = POSITIONNAMES_SHORT;
    public int lineupRatingPredictionModelSetting = RATINGPREDICTIONMODEL_NEW;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new UserParameter object.
     */
    private UserParameter() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @return singelton instance
     */
    public static UserParameter instance() {
        if (m_clUserParameter == null) {
            m_clUserParameter = new UserParameter();
        }

        return m_clUserParameter;
    }

    public static UserParameter temp() {
        if (m_clUserParameter == null)
            instance();
        if (m_clTemp == null) {
            m_clTemp = new UserParameter();
            m_clTemp.setValues(m_clUserParameter.getValues());
        }
        return m_clTemp;
    }

    public static void saveTempParameter() {
        if (m_clTemp != null) {
            m_clUserParameter.setValues(m_clTemp.getValues());
            deleteTempParameter();
        }
    }

    public static void deleteTempParameter() {
        m_clTemp = null;
    }

    @Override
    public Map<String, String> getValues() {
        Map<String, String> map = new HashMap<>();

        map.put("AccessToken", String.valueOf(AccessToken));
        map.put("TokenSecret", String.valueOf(TokenSecret));

        map.put("ProxyAuthName", String.valueOf(ProxyAuthName));
        map.put("ProxyAuthPassword", String.valueOf(ProxyAuthPassword));
        map.put("ProxyHost", String.valueOf(ProxyHost));
        map.put("ProxyPort", String.valueOf(ProxyPort));
        map.put("aufstellungsAssistentPanel_gruppe", String.valueOf(aufstellungsAssistentPanel_gruppe));
        map.put("hrfImport_HRFPath", DBManager.insertEscapeSequences(String.valueOf(hrfImport_HRFPath)));
//		map.put("htip",String.valueOf(htip));
        map.put("matchLineupImport_Path", String.valueOf(matchLineupImport_Path));
        map.put("spielPlanImport_Path", String.valueOf(spielPlanImport_Path));
        map.put("sprachDatei", String.valueOf(sprachDatei));
        map.put("skin", skin);
        map.put("ProxyAktiv", String.valueOf(ProxyAktiv));
        map.put("ProxyAuthAktiv", String.valueOf(ProxyAuthAktiv));
        map.put("aufstellungsAssistentPanel_cbfilter", String.valueOf(aufstellungsAssistentPanel_cbfilter));
        map.put("aufstellungsAssistentPanel_form", String.valueOf(aufstellungsAssistentPanel_form));
        map.put("aufstellungsAssistentPanel_gesperrt", String.valueOf(aufstellungsAssistentPanel_gesperrt));
        map.put("aufstellungsAssistentPanel_idealPosition", String.valueOf(aufstellungsAssistentPanel_idealPosition));
        map.put("lineupAssistentPanel_include_group", String.valueOf(lineupAssistentPanel_include_group));
        map.put("aufstellungsAssistentPanel_notLast", String.valueOf(aufstellungsAssistentPanel_notLast));
        map.put("aufstellungsAssistentPanel_verletzt", String.valueOf(aufstellungsAssistentPanel_verletzt));
        map.put("xmlDownload", String.valueOf(xmlDownload));

        if (ReleaseChannel == null) {
            ReleaseChannel = "Stable";
            if (HO.getVersionType() != null) {
                switch (HO.getVersionType()) {
                    case "DEV" -> ReleaseChannel = "Dev";
                    case "BETA" -> ReleaseChannel = "Beta";
                }
            }
        }
        map.put("ReleaseChannel", ReleaseChannel);

        map.put("fixtures", String.valueOf(fixtures));
        map.put("currentMatchlist", String.valueOf(downloadCurrentMatchlist));
        map.put("downloadLadderMatches", String.valueOf(downloadLadderMatches));
        map.put("downloadSingleMatches", String.valueOf(downloadSingleMatches));
        map.put("downloadTournamentGroupMatches", String.valueOf(downloadTournamentGroupMatches));
        map.put("downloadTournamentPlayoffMatches", String.valueOf(downloadTournamentPlayoffMatches));
        map.put("downloadDivisionBattleMatches", String.valueOf(downloadDivisionBattleMatches));
        map.put("showHRFSaveDialog", String.valueOf(showHRFSaveDialog));
        map.put("spieleranalyseVertikal", String.valueOf(spieleranalyseVertikal));
        map.put("statistikAlleBeschriftung", String.valueOf(statistikAlleBeschriftung));
        map.put("statistikAlleErfahrung", String.valueOf(statistikAlleErfahrung));
        map.put("statistikAlleFluegel", String.valueOf(statistikAlleFluegel));
        map.put("statistikAlleForm", String.valueOf(statistikAlleForm));
        map.put("statistikAlleFuehrung", String.valueOf(statistikAlleFuehrung));
        map.put("statistikAlleHilfslinien", String.valueOf(statistikAlleHilfslinien));
        map.put("statisticsTeamSumOrAverage", String.valueOf(statisticsTeamSumOrAverage));
        map.put("statisticsFinanceChartType", String.valueOf(statisticsFinanceChartType));
        map.put("statistikAlleKondition", String.valueOf(statistikAlleKondition));
        map.put("statistikAllLoyalty", String.valueOf(statistikAllLoyalty));
        map.put("statistikAllePasspiel", String.valueOf(statistikAllePasspiel));
        map.put("statistikAlleSpielaufbau", String.valueOf(statistikAlleSpielaufbau));
        map.put("statistikAlleStandards", String.valueOf(statistikAlleStandards));
        map.put("statistikAlleTorschuss", String.valueOf(statistikAlleTorschuss));
        map.put("statistikAlleTorwart", String.valueOf(statistikAlleTorwart));
        map.put("statistikAlleVerteidigung", String.valueOf(statistikAlleVerteidigung));
        map.put("statistikAllTSI", String.valueOf(statistikAllTSI));
        map.put("statistikAllWages", String.valueOf(statistikAllWages));
        map.put("statistikBeschriftung", String.valueOf(statistikBeschriftung));
        map.put("statistikBewertung", String.valueOf(statistikBewertung));
        map.put("statistikErfahrung", String.valueOf(statistikErfahrung));
        map.put("statistikFananzahl", String.valueOf(statistikFananzahl));
        map.put("statistikFans", String.valueOf(statistikFans));
        map.put("statistikFinanzenHilfslinien", String.valueOf(statistikFinanzenHilfslinien));
        map.put("statistikFinanzenIncludeTransfers", String.valueOf(statisticsFinanceIncludeTransfers));

        // Module Statistics -> Tab Club  ======================================================================
        map.put("statisticsClubHelpLines", String.valueOf(statisticsClubHelpLines));
        map.put("statisticsClubAssistantTrainersLevel", String.valueOf(statisticsClubAssistantTrainersLevel));
        map.put("statisticsClubFinancialDirectorsLevel", String.valueOf(statisticsClubFinancialDirectorsLevel));
        map.put("statisticsClubFormCoachsLevel", String.valueOf(statisticsClubFormCoachsLevel));
        map.put("statisticsClubDoctorsLevel", String.valueOf(statisticsClubDoctorsLevel));
        map.put("statisticsClubSpokePersonsLevel", String.valueOf(statisticsClubSpokePersonsLevel));
        map.put("statisticsClubSportPsychologistLevels", String.valueOf(statisticsClubSportPsychologistLevels));
        map.put("statisticsClubTacticalAssistantLevels", String.valueOf(statisticsClubTacticalAssistantLevels));
        map.put("statisticsClubYouthSquadLevel", String.valueOf(statisticsClubYouthSquadLevel));
        map.put("statisticsClubYouthSquadInvestment", String.valueOf(statisticsClubYouthSquadInvestment));
        map.put("statisticsClubFanClubSize", String.valueOf(statisticsClubFanClubSize));
        map.put("statisticsClubGlobalRanking", String.valueOf(statisticsClubGlobalRanking));
        map.put("statisticsClubLeagueRanking", String.valueOf(statisticsClubLeagueRanking));
        map.put("statisticsClubRegionRanking", String.valueOf(statisticsClubRegionRanking));
        map.put("statisticsClubPowerRating", String.valueOf(statisticsClubPowerRating));

        // =============================================================================================================
        map.put("statistikFluegel", String.valueOf(statistikFluegel));
        map.put("statistikForm", String.valueOf(statistikForm));
        map.put("statistikFuehrung", String.valueOf(statistikFuehrung));
        map.put("statistikGesamtAusgaben", String.valueOf(statistikGesamtAusgaben));
        map.put("statistikGesamtEinnahmen", String.valueOf(statistikGesamtEinnahmen));
        map.put("statistikGewinnVerlust", String.valueOf(statistikGewinnVerlust));
        map.put("statistikHilfslinien", String.valueOf(statistikHilfslinien));
        map.put("statistikJugend", String.valueOf(statistikJugend));
        map.put("statistikKondition", String.valueOf(statistikKondition));
        map.put("statistikLoyalty", String.valueOf(statistikLoyalty));
        map.put("statistikKontostand", String.valueOf(statistikKontostand));
        map.put("statistikMarktwert", String.valueOf(statistikMarktwert));
        map.put("statistikPasspiel", String.valueOf(statistikPasspiel));
        map.put("statistikSonstigeAusgaben", String.valueOf(statistikSonstigeAusgaben));
        map.put("statistikSonstigeEinnahmen", String.valueOf(statistikSonstigeEinnahmen));
        map.put("statistikSpielaufbau", String.valueOf(statistikSpielaufbau));
        map.put("statistikSpieleAbwehrzentrum", String.valueOf(statistikSpieleAbwehrzentrum));
        map.put("statistikSpieleAngriffszentrum", String.valueOf(statistikSpieleAngriffszentrum));
        map.put("statistikSpieleBewertung", String.valueOf(statistikSpieleBewertung));
        map.put("statistikSpieleGesamt", String.valueOf(statistikSpieleGesamt));
        map.put("statistikSpieleLinkeAbwehr", String.valueOf(statistikSpieleLinkeAbwehr));
        map.put("statistikSpieleLinkerAngriff", String.valueOf(statistikSpieleLinkerAngriff));
        map.put("statistikSpieleMittelfeld", String.valueOf(statistikSpieleMittelfeld));
        map.put("statistikSpieleRechteAbwehr", String.valueOf(statistikSpieleRechteAbwehr));
        map.put("statistikSpieleRechterAngriff", String.valueOf(statistikSpieleRechterAngriff));
        map.put("statistikSpieleSelbstvertrauen", String.valueOf(statistikSpieleSelbstvertrauen));
        map.put("statistikSpieleStimmung", String.valueOf(statistikSpieleStimmung));
        map.put("statistikSpieleHatStats", String.valueOf(statistikSpieleHatStats));
        map.put("statistikSpieleLoddarStats", String.valueOf(statistikSpieleLoddarStats));
        map.put("statistikSpielerFinanzenBeschriftung", String.valueOf(statistikSpielerFinanzenBeschriftung));
        map.put("statistikSpielerFinanzenGehalt", String.valueOf(statistikSpielerFinanzenGehalt));
        map.put("statistikSpielerFinanzenHilfslinien", String.valueOf(statistikSpielerFinanzenHilfslinien));
        map.put("statistikSpielerFinanzenMarktwert", String.valueOf(statistikSpielerFinanzenMarktwert));
        map.put("statistikSpielergehaelter", String.valueOf(statistikSpielergehaelter));
        map.put("statistikSponsoren", String.valueOf(statistikSponsoren));
        map.put("statistikStadion", String.valueOf(statistikStadion));
        map.put("statistikStandards", String.valueOf(statistikStandards));
        map.put("statistikTorschuss", String.valueOf(statistikTorschuss));
        map.put("statistikTorwart", String.valueOf(statistikTorwart));
        map.put("statistikTrainerstab", String.valueOf(statistikTrainerstab));
        map.put("statistikVerteidigung", String.valueOf(statistikVerteidigung));
        map.put("statistikZinsaufwendungen", String.valueOf(statistikZinsaufwendungen));
        map.put("statistikZinsertraege", String.valueOf(statistikZinsertraege));
        map.put("statistikZuschauer", String.valueOf(statistikZuschauer));
//		map.put("tempTabArenasizer",String.valueOf(tempTabArenasizer));
        map.put("tempTabAufstellung", String.valueOf(tempTabAufstellung));
        map.put("tempTabInformation", String.valueOf(tempTabInformation));
        map.put("tempTabLigatabelle", String.valueOf(tempTabLigatabelle));
        map.put("tempTabSpiele", String.valueOf(tempTabSpiele));
        map.put("tempTabSpieleranalyse", String.valueOf(tempTabSpieleranalyse));
        map.put("tempTabSpieleruebersicht", String.valueOf(tempTabSpieleruebersicht));
        map.put("tempTabStatistik", String.valueOf(tempTabStatistik));
        map.put("updateCheck", String.valueOf(updateCheck));
        map.put("zahlenFuerSkill", String.valueOf(zahlenFuerSkill));
        map.put("AlterFaktor", String.valueOf(TRAINING_OFFSET_AGE));
        map.put("CoTrainerFaktor", String.valueOf(TRAINING_OFFSET_ASSISTANTS));
        map.put("IntensitaetFaktor", String.valueOf(TRAINING_OFFSET_INTENSITY));
        map.put("TrainingPredictionShowSkillDrops", String.valueOf(TRAINING_SHOW_SKILLDROPS));
        map.put("TrainerFaktor", String.valueOf(TrainerFaktor));
        map.put("faktorGeld", String.valueOf(faktorGeld));
        map.put("zellenbreitenFaktor", String.valueOf(zellenbreitenFaktor));
        map.put("DAUER_CHANCENVERWERTUNG", String.valueOf(TRAINING_OFFSET_SCORING));
        map.put("DAUER_FLUEGELSPIEL", String.valueOf(TRAINING_OFFSET_WINGER));
        map.put("DAUER_KONDITION", String.valueOf(TRAINING_OFFSET_STAMINA));
        map.put("DAUER_PASSPIEL", String.valueOf(TRAINING_OFFSET_PASSING));
        map.put("DAUER_SPIELAUFBAU", String.valueOf(TRAINING_OFFSET_PLAYMAKING));
        map.put("DAUER_STANDARDS", String.valueOf(TRAINING_OFFSET_SETPIECES));
        map.put("DAUER_TORWART", String.valueOf(TRAINING_OFFSET_GOALKEEPING));
        map.put("DAUER_VERTEIDIGUNG", String.valueOf(TRAINING_OFFSET_DEFENDING));
        map.put("DAUER_OSMOSIS", String.valueOf(TRAINING_OFFSET_OSMOSIS));
        map.put("TimeZoneDifference", String.valueOf(TimeZoneDifference));
        map.put("nbDecimals", String.valueOf(nbDecimals));
        map.put("aufstellungsAssistentPanel_reihenfolge", String.valueOf(aufstellungsAssistentPanel_reihenfolge));
        map.put("lineupPanel_horizontalSplitLocation", String.valueOf(lineupPanel_horizontalSplitLocation));
        map.put("lineupPanel_verticalSplitLocation", String.valueOf(lineupPanel_verticalSplitLocation));
        map.put("hoMainFrame_PositionX", String.valueOf(hoMainFrame_PositionX));
        map.put("hoMainFrame_PositionY", String.valueOf(hoMainFrame_PositionY));
        map.put("hoMainFrame_height", String.valueOf(hoMainFrame_height));
        map.put("hoMainFrame_width", String.valueOf(hoMainFrame_width));
        map.put("miniscout_PositionX", String.valueOf(miniscout_PositionX));
        map.put("miniscout_PositionY", String.valueOf(miniscout_PositionY));
        map.put("schriftGroesse", String.valueOf(fontSize));
        map.put("spieleFilter", String.valueOf(spieleFilter));
        map.put("matchLocation", matchLocation.toString());
        map.put("spielePanel_horizontalLeftSplitPane", String.valueOf(spielePanel_horizontalLeftSplitPane));
        map.put("spielePanel_horizontalRightSplitPane", String.valueOf(spielePanel_horizontalRightSplitPane));
        map.put("spielePanel_verticalSplitPane", String.valueOf(spielePanel_verticalSplitPane));
        map.put("spielerAnalysePanel_horizontalSplitPane", String.valueOf(spielerAnalysePanel_horizontalSplitPane));
        map.put("spielerDetails_PositionX", String.valueOf(spielerDetails_PositionX));
        map.put("spielerDetails_PositionY", String.valueOf(spielerDetails_PositionY));
        map.put("futureWeeks", String.valueOf(futureWeeks));
        map.put("spielerUebersichtsPanel_horizontalLeftSplitPane", String.valueOf(spielerUebersichtsPanel_horizontalLeftSplitPane));
        map.put("spielerUebersichtsPanel_horizontalRightSplitPane", String.valueOf(spielerUebersichtsPanel_horizontalRightSplitPane));
        map.put("spielerUebersichtsPanel_verticalSplitPane", String.valueOf(spielerUebersichtsPanel_verticalSplitPane));
        map.put("standardsortierung", String.valueOf(standardsortierung));
        map.put("statistikAlleAnzahlHRF", String.valueOf(statistikAlleAnzahlHRF));
        map.put("statistikAnzahlHRF", String.valueOf(statistikAnzahlHRF));
        map.put("statistikFinanzenAnzahlHRF", String.valueOf(statistikFinanzenAnzahlHRF));
        map.put("statistikSpieleFilter", String.valueOf(statistikSpieleFilter));
        map.put("statistikSpielerFinanzenAnzahlHRF", String.valueOf(statistikSpielerFinanzenAnzahlHRF));
        map.put("transferScoutPanel_horizontalSplitPane", String.valueOf(transferScoutPanel_horizontalSplitPane));
        map.put("waehrungsID", String.valueOf(waehrungsID));
        map.put("simulatorMatches", String.valueOf(simulatorMatches));
        map.put("assistant101", String.valueOf(assistant101));
        map.put("assistant102", String.valueOf(assistant102));
        map.put("assistant103", String.valueOf(assistant103));
        map.put("assistant104", String.valueOf(assistant104));
        map.put("assistant105", String.valueOf(assistant105));
        map.put("assistant106", String.valueOf(assistant106));
        map.put("assistant107", String.valueOf(assistant107));
        map.put("assistant108", String.valueOf(assistant108));
        map.put("assistant109", String.valueOf(assistant109));
        map.put("assistant110", String.valueOf(assistant110));
        map.put("assistant111", String.valueOf(assistant111));
        map.put("assistant112", String.valueOf(assistant112));
        map.put("assistant113", String.valueOf(assistant113));
        map.put("assistantSaved", String.valueOf(assistantSaved));
        map.put("theme", String.valueOf(theme));
        map.put("transferHistoryPane_splitPane", String.valueOf(transferHistoryPane_splitPane));
        map.put("transferTypePane_splitPane", String.valueOf(transferTypePane_splitPane));
        map.put("training_splitPane", String.valueOf(training_splitPane));
        map.put("training_bottomSplitPane", String.valueOf(training_bottomSplitPane));
        map.put("training_rightSplitPane", String.valueOf(training_rightSplitPane));
        map.put("training_mainSplitPane", String.valueOf(training_mainSplitPane));
        map.put("training_lowerLeftSplitPane", String.valueOf(training_lowerLeftSplitPane));
        map.put("teamAnalyzer_SimButtonSplitPane", String.valueOf(teamAnalyzer_SimButtonSplitPane));
        map.put("teamAnalyzer_RatingPanelSplitPane", String.valueOf(teamAnalyzer_RatingPanelSplitPane));
        map.put("teamAnalyzer_FilterPanelSplitPane", String.valueOf(teamAnalyzer_FilterPanelSplitPane));
        map.put("teamAnalyzer_MainPanelSplitPane", String.valueOf(teamAnalyzer_MainPanelSplitPane));
        map.put("teamAnalyzer_BottomSplitPane", String.valueOf(teamAnalyzer_BottomSplitPane));
        map.put("training_pastFutureTrainingsSplitPane", String.valueOf(training_pastFutureTrainingsSplitPane));
        map.put("RatingChartFrame_Combined", String.valueOf(RatingChartFrame_Combined));
        map.put("RatingChartFrame_Multiple", String.valueOf(RatingChartFrame_Multiple));
        map.put("RatingChartFrame_ET", String.valueOf(RatingChartFrame_ET));
        map.put("MultipleRatingChartsPanel_Values", String.valueOf(MultipleRatingChartsPanel_Values));
        map.put("MultipleRatingChartsPanel_HelpLines", String.valueOf(MultipleRatingChartsPanel_HelpLines));
        map.put("CombinedRatingChartPanel_LeftDefense", String.valueOf(CombinedRatingChartPanel_LeftDefense));
        map.put("CombinedRatingChartPanel_CentralDefense", String.valueOf(CombinedRatingChartPanel_CentralDefense));
        map.put("CombinedRatingChartPanel_RightDefense", String.valueOf(CombinedRatingChartPanel_RightDefense));
        map.put("CombinedRatingChartPanel_Midfield", String.valueOf(CombinedRatingChartPanel_Midfield));
        map.put("CombinedRatingChartPanel_LeftAttack", String.valueOf(CombinedRatingChartPanel_LeftAttack));
        map.put("CombinedRatingChartPanel_CentralAttack", String.valueOf(CombinedRatingChartPanel_CentralAttack));
        map.put("CombinedRatingChartPanel_RightAttack", String.valueOf(CombinedRatingChartPanel_RightAttack));
        map.put("CombinedRatingChartPanel_HatStats", String.valueOf(CombinedRatingChartPanel_HatStats));
        map.put("CombinedRatingChartPanel_Loddar", String.valueOf(CombinedRatingChartPanel_Loddar));
        map.put("CombinedRatingChartPanel_Values", String.valueOf(CombinedRatingChartPanel_Values));
        map.put("CombinedRatingChartPanel_HelpLines", String.valueOf(CombinedRatingChartPanel_HelpLines));
        map.put("alternativePositionsTolerance", String.valueOf(alternativePositionsTolerance));

        map.put("promotionManagerTest", String.valueOf(promotionManagerTest));

        map.put("youthtrainingFactorPrimary", String.valueOf(this.youthtrainingFactorPrimary));
        map.put("youthtrainingFactorSecondary", String.valueOf(this.youthtrainingFactorSecondary));

        map.put("lineupOrientation", String.valueOf(this.lineupOrientationSetting));
        map.put("lineupRatingPredictionModel", String.valueOf(this.lineupRatingPredictionModelSetting));
        map.put("lineupPositionnames", String.valueOf(this.lineupPositionNamesSetting));

        return map;
    }

    @Override
    public void setValues(Map<String, String> values) {
        AccessToken = getStringValue(values, "AccessToken");
        TokenSecret = getStringValue(values, "TokenSecret");

        ProxyAuthName = getStringValue(values, "ProxyAuthName");
        ProxyAuthPassword = getStringValue(values, "ProxyAuthPassword");
        ProxyHost = getStringValue(values, "ProxyHost");
        ProxyPort = getStringValue(values, "ProxyPort");
        aufstellungsAssistentPanel_gruppe = getStringValue(values, "aufstellungsAssistentPanel_gruppe");
        hrfImport_HRFPath = DBManager.deleteEscapeSequences(getStringValue(values, "hrfImport_HRFPath"));
        matchLineupImport_Path = getStringValue(values, "matchLineupImport_Path");
        spielPlanImport_Path = getStringValue(values, "spielPlanImport_Path");
        sprachDatei = getStringValue(values, "sprachDatei");
        skin = getStringValue(values, "skin");

        ProxyAktiv = getBooleanValue(values, "ProxyAktiv");
        ProxyAuthAktiv = getBooleanValue(values, "ProxyAuthAktiv");
        aufstellungsAssistentPanel_cbfilter = getBooleanValue(values, "aufstellungsAssistentPanel_cbfilter");
        aufstellungsAssistentPanel_form = getBooleanValue(values, "aufstellungsAssistentPanel_form");
        aufstellungsAssistentPanel_gesperrt = getBooleanValue(values, "aufstellungsAssistentPanel_gesperrt");
        aufstellungsAssistentPanel_idealPosition = getBooleanValue(values, "aufstellungsAssistentPanel_idealPosition");
        lineupAssistentPanel_include_group = getBooleanValue(values, "lineupAssistentPanel_include_group");
        aufstellungsAssistentPanel_notLast = getBooleanValue(values, "aufstellungsAssistentPanel_notLast");
        aufstellungsAssistentPanel_verletzt = getBooleanValue(values, "aufstellungsAssistentPanel_verletzt");
        xmlDownload = getBooleanValue(values, "xmlDownload");
        ReleaseChannel = getStringValue(values, "ReleaseChannel");
        fixtures = getBooleanValue(values, "fixtures");
        downloadCurrentMatchlist = getBooleanValue(values, "currentMatchlist");
        downloadSingleMatches = getBooleanValue(values, "downloadSingleMatches");
        downloadLadderMatches = getBooleanValue(values, "downloadLadderMatches");
        downloadTournamentGroupMatches = getBooleanValue(values, "downloadTournamentGroupMatches");
        downloadTournamentPlayoffMatches = getBooleanValue(values, "downloadTournamentPlayoffMatches");
        downloadDivisionBattleMatches = getBooleanValue(values, "downloadDivisionBattleMatches");
        showHRFSaveDialog = getBooleanValue(values, "showHRFSaveDialog");
        spieleranalyseVertikal = getBooleanValue(values, "spieleranalyseVertikal");
        statistikAlleBeschriftung = getBooleanValue(values, "statistikAlleBeschriftung");
        statistikAlleErfahrung = getBooleanValue(values, "statistikAlleErfahrung");
        statistikAlleFluegel = getBooleanValue(values, "statistikAlleFluegel");
        statistikAlleForm = getBooleanValue(values, "statistikAlleForm");
        statistikAlleFuehrung = getBooleanValue(values, "statistikAlleFuehrung");
        statistikAlleHilfslinien = getBooleanValue(values, "statistikAlleHilfslinien");
        statisticsTeamSumOrAverage = getIntValue(values, "statisticsTeamSumOrAverage");
        statisticsFinanceChartType = getIntValue(values, "statisticsFinanceChartType");
        statistikAlleKondition = getBooleanValue(values, "statistikAlleKondition");
        statistikAllLoyalty = getBooleanValue(values, "statistikAllLoyalty");
        statistikAllePasspiel = getBooleanValue(values, "statistikAllePasspiel");
        statistikAlleSpielaufbau = getBooleanValue(values, "statistikAlleSpielaufbau");
        statistikAlleStandards = getBooleanValue(values, "statistikAlleStandards");
        statistikAlleTorschuss = getBooleanValue(values, "statistikAlleTorschuss");
        statistikAlleTorwart = getBooleanValue(values, "statistikAlleTorwart");
        statistikAlleVerteidigung = getBooleanValue(values, "statistikAlleVerteidigung");
        statistikAllTSI = getBooleanValue(values, "statistikAllTSI");
        statistikAllWages = getBooleanValue(values, "statistikAllWages");
        statistikBeschriftung = getBooleanValue(values, "statistikBeschriftung");
        statistikBewertung = getBooleanValue(values, "statistikBewertung");
        statistikErfahrung = getBooleanValue(values, "statistikErfahrung");
        statistikFananzahl = getBooleanValue(values, "statistikFananzahl");
        statistikFans = getBooleanValue(values, "statistikFans");
        statistikFinanzenHilfslinien = getBooleanValue(values, "statistikFinanzenHilfslinien");
        statisticsFinanceIncludeTransfers = getBooleanValue(values, "statistikFinanzenIncludeTransfers");

        // Module Statistics -> Tab Club  ======================================================================
        statisticsClubHelpLines = getBooleanValue(values, "statisticsClubHelpLines");
        statisticsClubAssistantTrainersLevel = getBooleanValue(values, "statisticsClubAssistantTrainersLevel");
        statisticsClubFinancialDirectorsLevel = getBooleanValue(values, "statisticsClubFinancialDirectorsLevel");
        statisticsClubFormCoachsLevel = getBooleanValue(values, "statisticsClubFormCoachsLevel");
        statisticsClubDoctorsLevel = getBooleanValue(values, "statisticsClubDoctorsLevel");
        statisticsClubSpokePersonsLevel = getBooleanValue(values, "statisticsClubSpokePersonsLevel");
        statisticsClubSportPsychologistLevels = getBooleanValue(values, "statisticsClubSportPsychologistLevels");
        statisticsClubTacticalAssistantLevels = getBooleanValue(values, "statisticsClubTacticalAssistantLevels");
        statisticsClubYouthSquadLevel = getBooleanValue(values, "statisticsClubYouthSquadLevel");
        statisticsClubYouthSquadInvestment = getBooleanValue(values, "statisticsClubYouthSquadInvestment");
        statisticsClubFanClubSize = getBooleanValue(values, "statisticsClubFanClubSize");
        statisticsClubGlobalRanking = getBooleanValue(values, "statisticsClubGlobalRanking");
        statisticsClubLeagueRanking = getBooleanValue(values, "statisticsClubLeagueRanking");
        statisticsClubRegionRanking = getBooleanValue(values, "statisticsClubRegionRanking");
        statisticsClubPowerRating = getBooleanValue(values, "statisticsClubPowerRating");
        // ============================================================================================================

        statistikFluegel = getBooleanValue(values, "statistikFluegel");
        statistikForm = getBooleanValue(values, "statistikForm");
        statistikFuehrung = getBooleanValue(values, "statistikFuehrung");
        statistikGesamtAusgaben = getBooleanValue(values, "statistikGesamtAusgaben");
        statistikGesamtEinnahmen = getBooleanValue(values, "statistikGesamtEinnahmen");
        statistikGewinnVerlust = getBooleanValue(values, "statistikGewinnVerlust");
        statistikHilfslinien = getBooleanValue(values, "statistikHilfslinien");
        statistikJugend = getBooleanValue(values, "statistikJugend");
        statistikKondition = getBooleanValue(values, "statistikKondition");
        statistikLoyalty = getBooleanValue(values, "statistikLoyalty");
        statistikKontostand = getBooleanValue(values, "statistikKontostand");
        statistikMarktwert = getBooleanValue(values, "statistikMarktwert");
        statistikPasspiel = getBooleanValue(values, "statistikPasspiel");
        statistikSonstigeAusgaben = getBooleanValue(values, "statistikSonstigeAusgaben");
        statistikSonstigeEinnahmen = getBooleanValue(values, "statistikSonstigeEinnahmen");
        statistikSpielaufbau = getBooleanValue(values, "statistikSpielaufbau");
        statistikSpieleAbwehrzentrum = getBooleanValue(values, "statistikSpieleAbwehrzentrum");
        statistikSpieleAngriffszentrum = getBooleanValue(values, "statistikSpieleAngriffszentrum");
        statistikSpieleBewertung = getBooleanValue(values, "statistikSpieleBewertung");
        statistikSpieleGesamt = getBooleanValue(values, "statistikSpieleGesamt");
        statistikSpieleLinkeAbwehr = getBooleanValue(values, "statistikSpieleLinkeAbwehr");
        statistikSpieleLinkerAngriff = getBooleanValue(values, "statistikSpieleLinkerAngriff");
        statistikSpieleMittelfeld = getBooleanValue(values, "statistikSpieleMittelfeld");
        statistikSpieleRechteAbwehr = getBooleanValue(values, "statistikSpieleRechteAbwehr");
        statistikSpieleRechterAngriff = getBooleanValue(values, "statistikSpieleRechterAngriff");
        statistikSpieleSelbstvertrauen = getBooleanValue(values, "statistikSpieleSelbstvertrauen");
        statistikSpieleStimmung = getBooleanValue(values, "statistikSpieleStimmung");
        statistikSpieleHatStats = getBooleanValue(values, "statistikSpieleHatStats");
        statistikSpieleLoddarStats = getBooleanValue(values, "statistikSpieleLoddarStats");

        statistikSpielerFinanzenBeschriftung = getBooleanValue(values, "statistikSpielerFinanzenBeschriftung");
        statistikSpielerFinanzenGehalt = getBooleanValue(values, "statistikSpielerFinanzenGehalt");
        statistikSpielerFinanzenHilfslinien = getBooleanValue(values, "statistikSpielerFinanzenHilfslinien");
        statistikSpielerFinanzenMarktwert = getBooleanValue(values, "statistikSpielerFinanzenMarktwert");
        statistikSpielergehaelter = getBooleanValue(values, "statistikSpielergehaelter");
        statistikSponsoren = getBooleanValue(values, "statistikSponsoren");
        statistikStadion = getBooleanValue(values, "statistikStadion");
        statistikStandards = getBooleanValue(values, "statistikStandards");
        statistikTorschuss = getBooleanValue(values, "statistikTorschuss");
        statistikTorwart = getBooleanValue(values, "statistikTorwart");
        statistikTrainerstab = getBooleanValue(values, "statistikTrainerstab");
        statistikVerteidigung = getBooleanValue(values, "statistikVerteidigung");
        statistikZinsaufwendungen = getBooleanValue(values, "statistikZinsaufwendungen");
        statistikZinsertraege = getBooleanValue(values, "statistikZinsertraege");
        statistikZuschauer = getBooleanValue(values, "statistikZuschauer");
        tempTabAufstellung = getBooleanValue(values, "tempTabAufstellung");
        tempTabInformation = getBooleanValue(values, "tempTabInformation");
        tempTabLigatabelle = getBooleanValue(values, "tempTabLigatabelle");
        tempTabSpiele = getBooleanValue(values, "tempTabSpiele");
        tempTabSpieleranalyse = getBooleanValue(values, "tempTabSpieleranalyse");
        tempTabSpieleruebersicht = getBooleanValue(values, "tempTabSpieleruebersicht");
        tempTabStatistik = getBooleanValue(values, "tempTabStatistik");
        updateCheck = getBooleanValue(values, "updateCheck");
        zahlenFuerSkill = getBooleanValue(values, "zahlenFuerSkill");

        TRAINING_OFFSET_AGE = getFloatValue(values, "AlterFaktor");
        TRAINING_OFFSET_ASSISTANTS = getFloatValue(values, "CoTrainerFaktor");
        TRAINING_OFFSET_INTENSITY = getFloatValue(values, "IntensitaetFaktor");
        TRAINING_SHOW_SKILLDROPS = getBooleanValue(values, "TrainingPredictionShowSkillDrops");
        TrainerFaktor = getFloatValue(values, "TrainerFaktor");
        faktorGeld = getFloatValue(values, "faktorGeld");
        zellenbreitenFaktor = getFloatValue(values, "zellenbreitenFaktor");
        TRAINING_OFFSET_SCORING = getFloatValue(values, "DAUER_CHANCENVERWERTUNG");
        TRAINING_OFFSET_WINGER = getFloatValue(values, "DAUER_FLUEGELSPIEL");
        TRAINING_OFFSET_STAMINA = getFloatValue(values, "DAUER_KONDITION");
        TRAINING_OFFSET_PASSING = getFloatValue(values, "DAUER_PASSPIEL");
        TRAINING_OFFSET_PLAYMAKING = getFloatValue(values, "DAUER_SPIELAUFBAU");
        TRAINING_OFFSET_SETPIECES = getFloatValue(values, "DAUER_STANDARDS");
        TRAINING_OFFSET_GOALKEEPING = getFloatValue(values, "DAUER_TORWART");
        TRAINING_OFFSET_DEFENDING = getFloatValue(values, "DAUER_VERTEIDIGUNG");
        TRAINING_OFFSET_OSMOSIS = getFloatValue(values, "DAUER_OSMOSIS");

        TimeZoneDifference = getIntValue(values, "TimeZoneDifference");
        nbDecimals = getIntValue(values, "nbDecimals");
        aufstellungsAssistentPanel_reihenfolge = getIntValue(values, "aufstellungsAssistentPanel_reihenfolge");
        lineupPanel_horizontalSplitLocation = getIntValue(values, "lineupPanel_horizontalSplitLocation");
        lineupPanel_verticalSplitLocation = getIntValue(values, "lineupPanel_verticalSplitLocation");
        hoMainFrame_PositionX = getIntValue(values, "hoMainFrame_PositionX");
        hoMainFrame_PositionY = getIntValue(values, "hoMainFrame_PositionY");
        hoMainFrame_height = getIntValue(values, "hoMainFrame_height");
        hoMainFrame_width = getIntValue(values, "hoMainFrame_width");
        miniscout_PositionX = getIntValue(values, "miniscout_PositionX");
        miniscout_PositionY = getIntValue(values, "miniscout_PositionY");
        fontSize = getIntValue(values, "schriftGroesse");
        spieleFilter = getIntValue(values, "spieleFilter");
        matchLocation = MatchLocation.valueOf(MatchLocation.class, values.get("matchLocation"));
        spielePanel_horizontalLeftSplitPane = getIntValue(values, "spielePanel_horizontalLeftSplitPane");
        spielePanel_horizontalRightSplitPane = getIntValue(values, "spielePanel_horizontalRightSplitPane");
        spielePanel_verticalSplitPane = getIntValue(values, "spielePanel_verticalSplitPane");
        spielerAnalysePanel_horizontalSplitPane = getIntValue(values, "spielerAnalysePanel_horizontalSplitPane");
        spielerDetails_PositionX = getIntValue(values, "spielerDetails_PositionX");
        spielerDetails_PositionY = getIntValue(values, "spielerDetails_PositionY");
        futureWeeks = getIntValue(values, "futureWeeks");
        spielerUebersichtsPanel_horizontalLeftSplitPane = getIntValue(values, "spielerUebersichtsPanel_horizontalLeftSplitPane");
        spielerUebersichtsPanel_horizontalRightSplitPane = getIntValue(values, "spielerUebersichtsPanel_horizontalRightSplitPane");
        spielerUebersichtsPanel_verticalSplitPane = getIntValue(values, "spielerUebersichtsPanel_verticalSplitPane");
        standardsortierung = getIntValue(values, "standardsortierung");
        statistikAlleAnzahlHRF = getIntValue(values, "statistikAlleAnzahlHRF");
        statistikAnzahlHRF = getIntValue(values, "statistikAnzahlHRF");
        statistikFinanzenAnzahlHRF = getIntValue(values, "statistikFinanzenAnzahlHRF");
        statistikSpieleFilter = getIntValue(values, "statistikSpieleFilter");
        statistikSpielerFinanzenAnzahlHRF = getIntValue(values, "statistikSpielerFinanzenAnzahlHRF");
        transferScoutPanel_horizontalSplitPane = getIntValue(values, "transferScoutPanel_horizontalSplitPane");
        waehrungsID = getIntValue(values, "waehrungsID");
        simulatorMatches = getIntValue(values, "simulatorMatches");

        assistant101 = getBooleanValue(values, "assistant101");
        assistant102 = getBooleanValue(values, "assistant102");
        assistant103 = getBooleanValue(values, "assistant103");
        assistant104 = getBooleanValue(values, "assistant104");
        assistant105 = getBooleanValue(values, "assistant105");
        assistant106 = getBooleanValue(values, "assistant106");
        assistant107 = getBooleanValue(values, "assistant107");
        assistant108 = getBooleanValue(values, "assistant108");
        assistant109 = getBooleanValue(values, "assistant109");
        assistant110 = getBooleanValue(values, "assistant110");
        assistant111 = getBooleanValue(values, "assistant111");
        assistant112 = getBooleanValue(values, "assistant112");
        assistant113 = getBooleanValue(values, "assistant113");
        assistantSaved = getBooleanValue(values, "assistantSaved");

        theme = getStringValue(values, "theme");
        transferHistoryPane_splitPane = getIntValue(values, "transferHistoryPane_splitPane");
        transferTypePane_splitPane = getIntValue(values, "transferTypePane_splitPane");
        training_splitPane = getIntValue(values, "training_splitPane");
        training_bottomSplitPane = getIntValue(values, "training_bottomSplitPane");
        training_rightSplitPane = getIntValue(values, "training_rightSplitPane");
        training_mainSplitPane = getIntValue(values, "training_mainSplitPane");
        training_lowerLeftSplitPane = getIntValue(values, "training_lowerLeftSplitPane");
        teamAnalyzer_SimButtonSplitPane = getIntValue(values, "teamAnalyzer_SimButtonSplitPane");
        teamAnalyzer_RatingPanelSplitPane = getIntValue(values, "teamAnalyzer_RatingPanelSplitPane");
        teamAnalyzer_FilterPanelSplitPane = getIntValue(values, "teamAnalyzer_FilterPanelSplitPane");
        teamAnalyzer_MainPanelSplitPane = getIntValue(values, "teamAnalyzer_MainPanelSplitPane");
        teamAnalyzer_BottomSplitPane = getIntValue(values, "teamAnalyzer_BottomSplitPane");
        training_pastFutureTrainingsSplitPane = getIntValue(values, "training_pastFutureTrainingsSplitPane");

        RatingChartFrame_Combined = getBooleanValue(values, "RatingChartFrame_Combined");
        RatingChartFrame_Multiple = getBooleanValue(values, "RatingChartFrame_Multiple");
        RatingChartFrame_ET = getBooleanValue(values, "RatingChartFrame_ET");
        MultipleRatingChartsPanel_Values = getBooleanValue(values, "MultipleRatingChartsPanel_Values");
        MultipleRatingChartsPanel_HelpLines = getBooleanValue(values, "MultipleRatingChartsPanel_HelpLines");
        CombinedRatingChartPanel_LeftDefense = getBooleanValue(values, "CombinedRatingChartPanel_LeftDefense");
        CombinedRatingChartPanel_CentralDefense = getBooleanValue(values, "CombinedRatingChartPanel_CentralDefense");
        CombinedRatingChartPanel_RightDefense = getBooleanValue(values, "CombinedRatingChartPanel_RightDefense");
        CombinedRatingChartPanel_Midfield = getBooleanValue(values, "CombinedRatingChartPanel_Midfield");
        CombinedRatingChartPanel_LeftAttack = getBooleanValue(values, "CombinedRatingChartPanel_LeftAttack");
        CombinedRatingChartPanel_CentralAttack = getBooleanValue(values, "CombinedRatingChartPanel_CentralAttack");
        CombinedRatingChartPanel_RightAttack = getBooleanValue(values, "CombinedRatingChartPanel_RightAttack");
        CombinedRatingChartPanel_HatStats = getBooleanValue(values, "CombinedRatingChartPanel_HatStats");
        CombinedRatingChartPanel_Loddar = getBooleanValue(values, "CombinedRatingChartPanel_Loddar");
        CombinedRatingChartPanel_Values = getBooleanValue(values, "CombinedRatingChartPanel_Values");
        CombinedRatingChartPanel_HelpLines = getBooleanValue(values, "CombinedRatingChartPanel_HelpLines");
        alternativePositionsTolerance = getFloatValue(values, "alternativePositionsTolerance");

        promotionManagerTest = getBooleanValue(values, "promotionManagerTest");

        youthtrainingFactorPrimary = getDoubleValue(values, "youthtrainingFactorPrimary", youthtrainingFactorPrimary);
        youthtrainingFactorSecondary= getDoubleValue(values, "youthtrainingFactorSecondary", youthtrainingFactorSecondary);

        lineupOrientationSetting = getIntValue(values, "lineupOrientation");
        lineupRatingPredictionModelSetting = getIntValue(values, "lineupRatingPredictionModel");
        lineupPositionNamesSetting = getIntValue(values,"lineupPositionnames" );
    }

}
