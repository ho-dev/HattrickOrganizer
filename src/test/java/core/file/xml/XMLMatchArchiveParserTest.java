package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class XMLMatchArchiveParserTest {

    final String xmlFileMatchArchive = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
  <FileName>matchesArchive.xml</FileName>
  <Version>1.5</Version>
  <UserID>1757369</UserID>
  <FetchedDate>2026-10-18 11:33:15</FetchedDate>
  <IsYouth>False</IsYouth>
  <Team>
    <TeamID>50596</TeamID>
    <TeamName>Samatorca united</TeamName>
    <MatchList>
      <Match>
        <MatchID>767128928</MatchID>
        <HomeTeam>
          <HomeTeamID>1629411</HomeTeamID>
          <HomeTeamName>Vicenza EP</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-07-19 23:50:00</MatchDate>
        <MatchType>8</MatchType>
        <MatchContextId>0</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>767209882</MatchID>
        <HomeTeam>
          <HomeTeamID>-69920389</HomeTeamID>
          <HomeTeamName>Squadretta di dopolavoristi</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-07-21 19:00:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>7</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>7</CupId>
        <CupLevel>1</CupLevel>
        <CupLevelIndex>1</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>9</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735173</MatchID>
        <HomeTeam>
          <HomeTeamID>1524332</HomeTeamID>
          <HomeTeamName>Flacelli de Tio</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-07-25 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>4</AwayGoals>
      </Match>
      <Match>
        <MatchID>770583830</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1521613</AwayTeamID>
          <AwayTeamName>ZZ SGRUNT</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-07-28 19:00:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>7</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>7</CupId>
        <CupLevel>1</CupLevel>
        <CupLevelIndex>1</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>4</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735179</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1524577</AwayTeamID>
          <AwayTeamName>DAMBEL CITY</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-01 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>1</AwayGoals>
      </Match>
      <Match>
        <MatchID>770765446</MatchID>
        <HomeTeam>
          <HomeTeamID>1692856</HomeTeamID>
          <HomeTeamName>ilfenomeno</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-04 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735184</MatchID>
        <HomeTeam>
          <HomeTeamID>50237</HomeTeamID>
          <HomeTeamName>Vertikal Alta Val Torre</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-08 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
      <Match>
        <MatchID>770948579</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1175652</AwayTeamID>
          <AwayTeamName>Hertha Belino SC</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-11 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>1</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735187</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1037402</AwayTeamID>
          <AwayTeamName>Gin Team</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-15 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>2</AwayGoals>
      </Match>
      <Match>
        <MatchID>771089541</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>2271775</AwayTeamID>
          <AwayTeamName>Benacus Astura</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-18 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>7</HomeGoals>
        <AwayGoals>5</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735193</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1934572</AwayTeamID>
          <AwayTeamName>Loch Ness</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-22 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>4</AwayGoals>
      </Match>
      <Match>
        <MatchID>771196474</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1172344</AwayTeamID>
          <AwayTeamName>Dinomo La Spezia</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-25 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>1</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735198</MatchID>
        <HomeTeam>
          <HomeTeamID>1171524</HomeTeamID>
          <HomeTeamName>avanti in coppa</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-08-29 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
      <Match>
        <MatchID>771292534</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>852548</AwayTeamID>
          <AwayTeamName>NAVIGLINBOLGIA</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-01 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735199</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1695657</AwayTeamID>
          <AwayTeamName>Eagles_1900</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-05 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>771382233</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1729456</AwayTeamID>
          <AwayTeamName>Parma_Hammers</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-08 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>6</HomeGoals>
        <AwayGoals>1</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735204</MatchID>
        <HomeTeam>
          <HomeTeamID>1695657</HomeTeamID>
          <HomeTeamName>Eagles_1900</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-12 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
      <Match>
        <MatchID>771469604</MatchID>
        <HomeTeam>
          <HomeTeamID>855445</HomeTeamID>
          <HomeTeamName>Armonia</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-15 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735206</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1171524</AwayTeamID>
          <AwayTeamName>avanti in coppa</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-19 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>4</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>771555863</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>1927827</AwayTeamID>
          <AwayTeamName>AngelsandDevils</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-22 19:30:00</MatchDate>
        <MatchType>3</MatchType>
        <MatchContextId>644</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>644</CupId>
        <CupLevel>2</CupLevel>
        <CupLevelIndex>2</CupLevelIndex>
        <HomeGoals>3</HomeGoals>
        <AwayGoals>4</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735210</MatchID>
        <HomeTeam>
          <HomeTeamID>1934572</HomeTeamID>
          <HomeTeamName>Loch Ness</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-26 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>1</HomeGoals>
        <AwayGoals>2</AwayGoals>
      </Match>
      <Match>
        <MatchID>771703322</MatchID>
        <HomeTeam>
          <HomeTeamID>2264771</HomeTeamID>
          <HomeTeamName>CD La Chuzona</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-09-30 21:50:00</MatchDate>
        <MatchType>8</MatchType>
        <MatchContextId>0</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735216</MatchID>
        <HomeTeam>
          <HomeTeamID>1037402</HomeTeamID>
          <HomeTeamName>Gin Team</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-10-03 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>2</AwayGoals>
      </Match>
      <Match>
        <MatchID>771787082</MatchID>
        <HomeTeam>
          <HomeTeamID>2254644</HomeTeamID>
          <HomeTeamName>Pohattanippu FC</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-10-06 20:25:00</MatchDate>
        <MatchType>9</MatchType>
        <MatchContextId>0</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>1</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735219</MatchID>
        <HomeTeam>
          <HomeTeamID>50596</HomeTeamID>
          <HomeTeamName>Samatorca united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50237</AwayTeamID>
          <AwayTeamName>Vertikal Alta Val Torre</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-10-10 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>0</AwayGoals>
      </Match>
      <Match>
        <MatchID>771887045</MatchID>
        <HomeTeam>
          <HomeTeamID>793412</HomeTeamID>
          <HomeTeamName>ao united</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-10-14 22:20:00</MatchDate>
        <MatchType>8</MatchType>
        <MatchContextId>0</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>2</HomeGoals>
        <AwayGoals>2</AwayGoals>
      </Match>
      <Match>
        <MatchID>767735224</MatchID>
        <HomeTeam>
          <HomeTeamID>1524577</HomeTeamID>
          <HomeTeamName>DAMBEL CITY</HomeTeamName>
        </HomeTeam>
        <AwayTeam>
          <AwayTeamID>50596</AwayTeamID>
          <AwayTeamName>Samatorca united</AwayTeamName>
        </AwayTeam>
        <MatchDate>2026-10-17 12:00:00</MatchDate>
        <MatchType>1</MatchType>
        <MatchContextId>29541</MatchContextId>
        <SourceSystem>Hattrick</SourceSystem>
        <MatchRuleId>0</MatchRuleId>
        <CupId>0</CupId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <HomeGoals>0</HomeGoals>
        <AwayGoals>3</AwayGoals>
      </Match>
    </MatchList>
  </Team>
</HattrickData>
        """;

    @Test
    void parseMatchesFromString() {
        // when
        final var matches = XMLMatchArchivParser.parseMatchesFromString(xmlFileMatchArchive);
        assertThat(matches).isNotNull();

        var expectedMatches = new ArrayList<MatchKurzInfo>();
        expectedMatches.add(createMatch(767128928, 1629411, "Vicenza EP", 50596, "Samatorca united", "2026-07-19 23:50:00", 8, 0, 0, 0, 4, 0));
        expectedMatches.add(createMatch(767209882, -69920389, "Squadretta di dopolavoristi", 50596, "Samatorca united", "2026-07-21 19:00:00", 3, 7, 1, 1, 0, 9));
        expectedMatches.add(createMatch(767735173, 1524332, "Flacelli de Tio", 50596, "Samatorca united", "2026-07-25 12:00:00", 1, 29541, 0, 0, 4, 4));
        expectedMatches.add(createMatch(770583830, 50596, "Samatorca united", 1521613, "ZZ SGRUNT", "2026-07-28 19:00:00", 3, 7, 1, 1, 1, 4));
        expectedMatches.add(createMatch(767735179, 50596, "Samatorca united", 1524577, "DAMBEL CITY", "2026-08-01 12:00:00", 1, 29541, 0, 0, 4, 1));
        expectedMatches.add(createMatch(770765446, 1692856, "ilfenomeno", 50596, "Samatorca united", "2026-08-04 19:30:00", 3, 644, 2, 2, 0, 3));
        expectedMatches.add(createMatch(767735184, 50237, "Vertikal Alta Val Torre", 50596, "Samatorca united", "2026-08-08 12:00:00", 1, 29541, 0, 0, 1, 3));
        expectedMatches.add(createMatch(770948579, 50596, "Samatorca united", 1175652, "Hertha Belino SC", "2026-08-11 19:30:00", 3, 644, 2, 2, 4, 1));
        expectedMatches.add(createMatch(767735187, 50596, "Samatorca united", 1037402, "Gin Team", "2026-08-15 12:00:00", 1, 29541, 0, 0, 2, 2));
        expectedMatches.add(createMatch(771089541, 50596, "Samatorca united", 2271775, "Benacus Astura", "2026-08-18 19:30:00", 3, 644, 2, 2, 7, 5));
        expectedMatches.add(createMatch(767735193, 50596, "Samatorca united", 1934572, "Loch Ness", "2026-08-22 12:00:00", 1, 29541, 0, 0, 2, 4));
        expectedMatches.add(createMatch(771196474, 50596, "Samatorca united", 1172344, "Dinomo La Spezia", "2026-08-25 19:30:00", 3, 644, 2, 2, 2, 1));
        expectedMatches.add(createMatch(767735198, 1171524, "avanti in coppa", 50596, "Samatorca united", "2026-08-29 12:00:00", 1, 29541, 0, 0, 1, 3));
        expectedMatches.add(createMatch(771292534, 50596, "Samatorca united", 852548, "NAVIGLINBOLGIA", "2026-09-01 19:30:00", 3, 644, 2, 2, 4, 0));
        expectedMatches.add(createMatch(767735199, 50596, "Samatorca united", 1695657, "Eagles_1900", "2026-09-05 12:00:00", 1, 29541, 0, 0, 1, 0));
        expectedMatches.add(createMatch(771382233, 50596, "Samatorca united", 1729456, "Parma_Hammers", "2026-09-08 19:30:00", 3, 644, 2, 2, 6, 1));
        expectedMatches.add(createMatch(767735204, 1695657, "Eagles_1900", 50596, "Samatorca united", "2026-09-12 12:00:00", 1, 29541, 0, 0, 1, 3));
        expectedMatches.add(createMatch(771469604, 855445, "Armonia", 50596, "Samatorca united", "2026-09-15 19:30:00", 3, 644, 2, 2, 0, 3));
        expectedMatches.add(createMatch(767735206, 50596, "Samatorca united", 1171524, "avanti in coppa", "2026-09-19 12:00:00", 1, 29541, 0, 0, 4, 0));
        expectedMatches.add(createMatch(771555863, 50596, "Samatorca united", 1927827, "AngelsandDevils", "2026-09-22 19:30:00", 3, 644, 2, 2, 3, 4));
        expectedMatches.add(createMatch(767735210, 1934572, "Loch Ness", 50596, "Samatorca united", "2026-09-26 12:00:00", 1, 29541, 0, 0, 1, 2));
        expectedMatches.add(createMatch(771703322, 2264771, "CD La Chuzona", 50596, "Samatorca united", "2026-09-30 21:50:00", 8, 0, 0, 0, 0, 0));
        expectedMatches.add(createMatch(767735216, 1037402, "Gin Team", 50596, "Samatorca united", "2026-10-03 12:00:00", 1, 29541, 0, 0, 2, 2));
        expectedMatches.add(createMatch(771787082, 2254644, "Pohattanippu FC", 50596, "Samatorca united", "2026-10-06 20:25:00", 9, 0, 0, 0, 0, 1));
        expectedMatches.add(createMatch(767735219, 50596, "Samatorca united", 50237, "Vertikal Alta Val Torre", "2026-10-10 12:00:00", 1, 29541, 0, 0, 2, 0));
        expectedMatches.add(createMatch(771887045, 793412, "ao united", 50596, "Samatorca united", "2026-10-14 22:20:00", 8, 0, 0, 0, 2, 2));
        expectedMatches.add(createMatch(767735224, 1524577, "DAMBEL CITY", 50596, "Samatorca united", "2026-10-17 12:00:00", 1, 29541, 0, 0, 0, 3));
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
        }
    }

    private static MatchKurzInfo createMatch(int matchID, int homeTeamID, String homeTeam, int awayTeamID, String awayTeam, String matchDate, int matchType, int matchContextID, int cupLevel, int cupLevelIndex, int homeGoals, int awayGoals) {
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
        return m;
    }
}
