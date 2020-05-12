package core.file.xml;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


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
        Document doc = null;

        doc = XMLManager.parseString(inputStream);

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
        final Vector<MyHashtable> liste = new Vector<MyHashtable>();
        MyHashtable hash = null;
        Element ele = null;
        Element root = null;
        NodeList list = null;

        try {
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("Team").item(0);
            root = (Element) root.getElementsByTagName("PlayerList").item(0);

            //Einträge adden
            list = root.getElementsByTagName("Player");

            for (int i = 0; (list != null) && (i < list.getLength()); i++) {
                hash = new core.file.xml.MyHashtable();

                //Root setzen
                root = (Element) list.item(i);

                //ht füllen
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

                //Trainer
                try {
                    Element tmp_Trainer = (Element) root.getElementsByTagName("TrainerData").item(0);
                    ele = (Element) tmp_Trainer.getElementsByTagName("TrainerType").item(0);
                    hash.put("TrainerType", (XMLManager.getFirstChildNodeValue(ele)));
                    ele = (Element) tmp_Trainer.getElementsByTagName("TrainerSkill").item(0);
                    hash.put("TrainerSkill", (XMLManager.getFirstChildNodeValue(ele)));
                } catch (Exception ep) {
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

                } catch (Exception ep) {
                }

                liste.add(hash);
            }
        } catch (Exception e) {
        }

        return liste;
    }
}
