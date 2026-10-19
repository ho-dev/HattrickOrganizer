package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static core.model.match.MatchKurzInfo.FINISHED;
import static core.model.match.MatchKurzInfo.UPCOMING;
import static org.assertj.core.api.Assertions.assertThat;

class XMLMatchesParserTest {

    final String xmlFileMatches = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
  <FileName>matches.xml</FileName>
  <Version>2.9</Version>
  <UserID>1757369</UserID>
  <FetchedDate>2026-10-18 13:07:56</FetchedDate>
  <IsYouth>False</IsYouth>
  <Team>
    <TeamID>520472</TeamID>
    <TeamName>Juventus Brenk</TeamName>
    <ShortTeamName>Juventus</ShortTeamName>
    <League>
      <LeagueID>3</LeagueID>
      <LeagueName>Deutschland</LeagueName>
    </League>
    <LeagueLevelUnit>
      <LeagueLevelUnitID>6253</LeagueLevelUnitID>
      <LeagueLevelUnitName>V.162</LeagueLevelUnitName>
      <LeagueLevel>5</LeagueLevel>
    </LeagueLevelUnit>
    <MatchList>
      <Match>
        <MatchID>771578631</MatchID>
        <HomeTeam>
          <HomeTeamID>396488</HomeTeamID>
          <HomeTeamName>tawşan</HomeTeamName>
          <HomeTeamNameShortName>tawşan</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-23 08:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768443696</MatchID>
        <HomeTeam>
          <HomeTeamID>977497</HomeTeamID>
          <HomeTeamName>Hotzenwälder SC</HomeTeamName>
          <HomeTeamNameShortName>Hotzenwälder</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-26 18:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>6253</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771662522</MatchID>
        <HomeTeam>
          <HomeTeamID>520472</HomeTeamID>
          <HomeTeamName>Juventus Brenk</HomeTeamName>
          <HomeTeamNameShortName>Juventus</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2318194</AwayTeamID>
          <AwayTeamName>Ruti88</AwayTeamName>
          <AwayTeamNameShortName>Ruti88</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-29 18:25:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768443702</MatchID>
        <HomeTeam>
          <HomeTeamID>1247727</HomeTeamID>
          <HomeTeamName>FC Zwietracht Cuxhaven</HomeTeamName>
          <HomeTeamNameShortName>CUX</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-03 18:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>6253</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771747669</MatchID>
        <HomeTeam>
          <HomeTeamID>2825742</HomeTeamID>
          <HomeTeamName>Macarrisme Català</HomeTeamName>
          <HomeTeamNameShortName>Macarrisme</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 15:15:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768443705</MatchID>
        <HomeTeam>
          <HomeTeamID>520472</HomeTeamID>
          <HomeTeamName>Juventus Brenk</HomeTeamName>
          <HomeTeamNameShortName>Juventus</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>130796</AwayTeamID>
          <AwayTeamName>Galatasaray-SC</AwayTeamName>
          <AwayTeamNameShortName>GS</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-10 18:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>6253</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771832586</MatchID>
        <HomeTeam>
          <HomeTeamID>2189893</HomeTeamID>
          <HomeTeamName>Balón a Tierra</HomeTeamName>
          <HomeTeamNameShortName>BaT</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 12:25:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768443710</MatchID>
        <HomeTeam>
          <HomeTeamID>824658</HomeTeamID>
          <HomeTeamName>SK Lation 16</HomeTeamName>
          <HomeTeamNameShortName>SKL</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 18:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>6253</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771917205</MatchID>
        <HomeTeam>
          <HomeTeamID>2832566</HomeTeamID>
          <HomeTeamName>De Soepsterren</HomeTeamName>
          <HomeTeamNameShortName>Soepsterren</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>520472</AwayTeamID>
          <AwayTeamName>Juventus Brenk</AwayTeamName>
          <AwayTeamNameShortName>Juventus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-21 11:15:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <OrdersGiven>True</OrdersGiven>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>768443715</MatchID>
        <HomeTeam>
          <HomeTeamID>520472</HomeTeamID>
          <HomeTeamName>Juventus Brenk</HomeTeamName>
          <HomeTeamNameShortName>Juventus</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>339078</AwayTeamID>
          <AwayTeamName>Haake-Becker Jungs</AwayTeamName>
          <AwayTeamNameShortName>Haake-Becker</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-24 18:00:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>6253</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <OrdersGiven>False</OrdersGiven>
        <Status>UPCOMING</Status>
      </Match>
    </MatchList>
  </Team>
</HattrickData>
        """;

    @Test
    void parseMatchesFromString() {
        // when
        final var matches = XMLMatchesParser.parseMatchesFromString(xmlFileMatches);
        assertThat(matches).isNotNull();

        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(771578631, 396488, "tawşan", 520472, "Juventus Brenk", "2026-09-23 08:00:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443696, 977497, "Hotzenwälder SC", 520472, "Juventus Brenk", "2026-09-26 18:00:00", 1, 0, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(771662522, 520472, "Juventus Brenk", 2318194, "Ruti88", "2026-09-29 18:25:00", 9, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443702, 1247727, "FC Zwietracht Cuxhaven", 520472, "Juventus Brenk", "2026-10-03 18:00:00", 1, 0, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(771747669, 2825742, "Macarrisme Català", 520472, "Juventus Brenk", "2026-10-07 15:15:00", 9, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443705, 520472, "Juventus Brenk", 130796, "Galatasaray-SC", "2026-10-10 18:00:00", 1, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(771832586, 2189893, "Balón a Tierra", 520472, "Juventus Brenk", "2026-10-14 12:25:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443710, 824658, "SK Lation 16", 520472, "Juventus Brenk", "2026-10-17 18:00:00", 1, 0, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(771917205, 2832566, "De Soepsterren", 520472, "Juventus Brenk", "2026-10-21 11:15:00", 9, 0, 0, 0, -1, -1, UPCOMING, true));
        expectedMatches.add(createMatch(768443715, 520472, "Juventus Brenk", 339078, "Haake-Becker Jungs", "2026-10-24 18:00:00", 1, 0, 0, 0, -1, -1, UPCOMING, false));

        assertThat(matches.size()).isEqualTo(expectedMatches.size());
        for (int i = 0; i < matches.size(); i++) {
            var expectedMatch = expectedMatches.get(i);
            var actualMatch = matches.get(i);

            assertThat(actualMatch.getMatchID()).isEqualTo(expectedMatch.getMatchID());
            assertThat(actualMatch.getHomeTeamID()).isEqualTo(expectedMatch.getHomeTeamID());
            assertThat(actualMatch.getHomeTeamName()).isEqualTo(expectedMatch.getHomeTeamName());
            assertThat(actualMatch.getGuestTeamID()).isEqualTo(expectedMatch.getGuestTeamID());
            assertThat(actualMatch.getGuestTeamName()).isEqualTo(expectedMatch.getGuestTeamName());
            assertThat(actualMatch.getMatchSchedule()).isEqualTo(expectedMatch.getMatchSchedule());
            assertThat(actualMatch.getMatchType()).isEqualTo(expectedMatch.getMatchType());
            assertThat(actualMatch.getMatchContextId()).isEqualTo(expectedMatch.getMatchContextId());
            assertThat(actualMatch.getCupLevel()).isEqualTo(expectedMatch.getCupLevel());
            assertThat(actualMatch.getCupLevelIndex()).isEqualTo(expectedMatch.getCupLevelIndex());
            assertThat(actualMatch.getHomeTeamGoals()).isEqualTo(expectedMatch.getHomeTeamGoals());
            assertThat(actualMatch.getGuestTeamGoals()).isEqualTo(expectedMatch.getGuestTeamGoals());
            assertThat(actualMatch.isOrdersGiven()).isEqualTo(expectedMatch.isOrdersGiven());
        }
    }

    private static MatchKurzInfo createMatch(int matchID, int homeTeamID, String homeTeam, int awayTeamID, String awayTeam, String matchDate, int matchType, int matchContextID, int cupLevel, int cupLevelIndex, int homeGoals, int awayGoals, int status, boolean isMatchOrderSet) {
        var m = new MatchKurzInfo();
        m.setMatchID(matchID);
        m.setHomeTeamID(homeTeamID);
        m.setHomeTeamName(homeTeam);
        m.setGuestTeamID(awayTeamID);
        m.setGuestTeamName(awayTeam);
        m.setMatchSchedule(HODateTime.fromHT(matchDate));
        m.setMatchType(MatchType.getById(matchType));
        m.setMatchContextId(matchContextID);
        m.setCupLevel(CupLevel.fromInt(cupLevel));
        m.setCupLevelIndex(CupLevelIndex.fromInt(cupLevelIndex));
        m.setHomeTeamGoals(homeGoals);
        m.setGuestTeamGoals(awayGoals);
        m.setMatchStatus(status);
        m.setOrdersGiven(isMatchOrderSet);
        return m;
    }
}
