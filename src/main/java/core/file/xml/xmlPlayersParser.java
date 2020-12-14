package core.file.xml;

import java.util.List;
import java.util.Vector;

import core.model.player.YouthPlayer;
import module.training.Skills;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static core.file.xml.XMLManager.xmlAttribute2Hash;
import static core.file.xml.XMLManager.xmlValue2Hash;


public class xmlPlayersParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of xmlPlayersParser
     */
    public xmlPlayersParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /////////////////////////////////////////////////////////////////////////////////    
    //parse public
    ////////////////////////////////////////////////////////////////////////////////   
    public final Vector<MyHashtable> parsePlayersFromString(String inputStream) {
        Document doc = XMLManager.parseString(inputStream);
        return createListe(doc);
    }

    /////////////////////////////////////////////////////////////////////////////////    
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////     

    /**
     * erzeugt das Team aus dem xml
     */

    //throws Exception
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

            for (int i = 0; (list != null) && (i < list.getLength()); i++) {
                var hash = new core.file.xml.MyHashtable();

                //Root setzen
                root = (Element) list.item(i);

                //ht füllen
                hash.put("TeamID", teamID);

                ele = (Element) root.getElementsByTagName("PlayerID").item(0);
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

                //TSI löste Marktwert ab!
                ele = (Element) root.getElementsByTagName("TSI").item(0);
                hash.put("MarketValue", (XMLManager.getFirstChildNodeValue(ele)));
                ele = (Element) root.getElementsByTagName("PlayerForm").item(0);
                hash.put("PlayerForm", (XMLManager.getFirstChildNodeValue(ele)));
                ele = (Element) root.getElementsByTagName("Statement").item(0);
                hash.put("Statement", (XMLManager.getFirstChildNodeValue(ele)));
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
                    hash.put("LastMatch_id", (XMLManager.getFirstChildNodeValue(ele)));

                    ele = (Element) tmp_lm.getElementsByTagName("PositionCode").item(0);
                    hash.put("LastMatch_PositionCode", (XMLManager.getFirstChildNodeValue(ele)));
                    ele = (Element) tmp_lm.getElementsByTagName("PlayedMinutes").item(0);
                    hash.put("LastMatch_PlayedMinutes", (XMLManager.getFirstChildNodeValue(ele)));
                    ele = (Element) tmp_lm.getElementsByTagName("RatingEndOfGame").item(0);
                    hash.put("LastMatch_RatingEndOfGame", (XMLManager.getFirstChildNodeValue(ele)));

                } catch (Exception ignored) {
                }

                liste.add(hash);
            }
        } catch (Exception ignored) {
        }

        return liste;
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
            for (int i = 0; (list != null) && (i < list.getLength()); i++) {
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
                for (int c = 0; (ScoutCommentsList != null) && (c < ScoutCommentsList.getLength()); c++) {
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


}