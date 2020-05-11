package module.teamAnalyzer.ui;

import core.gui.model.BaseTableModel;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.specialevents.SpecialEventsPrediction;
import core.specialevents.SpecialEventsPredictionManager;
import core.util.Helper;
import module.teamAnalyzer.vo.TeamLineup;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;


public class SpecialEventsPanel extends JPanel {
    private final JTable table;
    private BaseTableModel tableModel;
    private final JLabel resultLabel;
    private static HOVerwaltung hov = HOVerwaltung.instance();

    private final String[] columns = {
            hov.getLanguageString("Event"),
            hov.getLanguageString("Spieler"),
            hov.getLanguageString("ls.teamanalyzer.opponent_player"),
            hov.getLanguageString("ls.teamanalyzer.involved_player"),
            hov.getLanguageString("ls.teamanalyzer.probability"),
            hov.getLanguageString("ls.teamanalyzer.scores"),
            hov.getLanguageString("ls.teamanalyzer.opponent_scores")
    };

    public SpecialEventsPanel(){
        Vector<Vector<Object>> data = new Vector<>();

        tableModel = new BaseTableModel(data, new Vector<String>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        //table.setDefaultRenderer(Object.class, new RatingTableCellRenderer());

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JLabel start = new JLabel(hov.getLanguageString("ls.teamanalyzer.special_events"));
        add(start, BorderLayout.PAGE_START);
        add(scrollPane);

        resultLabel = new JLabel( hov.getLanguageString("ls.teamanalyzer.result") + ": 0.00 - 0.00");
        add(resultLabel, BorderLayout.PAGE_END);

    }

    public void reload(TeamLineup teamLineup) {
        if ( teamLineup==null) return;
        SpecialEventsPredictionManager specialEventsPredictionManager = teamLineup.getSpecialEventsPrediction();
        if (specialEventsPredictionManager == null) return;

        tableModel = new BaseTableModel(new Vector<>(), new Vector<>(Arrays.asList(columns)));
        table.setModel(tableModel);

        List<SpecialEventsPrediction> teamEvents = specialEventsPredictionManager.getTeamEvents();
        ArrayList<IMatchRoleID> involvedPositions;
        for ( SpecialEventsPrediction se : teamEvents){
            ArrayList<Player> involved = new ArrayList<Player>();
            involvedPositions = se.getInvolvedPositions();
            if ( involvedPositions != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getPlayer(id));
                }
            }
            involvedPositions = se.getInvolvedOpponentPositions();
            if ( involvedPositions != null){
                for ( IMatchRoleID id: involvedPositions){
                    involved.add(specialEventsPredictionManager.getOpponentPlayer(id));
                }
            }

            tableModel.addRow(
                    getRow(
                            se.getEventTypeAsString(),
                            specialEventsPredictionManager.getPlayer(se.getResponsiblePosition()),
                            null,
                            involved,
                            se.getChanceCreationProbability(),
                            se.getChanceCreationProbability()>0?se.getGoalProbability():null,
                            se.getChanceCreationProbability()>0?null:-se.getGoalProbability()
                    )
            );
        }

        List<SpecialEventsPrediction> opponentEvents = specialEventsPredictionManager.getOpponentEvents();
        for ( SpecialEventsPrediction se : opponentEvents){
            ArrayList<Player> involved = new ArrayList<Player>();

            involvedPositions = se.getInvolvedPositions();
            if ( involvedPositions != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getOpponentPlayer(id));     // SE from opponent perspective
                }
            }
            involvedPositions = se.getInvolvedOpponentPositions();
            if ( involvedPositions  != null) {
                for (IMatchRoleID id : involvedPositions) {
                    involved.add(specialEventsPredictionManager.getPlayer(id));     // SE from opponent perspective
                }
            }

            tableModel.addRow(
                    getRow(
                            se.getEventTypeAsString(),
                            null,
                            specialEventsPredictionManager.getOpponentPlayer(se.getResponsiblePosition()),
                            involved,
                            se.getChanceCreationProbability(),
                            se.getChanceCreationProbability()>0?null:-se.getGoalProbability(),
                            se.getChanceCreationProbability()>0?se.getGoalProbability():null
                    )
            );
        }

        double scores = specialEventsPredictionManager.getResultScores();
        double opponentScores = specialEventsPredictionManager.getOpponentResultScores();
        this.resultLabel.setText(String.format(hov.getLanguageString("ls.teamanalyzer.result") + ": %.2f : %.2f", scores, opponentScores));
    }

    private Vector<Object> getRow(String kind, Player player, Player opponentPlayer, ArrayList<Player> involved, double probability, Double scores, Double scoresOpponent) {

        ArrayList<String> involvedPlayerNames = new ArrayList<>();
        for ( Player p : involved){
            if ( p == null){
                continue;
            }
            involvedPlayerNames.add(p.getFullName());
        }
        Vector<Object> rowData = new Vector<>();

        rowData.add(kind);
        rowData.add(player!=null?player.getFullName():"");
        rowData.add(opponentPlayer!=null?opponentPlayer.getFullName():"");
        rowData.add(involvedPlayerNames);

        DecimalFormat df = new DecimalFormat("#.00");
        rowData.add(df.format(probability));
        rowData.add(scores!=null?df.format(scores):"");
        rowData.add(scoresOpponent!=null?df.format(scoresOpponent):"");

        return rowData;
    }

}
