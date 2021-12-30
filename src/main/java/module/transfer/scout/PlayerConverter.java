// %1329240092:de.hattrickorganizer.gui.transferscout%
package module.transfer.scout;

import core.constants.player.PlayerSpeciality;
import core.model.HOVerwaltung;
import core.util.HOLogger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Parses a player out of a text copied from HT. Tries also to give error informations but this may
 * be wrong!
 *
 * @author Marco Senn
 */
public class PlayerConverter {
    //~ Instance fields ----------------------------------------------------------------------------

    /** List of all 21 ratings for the active language */
	final private List<String> skills;
	final private List<Integer> skillvalues;
	final private List<String> specialities;
	final private List<Integer> specialitiesvalues;
	final private static Set<String> NORMALCHARS = new HashSet<String>();
    java.util.Date deadLineDate;

    public static final int SUCCESS = 0; // No error detected
    public static final int WARNING = 1; // One or some fields don't detected
    public static final int ERROR = 2; // Severe error detected
    public static final int EMPTY_INPUT_ERROR = 3; // Empty input error detected

	private int status;
    final private List<String> errorFields;
    final private List<String> notSupportedFields;
    final HOVerwaltung homodel = HOVerwaltung.instance();

	static {
		for (int m = 97; m <= 122; m++) { // a-z
			NORMALCHARS.add(new String(new char[] { (char) m }));
		}
	}

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * We prepare our skill and specialities and sort them
     */
    public PlayerConverter() {

        errorFields = new ArrayList<String>();
        notSupportedFields = new ArrayList<String>();
        status = SUCCESS;
        skills = new ArrayList<String>();
        skillvalues = new ArrayList<Integer>();
        specialities = new ArrayList<String>();
        specialitiesvalues = new ArrayList<Integer>();
        deadLineDate = new java.sql.Date(System.currentTimeMillis());

        try{
            // Get all skills for active language
            // This should be the same language as in Hattrick
            skills.add(homodel.getLanguageString("ls.player.skill.value.non-existent").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.disastrous").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.wretched").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.poor").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.weak").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.inadequate").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.passable").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.solid").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.excellent").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.formidable").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.outstanding").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.brilliant").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.magnificent").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.worldclass").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.supernatural").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.titanic").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.extra-terrestrial").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.mythical").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.magical").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.utopian").toLowerCase(java.util.Locale.ENGLISH));
            skills.add(homodel.getLanguageString("ls.player.skill.value.divine").toLowerCase(java.util.Locale.ENGLISH));

            for (int k = 0; k < skills.size(); k++) {
                skillvalues.add(k);
            }

            // Sort skills by length (shortest first)
            int p = skills.size() - 1;

            while (p > 0) {
                int k = p;

                while ((k < skills.size())
                       && (skills.get(k - 1).toString().length() > skills.get(k).toString().length())) {
                    final String t = skills.get(k - 1).toString();
                    skills.set(k - 1, skills.get(k).toString());
                    skills.set(k, t);

                    final Integer i = skillvalues.get(k - 1);
                    skillvalues.set(k - 1, skillvalues.get(k));
                    skillvalues.set(k, i);
                    k++;
                }

                p--;
            }

            // Get all specialities for active language
            // This should be the same language as in Hattrick
            specialities.add(""); // No speciality
            specialities.add(homodel.getLanguageString("ls.player.speciality.technical").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.quick").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.powerful").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.unpredictable").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.head").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.regainer").toLowerCase(java.util.Locale.ENGLISH));
            specialities.add(homodel.getLanguageString("ls.player.speciality.support").toLowerCase(java.util.Locale.ENGLISH));

            for (int i = 0; i<PlayerSpeciality.ITEMS.length; i++){
                for (int k = 0; k < 8; k++) {
                    if(PlayerSpeciality.ITEMS[i].getText().toLowerCase(java.util.Locale.ENGLISH).equals(specialities.get(k))){
                        specialitiesvalues.add(PlayerSpeciality.ITEMS[i].getId());
                        break;
                    }
                }
            }

            // Sort specialities by length (shortest first)
            p = specialities.size() - 1;

            while (p > 0) {
                int k = p;

                while ((k < specialities.size())
                       && (specialities.get(k - 1).toString().length() > specialities.get(k).toString()
                                                                                     .length())) {
                    final String t = specialities.get(k - 1).toString();
                    specialities.set(k - 1, specialities.get(k).toString());
                    specialities.set(k, t);

                    final Integer i = specialitiesvalues.get(k - 1);
                    specialitiesvalues.set(k - 1, specialitiesvalues.get(k));
                    specialitiesvalues.set(k, i);
                    k++;
                }

                p--;
            }
        } catch (Exception e) {
            HOLogger.instance().debug(getClass(), e);
            status = ERROR;
        }
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns possible status. If status is nonzero, there was a problem.
     *
     * @return Returns possible status
     */
    public final int getStatus() {
        return status;
    }

    /**
     * Returns possible error. If errorFiels is non empty, there was a problem.
     *
     * @return Returns possible fields error
     */
    public final List<String> getErrorFields() {
        return errorFields;
    }

    /**
     * Returns possible not supported fiels. If notSupportedFields is non empty, there was some fields don't supported.
     *
     * @return Returns possible possible not supported fiels
     */
    public final List<String> getNotSupportedFields() {
        return notSupportedFields;
    }

    /**
     * Parses the copied text from Hattrick Copy Button and returns a Player Object
     *
     * @param text the copied text from HT site
     *
     * @return Player a Player object
     *
     * @throws Exception Throws exception on some parse errors
     */
    public final Player buildHTCopyButton(String text) throws Exception {
        status = SUCCESS;

        final Player player = new Player();
        String txtTmp;

        Scanner sc = new Scanner(text);
        String row;
        List<String> rows = new ArrayList<String>();
        while (sc.hasNextLine()) {
            row = sc.nextLine();
            row = row.trim();
            if (!row.isEmpty())
                rows.add(row);
        }
        sc.close();

        // Set index rows
        int offsetIndexRowSpeciality = 0;
        if (rows.size()>11){
            offsetIndexRowSpeciality = 1;
        }
        int indexRowNamePlayerId = 0;
        int indexRowAge = 1;
        int indexRowExperience = 3;
        int indexMotherClub = 4;
        int indexRowTSI = 5;
        int indexRowSpecialty = 7;
        int indexRowWarning = 7 + offsetIndexRowSpeciality;
        int indexRowInjure = 8 + offsetIndexRowSpeciality;
        int indexRowFormStamina = 9 + offsetIndexRowSpeciality;
        int indexRowSkills = 10 + offsetIndexRowSpeciality;

        // Extract Name and PlayerId
        row = rows.get(indexRowNamePlayerId);
        sc = new Scanner(row);
        // Player Name
        sc.useDelimiter("\\[playerid=");
        txtTmp = sc.next().trim();
        if(!txtTmp.equals("")) {
            player.setPlayerName(txtTmp);
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.id"));
        }
        // Player Id
        sc.useDelimiter("\\]");
        txtTmp = sc.next().trim().substring(10);
        if(!txtTmp.equals("")) {
            player.setPlayerID(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.name"));
        }
        sc.close();
        // Age
        row = rows.get(indexRowAge);
        sc = new Scanner(row);
        sc.useDelimiter(" ");
        txtTmp = sc.next().trim();
        if(!txtTmp.equals("")) {
            player.setAge(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        }
        // Age Days
        sc.useDelimiter(" ");
        while (sc.hasNext()) {
            if (sc.hasNextInt()) {
                txtTmp = sc.next().trim();
            } else {
                sc.next();
            }
        }
        if(!txtTmp.equals("")) {
            player.setAgeDays(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("s.player.age.days"));
        }
        sc.close();
        //Analyze Player Description
        row = rows.get(indexRowExperience);
        sc = new Scanner(row.toLowerCase(java.util.Locale.ENGLISH));
        Pattern pattern = Pattern.compile(" |\\.");
        sc.useDelimiter(pattern);
        // Experience
        boolean found = false;
        while (sc.hasNext() && !found){
            for (int index=0;index<skills.size();index++) {
                if(sc.hasNext(skills.get(index))) {
                    player.setExperience(skillvalues.get(index));
                    found = true;
                    break;
                }
            }
            sc.next();
        }
        if(!found){
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
        }
        // Leadership
        found = false;
        while (sc.hasNext() && !found){
            for (int index=0;index<skills.size();index++) {
                if(sc.hasNext(skills.get(index))) {
                    player.setLeadership(skillvalues.get(index));
                    found = true;
                    break;
                }
            }
            sc.next();
        }
        if(!found){
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.leadership"));
        }

        /*
        Removed as discussed on post:
        https://github.com/akasolace/HO/pull/76#issuecomment-454571806
        // Loyalty
        found = false;
        while (sc.hasNext() && !found){
            for (int index=0;index<skills.size();index++) {
                if(sc.hasNext(skills.get(index))) {
                    player.setLoyalty(skillvalues.get(index));
                    found = true;
                    break;
                }
            }
            sc.next();
        }
        if(!found){
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.loyalty"));
        }
        sc.close();
        // Mother Club
        row = rows.get(indexMotherClub).toLowerCase(java.util.Locale.ENGLISH);
        if (!HOVerwaltung.instance().getLanguageString("ls.player.motherclub.bonus").isEmpty() && row.indexOf(HOVerwaltung.instance().getLanguageString("ls.player.motherclub.bonus").toLowerCase(java.util.Locale.ENGLISH))>=0) {
            player.setHomeGrown(true);
        }else if (!HOVerwaltung.instance().getLanguageString("ls.player.motherclub").isEmpty() && row.indexOf(HOVerwaltung.instance().getLanguageString("ls.player.motherclub").toLowerCase(java.util.Locale.ENGLISH))>=0) {
            player.setHomeGrown(true);
        }
        */

        // TSI
        row = rows.get(indexRowTSI);
        sc = new Scanner(row);
        sc.useDelimiter("");
        txtTmp = "";
        String c = "";
        while (sc.hasNext()) {
            if (sc.hasNextShort() ) {
                txtTmp = txtTmp + sc.next().trim();
            } else {
                c = sc.next();
            }
        }
        if(!txtTmp.equals("")) {
            player.setTSI(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.tsi"));
        }
        // Speciality
        row = rows.get(indexRowSpecialty).toLowerCase(java.util.Locale.ENGLISH);
        for (int index=1;index<specialities.size();index++) {
            if(row.contains(specialities.get(index))) {
                player.setSpeciality(specialitiesvalues.get(index));
                break;
            }
        }
        // Warnings
        row = rows.get(indexRowWarning);
        sc = new Scanner(row);
        sc.useDelimiter("");
        txtTmp = "";
        c = "";
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
            } else {
                c = sc.next();
                // System.out.println(c);
                if (c.equals("-")){
                    txtTmp = txtTmp + c;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setBooked(row.trim());
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.warningstatus"));
        }
        // Injure
        row = rows.get(indexRowInjure);
        sc = new Scanner(row);
        sc.useDelimiter("");
        txtTmp = "";
        c = "";
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
            } else {
                c = sc.next();
                if (c.equals("+") || c.equals("∞")){  //TODO Acciaccato ma gioca
                    txtTmp = txtTmp + c;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setInjury(Integer.parseInt(txtTmp));
        }
        //Form and Stamina
        row = rows.get(indexRowFormStamina);
        sc = new Scanner(row);
        sc.useDelimiter("");
        txtTmp = "";
        c = "";
        boolean findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setForm(Integer.parseInt(txtTmp));
        }else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.form"));
        }
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setStamina(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
        }
        //Keeper
        row = rows.get(indexRowSkills);
        sc = new Scanner(row);
        sc.useDelimiter("");
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setGoalKeeping(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        }
        //Defense
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setDefense(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        }
        //PlayMaking
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setPlayMaking(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        }
        //Wing
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setWing(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        }
        //Passing
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setPassing(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        }
        //Attack
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setAttack(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        }
        //Set Pieces
        txtTmp = "";
        c = "";
        findValue = false;
        while (sc.hasNext()) {
            if (sc.hasNextInt() ) {
                txtTmp = txtTmp + sc.next().trim();
                findValue = true;
            } else {
                c = sc.next();
                if (findValue) {
                    break;
                }
            }
        }
        if(!txtTmp.equals("")) {
            player.setSetPieces(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        }

        //Price - Price is not present in HT copy button
        /*
        txtTmp = "";
        if(!txtTmp.equals("")) {
            player.setPrice(Integer.parseInt(txtTmp));
        } else {
            addErrorField(HOVerwaltung.instance().getLanguageString("scout_price"));
        }
        */
        addNotSupportedField(HOVerwaltung.instance().getLanguageString("scout_price"));

        //Deadline - Date and Time expired is not present in HT copy button
        //setDeadline(player);
        addNotSupportedField(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));

        return player;
    }

    public final Player build(String text) throws Exception {
        Player player = null;
        if (text==null || text.isEmpty()){
            // This error to shown error for empty input
            status = EMPTY_INPUT_ERROR;
        }else{
            if(text.indexOf("[playerid=")>=0){
                player = this.buildHTCopyButton(text);
            }else {
                player = this.buildClassicPage(text);
            }
        }
        return player;
    }

    private void addErrorField(String fieldName){
        this.errorFields.add(fieldName);
        this.status = WARNING;
    }

    private void addNotSupportedField(String fieldName){
        this.notSupportedFields.add(fieldName);
    }

    /**
     * Parses the copied text and returns a Player Object
     *
     * @param text the copied text from HT site
     *
     * @return Player a Player object
     *
     * @throws Exception Throws exception on some parse errors
     */
    public final Player buildClassicPage(String text) throws Exception {
        status = 0;

        final Player player = new Player();

        // Init some helper variables
        String mytext = text;
        final List<String> lines = new ArrayList<String>();
        int p = -1;
        String tmp = "";

        // Detect linefeed
        //  \n will do for linux and windows
        //  \r is for mac
        String feed = "";

        if (text.indexOf("\n") >= 0) {
            feed = "\n";
        } else {
            if (text.indexOf("\r") >= 0) {
                feed = "\r";
            }
        }

        // If we detected some possible player
        if (!feed.equals("")) {
            //
            // We start reformating given input here and extracting
            // only needed lines for player detection
            //
            // Delete empty lines from input
            String txt = text;

            boolean startFound = false;
            while ((p = txt.indexOf(feed)) >= 0) {
                tmp = txt.substring(0, p).trim();

                if (tmp.indexOf("»") > 0) {
                	startFound = true;
                }

                if (!tmp.equals("") && startFound) {
                    lines.add(tmp);
                }

                txt = txt.substring(p + 1);
            }

            //-- get name and store club name
            tmp = lines.get(0).toString();
            player.setPlayerName(tmp.substring(tmp.indexOf("»")+1).trim());
            String teamname = tmp.substring(0, tmp.indexOf("»")).trim();

            //-- get playerid
            int found_at_line = -1;
            int n = 0;
            for (int m = 0; m<10; m++) {
            	tmp = lines.get(m).toString();

            	try {
            		// Players from China etc. have Brackets in their names!!!
            		// Therefore we need lastIndexOf
            		// This also deals with player categories
					if ((p = tmp.indexOf("(")) > -1 && (n = tmp.indexOf(")")) > -1 && Integer.parseInt(tmp.substring(tmp.lastIndexOf("(")+1, tmp.lastIndexOf(")")).trim()) > 100000) {
						player.setPlayerID(Integer.parseInt(tmp.substring(tmp.lastIndexOf("(")+1, tmp.lastIndexOf(")")).trim()));
						found_at_line = m;
						break;
					}
            	} catch (Exception e) {
					if (p < 0) continue;
				}
            }

            //-- get age
            tmp = lines.get(found_at_line + 1).toString();

            String age = "";
            p = 0;
            n = 0;

            while (p < tmp.length()) {
                if ((tmp.charAt(p) < '0') || (tmp.charAt(p) > '9')) {
                    n++;
                } else {
                    tmp = tmp.substring(n);
                    break;
                }

                p++;
            }

            p = 0;

            while (p < tmp.length()) {
                if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                    age = age + tmp.charAt(p);
                } else {
                    break;
                }

                p++;
            }

            if (!age.equals("")) {
                player.setAge(Integer.valueOf(age).intValue());
            } else {
                addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.age"));
            }

            //-- get ageDays
            int ageIndex = tmp.indexOf(age) + age.length();
            tmp = tmp.substring(ageIndex);

            String ageDays = "";
            p = 0;
            n = 0;

            while (p < tmp.length()) {
                if ((tmp.charAt(p) < '0') || (tmp.charAt(p) > '9')) {
                    n++;
                } else {
                    tmp = tmp.substring(n);
                    break;
                }

                p++;
            }

            p = 0;

            while (p < tmp.length()) {
                if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                    ageDays = ageDays + tmp.charAt(p);
                } else {
                    break;
                }

                p++;
            }

            if (!ageDays.equals("")) {
                player.setAgeDays(Integer.valueOf(ageDays).intValue());
            } else {
                addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.age.days"));
            }

            // clean lines till here
            if (found_at_line > 0) {
            	for (int m=0; m<=(found_at_line+1); m++) {
            		lines.remove(0);
            	}
            }
            // remove club line and all lines until the time info (e.g. "since 06.04.2008")
            boolean teamfound = false;
            boolean datefound = false;
            for (int m = 0; m<12; m++) {
            	tmp = lines.get(m).toString();
            	if (tmp.indexOf(teamname)>-1) {
            		teamfound = true;
            	}
            	if (teamfound && !datefound) {
            		lines.remove(m);
            		m--;
            	}
            	if (teamfound && tmp.indexOf("(")>-1 && tmp.indexOf(")")>-1) {
            		datefound = true;
            		break;
            	}
            }

            // Extract TSI-line
            p = 2;

            while (p < lines.size()) {
                //Search for TSI-line (ending in numbers)
                tmp = lines.get(p).toString();

                if ((tmp.charAt(tmp.length() - 1) >= '0') && (tmp.charAt(tmp.length() - 1) <= '9') ) {
                	if (tmp.length()>9 && tmp.substring(tmp.length()-9, tmp.length()).indexOf(".")>-1) {
                		p++;
                		continue;
                	}
                	found_at_line = p;
                    break;
                }

                p++;
            }
            //-- get tsi
            String tsi = "";
            p = 0;

            while (p < tmp.length()) {
                if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                    tsi = tsi + tmp.charAt(p);
                }

                p++;
            }

            if (!tsi.equals("")) {
                player.setTSI(Integer.valueOf(tsi).intValue());
            } else {
                addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.tsi"));
            }

            // -- check for line wage / season (since FoxTrick 0.4.8.2)
			String wage = "";
			p = 0;
			tmp = lines.get(found_at_line + 2).toString();
			// extract spaces
			tmp = tmp.replace(" ", "");
			// get first number
			while (p < tmp.length()) {
				if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
					break;
				}
				p++;
			}
			// stop after first non-number
			while (p < tmp.length()) {
				if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
					wage += tmp.charAt(p);
				} else break;
				p++;
			}
            if (!wage.equals("") && Integer.parseInt(wage) >= 500) {
				found_at_line++;
			}
			//player.setBaseWage(i);

            // -- check bookings
            tmp = lines.get(found_at_line+2).toString();
            try {
            	if (tmp.indexOf(":") > -1 && tmp.indexOf("0") == -1) {
            		player.setBooked(tmp);
            	}
            } catch (Exception e) { /* ignore */ }

            //-- Get injury
            tmp = lines.get(found_at_line+3).toString();
            try {
            	String injury = "";
            	for (int j = 0; j < tmp.length(); j++) {
            		if ((tmp.charAt(j) >= '0') && (tmp.charAt(j) <= '9') && (tmp.charAt(j-1) != '[')) {
            			injury = String.valueOf(tmp.charAt(j));
            			break;
            		}
            	}

            	if (!injury.equals("")) {
            		player.setInjury(Integer.valueOf(injury).intValue());
            	}
            } catch (Exception e) { /* ignore */ }

            // Search for actual year (expires) and also next year
            // (end of year problem)
            final Date d = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy");
            final String year = f.format(d);
            final String year2 = String.valueOf((Integer.parseInt(year)+1));

            p = 0;
            for (int m = 6; m < 8; m++) {
            	// Delete all rows not containing our year
            	tmp = lines.get(m).toString();

            	if (p > 14) { // already 10 lines deleted - there must be something wrong, break
            		break;
            	}

            	if ((tmp.indexOf(year) > -1) || (tmp.indexOf(year2) > -1)) {
            		found_at_line = m;
            		break;
            	} else {
            		lines.remove(m);
            		m--;
            		p++;
            	}
            }
            String exp = getDeadlineString(tmp);

            // Extract minimal bid
            tmp = lines.get(found_at_line+1).toString();
            n = 0;
            int k = 0;
            String bid = "";
            while (k < tmp.length()) {
                if ((tmp.charAt(k) < '0') || (tmp.charAt(k) > '9')) {
                    n++;
                } else {
                    tmp = tmp.substring(n);
                    break;
                }

                k++;
            }
            k = 0;
            while (k < tmp.length()) {
                if ((tmp.charAt(k) >= '0') && (tmp.charAt(k) <= '9')) {
                    bid += tmp.charAt(k);
                }

                k++;
            }

            // Extract current bid if any
            tmp = lines.get(found_at_line + 2).toString();
            n = 0;
            k = 0;
            String curbid = "";
            while (k < tmp.length()) {
                if ((tmp.charAt(k) < '0') || (tmp.charAt(k) > '9')) {
                    n++;
                } else {
                    tmp = tmp.substring(n);
                    break;
                }

                k++;
            }
            k = 0;
            while (k < tmp.length()) {
                if ((tmp.charAt(k) >= '0') && (tmp.charAt(k) <= '9')) {
                    curbid += tmp.charAt(k);
                } else if ((tmp.charAt(k) != ' ') && curbid.length()>0) { // avoid to add numbers from bidding team names
                	break;
                }

                k++;
            }

            player.setPrice(getPrice(bid, curbid));

            //--------------------------------------------

            // exp is of format: ddmmyyyyhhmm
            try {
				player.setExpiryDate(exp.substring(0, 2) + "." + exp.substring(2, 4) + "."
				                     + exp.substring(6, 8));
				player.setExpiryTime(exp.substring(8, 10) + ":" + exp.substring(10, 12));
			} catch (RuntimeException e) {
				// error getting deadline - just set current date
				f = new SimpleDateFormat("dd.MM.yyyy");
				player.setExpiryDate(f.format(new Date()));
				f = new SimpleDateFormat("HH:mm");
				player.setExpiryTime(f.format(new Date()));
				if (status == 0) {
                    addErrorField(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));
                }
			}

            // truncate text from player name to date (year)
            final String name = player.getPlayerName();
            if ((p = mytext.indexOf(name)) >= 0) {
                mytext = mytext.substring(p + name.length());
            }
            if ((p = mytext.indexOf(name)) >= 0) {
                mytext = mytext.substring(p);
            }

            //-- special handling for teams with the * sign in their names - it would lead to an exception in replaceAll()
            if (teamname.indexOf('*') > -1) {
            	teamname = teamname.replaceAll("\\*", ".");
            	mytext = mytext.replaceAll("\\*", ".");
            }

            char[] cs = new char[teamname.length()];
            for (int cl = 0; cl < cs.length; cl++) {
                cs[cl] = '*';
            }
            mytext = mytext.replaceAll(Pattern.quote(teamname), new String(cs)).toLowerCase(java.util.Locale.ENGLISH);

            cs = new char[name.length()];
            for (int cl = 0; cl < cs.length; cl++) {
                cs[cl] = '*';
            }
            mytext = mytext.replaceAll(Pattern.quote(name.toLowerCase(java.util.Locale.ENGLISH)), new String(cs)).toLowerCase(java.util.Locale.ENGLISH);

            // We can search all the skills in text now
            p = skills.size() - 1;

            final List<List<Object>> foundskills = new ArrayList<List<Object>>();

            while (p >= 0) {
                final String singleskill = skills.get(p).toString();
                k = mytext.indexOf(singleskill);

                if (k >= 0) {
                    final List<Object> pair = new ArrayList<Object>();
                    pair.add(k);
                    pair.add(singleskill);
                    pair.add(p);
                    foundskills.add(pair);

                    final char[] ct = new char[singleskill.length()];

                    for (int cl = 0; cl < ct.length; cl++) {
                        ct[cl] = '*';
                    }

                    mytext = mytext.replaceFirst(singleskill, new String(ct));
                } else {
                    p--;
                }
            }

            if ((foundskills.size() < 11) && (status == 0)) {
                addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill"));
            }

            // Sort skills by location
            p = foundskills.size() - 1;

            while (p > 0) {
                k = p;

                while (k < foundskills.size()) {
                    final List<Object> ts1 = foundskills.get(k - 1);
                    final List<Object> ts2 = foundskills.get(k);

                    if ((Integer) ts1.get(0) > (Integer) ts2.get(0)) {
                        foundskills.set(k - 1, ts2);
                        foundskills.set(k, ts1);
                        k++;
                    } else {
                        break;
                    }
                }

                p--;
            }

            // check format
            try {
				p = mytext.indexOf("/20");
				if (p > -1 && mytext.indexOf("/20", p+5) > -1 && foundskills.size() >= 12) {
					setSkillsBarStyle(player, foundskills);
				} else if (foundskills.size() >= 13) {
					setSkillsClassicStyle(player, foundskills);
				} else if (foundskills.size() == 12) { // no "20" in the text, but 12 skills (e.g. IE6)
					setSkillsBarStyle(player, foundskills);
				}
			} catch (Exception e) {
                addErrorField(HOVerwaltung.instance().getLanguageString("ls.player.skill"));
			}

            // We can search the speciality in text now
            p = specialities.size() - 1;

            final List<List<Object>> foundspecialities = new ArrayList<List<Object>>();

            while (p > 0) {
                final String singlespeciality = specialities.get(p).toString();
                k = mytext.indexOf(singlespeciality);

                if (k >= 0) {
                	// check letter after the found specialty. Skip specialty in case of a normal letter
                	final int k2 = k + singlespeciality.length();
                	boolean skip = false;
                	if (k2 + 1 < mytext.length()) {
                		final String specadd = mytext.substring(k2, k2+1);
                		if (NORMALCHARS.contains(specadd)) {
                			skip = true;
                		}
                	}
                	if (!skip) {
                		final List<Object> pair = new ArrayList<Object>();
                		pair.add(k);
                		pair.add(singlespeciality);
                		pair.add(p);
                		foundspecialities.add(pair);
                	}

                    final char[] ct = new char[singlespeciality.length()];

                    for (int cl = 0; cl < ct.length; cl++) {
                        ct[cl] = '*';
                    }

                    mytext = mytext.replaceFirst(singlespeciality, new String(ct));
                } else {
                    p--;
                }
            }

            if ((foundspecialities.size() > 1) && (status == 0)) {
            	status = WARNING;
            	try {
            		if (foundspecialities.size() == 2 && (Integer)(foundspecialities.get(1).get(0)) > 1500) {
            			// no error, but caused by Foxtricks quick-links (QUICK links <-> QUICK player special)
            			status = 0;
            		}
            	} catch (Exception e) {
            		// nothing todo here
            	}
            }

            // Sort specialities by location
            p = foundspecialities.size() - 1;

            while (p > 0) {
                k = p;

                while (k < foundspecialities.size()) {
                    final List<Object> ts1 = foundspecialities.get(k - 1);
                    final List<Object> ts2 = foundspecialities.get(k);

                    if ((Integer) ts1.get(0) > (Integer) ts2.get(0)) {
                        foundspecialities.set(k - 1, ts2);
                        foundspecialities.set(k, ts1);
                        k++;
                    } else {
                        break;
                    }
                }

                p--;
            }

            if (foundspecialities.size() > 0) {
                player.setSpeciality(specialitiesvalues.get((Integer) (foundspecialities.get(0)).get(2)));
            } else {
                player.setSpeciality(0);
            }

            setDeadline(player);
        }else{
            status = ERROR;
        }
        return player;
    }

    /**
     * Set parsed skills in the player object. Bar style.
     */
    private void setSkillsBarStyle(Player player, final List<List<Object>> foundskills) throws Exception {
    	// player skills (long default format with bars)
    	player.setForm(skillvalues.get((Integer) (foundskills.get(0)).get(2)));
    	player.setStamina(skillvalues.get((Integer) (foundskills.get(1)).get(2)));
    	player.setExperience(skillvalues.get((Integer) (foundskills.get(2)).get(2)));
    	player.setLeadership(skillvalues.get((Integer) (foundskills.get(3)).get(2)));

    	player.setGoalKeeping(skillvalues.get((Integer) (foundskills.get(5)).get(2)));
    	player.setDefense(skillvalues.get((Integer) (foundskills.get(6)).get(2)));
    	player.setPlayMaking(skillvalues.get((Integer) (foundskills.get(7)).get(2)));
    	player.setWing(skillvalues.get((Integer) (foundskills.get(8)).get(2)));
    	player.setPassing(skillvalues.get((Integer) (foundskills.get(9)).get(2)));
    	player.setAttack(skillvalues.get((Integer) (foundskills.get(10)).get(2)));
    	player.setSetPieces(skillvalues.get((Integer) (foundskills.get(11)).get(2)));
    }

    /**
     * Set parsed skills in the player object. Classic style with 2 skills per line.
     */
    private void setSkillsClassicStyle(Player player, final List<List<Object>> foundskills) throws Exception {
    	// player skills (2er format without bars)
	    player.setForm(skillvalues.get((Integer) (foundskills.get(0)).get(2)));
	    player.setStamina(skillvalues.get((Integer) (foundskills.get(1)).get(2)));
	    player.setExperience(skillvalues.get((Integer) (foundskills.get(2)).get(2)));
	    player.setLeadership(skillvalues.get((Integer) (foundskills.get(3)).get(2)));

   	    player.setGoalKeeping((Integer) skillvalues.get((Integer) (foundskills.get(6)).get(2)));
	    player.setDefense((Integer) skillvalues.get((Integer) (foundskills.get(10)).get(2)));
	    player.setPlayMaking((Integer) skillvalues.get((Integer) (foundskills.get(7)).get(2)));
	    player.setWing((Integer) skillvalues.get((Integer) (foundskills.get(9)).get(2)));
	    player.setPassing((Integer) skillvalues.get((Integer) (foundskills.get(8)).get(2)));
	    player.setAttack((Integer) skillvalues.get((Integer) (foundskills.get(11)).get(2)));
	    player.setSetPieces((Integer) skillvalues.get((Integer) (foundskills.get(12)).get(2)));
    }

    public static int getPrice(String bid, String curbid) {
    	int price = 0;
    	try {
    		price = Integer.parseInt(bid);
    		if (curbid.length()>0 && Integer.parseInt(curbid) >= Integer.parseInt(bid)) {
    			price = Integer.parseInt(curbid);
    		}
    	} catch (Exception e) { /* nothing */ }
    	return price;
    }

    public static String getDeadlineString(String tmp) {
        // get deadline
        String exp = "";
        int p = 0;
        int k = 0;

        while (p < tmp.length()) {
            if ((tmp.charAt(p) < '0') || (tmp.charAt(p) > '9')) {
                k++;
            } else {
                tmp = tmp.substring(k);
                break;
            }

            p++;
        }

        p = 0;
        k = 0;

        String part1 = "";

        while (p < tmp.length()) {
            if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                k++;
            } else {
                part1 = tmp.substring(0, k);

                if (part1.length() < 2) {
                    part1 = "0" + part1;
                }

                tmp = tmp.substring(k + 1);
                break;
            }

            p++;
        }

        p = 0;
        k = 0;

        String part2 = "";

        while (p < tmp.length()) {
            if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                k++;
            } else {
                part2 = tmp.substring(0, k);

                if (part2.length() < 2) {
                    part2 = "0" + part2;
                }

                tmp = tmp.substring(k + 1);
                break;
            }

            p++;
        }

        p = 0;
        k = 0;

        String part3 = "";

        while (p < tmp.length()) {
            if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                k++;
            } else {
                part3 = tmp.substring(0, k);

                if (part3.length() < 2) {
                    part3 = "0" + part3;
                }

                tmp = tmp.substring(k + 1);
                break;
            }

            p++;
        }

        p = 0;

        String part4 = "";

        while (p < tmp.length()) {
            if ((tmp.charAt(p) >= '0') && (tmp.charAt(p) <= '9')) {
                part4 = part4 + tmp.charAt(p);
            }

            p++;
        }

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("ddMMyyyy");

        final Date d1 = c.getTime();
        final String date1 = f.format(d1);
        c.add(Calendar.DATE, 1);

        final Date d2 = c.getTime();
        final String date2 = f.format(d2);
        c.add(Calendar.DATE, 1);

        final Date d3 = c.getTime();
        final String date3 = f.format(d3);

        String date = part1 + part2 + part3;

        if ((date1.equals(date)) || (date2.equals(date)) || (date3.equals(date))) {
            exp = date + part4;
        } else {
            date = part1 + part3 + part2;

            if ((date1.equals(date)) || (date2.equals(date)) || (date3.equals(date))) {
                exp = date + part4;
            } else {
                date = part2 + part1 + part3;

                if ((date1.equals(date)) || (date2.equals(date)) || (date3.equals(date))) {
                    exp = date + part4;
                } else {
                    date = part2 + part3 + part1;

                    if ((date1.equals(date)) || (date2.equals(date)) || (date3.equals(date))) {
                        exp = date + part4;
                    } else {
                        date = part3 + part1 + part2;

                        if ((date1.equals(date))
                            || (date2.equals(date))
                            || (date3.equals(date))) {
                            exp = date + part4;
                        } else {
                            date = part3 + part2 + part1;

                            if ((date1.equals(date))
                                || (date2.equals(date))
                                || (date3.equals(date))) {
                                exp = date + part4;
                            } else {
                                exp = part1 + part2 + part3 + part4;
                            }
                        }
                    }
                }
            }
        }
        return exp;
    }

    public java.util.Date getDeadline(){
        return deadLineDate;
    }

    public void setDeadline(Player player){
        if(player.getExpiryDate() == null || player.getExpiryTime() == null || player.getExpiryDate().isEmpty() || player.getExpiryTime().isEmpty()){
            this.addErrorField(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));
        } else {
            try {
                final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("dd.MM.yy HH:mm",
                        java.util.Locale.GERMANY);
                deadLineDate = simpleFormat.parse(player.getExpiryDate() + " "
                        + player.getExpiryTime());
            } catch (Exception e) {
                HOLogger.instance().debug(getClass(), e);
                this.addErrorField(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));
            }
        }
    }
}
