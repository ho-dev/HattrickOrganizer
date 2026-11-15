package module.youth;

import core.db.AbstractTable;
import core.util.HODateTime;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The youth team itself (youthteamdetails 1.5 with {@code showScouts}), one snapshot per HRF.
 * The containers CHPP leaves empty (no league, no owner) leave their fields null.
 */
@Getter
@Setter
public class YouthTeamDetails extends AbstractTable.Storable implements YouthPlace {

    private int hrfId;
    private int youthTeamId;
    private String youthTeamName;
    private String shortTeamName;
    private HODateTime createdDate;
    /** The owner (1.2). The real responses spell it UserId, the docs UserID. */
    private Integer userId;
    /** 1.3: 1 male, 2 female. */
    private Integer genderId;
    /** 1.4 */
    private Integer leagueSystemId;
    /** 1.5: the youth team is playing a match right now. */
    private Boolean playingMatch;
    private Integer countryId;
    private String countryName;
    private Integer regionId;
    private String regionName;
    private Integer youthArenaId;
    private String youthArenaName;
    /** Null when the team takes part in no youth league. */
    private Integer youthLeagueId;
    private String youthLeagueName;
    /** YouthLeagueStatusID. */
    private Integer youthLeagueStatus;
    private Integer motherTeamId;
    private String motherTeamName;
    /** The youth player who is the coach. */
    private Integer trainerYouthPlayerId;
    /**
     * When the next friendly can be booked: before today if it can be booked now, more than three
     * weeks ahead if one is booked already.
     */
    private HODateTime nextTrainingMatchDate;

    private List<YouthScout> scouts = new ArrayList<>();
}
