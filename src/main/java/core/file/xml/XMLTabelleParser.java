package core.file.xml;

import core.model.series.LigaTabelle;
import core.model.series.SerieTableEntry;
import core.util.HOLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLTabelleParser {
    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of xmlTabelleParser
     */
    public XMLTabelleParser() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final LigaTabelle parseTabelle(String dateiname) {
        return createTabelle(XMLManager.parseFile(dateiname));
    }

    protected final SerieTableEntry createEintrag(Element ele) {
        Element tmp = null;
        final SerieTableEntry lte = new SerieTableEntry();

        try {
            tmp = (Element) ele.getElementsByTagName("TeamID").item(0);
            lte.setTeamId(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("Position").item(0);
            lte.setPosition(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("Matches").item(0);
            lte.setAnzSpiele(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("GoalsFor").item(0);
            lte.setToreFuer(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("GoalsAgainst").item(0);
            lte.setToreGegen(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("Points").item(0);
            lte.setPunkte(Integer.parseInt(tmp.getFirstChild().getNodeValue()));
            tmp = (Element) ele.getElementsByTagName("TeamName").item(0);
            lte.setTeamName(tmp.getFirstChild().getNodeValue());
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLTabelleParser.createEintrag : " + e);
            HOLogger.instance().log(getClass(),e);
        }

        return lte;
    }

    /*
       //gucken was es so gibt :
       ele =   doc.getDocumentElement ();
    
       HOLogger.instance().log(getClass(),"Root Name: " + ele.getTagName () );
    
       HOLogger.instance().log(getClass(),"Root Attr: " + ele.getAttributes ().getLength () );
       HOLogger.instance().log(getClass(),"Root Sub Team Nodes: " + ele.getElementsByTagName ( "Team" ).getLength () );
       HOLogger.instance().log(getClass(),"Root Gesamt Nodes: " + ele.getChildNodes ().getLength () );
    
       tmpEle  =   (Element) ele.getElementsByTagName ( "LeagueName" ).item (0);
    
       HOLogger.instance().log(getClass(),"tmp Name: " + tmpEle.getTagName () );
       HOLogger.instance().log(getClass(),"tmp Namespace: " + tmpEle.getNamespaceURI () );
       HOLogger.instance().log(getClass(),"tmp first Child Value: " + tmpEle.getFirstChild ().getNodeValue () );
       HOLogger.instance().log(getClass(),"tmp first Child Value: " + tmpEle.getFirstChild ().getNodeName () );
       HOLogger.instance().log(getClass(),"tmp Anz Childs: " + tmpEle.getChildNodes ().getLength () );
       titel   =   " " + ele.getElementsByTagName ( "LeagueName" ).getLength ();
       titel   +=  " " + ele.getElementsByTagName ( "LeagueName" ).item (0).getNodeType ();
       titel   +=  " " + ele.getElementsByTagName  ( "LeagueLevelUnitName" ).item (0).getAttributes ().getLength ();
    
       HOLogger.instance().log(getClass(),"Titel: " + titel );
     */
    protected final LigaTabelle createTabelle(Document doc) {
        Element ele = null;
        Element root = null;
        final LigaTabelle tbl = new LigaTabelle();
        NodeList list = null;

        if (doc == null) {
            return tbl;
        }

        //Tabelle erstellen
        root = doc.getDocumentElement();

        try {
            //Daten füllen
            ele = (Element) root.getElementsByTagName("LeagueName").item(0);
            tbl.setLigaName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("LeagueID").item(0);
            tbl.setLigaLandId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("LeagueName").item(0);
            tbl.setLigaLandName(ele.getFirstChild().getNodeValue());
            ele = (Element) root.getElementsByTagName("LeagueLevel").item(0);
            tbl.setSpielklasse(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("MaxLevel").item(0);
            tbl.setMaxAnzahlSpielklassen(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("LeagueLevelUnitID").item(0);
            tbl.setLigaId(Integer.parseInt(ele.getFirstChild().getNodeValue()));
            ele = (Element) root.getElementsByTagName("LeagueLevelUnitName").item(0);
            tbl.setLigaLandName(ele.getFirstChild().getNodeValue());

            //Einträge adden
            list = root.getElementsByTagName("Team");

            for (int i = 0; (list != null) && (i < list.getLength()); i++) {
                tbl.addEintrag(createEintrag((Element) list.item(i)));
            }
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLTabelleParser.createTabelle : " + e);
            HOLogger.instance().log(getClass(),e);
        }

        return tbl;
    }
}
