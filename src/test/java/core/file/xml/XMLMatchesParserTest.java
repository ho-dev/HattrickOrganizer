package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.MatchEvent;
import core.model.match.MatchKurzInfo;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static core.model.match.MatchKurzInfo.FINISHED;
import static core.model.match.MatchKurzInfo.UPCOMING;
import static org.assertj.core.api.Assertions.assertThat;

class XMLMatchesParserTest {

    final String xmlFileMatches = """
        <?xml version="1.0" encoding="utf-8"?>
        <HattrickData>
          <FileName>matches.xml</FileName>
          <Version>2.8</Version>
          <UserID>1757369</UserID>
          <FetchedDate>2026-10-23 10:07:43</FetchedDate>
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
                <HomeGoals>0</HomeGoals>
                <AwayGoals>5</AwayGoals>
                <Status>FINISHED</Status>
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
                <OrdersGiven>True</OrdersGiven>
                <Status>UPCOMING</Status>
              </Match>
              <Match>
                <MatchID>772000965</MatchID>
                <HomeTeam>
                  <HomeTeamID>520472</HomeTeamID>
                  <HomeTeamName>Juventus Brenk</HomeTeamName>
                  <HomeTeamNameShortName>Juventus</HomeTeamNameShortName>
                </HomeTeam>
                <AwayTeam>
                  <AwayTeamID>3445201</AwayTeamID>
                  <AwayTeamName>Grebenčeva team</AwayTeamName>
                  <AwayTeamNameShortName>Grebenčeva</AwayTeamNameShortName>
                </AwayTeam>
                <MatchDate>2026-10-27 18:25:00</MatchDate>
                <SourceSystem>Hattrick</SourceSystem>
                <MatchType>9</MatchType>
                <MatchContextId>0</MatchContextId>
                <CupLevel>0</CupLevel>
                <CupLevelIndex>0</CupLevelIndex>
                <OrdersGiven>False</OrdersGiven>
                <Status>UPCOMING</Status>
              </Match>
            </MatchList>
          </Team>
        </HattrickData>
        """;

    ArrayList<MatchKurzInfo> createExpectedMatches() {
        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(768443696, 977497, "Hotzenwälder SC", 520472, "Juventus Brenk", "2026-09-26 18:00:00", 1, 0, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(771662522, 520472, "Juventus Brenk", 2318194, "Ruti88", "2026-09-29 18:25:00", 9, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443702, 1247727, "FC Zwietracht Cuxhaven", 520472, "Juventus Brenk", "2026-10-03 18:00:00", 1, 0, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(771747669, 2825742, "Macarrisme Català", 520472, "Juventus Brenk", "2026-10-07 15:15:00", 9, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443705, 520472, "Juventus Brenk", 130796, "Galatasaray-SC", "2026-10-10 18:00:00", 1, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(771832586, 2189893, "Balón a Tierra", 520472, "Juventus Brenk", "2026-10-14 12:25:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443710, 824658, "SK Lation 16", 520472, "Juventus Brenk", "2026-10-17 18:00:00", 1, 0, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(771917205, 2832566, "De Soepsterren", 520472, "Juventus Brenk", "2026-10-21 11:15:00", 9, 0, 0, 0, 0, 5, FINISHED, true));
        expectedMatches.add(createMatch(768443715, 520472, "Juventus Brenk", 339078, "Haake-Becker Jungs", "2026-10-24 18:00:00", 1, 0, 0, 0, -1, -1, UPCOMING, true));
        expectedMatches.add(createMatch(772000965, 520472, "Juventus Brenk", 3445201, "Grebenčeva team", "2026-10-27 18:25:00", 9, 0, 0, 0, -1, -1, UPCOMING, false));

        return expectedMatches;
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

    final String xmlFileMatchesWithTournament = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
  <FileName>matches.xml</FileName>
  <Version>2.8</Version>
  <UserID>1757369</UserID>
  <FetchedDate>2026-10-23 10:50:17</FetchedDate>
  <IsYouth>False</IsYouth>
  <Team>
    <TeamID>669787</TeamID>
    <TeamName>FC Ruja</TeamName>
    <ShortTeamName>Ruja</ShortTeamName>
    <League>
      <LeagueID>66</LeagueID>
      <LeagueName>Litauen</LeagueName>
    </League>
    <LeagueLevelUnit>
      <LeagueLevelUnitID>29752</LeagueLevelUnitID>
      <LeagueLevelUnitName>III.1</LeagueLevelUnitName>
      <LeagueLevel>3</LeagueLevel>
    </LeagueLevelUnit>
    <MatchList>
      <Match>
        <MatchID>42188432</MatchID>
        <HomeTeam>
          <HomeTeamID>263273</HomeTeamID>
    <HomeTeamName>Amiga Janow</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-25 10:54:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42164578</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>673239</AwayTeamID>
    <AwayTeamName>FC Žaliakalnis</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-25 19:20:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>3686848</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42207386</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-26 11:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768137612</MatchID>
        <HomeTeam>
          <HomeTeamID>670092</HomeTeamID>
    <HomeTeamName>FC Kvieciai</HomeTeamName>
          <HomeTeamNameShortName>Kvieciai</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName>Ruja</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-26 15:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>29752</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42207718</MatchID>
        <HomeTeam>
          <HomeTeamID>2137810</HomeTeamID>
    <HomeTeamName>Caribbean Hammers</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-27 13:51:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42208834</MatchID>
        <HomeTeam>
          <HomeTeamID>2871154</HomeTeamID>
          <HomeTeamName>Leprae</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-27 21:21:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42177804</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2131566</AwayTeamID>
    <AwayTeamName>Athloi United</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-28 11:22:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>51</MatchType>
        <MatchContextId>7094081</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>8</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42217467</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-29 12:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42218201</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-29 17:17:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42220118</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 08:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42217171</MatchID>
        <HomeTeam>
          <HomeTeamID>932038</HomeTeamID>
    <HomeTeamName>Byrde Boldklub</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 10:39:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42220411</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 10:44:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42220983</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 12:47:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42221679</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 17:51:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771672468</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>614767</AwayTeamID>
    <AwayTeamName>Tourinorum FC</AwayTeamName>
          <AwayTeamNameShortName>Tourinorum</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-09-30 19:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42223596</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-01 07:39:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42224032</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-01 10:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7153345</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42224415</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>59972</AwayTeamID>
    <AwayTeamName>Sami Panda</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-01 17:53:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42224409</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1069888</AwayTeamID>
    <AwayTeamName>Kåravikjo Bulldogs</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-02 13:09:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42224427</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>856980</AwayTeamID>
    <AwayTeamName>Real Tuorloduovo</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-02 13:16:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42203024</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>-1</AwayTeamID>
    <AwayTeamName>Zakrock Rooch</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-02 19:20:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>3686848</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>13</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42228024</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1983262</AwayTeamID>
          <AwayTeamName>rabotnishki</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-03 08:05:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768137620</MatchID>
        <HomeTeam>
          <HomeTeamID>483189</HomeTeamID>
    <HomeTeamName>Ukmergės Squier</HomeTeamName>
          <HomeTeamNameShortName>Squier</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName>Ruja</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-03 15:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>29752</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42249739</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2800474</AwayTeamID>
          <AwayTeamName>2LOS LEONES</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 04:10:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>7</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42249709</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1016713</AwayTeamID>
    <AwayTeamName>FC Perpetuo Servaturum</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 08:06:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42249728</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2061041</AwayTeamID>
    <AwayTeamName>Wise Guys</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 08:12:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42249731</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>274092</AwayTeamID>
    <AwayTeamName>Bitacora F.C.</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 08:14:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42253354</MatchID>
        <HomeTeam>
          <HomeTeamID>484321</HomeTeamID>
    <HomeTeamName>Cabo Verde</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 08:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42254243</MatchID>
        <HomeTeam>
          <HomeTeamID>484321</HomeTeamID>
    <HomeTeamName>Cabo Verde</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 12:23:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42256392</MatchID>
        <HomeTeam>
          <HomeTeamID>484322</HomeTeamID>
          <HomeTeamName>Norge</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 18:24:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42257413</MatchID>
        <HomeTeam>
          <HomeTeamID>484322</HomeTeamID>
          <HomeTeamName>Norge</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-06 21:17:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42259720</MatchID>
        <HomeTeam>
          <HomeTeamID>484323</HomeTeamID>
          <HomeTeamName>England</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 09:55:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42260711</MatchID>
        <HomeTeam>
          <HomeTeamID>484323</HomeTeamID>
          <HomeTeamName>England</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 12:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42261471</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 14:54:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42262136</MatchID>
        <HomeTeam>
          <HomeTeamID>484323</HomeTeamID>
          <HomeTeamName>England</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 17:04:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771733518</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2071147</AwayTeamID>
          <AwayTeamName>zYxJaG_Fc</AwayTeamName>
          <AwayTeamNameShortName>zJFc</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 19:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42262937</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 19:34:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42263902</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-07 21:55:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42265578</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 06:02:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42266038</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 08:15:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42266757</MatchID>
        <HomeTeam>
          <HomeTeamID>484326</HomeTeamID>
    <HomeTeamName>The Thinkers</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 10:52:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>10</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42266769</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 10:54:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42260137</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>327894</AwayTeamID>
    <AwayTeamName>Colonia Avellaneda</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 11:09:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42260163</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>135139</AwayTeamID>
    <AwayTeamName>Güngi United</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 11:16:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42260171</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2021208</AwayTeamID>
    <AwayTeamName>Sparta Cvrcek Oman</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 11:17:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42260185</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1174259</AwayTeamID>
          <AwayTeamName>Vendetta23</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 11:21:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42267692</MatchID>
        <HomeTeam>
          <HomeTeamID>484328</HomeTeamID>
    <HomeTeamName>Symphony FC</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 13:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>10</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42267717</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 13:17:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42268514</MatchID>
        <HomeTeam>
          <HomeTeamID>484329</HomeTeamID>
    <HomeTeamName>Science Lab</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 16:03:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>7</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42268517</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 16:04:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42269194</MatchID>
        <HomeTeam>
          <HomeTeamID>484330</HomeTeamID>
    <HomeTeamName>Literary Club</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 18:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>9</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42269197</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 18:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42270270</MatchID>
        <HomeTeam>
          <HomeTeamID>484332</HomeTeamID>
    <HomeTeamName>The Brushstrokes</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 21:01:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>7</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42270283</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-08 21:04:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42271297</MatchID>
        <HomeTeam>
          <HomeTeamID>484334</HomeTeamID>
    <HomeTeamName>Silver Screen Stars</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 02:28:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42271298</MatchID>
        <HomeTeam>
          <HomeTeamID>484324</HomeTeamID>
          <HomeTeamName>Argentina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 02:29:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42271528</MatchID>
        <HomeTeam>
          <HomeTeamID>484333</HomeTeamID>
    <HomeTeamName>The Greatest Hits</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 05:53:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7167376</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42271532</MatchID>
        <HomeTeam>
          <HomeTeamID>484325</HomeTeamID>
          <HomeTeamName>España</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 05:57:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7164836</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42272813</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2353321</AwayTeamID>
          <AwayTeamName>- Trotter C&amp;F Club -</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 11:01:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42268560</MatchID>
        <HomeTeam>
          <HomeTeamID>932038</HomeTeamID>
    <HomeTeamName>Byrde Boldklub</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-09 16:07:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42272804</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>107690</AwayTeamID>
    <AwayTeamName>FC Pine Box</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-10 10:30:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42272886</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1901514</AwayTeamID>
    <AwayTeamName>Chelsea Addict</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-10 10:35:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768137622</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>675264</AwayTeamID>
          <AwayTeamName>Timeteams</AwayTeamName>
          <AwayTeamNameShortName>Timeteams</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-10 15:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>29752</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42294534</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1961030</AwayTeamID>
    <AwayTeamName>The owls are not what they seem</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-12 14:44:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42294559</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>581729</AwayTeamID>
    <AwayTeamName>Józsi csapata</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-12 14:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42294562</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1901514</AwayTeamID>
    <AwayTeamName>Chelsea Addict</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-12 14:50:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42297732</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>342895</AwayTeamID>
          <AwayTeamName>dandush</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-13 07:44:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>6</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42303531</MatchID>
        <HomeTeam>
          <HomeTeamID>593920</HomeTeamID>
          <HomeTeamName>Mattias's Sunsets</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-13 12:50:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>9</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42304075</MatchID>
        <HomeTeam>
          <HomeTeamID>594042</HomeTeamID>
          <HomeTeamName>Dimitris's Designerds</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-13 15:01:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42304603</MatchID>
        <HomeTeam>
          <HomeTeamID>594207</HomeTeamID>
          <HomeTeamName>Daniel's Surfers</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-13 17:38:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42305646</MatchID>
        <HomeTeam>
          <HomeTeamID>594241</HomeTeamID>
          <HomeTeamName>Jens's Runners</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-13 21:26:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42306810</MatchID>
        <HomeTeam>
          <HomeTeamID>594424</HomeTeamID>
          <HomeTeamName>Aartspam's Falcons</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 07:38:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301773</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>223280</AwayTeamID>
    <AwayTeamName>Fighting Donkey's FC</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42307373</MatchID>
        <HomeTeam>
          <HomeTeamID>594432</HomeTeamID>
          <HomeTeamName>Bodin's Boys</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 09:38:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42303290</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1906472</AwayTeamID>
    <AwayTeamName>Verte collineSF</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 11:30:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42307736</MatchID>
        <HomeTeam>
          <HomeTeamID>594495</HomeTeamID>
          <HomeTeamName>Johan's Olympians</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 11:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42308190</MatchID>
        <HomeTeam>
          <HomeTeamID>595952</HomeTeamID>
          <HomeTeamName>Tasos's Deus Ex Machina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 13:48:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42308826</MatchID>
        <HomeTeam>
          <HomeTeamID>596055</HomeTeamID>
          <HomeTeamName>Chase's All Stars</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 15:56:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42309265</MatchID>
        <HomeTeam>
          <HomeTeamID>597783</HomeTeamID>
          <HomeTeamName>Juan's Morphy FC</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 17:56:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771820886</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>282180</AwayTeamID>
    <AwayTeamName>Whitehead Nigeria</AwayTeamName>
          <AwayTeamNameShortName>Whitehead</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 19:20:00</MatchDate>
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
        <MatchID>42309660</MatchID>
        <HomeTeam>
          <HomeTeamID>597783</HomeTeamID>
          <HomeTeamName>Juan's Morphy FC</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 20:00:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42310086</MatchID>
        <HomeTeam>
          <HomeTeamID>597783</HomeTeamID>
          <HomeTeamName>Juan's Morphy FC</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-14 22:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301787</MatchID>
        <HomeTeam>
          <HomeTeamID>3463215</HomeTeamID>
    <HomeTeamName>Fc Bussikuskit</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42311410</MatchID>
        <HomeTeam>
          <HomeTeamID>597926</HomeTeamID>
          <HomeTeamName>TaelS's Dark Illusions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 09:02:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42311789</MatchID>
        <HomeTeam>
          <HomeTeamID>597926</HomeTeamID>
          <HomeTeamName>TaelS's Dark Illusions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 11:07:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42307722</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1961030</AwayTeamID>
    <AwayTeamName>The owls are not what they seem</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 11:18:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42307732</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1901514</AwayTeamID>
    <AwayTeamName>Chelsea Addict</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 11:23:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>8</HomeGoals>
        <AwayGoals>9</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42307764</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>160152</AwayTeamID>
          <AwayTeamName>Odd-Grenland</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 11:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42312380</MatchID>
        <HomeTeam>
          <HomeTeamID>597926</HomeTeamID>
          <HomeTeamName>TaelS's Dark Illusions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 13:35:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42313030</MatchID>
        <HomeTeam>
          <HomeTeamID>597926</HomeTeamID>
          <HomeTeamName>TaelS's Dark Illusions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 17:03:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42313930</MatchID>
        <HomeTeam>
          <HomeTeamID>594424</HomeTeamID>
          <HomeTeamName>Aartspam's Falcons</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-15 21:53:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>6</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42314934</MatchID>
        <HomeTeam>
          <HomeTeamID>595952</HomeTeamID>
          <HomeTeamName>Tasos's Deus Ex Machina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 07:38:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301789</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>649745</AwayTeamID>
    <AwayTeamName>Die Op!umbrigade</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42315361</MatchID>
        <HomeTeam>
          <HomeTeamID>595952</HomeTeamID>
          <HomeTeamName>Tasos's Deus Ex Machina</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 10:16:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42316026</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 12:34:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42316940</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 17:12:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42317449</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 20:09:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42317821</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-16 22:24:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42314969</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2012237</AwayTeamID>
    <AwayTeamName>Abrakadabra FC</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 07:42:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301799</MatchID>
        <HomeTeam>
          <HomeTeamID>2268079</HomeTeamID>
    <HomeTeamName>FC ShaChri</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>8</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42319532</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 13:37:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42319565</MatchID>
        <HomeTeam>
          <HomeTeamID>484321</HomeTeamID>
    <HomeTeamName>Cabo Verde</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 13:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7175115</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>4</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>768137626</MatchID>
        <HomeTeam>
          <HomeTeamID>674699</HomeTeamID>
          <HomeTeamName>Moons</HomeTeamName>
          <HomeTeamNameShortName>Moons</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName>Ruja</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 15:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>29752</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42319852</MatchID>
        <HomeTeam>
          <HomeTeamID>484322</HomeTeamID>
          <HomeTeamName>Norge</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 16:03:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7175115</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42319857</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 16:07:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42320286</MatchID>
        <HomeTeam>
          <HomeTeamID>484323</HomeTeamID>
          <HomeTeamName>England</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 19:22:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7175115</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42320311</MatchID>
        <HomeTeam>
          <HomeTeamID>601338</HomeTeamID>
          <HomeTeamName>Gusy's Champions</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 19:23:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7172108</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42320954</MatchID>
        <HomeTeam>
          <HomeTeamID>484323</HomeTeamID>
          <HomeTeamName>England</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-17 22:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7175115</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301800</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2330514</AwayTeamID>
    <AwayTeamName>RCD edps</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-18 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42318719</MatchID>
        <HomeTeam>
          <HomeTeamID>527644</HomeTeamID>
    <HomeTeamName>FC Kosiv</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-18 09:01:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42319053</MatchID>
        <HomeTeam>
          <HomeTeamID>1743898</HomeTeamID>
    <HomeTeamName>Caballeros de Primavera</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-18 10:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42290210</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>421188</AwayTeamID>
          <AwayTeamName>UnlikelyFC</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-19 11:00:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>51</MatchType>
        <MatchContextId>6535502</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42325729</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1901514</AwayTeamID>
    <AwayTeamName>Chelsea Addict</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-19 21:37:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42325734</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1805694</AwayTeamID>
    <AwayTeamName>Poznański Czerwiec 1956</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-19 21:40:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42325742</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>397010</AwayTeamID>
    <AwayTeamName>Malleus hereticorum</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-19 21:42:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42325750</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1108437</AwayTeamID>
    <AwayTeamName>Cubura Srbija</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-19 21:44:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>8</HomeGoals>
        <AwayGoals>2</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301812</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1063974</AwayTeamID>
    <AwayTeamName>CSM Victoria Carei</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-21 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42334691</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1682845</AwayTeamID>
    <AwayTeamName>FC Michálkovice</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-21 11:44:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1255256</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>771921852</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>33756</AwayTeamID>
    <AwayTeamName>Railay Beach-Boys</AwayTeamName>
          <AwayTeamNameShortName>Beach-Boys</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-21 19:20:00</MatchDate>
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
        <MatchID>42301908</MatchID>
        <HomeTeam>
          <HomeTeamID>2316933</HomeTeamID>
          <HomeTeamName>Leyre1998</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-22 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>5</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42334666</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1901514</AwayTeamID>
    <AwayTeamName>Chelsea Addict</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-22 08:37:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42334688</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>793659</AwayTeamID>
          <AwayTeamName>Purpleway</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-22 08:46:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140761</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>5</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42334742</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>939789</AwayTeamID>
          <AwayTeamName>Hiro_BG-san</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-22 08:49:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>3140692</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>11</HomeGoals>
        <AwayGoals>12</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301822</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2351824</AwayTeamID>
    <AwayTeamName>Real Sociedad de San Cristóbal</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-23 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
        <Status>FINISHED</Status>
      </Match>
      <Match>
        <MatchID>42301917</MatchID>
        <HomeTeam>
          <HomeTeamID>3234951</HomeTeamID>
    <HomeTeamName>the roiters</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-24 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42342803</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1602482</AwayTeamID>
    <AwayTeamName>Erbil Sport Club</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-24 10:13:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>62</MatchType>
        <MatchContextId>1258033</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>768137632</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>482976</AwayTeamID>
          <AwayTeamName>-=TXT=-</AwayTeamName>
          <AwayTeamNameShortName>Nerealus</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-24 15:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>1</MatchType>
        <MatchContextId>29752</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301835</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>3225770</AwayTeamID>
    <AwayTeamName>los tronchamuros</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-25 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301838</MatchID>
        <HomeTeam>
          <HomeTeamID>52030</HomeTeamID>
    <HomeTeamName>Berliner Elbkicker</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-28 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>771989634</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName>Ruja</HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2622805</AwayTeamID>
          <AwayTeamName>FCPakistan</AwayTeamName>
          <AwayTeamNameShortName>FCPakistan</AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-28 19:20:00</MatchDate>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301842</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>52030</AwayTeamID>
    <AwayTeamName>Berliner Elbkicker</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-29 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301851</MatchID>
        <HomeTeam>
          <HomeTeamID>3225770</HomeTeamID>
    <HomeTeamName>los tronchamuros</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-30 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301857</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>3234951</AwayTeamID>
    <AwayTeamName>the roiters</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-10-31 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301864</MatchID>
        <HomeTeam>
          <HomeTeamID>2351824</HomeTeamID>
    <HomeTeamName>Real Sociedad de San Cristóbal</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-01 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301868</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2316933</AwayTeamID>
          <AwayTeamName>Leyre1998</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-04 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301876</MatchID>
        <HomeTeam>
          <HomeTeamID>1063974</HomeTeamID>
    <HomeTeamName>CSM Victoria Carei</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-05 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301877</MatchID>
        <HomeTeam>
          <HomeTeamID>2330514</HomeTeamID>
    <HomeTeamName>RCD edps</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-06 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301889</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2268079</AwayTeamID>
    <AwayTeamName>FC ShaChri</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-07 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301891</MatchID>
        <HomeTeam>
          <HomeTeamID>649745</HomeTeamID>
    <HomeTeamName>Die Op!umbrigade</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-08 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301900</MatchID>
        <HomeTeam>
          <HomeTeamID>669787</HomeTeamID>
    <HomeTeamName>FC Ruja</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>3463215</AwayTeamID>
    <AwayTeamName>Fc Bussikuskit</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-11 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
      <Match>
        <MatchID>42301904</MatchID>
        <HomeTeam>
          <HomeTeamID>223280</HomeTeamID>
    <HomeTeamName>Fighting Donkey's FC</HomeTeamName>
          <HomeTeamNameShortName></HomeTeamNameShortName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>669787</AwayTeamID>
    <AwayTeamName>FC Ruja</AwayTeamName>
          <AwayTeamNameShortName></AwayTeamNameShortName>
        </AwayTeam>
        <MatchDate>2026-11-12 08:32:00</MatchDate>
        <SourceSystem>HTOIntegrated</SourceSystem>
        <MatchType>50</MatchType>
        <MatchContextId>7146394</MatchContextId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <Status>UPCOMING</Status>
      </Match>
    </MatchList>
  </Team>
</HattrickData>
""";

    private static Stream<Arguments> test_parseMatchesFromString() {
        return Stream.of(MatchEvent.MatchEventID.values())
            .map(matchEventID -> Arguments.of(xmlM, SUBSTITUTION_EVENTS.contains(matchEventID)));
    }



    @ParameterizedTest
    @MethodSource
    void test_isSubstitution(MatchEvent.MatchEventID matchEventID, boolean expected) {
        final var matchHighlightFromEvent = createMatchHighlight(matchEventID);
        assertThat(matchHighlightFromEvent.isSubstitution()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource
    void test_parseMatchesFromString(String xmlFileMatches, ArrayList<MatchKurzInfo> expectedMatches) {
        // when
        final var matches = XMLMatchesParser.parseMatchesFromString(xmlFileMatches);
        assertThat(matches).isNotNull();

        var expectedMatches = new ArrayList<MatchKurzInfo>();
//        expectedMatches.add(createMatch(771578631, 396488, "tawşan", 520472, "Juventus Brenk", "2026-09-23 08:00:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443696, 977497, "Hotzenwälder SC", 520472, "Juventus Brenk", "2026-09-26 18:00:00", 1, 0, 0, 0, 1, 6, FINISHED, true));
        expectedMatches.add(createMatch(771662522, 520472, "Juventus Brenk", 2318194, "Ruti88", "2026-09-29 18:25:00", 9, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443702, 1247727, "FC Zwietracht Cuxhaven", 520472, "Juventus Brenk", "2026-10-03 18:00:00", 1, 0, 0, 0, 1, 5, FINISHED, true));
        expectedMatches.add(createMatch(771747669, 2825742, "Macarrisme Català", 520472, "Juventus Brenk", "2026-10-07 15:15:00", 9, 0, 0, 0, 1, 0, FINISHED, true));
        expectedMatches.add(createMatch(768443705, 520472, "Juventus Brenk", 130796, "Galatasaray-SC", "2026-10-10 18:00:00", 1, 0, 0, 0, 3, 0, FINISHED, true));
        expectedMatches.add(createMatch(771832586, 2189893, "Balón a Tierra", 520472, "Juventus Brenk", "2026-10-14 12:25:00", 9, 0, 0, 0, 0, 2, FINISHED, true));
        expectedMatches.add(createMatch(768443710, 824658, "SK Lation 16", 520472, "Juventus Brenk", "2026-10-17 18:00:00", 1, 0, 0, 0, 1, 1, FINISHED, true));
        expectedMatches.add(createMatch(771917205, 2832566, "De Soepsterren", 520472, "Juventus Brenk", "2026-10-21 11:15:00", 9, 0, 0, 0, 0, 5, FINISHED, true));
        expectedMatches.add(createMatch(768443715, 520472, "Juventus Brenk", 339078, "Haake-Becker Jungs", "2026-10-24 18:00:00", 1, 0, 0, 0, -1, -1, UPCOMING, true));
        expectedMatches.add(createMatch(772000965, 520472, "Juventus Brenk", 3445201, "Grebenčeva team", "2026-10-27 18:25:00", 9, 0, 0, 0, -1, -1, UPCOMING, false));

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
            assertThat(actualMatch.getMatchStatus()).isEqualTo(expectedMatch.getMatchStatus());
            assertThat(actualMatch.isOrdersGiven()).isEqualTo(expectedMatch.isOrdersGiven());
        }
    }

}
