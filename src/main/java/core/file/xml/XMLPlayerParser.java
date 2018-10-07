package core.file.xml;

import core.model.player.Spieler;
import core.util.HOLogger;

import java.sql.Timestamp;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLPlayerParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of XMLPlayerParser
     */
    public XMLPlayerParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /////////////////////////////////////////////////////////////////////////////////    
    //parse public
    ////////////////////////////////////////////////////////////////////////////////    
    public final Vector<Spieler> parsePlayer(String dateiname) {
        return parseSpieler(XMLManager.parseFile(dateiname));
    }

    public final Vector<Spieler> parsePlayer(java.io.File datei) {
        return parseSpieler(XMLManager.parseFile(datei));
    }

    /**
     * erzeugt einen Spieler aus dem xml
     */
    protected final Spieler createPlayer(Element ele, Timestamp fetchdate)
      throws Exception
    {
        Element tmp = null;
        final Spieler player = new Spieler();

        //player.setFetchDate ( fetchdate );
        tmp = (Element) ele.getElementsByTagName("PlayerID").item(0);
        player.setSpielerID(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
 
        tmp = (Element) ele.getElementsByTagName("FirstName").item(0);
        player.setName(tmp.getFirstChild().getNodeValue());
        tmp = (Element) ele.getElementsByTagName("LastName").item(0);
        player.setName(player.getName() + " " + tmp.getFirstChild().getNodeValue());
    
        try {
            tmp = (Element) ele.getElementsByTagName("PlayerNumber").item(0);
            player.setTrikotnummer(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        } catch (Exception e) {
            //halt kein SUpporter , egal weiter
        }

        tmp = (Element) ele.getElementsByTagName("TSI").item(0);
        player.setTSI(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("PlayerForm").item(0);
        player.setForm(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Age").item(0);
        player.setAlter(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("AgeDays").item(0);
        player.setAgeDays(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Experience").item(0);
        player.setErfahrung(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Loyalty").item(0);
        player.setLoyalty(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("MotherClubBonus").item(0);
        player.setHomeGrown(Boolean.parseBoolean(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Leadership").item(0);
        player.setFuehrung(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Specialty").item(0);
        player.setSpezialitaet(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("TransferListed").item(0);
        player.setTransferlisted(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("CountryID").item(0);
        player.setNationalitaet(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Caps").item(0);
        player.setLaenderspiele(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("CapsU20").item(0);
        player.setU20Laenderspiele(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("Cards").item(0);
        player.setGelbeKarten(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("InjuryLevel").item(0);
        player.setVerletzt(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("StaminaSkill").item(0);
        player.setKondition(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("PlaymakerSkill").item(0);
        player.setSpielaufbau(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("ScorerSkill").item(0);
        player.setTorschuss(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("PassingSkill").item(0);
        player.setPasspiel(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("WingerSkill").item(0);
        player.setFluegelspiel(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("DefenderSkill").item(0);
        player.setVerteidigung(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
        tmp = (Element) ele.getElementsByTagName("SetPiecesSkill").item(0);
        player.setStandards(Integer.parseInt(tmp.getFirstChild().getNodeValue()));

        return player;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //Helper
    //////////////////////////////////////////////////////////////////////////////      

    /**
     * parsed nen String ins DateFormat
     */
    protected final Timestamp parseDateFromString(String date) {
        try {
            //Hattrick
            final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                                                                                           java.util.Locale.GERMANY);

            return new java.sql.Timestamp(simpleFormat.parse(date).getTime());
        } catch (Exception e) {
            try {
                //Hattrick
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd",
                                                                                               java.util.Locale.GERMANY);

                return new java.sql.Timestamp(simpleFormat.parse(date).getTime());
            } catch (Exception ex) {
                HOLogger.instance().log(getClass(),ex);
            }
        }

        return null;
    }

    /////////////////////////////////////////////////////////////////////////////////    
    //Parser Helper private
    ////////////////////////////////////////////////////////////////////////////////    

    /**
     * erstellt das MAtchlineup Objekt
     */
    protected final Vector<Spieler> parseSpieler(Document doc) {
        Element ele = null;
        Element root = null;
        final Vector<Spieler> liste = new Vector<Spieler>();
        Timestamp fetchDate = null;
        NodeList list = null;

        if (doc == null) {
            return liste;
        }

        //Tabelle erstellen
        root = doc.getDocumentElement();

        try {
            //Daten füllen
            ele = (Element) root.getElementsByTagName("FetchedDate").item(0);
            fetchDate = parseDateFromString(ele.getFirstChild().getNodeValue());

            //Root wechseln(Team)
            root = (Element) root.getElementsByTagName("Team").item(0);

            //Root wechseln(Playerlist)
            root = (Element) root.getElementsByTagName("PlayerList").item(0);

            //Player auslesen
            list = root.getElementsByTagName("Player");

            for (int i = 0; (list != null) && (i < list.getLength()); i++) {
                liste.add(createPlayer((Element) list.item(i), fetchDate));
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLPlayerParser.parseSpieler Exception gefangen: " + e);
            HOLogger.instance().log(getClass(),e);
            liste.removeAllElements();
        }

        return liste;
    }
}
