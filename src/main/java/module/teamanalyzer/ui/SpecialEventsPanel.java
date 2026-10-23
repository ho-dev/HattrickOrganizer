package module.teamanalyzer.ui;

import core.gui.model.BaseTableModel;
import core.model.TranslationFacility;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.playername.NameFormat;
import core.playername.PlayerNameFormatter;
import core.playername.PlayerNameFormatterFactory;
import core.playername.PlayerNameUtils;
import core.specialevents.SpecialEventsPrediction;
import core.specialevents.SpecialEventsPredictionManager;
import core.util.HOLogger;
import core.util.StreamUtils;
import module.teamanalyzer.vo.TeamLineup;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SpecialEventsPanel extends JPanel {
    private final JTable table;
    private BaseTableModel tableModel;
    private final JLabel resultLabel;
    private final NameFormat nameFormat;

    private final String[] columns = {
            TranslationFacility.tr("Event"),
            TranslationFacility.tr("Spieler"),
            TranslationFacility.tr("ls.teamanalyzer.opponent_player"),
            TranslationFacility.tr("ls.teamanalyzer.involved_player"),
            TranslationFacility.tr("ls.teamanalyzer.probability"),
            TranslationFacility.tr("ls.teamanalyzer.scores"),
            TranslationFacility.tr("ls.teamanalyzer.opponent_scores")
    };

    public SpecialEventsPanel(NameFormat nameFormat) {
        this.nameFormat = nameFormat;
        Vector<Vector<Object>> data = new Vector<>();

        tableModel = new BaseTableModel(data, new Vector<>(Arrays.asList(columns)));
        table = new JTable(tableModel);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(TranslationFacility.tr("ls.teamanalyzer.special_events")));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);

        resultLabel = new JLabel( TranslationFacility.tr("ls.teamanalyzer.result") + ": 0.00 - 0.00");
        add(resultLabel, BorderLayout.PAGE_END);
    }

    public void reload(TeamLineup teamLineup) {
        tableModel.removeAllRows();
        if ( teamLineup==null) return;
        SpecialEventsPredictionManager specialEventsPredictionManager = teamLineup.getSpecialEventsPrediction();
        if (specialEventsPredictionManager == null) return;

        tableModel = new BaseTableModel(new Vector<>(), new Vector<>(Arrays.asList(columns)));
        table.setModel(tableModel);

        List<SpecialEventsPrediction> teamEvents = specialEventsPredictionManager.getTeamEvents();
        List<SpecialEventsPrediction> opponentEvents = specialEventsPredictionManager.getOpponentEvents();

        // TODO: Context is all positions not only involved team/opponent players
        final var allPlayers = StreamUtils.concat(
                Optional.ofNullable(teamEvents).stream().flatMap(events -> events.stream()
                    .flatMap(sep ->
                        StreamUtils.concat(
                            Stream.of(sep.getResponsiblePosition()).map(specialEventsPredictionManager::getPlayer),
                            getAllInvolvedPlayersOfTeamEvents(sep, specialEventsPredictionManager).stream())
                    )),
                Optional.ofNullable(opponentEvents).stream().flatMap(events -> events.stream()
                    .flatMap(sep ->
                        StreamUtils.concat(
                            Stream.of(sep.getResponsiblePosition()).map(specialEventsPredictionManager::getOpponentPlayer),
                            getAllInvolvedPlayersOfOpponentEvents(sep, specialEventsPredictionManager).stream())
                    )))
            .toList();
        final var playerNameFormatter = PlayerNameFormatterFactory.createFromPlayers(allPlayers);

        if (teamEvents != null) {
            for (SpecialEventsPrediction se : teamEvents) {
                final var involved = getAllInvolvedPlayersOfTeamEvents(se, specialEventsPredictionManager);

                tableModel.addRow(
                    createRowData(
                        se.getEventTypeAsString(),
                        specialEventsPredictionManager.getPlayer(se.getResponsiblePosition()),
                        null,
                        involved,
                        se.getChanceCreationProbability(),
                        se.getChanceCreationProbability() > 0 ? se.getGoalProbability() : null,
                        se.getChanceCreationProbability() > 0 ? null : -se.getGoalProbability(),
                        playerNameFormatter, nameFormat)
                );
            }
        }

        if (opponentEvents != null) {
            for (SpecialEventsPrediction se : opponentEvents) {
                final var involved = getAllInvolvedPlayersOfOpponentEvents(se, specialEventsPredictionManager);

                tableModel.addRow(
                    createRowData(
                        se.getEventTypeAsString(),
                        null,
                        specialEventsPredictionManager.getOpponentPlayer(se.getResponsiblePosition()),
                        involved,
                        se.getChanceCreationProbability(),
                        se.getChanceCreationProbability() > 0 ? null : -se.getGoalProbability(),
                        se.getChanceCreationProbability() > 0 ? se.getGoalProbability() : null,
                        playerNameFormatter, nameFormat)
                );
            }
        }

        double scores = specialEventsPredictionManager.getResultScores();
        double opponentScores = specialEventsPredictionManager.getOpponentResultScores();
        this.resultLabel.setText(String.format(TranslationFacility.tr("ls.teamanalyzer.result") + ": %.2f : %.2f", scores, opponentScores));
    }

    private static List<Player> getAllInvolvedPlayersOfTeamEvents(SpecialEventsPrediction se, SpecialEventsPredictionManager specialEventsPredictionManager) {
        return StreamUtils.concat(
                mapToPlayers(se.getInvolvedPositions(), specialEventsPredictionManager).stream(),
                mapToOpponentPlayers(se.getInvolvedOpponentPositions(), specialEventsPredictionManager).stream())
            .toList();
    }

    private static List<Player> getAllInvolvedPlayersOfOpponentEvents(SpecialEventsPrediction se, SpecialEventsPredictionManager specialEventsPredictionManager) {
        return StreamUtils.concat(
                mapToOpponentPlayers(se.getInvolvedPositions(), specialEventsPredictionManager).stream(),
                mapToPlayers(se.getInvolvedOpponentPositions(), specialEventsPredictionManager).stream())
            .toList();
    }

    private static List<Player> mapToPlayers(List<IMatchRoleID> iMatchRoleIDs, SpecialEventsPredictionManager specialEventsPredictionManager) {
        if (iMatchRoleIDs == null) {
            return new ArrayList<>();
        }
        return iMatchRoleIDs.stream().map(specialEventsPredictionManager::getPlayer).toList();
    }

    private static List<Player> mapToOpponentPlayers(List<IMatchRoleID> iMatchRoleIDs, SpecialEventsPredictionManager specialEventsPredictionManager) {
        if (iMatchRoleIDs == null) {
            return new ArrayList<>();
        }
        return iMatchRoleIDs.stream().map(specialEventsPredictionManager::getOpponentPlayer).toList();
    }

    private static Vector<Object> createRowData(String kind, Player player, Player opponentPlayer, List<Player> involved,
                                                double probability, Double scores, Double scoresOpponent,
                                                PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        final String playerName = getOptionalFormattedPlayerName(player, playerNameFormatter, nameFormat);
        final String opponentPlayerName = getOptionalFormattedPlayerName(opponentPlayer, playerNameFormatter, nameFormat);
        final String involvedPlayerNamesStr = createFormattedPlayerNamesListString(involved, playerNameFormatter, nameFormat);

        Vector<Object> rowData = new Vector<>();
        rowData.add(kind);
        rowData.add(playerName);
        rowData.add(opponentPlayerName);
        HOLogger.instance().info(SpecialEventsPanel.class, "opponentPlayer: %s".formatted(Optional.ofNullable(opponentPlayer).map(Player::getFullName).orElse("")));
        rowData.add(involvedPlayerNamesStr);

        DecimalFormat df = new DecimalFormat("#.00");
        rowData.add(df.format(probability));
        rowData.add(scores!=null?df.format(scores):"");
        rowData.add(scoresOpponent!=null?df.format(scoresOpponent):"");

        return rowData;
    }

    private static String createFormattedPlayerNamesListString(List<Player> players, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        return players.stream()
            .filter(Objects::nonNull)
            .map(player -> getFormattedPlayerName(player, playerNameFormatter, nameFormat))
            .collect(Collectors.joining("; "));
    }

    private static String getOptionalFormattedPlayerName(Player player, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        return Optional.ofNullable(player).map(p -> getFormattedPlayerName(p, playerNameFormatter, nameFormat)).orElse(null);
    }

    private static String getFormattedPlayerName(Player player, PlayerNameFormatter playerNameFormatter, NameFormat nameFormat) {
        return playerNameFormatter.format(PlayerNameUtils.createPlayerName(player), nameFormat);
    }
}
