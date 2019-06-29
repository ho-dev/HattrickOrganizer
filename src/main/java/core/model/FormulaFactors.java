package core.model;

import core.file.xml.XMLManager;
import core.model.player.IMatchRoleID;
import core.util.HOLogger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FormulaFactors {
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
     * singelton
     */
    private static FormulaFactors m_clInstance;

    /**
     * number of formula object to eval
     */
    protected static final int NB_FORMULA_FACTORS = 20;

    /**
     * Last change date
     */
    private static Date lastChange = new Date();

    //~ Instance fields ----------------------------------------------------------------------------

    ////////////////////////////////AV//////////////////////////////////////////
    // wingback
    FactorObject foWB;
    // def wingback
    FactorObject foWB_DEF;
    // wingback towards middle
    FactorObject foWB_TM;
    // off wingback
    FactorObject foWB_OFF;

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
    // normal forward
    FactorObject foFW;
    // defensive forward
    FactorObject foFW_DEF;
    // technical defensive forward
    FactorObject foFW_DEF_TECH;
    // forward towards wing
    FactorObject foFW_TW;

    ////////////////////////////////TW//////////////////////////////////////////
    // keeper
    FactorObject foGK;

    ////////////////////////////////ZM//////////////////////////////////////////
    // normal inner midfielder
    FactorObject foIM;
    // inner towards wing
    FactorObject foIM_TW;
    // def inner
    FactorObject foIM_DEF;
    // off inner
    FactorObject foIM_OFF;

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
        final FactorObject[] allObj = new FactorObject[NB_FORMULA_FACTORS];

        allObj[0] = foGK;
        allObj[1] = m_clInnenVerteidiger;
        allObj[2] = m_clInnenVerteidiger_AUS;
        allObj[3] = m_clInnenVerteidiger_OFF;
        allObj[4] = foWB;
        allObj[5] = foWB_OFF;
        allObj[6] = foWB_TM;
        allObj[7] = foWB_DEF;
        allObj[8] = m_clFluegelspieler;
        allObj[9] = m_clFluegelspieler_DEF;
        allObj[10] = m_clFluegelspieler_OFF;
        allObj[11] = m_clFluegelspieler_IN;
        allObj[12] = foIM;
        allObj[13] = foIM_OFF;
        allObj[14] = foIM_DEF;
        allObj[15] = foIM_TW;
        allObj[16] = foFW;
        allObj[17] = foFW_DEF;
        allObj[18] = foFW_DEF_TECH;
        allObj[19] = foFW_TW;
        return allObj;
    }

    /**
     * Import star formulas from the default XML.
     */
    public void importDefaults() {
        init();
        readFromXML("prediction" + File.separatorChar + "defaults.xml");
    }

    /**
     * Initialize member with 'hardcoded' default values.
     * Usually these values should never be used, as we read the default.xml afterwards.
     */
    public void init() {
        //                                     position,								    	 GK         PM         PS       WI        DE        SC        SP
        foGK = new FactorObject(IMatchRoleID.KEEPER, 10.0f, 0.0f, 0.0f, 0.0f, 2.6f, 0.0f, 0.0f);
        m_clInnenVerteidiger = new FactorObject(IMatchRoleID.CENTRAL_DEFENDER, 0.0f, 3.0f, 0.5f, 0.0f, 9.0f, 0.0f, 0.0f);
        m_clInnenVerteidiger_AUS = new FactorObject(IMatchRoleID.CENTRAL_DEFENDER_TOWING, 0.0f, 1.5f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        m_clInnenVerteidiger_OFF = new FactorObject(IMatchRoleID.CENTRAL_DEFENDER_OFF, 0.0f, 5.0f, 0.5f, 0.0f, 6.0f, 0.0f, 0.0f);
        foWB_TM = new FactorObject(IMatchRoleID.BACK_TOMID, 0.0f, 1.0f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        foWB_OFF = new FactorObject(IMatchRoleID.BACK_OFF, 0.0f, 1.5f, 1.5f, 4.0f, 6.0f, 0.0f, 0.0f);
        foWB_DEF = new FactorObject(IMatchRoleID.BACK_DEF, 0.0f, 0.5f, 0.5f, 2.0f, 8.5f, 0.0f, 0.0f);
        foWB = new FactorObject(IMatchRoleID.BACK, 0.0f, 1.0f, 0.0f, 3.5f, 8.0f, 0.0f, 0.0f);
        m_clFluegelspieler_OFF = new FactorObject(IMatchRoleID.WINGER_OFF, 0.0f, 3.0f, 2.5f, 7.0f, 1.0f, 0.0f, 0.0f);
        m_clFluegelspieler_DEF = new FactorObject(IMatchRoleID.WINGER_DEF, 0.0f, 3.5f, 2.0f, 5.0f, 4.0f, 0.0f, 0.0f);
        m_clFluegelspieler_IN = new FactorObject(IMatchRoleID.WINGER_TOMID, 0.0f, 6.0f, 2.0f, 4.0f, 2.0f, 0.0f, 0.0f);
        m_clFluegelspieler = new FactorObject(IMatchRoleID.WINGER, 0.0f, 3.5f, 2.5f, 7.0f, 1.5f, 0.0f, 0.0f);
        foIM_OFF = new FactorObject(IMatchRoleID.MIDFIELDER_OFF, 0.0f, 8.0f, 3.5f, 0.0f, 2.0f, 0.0f, 0.0f);
        foIM_DEF = new FactorObject(IMatchRoleID.MIDFIELDER_DEF, 0.0f, 8.0f, 2.0f, 0.0f, 3.5f, 0.0f, 0.0f);
        foIM_TW = new FactorObject(IMatchRoleID.MIDFIELDER_TOWING, 0.0f, 6.0f, 2.0f, 5.0f, 2.0f, 0.0f, 0.0f);
        foIM = new FactorObject(IMatchRoleID.MIDFIELDER, 0.0f, 8.0f, 3.0f, 0.0f, 3.0f, 0.0f, 0.0f);
        foFW = new FactorObject(IMatchRoleID.FORWARD, 0.0f, 0.0f, 3.0f, 1.5f, 0.0f, 9.0f, 0.0f);
        foFW_DEF = new FactorObject(IMatchRoleID.FORWARD_DEF, 0.0f, 5.0f, 3.0f, 0.0f, 0.0f, 6.0f, 0.0f);
        foFW_TW = new FactorObject(IMatchRoleID.FORWARD_TOWING, 0.0f, 0.0f, 3.0f, 4.0f, 0.0f, 6.5f, 0.0f);
    }

    /**
     * Read an XML file with star formula configurations.
     *
     * @param defaults the filename of the xml config
     */
    public void readFromXML(String defaults) {
        Document doc;

        try {
            File formulaFactorsFile = new File(defaults);
            if (! formulaFactorsFile.exists()) {
                try {
                    formulaFactorsFile = new File(getClass().getClassLoader().getResource("prediction/defaults.xml").getFile());
                } catch (Exception e) {
                    HOLogger.instance().error(getClass(), "Error while loading prediction/defaults.xml: " + e);
                }
            }

            doc = XMLManager.parseFile(formulaFactorsFile.getAbsolutePath());

            //Reading xml ==========================================
            final Element root = doc.getDocumentElement();

            try {
                foGK = readObject("KEEPER", root);
                m_clInnenVerteidiger = readObject("DEFENSE", root);
                m_clInnenVerteidiger_OFF = readObject("DEFENSE_O", root);
                m_clInnenVerteidiger_AUS = readObject("DEFENSE_W", root);
                foWB = readObject("WB", root);
                foWB_DEF = readObject("WB_D", root);
                foWB_OFF = readObject("WB_O", root);
                foWB_TM = readObject("WB_M", root);
                foIM_DEF = readObject("MD_D", root);
                foIM = readObject("MD", root);
                foIM_OFF = readObject("MD_O", root);
                foIM_TW = readObject("MD_W", root);
                m_clFluegelspieler_IN = readObject("WING_M", root);
                m_clFluegelspieler_OFF = readObject("WING_O", root);
                m_clFluegelspieler_DEF = readObject("WING_D", root);
                m_clFluegelspieler = readObject("WING", root);
                foFW_DEF = readObject("FW_D", root);
                foFW_DEF_TECH = readObject("FW_D_TECH", root);
                foFW = readObject("FW", root);
                foFW_TW = readObject("FW_W", root);
            }
            catch (Exception e) {
                HOLogger.instance().log(getClass(), "Error when parsing formula factors XML: " + e);
            }

        }
        catch (Exception e) {
            HOLogger.instance().error(getClass(), "Error while loading prediction/defaults.xml");
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
            writeFaktorObj(doc, foGK, tmpEle, "KEEPER");
            writeFaktorObj(doc, m_clInnenVerteidiger, tmpEle, "DEFENSE");
            writeFaktorObj(doc, m_clInnenVerteidiger_OFF, tmpEle, "DEFENSE_O");
            writeFaktorObj(doc, m_clInnenVerteidiger_AUS, tmpEle, "DEFENSE_W");
            writeFaktorObj(doc, foWB, tmpEle, "WB");
            writeFaktorObj(doc, foWB_DEF, tmpEle, "WB_D");
            writeFaktorObj(doc, foWB_OFF, tmpEle, "WB_O");
            writeFaktorObj(doc, foWB_TM, tmpEle, "WB_M");
            writeFaktorObj(doc, foIM_DEF, tmpEle, "MD_D");
            writeFaktorObj(doc, foIM, tmpEle, "MD");
            writeFaktorObj(doc, foIM_OFF, tmpEle, "MD_O");
            writeFaktorObj(doc, foIM_TW, tmpEle, "MD_W");
            writeFaktorObj(doc, m_clFluegelspieler_IN, tmpEle, "WING_M");
            writeFaktorObj(doc, m_clFluegelspieler_OFF, tmpEle, "WING_O");
            writeFaktorObj(doc, m_clFluegelspieler_DEF, tmpEle, "WING_D");
            writeFaktorObj(doc, m_clFluegelspieler, tmpEle, "WING");
            writeFaktorObj(doc, foFW_DEF, tmpEle, "FW_D");
			writeFaktorObj(doc, foFW_TW, tmpEle, "FW_W");
            writeFaktorObj(doc, foFW, tmpEle, "FW");

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
        ele.appendChild(doc.createTextNode("" + obj.getGKfactor()));
        ele = doc.createElement("defense");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getDEfactor()));
        ele = doc.createElement("passing");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getPSfactor()));
        ele = doc.createElement("playmaking");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getPMfactor()));
        ele = doc.createElement("scoring");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getSCfactor()));
        ele = doc.createElement("wing");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getWIfactor()));
        ele = doc.createElement("standard");
        tmpEle.appendChild(ele);
        ele.appendChild(doc.createTextNode("" + obj.getSPfactor()));
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
	        case IMatchRoleID.KEEPER: 				    	return foGK;
	        case IMatchRoleID.CENTRAL_DEFENDER:         	return m_clInnenVerteidiger;
	        case IMatchRoleID.CENTRAL_DEFENDER_OFF:     	return m_clInnenVerteidiger_OFF;
	        case IMatchRoleID.CENTRAL_DEFENDER_TOWING:		return m_clInnenVerteidiger_AUS;
	        case IMatchRoleID.BACK_OFF:   	                return foWB_OFF;
	        case IMatchRoleID.BACK_DEF:   	                return foWB_DEF;
	        case IMatchRoleID.BACK_TOMID:    	            return foWB_TM;
	        case IMatchRoleID.BACK:       	                return foWB;
	        case IMatchRoleID.MIDFIELDER_DEF:        	    return foIM_DEF;
	        case IMatchRoleID.MIDFIELDER_OFF:        	    return foIM_OFF;
	        case IMatchRoleID.MIDFIELDER_TOWING:        	return foIM_TW;
	        case IMatchRoleID.MIDFIELDER:	        	    return foIM;
	        case IMatchRoleID.WINGER_DEF:        	        return m_clFluegelspieler_DEF;
	        case IMatchRoleID.WINGER_OFF:			        return m_clFluegelspieler_OFF;
	        case IMatchRoleID.WINGER_TOMID:			        return m_clFluegelspieler_IN;
	        case IMatchRoleID.WINGER:				        return m_clFluegelspieler;
	        case IMatchRoleID.FORWARD_DEF:				    return foFW_DEF;
            case IMatchRoleID.FORWARD_DEF_TECH:             return foFW_DEF_TECH;
	        case IMatchRoleID.FORWARD:					    return foFW;
	        case IMatchRoleID.FORWARD_TOWING:				return foFW_TW;
	        case IMatchRoleID.COACH:
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
        case IMatchRoleID.KEEPER:
            foGK = factorObject;
            break;

        case IMatchRoleID.CENTRAL_DEFENDER:
            m_clInnenVerteidiger = factorObject;
            break;

        case IMatchRoleID.CENTRAL_DEFENDER_OFF:
            m_clInnenVerteidiger_OFF = factorObject;
            break;

        case IMatchRoleID.CENTRAL_DEFENDER_TOWING:
            m_clInnenVerteidiger_AUS = factorObject;
            break;

        case IMatchRoleID.BACK:
            foWB = factorObject;
            break;

        case IMatchRoleID.BACK_OFF:
            foWB_OFF = factorObject;
            break;

        case IMatchRoleID.BACK_DEF:
            foWB_DEF = factorObject;
            break;

        case IMatchRoleID.BACK_TOMID:
            foWB_TM = factorObject;
            break;

        case IMatchRoleID.MIDFIELDER:
            foIM = factorObject;
            break;

        case IMatchRoleID.MIDFIELDER_OFF:
            foIM_OFF = factorObject;
            break;

        case IMatchRoleID.MIDFIELDER_DEF:
            foIM_DEF = factorObject;
            break;

        case IMatchRoleID.MIDFIELDER_TOWING:
            foIM_TW = factorObject;
            break;

        case IMatchRoleID.WINGER:
            m_clFluegelspieler = factorObject;
            break;

        case IMatchRoleID.WINGER_OFF:
            m_clFluegelspieler_OFF = factorObject;
            break;

        case IMatchRoleID.WINGER_DEF:
            m_clFluegelspieler_DEF = factorObject;
            break;

        case IMatchRoleID.WINGER_TOMID:
            m_clFluegelspieler_IN = factorObject;
            break;

        case IMatchRoleID.FORWARD:
            foFW = factorObject;
            break;

        case IMatchRoleID.FORWARD_DEF:
            foFW_DEF = factorObject;
            break;

		case IMatchRoleID.FORWARD_TOWING:
			foFW_TW = factorObject;
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
