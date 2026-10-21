package core.model.util;

import core.model.match.MatchLineupPosition;
import core.util.HOLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

public final class PlayerNameFixupUtils {

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^(.+?)\\s+'([^']+)'\\s+(.+)$");
    private static final int INDEX_FIRST_NAME = 1;
    private static final int INDEX_NICK_NAME = 2;
    private static final int INDEX_LAST_NAME = 3;

    public record PlayerName(String firstName, String nickName, String lastName) {
    }

    private PlayerNameFixupUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void fixup(MatchLineupPosition matchLineupPosition) {
        if (isEmpty(matchLineupPosition.getFirstName()) &&
            isEmpty(matchLineupPosition.getNickName()) &&
            isNotEmpty(matchLineupPosition.getLastName())) {
            HOLogger.instance().debug(PlayerNameFixupUtils.class, "Candidate to fix is: FirstName='%s', Nick='%s', LastName='%s'"
                .formatted(matchLineupPosition.getFirstName(), matchLineupPosition.getNickName(), matchLineupPosition.getLastName()));

            final var playerName = splitName(matchLineupPosition.getLastName());
            if (isNotBlank(playerName.firstName())) {
                HOLogger.instance().debug(PlayerNameFixupUtils.class, "Fixing player name '%s' to %s".formatted(matchLineupPosition.getLastName(), playerName));

                matchLineupPosition.setFirstName(playerName.firstName());
                matchLineupPosition.setNickName(playerName.nickName());
                matchLineupPosition.setLastName(playerName.lastName());
                return;
            }

            HOLogger.instance().debug(PlayerNameFixupUtils.class, "Could not reliably split player name: '%s'".formatted(matchLineupPosition.getLastName()));
        }
    }

    public static PlayerName splitName(String fullName) {
        final String name = StringUtils.trimToEmpty(fullName);

        final Matcher matcher = NICKNAME_PATTERN.matcher(name);
        if (matcher.matches()) {
            final var firstName = StringUtils.trim(matcher.group(INDEX_FIRST_NAME));
            final var nickName = StringUtils.trim(matcher.group(INDEX_NICK_NAME));
            final var lastName = StringUtils.trim(matcher.group(INDEX_LAST_NAME));
            return new PlayerName(firstName, nickName, lastName);
        }

        final String[] parts = StringUtils.split(name);
        if (parts != null && parts.length == 2) {
            return new PlayerName(parts[0], EMPTY, parts[1]);
        }

        return new PlayerName(EMPTY, EMPTY, name);
    }
}
