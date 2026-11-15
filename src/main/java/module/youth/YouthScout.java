package module.youth;

import core.db.AbstractTable;
import core.util.HODateTime;
import lombok.Getter;
import lombok.Setter;

/** One of the youth team's scouts (youthteamdetails ScoutList), one snapshot per HRF. */
@Getter
@Setter
public class YouthScout extends AbstractTable.Storable implements YouthPlace {

    private int hrfId;
    private int youthScoutId;
    private String scoutName;
    private Integer age;
    /** Where the scout comes from. */
    private Integer countryId;
    private String countryName;
    private Integer regionId;
    private String regionName;
    /** Where the scout is now. */
    private Integer inCountryId;
    private String inCountryName;
    private Integer inRegionId;
    private String inRegionName;
    private HODateTime hiredDate;
    private HODateTime lastCalled;
    /** ScoutSearchTypeID: the kind of player the scout is looking for. */
    private Integer playerTypeSearch;
    /** 1.1: the hall of fame player behind the scout, 0 if none. */
    private Integer hofPlayerId;
    /** Null while the scout is not travelling. */
    private HODateTime travelStartDate;
    /** km */
    private Integer travelLength;
    /** ScoutTravelTypeID */
    private Integer travelType;
}
