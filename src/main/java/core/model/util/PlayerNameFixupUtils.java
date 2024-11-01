package core.model.util;

import core.model.match.MatchLineupPosition;
import core.util.HOLogger;
import module.teamanalyzer.vo.MatchDetail;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class PlayerNameFixupUtils {

    private PlayerNameFixupUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void fixup(MatchLineupPosition matchLineupPosition) {
        if (isEmpty(matchLineupPosition.getFirstName()) &&
            isEmpty(matchLineupPosition.getNickName()) &&
            isNotEmpty(matchLineupPosition.getLastName())) {
            HOLogger.instance().debug(MatchDetail.class, "Candidate to fix is: FirstName='%s', Nick='%s', LastName='%s'"
                .formatted(matchLineupPosition.getFirstName(), matchLineupPosition.getNickName(), matchLineupPosition.getLastName()));
            final var parts = matchLineupPosition.getLastName().split("\\s");
            if (parts.length >= 2) {
                final var newFirstName = String.join(StringUtils.SPACE, Arrays.asList(parts).subList(0, (parts.length - 1)));
                matchLineupPosition.setFirstName(newFirstName);
                matchLineupPosition.setLastName(parts[parts.length - 1]);
                HOLogger.instance().debug(MatchDetail.class, "Fixed to: FirstName='%s', Nick='%s', LastName='%s'"
                    .formatted(matchLineupPosition.getFirstName(), matchLineupPosition.getNickName(), matchLineupPosition.getLastName()));
            } else {
                HOLogger.instance().debug(MatchDetail.class, "Not fixed because last name must be split in 2 or more parts: current number %d".formatted(parts.length));
            }
        }
    }
}
