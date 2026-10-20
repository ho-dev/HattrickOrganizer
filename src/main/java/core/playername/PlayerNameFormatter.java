package core.playername;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerNameFormatter {

    private final List<PlayerNameWrapper> playerNameWrappers;
    private final Set<Integer> knownPlayerIds;
    private final Set<String> duplicateNickNames;
    private final Map<String, List<PlayerNameWrapper>> mapLastNameToPlayerNameWrappers;

    public enum NameFormat {
        HATTRICK,
        FULL_FIRST_LAST,
        FULL_LAST_FIRST,
        SMART_FIRST_LAST,
        SMART_LAST_FIRST
    }

    private record PlayerNameWrapper(PlayerName playerName) {
        public int getPlayerId() {
            return playerName.getPlayerId();
        }

        public String getFirstName() {
            return findFirstName().orElseThrow(() -> new NoSuchElementException("Expected player %d to have a first name".formatted(playerName.getPlayerId())));
        }

        public Optional<String> findFirstName() {
            return Optional.ofNullable(playerName.getFirstName()).filter(StringUtils::isNotBlank).map(String::strip);
        }

        public String getLastName() {
            return findLastName().orElseThrow(() -> new NoSuchElementException("Expected player %d to have a last name".formatted(playerName.getPlayerId())));
        }

        public Optional<String> findLastName() {
            return Optional.ofNullable(playerName.getLastName()).filter(StringUtils::isNotBlank).map(String::strip);
        }

        public String getNickName() {
            return findNickName().orElseThrow(() -> new NoSuchElementException("Expected player %d to have a nickname".formatted(playerName.getPlayerId())));
        }

        public Optional<String> findNickName() {
            return Optional.ofNullable(playerName.getNickName()).filter(StringUtils::isNotBlank).map(String::strip);
        }
    }

    public PlayerNameFormatter(List<? extends PlayerName> playerNames) {
        final var validatedPlayerNames = toValidatedUniquePlayerNames(playerNames);
        this.knownPlayerIds = validatedPlayerNames.stream().map(PlayerName::getPlayerId).collect(Collectors.toSet());
        this.playerNameWrappers = validatedPlayerNames.stream().map(PlayerNameWrapper::new).collect(Collectors.toList());
        this.duplicateNickNames = calcDuplicates(PlayerNameWrapper::findNickName);
        this.mapLastNameToPlayerNameWrappers = calcMapLastNameToPlayers();
    }

    private static List<? extends PlayerName> toValidatedUniquePlayerNames(List<? extends PlayerName> originalPlayerNames) {
        final var mapPlayerIdToPlayerNames = originalPlayerNames.stream().collect(Collectors.groupingBy(PlayerName::getPlayerId));
        mapPlayerIdToPlayerNames.forEach((playerId, playerNames) -> {
                if (playerNames.size() > 1) {
                    final var firstPlayerName = playerNames.get(0);
                    final var firstUnequal = playerNames.stream()
                        .skip(1)
                        .filter(playerName -> !allNamesEqual(firstPlayerName, playerName))
                        .findFirst();
                    if (firstUnequal.isPresent()) {

                        throw new IllegalArgumentException(
                            "Conflicting name data for player ID %d: %s vs %s".formatted(playerId,
                                playerNameToString(firstPlayerName),
                                playerNameToString(firstUnequal.orElseThrow())
                            )
                        );
                    }
                }
            }
        );

        return mapPlayerIdToPlayerNames.values().stream().map(v -> v.get(0)).collect(Collectors.toList());
    }

    private static String playerNameToString(PlayerName playerName) {
        return "ID %d as [%s], [%s] with Nickname [%s]".formatted(
            playerName.getPlayerId(),
            playerName.getLastName(),
            playerName.getFirstName(),
            playerName.getNickName()
        );
    }

    private static boolean allNamesEqual(PlayerName lhs, PlayerName rhs) {
        return Objects.equals(lhs.getFirstName(), rhs.getFirstName()) &&
            Objects.equals(lhs.getLastName(), rhs.getLastName()) &&
            Objects.equals(lhs.getNickName(), rhs.getNickName());
    }

    public String format(PlayerName playerName, NameFormat nameFormat) {
        if (!knownPlayerIds.contains(playerName.getPlayerId())) {
            throw new IllegalStateException("Player id %d is unknown in formatter context.".formatted(playerName.getPlayerId()));
        }

        final var playerNameWrapper = new PlayerNameWrapper(playerName);
        return switch (nameFormat) {
            case HATTRICK -> formatHattrick(playerNameWrapper);
            case FULL_FIRST_LAST -> formatFullFirstLast(playerNameWrapper);
            case FULL_LAST_FIRST -> formatFullLastFirst(playerNameWrapper);
            case SMART_FIRST_LAST -> formatSmartFirstLast(playerNameWrapper);
            case SMART_LAST_FIRST -> formatSmartLastFirst(playerNameWrapper);
        };
    }

    private String formatHattrick(PlayerNameWrapper playerNameWrapper) {
        return formatFullFirstLast(playerNameWrapper);
    }

    private String formatFullFirstLast(PlayerNameWrapper playerNameWrapper) {
        final var firstName = playerNameWrapper.findFirstName();
        final var lastName = playerNameWrapper.findLastName();
        return playerNameWrapper.findNickName()
            .map(nickName -> {
                    if (firstName.isPresent() && lastName.isPresent()) {
                        return "%s %s %s".formatted(firstName.orElseThrow(), formatNickName(nickName), lastName.orElseThrow());
                    } else if (firstName.isPresent()) {
                        return "%s %s".formatted(firstName.orElseThrow(), formatNickName(nickName));
                    } else if (lastName.isPresent()) {
                        return "%s %s".formatted(formatNickName(nickName), lastName.orElseThrow());
                    } else {
                        return formatNickName(nickName);
                    }
                }
            )
            .orElseGet(
                () -> {
                    if (firstName.isPresent() && lastName.isPresent()) {
                        return "%s %s".formatted(firstName.orElseThrow(), lastName.orElseThrow());
                    } else if (firstName.isPresent()) {
                        return firstName.orElseThrow();
                    } else if (lastName.isPresent()) {
                        return lastName.orElseThrow();
                    } else {
                        return formatPlayerId(playerNameWrapper.getPlayerId());
                    }
                });
    }

    private String formatFullLastFirst(PlayerNameWrapper playerNameWrapper) {
        final var firstName = playerNameWrapper.findFirstName();
        final var lastName = playerNameWrapper.findLastName();
        return playerNameWrapper.findNickName()
            .map(nickName -> {
                    if (firstName.isPresent() && lastName.isPresent()) {
                        return "%s, %s %s".formatted(lastName.orElseThrow(), firstName.orElseThrow(), formatNickName(nickName));
                    } else if (firstName.isPresent()) {
                        return "%s %s".formatted(firstName.orElseThrow(), formatNickName(nickName));
                    } else if (lastName.isPresent()) {
                        return "%s %s".formatted(lastName.orElseThrow(), formatNickName(nickName));
                    } else {
                        return formatNickName(nickName);
                    }
                }
            )
            .orElseGet(
                () -> {
                    if (firstName.isPresent() && lastName.isPresent()) {
                        return "%s, %s".formatted(lastName.orElseThrow(), firstName.orElseThrow());
                    } else if (firstName.isPresent()) {
                        return firstName.orElseThrow();
                    } else if (lastName.isPresent()) {
                        return lastName.orElseThrow();
                    } else {
                        return formatPlayerId(playerNameWrapper.getPlayerId());
                    }
                });
    }

    private String formatSmartFirstLast(PlayerNameWrapper playerNameWrapper) {
        return formatSmart(playerNameWrapper, false);
    }

    private String formatSmartLastFirst(PlayerNameWrapper playerNameWrapper) {
        return formatSmart(playerNameWrapper, true);
    }

    private String formatSmart(PlayerNameWrapper playerNameWrapper, boolean lastNameFirst) {
        if (nickNameUnique(playerNameWrapper)) {
            return formatNickName(playerNameWrapper);
        }

        if (lastNameUnique(playerNameWrapper)) {
            return playerNameWrapper.getLastName();
        }

        final var optFirstName = playerNameWrapper.findFirstName();
        final var optLastName = playerNameWrapper.findLastName();

        if (optFirstName.isPresent() && optLastName.isPresent()) {
            final String firstName = optFirstName.get();
            final String lastName = optLastName.get();

            final var otherWithSameLastName = getPlayersWithSameLastName(lastName).stream()
                .filter(other -> other.getPlayerId() != playerNameWrapper.getPlayerId()).toList();
            final var otherFirstNames = otherWithSameLastName.stream()
                .map(PlayerNameWrapper::findFirstName)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());

            if (otherFirstNames.contains(firstName)) {
                return "%s %s".formatted(formatSmartName(firstName, lastName, lastNameFirst), formatPlayerId(playerNameWrapper.getPlayerId()));
            }

            // Find the shortest unique first name prefix.
            for (int length = 1; length < firstName.length(); length++) {
                final String prefix = firstName.substring(0, length);

                final boolean unique = otherFirstNames.stream().noneMatch(name -> name.startsWith(prefix));

                if (unique) {
                    return formatSmartName(prefix + ".", lastName, lastNameFirst);
                }
            }

            return formatSmartName(firstName, lastName, lastNameFirst);
        } else if (optLastName.isPresent()) {
            final String lastName = optLastName.get();
            return "%s %s".formatted(lastName, formatPlayerId(playerNameWrapper.getPlayerId()));
        } else if (optFirstName.isPresent()) {
            final String firstName = optFirstName.get();
            return "%s %s".formatted(firstName, formatPlayerId(playerNameWrapper.getPlayerId()));
        } else {
            return formatPlayerId(playerNameWrapper.getPlayerId());
        }
    }

    private static String formatSmartName(String firstName, String lastName, boolean lastNameFirst) {
        return lastNameFirst ? "%s, %s".formatted(lastName, firstName) : "%s %s".formatted(firstName, lastName);
    }

    private static String formatNickName(PlayerNameWrapper playerName) {
        return formatNickName(playerName.getNickName());
    }

    private static String formatNickName(String nickName) {
        return "'%s'".formatted(nickName);
    }

    private static String formatPlayerId(int playerId) {
        return "(%d)".formatted(playerId);
    }

    private boolean nickNameUnique(PlayerNameWrapper playerName) {
        return playerName.findNickName().map(this::nickNameUnique).orElse(false);
    }

    private boolean nickNameUnique(String nickName) {
        Objects.requireNonNull(nickName);
        return !duplicateNickNames.contains(nickName);
    }

    private boolean lastNameUnique(PlayerNameWrapper playerName) {
        return playerName.findLastName().map(this::lastNameUnique).orElse(false);
    }

    private boolean lastNameUnique(String lastName) {
        if (StringUtils.isBlank(lastName)) {
            return false;
        }

        final var withSameLastName = getPlayersWithSameLastName(lastName);
        return withSameLastName.size() == 1;
    }

    private Set<String> calcDuplicates(Function<PlayerNameWrapper, Optional<String>> nameExtractor) {
        return playerNameWrappers.stream()
            .map(nameExtractor)
            .filter(Optional::isPresent)
            .map(Optional::orElseThrow)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private Map<String, List<PlayerNameWrapper>> calcMapLastNameToPlayers() {
        return playerNameWrappers.stream()
            .filter(playerNameWrapper -> playerNameWrapper.findLastName().isPresent())
            .collect(Collectors.groupingBy(PlayerNameWrapper::getLastName));
    }

    private List<PlayerNameWrapper> getPlayersWithSameLastName(String lastName) {
        return Objects.requireNonNull(mapLastNameToPlayerNameWrappers.get(lastName), "Last name not found: %s".formatted(lastName));
    }
}
