package core.constants.player;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import java.util.LinkedHashMap;

public final class PlayerSpeciality {

    public static final int NO_SPECIALITY 	= 0;
    public static final int TECHNICAL 		= 1;
    public static final int QUICK 			= 2;
    public static final int POWERFUL 		= 3;
    public static final int UNPREDICTABLE 	= 4;
    public static final int HEAD 			= 5;
    public static final int REGAINER 		= 6;
    public static final int SUPPORT 		= 8;
    
    public static final CBItem[] ITEMS = {
    	new CBItem("", NO_SPECIALITY),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"), TECHNICAL),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"),QUICK),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"), POWERFUL),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"), UNPREDICTABLE),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"), HEAD),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"), REGAINER),
    	new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.support"), SUPPORT)
    };
    

    private static final LinkedHashMap<Integer, String> ITEMS2;
    static {
    	ITEMS2= new LinkedHashMap<>();
	    ITEMS2.put(NO_SPECIALITY, "");
	    ITEMS2.put(TECHNICAL, HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"));
	    ITEMS2.put(QUICK, HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"));
	    ITEMS2.put(POWERFUL, HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"));
	    ITEMS2.put(UNPREDICTABLE, HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"));
	    ITEMS2.put(HEAD, HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"));
	    ITEMS2.put(REGAINER, HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"));
	    ITEMS2.put(SUPPORT, HOVerwaltung.instance().getLanguageString("ls.player.speciality.support"));
    }
    
    
	private PlayerSpeciality(){};
	
//	public static String toString(int speciality){
//		if(speciality >= NO_SPECIALITY && speciality <= SUPPORT)
//			return ITEMS[speciality].getText();
//		else
//			return HOVerwaltung.instance().getLanguageString("Unbestimmt");
//	}
	
	public static String toString(int speciality){
			return ITEMS2.getOrDefault(speciality,  HOVerwaltung.instance().getLanguageString("Unbestimmt"));
	}

	/*
	   Wetterabhängige Sonderereignisse
	   Bestimmte Spezialfähigkeiten können in Zusammenhang mit einem bestimmten Wetter
	    zu Sonderereignissen führen. Die Auswirkung dieses Sonderereignisses tritt
	    von dem Zeitpunkt in Kraft, an dem es im Spielbericht erwähnt wird,
	    und hat bis zum Spielende Einfluß auf die Leistung des Spielers.
	    Diese Auswirkung wird nach dem Spiel an der Spielerbewertung (Anzahl Sterne) sichtbar.
	   Die Torschuß- und die Spielaufbau-Fähigkeit von Ballzauberern kann sich bei Regen verschlechtern,
	    während sich die gleichen Fähigkeiten bei Sonnenschein verbessern können.
	   Bei Regen gibt es die Möglichkeit, daß sich die Torschuß-, Verteidigungs- und Spielaufbau-Fähigkeit
	    von durchsetzungsstarken Spielern verbessert.
	    Auf der anderen Seite kann sich die Torschußfähigkeit bei Sonnenschein verschlechtern.
	   Schnelle Player laufen bei Regen Gefahr, daß sich ihre Torschuß- und
	    Verteidigungsfähigkeiten verschlechtern. Bei Sonnenschein besteht das Risiko
	    , daß ihre Torschußfähigkeit unter dem Wetter leidet.
	 */
	/*
	   Liefert die mögliche Auswirkung des Wetters auf den Player
	   return 0 bei keine auswirkung
	   1 bei positiv
	   -1 bei negativ
	 */
	public static int getWeatherEffect(Weather wetter, int m_iSpezialitaet) {
	    int ret = 0;
	
	    switch (wetter) {
	        case SUNNY:
	
	            //zauberer
	            if (m_iSpezialitaet == TECHNICAL) {
	                ret = 1;
	            }
	            //durchsetz
	            else if (m_iSpezialitaet == POWERFUL) {
	                ret = -1;
	            }
	            //schnell
	            else if (m_iSpezialitaet == QUICK) {
	                ret = -1;
	            }
	
	            break;
	
	        case PARTIALLY_CLOUDY:
	        case OVERCAST:
	            break;
	
	        case RAINY:
	
	            //zauberer
	            if (m_iSpezialitaet == TECHNICAL) {
	                ret = -1;
	            }
	            //durchsetz
	            else if (m_iSpezialitaet == POWERFUL) {
	                ret = 1;
	            }
	            //schnell
	            else if (m_iSpezialitaet == QUICK) {
	                ret = -1;
	            }
	
	            break;
	    }
	
	    return ret;
	}
}
