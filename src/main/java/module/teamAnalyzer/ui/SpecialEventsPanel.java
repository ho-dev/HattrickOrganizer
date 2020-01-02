package module.teamAnalyzer.ui;

import core.gui.model.BaseTableModel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.specialevents.SpecialEventsPrediction;
import core.specialevents.SpecialEventsPredictionManager;
import core.util.Helper;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class SpecialEventsPanel extends JPanel {
    private JTable table;
    private BaseTableModel tableModel;

    private String[] columns = {
            "Kind",
            "Player",
            "Opponent Player",
            "Involved Player",
            "Prob.",
            "Scores",
            "Opponent Scores"
    };



    public SpecialEventsPanel(){
        Vector<Object> data = new Vector<Object>();

        tableModel = new BaseTableModel(data, new Vector<String>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        //table.setDefaultRenderer(Object.class, new RatingTableCellRenderer());

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
    }

    public void reload(SpecialEventsPredictionManager specialEventsPredictionManager) {
        tableModel = new BaseTableModel(new Vector<Object>(), new Vector<String>(Arrays.asList(columns)));
        table.setModel(tableModel);

        if (specialEventsPredictionManager == null) {
            return;
        }

        List<SpecialEventsPrediction> teamEvents = specialEventsPredictionManager.getTeamEvents();
        for ( SpecialEventsPrediction se : teamEvents){

            HashSet<Player> involved = new HashSet<Player>();
            for ( IMatchRoleID id: se.getInvolvedPositions()){
                involved.add(specialEventsPredictionManager.getPlayer(id));
            }
            for ( IMatchRoleID id: se.getInvolvedOpponentPositions()){
                involved.add(specialEventsPredictionManager.getOpponentPlayer(id));
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

            HashSet<Player> involved = new HashSet<Player>();
            for ( IMatchRoleID id: se.getInvolvedPositions()){
                involved.add(specialEventsPredictionManager.getOpponentPlayer(id));     // SE from opponent perspective
            }
            for ( IMatchRoleID id: se.getInvolvedOpponentPositions()){
                involved.add(specialEventsPredictionManager.getPlayer(id));     // SE from opponent perspective
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


        table.getTableHeader().getColumnModel().getColumn(0).setWidth(130);
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getTableHeader().getColumnModel().getColumn(1).setWidth(100);
        table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(100);
    }

    private Vector<Object> getRow(String kind, Player player, Player opponentPlayer, HashSet<Player> involved, double propability, double scores, double scoresOpponent) {

        Vector<String> involvedPlayerNames = new Vector<>();
        for ( Player p : involved){
            involvedPlayerNames.add(p.getName());
        }
        Vector<Object> rowData = new Vector<Object>();

        rowData.add(kind);
        rowData.add(player!=null?player.getName():"");
        rowData.add(opponentPlayer!=null?opponentPlayer.getName():"");
        rowData.add(involvedPlayerNames);

        DecimalFormat df = new DecimalFormat("#.00");
        rowData.add(df.format(propability));
        rowData.add(df.format(scores));
        rowData.add(df.format(scoresOpponent));

        return rowData;
    }

}
