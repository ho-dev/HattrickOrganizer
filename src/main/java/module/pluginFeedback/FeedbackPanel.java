package module.pluginFeedback;

import com.google.gson.Gson;
import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.util.HODateTime;
import core.util.HOLogger;
import core.util.UTF8Control;
import module.lineup.ratings.RatingComparisonPanel;
import module.teamAnalyzer.vo.MatchRating;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FeedbackPanel extends JFrame {

    GridBagLayout layout;
    JLabel lineupRecommendation;
    JLabel GK, WBr, CDr, CDc, CDl, WBl, WIr, IMr, IMc, IMl, WIl, FWr, FWc, FWl;
    JTextArea jtaCopyPaste;
    JButton jbRefresh, jbSend;
    SimpleLineup requirements;
    MatchRating HTRatings;
    Boolean bFetchLineupSuccess;
    Boolean areLineupsValid = false;
    RatingComparisonPanel HOPredictionRating, HTPredictionRating, DeltaPredictionRating;

    public FeedbackPanel() {
        int lastHrfId = DBManager.instance().getLatestHRF().getHrfId();
        var dateHrf = DBManager.instance().getBasics(lastHrfId).getDatum();
        if (Duration.between(dateHrf.instant, HODateTime.now().instant).toHours() >= 1) {
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.dataTooOldWarning"); //java.text.DateFormat.getDateTimeInstance().format(dateHrf));
            message = String.format(message, java.text.DateFormat.getDateTimeInstance().format(dateHrf));
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
        } else {
            HTRatings = new MatchRating();
            bFetchLineupSuccess = fetchRequiredLineup();
            initComponents();
            refresh();
        }
    }

    public boolean parseHTRating(String input) {
        Pattern pattern;
        Matcher matcher;
        String regex;

        if (input.isEmpty()) {
            return false;
        }

        // Parsing HT ratings values ============================================================
        regex = "(?<=\\])([0-9\\.]+)(?=\\[)";
        pattern = Pattern.compile(regex, Pattern.MULTILINE);
        List<Double> allRatings;

        try {
            matcher = pattern.matcher(input);
            allRatings = new ArrayList<>();
            while (matcher.find()) {
                allRatings.add(Double.parseDouble(matcher.group(0)));
            }
        } catch (Exception e) {
            // Error while parsing HT ratings values ============================================================
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.ParseHTRatingError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (allRatings.size() != 7) {
            // We haven't found 7 ratings as expected  ============================================================
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.ParseHTRatingError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        // Parsing other attributes ============================================================

        List<String> allKeys = new ArrayList<>();
        List<String> allValues = new ArrayList<>();

        // get all the keys
        regex = "(?<=\\[b\\])(.*?)(?=\\[\\/b\\])";
        pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);

        try {
            matcher = pattern.matcher(input);

            while (matcher.find()) {
                allKeys.add(matcher.group(0));
            }
        } catch (Exception e) {
            // Error while parsing other attributes ============================================================
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.ParseHTRatingError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (allKeys.size() < 4) {
            // We haven't found 4 attributes as expected  ============================================================
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.ParseHTRatingError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        String thisKey, nextKey;

        for (int i = 0; i < allKeys.size() - 1; i++) {

            thisKey = allKeys.get(i);
            nextKey = allKeys.get(i + 1);

            regex = "(?<=" + thisKey + ")(.*)(?=" + nextKey + ")";
            pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
            matcher = pattern.matcher(input);
            matcher.find();
            allValues.add(matcher.group(0));
        }

        thisKey = allKeys.get(allKeys.size() - 1);
        regex = "(?<=" + thisKey + ")(.*)";
        pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        matcher = pattern.matcher(input);
        matcher.find();
        allValues.add(matcher.group(0));

        HashMap<String, String> otherAttributes = new HashMap<>();

        String temp;

        for (int i = 0; i < allValues.size(); i++) {
            temp = allValues.get(i).replace("[/b]", "");
            temp = temp.replace("[b]", "");
            temp = temp.replaceAll("[:|\\s]", "");
            otherAttributes.put(allKeys.get(i).toLowerCase(), temp.toLowerCase());
        }

        String attitude = getTerms(otherAttributes, "ls.team.teamattitude");
        String tacticType = getTerms(otherAttributes, "ls.team.tactics");
        String style_of_play = getTerms(otherAttributes, "ls.team.styleofPlay");

        int iTacticType = MatchRating.TacticTypeStringToInt(tacticType);
        var lineup = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup();
        int iTacticlevel = MatchRating.float2HTint(lineup.getTacticLevel(iTacticType));
        int iAttitude = MatchRating.AttitudeStringToInt(attitude);
        int iStyle_of_play = MatchRating.StyleOfPlayStringToInt(style_of_play);

        if ((iTacticType == MatchRating.ERROR) || (iAttitude == MatchRating.ERROR) ||
                (iStyle_of_play == MatchRating.ERROR)) {
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.ParseHTRatingError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        HTRatings = new MatchRating(allRatings.get(2), allRatings.get(1), allRatings.get(0), allRatings.get(3), allRatings.get(6),
                allRatings.get(5), allRatings.get(4), iAttitude, iTacticType, iTacticlevel, iStyle_of_play);

        return true;
    }


    private static String getTerms(HashMap<String, String> map, String term) {

        ResourceBundle tempBundle = ResourceBundle.getBundle("sprache.English", new UTF8Control());
        String english_term = tempBundle.getString(term).toLowerCase();

        String local_term = HOVerwaltung.instance().getLanguageString(term).toLowerCase();
        for (String _term : Arrays.asList(local_term, english_term)) {
            if (map.containsKey(_term)) {
                return map.get(_term);
            }
        }

        // language is not English and translation does not exist, we will try to parse using a proxy
        if (term.equals("ls.team.tactics")) {
            String local_proxy_term = HOVerwaltung.instance().getLanguageString("ls.team.tactic").toLowerCase().substring(0, 5);
            String short_key;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                short_key = entry.getKey().substring(0, 5);
                if (short_key.equals(local_proxy_term)) {
                    return entry.getValue();
                }
            }
        }

        // language is not English and translation does not exist, we will try to parse using a proxy
        if (term.equals("ls.team.teamattitude")) {
            String local_proxy_term = HOVerwaltung.instance().getLanguageString("ls.team.teamattitude").toLowerCase().substring(0, 5);
            String short_key;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                short_key = entry.getKey().substring(0, 5);
                if (short_key.equals(local_proxy_term)) {
                    return entry.getValue();
                }
            }
        }

        return "";
    }


    private boolean checkHOandHTLineups() {
        int positionHO, orderHO;
        boolean isAligned, positionIsRequired;


        // return false if attitude not properly set
        String requirementsAttitude = HOVerwaltung.instance().getLanguageString("ls.team.teamattitude." + requirements.attitude.toLowerCase()).toLowerCase();
        int iAttitude = MatchRating.AttitudeStringToInt(requirementsAttitude);
        var lineup = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup();
        if ((lineup.getAttitude() != HTRatings.getAttitude()) ||
                (lineup.getAttitude() != MatchRating.AttitudeStringToInt(requirementsAttitude))) return false;

        // return false if tactic not properly set
        String requirementsTacticType = HOVerwaltung.instance().getLanguageString("ls.team.tactic." + requirements.tactic.toLowerCase()).toLowerCase();
        int iTactic = MatchRating.TacticTypeStringToInt(requirementsTacticType);
        if ((lineup.getTacticType() != HTRatings.getTacticType()) ||
                (lineup.getTacticType() != MatchRating.TacticTypeStringToInt(requirementsTacticType))) return false;

        // return false if style of play not properly set
        if ((lineup.getStyleOfPlay() * 10) != HTRatings.getStyle_of_play()) return false;

        // return false if HOLineup not fully included in required Lineup
        for (var obj : lineup.getAllPositions()) {
            positionHO = obj.getId();
            orderHO = obj.getTactic();
            isAligned = (obj.getPlayerId() != 0) && IMatchRoleID.aFieldMatchRoleID.contains(positionHO);


            if (isAligned) {
                positionIsRequired = requirements.lineup.containsKey(positionHO);
                if (!positionIsRequired)
                    return false; // Player in the lineup at a position not listed in the requirements
                if (requirements.lineup.get(positionHO) != orderHO) return false; // Player has incorrect orders
            }
        }

        // return false if required Lineup not fully included in HO Lineup
        for (Map.Entry<Integer, Byte> entry : requirements.lineup.entrySet()) {
            var position = lineup.getPositionById(entry.getKey());
            orderHO = position.getTactic();
            isAligned = (position.getPlayerId() != 0) && IMatchRoleID.aFieldMatchRoleID.contains(position.getId());
            if ((!isAligned) || (orderHO!=entry.getValue())) {return false;}
        }

        return true;
    }


    private void sendToServer() {
        HOVerwaltung m_hov = HOVerwaltung.instance();

        // TODO: implementer warning based on comparison HO vs HT ???

        PluginFeedback pluginFeedback = new PluginFeedback(requirements.server_url);
        String message = "[" + pluginFeedback.getHoToken() + "] ";
        try {
            var HOLineup = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup();
            String result = pluginFeedback.sendFeedbackToServer(HOLineup, HTRatings, requirements.lineupName);
            message += m_hov.getLanguageString("feedbackplugin.success");
            HOLogger.instance().info(getClass(), message);
            JOptionPane.showMessageDialog(null, message);
        } catch (IOException ex) {
            HOLogger.instance().error(getClass(), ex);
            ex.printStackTrace();
            message += m_hov.getLanguageString("feedbackplugin.serverError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            HOLogger.instance().error(getClass(), ex);
            ex.printStackTrace();
            message += m_hov.getLanguageString("feedbackplugin.inputError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean fetchRequiredLineup() {

        try {
            URL url = new URL("https://ho-dev.github.io/HattrickOrganizer/feedback.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            BufferedReader json = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            //BufferedReader json = new BufferedReader(new FileReader("D:\\Temp\\feedback.json"));
            requirements = new Gson().fromJson(json, SimpleLineup.class);
            return true;
        } catch (Exception e) {
            String message = HOVerwaltung.instance().getLanguageString("feedbackplugin.fetcRequiredLineupError");
            JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }


    private void formatPlayerBox(JLabel jl, String pos, Byte order) {
        if (order != null) {
            String s_order = pos;
            jl.setBackground(Color.WHITE);
            jl.setForeground(Color.BLACK);
            jl.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));

            java.util.List<String> righSide = Arrays.asList("WBr", "CDr", "WIr", "IMr", "FWr");

            String right_arrow = "\uD83E\uDC7A";
            String left_arrow = "\uD83E\uDC78";
            String up_arrow = "\uD83E\uDC79";
            String down_arrow = "\uD83E\uDC7B";

            HOVerwaltung hoi = HOVerwaltung.instance();

            String off = "(" + hoi.getLanguageString("ls.player.behaviour.offensive.short").toUpperCase() + ")";
            String def = "(" + hoi.getLanguageString("ls.player.behaviour.defensive.short").toUpperCase() + ")";
            String tm = "(" + hoi.getLanguageString("ls.player.behaviour.towardsmiddle.short").toUpperCase() + ")";
            String tw = "(" + hoi.getLanguageString("ls.player.behaviour.towardswing.short").toUpperCase() + ")";

            switch (order) {
                case IMatchRoleID.NORMAL -> {
                }
                case IMatchRoleID.OFFENSIVE -> s_order += " " + off + " " + down_arrow;
                case IMatchRoleID.DEFENSIVE -> s_order += " " + def + " " + up_arrow;
                case IMatchRoleID.TOWARDS_WING -> {
                    if (righSide.contains(pos)) {
                        s_order = left_arrow + " " + s_order + " " + tw;
                    } else {
                        s_order = s_order + " " + tw + " " + right_arrow;
                    }
                }
                case IMatchRoleID.TOWARDS_MIDDLE -> {
                    if (!righSide.contains(pos)) {
                        s_order = left_arrow + " " + s_order + " " + tm;
                    } else {
                        s_order = s_order + " " + tm + " " + right_arrow;
                    }
                }
            }
            jl.setText(s_order);
        }

    }


    private void formatSendButton() {
        if (areLineupsValid && requirements.server_status.equals("up")) {
            jbSend.setEnabled(true);
            jbSend.setToolTipText(HOVerwaltung.instance().getLanguageString("feedbackplugin.jbSendActivated"));
        } else {
            jbSend.setEnabled(false);
            jbSend.setToolTipText(HOVerwaltung.instance().getLanguageString("feedbackplugin.jbSendDeactivated"));
        }
    }

    private void refreshRatingComparisonPanel() {
        HOPredictionRating.refresh();
        HTPredictionRating.refresh();
        DeltaPredictionRating.refresh();
    }

    private void refresh() {

        // Refresh HO Lineup and ratings
        var HORatings = HOVerwaltung.instance().getModel().getCurrentLineupTeamRecalculated().getLineup().getRatings();
        bFetchLineupSuccess = parseHTRating(jtaCopyPaste.getText());
        HOPredictionRating.setMatchRating(HORatings);
        HTPredictionRating.setMatchRating(HTRatings);
        DeltaPredictionRating.setMatchRating(HOPredictionRating.getMatchRating().minus(HTPredictionRating.getMatchRating()));
        refreshRatingComparisonPanel();

        if (bFetchLineupSuccess) {
            areLineupsValid = checkHOandHTLineups();
            if (!areLineupsValid) {
                String message = "<html>" +
                        HOVerwaltung.instance().getLanguageString("feedbackplugin.jbSendDeactivated") + "</br>" +
                        HOVerwaltung.instance().getLanguageString("feedbackplugin.notMatchRequirements") +
                        "</html>";
                JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
            }
            if (!requirements.server_status.equals("up")) {
                String message = "<html>" +
                        HOVerwaltung.instance().getLanguageString("feedbackplugin.serverOffline") +
                        "</html>";
                JOptionPane.showMessageDialog(null, message, "", JOptionPane.INFORMATION_MESSAGE);
            }
            formatSendButton(); // it is possible to send data only if all criteria are met
        }

    }


    private void initComponents() {

        HOVerwaltung hoi = HOVerwaltung.instance();
        GridBagConstraints gbc = new GridBagConstraints();
        setTitle(hoi.getLanguageString("Lineup.Feedback.Panel.Title"));
        layout = new GridBagLayout();
        this.setLayout(layout);

        // Lineup recommendation =====================================================================
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridwidth = 5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        String start = "<html><b><font color='red'>1.</font></b> ";
        String end = ":</html>";
        String msg = hoi.getLanguageString("Lineup.Feedback.Panel.Instruction1");
        lineupRecommendation = new JLabel(start + msg + end);
        this.add(lineupRecommendation, gbc);
        // ==========================================================================================

        // Lineup ======================================================================
        // GK ======================================================================
        gbc.insets = new Insets(15, 5, 5, 5);  //top padding
        gbc.gridwidth = 1;
        gbc.ipadx = 10;
        gbc.weightx = 1;
        gbc.gridx = 2;
        gbc.gridy = 1;
        msg = hoi.getLanguageString("subs.gk");
        GK = new JLabel(msg, JLabel.CENTER);
        GK.setOpaque(true);
        GK.setBackground(Color.WHITE);
        GK.setForeground(Color.GRAY);
        GK.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(GK, msg, requirements.lineup.get(IMatchRoleID.keeper));
        this.add(GK, gbc);
        // WBr ======================================================================
        gbc.insets = new Insets(5, 5, 5, 5);  //top padding
        gbc.gridx = 0;
        gbc.gridy = 2;
        msg = hoi.getLanguageString("subs.rb");
        WBr = new JLabel(msg, JLabel.CENTER);
        WBr.setOpaque(true);
        WBr.setBackground(Color.WHITE);
        WBr.setForeground(Color.GRAY);
        WBr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(WBr, msg, requirements.lineup.get(IMatchRoleID.rightBack));
        this.add(WBr, gbc);
        // CDr ======================================================================
        gbc.gridx = 1;
        gbc.gridy = 2;
        msg = hoi.getLanguageString("subs.rcd");
        CDr = new JLabel(msg, JLabel.CENTER);
        CDr.setOpaque(true);
        CDr.setBackground(Color.WHITE);
        CDr.setForeground(Color.GRAY);
        CDr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(CDr, msg, requirements.lineup.get(IMatchRoleID.rightCentralDefender));
        this.add(CDr, gbc);
        // CDc ======================================================================
        gbc.gridx = 2;
        gbc.gridy = 2;
        msg = hoi.getLanguageString("subs.mcd");
        CDc = new JLabel(msg, JLabel.CENTER);
        CDc.setOpaque(true);
        CDc.setBackground(Color.WHITE);
        CDc.setForeground(Color.GRAY);
        CDc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(CDc, msg, requirements.lineup.get(IMatchRoleID.middleCentralDefender));
        this.add(CDc, gbc);
        // CDl ======================================================================
        gbc.gridx = 3;
        gbc.gridy = 2;
        msg = hoi.getLanguageString("subs.lcd");
        CDl = new JLabel(msg, JLabel.CENTER);
        CDl.setOpaque(true);
        CDl.setBackground(Color.WHITE);
        CDl.setForeground(Color.GRAY);
        CDl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(CDl, msg, requirements.lineup.get(IMatchRoleID.leftCentralDefender));
        this.add(CDl, gbc);
        // WBl ======================================================================
        gbc.gridx = 4;
        gbc.gridy = 2;
        msg = hoi.getLanguageString("subs.lb");
        WBl = new JLabel(msg, JLabel.CENTER);
        WBl.setOpaque(true);
        WBl.setBackground(Color.WHITE);
        WBl.setForeground(Color.GRAY);
        WBl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(WBl, msg, requirements.lineup.get(IMatchRoleID.leftBack));
        this.add(WBl, gbc);
        // WIr ======================================================================
        gbc.insets = new Insets(5, 5, 5, 5);  //top padding
        gbc.gridx = 0;
        gbc.gridy = 3;
        msg = hoi.getLanguageString("subs.rw");
        WIr = new JLabel(msg, JLabel.CENTER);
        WIr.setOpaque(true);
        WIr.setBackground(Color.WHITE);
        WIr.setForeground(Color.GRAY);
        WIr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(WIr, msg, requirements.lineup.get(IMatchRoleID.rightWinger));
        this.add(WIr, gbc);
        // IMr ======================================================================
        gbc.gridx = 1;
        gbc.gridy = 3;
        msg = hoi.getLanguageString("subs.rim");
        IMr = new JLabel(msg, JLabel.CENTER);
        IMr.setOpaque(true);
        IMr.setBackground(Color.WHITE);
        IMr.setForeground(Color.GRAY);
        IMr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(IMr, msg, requirements.lineup.get(IMatchRoleID.rightInnerMidfield));
        this.add(IMr, gbc);
        // IMc ======================================================================
        gbc.gridx = 2;
        gbc.gridy = 3;
        msg = hoi.getLanguageString("subs.cim");
        IMc = new JLabel(msg, JLabel.CENTER);
        IMc.setOpaque(true);
        IMc.setBackground(Color.WHITE);
        IMc.setForeground(Color.GRAY);
        IMc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(IMc, msg, requirements.lineup.get(IMatchRoleID.centralInnerMidfield));
        this.add(IMc, gbc);
        // IMl ======================================================================
        gbc.gridx = 3;
        gbc.gridy = 3;
        msg = hoi.getLanguageString("subs.lim");
        IMl = new JLabel(msg, JLabel.CENTER);
        IMl.setOpaque(true);
        IMl.setBackground(Color.WHITE);
        IMl.setForeground(Color.GRAY);
        IMl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(IMl, msg, requirements.lineup.get(IMatchRoleID.leftInnerMidfield));
        this.add(IMl, gbc);
        // WIl ======================================================================
        gbc.gridx = 4;
        gbc.gridy = 3;
        msg = hoi.getLanguageString("subs.lw");
        WIl = new JLabel(msg, JLabel.CENTER);
        WIl.setOpaque(true);
        WIl.setBackground(Color.WHITE);
        WIl.setForeground(Color.GRAY);
        WIl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(WIl, msg, requirements.lineup.get(IMatchRoleID.leftWinger));
        this.add(WIl, gbc);
        // FWr ======================================================================
        gbc.gridx = 1;
        gbc.gridy = 4;
        msg = hoi.getLanguageString("subs.rfw");
        FWr = new JLabel(msg, JLabel.CENTER);
        FWr.setOpaque(true);
        FWr.setBackground(Color.WHITE);
        FWr.setForeground(Color.GRAY);
        FWr.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(FWr, msg, requirements.lineup.get(IMatchRoleID.rightForward));
        this.add(FWr, gbc);
        // FWc ======================================================================
        gbc.gridx = 2;
        gbc.gridy = 4;
        msg = hoi.getLanguageString("subs.cfw");
        FWc = new JLabel(msg, JLabel.CENTER);
        FWc.setOpaque(true);
        FWc.setBackground(Color.WHITE);
        FWc.setForeground(Color.GRAY);
        FWc.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(FWc, msg, requirements.lineup.get(IMatchRoleID.centralForward));
        this.add(FWc, gbc);
        // FWl ======================================================================
        gbc.gridx = 3;
        gbc.gridy = 4;
        msg = hoi.getLanguageString("subs.lfw");
        FWl = new JLabel(msg, JLabel.CENTER);
        FWl.setOpaque(true);
        FWl.setBackground(Color.WHITE);
        FWl.setForeground(Color.GRAY);
        FWl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        formatPlayerBox(FWl, msg, requirements.lineup.get(IMatchRoleID.leftForward));
        this.add(FWl, gbc);
        // ==========================================================================================
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 0, 10);
        start = "<html><b><u>";

        String jlTactics_message = hoi.getLanguageString("ls.team.tactic");
        String jlTeamAttitude_message = hoi.getLanguageString("ls.team.teamattitude");

        if (bFetchLineupSuccess) {
            String requirementsTacticType = hoi.getLanguageString("ls.team.tactic." + requirements.tactic.toLowerCase()).toLowerCase();
            int iTactic = MatchRating.TacticTypeStringToInt(requirementsTacticType);

            String requirementsAttitude = hoi.getLanguageString("ls.team.teamattitude." + requirements.attitude.toLowerCase()).toLowerCase();
            int iAttitude = MatchRating.AttitudeStringToInt(requirementsAttitude);

            core.model.match.Matchdetails md = new core.model.match.Matchdetails();
            jlTactics_message = start + jlTactics_message + ":</u></b> " + Matchdetails.getNameForEinstellung(iTactic) + "</html>";
            jlTeamAttitude_message = start + jlTeamAttitude_message + ":</u></b> " + Matchdetails.getNameForEinstellung(iAttitude) + "</html>";
        } else {
            jlTactics_message += "?</html>";
            jlTeamAttitude_message += "?</html>";
        }

        JLabel jlTactics = new JLabel(jlTactics_message, JLabel.LEFT);
        this.add(jlTactics, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 10, 0, 10);
        JLabel jlTeamAttitude = new JLabel(jlTeamAttitude_message, JLabel.LEFT);
        this.add(jlTeamAttitude, gbc);

        // ========================================================================
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 5, 0, 5);
        gbc.gridwidth = 5;
        gbc.gridx = 0;
        gbc.gridy = 7;
        start = "<html><b><font color='red'>2.</font></b> ";
        msg = hoi.getLanguageString("Lineup.Feedback.Panel.Instruction2");
        end = "</html>";

        lineupRecommendation = new JLabel(start + msg + end);
        this.add(lineupRecommendation, gbc);
        // ==========================================================================================

        // Copy Paste Area  ==========================================================================
        gbc.insets = new Insets(10, 5, 0, 10);
        gbc.gridwidth = 5;
        gbc.ipadx = 0;
        gbc.ipady = 100;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridy = 8;
        jtaCopyPaste = new JTextArea();
        jtaCopyPaste.setFont(new Font("Serif", Font.ITALIC, 10));
        jtaCopyPaste.setLineWrap(true);
        jtaCopyPaste.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(jtaCopyPaste);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(areaScrollPane, gbc);

        // ================ PREDICTION RATING  =========================================================================
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.insets = new Insets(30, 5, 0, 10);

        HOPredictionRating = new RatingComparisonPanel("HO");
        HTPredictionRating = new RatingComparisonPanel("HT");
        DeltaPredictionRating = new RatingComparisonPanel(hoi.getLanguageString("Delta"));

        JPanel content = new JPanel();
        content.add(HOPredictionRating);
        content.add(HTPredictionRating);
        content.add(DeltaPredictionRating);

        this.add(content, gbc);

        // =================BUTTONS    ===============================================================================================
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 5, 10, 5);
        gbc.ipady = 0;
        gbc.gridx = 1;
        gbc.gridy = 10;
        jbRefresh = new JButton(hoi.getLanguageString("Lineup.Feedback.Panel.btnRefresh"));
        jbRefresh.addActionListener(e -> refresh());
        this.add(jbRefresh, gbc);

        gbc.gridx = 3;
        jbSend = new JButton("  " + hoi.getLanguageString("Lineup.Feedback.Panel.btnSend") + "  ");
        jbSend.addActionListener(e -> sendToServer());
        formatSendButton();


        this.add(jbSend, gbc);

        setSize(900, 800);
        setPreferredSize(getSize());
        setResizable(false);
        setVisible(true);

    }

    private static class SimpleLineup {
        String server_url;
        String server_status;
        String lineupName;
        String attitude;
        String tactic;
        Map<Integer, Byte> lineup;
    }
}
