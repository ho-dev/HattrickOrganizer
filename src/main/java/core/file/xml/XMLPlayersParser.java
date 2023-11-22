package core.file.xml;

import java.util.List;
import java.util.Vector;

import core.db.user.UserManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.model.player.PlayerCategory;
import core.model.player.TrainerType;
import module.youth.YouthPlayer;
import module.training.Skills;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import core.db.DBManager;

import static core.file.xml.XMLManager.*;


public class XMLPlayersParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of xmlPlayersParser
     */
    public XMLPlayersParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /////////////////////////////////////////////////////////////////////////////////
    //parse public
    ////////////////////////////////////////////////////////////////////////////////
    public final Vector<MyHashtable> parsePlayersFromString(String inputStream) {
        Document doc = XMLManager.parseString(inputStream);
        return createListe(doc);
    }

    public final MyHashtable parsePlayerDetails(String inputStream){
        Document doc = XMLManager.parseString(inputStream);
        Element root = doc.getDocumentElement();
        Element ele = (Element) root.getElementsByTagName("Player").item(0);
        return createPlayerDetails(ele);
    }

    /////////////////////////////////////////////////////////////////////////////////
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * erzeugt das Team aus dem xml
     */
    protected final Vector<MyHashtable> createListe(Document doc) {
        final Vector<MyHashtable> liste = new Vector<>();

        try {
            var root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Team").item(0);
            var ele = (Element) root.getElementsByTagName("TeamID").item(0);
            var teamID = XMLManager.getFirstChildNodeValue(ele);
            root = (Element) root.getElementsByTagName("PlayerList").item(0);

            //Einträge adden
            var list = root.getElementsByTagName("Player");

            for (int i = 0; i < list.getLength(); i++) {
                //Root setzen
                root = (Element) list.item(i);
                var hash = createPlayer(root, teamID);

                liste.add(hash);
            }
        } catch (Exception ignored) {
        }

        return liste;
    }

    public static MyHashtable createPlayerDetails(Element root) {

        var owningTeam = (Element)root.getElementsByTagName("OwningTeam").item(0);
        var teamID = xmlValue(owningTeam, "TeamID");
        var teamName = xmlValue(owningTeam, "TeamName");

        var hash = createPlayer(root, teamID);
        hash.put("TeamName", teamName);
        xmlValue2Hash(hash, root, "OwnerNotes");
        xmlValue2Hash(hash, root, "Statement");
        xmlValue2Hash(hash, root, "NativeCountryID", "CountryID");
        xmlValue2Hash(hash, root, "NativeLeagueID");
        xmlValue2Hash(hash, root, "NativeLeagueName");
        xmlValue2Hash(hash, root, "NationalTeamName");

        var transferDetails = (Element)root.getElementsByTagName("TransferDetails").item(0);
        xmlValue2Hash(hash, transferDetails, "AskingPrice");
        xmlValue2Hash(hash, transferDetails, "Deadline");
        xmlValue2Hash(hash, transferDetails, "HighestBid");

        var motherclub = (Element)root.getElementsByTagName("MotherClub").item(0);
        xmlValue2Hash(hash, motherclub, "TeamID", "MotherclubTeamID");
        xmlValue2Hash(hash, motherclub, "TeamName", "MotherclubTeam");

        return hash;
    }

    private static MyHashtable createPlayer(Element root, String teamID) {

        var hash = new MyHashtable();

        //ht füllen
        hash.put("TeamID", teamID);

        var ele = (Element) root.getElementsByTagName("PlayerID").item(0);
        hash.put("PlayerID", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("FirstName").item(0);
        hash.put("FirstName", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("NickName").item(0);
        hash.put("NickName", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("LastName").item(0);
        hash.put("LastName", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("PlayerNumber").item(0);
        hash.put("PlayerNumber", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Age").item(0);
        hash.put("Age", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("AgeDays").item(0);
        hash.put("AgeDays", (XMLManager.getFirstChildNodeValue(ele)));
        xmlValue2Hash(hash, root, "ArrivalDate");
        //TSI löste Marktwert ab!
        ele = (Element) root.getElementsByTagName("TSI").item(0);
        hash.put("MarketValue", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("PlayerForm").item(0);
        hash.put("PlayerForm", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("PlayerCategoryId").item(0);
        hash.put("PlayerCategoryId", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Experience").item(0);
        hash.put("Experience", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Loyalty").item(0);
        hash.put("Loyalty", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("MotherClubBonus").item(0);
        hash.put("MotherClubBonus", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Leadership").item(0);
        hash.put("Leadership", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Salary").item(0);
        hash.put("Salary", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Agreeability").item(0);
        hash.put("Agreeability", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Aggressiveness").item(0);
        hash.put("Aggressiveness", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Honesty").item(0);
        hash.put("Honesty", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("LeagueGoals").item(0);
        hash.put("LeagueGoals", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("CupGoals").item(0);
        hash.put("CupGoals", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("FriendliesGoals").item(0);
        hash.put("FriendliesGoals", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("CareerGoals").item(0);
        hash.put("CareerGoals", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("CareerHattricks").item(0);
        hash.put("CareerHattricks", (XMLManager.getFirstChildNodeValue(ele)));
        xmlValue2Hash(hash, root, "MatchesCurrentTeam");
        xmlValue2Hash(hash, root, "GoalsCurrentTeam");
        ele = (Element) root.getElementsByTagName("Specialty").item(0);
        hash.put("Specialty", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("TransferListed").item(0);
        hash.put("TransferListed", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("NationalTeamID").item(0);
        hash.put("NationalTeamID", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("CountryID").item(0);
        hash.put("CountryID", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Caps").item(0);
        hash.put("Caps", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("CapsU20").item(0);
        hash.put("CapsU20", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("Cards").item(0);
        hash.put("Cards", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("InjuryLevel").item(0);
        hash.put("InjuryLevel", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("StaminaSkill").item(0);
        hash.put("StaminaSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("KeeperSkill").item(0);
        hash.put("KeeperSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("PlaymakerSkill").item(0);
        hash.put("PlaymakerSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("ScorerSkill").item(0);
        hash.put("ScorerSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("PassingSkill").item(0);
        hash.put("PassingSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("WingerSkill").item(0);
        hash.put("WingerSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("DefenderSkill").item(0);
        hash.put("DefenderSkill", (XMLManager.getFirstChildNodeValue(ele)));
        ele = (Element) root.getElementsByTagName("SetPiecesSkill").item(0);
        hash.put("SetPiecesSkill", (XMLManager.getFirstChildNodeValue(ele)));

        // Coach
        NodeList trainerData = root.getElementsByTagName("TrainerData");
        if (trainerData.getLength() > 0) {
            Element tmp_Trainer = (Element) trainerData.item(0);
            ele = (Element) tmp_Trainer.getElementsByTagName("TrainerType").item(0);
            hash.put("TrainerType", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) tmp_Trainer.getElementsByTagName("TrainerSkill").item(0);
            hash.put("TrainerSkill", (XMLManager.getFirstChildNodeValue(ele)));
        }

        //LastMatch #461
        try {
            Element tmp_lm = (Element) root.getElementsByTagName("LastMatch").item(0);
            ele = (Element) tmp_lm.getElementsByTagName("Date").item(0);
            hash.put("LastMatch_Date", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) tmp_lm.getElementsByTagName("Rating").item(0);
            hash.put("LastMatch_Rating", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) tmp_lm.getElementsByTagName("MatchId").item(0);


            String lastMatchId = XMLManager.getFirstChildNodeValue(ele);
            hash.put("LastMatch_id", lastMatchId);

            // Retrieve MatchType of last match by its ID.
            MatchKurzInfo matchInfo =  DBManager.instance().getLastMatchWithMatchId(Integer.parseInt(lastMatchId));
            if (matchInfo != null) {
                hash.put("LastMatch_Type", String.valueOf(matchInfo.getMatchType().getId()));
            }

            ele = (Element) tmp_lm.getElementsByTagName("PositionCode").item(0);
            hash.put("LastMatch_PositionCode", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) tmp_lm.getElementsByTagName("PlayedMinutes").item(0);
            hash.put("LastMatch_PlayedMinutes", (XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) tmp_lm.getElementsByTagName("RatingEndOfGame").item(0);
            hash.put("LastMatch_RatingEndOfGame", (XMLManager.getFirstChildNodeValue(ele)));

        } catch (Exception ignored) {
        }

        return hash;
    }

    public List<MyHashtable> parseYouthPlayersFromString(String inputStream) {
        Document doc = XMLManager.parseString(inputStream);
        return createYouthPlayerList(doc);
    }

    private List<MyHashtable> createYouthPlayerList(Document doc) {
        final Vector<MyHashtable> ret = new Vector<>();

        try {
            var root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("PlayerList").item(0);

            // <YouthPlayer>
            var list = root.getElementsByTagName("YouthPlayer");
            for (int i = 0; i < list.getLength(); i++) {
                var hash = new core.file.xml.MyHashtable();

                root = (Element) list.item(i);

                //<YouthPlayerID>264643130</YouthPlayerID>
                xmlValue2Hash(hash, root, "YouthPlayerID");
                //<FirstName>Claudio</FirstName>
                xmlValue2Hash(hash, root, "FirstName");
                //      <NickName />
                xmlValue2Hash(hash, root, "NickName");
                //      <LastName>Dattenfeld</LastName>
                xmlValue2Hash(hash, root, "LastName");
                //      <Age>18</Age>
                xmlValue2Hash(hash, root, "Age");
                //      <AgeDays>50</AgeDays>
                xmlValue2Hash(hash, root, "AgeDays");
                //      <ArrivalDate>2020-04-18 09:11:00</ArrivalDate>
                xmlValue2Hash(hash, root, "ArrivalDate");
                //      <CanBePromotedIn>-85</CanBePromotedIn>
                xmlValue2Hash(hash, root, "CanBePromotedIn");
                //      <PlayerNumber>100</PlayerNumber>
                xmlValue2Hash(hash, root, "PlayerNumber");
                //      <Statement />
                xmlValue2Hash(hash, root, "Statement");
                //      <OwnerNotes />
                xmlValue2Hash(hash, root, "OwnerNotes");
                //      <PlayerCategoryID>0</PlayerCategoryID>
                xmlValue2Hash(hash, root, "PlayerCategoryID");
                //      <Cards>0</Cards>
                xmlValue2Hash(hash, root, "Cards");
                //      <InjuryLevel>-1</InjuryLevel>
                xmlValue2Hash(hash, root, "InjuryLevel");
                //      <Specialty>3</Specialty>
                xmlValue2Hash(hash, root, "Specialty");
                //      <CareerGoals>3</CareerGoals>
                xmlValue2Hash(hash, root, "CareerGoals");
                //      <CareerHattricks>0</CareerHattricks>
                xmlValue2Hash(hash, root, "CareerHattricks");
                //      <LeagueGoals>0</LeagueGoals>
                xmlValue2Hash(hash, root, "LeagueGoals");
                //      <FriendlyGoals>1</FriendlyGoals>
                xmlValue2Hash(hash, root, "FriendlyGoals");
                //      <OwningYouthTeam>
                //        <YouthTeamID>2325763</YouthTeamID>
                //        <YouthTeamName>Brenk Street Boys 2</YouthTeamName>
                //        <YouthTeamLeagueID>685791</YouthTeamLeagueID>
                //        <SeniorTeam>
                //          <SeniorTeamID>1242154</SeniorTeamID>
                //          <SeniorTeamName>Juventus Brenk 2</SeniorTeamName>
                //        </SeniorTeam>
                //      </OwningYouthTeam>
                //      <PlayerSkills>
                var playerSkills = (Element) root.getElementsByTagName("PlayerSkills").item(0);
                //        <KeeperSkill IsAvailable="False" IsMaxReached="False" MayUnlock="False" />
                //        <KeeperSkillMax IsAvailable="True">2</KeeperSkillMax>

                for ( var skillId : YouthPlayer.skillIds){
                    youthplayerSkills2Hash(hash,playerSkills,skillId);
                }

                //      </PlayerSkills>
                //      <ScoutCall>
                var scoutCall = (Element) root.getElementsByTagName("ScoutCall").item(0);
                //        <Scout>
                var scout = (Element)scoutCall.getElementsByTagName("Scout").item(0);
                //          <ScoutId>382876</ScoutId>
                xmlValue2Hash(hash, scout, "ScoutId");
                //          <ScoutName>Joerg Hopfen</ScoutName>
                xmlValue2Hash(hash, scout, "ScoutName");
                //        </Scout>
                //        <ScoutingRegionID>229</ScoutingRegionID>
                xmlValue2Hash(hash, scoutCall, "ScoutingRegionID");
                //        <ScoutComments>
                var ScoutComments = (Element)scoutCall.getElementsByTagName("ScoutComments").item(0);
                //          <ScoutComment>
                var ScoutCommentsList = ScoutComments.getElementsByTagName("ScoutComment");
                for (int c = 0; c < ScoutCommentsList.getLength(); c++) {
                    var ScoutComment = (Element) ScoutCommentsList.item(c);
                    var prefix = "ScoutComment"+c;
                    //            <CommentText>Wir haben einen aussichtsreichen Kandidaten zu beurteilen. Er trägt den Namen Claudio Dattenfeld und ist 16 Jahre alt.</CommentText>
                    xmlValue2Hash(hash, ScoutComment, "CommentText", prefix+"Text");
                    //            <CommentType>1</CommentType>
                    xmlValue2Hash(hash, ScoutComment, "CommentType", prefix+"Type");
                    //            <CommentVariation>1</CommentVariation>
                    xmlValue2Hash(hash, ScoutComment, "CommentVariation", prefix+"Variation");
                    //            <CommentSkillType>264643130</CommentSkillType>
                    xmlValue2Hash(hash, ScoutComment, "CommentSkillType", prefix+"SkillType");
                    //            <CommentSkillLevel>16</CommentSkillLevel>
                    xmlValue2Hash(hash, ScoutComment, "CommentSkillLevel", prefix+"SkillLevel");
                    //          </ScoutComment>
                }
                //        </ScoutComments>
                //      </ScoutCall>
                var LastMatch = (Element) root.getElementsByTagName("LastMatch").item(0);
                //      <LastMatch>
                //        <YouthMatchID>116841872</YouthMatchID>
                xmlValue2Hash(hash, LastMatch, "YouthMatchID");
                //        <Date>2020-10-29 04:10:00</Date>
                xmlValue2Hash(hash, LastMatch, "Date", "YouthMatchDate");
                //        <PositionCode>105</PositionCode>
                xmlValue2Hash(hash, LastMatch, "PositionCode");
                //        <PlayedMinutes>90</PlayedMinutes>
                xmlValue2Hash(hash, LastMatch, "PlayedMinutes");
                //        <Rating>5</Rating>
                xmlValue2Hash(hash, LastMatch, "Rating");
                //      </LastMatch>
                //    </YouthPlayer>
                ret.add(hash);
            }
        } catch (Exception ignored) {
        }

        return ret;
    }

    private void youthplayerSkills2Hash(MyHashtable hash, Element playerSkills, Skills.HTSkillID skillId) {
        //        <KeeperSkill IsAvailable="False" IsMaxReached="False" MayUnlock="False" />
        var attr = skillId.toString() + "Skill";
        xmlValue2Hash(hash, playerSkills, attr);
        xmlAttribute2Hash(hash, playerSkills, attr, "IsAvailable");
        xmlAttribute2Hash(hash, playerSkills, attr, "IsMaxReached");
        xmlAttribute2Hash(hash, playerSkills, attr, "MayUnlock");
        //        <KeeperSkillMax IsAvailable="True">2</KeeperSkillMax>
        attr += "Max";
        xmlValue2Hash(hash, playerSkills, attr);
        xmlAttribute2Hash(hash, playerSkills, attr, "IsAvailable");
        xmlAttribute2Hash(hash, playerSkills, attr, "MayUnlock");
    }

    // TODO refactor, use createPlayerDetails
    public Player parsePlayerDetailsFromString(String xml) {
        Document doc = XMLManager.parseString(xml);

        var root = doc.getDocumentElement();
        root = (Element) root.getElementsByTagName("Player").item(0);

        var owningTeam = (Element)root.getElementsByTagName("OwningTeam").item(0);
        var teamID = xmlIntValue(owningTeam, "TeamID");
        if (!UserManager.instance().getCurrentUser().isNtTeam() &&
             teamID != HOVerwaltung.instance().getModel().getBasics().getTeamId()) return null; // foreign player

        var player = new Player();
        player.setPlayerId(xmlIntValue(root, "PlayerID"));
        player.setFirstName(xmlValue(root, "FirstName"));
        player.setNickName(xmlValue(root, "NickName"));
        player.setLastName(xmlValue(root, "LastName"));
        player.setShirtNumber(xmlIntValue(root, "PlayerNumber"));
        player.setPlayerCategory(PlayerCategory.valueOf(xmlIntegerValue(root, "PlayerCategoryID")));
        player.setOwnerNotes(xmlValue(root, "OwnerNotes"));
        player.setAge(xmlIntValue(root, "Age"));
        player.setAgeDays(xmlIntValue(root, "AgeDays"));

//        NextBirthDay : DateTime
//        The aproximate Date/time of next birthday.

        player.setArrivalDate(xmlValue(root, "ArrivalDate"));
        player.setForm(xmlIntValue(root, "PlayerForm"));
        player.setTotalCards(xmlIntValue(root, "Cards"));
        player.setInjuryWeeks(xmlIntValue(root, "InjuryLevel"));
        player.setPlayerStatement(xmlValue(root, "Statement"));

//        PlayerLanguage : String
//        This container and its elements is only shown if the user has supporter
//        The language the player speaks in his Statement, if existing.
//                PlayerLanguageID : unsigned Integer
//        This container and its elements is only shown if the user has supporter
//        The languageID the player speaks in his Statement, if existing.

        player.setHonesty(xmlIntValue(root, "Agreeability"));
        player.setAggressivity(xmlIntValue(root, "Aggressiveness"));
        player.setGentleness(xmlIntValue(root, "Honesty"));
        player.setExperience(xmlIntValue(root, "Experience"));
        player.setLoyalty(xmlIntValue(root, "Loyalty"));
        player.setHomeGrown(xmlBoolValue(root, "MotherClubBonus"));
        player.setLeadership(xmlIntValue(root, "Leadership"));
        player.setSpecialty(xmlIntValue(root, "Specialty"));
        player.setNationalityId(xmlIntValue(root, "NativeCountryID"));

//                NativeLeagueID : unsigned Integer
//        LeagueID of the league where the player was born.
//                NativeLeagueName : String
//        LeagueName of the league where the player was born.

        player.setTsi(xmlIntValue(root, "TSI"));

//        -OwningTeam
//        Container for the team that owns the player.
//        TeamID : unsigned Integer
//        The globally unique TeamID of the team owning the player.
//        TeamName : String
//        The full team name of the team owning the player.
//        LeagueID : unsigned Integer
//        LeagueID for the league of the team owning the player.

        player.setWage(xmlIntValue(root, "Salary"));

//        IsAbroad : Boolean*
//                The abroad status of the player, given as an integer. true for beeing in home country, false for beeing in a team abroad. The container is empty If the player has no owner.

        player.setInternationalMatches(xmlIntValue(root, "Caps"));
        player.setU20InternationalMatches(xmlIntValue(root, "CapsU20"));
        player.setTotalGoals(xmlIntValue(root, "CareerGoals"));
        player.setHatTricks(xmlIntValue(root, "CareerHattricks"));
        player.setCurrentTeamMatches(xmlIntegerValue(root, "MatchesCurrentTeam"));
        player.setCurrentTeamGoals(xmlIntValue(root, "GoalsCurrentTeam"));
        player.setNationalTeamId(xmlIntegerValue(root, "NationalTeamID"));

//        NationalTeamName : String*
//                If the player is enrolled on a national team, this is that national team's name.
        player.setTransferListed(xmlBoolValue(root, "TransferListed")?1:0);

        var ele = (Element) root.getElementsByTagName("TrainerData").item(0);
        if ( ele != null) {
            player.setTrainerType(TrainerType.fromInt(xmlIntegerValue(ele, "TrainerType")));
            var trainerSkill = xmlIntegerValue(root, "TrainerSkill");
            if ( trainerSkill == null){
                trainerSkill=0;
            }
            player.setCoachSkill(trainerSkill);
        }

        ele = (Element) root.getElementsByTagName("MotherClub").item(0);
        if ( ele != null) {
            player.setMotherClubId(xmlIntegerValue(ele, "TeamID"));
            player.setMotherClubName(xmlValue(ele, "TeamName"));
        }

        ele = (Element) root.getElementsByTagName("PlayerSkills").item(0);
        if ( ele != null) {
            player.setStamina(xmlIntValue(ele, "StaminaSkill"));
            player.setGoalkeeperSkill(xmlIntValue(ele, "KeeperSkill"));
            player.setPlaymakingSkill(xmlIntValue(ele, "PlaymakerSkill"));
            player.setScoringSkill(xmlIntValue(ele, "ScorerSkill"));
            player.setPassingSkill(xmlIntValue(ele, "PassingSkill"));
            player.setWingerSkill(xmlIntValue(ele, "WingerSkill"));
            player.setDefendingSkill(xmlIntValue(ele, "DefenderSkill"));
            player.setSetPiecesSkill(xmlIntValue(ele, "SetPiecesSkill"));
        }

        return player;

    }
}
