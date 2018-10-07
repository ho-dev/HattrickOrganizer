package core.model;

import core.file.xml.XMLManager;
import core.model.player.ISpielerPosition;
import core.util.HOLogger;
import java.io.File;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FormulaFactors {
    //~ Static fields/initializers -----------------------------------------------------------------

    /** singelton */
    private static FormulaFactors m_clInstance;

    /** Konstante wieviel PositionsObjekte es gibt */
    protected static final int ANZ_FAKTOROBJEKTE = 19;

    /** Last change date */
    private static Date lastChange = new Date();
    
    //~ Instance fields ----------------------------------------------------------------------------

    ////////////////////////////////AV//////////////////////////////////////////
    // wingback
    FactorObject m_clAussenVerteidiger;
    // def wingback
    FactorObject m_clAussenVerteidiger_DEF;
    // wingback towards middle
    FactorObject m_clAussenVerteidiger_IN;
    // off wingback
    FactorObject m_clAussenVerteidiger_OFF;

    ////////////////////////////////AM//////////////////////////////////////////
    // normal winger
    FactorObject m_clFluegelspieler;
    // def winger
    FactorObject m_clFluegelspieler_DEF;
    // winger towards middle
    FactorObject m_clFluegelspieler_IN;
    // off winger
    FactorObject m_clFluegelspieler_OFF;

    ////////////////////////////////Central_DEF//////////////////////////////////////////
    // central defender
    FactorObject m_clInnenVerteidiger;
    // defender towards wing
    FactorObject m_clInnenVerteidiger_AUS;
    // offensive defender
    FactorObject m_clInnenVerteidiger_OFF;

    ////////////////////////////////MS//////////////////////////////////////////
    // normal foreward
    FactorObject m_clSturm;
    // defensive forward
    FactorObject m_clSturm_DEF;
    // forward towards wing
    FactorObject m_clSturm_AUS;

    ////////////////////////////////TW//////////////////////////////////////////
    // keeper
    FactorObject m_clTorwart;

    ////////////////////////////////ZM//////////////////////////////////////////
    // normal inner midfielder
    FactorObject m_clZentralesMittelfeld;
    // inner towards wing
    FactorObject m_clZentralesMittelfeld_AUS;
    // def inner
    FactorObject m_clZentralesMittelfeld_DEF;
    // off inner
    FactorObject m_clZentralesMittelfeld_OFF;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of FormulaFactors
     */
    private FormulaFactors() {
    	resetLastChange();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Get the singleton FormulaFactors instance.
     */
    public static FormulaFactors instance() {
        if (m_clInstance == null) {
            m_clInstance = new FormulaFactors();
            m_clInstance.importDefaults();
        }

        return m_clInstance;
    }

    /**
     * liefert Array mit allen Objekten
     */
    public FactorObject[] getAllObj() {
        final FactorObject[] allObj = new FactorObject[ANZ_FAKTOROBJEKTE];

        allObj[0] = m_clTorwart;
        allObj[1] = m_clInnenVerteidiger;
        allObj[2] = m_clInnenVerteidiger_AUS;
        allObj[3] = m_clInnenVerteidiger_OFF;
        allObj[4] = m_clAussenVerteidiger;
        allObj[5] = m_clAussenVerteidiger_OFF;
        allObj[6] = m_clAussenVerteidiger_IN;
        allObj[7] = m_clAussenVerteidiger_DEF;
        allObj[8] = m_clFluegelspieler;
        allObj[9] = m_clFluegelspieler_DEF;
        allObj[10] = m_clFluegelspieler_OFF;
        allObj[11] = m_clFluegelspieler_IN;
        allObj[12] = m_clZentralesMittelfeld;
        allObj[13] = m_clZentralesMittelfeld_OFF;
        allObj[14] = m_clZentralesMittelfeld_DEF;
        allObj[15] = m_clZentralesMittelfeld_AUS;
        allObj[16] = m_clSturm;
        allObj[17] = m_clSturm_DEF;
        allObj[18] = m_clSturm_AUS;
        return allObj;
    }

    /**
     * Import star formulas from the default XML.
     */
    public void importDefaults() {
        //vorsichtshalber vorinitialisieren.
        init();
        //eigentliche Defaults lesen
       	readFromXML("prediction/defaults.xml");
    }

    /**
     * Initialize member with 'hardcoded' default values.
     * Usually these values should never be used, as we read the default.xml afterwards.
     */
    public void init() {
        //                                     position,									tw,   sa,   ps,   fl,   vt,   ts,   std
        m_clTorwart = new FactorObject(ISpielerPosition.KEEPER,		      		 		10.0f,0.0f, 0.0f, 0.0f, 2.6f, 0.0f, 0.0f);
        m_clInnenVerteidiger = new FactorObject(ISpielerPosition.CENTRAL_DEFENDER,			0.0f, 3.0f, 0.5f, 0.0f, 9.0f, 0.0f, 0.0f);
        m_clInnenVerteidiger_AUS = new FactorObject(ISpielerPosition.CENTRAL_DEFENDER_TOWING,	0.0f, 1.5f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        m_clInnenVerteidiger_OFF = new FactorObject(ISpielerPosition.CENTRAL_DEFENDER_OFF,	0.0f, 5.0f, 0.5f, 0.0f, 6.0f, 0.0f, 0.0f);
        m_clAussenVerteidiger_IN = new FactorObject(ISpielerPosition.BACK_TOMID,	0.0f, 1.0f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        m_clAussenVerteidiger_OFF = new FactorObject(ISpielerPosition.BACK_OFF,0.0f, 1.5f, 1.5f, 4.0f, 6.0f, 0.0f, 0.0f);
        m_clAussenVerteidiger_DEF = new FactorObject(ISpielerPosition.BACK_DEF,0.0f, 0.5f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        m_clAussenVerteidiger = new FactorObject(ISpielerPosition.BACK,		0.0f, 1.0f, 0.0f, 3.5f, 8.0f, 0.0f, 0.0f);
        m_clFluegelspieler_OFF = new FactorObject(ISpielerPosition.WINGER_OFF,		0.0f, 3.0f, 2.5f, 7.0f, 1.0f, 0.0f, 0.0f);
        m_clFluegelspieler_DEF = new FactorObject(ISpielerPosition.WINGER_DEF,		0.0f, 3.5f, 2.0f, 5.0f, 4.0f, 0.0f, 0.0f);
        m_clFluegelspieler_IN = new FactorObject(ISpielerPosition.WINGER_TOMID,			0.0f, 6.0f, 2.0f, 4.0f, 2.0f, 0.0f, 0.0f);
        m_clFluegelspieler = new FactorObject(ISpielerPosition.WINGER,				0.0f, 3.5f, 2.5f, 7.0f, 1.5f, 0.0f, 0.0f);
        m_clZentralesMittelfeld_OFF = new FactorObject(ISpielerPosition.MIDFIELDER_OFF,		0.0f, 8.0f, 3.5f, 0.0f, 2.0f, 0.0f, 0.0f);
        m_clZentralesMittelfeld_DEF = new FactorObject(ISpielerPosition.MIDFIELDER_DEF,		0.0f, 8.0f, 2.0f, 0.0f, 3.5f, 0.0f, 0.0f);
	    m_clZentralesMittelfeld_AUS = new FactorObject(ISpielerPosition.MIDFIELDER_TOWING,		0.0f, 6.0f, 2.0f, 5.0f, 2.0f, 0.0f, 0.0f);
        m_clZentralesMittelfeld = new FactorObject(ISpielerPosition.MIDFIELDER,				0.0f, 8.0f, 3.0f, 0.0f, 3.0f, 0.0f, 0.0f);
        m_clSturm = new FactorObject(ISpielerPosition.FORWARD,								0.0f, 0.0f, 3.0f, 1.5f, 0.0f, 9.0f, 0.0f);
        m_clSturm_DEF = new FactorObject(ISpielerPosition.FORWARD_DEF,						0.0f, 5.0f, 3.0f, 0.0f, 0.0f, 6.0f, 0.0f);
        m_clSturm_AUS = new FactorObject(ISpielerPosition.FORWARD_TOWING,						0.0f, 0.0f, 3.0f, 4.0f, 0.0f, 6.5f, 0.0f);
    }

    /**
     * Read an XML file with star formula configurations.
     *
     * @param dateiname the filename of the xml config
     */
    public void readFromXML(String defaults) {
		Document doc = null;

		if (new File(defaults).exists()) {
			doc = XMLManager.parseFile(defaults);
		} else {
			HOLogger.instance().debug(getClass(), "File " + defaults + " not found");
			try {
				final ClassLoader loader = getClass().getClassLoader();
				doc = XMLManager.parseFile(loader.getResourceAsStream(defaults));
			} catch (Exception e) {
				HOLogger.instance().debug(getClass(), "Error loading " + defaults + " as resource: " + e);
			}
		}

		if (doc == null) {
			return;
		}

        //Tabelle erstellen
    	final Element root = doc.getDocumentElement();

        try {
            //Daten füllen
            m_clTorwart = readObject("KEEPER", root);
            m_clInnenVerteidiger = readObject("DEFENSE", root);
            m_clInnenVerteidiger_OFF = readObject("DEFENSE_O", root);
            m_clInnenVerteidiger_AUS = readObject("DEFENSE_W", root);
            m_clAussenVerteidiger = readObject("WB", root);
            m_clAussenVerteidiger_DEF = readObject("WB_D", root);
            m_clAussenVerteidiger_OFF = readObject("WB_O", root);
            m_clAussenVerteidiger_IN = readObject("WB_M", root);
            m_clZentralesMittelfeld_DEF = readObject("MD_D", root);
            m_clZentralesMittelfeld = readObject("MD", root);
            m_clZentralesMittelfeld_OFF = readObject("MD_O", root);
            m_clZentralesMittelfeld_AUS = readObject("MD_W", root);
            m_clFluegelspieler_IN = readObject("WING_M", root);
            m_clFluegelspieler_OFF = readObject("WING_O", root);
            m_clFluegelspieler_DEF = readObject("WING_D", root);
            m_clFluegelspieler = readObject("WING", root);
            m_clSturm_DEF = readObject("FW_D", root);
            m_clSturm = readObject("FW", root);
            m_clSturm_AUS = readObject("FW_W", root);
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"FormulaFactor.redxmlException gefangen: " + e);
            HOLogger.instance().log(getClass(),e);
        }
        resetLastChange();
    }

    /**
     * Read the single skill contributions for a position.
     *
     * @param tagname tag name for a position
     * @param root the XML root element
     *
     * @return the created FactorObject
     */
    public FactorObject readObject(String tagname, Element root) {
        Element ele = null;
        final FactorObject factorObject = new FactorObject(null);
        try {
            root = (Element) root.getElementsByTagName(tagname).item(0);

            //Daten füllen
            ele = (Element) root.getElementsByTagName("Position").item(0);
            factorObject.setPosition(Byte.parseByte(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("defense").item(0);
            factorObject.setVerteidigung(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("passing").item(0);
            factorObject.setPasspiel(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("playmaking").item(0);
            factorObject.setSpielaufbau(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("scoring").item(0);
            factorObject.setTorschuss(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("wing").item(0);
            factorObject.setFluegelspiel(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("keeper").item(0);
            factorObject.setTorwart(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
            ele = (Element) root.getElementsByTagName("standard").item(0);
            factorObject.setStandards(Float.parseFloat(XMLManager.getFirstChildNodeValue(ele)));
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"FormulaFactor.redxmlException gefangen: " + e);
            HOLogger.instance().log(getClass(),e);
        }

        return factorObject;
    }

    /**
     * gesaved
     */
    public void save() {
        final FactorObject[] allFaktoren = getAllObj();

        for (int i = 0; (allFaktoren != null) && (i < allFaktoren.length); i++) {
            core.db.DBManager.instance().setFaktorenFromDB(allFaktoren[i]);
        }
    }

    /**
     * Write the currently configured values into a specified file.
     */
    public void write2XML(String filename) {
        try {
            Document doc = null;
            //Element ele = null;
            Element tmpEle = null;
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            doc = builder.newDocument();
            tmpEle = doc.createElement("FormulaFactors");
            doc.appendChild(tmpEle);

            //Objekte schreiben
            writeFaktorObj(doc, m_clTorwart, tmpEle, "KEEPER");
            writeFaktorObj(doc, m_clInnenVerteidiger, tmpEle, "DEFENSE");
            writeFaktorObj(doc, m_clInnenVerteidiger_OFF, tmpEle, "DEFENSE_O");
            writeFaktorObj(doc, m_clInnenVerteidiger_AUS, tmpEle, "DEFENSE_W");
            writeFaktorObj(doc, m_clAussenVerteidiger, tmpEle, "WB");
            writeFaktorObj(doc, m_clAussenVerteidiger_DEF, tmpEle, "WB_D");
            writeFaktorObj(doc, m_clAussenVerteidiger_OFF, tmpEle, "WB_O");
            writeFaktorObj(doc, m_clAussenVerteidiger_IN, tmpEle, "WB_M");
            writeFaktorObj(doc, m_clZentralesMittelfeld_DEF, tmpEle, "MD_D");
            writeFaktorObj(doc, m_clZentralesMittelfeld, tmpEle, "MD");
            writeFaktorObj(doc, m_clZentralesMittelfeld_OFF, tmpEle, "MD_O");
            writeFaktorObj(doc, m_clZentralesMittelfeld_AUS, tmpEle, "MD_W");
            writeFaktorObj(doc, m_clFluegelspieler_IN, tmpEle, "WING_M");
            writeFaktorObj(doc, m_clFluegelspieler_OFF, tmpEle, "WING_O");
            writeFaktorObj(doc, m_clFluegelspieler_DEF, tmpEle, "WING_D");
            writeFaktorObj(doc, m_clFluegelspieler, tmpEle, "WING");
            writeFaktorObj(doc, m_clSturm_DEF, tmpEle, "FW_D");
			writeFaktorObj(doc, m_clSturm_AUS, tmpEle, "FW_W");
            writeFaktorObj(doc, m_clSturm, tmpEle, "FW");

            //doc.appendChild ( ele );
            XMLManager.writeXML(doc, filename);
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),"XMLManager.writeXML: " + e);
            HOLogger.instance().log(getClass(),e);
        }
    }

    /**
     * Add data for a single position to the XML tree.
     */
    protected void writeFaktorObj(Document doc, FactorObject obj, Element root, String tagName) {
        Element ele = null;
        Element tmpEle = null;

        tmpEle = doc.createElement(tagName);
        root.appendChild(tmpEle);
        ele = doc.createElement("Position");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getPosition()));

        //Faktoren
        ele = doc.createElement("keeper");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getTorwart()));
        ele = doc.createElement("defense");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getVerteidigung()));
        ele = doc.createElement("passing");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getPasspiel()));
        ele = doc.createElement("playmaking");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getSpielaufbau()));
        ele = doc.createElement("scoring");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getTorschuss()));
        ele = doc.createElement("wing");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getFluegelspiel()));
        ele = doc.createElement("standard");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getStandards()));
    }

    /**
     * Return a FactorObject for hoPosition
     *
     * @author Thorsten Dietz
     * @param playerPosition
     * @return
     */
    public FactorObject getPositionFactor(byte playerPosition){

    	switch (playerPosition) {
	        case ISpielerPosition.KEEPER: 					return m_clTorwart;
	        case ISpielerPosition.CENTRAL_DEFENDER:        	return m_clInnenVerteidiger;
	        case ISpielerPosition.CENTRAL_DEFENDER_OFF:    	return m_clInnenVerteidiger_OFF;
	        case ISpielerPosition.CENTRAL_DEFENDER_TOWING:		return m_clInnenVerteidiger_AUS;
	        case ISpielerPosition.BACK_OFF:   	return m_clAussenVerteidiger_OFF;
	        case ISpielerPosition.BACK_DEF:   	return m_clAussenVerteidiger_DEF;
	        case ISpielerPosition.BACK_TOMID:    	return m_clAussenVerteidiger_IN;
	        case ISpielerPosition.BACK:       	return m_clAussenVerteidiger;
	        case ISpielerPosition.MIDFIELDER_DEF:        	return m_clZentralesMittelfeld_DEF;
	        case ISpielerPosition.MIDFIELDER_OFF:        	return m_clZentralesMittelfeld_OFF;
	        case ISpielerPosition.MIDFIELDER_TOWING:        	return m_clZentralesMittelfeld_AUS;
	        case ISpielerPosition.MIDFIELDER:	        	return m_clZentralesMittelfeld;
	        case ISpielerPosition.WINGER_DEF:        	return m_clFluegelspieler_DEF;
	        case ISpielerPosition.WINGER_OFF:			return m_clFluegelspieler_OFF;
	        case ISpielerPosition.WINGER_TOMID:			return m_clFluegelspieler_IN;
	        case ISpielerPosition.WINGER:				return m_clFluegelspieler;
	        case ISpielerPosition.FORWARD_DEF:				return m_clSturm_DEF;
	        case ISpielerPosition.FORWARD:					return m_clSturm;
	        case ISpielerPosition.FORWARD_TOWING:				return m_clSturm_AUS;
	        case ISpielerPosition.COACH:
        default:
            return null;
    	}
    }

    /**
     * set factorObject for a hoPosition
     *
     * @author Thorsten Dietz
     * @param hoPosition
     * @param factorObject
     */
    public void setPositionFactor(byte pos, FactorObject factorObject){
    	switch(pos){
        case ISpielerPosition.KEEPER:
            m_clTorwart = factorObject;
            break;

        case ISpielerPosition.CENTRAL_DEFENDER:
            m_clInnenVerteidiger = factorObject;
            break;

        case ISpielerPosition.CENTRAL_DEFENDER_OFF:
            m_clInnenVerteidiger_OFF = factorObject;
            break;

        case ISpielerPosition.CENTRAL_DEFENDER_TOWING:
            m_clInnenVerteidiger_AUS = factorObject;
            break;

        case ISpielerPosition.BACK:
            m_clAussenVerteidiger = factorObject;
            break;

        case ISpielerPosition.BACK_OFF:
            m_clAussenVerteidiger_OFF = factorObject;
            break;

        case ISpielerPosition.BACK_DEF:
            m_clAussenVerteidiger_DEF = factorObject;
            break;

        case ISpielerPosition.BACK_TOMID:
            m_clAussenVerteidiger_IN = factorObject;
            break;

        case ISpielerPosition.MIDFIELDER:
            m_clZentralesMittelfeld = factorObject;
            break;

        case ISpielerPosition.MIDFIELDER_OFF:
            m_clZentralesMittelfeld_OFF = factorObject;
            break;

        case ISpielerPosition.MIDFIELDER_DEF:
            m_clZentralesMittelfeld_DEF = factorObject;
            break;

        case ISpielerPosition.MIDFIELDER_TOWING:
            m_clZentralesMittelfeld_AUS = factorObject;
            break;

        case ISpielerPosition.WINGER:
            m_clFluegelspieler = factorObject;
            break;

        case ISpielerPosition.WINGER_OFF:
            m_clFluegelspieler_OFF = factorObject;
            break;

        case ISpielerPosition.WINGER_DEF:
            m_clFluegelspieler_DEF = factorObject;
            break;

        case ISpielerPosition.WINGER_TOMID:
            m_clFluegelspieler_IN = factorObject;
            break;

        case ISpielerPosition.FORWARD:
            m_clSturm = factorObject;
            break;

        case ISpielerPosition.FORWARD_DEF:
            m_clSturm_DEF = factorObject;
            break;

		case ISpielerPosition.FORWARD_TOWING:
			m_clSturm_AUS = factorObject;
			break;
    	}
    	resetLastChange();
    }

    /**
     * Get last change date
     * 
     * @return last change date
     */
	public static Date getLastChange() {
		return lastChange;
	}

	/**
	 * Reset last change date to now
	 */
	public static void resetLastChange() {
//		System.out.println ("Resetting last change date in FormulaFactors");
		lastChange = new Date();
	}
}
