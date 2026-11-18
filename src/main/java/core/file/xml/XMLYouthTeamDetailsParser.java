package core.file.xml;

import core.util.HOLogger;
import core.util.XMLUtils;
import module.youth.YouthPlace;
import module.youth.YouthScout;
import module.youth.YouthTeamDetails;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

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
            final int youthTeamId = XMLUtils.findIntegerValue(t, "YouthTeamID").orElse(0);
            if (youthTeamId == 0) {
                return null;
            }
            final var details = new YouthTeamDetails();
            details.setHrfId(hrfId);
            details.setYouthTeamId(youthTeamId);
            XMLUtils.findStringValue(t, "YouthTeamName").ifPresent(details::setYouthTeamName);
            XMLUtils.findStringValue(t, "ShortTeamName").ifPresent(details::setShortTeamName);
            XMLUtils.findHODateTimeValue(t, "CreatedDate").ifPresent(details::setCreatedDate);
            // the docs say UserID, the real 1.5 response sends UserId
            XMLUtils.findIntegerValue(t, "UserId")
                    .or(() -> XMLUtils.findIntegerValue(t, "UserID"))
                    .ifPresent(details::setUserId);
            XMLUtils.findIntegerValue(t, "GenderID").ifPresent(details::setGenderId);
            XMLUtils.findIntegerValue(t, "LeagueSystemID").ifPresent(details::setLeagueSystemId);
            XMLUtils.findStringValue(t, "IsPlayingMatch").map(Boolean::parseBoolean).ifPresent(details::setPlayingMatch);
            readPlace(t, details);
            XMLUtils.findChildElement(t, "YouthArena").ifPresent(a -> {
                XMLUtils.findIntegerValue(a, "YouthArenaID").ifPresent(details::setYouthArenaId);
                XMLUtils.findStringValue(a, "YouthArenaName").ifPresent(details::setYouthArenaName);
            });
            XMLUtils.findChildElement(t, "YouthLeague").ifPresent(l -> {
                XMLUtils.findIntegerValue(l, "YouthLeagueID").ifPresent(details::setYouthLeagueId);
                XMLUtils.findStringValue(l, "YouthLeagueName").ifPresent(details::setYouthLeagueName);
                XMLUtils.findIntegerValue(l, "YouthLeagueStatus").ifPresent(details::setYouthLeagueStatus);
            });
            XMLUtils.findChildElement(t, "OwningTeam").ifPresent(o -> {
                XMLUtils.findIntegerValue(o, "MotherTeamID").ifPresent(details::setMotherTeamId);
                XMLUtils.findStringValue(o, "MotherTeamName").ifPresent(details::setMotherTeamName);
            });
            XMLUtils.findChildElement(t, "YouthTrainer")
                    .flatMap(trainer -> XMLUtils.findIntegerValue(trainer, "YouthPlayerID"))
                    .ifPresent(details::setTrainerYouthPlayerId);
            XMLUtils.findHODateTimeValue(t, "NextTrainingMatchDate").ifPresent(details::setNextTrainingMatchDate);
            XMLUtils.findChildElement(t, "ScoutList").ifPresent(list -> {
                for (final var s : XMLUtils.getChildElements(list, "Scout")) {
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
        XMLUtils.findStringValue(s, "ScoutName").ifPresent(scout::setScoutName);
        XMLUtils.findIntegerValue(s, "Age").ifPresent(scout::setAge);
        readPlace(s, scout);
        XMLUtils.findChildElement(s, "InCountry").ifPresent(c -> {
            XMLUtils.findIntegerValue(c, COUNTRY_ID).ifPresent(scout::setInCountryId);
            XMLUtils.findStringValue(c, COUNTRY_NAME).ifPresent(scout::setInCountryName);
        });
        XMLUtils.findChildElement(s, "InRegion").ifPresent(r -> {
            XMLUtils.findIntegerValue(r, REGION_ID).ifPresent(scout::setInRegionId);
            XMLUtils.findStringValue(r, REGION_NAME).ifPresent(scout::setInRegionName);
        });
        XMLUtils.findHODateTimeValue(s, "HiredDate").ifPresent(scout::setHiredDate);
        XMLUtils.findHODateTimeValue(s, "LastCalled").ifPresent(scout::setLastCalled);
        XMLUtils.findIntegerValue(s, "PlayerTypeSearch").ifPresent(scout::setPlayerTypeSearch);
        XMLUtils.findIntegerValue(s, "HofPlayerId").ifPresent(scout::setHofPlayerId);
        XMLUtils.findChildElement(s, "Travel").ifPresent(travel -> {
            XMLUtils.findHODateTimeValue(travel, "TravelStartDate").ifPresent(scout::setTravelStartDate);
            XMLUtils.findIntegerValue(travel, "TravelLength").ifPresent(scout::setTravelLength);
            XMLUtils.findIntegerValue(travel, "TravelType").ifPresent(scout::setTravelType);
        });
        return scout;
    }

    /** Country and Region of a team or a scout. */
    private static void readPlace(Element parent, YouthPlace place) {
        XMLUtils.findChildElement(parent, COUNTRY).ifPresent(c -> {
            XMLUtils.findIntegerValue(c, COUNTRY_ID).ifPresent(place::setCountryId);
            XMLUtils.findStringValue(c, COUNTRY_NAME).ifPresent(place::setCountryName);
        });
        XMLUtils.findChildElement(parent, REGION).ifPresent(r -> {
            XMLUtils.findIntegerValue(r, REGION_ID).ifPresent(place::setRegionId);
            XMLUtils.findStringValue(r, REGION_NAME).ifPresent(place::setRegionName);
        });
    }
}
