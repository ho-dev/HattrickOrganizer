package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class XMLMatchdetailsParserTest {

    final String xmlFileMatchDetails = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
    <FileName>matchdetails.xml</FileName>
    <Version>3.0</Version>Kaffee
    <UserID>2192218</UserID>
    <FetchedDate>2026-10-08 12:36:42</FetchedDate>
    <UserSupporterTier>gold</UserSupporterTier>
    <SourceSystem>Hattrick</SourceSystem>
    <Match>
        <MatchID>771794947</MatchID>
        <MatchType>4</MatchType>
        <MatchContextId>0</MatchContextId>
        <MatchRuleId>0</MatchRuleId>
        <CupLevel>0</CupLevel>
        <CupLevelIndex>0</CupLevelIndex>
        <MatchDate>2026-10-06 18:15:00</MatchDate>
        <FinishedDate>2026-10-06 20:03:00</FinishedDate>
        <AddedMinutes>3</AddedMinutes>
        <HomeTeam>
            <HomeTeamID>2157350</HomeTeamID>
            <HomeTeamName>Kaffee-Junkies Hannover</HomeTeamName>
            <DressURI>//res.hattrick.org/kits/34/338/3376/3375881/matchKitSmall.png</DressURI>
            <Formation>4-4-2</Formation>
            <HomeGoals>7</HomeGoals>
            <TacticType>7</TacticType>
            <TacticSkill>11</TacticSkill>
            <RatingMidfield>22</RatingMidfield>
            <RatingRightDef>29</RatingRightDef>
            <RatingMidDef>31</RatingMidDef>
            <RatingLeftDef>28</RatingLeftDef>
            <RatingRightAtt>16</RatingRightAtt>
            <RatingMidAtt>18</RatingMidAtt>
            <RatingLeftAtt>21</RatingLeftAtt>
            <TeamAttitude>0</TeamAttitude>
            <RatingIndirectSetPiecesDef>22</RatingIndirectSetPiecesDef>
            <RatingIndirectSetPiecesAtt>18</RatingIndirectSetPiecesAtt>
        </HomeTeam>
        <AwayTeam>
            <AwayTeamID>128206</AwayTeamID>
            <AwayTeamName>SSC Napoli-Köpenick</AwayTeamName>
            <DressURI>//res.hattrick.org/kits/1/1/1/2/matchKitSmall.png</DressURI>
            <Formation>4-5-1</Formation>
            <AwayGoals>0</AwayGoals>
            <TacticType>0</TacticType>
            <TacticSkill>0</TacticSkill>
            <RatingMidfield>18</RatingMidfield>
            <RatingRightDef>15</RatingRightDef>
            <RatingMidDef>16</RatingMidDef>
            <RatingLeftDef>10</RatingLeftDef>
            <RatingRightAtt>25</RatingRightAtt>
            <RatingMidAtt>17</RatingMidAtt>
            <RatingLeftAtt>35</RatingLeftAtt>
            <RatingIndirectSetPiecesDef>15</RatingIndirectSetPiecesDef>
            <RatingIndirectSetPiecesAtt>18</RatingIndirectSetPiecesAtt>
        </AwayTeam>
        <Arena>
            <ArenaID>2154092</ArenaID>
            <ArenaName>Alte Rösterei</ArenaName>
            <WeatherID>1</WeatherID>
            <SoldTotal>1648</SoldTotal>
            <SoldTerraces>938</SoldTerraces>
            <SoldBasic>390</SoldBasic>
            <SoldRoof>280</SoldRoof>
            <SoldVIP>40</SoldVIP>
        </Arena>
        <MatchOfficials>
            <Referee>
                <RefereeId>428327644</RefereeId>
                <RefereeName>Jean Rezende</RefereeName>
                <RefereeCountryId>16</RefereeCountryId>
                <RefereeCountryName>Brasil</RefereeCountryName>
                <RefereeTeamId>92594</RefereeTeamId>
                <RefereeTeamname>The Mended Drum</RefereeTeamname>
            </Referee>
            <RefereeAssistant1>
                <RefereeId>447281075</RefereeId>
                <RefereeName>Adam Mettler</RefereeName>
                <RefereeCountryId>8</RefereeCountryId>
                <RefereeCountryName>USA</RefereeCountryName>
                <RefereeTeamId>221928</RefereeTeamId>
                <RefereeTeamname>Fenian FC Boston</RefereeTeamname>
            </RefereeAssistant1>
            <RefereeAssistant2>
                <RefereeId>424270487</RefereeId>
                <RefereeName>Guillermo Riquelme</RefereeName>
                <RefereeCountryId>36</RefereeCountryId>
                <RefereeCountryName>España</RefereeCountryName>
                <RefereeTeamId>663326</RefereeTeamId>
                <RefereeTeamname>team_KK</RefereeTeamname>
            </RefereeAssistant2>
        </MatchOfficials>
        <Scorers>
            <Goal Index="0">
                <ScorerPlayerID>456920840</ScorerPlayerID>
                <ScorerPlayerName>Klaus Mahler</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>1</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>8</ScorerMinute>
                <MatchPart>1</MatchPart>
            </Goal>
            <Goal Index="1">
                <ScorerPlayerID>462788780</ScorerPlayerID>
                <ScorerPlayerName>Daniel Stendel</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>2</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>25</ScorerMinute>
                <MatchPart>1</MatchPart>
            </Goal>
            <Goal Index="2">
                <ScorerPlayerID>485463818</ScorerPlayerID>
                <ScorerPlayerName>Derk Lerf</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>3</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>33</ScorerMinute>
                <MatchPart>1</MatchPart>
            </Goal>
            <Goal Index="3">
                <ScorerPlayerID>462788780</ScorerPlayerID>
                <ScorerPlayerName>Daniel Stendel</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>4</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>37</ScorerMinute>
                <MatchPart>1</MatchPart>
            </Goal>
            <Goal Index="4">
                <ScorerPlayerID>456920840</ScorerPlayerID>
                <ScorerPlayerName>Klaus Mahler</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>5</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>52</ScorerMinute>
                <MatchPart>2</MatchPart>
            </Goal>
            <Goal Index="5">
                <ScorerPlayerID>462788780</ScorerPlayerID>
                <ScorerPlayerName>Daniel Stendel</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>6</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>63</ScorerMinute>
                <MatchPart>2</MatchPart>
            </Goal>
            <Goal Index="6">
                <ScorerPlayerID>473360703</ScorerPlayerID>
                <ScorerPlayerName>Sergio Pinto</ScorerPlayerName>
                <ScorerTeamID>2157350</ScorerTeamID>
                <ScorerHomeGoals>7</ScorerHomeGoals>
                <ScorerAwayGoals>0</ScorerAwayGoals>
                <ScorerMinute>84</ScorerMinute>
                <MatchPart>2</MatchPart>
            </Goal>
        </Scorers>
        <Bookings>
            <Booking Index="0">
                <BookingPlayerID>502246230</BookingPlayerID>
                <BookingPlayerName>Felix Klaus</BookingPlayerName>
                <BookingTeamID>2157350</BookingTeamID>
                <BookingType>1</BookingType>
                <BookingMinute>42</BookingMinute>
                <MatchPart>1</MatchPart>
            </Booking>
        </Bookings>
        <Injuries />
        <PossessionFirstHalfHome>55</PossessionFirstHalfHome>
        <PossessionFirstHalfAway>45</PossessionFirstHalfAway>
        <PossessionSecondHalfHome>53</PossessionSecondHalfHome>
        <PossessionSecondHalfAway>47</PossessionSecondHalfAway>
        <EventList>
            <Event Index="0">
                <Minute>0</Minute>
                <SubjectPlayerID>2154092</SubjectPlayerID>
                <SubjectTeamID>50000</SubjectTeamID>
                <ObjectPlayerID>1648</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>31</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText>&lt;a href="/Club/Stadium/?stadiumId=2154092"&gt;Alte Rösterei&lt;/a&gt; - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. &lt;a href="/Club/HallOfFame/Player.aspx?playerId=455444108"&gt;Sultan Hafeeth Al-Buayqy&lt;/a&gt; wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten &lt;a href="/Club/HallOfFame/Player.aspx?playerId=474275056"&gt;Bill Matthews&lt;/a&gt; und &lt;a href="/Club/HallOfFame/Player.aspx?playerId=459237940"&gt;Idar Åkesson&lt;/a&gt;.</EventText>
            </Event>
            <Event Index="1">
                <Minute>0</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>19</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="2">
                <Minute>0</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>21</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>Folgende Spieler begannen: &lt;a href="/Club/Players/Player.aspx?playerId=473025501" title="Sievers" class="homeplayer"&gt;Sievers&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=497733968" title="Eggimann" class="homeplayer"&gt;Eggimann&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=480433526" title="Br&amp;#252;ggemann" class="homeplayer"&gt;Brüggemann&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=456920840" title="Mahler" class="homeplayer"&gt;Mahler&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=503053978" title="Cherundolo" class="homeplayer"&gt;Cherundolo&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=502246230" title="Klaus" class="homeplayer"&gt;Klaus&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=473360703" title="Pinto" class="homeplayer"&gt;Pinto&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=457053654" title="Leopold" class="homeplayer"&gt;Leopold&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=511385914" title="Maina" class="homeplayer"&gt;Maina&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=485463818" title="Lerf" class="homeplayer"&gt;Lerf&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Stendel" class="homeplayer"&gt;Stendel&lt;/a&gt;.&lt;br /&gt;&lt;br /&gt;</EventText>
            </Event>
            <Event Index="3">
                <Minute>0</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>21</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>Es liefen auf: &lt;a href="/Club/Players/Player.aspx?playerId=472120229" title="Schlegelmilch" class="awayplayer"&gt;Schlegelmilch&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=489791494" title="Masciadri" class="awayplayer"&gt;Masciadri&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=496877677" title="Gierer" class="awayplayer"&gt;Gierer&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=472291599" title="Rutherford" class="awayplayer"&gt;Rutherford&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=483281921" title="Parmendola" class="awayplayer"&gt;Parmendola&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=490747950" title="Rajs" class="awayplayer"&gt;Rajs&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=490797976" title="Hultflod" class="awayplayer"&gt;Hultflod&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=474503458" title="Foltea" class="awayplayer"&gt;Foltea&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=472214265" title="Sellinger" class="awayplayer"&gt;Sellinger&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=492622921" title="Adderton" class="awayplayer"&gt;Adderton&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=472437980" title="Schichta" class="awayplayer"&gt;Schichta&lt;/a&gt;.&lt;br /&gt;&lt;br /&gt;</EventText>
            </Event>
            <Event Index="4">
                <Minute>0</Minute>
                <SubjectPlayerID>1040402</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>1040501</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>24</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;Kaffee-Junkies Hannover&lt;/span&gt; lief in einem 4-4-2 auf,  &lt;span class="awayteam"&gt;SSC Napoli-Köpenick&lt;/span&gt; entschied sich für ein 4-5-1. </EventText>
            </Event>
            <Event Index="5">
                <Minute>0</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>0</MatchPart>
                <EventTypeID>598</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="6">
                <Minute>0</Minute>
                <SubjectPlayerID>151</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>335</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; befleißigte sich einer kreativen Spielweise. Sieht &lt;a href="/Help/Rules/AppDenominations.aspx?lt=skill&amp;amp;ll=11#skill" class="skill"&gt;brillant&lt;/a&gt; aus, was die Taktikstärke angeht. </EventText>
            </Event>
            <Event Index="7">
                <Minute>5</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>1040402</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>481</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="8">
                <Minute>8</Minute>
                <SubjectPlayerID>456920840</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>121</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>Ein ohrenbetäubendes Vuvuzela-Konzert brach in der 8. Minute auf den Tribünen aus, nachdem &lt;a href="/Club/Players/Player.aspx?playerId=456920840" title="Klaus Mahler" class="homeplayer"&gt;Klaus Mahler&lt;/a&gt; zentral vor dem Tor abgezogen und einen Treffer für &lt;span class="hometeam"&gt;KJH&lt;/span&gt; markiert hatte. Es stand nun 1 - 0.</EventText>
            </Event>
            <Event Index="9">
                <Minute>11</Minute>
                <SubjectPlayerID>95</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>61</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>Bei &lt;span class="hometeam"&gt;KJH&lt;/span&gt; schien nach 11 Minuten keiner wirklich zu wissen, wo er zu stehen hat. Die Mannschaft war &lt;a href="/Help/Rules/AppDenominations.aspx?lt=skill&amp;amp;ll=7#skill" class="skill"&gt;gut&lt;/a&gt; organisiert.</EventText>
            </Event>
            <Event Index="10">
                <Minute>13</Minute>
                <SubjectPlayerID>59</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>471</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="11">
                <Minute>15</Minute>
                <SubjectPlayerID>511385914</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>503221197</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>352</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText>Vollkommen überrascht war &lt;a href="/Club/Players/Player.aspx?playerId=511385914" title="Linton Maina" class="homeplayer"&gt;Linton Maina&lt;/a&gt; von seiner Auswechslung in der 15. Minute. Bei &lt;span class="hometeam"&gt;KJH&lt;/span&gt; betrat an seiner Stelle &lt;a href="/Club/Players/Player.aspx?playerId=503221197" title="Leonardo &amp;#39;Leo&amp;#39; Bittencourt" class="homeplayer"&gt;Leonardo 'Leo' Bittencourt&lt;/a&gt; den Rasen.</EventText>
            </Event>
            <Event Index="12">
                <Minute>15</Minute>
                <SubjectPlayerID>79</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>61</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; hatte offensichtlich Probleme mit den taktischen Anweisungen. Die Mannschaft war in der 15. Minute nur noch &lt;a href="/Help/Rules/AppDenominations.aspx?lt=skill&amp;amp;ll=4#skill" class="skill"&gt;schwach&lt;/a&gt; organisiert.</EventText>
            </Event>
            <Event Index="13">
                <Minute>17</Minute>
                <SubjectPlayerID>485463818</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>489</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="14">
                <Minute>20</Minute>
                <SubjectPlayerID>502246230</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>483281921</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>289</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=483281921" title="Cesare Parmendola" class="awayplayer"&gt;Cesare Parmendola&lt;/a&gt; erntete in der 20. Minute tosenden Applaus, als er einen Pfau einfing, der plötzlich durch seinen Strafraum stolzierte. Das Tier war bei den nahen Dreharbeiten zu "Bauer sucht Pfau" stiften gegangen. Diese spektakuläre Aktion war noch lange in aller Munde. Dass &lt;a href="/Club/Players/Player.aspx?playerId=483281921" title="Cesare Parmendola" class="awayplayer"&gt;Cesare Parmendola&lt;/a&gt; noch in der gleichen Minute &lt;span class="hometeam"&gt;KJH&lt;/span&gt;-Flitzer &lt;a href="/Club/Players/Player.aspx?playerId=502246230" title="Felix Klaus" class="homeplayer"&gt;Felix Klaus&lt;/a&gt; den Ball vom Fuß spitzelte und so einen Gegentreffer verhinderte, ging dabei fast unter.</EventText>
            </Event>
            <Event Index="15">
                <Minute>22</Minute>
                <SubjectPlayerID>472120229</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>100</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>452</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="16">
                <Minute>25</Minute>
                <SubjectPlayerID>462788780</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>131</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>Nach 25 Minuten kam es für die Gäste noch schlimmer, als &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt; es ohne große Widerstände durch die Abwehrmitte vor das Tor schaffte und zum 2 - 0 traf.</EventText>
            </Event>
            <Event Index="17">
                <Minute>33</Minute>
                <SubjectPlayerID>485463818</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>503221197</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>116</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt;-Akteur &lt;a href="/Club/Players/Player.aspx?playerId=503221197" title="Leonardo &amp;#39;Leo&amp;#39; Bittencourt" class="homeplayer"&gt;Leonardo 'Leo' Bittencourt&lt;/a&gt; konnte sich in der 33. Minute dank seiner Schnelligkeit mühelos von seinem Gegenspieler absetzen. &lt;a href="/Club/Players/Player.aspx?playerId=485463818" title="Derk Lerf" class="homeplayer"&gt;Derk Lerf&lt;/a&gt; hatte keinerlei Schwierigkeit, die scharfe Hereingabe am herausstürzenden Torwart vorbei zum 3 - 0 zu verwandeln.</EventText>
            </Event>
            <Event Index="18">
                <Minute>34</Minute>
                <SubjectPlayerID>457053654</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>231</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>Eine wundervolle Kombination im Angriffszentrum hätte nach 34 Minuten fast zu einem weiteren Tor für &lt;span class="hometeam"&gt;KJH&lt;/span&gt; geführt, doch &lt;a href="/Club/Players/Player.aspx?playerId=472120229" title="Friederich Schlegelmilch" class="awayplayer"&gt;Friederich Schlegelmilch&lt;/a&gt; war bei diesem Schuss von &lt;a href="/Club/Players/Player.aspx?playerId=457053654" title="Enzo Leopold" class="homeplayer"&gt;Enzo Leopold&lt;/a&gt; auf dem Posten.</EventText>
            </Event>
            <Event Index="19">
                <Minute>37</Minute>
                <SubjectPlayerID>462788780</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>134</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; wurde in der 37. Minute ein Strafstoß zugesprochen. &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt; ließ sich die Chance nicht entgehen und verwandelte mit einem Lächeln auf dem Gesicht zum 4 - 0 für das Heimteam.</EventText>
            </Event>
            <Event Index="20">
                <Minute>40</Minute>
                <SubjectPlayerID>502246230</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>462788780</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>218</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt; schlug nach 40 Minuten einen raffiniert herein gezogenen Eckball, doch &lt;a href="/Club/Players/Player.aspx?playerId=502246230" title="Felix Klaus" class="homeplayer"&gt;Felix Klaus&lt;/a&gt; war zu überrascht, den Ball zu bekommen, und verstolperte die Chance für &lt;span class="hometeam"&gt;KJH&lt;/span&gt;.</EventText>
            </Event>
            <Event Index="21">
                <Minute>41</Minute>
                <SubjectPlayerID>497733968</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>232</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Nach einer Flanke von links spitzelte &lt;a href="/Club/Players/Player.aspx?playerId=497733968" title="Mario Eggimann" class="homeplayer"&gt;Mario Eggimann&lt;/a&gt; den Ball in Richtung Glück und Seligkeit, doch der Außenpfosten verhinderte ein weiteres Tor für &lt;span class="hometeam"&gt;KJH&lt;/span&gt;. Da fiel dem Präsidium des gegnerischen Teams auf der Tribüne sichtlich ein Stein vom Herzen, dass diese Situation in der 41. Minute ein gutes Ende nahm. </EventText>
            </Event>
            <Event Index="22">
                <Minute>42</Minute>
                <SubjectPlayerID>502246230</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>510</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt;-Spieler &lt;a href="/Club/Players/Player.aspx?playerId=502246230" title="Felix Klaus" class="homeplayer"&gt;Felix Klaus&lt;/a&gt; hatte es bei seiner Attacke in der 42. Minute nur auf die Beine des Gegenspielers abgesehen. Folgerichtig sah er die Gelbe Karte.</EventText>
            </Event>
            <Event Index="23">
                <Minute>43</Minute>
                <SubjectPlayerID>472291599</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>360</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Spielminute 43: Um das Team noch auf die Siegerstraße zu bringen, richtete der &lt;span class="awayteam"&gt;Napoli-Köpenick&lt;/span&gt;-Trainer seine Mannen taktisch neu aus.</EventText>
            </Event>
            <Event Index="24">
                <Minute>44</Minute>
                <SubjectPlayerID>496877677</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>360</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText>Bei &lt;span class="awayteam"&gt;Napoli-Köpenick&lt;/span&gt; war man über den Rückstand verständlicherweise nicht erfreut, daher formierten sich die Spieler nach 44 Minuten auf Anweisung ihres Trainers neu.</EventText>
            </Event>
            <Event Index="25">
                <Minute>45</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>45</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Zur Halbzeit stand es 4 - 0.</EventText>
            </Event>
            <Event Index="26">
                <Minute>45</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>55</ObjectPlayerID>
                <MatchPart>1</MatchPart>
                <EventTypeID>40</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; war in dieser Halbzeit das dominierende Team - die Spieler brachten den Ballbesitz auf 55 Prozent.&lt;br /&gt;&lt;br /&gt;</EventText>
            </Event>
            <Event Index="27">
                <Minute>46</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>597</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="28">
                <Minute>46</Minute>
                <SubjectPlayerID>456933528</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>94</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>64</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>Ein erneuter Blick auf die Taktiktafel in der Pause sorgte dafür, dass die Spieler von &lt;span class="hometeam"&gt;KJH&lt;/span&gt; nun &lt;a href="/Help/Rules/AppDenominations.aspx?lt=skill&amp;amp;ll=6#skill" class="skill"&gt;passabel&lt;/a&gt; organisiert waren.</EventText>
            </Event>
            <Event Index="29">
                <Minute>46</Minute>
                <SubjectPlayerID>473360703</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>361</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Zufrieden mit dem Spielstand verordnete der &lt;span class="hometeam"&gt;KJH&lt;/span&gt;-Trainer seiner Mannschaft in der 46. Minute eine neue taktische Ausrichtung.</EventText>
            </Event>
            <Event Index="31">
                <Minute>51</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>1040303</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>481</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="32">
                <Minute>52</Minute>
                <SubjectPlayerID>456920840</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>132</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; baute in der 52. Minute die Führung auf 5 - 0 aus, nachdem ein Verteidiger der Gäste den Ball leichtfertig an &lt;a href="/Club/Players/Player.aspx?playerId=456920840" title="Klaus Mahler" class="homeplayer"&gt;Klaus Mahler&lt;/a&gt; verloren hatte. Dieser drang über links in den Strafraum ein und spielte dann noch gekonnt den Torwart aus, bevor er das Leder lässig über die Linie schob.</EventText>
            </Event>
            <Event Index="33">
                <Minute>56</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>1648</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>464</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="34">
                <Minute>60</Minute>
                <SubjectPlayerID>457053654</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>461796896</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>352</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>Darauf hatte &lt;a href="/Club/Players/Player.aspx?playerId=461796896" title="Lars Stindl" class="homeplayer"&gt;Lars Stindl&lt;/a&gt; schon länger gewartet. Nach 60 Minuten war es soweit, er kam zu seinem Einsatz für &lt;span class="hometeam"&gt;KJH&lt;/span&gt;. Im Gegenzug verließ &lt;a href="/Club/Players/Player.aspx?playerId=457053654" title="Enzo Leopold" class="homeplayer"&gt;Enzo Leopold&lt;/a&gt; den Platz.</EventText>
            </Event>
            <Event Index="35">
                <Minute>62</Minute>
                <SubjectPlayerID>75</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>61</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>Spielminute 62: Bei &lt;span class="hometeam"&gt;KJH&lt;/span&gt; herrschte ein ziemliches Durcheinander auf dem Feld - die Spieler hatten Probleme, ihre Positionen zu halten, und waren &lt;a href="/Help/Rules/AppDenominations.aspx?lt=skill&amp;amp;ll=3#skill" class="skill"&gt;armselig&lt;/a&gt; organisiert.</EventText>
            </Event>
            <Event Index="36">
                <Minute>63</Minute>
                <SubjectPlayerID>462788780</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>132</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>Ein schnell vorgetragener Angriff von &lt;span class="hometeam"&gt;KJH&lt;/span&gt; über die linke Seite des Feldes wurde in der 63. Minute mit einem weiteren Tor zum 6 - 0 gekrönt. &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt; hatte aus spitzem Winkel abgezogen. Ein wichtiger Pass zum richtigen Zeitpunkt: &lt;a href="/Club/Players/Player.aspx?playerId=503221197" title="Leonardo &amp;#39;Leo&amp;#39; Bittencourt" class="homeplayer"&gt;Leonardo 'Leo' Bittencourt&lt;/a&gt; bereitete das Tor exzellent vor.</EventText>
            </Event>
            <Event Index="37">
                <Minute>63</Minute>
                <SubjectPlayerID>462788780</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>46</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>Damit konnte sich &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt; als Hattrickschütze notieren lassen.</EventText>
            </Event>
            <Event Index="38">
                <Minute>74</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>494</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="39">
                <Minute>83</Minute>
                <SubjectPlayerID>472291599</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>473025501</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>251</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>83 Minuten waren gespielt, als &lt;a href="/Club/Players/Player.aspx?playerId=472291599" title="Raven Rutherford" class="awayplayer"&gt;Raven Rutherford&lt;/a&gt; sich in der Mitte prima durchsetzte und nur noch &lt;a href="/Club/Players/Player.aspx?playerId=473025501" title="J&amp;#246;rg &amp;#39;Colt&amp;#39; Sievers" class="homeplayer"&gt;Jörg 'Colt' Sievers&lt;/a&gt; vor sich hatte. Der blieb lange stehen und wehrte den Schuss ab. Es blieb beim 6 - 0.</EventText>
            </Event>
            <Event Index="40">
                <Minute>84</Minute>
                <SubjectPlayerID>473360703</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>472120229</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>133</EventTypeID>
                <EventVariation>4</EventVariation>
                <EventText>Eine schöne Ballstafette auf dem rechten Flügel der Gastgeber konnte von der gegnerischen Abwehr nicht unterbunden werden. So gelangte das Objekt der Begierde schließlich zu
                    &lt;a href="/Club/Players/Player.aspx?playerId=473360703" title="Sergio Pinto" class="homeplayer"&gt;Sergio Pinto&lt;/a&gt;, der ein weiteres Tor für &lt;span class="hometeam"&gt;KJH&lt;/span&gt; erzielte. Die Führung erhöhte sich dadurch in der 84. Minute auf 7 - 0.
                    Ein wichtiger Pass zum richtigen Zeitpunkt: &lt;a href="/Club/Players/Player.aspx?playerId=503221197" title="Leonardo &amp;#39;Leo&amp;#39; Bittencourt" class="homeplayer"&gt;Leonardo 'Leo' Bittencourt&lt;/a&gt; bereitete das Tor exzellent vor.</EventText>
            </Event>
            <Event Index="41">
                <Minute>89</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>75</EventTypeID>
                <EventVariation>5</EventVariation>
                <EventText>Die Nachspielzeit beträgt 3 Minute(n).</EventText>
            </Event>
            <Event Index="42">
                <Minute>93</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>0</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>599</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Die Begegnung endete 7 - 0.</EventText>
            </Event>
            <Event Index="43">
                <Minute>93</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>601</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText></EventText>
            </Event>
            <Event Index="44">
                <Minute>93</Minute>
                <SubjectPlayerID>0</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>53</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>40</EventTypeID>
                <EventVariation>1</EventVariation>
                <EventText>&lt;span class="hometeam"&gt;KJH&lt;/span&gt; erreichte laut HT-Datenbank 53 Prozent Ballbesitz.&lt;br /&gt;&lt;br /&gt;</EventText>
            </Event>
            <Event Index="45">
                <Minute>93</Minute>
                <SubjectPlayerID>462788780</SubjectPlayerID>
                <SubjectTeamID>2157350</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>41</EventTypeID>
                <EventVariation>2</EventVariation>
                <EventText>Der beste Spieler von &lt;span class="hometeam"&gt;KJH&lt;/span&gt; war ohne jeden Zweifel &lt;a href="/Club/Players/Player.aspx?playerId=462788780" title="Daniel Stendel" class="homeplayer"&gt;Daniel Stendel&lt;/a&gt;.</EventText>
            </Event>
            <Event Index="46">
                <Minute>93</Minute>
                <SubjectPlayerID>483281921</SubjectPlayerID>
                <SubjectTeamID>128206</SubjectTeamID>
                <ObjectPlayerID>0</ObjectPlayerID>
                <MatchPart>2</MatchPart>
                <EventTypeID>41</EventTypeID>
                <EventVariation>3</EventVariation>
                <EventText>Nach diesem Spiel forderten die Fans von &lt;span class="awayteam"&gt;Napoli-Köpenick&lt;/span&gt; eine "&lt;a href="/Club/Players/Player.aspx?playerId=483281921" title="Cesare Parmendola" class="awayplayer"&gt;Cesare Parmendola&lt;/a&gt;-Straße" - er hatte eine herausragende Leistung abgeliefert.</EventText>
            </Event>
        </EventList>
    </Match>
</HattrickData>

        """;

    final String xmlFileMatchLineupTeam1 = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
  <FileName>matchlineup.xml</FileName>
  <Version>2.1</Version>
  <UserID>1757369</UserID>
  <FetchedDate>2026-10-16 17:26:16</FetchedDate>
  <MatchID>771832586</MatchID>
  <SourceSystem>Hattrick</SourceSystem>
  <MatchType>9</MatchType>
  <MatchContextId>0</MatchContextId>
  <MatchDate>2026-10-14 12:25:00</MatchDate>
  <HomeTeam>
    <HomeTeamID>2189893</HomeTeamID>
    <HomeTeamName>Balón a Tierra</HomeTeamName>
  </HomeTeam>
  <AwayTeam>
    <AwayTeamID>520472</AwayTeamID>
    <AwayTeamName>Juventus Brenk</AwayTeamName>
  </AwayTeam>
  <Arena>
    <ArenaID>2186635</ArenaID>
    <ArenaName>Study BaT</ArenaName>
  </Arena>
  <Team>
    <TeamID>2189893</TeamID>
    <TeamName>Balón a Tierra</TeamName>
    <ExperienceLevel>2</ExperienceLevel>
    <StyleOfPlay>10</StyleOfPlay>
    <StartingLineup>
      <Player>
        <PlayerID>512764588</PlayerID>
        <RoleID>100</RoleID>
        <FirstName>Mauregato</FirstName>
        <LastName>Mateos Cabeza</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>515759423</PlayerID>
        <RoleID>101</RoleID>
        <FirstName>Gartzi</FirstName>
        <LastName>Latsaldebea</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>482584544</PlayerID>
        <RoleID>102</RoleID>
        <FirstName>Rendert</FirstName>
        <LastName>van der Gouw</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>515182494</PlayerID>
        <RoleID>103</RoleID>
        <FirstName>Fayad</FirstName>
        <LastName>El-Badaoui</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>486147501</PlayerID>
        <RoleID>104</RoleID>
        <FirstName>Adam</FirstName>
        <LastName>Mouaoui</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>492683494</PlayerID>
        <RoleID>105</RoleID>
        <FirstName>Sergi</FirstName>
        <LastName>Balcells</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>514953230</PlayerID>
        <RoleID>106</RoleID>
        <FirstName>Bogdan</FirstName>
        <LastName>Malaxa</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>503140089</PlayerID>
        <RoleID>108</RoleID>
        <FirstName>Abdennasser</FirstName>
        <LastName>Boutasaa</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>490057160</PlayerID>
        <RoleID>110</RoleID>
        <FirstName>Ettore</FirstName>
        <LastName>Benecchi</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>512704162</PlayerID>
        <RoleID>111</RoleID>
        <FirstName>Álvaro</FirstName>
        <LastName>Santiago</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>490253573</PlayerID>
        <RoleID>113</RoleID>
        <FirstName>Bonifacio</FirstName>
        <LastName>Somozas</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>482584544</PlayerID>
        <RoleID>17</RoleID>
        <FirstName>Rendert</FirstName>
        <LastName>van der Gouw</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>486147501</PlayerID>
        <RoleID>18</RoleID>
        <FirstName>Adam</FirstName>
        <LastName>Mouaoui</LastName>
        <NickName></NickName>
      </Player>
    </StartingLineup>
    <Substitutions>
      <Substitution>
        <TeamID>2189893</TeamID>
        <SubjectPlayerID>492683494</SubjectPlayerID>
        <ObjectPlayerID>503140086</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>105</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>70</MatchMinute>
        <MatchPart>2</MatchPart>
      </Substitution>
      <Substitution>
        <TeamID>2189893</TeamID>
        <SubjectPlayerID>515759423</SubjectPlayerID>
        <ObjectPlayerID>499491994</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>101</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>70</MatchMinute>
        <MatchPart>2</MatchPart>
      </Substitution>
      <Substitution>
        <TeamID>2189893</TeamID>
        <SubjectPlayerID>482584544</SubjectPlayerID>
        <ObjectPlayerID>497811017</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>102</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>70</MatchMinute>
        <MatchPart>2</MatchPart>
      </Substitution>
    </Substitutions>
    <Lineup>
      <Player>
        <PlayerID>512764588</PlayerID>
        <RoleID>100</RoleID>
        <FirstName>Mauregato</FirstName>
        <LastName>Mateos Cabeza</LastName>
        <NickName></NickName>
        <RatingStars>4</RatingStars>
        <RatingStarsEndOfMatch>3</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>499491994</PlayerID>
        <RoleID>101</RoleID>
        <FirstName>Constante</FirstName>
        <LastName>Cernadelo</LastName>
        <NickName></NickName>
        <RatingStars>4.5</RatingStars>
        <RatingStarsEndOfMatch>4</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>497811017</PlayerID>
        <RoleID>102</RoleID>
        <FirstName>Isra</FirstName>
        <LastName>Llagostera</LastName>
        <NickName></NickName>
        <RatingStars>3.5</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>515182494</PlayerID>
        <RoleID>103</RoleID>
        <FirstName>Fayad</FirstName>
        <LastName>El-Badaoui</LastName>
        <NickName></NickName>
        <RatingStars>3.5</RatingStars>
        <RatingStarsEndOfMatch>1.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>486147501</PlayerID>
        <RoleID>104</RoleID>
        <FirstName>Adam</FirstName>
        <LastName>Mouaoui</LastName>
        <NickName></NickName>
        <RatingStars>6</RatingStars>
        <RatingStarsEndOfMatch>4.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>503140086</PlayerID>
        <RoleID>105</RoleID>
        <FirstName>Didaka</FirstName>
        <LastName>Irigoienahadoberri</LastName>
        <NickName></NickName>
        <RatingStars>3.5</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>503140089</PlayerID>
        <RoleID>108</RoleID>
        <FirstName>Abdennasser</FirstName>
        <LastName>Boutasaa</LastName>
        <NickName></NickName>
        <RatingStars>4</RatingStars>
        <RatingStarsEndOfMatch>3</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>490057160</PlayerID>
        <RoleID>110</RoleID>
        <FirstName>Ettore</FirstName>
        <LastName>Benecchi</LastName>
        <NickName></NickName>
        <RatingStars>4.5</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>512704162</PlayerID>
        <RoleID>111</RoleID>
        <FirstName>Álvaro</FirstName>
        <LastName>Santiago</LastName>
        <NickName></NickName>
        <RatingStars>5</RatingStars>
        <RatingStarsEndOfMatch>3</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>490253573</PlayerID>
        <RoleID>113</RoleID>
        <FirstName>Bonifacio</FirstName>
        <LastName>Somozas</LastName>
        <NickName></NickName>
        <RatingStars>6</RatingStars>
        <RatingStarsEndOfMatch>4.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>487376992</PlayerID>
        <RoleID>114</RoleID>
        <FirstName>Dídac</FirstName>
        <LastName>Moncusí</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>491592848</PlayerID>
        <RoleID>115</RoleID>
        <FirstName>Lucival</FirstName>
        <LastName>Itaitinga</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>488863783</PlayerID>
        <RoleID>116</RoleID>
        <FirstName>Rubén</FirstName>
        <LastName>Iriztain</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>489804720</PlayerID>
        <RoleID>118</RoleID>
        <FirstName>Juan Antonio</FirstName>
        <LastName>Frances</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>497811017</PlayerID>
        <RoleID>17</RoleID>
        <FirstName>Isra</FirstName>
        <LastName>Llagostera</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>486147501</PlayerID>
        <RoleID>18</RoleID>
        <FirstName>Adam</FirstName>
        <LastName>Mouaoui</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>492683494</PlayerID>
        <RoleID>19</RoleID>
        <FirstName>Sergi</FirstName>
        <LastName>Balcells</LastName>
        <NickName></NickName>
        <RatingStars>6</RatingStars>
        <RatingStarsEndOfMatch>6</RatingStarsEndOfMatch>
      </Player>
      <Player>
        <PlayerID>515759423</PlayerID>
        <RoleID>20</RoleID>
        <FirstName>Gartzi</FirstName>
        <LastName>Latsaldebea</LastName>
        <NickName></NickName>
        <RatingStars>4</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
      </Player>
      <Player>
        <PlayerID>482584544</PlayerID>
        <RoleID>21</RoleID>
        <FirstName>Rendert</FirstName>
        <LastName>van der Gouw</LastName>
        <NickName></NickName>
        <RatingStars>6</RatingStars>
        <RatingStarsEndOfMatch>6</RatingStarsEndOfMatch>
      </Player>
      <Player>
        <PlayerID>514953230</PlayerID>
        <RoleID>33</RoleID>
        <FirstName>Bogdan</FirstName>
        <LastName>Malaxa</LastName>
        <NickName></NickName>
        <RatingStars>4</RatingStars>
        <RatingStarsEndOfMatch>4</RatingStarsEndOfMatch>
      </Player>
    </Lineup>
  </Team>
</HattrickData>

        """;


    final String xmlFileMatchLineupTeam2 = """
<?xml version="1.0" encoding="utf-8"?>
<HattrickData>
  <FileName>matchlineup.xml</FileName>
  <Version>2.1</Version>
  <UserID>1757369</UserID>
  <FetchedDate>2026-10-16 17:31:24</FetchedDate>
  <MatchID>771832586</MatchID>
  <SourceSystem>Hattrick</SourceSystem>
  <MatchType>9</MatchType>
  <MatchContextId>0</MatchContextId>
  <MatchDate>2026-10-14 12:25:00</MatchDate>
  <HomeTeam>
    <HomeTeamID>2189893</HomeTeamID>
    <HomeTeamName>Balón a Tierra</HomeTeamName>
  </HomeTeam>
  <AwayTeam>
    <AwayTeamID>520472</AwayTeamID>
    <AwayTeamName>Juventus Brenk</AwayTeamName>
  </AwayTeam>
  <Arena>
    <ArenaID>2186635</ArenaID>
    <ArenaName>Study BaT</ArenaName>
  </Arena>
  <Team>
    <TeamID>520472</TeamID>
    <TeamName>Juventus Brenk</TeamName>
    <ExperienceLevel>4</ExperienceLevel>
    <StyleOfPlay>0</StyleOfPlay>
    <StartingLineup>
      <Player>
        <PlayerID>472298043</PlayerID>
        <RoleID>100</RoleID>
        <FirstName>Dionisis</FirstName>
        <LastName>Violaris</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>491124183</PlayerID>
        <RoleID>107</RoleID>
        <FirstName>Wilfried</FirstName>
        <LastName>Ellhardt</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>500430459</PlayerID>
        <RoleID>102</RoleID>
        <FirstName>Gerome</FirstName>
        <LastName>Kalliss</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>485116928</PlayerID>
        <RoleID>103</RoleID>
        <FirstName>Dierk</FirstName>
        <LastName>Riester</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>513041996</PlayerID>
        <RoleID>104</RoleID>
        <FirstName>Gert</FirstName>
        <LastName>Hammes</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>497671194</PlayerID>
        <RoleID>105</RoleID>
        <FirstName>Benny</FirstName>
        <LastName>Broska</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>481232083</PlayerID>
        <RoleID>101</RoleID>
        <FirstName>Vincent</FirstName>
        <LastName>Kleinekarhoff</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>460543038</PlayerID>
        <RoleID>108</RoleID>
        <FirstName>Silas</FirstName>
        <LastName>Bredtmann</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>493922143</PlayerID>
        <RoleID>109</RoleID>
        <FirstName>Ulf</FirstName>
        <LastName>Nitzsche</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>465914810</PlayerID>
        <RoleID>110</RoleID>
        <FirstName>Kai</FirstName>
        <LastName>Oeltze</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>485513496</PlayerID>
        <RoleID>112</RoleID>
        <FirstName>Johannes</FirstName>
        <LastName>Broska</LastName>
        <NickName></NickName>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>485116928</PlayerID>
        <RoleID>17</RoleID>
        <FirstName>Dierk</FirstName>
        <LastName>Riester</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>460543038</PlayerID>
        <RoleID>18</RoleID>
        <FirstName>Silas</FirstName>
        <LastName>Bredtmann</LastName>
        <NickName></NickName>
      </Player>
    </StartingLineup>
    <Substitutions>
      <Substitution>
        <TeamID>520472</TeamID>
        <SubjectPlayerID>493922143</SubjectPlayerID>
        <ObjectPlayerID>493526013</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>109</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>60</MatchMinute>
        <MatchPart>2</MatchPart>
      </Substitution>
      <Substitution>
        <TeamID>520472</TeamID>
        <SubjectPlayerID>481232083</SubjectPlayerID>
        <ObjectPlayerID>491124183</ObjectPlayerID>
        <OrderType>3</OrderType>
        <NewPositionId>107</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>91</MatchMinute>
        <MatchPart>3</MatchPart>
      </Substitution>
      <Substitution>
        <TeamID>520472</TeamID>
        <SubjectPlayerID>500430459</SubjectPlayerID>
        <ObjectPlayerID>451269636</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>102</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>91</MatchMinute>
        <MatchPart>3</MatchPart>
      </Substitution>
      <Substitution>
        <TeamID>520472</TeamID>
        <SubjectPlayerID>485116928</SubjectPlayerID>
        <ObjectPlayerID>469060724</ObjectPlayerID>
        <OrderType>1</OrderType>
        <NewPositionId>103</NewPositionId>
        <NewPositionBehaviour>0</NewPositionBehaviour>
        <MatchMinute>91</MatchMinute>
        <MatchPart>3</MatchPart>
      </Substitution>
    </Substitutions>
    <Lineup>
      <Player>
        <PlayerID>472298043</PlayerID>
        <RoleID>100</RoleID>
        <FirstName>Dionisis</FirstName>
        <LastName>Violaris</LastName>
        <NickName></NickName>
        <RatingStars>3</RatingStars>
        <RatingStarsEndOfMatch>1.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>491124183</PlayerID>
        <RoleID>101</RoleID>
        <FirstName>Wilfried</FirstName>
        <LastName>Ellhardt</LastName>
        <NickName></NickName>
        <RatingStars>7</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>451269636</PlayerID>
        <RoleID>102</RoleID>
        <FirstName>Pit</FirstName>
        <LastName>Drexler</LastName>
        <NickName></NickName>
        <RatingStars>5</RatingStars>
        <RatingStarsEndOfMatch>4.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>469060724</PlayerID>
        <RoleID>103</RoleID>
        <FirstName>Hardy</FirstName>
        <LastName>Diepelt</LastName>
        <NickName></NickName>
        <RatingStars>6</RatingStars>
        <RatingStarsEndOfMatch>6</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>513041996</PlayerID>
        <RoleID>104</RoleID>
        <FirstName>Gert</FirstName>
        <LastName>Hammes</LastName>
        <NickName></NickName>
        <RatingStars>3.5</RatingStars>
        <RatingStarsEndOfMatch>2.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>497671194</PlayerID>
        <RoleID>105</RoleID>
        <FirstName>Benny</FirstName>
        <LastName>Broska</LastName>
        <NickName></NickName>
        <RatingStars>5.5</RatingStars>
        <RatingStarsEndOfMatch>4.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>481232083</PlayerID>
        <RoleID>107</RoleID>
        <FirstName>Vincent</FirstName>
        <LastName>Kleinekarhoff</LastName>
        <NickName></NickName>
        <RatingStars>5</RatingStars>
        <RatingStarsEndOfMatch>4</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>460543038</PlayerID>
        <RoleID>108</RoleID>
        <FirstName>Silas</FirstName>
        <LastName>Bredtmann</LastName>
        <NickName></NickName>
        <RatingStars>7.5</RatingStars>
        <RatingStarsEndOfMatch>4</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>493526013</PlayerID>
        <RoleID>109</RoleID>
        <FirstName>Zlatko</FirstName>
        <LastName>Adžić</LastName>
        <NickName></NickName>
        <RatingStars>4</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>465914810</PlayerID>
        <RoleID>110</RoleID>
        <FirstName>Kai</FirstName>
        <LastName>Oeltze</LastName>
        <NickName></NickName>
        <RatingStars>4.5</RatingStars>
        <RatingStarsEndOfMatch>2.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>485513496</PlayerID>
        <RoleID>112</RoleID>
        <FirstName>Johannes</FirstName>
        <LastName>Broska</LastName>
        <NickName></NickName>
        <RatingStars>5</RatingStars>
        <RatingStarsEndOfMatch>3.5</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>453419222</PlayerID>
        <RoleID>117</RoleID>
        <FirstName>René</FirstName>
        <LastName>Eisfeld</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>488169611</PlayerID>
        <RoleID>118</RoleID>
        <FirstName>Constantin</FirstName>
        <LastName>Hollerbach</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>456806492</PlayerID>
        <RoleID>119</RoleID>
        <FirstName>Horst</FirstName>
        <LastName>Hainbuchen</LastName>
        <NickName></NickName>
        <RatingStars>0</RatingStars>
        <RatingStarsEndOfMatch>0</RatingStarsEndOfMatch>
        <Behaviour>0</Behaviour>
      </Player>
      <Player>
        <PlayerID>451269636</PlayerID>
        <RoleID>17</RoleID>
        <FirstName>Pit</FirstName>
        <LastName>Drexler</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>460543038</PlayerID>
        <RoleID>18</RoleID>
        <FirstName>Silas</FirstName>
        <LastName>Bredtmann</LastName>
        <NickName></NickName>
      </Player>
      <Player>
        <PlayerID>493922143</PlayerID>
        <RoleID>19</RoleID>
        <FirstName>Ulf</FirstName>
        <LastName>Nitzsche</LastName>
        <NickName></NickName>
        <RatingStars>5.5</RatingStars>
        <RatingStarsEndOfMatch>4</RatingStarsEndOfMatch>
      </Player>
      <Player>
        <PlayerID>500430459</PlayerID>
        <RoleID>20</RoleID>
        <FirstName>Gerome</FirstName>
        <LastName>Kalliss</LastName>
        <NickName></NickName>
        <RatingStars>5.5</RatingStars>
        <RatingStarsEndOfMatch>4.5</RatingStarsEndOfMatch>
      </Player>
      <Player>
        <PlayerID>485116928</PlayerID>
        <RoleID>21</RoleID>
        <FirstName>Dierk</FirstName>
        <LastName>Riester</LastName>
        <NickName></NickName>
        <RatingStars>5.5</RatingStars>
        <RatingStarsEndOfMatch>5</RatingStarsEndOfMatch>
      </Player>
    </Lineup>
  </Team>
</HattrickData>
        """;

    @Test
    void parseMatchdetailsFromString_withMatchLineupIsNull() {
        // given

//        final DBManager dbManager = mock(DBManager.class);
//
//        try (MockedStatic<DBManager> mockedDbManager = mockStatic(DBManager.class)) {
//            mockedDbManager.when(DBManager::instance).thenReturn(dbManager);
//
//            when(dbManager.getMatchHighlights(anyInt(), anyInt())).thenReturn(List.of());

            // when
            final var matchdetails = XMLMatchdetailsParser.parseMatchdetailsFromString(xmlFileMatchDetails, null);

            // then
            assertThat(matchdetails.getArenaName()).isEqualTo("Alte Rösterei");
            assertThat(matchdetails.getGuestTeamName()).isEqualTo("SSC Napoli-Köpenick");
            assertThat(matchdetails.getHomeTeamName()).isEqualTo("Kaffee-Junkies Hannover");
            assertThat(matchdetails.getMatchreport()).isEqualTo("");
            assertThat(matchdetails.getFetchDatum()).isEqualTo(HODateTime.fromHT("2026-10-08 12:36:42"));
            assertThat(matchdetails.getMatchDate()).isEqualTo(HODateTime.fromHT("2026-10-06 18:15:00"));
            assertThat(matchdetails.getHighlights()).isEmpty();
            assertThat(matchdetails.getArenaID()).isEqualTo(2154092);
            assertThat(matchdetails.getGuestTeamId()).isEqualTo(128206);
            assertThat(matchdetails.getGuestEinstellung()).isEqualTo(IMatchDetails.EINSTELLUNG_UNBEKANNT);
            assertThat(matchdetails.getGuestGoals()).isEqualTo(0);
            assertThat(matchdetails.getGuestLeftAtt()).isEqualTo(35);
            assertThat(matchdetails.getGuestLeftDef()).isEqualTo(10);
            assertThat(matchdetails.getGuestMidAtt()).isEqualTo(17);
            assertThat(matchdetails.getGuestMidDef()).isEqualTo(16);
            assertThat(matchdetails.getGuestMidfield()).isEqualTo(18);
            assertThat(matchdetails.getGuestRightAtt()).isEqualTo(25);
            assertThat(matchdetails.getGuestRightDef()).isEqualTo(15);
            assertThat(matchdetails.getGuestHatStats()).isEqualTo(172);
            assertThat(matchdetails.getGuestTacticSkill()).isEqualTo(0);
            assertThat(matchdetails.getGuestTacticType()).isEqualTo(IMatchDetails.TAKTIK_NORMAL);
            assertThat(matchdetails.getHomeTeamId()).isEqualTo(2157350);
            assertThat(matchdetails.getMatchType()).isEqualTo(MatchType.FRIENDLYNORMAL);
            assertThat(matchdetails.getHomeEinstellung()).isEqualTo(IMatchDetails.EINSTELLUNG_NORMAL);
            assertThat(matchdetails.getHomeGoals()).isEqualTo(7);
            assertThat(matchdetails.getHomeLeftAtt()).isEqualTo(21);
            assertThat(matchdetails.getHomeLeftDef()).isEqualTo(28);
            assertThat(matchdetails.getHomeMidAtt()).isEqualTo(18);
            assertThat(matchdetails.getHomeMidDef()).isEqualTo(31);
            assertThat(matchdetails.getHomeMidfield()).isEqualTo(22);
            assertThat(matchdetails.getHomeRightAtt()).isEqualTo(16);
            assertThat(matchdetails.getHomeRightDef()).isEqualTo(29);
            assertThat(matchdetails.getHomeHatStats()).isEqualTo(209);
            assertThat(matchdetails.getHomeTacticSkill()).isEqualTo(11);
            assertThat(matchdetails.getHomeTacticType()).isEqualTo(IMatchDetails.TAKTIK_CREATIVE);
            assertThat(matchdetails.getMatchID()).isEqualTo(771794947);
            assertThat(matchdetails.getWetterId()).isEqualTo(Weather.OVERCAST.getId());
            assertThat(matchdetails.getZuschauer()).isEqualTo(1648);
            assertThat(matchdetails.getCupLevel()).isEqualTo(CupLevel.NONE);
            assertThat(matchdetails.getCupLevelIndex()).isEqualTo(CupLevelIndex.NONE);
            assertThat(matchdetails.getMatchContextId()).isEqualTo(0);
            assertThat(matchdetails.getSoldTerraces()).isEqualTo(938);
            assertThat(matchdetails.getSoldBasic()).isEqualTo(390);
            assertThat(matchdetails.getSoldRoof()).isEqualTo(280);
            assertThat(matchdetails.getSoldVIP()).isEqualTo(40);
            assertThat(matchdetails.getRegionId()).isNull();
            assertThat(matchdetails.getHomeRatingIndirectSetPiecesAtt()).isEqualTo(18);
            assertThat(matchdetails.getHomeRatingIndirectSetPiecesDef()).isEqualTo(22);
            assertThat(matchdetails.getGuestRatingIndirectSetPiecesAtt()).isEqualTo(18);
            assertThat(matchdetails.getGuestRatingIndirectSetPiecesDef()).isEqualTo(15);
            assertThat(matchdetails.getM_Injuries()).isEmpty();
            // homeGoalsInParts in another test
            // guestGoalsInParts in another test
            assertThat(matchdetails.getFormation(true)).isEqualTo("4-4-2");
            assertThat(matchdetails.getFormation(false)).isEqualTo("4-5-1");
            assertThat(matchdetails.getResult()).isEqualTo(" 7 !ls.match.result.separation! 0");
//        }
    }

    @Test
    void parseMatchdetailsFromString_withMatchLineup() {
        // given

        var matchLineup = XMLMatchLineupParser.parseMatchLineupFromString(xmlFileMatchLineupTeam1);
        matchLineup.setGuestTeam(XMLMatchLineupParser.parseMatchLineupFromString(xmlFileMatchLineupTeam2).getGuestTeam());

        final var expectedMatchDate = HODateTime.fromHT("2026-10-06 18:15:00");

        final var expectedMatchEvents = new ArrayList<MatchEvent>();
        expectedMatchEvents.add(createExpectedMatchEvent(1, Matchdetails.eInjuryType.NA, 50000, 31, 5, 0, 0,2154092, true, 1648, true,  "<a href=\"/Club/Stadium/?stadiumId=2154092\">Alte Rösterei</a> - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. <a href=\"/Club/HallOfFame/Player.aspx?playerId=455444108\">Sultan Hafeeth Al-Buayqy</a> wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten <a href=\"/Club/HallOfFame/Player.aspx?playerId=474275056\">Bill Matthews</a> und <a href=\"/Club/HallOfFame/Player.aspx?playerId=459237940\">Idar Åkesson</a>."));
        expectedMatchEvents.add(createExpectedMatchEvent(2, Matchdetails.eInjuryType.NA, 0, 19, 1, 0, 0,0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(3, Matchdetails.eInjuryType.NA, 2157350, 21, 4, 0, 0,0, true, 0, true, "Folgende Spieler begannen: <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"Sievers\" class=\"homeplayer\">Sievers</a> - <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Eggimann\" class=\"homeplayer\">Eggimann</a>, <a href=\"/Club/Players/Player.aspx?playerId=480433526\" title=\"Br&#252;ggemann\" class=\"homeplayer\">Brüggemann</a>, <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Mahler\" class=\"homeplayer\">Mahler</a>, <a href=\"/Club/Players/Player.aspx?playerId=503053978\" title=\"Cherundolo\" class=\"homeplayer\">Cherundolo</a> - <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Klaus\" class=\"homeplayer\">Klaus</a>, <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Pinto\" class=\"homeplayer\">Pinto</a>, <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Leopold\" class=\"homeplayer\">Leopold</a>, <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Maina\" class=\"homeplayer\">Maina</a> - <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Lerf\" class=\"homeplayer\">Lerf</a>, <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Stendel\" class=\"homeplayer\">Stendel</a>.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(4, Matchdetails.eInjuryType.NA, 128206, 21, 1, 0, 0,0, true, 0, true, "Es liefen auf: <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Schlegelmilch\" class=\"awayplayer\">Schlegelmilch</a> - <a href=\"/Club/Players/Player.aspx?playerId=489791494\" title=\"Masciadri\" class=\"awayplayer\">Masciadri</a>, <a href=\"/Club/Players/Player.aspx?playerId=496877677\" title=\"Gierer\" class=\"awayplayer\">Gierer</a>, <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Rutherford\" class=\"awayplayer\">Rutherford</a>, <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Parmendola\" class=\"awayplayer\">Parmendola</a> - <a href=\"/Club/Players/Player.aspx?playerId=490747950\" title=\"Rajs\" class=\"awayplayer\">Rajs</a>, <a href=\"/Club/Players/Player.aspx?playerId=490797976\" title=\"Hultflod\" class=\"awayplayer\">Hultflod</a>, <a href=\"/Club/Players/Player.aspx?playerId=474503458\" title=\"Foltea\" class=\"awayplayer\">Foltea</a>, <a href=\"/Club/Players/Player.aspx?playerId=472214265\" title=\"Sellinger\" class=\"awayplayer\">Sellinger</a>, <a href=\"/Club/Players/Player.aspx?playerId=492622921\" title=\"Adderton\" class=\"awayplayer\">Adderton</a> - <a href=\"/Club/Players/Player.aspx?playerId=472437980\" title=\"Schichta\" class=\"awayplayer\">Schichta</a>.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(5, Matchdetails.eInjuryType.NA, 0, 24, 2, 0, 0,1040402, true, 1040501, true, "<span class=\"hometeam\">Kaffee-Junkies Hannover</span> lief in einem 4-4-2 auf,  <span class=\"awayteam\">SSC Napoli-Köpenick</span> entschied sich für ein 4-5-1. "));
        expectedMatchEvents.add(createExpectedMatchEvent(6, Matchdetails.eInjuryType.NA, 0, 598, 2, 0, 0,0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(7, Matchdetails.eInjuryType.NA, 2157350, 335, 4, 1, 0,151, true, 0, true, "<span class=\"hometeam\">KJH</span> befleißigte sich einer kreativen Spielweise. Sieht <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=11#skill\" class=\"skill\">brillant</a> aus, was die Taktikstärke angeht. "));
        expectedMatchEvents.add(createExpectedMatchEvent(8, Matchdetails.eInjuryType.NA, 2157350, 481, 5, 1, 5,0, true, 1040402, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(9, Matchdetails.eInjuryType.NA, 2157350, 121, 1, 1, 8,456920840, false, 472120229, false, "Ein ohrenbetäubendes Vuvuzela-Konzert brach in der 8. Minute auf den Tribünen aus, nachdem <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> zentral vor dem Tor abgezogen und einen Treffer für <span class=\"hometeam\">KJH</span> markiert hatte. Es stand nun 1 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(10, Matchdetails.eInjuryType.NA, 2157350, 61, 1, 1, 11,95, false, 0, true, "Bei <span class=\"hometeam\">KJH</span> schien nach 11 Minuten keiner wirklich zu wissen, wo er zu stehen hat. Die Mannschaft war <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=7#skill\" class=\"skill\">gut</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(11, Matchdetails.eInjuryType.NA, 2157350, 471, 4, 1, 13,59, false, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(12, Matchdetails.eInjuryType.NA, 2157350, 352, 5, 1, 15,511385914, false, 503221197, false, "Vollkommen überrascht war <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Linton Maina\" class=\"homeplayer\">Linton Maina</a> von seiner Auswechslung in der 15. Minute. Bei <span class=\"hometeam\">KJH</span> betrat an seiner Stelle <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> den Rasen."));
        expectedMatchEvents.add(createExpectedMatchEvent(13, Matchdetails.eInjuryType.NA, 2157350, 61, 2, 1, 15,79, false, 0, true, "<span class=\"hometeam\">KJH</span> hatte offensichtlich Probleme mit den taktischen Anweisungen. Die Mannschaft war in der 15. Minute nur noch <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=4#skill\" class=\"skill\">schwach</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(14, Matchdetails.eInjuryType.NA, 2157350, 489, 5, 1,17,485463818, false, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(15, Matchdetails.eInjuryType.NA, 2157350, 289, 2, 1, 20,502246230, false, 483281921, false, "<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> erntete in der 20. Minute tosenden Applaus, als er einen Pfau einfing, der plötzlich durch seinen Strafraum stolzierte. Das Tier war bei den nahen Dreharbeiten zu \"Bauer sucht Pfau\" stiften gegangen. Diese spektakuläre Aktion war noch lange in aller Munde. Dass <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> noch in der gleichen Minute <span class=\"hometeam\">KJH</span>-Flitzer <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> den Ball vom Fuß spitzelte und so einen Gegentreffer verhinderte, ging dabei fast unter."));
        expectedMatchEvents.add(createExpectedMatchEvent(16, Matchdetails.eInjuryType.NA, 128206, 452, 3, 1, 22,472120229, false, 100, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(17, Matchdetails.eInjuryType.NA, 2157350, 131, 1, 1, 25,462788780, false, 472120229, false, "Nach 25 Minuten kam es für die Gäste noch schlimmer, als <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> es ohne große Widerstände durch die Abwehrmitte vor das Tor schaffte und zum 2 - 0 traf."));
        expectedMatchEvents.add(createExpectedMatchEvent(18, Matchdetails.eInjuryType.NA, 2157350, 116, 3, 1, 33,485463818, false, 503221197, false, "<span class=\"hometeam\">KJH</span>-Akteur <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> konnte sich in der 33. Minute dank seiner Schnelligkeit mühelos von seinem Gegenspieler absetzen. <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Derk Lerf\" class=\"homeplayer\">Derk Lerf</a> hatte keinerlei Schwierigkeit, die scharfe Hereingabe am herausstürzenden Torwart vorbei zum 3 - 0 zu verwandeln."));
        expectedMatchEvents.add(createExpectedMatchEvent(19, Matchdetails.eInjuryType.NA, 2157350, 231, 2, 1, 34,457053654, false, 472120229, false, "Eine wundervolle Kombination im Angriffszentrum hätte nach 34 Minuten fast zu einem weiteren Tor für <span class=\"hometeam\">KJH</span> geführt, doch <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Friederich Schlegelmilch\" class=\"awayplayer\">Friederich Schlegelmilch</a> war bei diesem Schuss von <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> auf dem Posten."));
        expectedMatchEvents.add(createExpectedMatchEvent(20, Matchdetails.eInjuryType.NA, 2157350, 134, 2, 1, 37,462788780, false, 472120229, false, "<span class=\"hometeam\">KJH</span> wurde in der 37. Minute ein Strafstoß zugesprochen. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> ließ sich die Chance nicht entgehen und verwandelte mit einem Lächeln auf dem Gesicht zum 4 - 0 für das Heimteam."));
        expectedMatchEvents.add(createExpectedMatchEvent(21, Matchdetails.eInjuryType.NA, 2157350, 218, 1, 1, 40,502246230, false, 462788780, false, "<a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> schlug nach 40 Minuten einen raffiniert herein gezogenen Eckball, doch <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> war zu überrascht, den Ball zu bekommen, und verstolperte die Chance für <span class=\"hometeam\">KJH</span>."));
        expectedMatchEvents.add(createExpectedMatchEvent(22, Matchdetails.eInjuryType.NA, 2157350, 232, 3, 1, 41,497733968, false, 472120229, false, "Nach einer Flanke von links spitzelte <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Mario Eggimann\" class=\"homeplayer\">Mario Eggimann</a> den Ball in Richtung Glück und Seligkeit, doch der Außenpfosten verhinderte ein weiteres Tor für <span class=\"hometeam\">KJH</span>. Da fiel dem Präsidium des gegnerischen Teams auf der Tribüne sichtlich ein Stein vom Herzen, dass diese Situation in der 41. Minute ein gutes Ende nahm. "));
        expectedMatchEvents.add(createExpectedMatchEvent(23, Matchdetails.eInjuryType.NA, 2157350, 510, 4, 1, 42,502246230, false, 0, true, "<span class=\"hometeam\">KJH</span>-Spieler <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> hatte es bei seiner Attacke in der 42. Minute nur auf die Beine des Gegenspielers abgesehen. Folgerichtig sah er die Gelbe Karte."));
        expectedMatchEvents.add(createExpectedMatchEvent(24, Matchdetails.eInjuryType.NA, 128206, 360, 3, 1, 43,472291599, false, 0, true, "Spielminute 43: Um das Team noch auf die Siegerstraße zu bringen, richtete der <span class=\"awayteam\">Napoli-Köpenick</span>-Trainer seine Mannen taktisch neu aus."));
        expectedMatchEvents.add(createExpectedMatchEvent(25, Matchdetails.eInjuryType.NA, 128206, 360, 5, 1, 44,496877677, false, 0, true, "Bei <span class=\"awayteam\">Napoli-Köpenick</span> war man über den Rückstand verständlicherweise nicht erfreut, daher formierten sich die Spieler nach 44 Minuten auf Anweisung ihres Trainers neu."));
        expectedMatchEvents.add(createExpectedMatchEvent(26, Matchdetails.eInjuryType.NA, 0, 45, 3, 1, 45,0, true, 0, true, "Zur Halbzeit stand es 4 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(27, Matchdetails.eInjuryType.NA, 2157350, 40, 4, 1, 45,0, true, 55, false, "<span class=\"hometeam\">KJH</span> war in dieser Halbzeit das dominierende Team - die Spieler brachten den Ballbesitz auf 55 Prozent.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(28, Matchdetails.eInjuryType.NA, 0, 597, 3, 2, 46,0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(29, Matchdetails.eInjuryType.NA, 2157350, 64, 4, 2, 46,456933528, false, 94, false, "Ein erneuter Blick auf die Taktiktafel in der Pause sorgte dafür, dass die Spieler von <span class=\"hometeam\">KJH</span> nun <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=6#skill\" class=\"skill\">passabel</a> organisiert waren."));
        expectedMatchEvents.add(createExpectedMatchEvent(30, Matchdetails.eInjuryType.NA, 2157350, 361, 3, 2, 46,473360703, false, 0, true, "Zufrieden mit dem Spielstand verordnete der <span class=\"hometeam\">KJH</span>-Trainer seiner Mannschaft in der 46. Minute eine neue taktische Ausrichtung."));
        expectedMatchEvents.add(createExpectedMatchEvent(31, Matchdetails.eInjuryType.NA, 2157350, 481, 2, 2, 51,0, true, 1040303, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(32, Matchdetails.eInjuryType.NA, 2157350, 132, 3, 2, 52,456920840, false, 472120229, false, "<span class=\"hometeam\">KJH</span> baute in der 52. Minute die Führung auf 5 - 0 aus, nachdem ein Verteidiger der Gäste den Ball leichtfertig an <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> verloren hatte. Dieser drang über links in den Strafraum ein und spielte dann noch gekonnt den Torwart aus, bevor er das Leder lässig über die Linie schob."));
        expectedMatchEvents.add(createExpectedMatchEvent(33, Matchdetails.eInjuryType.NA, 0, 464, 2, 2, 56,0, true, 1648, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(34, Matchdetails.eInjuryType.NA, 2157350, 352, 4, 2, 60,457053654, false, 461796896, false, "Darauf hatte <a href=\"/Club/Players/Player.aspx?playerId=461796896\" title=\"Lars Stindl\" class=\"homeplayer\">Lars Stindl</a> schon länger gewartet. Nach 60 Minuten war es soweit, er kam zu seinem Einsatz für <span class=\"hometeam\">KJH</span>. Im Gegenzug verließ <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> den Platz."));
        expectedMatchEvents.add(createExpectedMatchEvent(35, Matchdetails.eInjuryType.NA, 2157350, 61, 4, 2, 62,75, false, 0, true, "Spielminute 62: Bei <span class=\"hometeam\">KJH</span> herrschte ein ziemliches Durcheinander auf dem Feld - die Spieler hatten Probleme, ihre Positionen zu halten, und waren <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=3#skill\" class=\"skill\">armselig</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(36, Matchdetails.eInjuryType.NA, 2157350, 132, 2, 2, 63,462788780, false, 472120229, false, "Ein schnell vorgetragener Angriff von <span class=\"hometeam\">KJH</span> über die linke Seite des Feldes wurde in der 63. Minute mit einem weiteren Tor zum 6 - 0 gekrönt. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> hatte aus spitzem Winkel abgezogen. Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor."));
        expectedMatchEvents.add(createExpectedMatchEvent(37, Matchdetails.eInjuryType.NA, 2157350, 46, 1, 2, 63,462788780, false, 0, true, "Damit konnte sich <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> als Hattrickschütze notieren lassen."));
        expectedMatchEvents.add(createExpectedMatchEvent(38, Matchdetails.eInjuryType.NA, 128206, 494, 1, 2, 74,0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(39, Matchdetails.eInjuryType.NA, 128206, 251, 4, 2, 83,472291599, false, 473025501, false, "83 Minuten waren gespielt, als <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Raven Rutherford\" class=\"awayplayer\">Raven Rutherford</a> sich in der Mitte prima durchsetzte und nur noch <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"J&#246;rg &#39;Colt&#39; Sievers\" class=\"homeplayer\">Jörg 'Colt' Sievers</a> vor sich hatte. Der blieb lange stehen und wehrte den Schuss ab. Es blieb beim 6 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(40, Matchdetails.eInjuryType.NA, 2157350, 133, 4, 2, 84,473360703, false, 472120229, false, "Eine schöne Ballstafette auf dem rechten Flügel der Gastgeber konnte von der gegnerischen Abwehr nicht unterbunden werden. So gelangte das Objekt der Begierde schließlich zu\n" +
            "                    <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Sergio Pinto\" class=\"homeplayer\">Sergio Pinto</a>, der ein weiteres Tor für <span class=\"hometeam\">KJH</span> erzielte. Die Führung erhöhte sich dadurch in der 84. Minute auf 7 - 0.\n" +
            "                    Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor."));
        expectedMatchEvents.add(createExpectedMatchEvent(41, Matchdetails.eInjuryType.NA, 0, 75, 5, 2, 89,0, true, 0, true, "Die Nachspielzeit beträgt 3 Minute(n)."));
        expectedMatchEvents.add(createExpectedMatchEvent(42, Matchdetails.eInjuryType.NA, 0, 599, 3, 2, 93,0, true, 0, true, "Die Begegnung endete 7 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(43, Matchdetails.eInjuryType.NA, 2157350, 601, 3, 2, 93,0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(44, Matchdetails.eInjuryType.NA, 2157350, 40, 1, 2, 93,0, true, 53, false, "<span class=\"hometeam\">KJH</span> erreichte laut HT-Datenbank 53 Prozent Ballbesitz.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(45, Matchdetails.eInjuryType.NA, 2157350, 41, 2, 2, 93,462788780, false, 0, true, "Der beste Spieler von <span class=\"hometeam\">KJH</span> war ohne jeden Zweifel <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a>."));
        expectedMatchEvents.add(createExpectedMatchEvent(46, Matchdetails.eInjuryType.NA, 128206, 41, 3, 2, 93,483281921, false, 0, true, "Nach diesem Spiel forderten die Fans von <span class=\"awayteam\">Napoli-Köpenick</span> eine \"<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a>-Straße\" - er hatte eine herausragende Leistung abgeliefert."));


//        final DBManager dbManager = mock(DBManager.class);
//
//        try (MockedStatic<DBManager> mockedDbManager = mockStatic(DBManager.class)) {
//            mockedDbManager.when(DBManager::instance).thenReturn(dbManager);
//
//            when(dbManager.getMatchHighlights(anyInt(), anyInt())).thenReturn(List.of());
//
            // when
            final var matchdetails = XMLMatchdetailsParser.parseMatchdetailsFromString(xmlFileMatchDetails, matchLineup);



            // then
            assertThat(matchdetails.getArenaName()).isEqualTo("Alte Rösterei");
            assertThat(matchdetails.getGuestTeamName()).isEqualTo("SSC Napoli-Köpenick");
            assertThat(matchdetails.getHomeTeamName()).isEqualTo("Kaffee-Junkies Hannover");
            assertThat(matchdetails.getMatchreport()).isEqualTo("<a href=\"/Club/Stadium/?stadiumId=2154092\">Alte Rösterei</a> - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. <a href=\"/Club/HallOfFame/Player.aspx?playerId=455444108\">Sultan Hafeeth Al-Buayqy</a> wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten <a href=\"/Club/HallOfFame/Player.aspx?playerId=474275056\">Bill Matthews</a> und <a href=\"/Club/HallOfFame/Player.aspx?playerId=459237940\">Idar Åkesson</a>.  Folgende Spieler begannen: <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"Sievers\" class=\"homeplayer\">Sievers</a> - <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Eggimann\" class=\"homeplayer\">Eggimann</a>, <a href=\"/Club/Players/Player.aspx?playerId=480433526\" title=\"Br&#252;ggemann\" class=\"homeplayer\">Brüggemann</a>, <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Mahler\" class=\"homeplayer\">Mahler</a>, <a href=\"/Club/Players/Player.aspx?playerId=503053978\" title=\"Cherundolo\" class=\"homeplayer\">Cherundolo</a> - <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Klaus\" class=\"homeplayer\">Klaus</a>, <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Pinto\" class=\"homeplayer\">Pinto</a>, <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Leopold\" class=\"homeplayer\">Leopold</a>, <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Maina\" class=\"homeplayer\">Maina</a> - <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Lerf\" class=\"homeplayer\">Lerf</a>, <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Stendel\" class=\"homeplayer\">Stendel</a>.<br ><br > Es liefen auf: <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Schlegelmilch\" class=\"awayplayer\">Schlegelmilch</a> - <a href=\"/Club/Players/Player.aspx?playerId=489791494\" title=\"Masciadri\" class=\"awayplayer\">Masciadri</a>, <a href=\"/Club/Players/Player.aspx?playerId=496877677\" title=\"Gierer\" class=\"awayplayer\">Gierer</a>, <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Rutherford\" class=\"awayplayer\">Rutherford</a>, <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Parmendola\" class=\"awayplayer\">Parmendola</a> - <a href=\"/Club/Players/Player.aspx?playerId=490747950\" title=\"Rajs\" class=\"awayplayer\">Rajs</a>, <a href=\"/Club/Players/Player.aspx?playerId=490797976\" title=\"Hultflod\" class=\"awayplayer\">Hultflod</a>, <a href=\"/Club/Players/Player.aspx?playerId=474503458\" title=\"Foltea\" class=\"awayplayer\">Foltea</a>, <a href=\"/Club/Players/Player.aspx?playerId=472214265\" title=\"Sellinger\" class=\"awayplayer\">Sellinger</a>, <a href=\"/Club/Players/Player.aspx?playerId=492622921\" title=\"Adderton\" class=\"awayplayer\">Adderton</a> - <a href=\"/Club/Players/Player.aspx?playerId=472437980\" title=\"Schichta\" class=\"awayplayer\">Schichta</a>.<br ><br > <span class=\"hometeam\">Kaffee-Junkies Hannover</span> lief in einem 4-4-2 auf,  <span class=\"awayteam\">SSC Napoli-Köpenick</span> entschied sich für ein 4-5-1.   <span class=\"hometeam\">KJH</span> befleißigte sich einer kreativen Spielweise. Sieht <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=11#skill\" class=\"skill\">brillant</a> aus, was die Taktikstärke angeht.   Ein ohrenbetäubendes Vuvuzela-Konzert brach in der 8. Minute auf den Tribünen aus, nachdem <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> zentral vor dem Tor abgezogen und einen Treffer für <span class=\"hometeam\">KJH</span> markiert hatte. Es stand nun 1 - 0. Bei <span class=\"hometeam\">KJH</span> schien nach 11 Minuten keiner wirklich zu wissen, wo er zu stehen hat. Die Mannschaft war <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=7#skill\" class=\"skill\">gut</a> organisiert.  Vollkommen überrascht war <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Linton Maina\" class=\"homeplayer\">Linton Maina</a> von seiner Auswechslung in der 15. Minute. Bei <span class=\"hometeam\">KJH</span> betrat an seiner Stelle <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> den Rasen. <span class=\"hometeam\">KJH</span> hatte offensichtlich Probleme mit den taktischen Anweisungen. Die Mannschaft war in der 15. Minute nur noch <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=4#skill\" class=\"skill\">schwach</a> organisiert.  <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> erntete in der 20. Minute tosenden Applaus, als er einen Pfau einfing, der plötzlich durch seinen Strafraum stolzierte. Das Tier war bei den nahen Dreharbeiten zu \"Bauer sucht Pfau\" stiften gegangen. Diese spektakuläre Aktion war noch lange in aller Munde. Dass <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> noch in der gleichen Minute <span class=\"hometeam\">KJH</span>-Flitzer <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> den Ball vom Fuß spitzelte und so einen Gegentreffer verhinderte, ging dabei fast unter.  Nach 25 Minuten kam es für die Gäste noch schlimmer, als <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> es ohne große Widerstände durch die Abwehrmitte vor das Tor schaffte und zum 2 - 0 traf. <span class=\"hometeam\">KJH</span>-Akteur <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> konnte sich in der 33. Minute dank seiner Schnelligkeit mühelos von seinem Gegenspieler absetzen. <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Derk Lerf\" class=\"homeplayer\">Derk Lerf</a> hatte keinerlei Schwierigkeit, die scharfe Hereingabe am herausstürzenden Torwart vorbei zum 3 - 0 zu verwandeln. Eine wundervolle Kombination im Angriffszentrum hätte nach 34 Minuten fast zu einem weiteren Tor für <span class=\"hometeam\">KJH</span> geführt, doch <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Friederich Schlegelmilch\" class=\"awayplayer\">Friederich Schlegelmilch</a> war bei diesem Schuss von <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> auf dem Posten. <span class=\"hometeam\">KJH</span> wurde in der 37. Minute ein Strafstoß zugesprochen. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> ließ sich die Chance nicht entgehen und verwandelte mit einem Lächeln auf dem Gesicht zum 4 - 0 für das Heimteam. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> schlug nach 40 Minuten einen raffiniert herein gezogenen Eckball, doch <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> war zu überrascht, den Ball zu bekommen, und verstolperte die Chance für <span class=\"hometeam\">KJH</span>. Nach einer Flanke von links spitzelte <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Mario Eggimann\" class=\"homeplayer\">Mario Eggimann</a> den Ball in Richtung Glück und Seligkeit, doch der Außenpfosten verhinderte ein weiteres Tor für <span class=\"hometeam\">KJH</span>. Da fiel dem Präsidium des gegnerischen Teams auf der Tribüne sichtlich ein Stein vom Herzen, dass diese Situation in der 41. Minute ein gutes Ende nahm.  <span class=\"hometeam\">KJH</span>-Spieler <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> hatte es bei seiner Attacke in der 42. Minute nur auf die Beine des Gegenspielers abgesehen. Folgerichtig sah er die Gelbe Karte. Spielminute 43: Um das Team noch auf die Siegerstraße zu bringen, richtete der <span class=\"awayteam\">Napoli-Köpenick</span>-Trainer seine Mannen taktisch neu aus. Bei <span class=\"awayteam\">Napoli-Köpenick</span> war man über den Rückstand verständlicherweise nicht erfreut, daher formierten sich die Spieler nach 44 Minuten auf Anweisung ihres Trainers neu. Zur Halbzeit stand es 4 - 0. <span class=\"hometeam\">KJH</span> war in dieser Halbzeit das dominierende Team - die Spieler brachten den Ballbesitz auf 55 Prozent.<br ><br >  Ein erneuter Blick auf die Taktiktafel in der Pause sorgte dafür, dass die Spieler von <span class=\"hometeam\">KJH</span> nun <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=6#skill\" class=\"skill\">passabel</a> organisiert waren. Zufrieden mit dem Spielstand verordnete der <span class=\"hometeam\">KJH</span>-Trainer seiner Mannschaft in der 46. Minute eine neue taktische Ausrichtung.  <span class=\"hometeam\">KJH</span> baute in der 52. Minute die Führung auf 5 - 0 aus, nachdem ein Verteidiger der Gäste den Ball leichtfertig an <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> verloren hatte. Dieser drang über links in den Strafraum ein und spielte dann noch gekonnt den Torwart aus, bevor er das Leder lässig über die Linie schob.  Darauf hatte <a href=\"/Club/Players/Player.aspx?playerId=461796896\" title=\"Lars Stindl\" class=\"homeplayer\">Lars Stindl</a> schon länger gewartet. Nach 60 Minuten war es soweit, er kam zu seinem Einsatz für <span class=\"hometeam\">KJH</span>. Im Gegenzug verließ <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> den Platz. Spielminute 62: Bei <span class=\"hometeam\">KJH</span> herrschte ein ziemliches Durcheinander auf dem Feld - die Spieler hatten Probleme, ihre Positionen zu halten, und waren <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=3#skill\" class=\"skill\">armselig</a> organisiert. Ein schnell vorgetragener Angriff von <span class=\"hometeam\">KJH</span> über die linke Seite des Feldes wurde in der 63. Minute mit einem weiteren Tor zum 6 - 0 gekrönt. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> hatte aus spitzem Winkel abgezogen. Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor. Damit konnte sich <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> als Hattrickschütze notieren lassen.  83 Minuten waren gespielt, als <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Raven Rutherford\" class=\"awayplayer\">Raven Rutherford</a> sich in der Mitte prima durchsetzte und nur noch <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"J&#246;rg &#39;Colt&#39; Sievers\" class=\"homeplayer\">Jörg 'Colt' Sievers</a> vor sich hatte. Der blieb lange stehen und wehrte den Schuss ab. Es blieb beim 6 - 0. Eine schöne Ballstafette auf dem rechten Flügel der Gastgeber konnte von der gegnerischen Abwehr nicht unterbunden werden. So gelangte das Objekt der Begierde schließlich zu\n" +
                "                    <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Sergio Pinto\" class=\"homeplayer\">Sergio Pinto</a>, der ein weiteres Tor für <span class=\"hometeam\">KJH</span> erzielte. Die Führung erhöhte sich dadurch in der 84. Minute auf 7 - 0.\n" +
                "                    Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor. Die Nachspielzeit beträgt 3 Minute(n). Die Begegnung endete 7 - 0.  <span class=\"hometeam\">KJH</span> erreichte laut HT-Datenbank 53 Prozent Ballbesitz.<br ><br > Der beste Spieler von <span class=\"hometeam\">KJH</span> war ohne jeden Zweifel <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a>. Nach diesem Spiel forderten die Fans von <span class=\"awayteam\">Napoli-Köpenick</span> eine \"<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a>-Straße\" - er hatte eine herausragende Leistung abgeliefert. ");
            assertThat(matchdetails.getFetchDatum()).isEqualTo(HODateTime.fromHT("2026-10-08 12:36:42"));
            assertThat(matchdetails.getMatchDate()).isEqualTo(expectedMatchDate);
            assertThat(matchdetails.getHighlights()).hasSize(expectedMatchEvents.size());


            for (var i=0; i<matchdetails.getHighlights().size(); i++) {
                var event =  matchdetails.getHighlights().get(i);
                var expectedEvent = expectedMatchEvents.get(i);

                assertThat(event.getMatchEventIndex()).isEqualTo(expectedEvent.getMatchEventIndex());
                assertThat(event.getM_eInjuryType()).isEqualTo(expectedEvent.getM_eInjuryType());
                assertThat(event.getTeamID()).isEqualTo(expectedEvent.getTeamID());
                assertThat(event.getMatchEventID()).isEqualTo(expectedEvent.getMatchEventID());
                assertThat(event.getEventVariation()).isEqualTo(expectedEvent.getEventVariation());
                assertThat(event.getMatchPartId()).isEqualTo(expectedEvent.getMatchPartId());
                assertThat(event.getMinute()).isEqualTo(expectedEvent.getMinute());
                assertThat(event.getPlayerId()).isEqualTo(expectedEvent.getPlayerId());
                assertThat(event.getAssistingPlayerId()).isEqualTo(expectedEvent.getAssistingPlayerId());
                assertThat(event.getEventText()).isEqualTo(expectedEvent.getEventText());
                assertThat(event.getSpielerHeim()).isEqualTo(expectedEvent.getSpielerHeim());
                assertThat(event.getGehilfeHeim()).isEqualTo(expectedEvent.getGehilfeHeim());


                assertThatIsEqualTo(event, expectedEvent);
            }

            matchdetails.getHighlights().forEach(highlight -> assertThat(highlight.getMatchId()).isEqualTo(0));
            assertThat(matchdetails.getArenaID()).isEqualTo(2154092);
            assertThat(matchdetails.getGuestTeamId()).isEqualTo(128206);
            assertThat(matchdetails.getGuestEinstellung()).isEqualTo(IMatchDetails.EINSTELLUNG_UNBEKANNT);
            assertThat(matchdetails.getGuestGoals()).isEqualTo(0);
            assertThat(matchdetails.getGuestLeftAtt()).isEqualTo(35);
            assertThat(matchdetails.getGuestLeftDef()).isEqualTo(10);
            assertThat(matchdetails.getGuestMidAtt()).isEqualTo(17);
            assertThat(matchdetails.getGuestMidDef()).isEqualTo(16);
            assertThat(matchdetails.getGuestMidfield()).isEqualTo(18);
            assertThat(matchdetails.getGuestRightAtt()).isEqualTo(25);
            assertThat(matchdetails.getGuestRightDef()).isEqualTo(15);
            assertThat(matchdetails.getGuestHatStats()).isEqualTo(172);
            assertThat(matchdetails.getGuestTacticSkill()).isEqualTo(0);
            assertThat(matchdetails.getGuestTacticType()).isEqualTo(IMatchDetails.TAKTIK_NORMAL);
            assertThat(matchdetails.getHomeTeamId()).isEqualTo(2157350);
            assertThat(matchdetails.getMatchType()).isEqualTo(MatchType.FRIENDLYNORMAL);
            assertThat(matchdetails.getHomeEinstellung()).isEqualTo(IMatchDetails.EINSTELLUNG_NORMAL);
            assertThat(matchdetails.getHomeGoals()).isEqualTo(7);
            assertThat(matchdetails.getHomeLeftAtt()).isEqualTo(21);
            assertThat(matchdetails.getHomeLeftDef()).isEqualTo(28);
            assertThat(matchdetails.getHomeMidAtt()).isEqualTo(18);
            assertThat(matchdetails.getHomeMidDef()).isEqualTo(31);
            assertThat(matchdetails.getHomeMidfield()).isEqualTo(22);
            assertThat(matchdetails.getHomeRightAtt()).isEqualTo(16);
            assertThat(matchdetails.getHomeRightDef()).isEqualTo(29);
            assertThat(matchdetails.getHomeHatStats()).isEqualTo(209);
            assertThat(matchdetails.getHomeTacticSkill()).isEqualTo(11);
            assertThat(matchdetails.getHomeTacticType()).isEqualTo(IMatchDetails.TAKTIK_CREATIVE);
            assertThat(matchdetails.getMatchID()).isEqualTo(771794947);
            assertThat(matchdetails.getWetterId()).isEqualTo(Weather.OVERCAST.getId());
            assertThat(matchdetails.getZuschauer()).isEqualTo(1648);
            assertThat(matchdetails.getCupLevel()).isEqualTo(CupLevel.NONE);
            assertThat(matchdetails.getCupLevelIndex()).isEqualTo(CupLevelIndex.NONE);
            assertThat(matchdetails.getMatchContextId()).isEqualTo(0);
            assertThat(matchdetails.getSoldTerraces()).isEqualTo(938);
            assertThat(matchdetails.getSoldBasic()).isEqualTo(390);
            assertThat(matchdetails.getSoldRoof()).isEqualTo(280);
            assertThat(matchdetails.getSoldVIP()).isEqualTo(40);
            assertThat(matchdetails.getRegionId()).isNull();
            assertThat(matchdetails.getHomeRatingIndirectSetPiecesAtt()).isEqualTo(18);
            assertThat(matchdetails.getHomeRatingIndirectSetPiecesDef()).isEqualTo(22);
            assertThat(matchdetails.getGuestRatingIndirectSetPiecesAtt()).isEqualTo(18);
            assertThat(matchdetails.getGuestRatingIndirectSetPiecesDef()).isEqualTo(15);
            assertThat(matchdetails.getM_Injuries()).isEmpty();
            // homeGoalsInParts in another test
            // guestGoalsInParts in another test
            assertThat(matchdetails.getFormation(true)).isEqualTo("4-4-2");
            assertThat(matchdetails.getFormation(false)).isEqualTo("4-5-1");
            assertThat(matchdetails.getResult()).isEqualTo(" 7 !ls.match.result.separation! 0");
            assertThat(matchLineup.getHomeTeam().getMatchTeamAttitude()).isEqualTo(MatchTeamAttitude.fromInt(matchdetails.getHomeEinstellung()));
            assertThat(matchLineup.getHomeTeam().getMatchTacticType()).isEqualTo(MatchTacticType.fromInt(matchdetails.getHomeTacticType()));
            assertThat(matchLineup.getGuestTeam().getMatchTeamAttitude()).isEqualTo(MatchTeamAttitude.fromInt(matchdetails.getGuestEinstellung()));
            assertThat(matchLineup.getGuestTeam().getMatchTacticType()).isEqualTo(MatchTacticType.fromInt(matchdetails.getGuestTacticType()));
//        }
    }

    private MatchEvent createExpectedMatchEvent(int eventIndex, Matchdetails.eInjuryType injuryType, int teamID, int matchEventID, int eventVariation, int matchPartId, int minute, int playerID, boolean playerIsFromHomeTeam, int assistingPlayerID, boolean assistingPlayerIsFromHomeTeam, String eventText) {
        var matchEvent = new  MatchEvent();
        matchEvent.setMatchEventIndex(eventIndex);
        matchEvent.setM_eInjuryType(injuryType);
        matchEvent.setTeamID(teamID);
        matchEvent.setMatchEventID(matchEventID);
        matchEvent.setEventVariation(eventVariation);
        matchEvent.setMatchPartId(MatchEvent.MatchPartId.fromMatchPartId(matchPartId));
        matchEvent.setEventText(eventText);
        matchEvent.setMinute(minute);
        matchEvent.setPlayerId(playerID);
        matchEvent.setAssistingPlayerId(assistingPlayerID);
        matchEvent.setSpielerHeim(playerIsFromHomeTeam);
        matchEvent.setGehilfeHeim(assistingPlayerIsFromHomeTeam);
        return matchEvent;
    }

    private void assertThatIsEqualTo(MatchEvent matchEvent, MatchEvent other) {
        assertThat(matchEvent.getMatchEventIndex()).isEqualTo(other.getMatchEventIndex());
        assertThat(matchEvent.getiMatchEventID()).isEqualTo(other.getiMatchEventID());
        assertThat(matchEvent.getEventText()).isEqualTo(other.getEventText());
        assertThat(matchEvent.getMatchId()).isEqualTo(other.getMatchId());
        assertThat(matchEvent.getMatchType()).isEqualTo(other.getMatchType());
        assertThat(matchEvent.getMatchDate()).isEqualTo(other.getMatchDate());
        assertThat(matchEvent.getMatchPartId()).isEqualTo(other.getMatchPartId());
        assertThat(matchEvent.getTeamID()).isEqualTo(other.getTeamID());
        assertThat(matchEvent.getPlayerId()).isEqualTo(other.getPlayerId());
        assertThat(matchEvent.getPlayerName()).isEqualTo(other.getPlayerName());
        assertThat(matchEvent.getAssistingPlayerId()).isEqualTo(other.getAssistingPlayerId());
        assertThat(matchEvent.getAssistingPlayerName()).isEqualTo(other.getAssistingPlayerName());
        assertThat(matchEvent.getSpielerHeim()).isEqualTo(other.getSpielerHeim());
        assertThat(matchEvent.getGehilfeHeim()).isEqualTo(other.getGehilfeHeim());
        assertThat(matchEvent.getEventVariation()).isEqualTo(other.getEventVariation());
        assertThat(matchEvent.getMinute()).isEqualTo(other.getMinute());
        assertThat(matchEvent.getM_eInjuryType()).isEqualTo(other.getM_eInjuryType());
    }
}
