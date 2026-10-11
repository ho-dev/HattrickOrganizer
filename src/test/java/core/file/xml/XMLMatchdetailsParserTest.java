package core.file.xml;

import core.db.DBManager;
import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HODateTime;
import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class XMLMatchdetailsParserTest {

    private static final String FILENAME = "chpp/matchdetails/matchdetails-example.xml";

    @Test
    void parseMatchdetailsFromString_withMatchLineupIsNull() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString(FILENAME);

        final DBManager dbManager = mock(DBManager.class);

        try (MockedStatic<DBManager> mockedDbManager = mockStatic(DBManager.class)) {
            mockedDbManager.when(DBManager::instance).thenReturn(dbManager);

            when(dbManager.getMatchHighlights(anyInt(), anyInt())).thenReturn(List.of());

            // when
            final var matchdetails = XMLMatchdetailsParser.parseMatchdetailsFromString(content, null);

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
        }
    }

    @Test
    void parseMatchdetailsFromString_withMatchLineup() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString(FILENAME);
        final var matchLineup = new MatchLineup();
        matchLineup.setHomeTeam(new MatchLineupTeam());
        matchLineup.setGuestTeam(new MatchLineupTeam());

        final var expectedMatchDate = HODateTime.fromHT("2026-10-06 18:15:00");

        final var expectedMatchEvent0 = new MatchEvent();
        expectedMatchEvent0.setMatchEventIndex(1);
        expectedMatchEvent0.setM_eInjuryType(Matchdetails.eInjuryType.NA);
        expectedMatchEvent0.setTeamID(50000);
        expectedMatchEvent0.setMatchEventID(MatchEvent.MatchEventID.SPECTATORS_OR_VENUE_CLOUDY.getValue());
        expectedMatchEvent0.setEventVariation(5);
        expectedMatchEvent0.setMatchPartId(MatchEvent.MatchPartId.BEFORE_THE_MATCH_STARTED);
        expectedMatchEvent0.setEventText("<a href=\"/Club/Stadium/?stadiumId=2154092\">Alte Rösterei</a> - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. <a href=\"/Club/HallOfFame/Player.aspx?playerId=455444108\">Sultan Hafeeth Al-Buayqy</a> wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten <a href=\"/Club/HallOfFame/Player.aspx?playerId=474275056\">Bill Matthews</a> und <a href=\"/Club/HallOfFame/Player.aspx?playerId=459237940\">Idar Åkesson</a>.");
        expectedMatchEvent0.setMinute(0);
        expectedMatchEvent0.setPlayerId(2154092);
        expectedMatchEvent0.setAssistingPlayerId(1648);

        final DBManager dbManager = mock(DBManager.class);

        try (MockedStatic<DBManager> mockedDbManager = mockStatic(DBManager.class)) {
            mockedDbManager.when(DBManager::instance).thenReturn(dbManager);

            when(dbManager.getMatchHighlights(anyInt(), anyInt())).thenReturn(List.of());

            // when
            final var matchdetails = XMLMatchdetailsParser.parseMatchdetailsFromString(content, matchLineup);

            // then
            assertThat(matchdetails.getArenaName()).isEqualTo("Alte Rösterei");
            assertThat(matchdetails.getGuestTeamName()).isEqualTo("SSC Napoli-Köpenick");
            assertThat(matchdetails.getHomeTeamName()).isEqualTo("Kaffee-Junkies Hannover");
            assertThat(matchdetails.getMatchreport()).isEqualTo("<a href=\"/Club/Stadium/?stadiumId=2154092\">Alte Rösterei</a> - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. <a href=\"/Club/HallOfFame/Player.aspx?playerId=455444108\">Sultan Hafeeth Al-Buayqy</a> wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten <a href=\"/Club/HallOfFame/Player.aspx?playerId=474275056\">Bill Matthews</a> und <a href=\"/Club/HallOfFame/Player.aspx?playerId=459237940\">Idar Åkesson</a>.  Folgende Spieler begannen: <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"Sievers\" class=\"homeplayer\">Sievers</a> - <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Eggimann\" class=\"homeplayer\">Eggimann</a>, <a href=\"/Club/Players/Player.aspx?playerId=480433526\" title=\"Br&#252;ggemann\" class=\"homeplayer\">Brüggemann</a>, <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Mahler\" class=\"homeplayer\">Mahler</a>, <a href=\"/Club/Players/Player.aspx?playerId=503053978\" title=\"Cherundolo\" class=\"homeplayer\">Cherundolo</a> - <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Klaus\" class=\"homeplayer\">Klaus</a>, <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Pinto\" class=\"homeplayer\">Pinto</a>, <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Leopold\" class=\"homeplayer\">Leopold</a>, <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Maina\" class=\"homeplayer\">Maina</a> - <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Lerf\" class=\"homeplayer\">Lerf</a>, <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Stendel\" class=\"homeplayer\">Stendel</a>.<br ><br > Es liefen auf: <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Schlegelmilch\" class=\"awayplayer\">Schlegelmilch</a> - <a href=\"/Club/Players/Player.aspx?playerId=489791494\" title=\"Masciadri\" class=\"awayplayer\">Masciadri</a>, <a href=\"/Club/Players/Player.aspx?playerId=496877677\" title=\"Gierer\" class=\"awayplayer\">Gierer</a>, <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Rutherford\" class=\"awayplayer\">Rutherford</a>, <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Parmendola\" class=\"awayplayer\">Parmendola</a> - <a href=\"/Club/Players/Player.aspx?playerId=490747950\" title=\"Rajs\" class=\"awayplayer\">Rajs</a>, <a href=\"/Club/Players/Player.aspx?playerId=490797976\" title=\"Hultflod\" class=\"awayplayer\">Hultflod</a>, <a href=\"/Club/Players/Player.aspx?playerId=474503458\" title=\"Foltea\" class=\"awayplayer\">Foltea</a>, <a href=\"/Club/Players/Player.aspx?playerId=472214265\" title=\"Sellinger\" class=\"awayplayer\">Sellinger</a>, <a href=\"/Club/Players/Player.aspx?playerId=492622921\" title=\"Adderton\" class=\"awayplayer\">Adderton</a> - <a href=\"/Club/Players/Player.aspx?playerId=472437980\" title=\"Schichta\" class=\"awayplayer\">Schichta</a>.<br ><br > <span class=\"hometeam\">Kaffee-Junkies Hannover</span> lief in einem 4-4-2 auf,  <span class=\"awayteam\">SSC Napoli-Köpenick</span> entschied sich für ein 4-5-1.   <span class=\"hometeam\">KJH</span> befleißigte sich einer kreativen Spielweise. Sieht <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=11#skill\" class=\"skill\">brillant</a> aus, was die Taktikstärke angeht.   Ein ohrenbetäubendes Vuvuzela-Konzert brach in der 8. Minute auf den Tribünen aus, nachdem <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> zentral vor dem Tor abgezogen und einen Treffer für <span class=\"hometeam\">KJH</span> markiert hatte. Es stand nun 1 - 0. Bei <span class=\"hometeam\">KJH</span> schien nach 11 Minuten keiner wirklich zu wissen, wo er zu stehen hat. Die Mannschaft war <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=7#skill\" class=\"skill\">gut</a> organisiert.  Vollkommen überrascht war <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Linton Maina\" class=\"homeplayer\">Linton Maina</a> von seiner Auswechslung in der 15. Minute. Bei <span class=\"hometeam\">KJH</span> betrat an seiner Stelle <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> den Rasen. <span class=\"hometeam\">KJH</span> hatte offensichtlich Probleme mit den taktischen Anweisungen. Die Mannschaft war in der 15. Minute nur noch <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=4#skill\" class=\"skill\">schwach</a> organisiert.  <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> erntete in der 20. Minute tosenden Applaus, als er einen Pfau einfing, der plötzlich durch seinen Strafraum stolzierte. Das Tier war bei den nahen Dreharbeiten zu \"Bauer sucht Pfau\" stiften gegangen. Diese spektakuläre Aktion war noch lange in aller Munde. Dass <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> noch in der gleichen Minute <span class=\"hometeam\">KJH</span>-Flitzer <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> den Ball vom Fuß spitzelte und so einen Gegentreffer verhinderte, ging dabei fast unter.  Nach 25 Minuten kam es für die Gäste noch schlimmer, als <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> es ohne große Widerstände durch die Abwehrmitte vor das Tor schaffte und zum 2 - 0 traf. <span class=\"hometeam\">KJH</span>-Akteur <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> konnte sich in der 33. Minute dank seiner Schnelligkeit mühelos von seinem Gegenspieler absetzen. <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Derk Lerf\" class=\"homeplayer\">Derk Lerf</a> hatte keinerlei Schwierigkeit, die scharfe Hereingabe am herausstürzenden Torwart vorbei zum 3 - 0 zu verwandeln. Eine wundervolle Kombination im Angriffszentrum hätte nach 34 Minuten fast zu einem weiteren Tor für <span class=\"hometeam\">KJH</span> geführt, doch <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Friederich Schlegelmilch\" class=\"awayplayer\">Friederich Schlegelmilch</a> war bei diesem Schuss von <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> auf dem Posten. <span class=\"hometeam\">KJH</span> wurde in der 37. Minute ein Strafstoß zugesprochen. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> ließ sich die Chance nicht entgehen und verwandelte mit einem Lächeln auf dem Gesicht zum 4 - 0 für das Heimteam. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> schlug nach 40 Minuten einen raffiniert herein gezogenen Eckball, doch <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> war zu überrascht, den Ball zu bekommen, und verstolperte die Chance für <span class=\"hometeam\">KJH</span>. Nach einer Flanke von links spitzelte <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Mario Eggimann\" class=\"homeplayer\">Mario Eggimann</a> den Ball in Richtung Glück und Seligkeit, doch der Außenpfosten verhinderte ein weiteres Tor für <span class=\"hometeam\">KJH</span>. Da fiel dem Präsidium des gegnerischen Teams auf der Tribüne sichtlich ein Stein vom Herzen, dass diese Situation in der 41. Minute ein gutes Ende nahm.  <span class=\"hometeam\">KJH</span>-Spieler <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> hatte es bei seiner Attacke in der 42. Minute nur auf die Beine des Gegenspielers abgesehen. Folgerichtig sah er die Gelbe Karte. Spielminute 43: Um das Team noch auf die Siegerstraße zu bringen, richtete der <span class=\"awayteam\">Napoli-Köpenick</span>-Trainer seine Mannen taktisch neu aus. Bei <span class=\"awayteam\">Napoli-Köpenick</span> war man über den Rückstand verständlicherweise nicht erfreut, daher formierten sich die Spieler nach 44 Minuten auf Anweisung ihres Trainers neu. Zur Halbzeit stand es 4 - 0. <span class=\"hometeam\">KJH</span> war in dieser Halbzeit das dominierende Team - die Spieler brachten den Ballbesitz auf 55 Prozent.<br ><br >  Ein erneuter Blick auf die Taktiktafel in der Pause sorgte dafür, dass die Spieler von <span class=\"hometeam\">KJH</span> nun <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=6#skill\" class=\"skill\">passabel</a> organisiert waren. Zufrieden mit dem Spielstand verordnete der <span class=\"hometeam\">KJH</span>-Trainer seiner Mannschaft in der 46. Minute eine neue taktische Ausrichtung.  <span class=\"hometeam\">KJH</span> baute in der 52. Minute die Führung auf 5 - 0 aus, nachdem ein Verteidiger der Gäste den Ball leichtfertig an <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> verloren hatte. Dieser drang über links in den Strafraum ein und spielte dann noch gekonnt den Torwart aus, bevor er das Leder lässig über die Linie schob.  Darauf hatte <a href=\"/Club/Players/Player.aspx?playerId=461796896\" title=\"Lars Stindl\" class=\"homeplayer\">Lars Stindl</a> schon länger gewartet. Nach 60 Minuten war es soweit, er kam zu seinem Einsatz für <span class=\"hometeam\">KJH</span>. Im Gegenzug verließ <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> den Platz. Spielminute 62: Bei <span class=\"hometeam\">KJH</span> herrschte ein ziemliches Durcheinander auf dem Feld - die Spieler hatten Probleme, ihre Positionen zu halten, und waren <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=3#skill\" class=\"skill\">armselig</a> organisiert. Ein schnell vorgetragener Angriff von <span class=\"hometeam\">KJH</span> über die linke Seite des Feldes wurde in der 63. Minute mit einem weiteren Tor zum 6 - 0 gekrönt. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> hatte aus spitzem Winkel abgezogen. Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor. Damit konnte sich <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> als Hattrickschütze notieren lassen.  83 Minuten waren gespielt, als <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Raven Rutherford\" class=\"awayplayer\">Raven Rutherford</a> sich in der Mitte prima durchsetzte und nur noch <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"J&#246;rg &#39;Colt&#39; Sievers\" class=\"homeplayer\">Jörg 'Colt' Sievers</a> vor sich hatte. Der blieb lange stehen und wehrte den Schuss ab. Es blieb beim 6 - 0. Eine schöne Ballstafette auf dem rechten Flügel der Gastgeber konnte von der gegnerischen Abwehr nicht unterbunden werden. So gelangte das Objekt der Begierde schließlich zu\n" +
                "                    <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Sergio Pinto\" class=\"homeplayer\">Sergio Pinto</a>, der ein weiteres Tor für <span class=\"hometeam\">KJH</span> erzielte. Die Führung erhöhte sich dadurch in der 84. Minute auf 7 - 0.\n" +
                "                    Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor. Die Nachspielzeit beträgt 3 Minute(n). Die Begegnung endete 7 - 0.  <span class=\"hometeam\">KJH</span> erreichte laut HT-Datenbank 53 Prozent Ballbesitz.<br ><br > Der beste Spieler von <span class=\"hometeam\">KJH</span> war ohne jeden Zweifel <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a>. Nach diesem Spiel forderten die Fans von <span class=\"awayteam\">Napoli-Köpenick</span> eine \"<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a>-Straße\" - er hatte eine herausragende Leistung abgeliefert. ");
            assertThat(matchdetails.getFetchDatum()).isEqualTo(HODateTime.fromHT("2026-10-08 12:36:42"));
            assertThat(matchdetails.getMatchDate()).isEqualTo(expectedMatchDate);
            assertThat(matchdetails.getHighlights()).hasSize(46);
            assertThatIsEqualTo(matchdetails.getHighlights().get(0), expectedMatchEvent0);
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
        }
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
