package core.file.xml;

import core.util.HODateTime;
import core.util.HOLogger;
import core.util.XMLUtils;
import module.youth.YouthPlace;
import module.youth.YouthScout;
import module.youth.YouthTeamDetails;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * youthteamdetails 1.5 with {@code showScouts=true}. Hattrick answers a club without a youth team
 * with an empty team of id 0 instead of an error; that reads as null.
 */
public final class XMLYouthTeamDetailsParser {

    private static final String COUNTRY = "Country";
    private static final String COUNTRY_ID = "CountryID";
    private static final String COUNTRY_NAME = "CountryName";
    private static final String REGION = "Region";
    private static final String REGION_ID = "RegionID";
    private static final String REGION_NAME = "RegionName";

    private XMLYouthTeamDetailsParser() {
    }

    /** @return the team, or null for no youth team, an empty response or a CHPP error */
    public static @Nullable YouthTeamDetails parse(String xml, int hrfId) {
        final var doc = XMLManager.parseString(xml);
        if (doc == null) {
            return null;
        }
        try {
            final var team = XMLUtils.findChildElement(doc.getDocumentElement(), "YouthTeam");
            if (team.isEmpty()) {
                return null;
            }
            final var t = team.get();
            final var youthTeamId = optInt(t, "YouthTeamID");
            if (youthTeamId == null || youthTeamId == 0) {
                return null;
            }
            final var details = new YouthTeamDetails();
            details.setHrfId(hrfId);
            details.setYouthTeamId(youthTeamId);
            details.setYouthTeamName(optString(t, "YouthTeamName"));
            details.setShortTeamName(optString(t, "ShortTeamName"));
            details.setCreatedDate(optDate(t, "CreatedDate"));
            // the docs say UserID, the real 1.5 response sends UserId
            details.setUserId(Optional.ofNullable(optInt(t, "UserId")).orElse(optInt(t, "UserID")));
            details.setGenderId(optInt(t, "GenderID"));
            details.setLeagueSystemId(optInt(t, "LeagueSystemID"));
            details.setPlayingMatch(Optional.ofNullable(optString(t, "IsPlayingMatch")).map(Boolean::parseBoolean).orElse(null));
            readPlace(t, details);
            idName(t, "YouthArena", "YouthArenaID", "YouthArenaName").ifPresent(a -> {
                details.setYouthArenaId(a.id());
                details.setYouthArenaName(a.name());
            });
            child(t, "YouthLeague").ifPresent(l -> {
                details.setYouthLeagueId(optInt(l, "YouthLeagueID"));
                details.setYouthLeagueName(optString(l, "YouthLeagueName"));
                details.setYouthLeagueStatus(optInt(l, "YouthLeagueStatus"));
            });
            child(t, "OwningTeam").ifPresent(o -> {
                details.setMotherTeamId(optInt(o, "MotherTeamID"));
                details.setMotherTeamName(optString(o, "MotherTeamName"));
            });
            child(t, "YouthTrainer").ifPresent(trainer -> details.setTrainerYouthPlayerId(optInt(trainer, "YouthPlayerID")));
            details.setNextTrainingMatchDate(optDate(t, "NextTrainingMatchDate"));
            child(t, "ScoutList").ifPresent(list -> {
                for (var s : XMLUtils.getChildElements(list, "Scout")) {
                    details.getScouts().add(scout(s, hrfId));
                }
            });
            return details;
        } catch (Exception e) {
            HOLogger.instance().log(XMLYouthTeamDetailsParser.class, e);
            return null;
        }
    }

    private static YouthScout scout(Element s, int hrfId) {
        final var scout = new YouthScout();
        scout.setHrfId(hrfId);
        scout.setYouthScoutId(XMLUtils.getIntValue(s, "YouthScoutID"));
        scout.setScoutName(optString(s, "ScoutName"));
        scout.setAge(optInt(s, "Age"));
        readPlace(s, scout);
        idName(s, "InCountry", COUNTRY_ID, COUNTRY_NAME).ifPresent(c -> {
            scout.setInCountryId(c.id());
            scout.setInCountryName(c.name());
        });
        idName(s, "InRegion", REGION_ID, REGION_NAME).ifPresent(r -> {
            scout.setInRegionId(r.id());
            scout.setInRegionName(r.name());
        });
        scout.setHiredDate(optDate(s, "HiredDate"));
        scout.setLastCalled(optDate(s, "LastCalled"));
        scout.setPlayerTypeSearch(optInt(s, "PlayerTypeSearch"));
        scout.setHofPlayerId(optInt(s, "HofPlayerId"));
        child(s, "Travel").ifPresent(travel -> {
            scout.setTravelStartDate(optDate(travel, "TravelStartDate"));
            scout.setTravelLength(optInt(travel, "TravelLength"));
            scout.setTravelType(optInt(travel, "TravelType"));
        });
        return scout;
    }

    private static Optional<Element> child(Element parent, String tagName) {
        return XMLUtils.findChildElement(parent, tagName);
    }

    private record IdName(@Nullable Integer id, @Nullable String name) {
    }

    /** Country and Region of a team or a scout. */
    private static void readPlace(Element parent, YouthPlace place) {
        idName(parent, COUNTRY, COUNTRY_ID, COUNTRY_NAME).ifPresent(c -> {
            place.setCountryId(c.id());
            place.setCountryName(c.name());
        });
        idName(parent, REGION, REGION_ID, REGION_NAME).ifPresent(r -> {
            place.setRegionId(r.id());
            place.setRegionName(r.name());
        });
    }

    /** A container of an id and a name, such as Country with CountryID and CountryName. */
    private static Optional<IdName> idName(Element parent, String container, String idTag, String nameTag) {
        return child(parent, container).map(c -> new IdName(optInt(c, idTag), optString(c, nameTag)));
    }

    static @Nullable String optString(Element parent, String tagName) {
        final var text = XMLUtils.getStringValue(parent, tagName, "").trim();
        return text.isEmpty() ? null : text;
    }

    static @Nullable Integer optInt(Element parent, String tagName) {
        final var text = optString(parent, tagName);
        return text == null ? null : Integer.valueOf(text);
    }

    static @Nullable HODateTime optDate(Element parent, String tagName) {
        final var text = optString(parent, tagName);
        return text == null ? null : HODateTime.fromHT(text);
    }
}
