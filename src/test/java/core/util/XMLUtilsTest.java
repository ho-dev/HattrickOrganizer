package core.util;

import core.file.xml.XMLManager;
import core.file.xml.XMLParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XMLUtilsTest {

    private static final String xmlText =
        """
            <?xml version="1.0" encoding="utf-8"?>
            <HattrickData>
              <FileName>matchdetails.xml</FileName>
              <Version>3.0</Version>
              <UserID>1757369</UserID>
              <FetchedDate>2026-10-11 14:35:09</FetchedDate>
              <UserSupporterTier>true</UserSupporterTier>
              <SourceSystem>Hattrick</SourceSystem>
              <Match>
                <MatchID>768440457</MatchID>
                <MatchType>1</MatchType>
                <MatchContextId>6195</MatchContextId>
                <MatchRuleId>0</MatchRuleId>
                <CupLevel>0</CupLevel>
                <CupLevelIndex>0</CupLevelIndex>
                <MatchDate>2026-10-10 18:00:00</MatchDate>
                <FinishedDate>2026-10-10 19:46:00</FinishedDate>
                <AddedMinutes>1</AddedMinutes>
                <HomeTeam>
                  <HomeTeamID>1001451</HomeTeamID>
                <HomeTeamName>Borussia Uncoachable</HomeTeamName>
                  <DressURI>//res.hattrick.org/kits/1/1/1/1/matchKitSmall.png</DressURI>
                  <Formation>3-5-2</Formation>
                  <HomeGoals>2</HomeGoals>
                  <TacticType>0</TacticType>
                  <TacticSkill>0</TacticSkill>
                  <RatingMidfield>43</RatingMidfield>
                  <RatingRightDef>54</RatingRightDef>
                  <RatingMidDef>57</RatingMidDef>
                  <RatingLeftDef>53</RatingLeftDef>
                  <RatingRightAtt>36</RatingRightAtt>
                  <RatingMidAtt>47</RatingMidAtt>
                  <RatingLeftAtt>38</RatingLeftAtt>
                  <RatingIndirectSetPiecesDef>34</RatingIndirectSetPiecesDef>
                  <RatingIndirectSetPiecesAtt>23</RatingIndirectSetPiecesAtt>
                </HomeTeam>
                <AwayTeam>
                  <AwayTeamID>1242154</AwayTeamID>
                <AwayTeamName>Juventus Brenk 2</AwayTeamName>
                  <DressURI>//res.hattrick.org/kits/1/1/1/4/matchKitSmall.png</DressURI>
                  <Formation>2-5-3</Formation>
                  <AwayGoals>1</AwayGoals>
                  <TacticType>0</TacticType>
                  <TacticSkill>0</TacticSkill>
                  <RatingMidfield>37</RatingMidfield>
                  <RatingRightDef>38</RatingRightDef>
                  <RatingMidDef>45</RatingMidDef>
                  <RatingLeftDef>44</RatingLeftDef>
                  <RatingRightAtt>40</RatingRightAtt>
                  <RatingMidAtt>58</RatingMidAtt>
                  <RatingLeftAtt>37</RatingLeftAtt>
                  <TeamAttitude>0</TeamAttitude>
                  <RatingIndirectSetPiecesDef>41</RatingIndirectSetPiecesDef>
                  <RatingIndirectSetPiecesAtt>36</RatingIndirectSetPiecesAtt>
                </AwayTeam>
                <Arena>
                  <ArenaID>998013</ArenaID>
                  <ArenaName>Torfabrik</ArenaName>
                  <WeatherID>2</WeatherID>
                  <SoldTotal>51491</SoldTotal>
                </Arena>
                <MatchOfficials>
                  <Referee>
                    <RefereeId>459706825</RefereeId>
                <RefereeName>Banni Shakko</RefereeName>
                    <RefereeCountryId>128</RefereeCountryId>
                <RefereeCountryName>Al Iraq</RefereeCountryName>
                    <RefereeTeamId>2013535</RefereeTeamId>
                <RefereeTeamname>AS Samnaun 2</RefereeTeamname>
                  </Referee>
                  <RefereeAssistant1>
                    <RefereeId>434033455</RefereeId>
                    <RefereeName>Jean-Cyril Henry</RefereeName>
                    <RefereeCountryId>44</RefereeCountryId>
                    <RefereeCountryName>Belgium</RefereeCountryName>
                    <RefereeTeamId>745667</RefereeTeamId>
                    <RefereeTeamname>Q.T.JIMA</RefereeTeamname>
                  </RefereeAssistant1>
                  <RefereeAssistant2>
                    <RefereeId>484169743</RefereeId>
                <RefereeName>Magnus Kristensen</RefereeName>
                    <RefereeCountryId>9</RefereeCountryId>
                    <RefereeCountryName>Norge</RefereeCountryName>
                    <RefereeTeamId>2131003</RefereeTeamId>
                <RefereeTeamname>Åga IL</RefereeTeamname>
                  </RefereeAssistant2>
                </MatchOfficials>
                <Scorers>
                  <Goal Index="0">
                    <ScorerPlayerID>478624666</ScorerPlayerID>
                <ScorerPlayerName>Paweł Czubała</ScorerPlayerName>
                    <ScorerTeamID>1001451</ScorerTeamID>
                    <ScorerHomeGoals>1</ScorerHomeGoals>
                    <ScorerAwayGoals>0</ScorerAwayGoals>
                    <ScorerMinute>7</ScorerMinute>
                    <MatchPart>1</MatchPart>
                  </Goal>
                  <Goal Index="1">
                    <ScorerPlayerID>472037713</ScorerPlayerID>
                <ScorerPlayerName>Elmar Zak</ScorerPlayerName>
                    <ScorerTeamID>1242154</ScorerTeamID>
                    <ScorerHomeGoals>1</ScorerHomeGoals>
                    <ScorerAwayGoals>1</ScorerAwayGoals>
                    <ScorerMinute>78</ScorerMinute>
                    <MatchPart>2</MatchPart>
                  </Goal>
                  <Goal Index="2">
                    <ScorerPlayerID>479028483</ScorerPlayerID>
                <ScorerPlayerName>Georg Engelhardt</ScorerPlayerName>
                    <ScorerTeamID>1001451</ScorerTeamID>
                    <ScorerHomeGoals>2</ScorerHomeGoals>
                    <ScorerAwayGoals>1</ScorerAwayGoals>
                    <ScorerMinute>80</ScorerMinute>
                    <MatchPart>2</MatchPart>
                  </Goal>
                </Scorers>
                <Bookings />
                <Injuries />
                <PossessionFirstHalfHome>53</PossessionFirstHalfHome>
                <PossessionFirstHalfAway>47</PossessionFirstHalfAway>
                <PossessionSecondHalfHome>54</PossessionSecondHalfHome>
                <PossessionSecondHalfAway>46</PossessionSecondHalfAway>
                <EventList>
                  <Event Index="0">
                    <Minute>0</Minute>
                    <SubjectPlayerID>998013</SubjectPlayerID>
                    <SubjectTeamID>75000</SubjectTeamID>
                    <ObjectPlayerID>51491</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>32</EventTypeID>
                    <EventVariation>1</EventVariation>
                    <EventText>&lt;a href="/Club/Stadium/?stadiumId=998013"&gt;Torfabrik&lt;/a&gt; - Optimales Fußballwetter hatte 51491 ZuschauerInnen ins Stadion gelockt. &lt;a href="/Club/HallOfFame/Player.aspx?playerId=426554822"&gt;Waldes 'Bobas' Bobaz&lt;/a&gt; wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten &lt;a href="/Club/HallOfFame/Player.aspx?playerId=464968546"&gt;Ingmar Stebel&lt;/a&gt; und &lt;a href="/Club/HallOfFame/Player.aspx?playerId=432195507"&gt;Ekke Partanen&lt;/a&gt;.</EventText>
                  </Event>
                  <Event Index="1">
                    <Minute>0</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>19</EventTypeID>
                    <EventVariation>4</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="2">
                    <Minute>0</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>21</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>Die Startformation umfasste folgende Spieler: &lt;a href="/Club/Players/Player.aspx?playerId=482748992" title="Macci" class="homeplayer"&gt;Macci&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=470839767" title="Gizdić" class="homeplayer"&gt;Gizdić&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=482144794" title="Stelmaszyk" class="homeplayer"&gt;Stelmaszyk&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=471143239" title="Ghasemi" class="homeplayer"&gt;Ghasemi&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=478624666" title="Czubała" class="homeplayer"&gt;Czubała&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=488527167" title="Hayos" class="homeplayer"&gt;Hayos&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=482047323" title="Ostermann" class="homeplayer"&gt;Ostermann&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=482579805" title="Paulaharju" class="homeplayer"&gt;Paulaharju&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=479677000" title="G&amp;#252;ray" class="homeplayer"&gt;Güray&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=473115964" title="Rutan" class="homeplayer"&gt;Rutan&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Engelhardt" class="homeplayer"&gt;Engelhardt&lt;/a&gt;.&lt;br /&gt;&lt;br /&gt;</EventText>
                  </Event>
                  <Event Index="3">
                    <Minute>0</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>21</EventTypeID>
                    <EventVariation>4</EventVariation>
                <EventText>Folgende Spieler begannen: &lt;a href="/Club/Players/Player.aspx?playerId=478717435" title="Bomh&amp;#246;ffer" class="awayplayer"&gt;Bomhöffer&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=476795796" title="Frankenberg-Ludwigsdorff" class="awayplayer"&gt;Frankenberg-Ludwigsdorff&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=462190166" title="Zaufall" class="awayplayer"&gt;Zaufall&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=491296884" title="Zuerz" class="awayplayer"&gt;Zuerz&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=461430464" title="Brecheis" class="awayplayer"&gt;Brecheis&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=469193165" title="Zentsch" class="awayplayer"&gt;Zentsch&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=458686572" title="Klinggr&amp;#228;ff" class="awayplayer"&gt;Klinggräff&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=461302463" title="Nemeth" class="awayplayer"&gt;Nemeth&lt;/a&gt; - &lt;a href="/Club/Players/Player.aspx?playerId=483334933" title="Lichtenh&amp;#228;ler" class="awayplayer"&gt;Lichtenhäler&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=472037713" title="Zak" class="awayplayer"&gt;Zak&lt;/a&gt;, &lt;a href="/Club/Players/Player.aspx?playerId=487254898" title="Braith" class="awayplayer"&gt;Braith&lt;/a&gt;.&lt;br /&gt;&lt;br /&gt;</EventText>
                  </Event>
                  <Event Index="4">
                    <Minute>0</Minute>
                    <SubjectPlayerID>1030502</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>1020503</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>24</EventTypeID>
                    <EventVariation>4</EventVariation>
                <EventText>Die Teams entschieden sich für unterschiedliche Herangehensweisen. Das 3-5-2 bei &lt;span class="hometeam"&gt;Borussia Uncoachable&lt;/span&gt; stand einem 2-5-3 von  &lt;span class="awayteam"&gt;Juventus Brenk 2&lt;/span&gt; gegenüber. </EventText>
                  </Event>
                  <Event Index="5">
                    <Minute>0</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>0</MatchPart>
                    <EventTypeID>598</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="6">
                    <Minute>4</Minute>
                    <SubjectPlayerID>483334933</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>482748992</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>273</EventTypeID>
                    <EventVariation>1</EventVariation>
                    <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=483334933" title="Emil Lichtenh&amp;#228;ler" class="awayplayer"&gt;Emil Lichtenhäler&lt;/a&gt; hätte &lt;span class="awayteam"&gt;Juventus&lt;/span&gt; in der 4. Minute in Führung bringen können, als er von rechts Richtung Tor stürmte, aber &lt;a href="/Club/Players/Player.aspx?playerId=482748992" title="Gianrico Macci" class="homeplayer"&gt;Gianrico Macci&lt;/a&gt; griff beherzt und blitzschnell ein und klärte die Situation.</EventText>
                  </Event>
                  <Event Index="7">
                    <Minute>7</Minute>
                    <SubjectPlayerID>478624666</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>122</EventTypeID>
                    <EventVariation>1</EventVariation>
                <EventText>In der 7. Minute griff &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; über die linke Seite an. &lt;a href="/Club/Players/Player.aspx?playerId=478624666" title="Paweł Czubała" class="homeplayer"&gt;Paweł Czubała&lt;/a&gt; versuchte es aus einem spitzen Winkel, und da der Torwart eher mit einer Flanke gerechnet hatte, schlug der Ball im Netz ein. 1 - 0.</EventText>
                  </Event>
                  <Event Index="8">
                    <Minute>15</Minute>
                    <SubjectPlayerID>52</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>471</EventTypeID>
                    <EventVariation>2</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="9">
                    <Minute>18</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>811</EventTypeID>
                    <EventVariation>2</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="10">
                    <Minute>21</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>232</EventTypeID>
                    <EventVariation>4</EventVariation>
                    <EventText>Nach 21 Minuten hatte &lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Georg Engelhardt" class="homeplayer"&gt;Georg Engelhardt&lt;/a&gt; eine gute Gelegenheit für &lt;span class="hometeam"&gt;Borussia&lt;/span&gt;, das Ergebnis deutlicher zu gestalten. Er zog vom linken Flügel ab, die Gästeabwehr konnte aber klären.</EventText>
                  </Event>
                  <Event Index="11">
                    <Minute>22</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>233</EventTypeID>
                    <EventVariation>1</EventVariation>
                <EventText>Ein zügig vorgetragener Angriff auf der rechten Seite hätte in der 22. Minute fast zu einem weiteren Tor für die Heimmannschaft durch &lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Georg Engelhardt" class="homeplayer"&gt;Georg Engelhardt&lt;/a&gt; geführt. Doch &lt;a href="/Club/Players/Player.aspx?playerId=478717435" title="Engelbert Bomh&amp;#246;ffer" class="awayplayer"&gt;Engelbert Bomhöffer&lt;/a&gt; konnte per Faustabwehr klären.</EventText>
                  </Event>
                  <Event Index="12">
                    <Minute>26</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>232</EventTypeID>
                    <EventVariation>5</EventVariation>
                    <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Georg Engelhardt" class="homeplayer"&gt;Georg Engelhardt&lt;/a&gt; hätte den Vorsprung für &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; ausbauen können, traf aber nach 26 Minuten aus halblinker Position nur das Außennetz.</EventText>
                  </Event>
                  <Event Index="13">
                    <Minute>28</Minute>
                    <SubjectPlayerID>482144794</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>231</EventTypeID>
                    <EventVariation>2</EventVariation>
                <EventText>Eine wundervolle Kombination im Angriffszentrum hätte nach 28 Minuten fast zu einem weiteren Tor für &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; geführt, doch &lt;a href="/Club/Players/Player.aspx?playerId=478717435" title="Engelbert Bomh&amp;#246;ffer" class="awayplayer"&gt;Engelbert Bomhöffer&lt;/a&gt; war bei diesem Schuss von &lt;a href="/Club/Players/Player.aspx?playerId=482144794" title="Radomir Stelmaszyk" class="homeplayer"&gt;Radomir Stelmaszyk&lt;/a&gt; auf dem Posten.</EventText>
                  </Event>
                  <Event Index="14">
                    <Minute>45</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>45</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>Zur Halbzeit stand es 1 - 0.</EventText>
                  </Event>
                  <Event Index="15">
                    <Minute>45</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>53</ObjectPlayerID>
                    <MatchPart>1</MatchPart>
                    <EventTypeID>40</EventTypeID>
                    <EventVariation>5</EventVariation>
                    <EventText>&lt;span class="hometeam"&gt;Borussia&lt;/span&gt; hatte die größeren Spielanteile und kam auf 53 Prozent Ballbesitz.&lt;br /&gt;&lt;br /&gt;</EventText>
                  </Event>
                  <Event Index="16">
                    <Minute>46</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>597</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="17">
                    <Minute>50</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>51491</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>464</EventTypeID>
                    <EventVariation>1</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="18">
                    <Minute>58</Minute>
                    <SubjectPlayerID>468655853</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>58</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>459</EventTypeID>
                    <EventVariation>4</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="19">
                    <Minute>63</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>494</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="20">
                    <Minute>66</Minute>
                    <SubjectPlayerID>482047323</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>233</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>In der 66. Minute mangelte es &lt;a href="/Club/Players/Player.aspx?playerId=482047323" title="Harald Ostermann" class="homeplayer"&gt;Harald Ostermann&lt;/a&gt; an der nötigen Konzentration um eine hundertprozentige Chance für &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; zu verwerten, als er den Torwart von rechts kommend bereits umspielt hatte, aus spitzem Winkel jedoch weit daneben schoss.</EventText>
                  </Event>
                  <Event Index="21">
                    <Minute>68</Minute>
                    <SubjectPlayerID>458686572</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>468655853</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>352</EventTypeID>
                    <EventVariation>4</EventVariation>
                <EventText>Darauf hatte &lt;a href="/Club/Players/Player.aspx?playerId=468655853" title="Miloš Novaković" class="awayplayer"&gt;Miloš Novaković&lt;/a&gt; schon länger gewartet. Nach 68 Minuten war es soweit, er kam zu seinem Einsatz für &lt;span class="awayteam"&gt;Juventus&lt;/span&gt;. Im Gegenzug verließ &lt;a href="/Club/Players/Player.aspx?playerId=458686572" title="Manuel Klinggr&amp;#228;ff" class="awayplayer"&gt;Manuel Klinggräff&lt;/a&gt; den Platz.</EventText>
                  </Event>
                  <Event Index="22">
                    <Minute>68</Minute>
                    <SubjectPlayerID>458686572</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>468655853</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>81</EventTypeID>
                    <EventVariation>1</EventVariation>
                    <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=458686572" title="Manuel Klinggr&amp;#228;ff" class="awayplayer"&gt;Manuel Klinggräff&lt;/a&gt; konnte offensichtlicherweise keine Standards mehr treten und so übernahm &lt;a href="/Club/Players/Player.aspx?playerId=468655853" title="Miloš Novaković" class="awayplayer"&gt;Miloš Novaković&lt;/a&gt; die Aufgaben des Standardschützen.</EventText>
                  </Event>
                  <Event Index="23">
                    <Minute>70</Minute>
                    <SubjectPlayerID>467889960</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>70</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>459</EventTypeID>
                    <EventVariation>1</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="24">
                    <Minute>70</Minute>
                    <SubjectPlayerID>463549813</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>70</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>459</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="25">
                    <Minute>71</Minute>
                    <SubjectPlayerID>481882712</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>71</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>459</EventTypeID>
                    <EventVariation>2</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="26">
                    <Minute>73</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>804</EventTypeID>
                    <EventVariation>5</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="27">
                    <Minute>78</Minute>
                    <SubjectPlayerID>472037713</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>482748992</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>162</EventTypeID>
                    <EventVariation>5</EventVariation>
                <EventText>In der 78. Minute glichen die Gäste zum 1 - 1 aus, als &lt;a href="/Club/Players/Player.aspx?playerId=472037713" title="Elmar Zak" class="awayplayer"&gt;Elmar Zak&lt;/a&gt; ein wunderschönes Zuspiel vom linken Flügel ins Tor verlängerte. Die &lt;span class="awayteam"&gt;Juventus&lt;/span&gt;-Fans waren aus dem Häuschen!</EventText>
                  </Event>
                  <Event Index="29">
                    <Minute>80</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>478717435</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>123</EventTypeID>
                    <EventVariation>2</EventVariation>
                    <EventText>&lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Georg Engelhardt" class="homeplayer"&gt;Georg Engelhardt&lt;/a&gt; brachte die Platzherren in der 80. Minute nach einem Angriff über rechts in Führung. "&lt;span class="hometeam"&gt;Borussia&lt;/span&gt; 2! Gäste 1!", vermeldete der Stadionsprecher mit Unterstützung der Heimfans.</EventText>
                  </Event>
                  <Event Index="30">
                    <Minute>81</Minute>
                    <SubjectPlayerID>473115964</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>467889960</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>351</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText>Den &lt;span class="hometeam"&gt;Borussia&lt;/span&gt;-Spielern war bewusst, dass sie dieses Spiel gewinnen könnten. Um den Sieg sicherzustellen, kam &lt;a href="/Club/Players/Player.aspx?playerId=467889960" title="Bartolom&amp;#233; Buyatti" class="homeplayer"&gt;Bartolomé Buyatti&lt;/a&gt; in der 81. Minute für &lt;a href="/Club/Players/Player.aspx?playerId=473115964" title="Logan Rutan" class="homeplayer"&gt;Logan Rutan&lt;/a&gt; ins Spiel.</EventText>
                  </Event>
                  <Event Index="31">
                    <Minute>81</Minute>
                    <SubjectPlayerID>478624666</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>463549813</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>351</EventTypeID>
                    <EventVariation>2</EventVariation>
                <EventText>Nur widerstrebend verließ &lt;span class="hometeam"&gt;Borussia&lt;/span&gt;-Spieler &lt;a href="/Club/Players/Player.aspx?playerId=478624666" title="Paweł Czubała" class="homeplayer"&gt;Paweł Czubała&lt;/a&gt; das Spielfeld. Doch die Entscheidung stand fest, in Minute 81 betrat &lt;a href="/Club/Players/Player.aspx?playerId=463549813" title="Willem Bux" class="homeplayer"&gt;Willem Bux&lt;/a&gt; das Spielfeld, um die Führung seiner Mannschaft abzusichern.</EventText>
                  </Event>
                  <Event Index="32">
                    <Minute>81</Minute>
                    <SubjectPlayerID>461302463</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>481882712</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>352</EventTypeID>
                    <EventVariation>3</EventVariation>
                    <EventText>Nach 81 gespielten Minuten entschloss sich der &lt;span class="awayteam"&gt;Juventus&lt;/span&gt;-Trainer, &lt;a href="/Club/Players/Player.aspx?playerId=461302463" title="Stephan Nemeth" class="awayplayer"&gt;Stephan Nemeth&lt;/a&gt; durch &lt;a href="/Club/Players/Player.aspx?playerId=481882712" title="Hammo H&amp;#246;cht" class="awayplayer"&gt;Hammo Höcht&lt;/a&gt; zu ersetzen.</EventText>
                  </Event>
                  <Event Index="33">
                    <Minute>81</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>361</EventTypeID>
                    <EventVariation>5</EventVariation>
                <EventText>In der 81. Minute erhielten die &lt;span class="hometeam"&gt;Borussia&lt;/span&gt;-Spieler neue Anweisungen von ihrem Trainer, um das Spielgeschehen zu kontrollieren und die Führung zu verteidigen.</EventText>
                  </Event>
                  <Event Index="34">
                    <Minute>81</Minute>
                    <SubjectPlayerID>488527167</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>361</EventTypeID>
                    <EventVariation>2</EventVariation>
                    <EventText>81 Minuten waren gespielt, als bei &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; die Spielweise umgestellt wurde, um die Führung zu konservieren.</EventText>
                  </Event>
                  <Event Index="36">
                    <Minute>86</Minute>
                    <SubjectPlayerID>479677000</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>461430464</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>239</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>Manche sagen, Fußball wäre nur bumm-bumm-bumm, aber nicht für &lt;a href="/Club/Players/Player.aspx?playerId=479677000" title="H&amp;#252;seyin G&amp;#252;ray" class="homeplayer"&gt;Hüseyin Güray&lt;/a&gt;, der &lt;a href="/Club/Players/Player.aspx?playerId=461430464" title="Otmar Brecheis" class="awayplayer"&gt;Otmar Brecheis&lt;/a&gt; in der 86. Minute herrlich austanzte. Weniger herrlich war der Abschluss, der deutlich neben das Tor ging. Typisch!</EventText>
                  </Event>
                  <Event Index="38">
                    <Minute>89</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>75</EventTypeID>
                    <EventVariation>2</EventVariation>
                <EventText>Es gibt noch ein bisschen oben drauf! Die Nachspielzeit beträgt 1 Minute(n).</EventText>
                  </Event>
                  <Event Index="39">
                    <Minute>91</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>0</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>599</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>Die Begegnung endete 2 - 1.</EventText>
                  </Event>
                  <Event Index="40">
                    <Minute>91</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>601</EventTypeID>
                    <EventVariation>5</EventVariation>
                    <EventText></EventText>
                  </Event>
                  <Event Index="41">
                    <Minute>91</Minute>
                    <SubjectPlayerID>0</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>54</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>40</EventTypeID>
                    <EventVariation>2</EventVariation>
                <EventText>Statistiker im Stadion ermittelten 54 Prozent Ballbesitz für &lt;span class="hometeam"&gt;Borussia&lt;/span&gt;.&lt;br /&gt;&lt;br /&gt;</EventText>
                  </Event>
                  <Event Index="42">
                    <Minute>91</Minute>
                    <SubjectPlayerID>479028483</SubjectPlayerID>
                    <SubjectTeamID>1001451</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>41</EventTypeID>
                    <EventVariation>3</EventVariation>
                <EventText>Nach diesem Spiel forderten die Fans von &lt;span class="hometeam"&gt;Borussia&lt;/span&gt; eine "&lt;a href="/Club/Players/Player.aspx?playerId=479028483" title="Georg Engelhardt" class="homeplayer"&gt;Georg Engelhardt&lt;/a&gt;-Straße" - er hatte eine herausragende Leistung abgeliefert.</EventText>
                  </Event>
                  <Event Index="43">
                    <Minute>91</Minute>
                    <SubjectPlayerID>472037713</SubjectPlayerID>
                    <SubjectTeamID>1242154</SubjectTeamID>
                    <ObjectPlayerID>0</ObjectPlayerID>
                    <MatchPart>2</MatchPart>
                    <EventTypeID>41</EventTypeID>
                    <EventVariation>2</EventVariation>
                <EventText>Der beste Spieler von &lt;span class="awayteam"&gt;Juventus&lt;/span&gt; war ohne jeden Zweifel &lt;a href="/Club/Players/Player.aspx?playerId=472037713" title="Elmar Zak" class="awayplayer"&gt;Elmar Zak&lt;/a&gt;.</EventText>
                  </Event>
                </EventList>
              </Match>
            </HattrickData>

            """;

    private static final Document doc = XMLManager.parseString(xmlText);
    private static final Element root = doc.getDocumentElement();
    private static final Element matchElement = XMLUtils.getElement(root, "Match");


    private static Stream<Arguments> test_getElement() {
        return Stream.of(
            arguments(matchElement, "Scorers"),
            arguments(root, "FileName"),
            arguments(root, "Version")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getElement(Element element, String tagName) {
        var child = XMLUtils.getElement(element, tagName);
        assertThat(child).isNotNull();
        assertThat(child.getTagName()).isEqualTo(tagName);
    }

    private static Stream<Arguments> test_getElement_throwsXMLParseException() {
        return Stream.of(
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", "More than one element found with name 'Goal'."),
            arguments(root, " FileName", "No element with tag name ' FileName'."),
            arguments(root, "version", "No element with tag name 'version'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getElement_throwsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getElement(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }


    private static Stream<Arguments> test_hasElement() {
        return Stream.of(
            arguments(matchElement, "Scorers", true),
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", true),
            arguments(root, " FileName", false),
            arguments(root, "version", false),
            arguments(root, "FileName", true),
            arguments(root, "Version", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_hasElement(Element element, String tagName, boolean expected) {
        assertThat(XMLUtils.hasElement(element, tagName)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getStringValue() {
        return Stream.of(
            arguments(root, "FileName", "matchdetails.xml"),
            arguments(root, "Version", "3.0")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getStringValue(Element element, String tagName, String expected) {
        assertThat(XMLUtils.getStringValue(element, tagName)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getStringValueDefault() {
        return Stream.of(
            arguments(root, "FileName", "matchdetails.xml"),
            arguments(root, "Version", "3.0"),
            arguments(root, "_FileName", "default"),
            arguments(root, "_Version", "default")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getStringValueDefault(Element element, String tagName, String expected) {
        assertThat(XMLUtils.getStringValue(element, tagName, "default")).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getStringValue_throwsXMLParseException() {
        return Stream.of(
            arguments(root, " FileName", "No element with tag name ' FileName'."),
            arguments(root, "version", "No element with tag name 'version'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getStringValue_throwsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getStringValue(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getStringValueDefault_throwsXMLParseException() {
        return Stream.of(
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", "More than one element found with name 'Goal'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getStringValueDefault_throwsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getStringValue(element, tagName, "default"));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getIntValue() {
        return Stream.of(
            arguments(root, "UserID", 1757369)
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getIntValue(Element element, String tagName, int expected) {
        assertThat(XMLUtils.getIntValue(element, tagName)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getIntValueDefault() {
        return Stream.of(
            arguments(root, "UserID", 1757369),
            arguments(root, "_FileName", -1),
            arguments(root, "_Version", -1)
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getIntValueDefault(Element element, String tagName, int expected) {
        assertThat(XMLUtils.getIntValue(element, tagName, -1)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getIntValue_ThrowsXMLParseException() {
        return Stream.of(
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", "More than one element found with name 'Goal'."),
            arguments(root, "_FileName", "No element with tag name '_FileName'."),
            arguments(root, "_Version", "No element with tag name '_Version'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getIntValue_ThrowsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getIntValue(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getIntValueDefault_ThrowsXMLParseException() {
        return Stream.of(
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", "More than one element found with name 'Goal'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getIntValueDefault_ThrowsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getIntValue(element, tagName, -1));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getIntValue_ThrowsNumberFormatException() {
        return Stream.of(
            arguments(root, "FileName", "For input string: \"matchdetails.xml\""),
            arguments(root, "Version", "For input string: \"3.0\"")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getIntValue_ThrowsNumberFormatException(Element element, String tagName, String expected) {
        NumberFormatException thrown = assertThrows(NumberFormatException.class, () -> XMLUtils.getIntValue(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_findIntegerValue() {
        return Stream.of(
            arguments(root, "UserID", Optional.of(1757369)),
            arguments(root, "_FileName", Optional.empty()),
            arguments(root, "_Version", Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_findIntegerValue(Element element, String tagName, Optional<Integer> expected) {
        assertThat(XMLUtils.findIntegerValue(element, tagName)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_findIntegerValue_ThrowsNumberFormatException() {
        return Stream.of(
            arguments(root, "FileName", "For input string: \"matchdetails.xml\""),
            arguments(root, "Version", "For input string: \"3.0\"")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_findIntegerValue_ThrowsNumberFormatException(Element element, String tagName, String expected) {
        NumberFormatException thrown = assertThrows(NumberFormatException.class, () -> XMLUtils.findIntegerValue(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getBooleanValue() {
        return Stream.of(
            arguments(root, "FileName", false),
            arguments(root, "Version", false),
            arguments(root, "UserSupporterTier", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getBooleanValue(Element element, String tagName, boolean expected) {
        assertThat(XMLUtils.getBooleanValue(element, tagName)).isEqualTo(expected);
    }

    private static Stream<Arguments> test_getBooleanValue_ThrowsXMLParseException() {
        return Stream.of(
            arguments(XMLUtils.getElement(matchElement, "Scorers"), "Goal", "More than one element found with name 'Goal'."),
            arguments(root, "_FileName", "No element with tag name '_FileName'."),
            arguments(root, "_Version", "No element with tag name '_Version'.")
        );
    }

    @ParameterizedTest
    @MethodSource
    void test_getBooleanValue_ThrowsXMLParseException(Element element, String tagName, String expected) {
        XMLParseException thrown = assertThrows(XMLParseException.class, () -> XMLUtils.getBooleanValue(element, tagName));
        assertThat(thrown.getMessage()).isEqualTo(expected);
    }
}
