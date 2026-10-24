package core.file.xml;

import core.model.cup.CupLevel;
import core.model.cup.CupLevelIndex;
import core.model.enums.MatchType;
import core.model.match.*;
import core.util.HODateTime;
import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class XMLMatchdetailsParserTest {

    @Test
    void parseMatchdetailsFromString_withMatchLineupIsNull() throws IOException {

        var xmlFileMatchDetails = ResourceUtils.getResourceFileAsString("chpp/matchdetails/matchdetails.xml");

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
        assertThat(matchdetails.getInjuries()).isEmpty();
        // homeGoalsInParts in another test
        // guestGoalsInParts in another test
        assertThat(matchdetails.getFormation(true)).isEqualTo("4-4-2");
        assertThat(matchdetails.getFormation(false)).isEqualTo("4-5-1");
        assertThat(matchdetails.getResult()).isEqualTo(" 7 !ls.match.result.separation! 0");
    }

    @Test
    void parseMatchdetailsFromString_withMatchLineup() throws IOException {
        // given
        var xmlFileMatchDetails = ResourceUtils.getResourceFileAsString("chpp/matchdetails/matchdetails.xml");
        var xmlFileMatchLineupTeam1 = ResourceUtils.getResourceFileAsString("chpp/matchlineup/team2189893.xml");
        var xmlFileMatchLineupTeam2 = ResourceUtils.getResourceFileAsString("chpp/matchlineup/team520472.xml");

        var matchLineup = XMLMatchLineupParser.parseMatchLineupFromString(xmlFileMatchLineupTeam1);
        matchLineup.setGuestTeam(XMLMatchLineupParser.parseMatchLineupFromString(xmlFileMatchLineupTeam2).getGuestTeam());

        final var expectedMatchDate = HODateTime.fromHT("2026-10-06 18:15:00");

        final var expectedMatchEvents = new ArrayList<MatchEvent>();
        expectedMatchEvents.add(createExpectedMatchEvent(0, Matchdetails.eInjuryType.NA, 50000, 31, 5, 0, 0, 2154092, true, 1648, true, "<a href=\"/Club/Stadium/?stadiumId=2154092\">Alte Rösterei</a> - 1648 ZuschauerInnen blickten angesichts der dunklen Wolken besorgt in den Himmel, aber der befürchtete Regen blieb aus. <a href=\"/Club/HallOfFame/Player.aspx?playerId=455444108\">Sultan Hafeeth Al-Buayqy</a> wurde als Schiedsrichter angesetzt, Unterstützung erhielt er von seinen Assistenten <a href=\"/Club/HallOfFame/Player.aspx?playerId=474275056\">Bill Matthews</a> und <a href=\"/Club/HallOfFame/Player.aspx?playerId=459237940\">Idar Åkesson</a>."));
        expectedMatchEvents.add(createExpectedMatchEvent(1, Matchdetails.eInjuryType.NA, 0, 19, 1, 0, 0, 0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(2, Matchdetails.eInjuryType.NA, 2157350, 21, 4, 0, 0, 0, true, 0, true, "Folgende Spieler begannen: <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"Sievers\" class=\"homeplayer\">Sievers</a> - <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Eggimann\" class=\"homeplayer\">Eggimann</a>, <a href=\"/Club/Players/Player.aspx?playerId=480433526\" title=\"Br&#252;ggemann\" class=\"homeplayer\">Brüggemann</a>, <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Mahler\" class=\"homeplayer\">Mahler</a>, <a href=\"/Club/Players/Player.aspx?playerId=503053978\" title=\"Cherundolo\" class=\"homeplayer\">Cherundolo</a> - <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Klaus\" class=\"homeplayer\">Klaus</a>, <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Pinto\" class=\"homeplayer\">Pinto</a>, <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Leopold\" class=\"homeplayer\">Leopold</a>, <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Maina\" class=\"homeplayer\">Maina</a> - <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Lerf\" class=\"homeplayer\">Lerf</a>, <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Stendel\" class=\"homeplayer\">Stendel</a>.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(3, Matchdetails.eInjuryType.NA, 128206, 21, 1, 0, 0, 0, true, 0, true, "Es liefen auf: <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Schlegelmilch\" class=\"awayplayer\">Schlegelmilch</a> - <a href=\"/Club/Players/Player.aspx?playerId=489791494\" title=\"Masciadri\" class=\"awayplayer\">Masciadri</a>, <a href=\"/Club/Players/Player.aspx?playerId=496877677\" title=\"Gierer\" class=\"awayplayer\">Gierer</a>, <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Rutherford\" class=\"awayplayer\">Rutherford</a>, <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Parmendola\" class=\"awayplayer\">Parmendola</a> - <a href=\"/Club/Players/Player.aspx?playerId=490747950\" title=\"Rajs\" class=\"awayplayer\">Rajs</a>, <a href=\"/Club/Players/Player.aspx?playerId=490797976\" title=\"Hultflod\" class=\"awayplayer\">Hultflod</a>, <a href=\"/Club/Players/Player.aspx?playerId=474503458\" title=\"Foltea\" class=\"awayplayer\">Foltea</a>, <a href=\"/Club/Players/Player.aspx?playerId=472214265\" title=\"Sellinger\" class=\"awayplayer\">Sellinger</a>, <a href=\"/Club/Players/Player.aspx?playerId=492622921\" title=\"Adderton\" class=\"awayplayer\">Adderton</a> - <a href=\"/Club/Players/Player.aspx?playerId=472437980\" title=\"Schichta\" class=\"awayplayer\">Schichta</a>.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(4, Matchdetails.eInjuryType.NA, 0, 24, 2, 0, 0, 1040402, true, 1040501, true, "<span class=\"hometeam\">Kaffee-Junkies Hannover</span> lief in einem 4-4-2 auf,  <span class=\"awayteam\">SSC Napoli-Köpenick</span> entschied sich für ein 4-5-1. "));
        expectedMatchEvents.add(createExpectedMatchEvent(5, Matchdetails.eInjuryType.NA, 0, 598, 2, 0, 0, 0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(6, Matchdetails.eInjuryType.NA, 2157350, 335, 4, 1, 0, 151, true, 0, true, "<span class=\"hometeam\">KJH</span> befleißigte sich einer kreativen Spielweise. Sieht <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=11#skill\" class=\"skill\">brillant</a> aus, was die Taktikstärke angeht. "));
        expectedMatchEvents.add(createExpectedMatchEvent(7, Matchdetails.eInjuryType.NA, 2157350, 481, 5, 1, 5, 0, true, 1040402, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(8, Matchdetails.eInjuryType.NA, 2157350, 121, 1, 1, 8, 456920840, false, 472120229, false, "Ein ohrenbetäubendes Vuvuzela-Konzert brach in der 8. Minute auf den Tribünen aus, nachdem <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> zentral vor dem Tor abgezogen und einen Treffer für <span class=\"hometeam\">KJH</span> markiert hatte. Es stand nun 1 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(9, Matchdetails.eInjuryType.NA, 2157350, 61, 1, 1, 11, 95, false, 0, true, "Bei <span class=\"hometeam\">KJH</span> schien nach 11 Minuten keiner wirklich zu wissen, wo er zu stehen hat. Die Mannschaft war <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=7#skill\" class=\"skill\">gut</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(10, Matchdetails.eInjuryType.NA, 2157350, 471, 4, 1, 13, 59, false, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(11, Matchdetails.eInjuryType.NA, 2157350, 352, 5, 1, 15, 511385914, false, 503221197, false, "Vollkommen überrascht war <a href=\"/Club/Players/Player.aspx?playerId=511385914\" title=\"Linton Maina\" class=\"homeplayer\">Linton Maina</a> von seiner Auswechslung in der 15. Minute. Bei <span class=\"hometeam\">KJH</span> betrat an seiner Stelle <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> den Rasen."));
        expectedMatchEvents.add(createExpectedMatchEvent(12, Matchdetails.eInjuryType.NA, 2157350, 61, 2, 1, 15, 79, false, 0, true, "<span class=\"hometeam\">KJH</span> hatte offensichtlich Probleme mit den taktischen Anweisungen. Die Mannschaft war in der 15. Minute nur noch <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=4#skill\" class=\"skill\">schwach</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(13, Matchdetails.eInjuryType.NA, 2157350, 489, 5, 1, 17, 485463818, false, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(14, Matchdetails.eInjuryType.NA, 2157350, 289, 2, 1, 20, 502246230, false, 483281921, false, "<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> erntete in der 20. Minute tosenden Applaus, als er einen Pfau einfing, der plötzlich durch seinen Strafraum stolzierte. Das Tier war bei den nahen Dreharbeiten zu \"Bauer sucht Pfau\" stiften gegangen. Diese spektakuläre Aktion war noch lange in aller Munde. Dass <a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a> noch in der gleichen Minute <span class=\"hometeam\">KJH</span>-Flitzer <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> den Ball vom Fuß spitzelte und so einen Gegentreffer verhinderte, ging dabei fast unter."));
        expectedMatchEvents.add(createExpectedMatchEvent(15, Matchdetails.eInjuryType.NA, 128206, 452, 3, 1, 22, 472120229, false, 100, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(16, Matchdetails.eInjuryType.NA, 2157350, 131, 1, 1, 25, 462788780, false, 472120229, false, "Nach 25 Minuten kam es für die Gäste noch schlimmer, als <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> es ohne große Widerstände durch die Abwehrmitte vor das Tor schaffte und zum 2 - 0 traf."));
        expectedMatchEvents.add(createExpectedMatchEvent(17, Matchdetails.eInjuryType.NA, 2157350, 116, 3, 1, 33, 485463818, false, 503221197, false, "<span class=\"hometeam\">KJH</span>-Akteur <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> konnte sich in der 33. Minute dank seiner Schnelligkeit mühelos von seinem Gegenspieler absetzen. <a href=\"/Club/Players/Player.aspx?playerId=485463818\" title=\"Derk Lerf\" class=\"homeplayer\">Derk Lerf</a> hatte keinerlei Schwierigkeit, die scharfe Hereingabe am herausstürzenden Torwart vorbei zum 3 - 0 zu verwandeln."));
        expectedMatchEvents.add(createExpectedMatchEvent(18, Matchdetails.eInjuryType.NA, 2157350, 231, 2, 1, 34, 457053654, false, 472120229, false, "Eine wundervolle Kombination im Angriffszentrum hätte nach 34 Minuten fast zu einem weiteren Tor für <span class=\"hometeam\">KJH</span> geführt, doch <a href=\"/Club/Players/Player.aspx?playerId=472120229\" title=\"Friederich Schlegelmilch\" class=\"awayplayer\">Friederich Schlegelmilch</a> war bei diesem Schuss von <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> auf dem Posten."));
        expectedMatchEvents.add(createExpectedMatchEvent(19, Matchdetails.eInjuryType.NA, 2157350, 134, 2, 1, 37, 462788780, false, 472120229, false, "<span class=\"hometeam\">KJH</span> wurde in der 37. Minute ein Strafstoß zugesprochen. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> ließ sich die Chance nicht entgehen und verwandelte mit einem Lächeln auf dem Gesicht zum 4 - 0 für das Heimteam."));
        expectedMatchEvents.add(createExpectedMatchEvent(20, Matchdetails.eInjuryType.NA, 2157350, 218, 1, 1, 40, 502246230, false, 462788780, false, "<a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> schlug nach 40 Minuten einen raffiniert herein gezogenen Eckball, doch <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> war zu überrascht, den Ball zu bekommen, und verstolperte die Chance für <span class=\"hometeam\">KJH</span>."));
        expectedMatchEvents.add(createExpectedMatchEvent(21, Matchdetails.eInjuryType.NA, 2157350, 232, 3, 1, 41, 497733968, false, 472120229, false, "Nach einer Flanke von links spitzelte <a href=\"/Club/Players/Player.aspx?playerId=497733968\" title=\"Mario Eggimann\" class=\"homeplayer\">Mario Eggimann</a> den Ball in Richtung Glück und Seligkeit, doch der Außenpfosten verhinderte ein weiteres Tor für <span class=\"hometeam\">KJH</span>. Da fiel dem Präsidium des gegnerischen Teams auf der Tribüne sichtlich ein Stein vom Herzen, dass diese Situation in der 41. Minute ein gutes Ende nahm. "));
        expectedMatchEvents.add(createExpectedMatchEvent(22, Matchdetails.eInjuryType.NA, 2157350, 510, 4, 1, 42, 502246230, false, 0, true, "<span class=\"hometeam\">KJH</span>-Spieler <a href=\"/Club/Players/Player.aspx?playerId=502246230\" title=\"Felix Klaus\" class=\"homeplayer\">Felix Klaus</a> hatte es bei seiner Attacke in der 42. Minute nur auf die Beine des Gegenspielers abgesehen. Folgerichtig sah er die Gelbe Karte."));
        expectedMatchEvents.add(createExpectedMatchEvent(23, Matchdetails.eInjuryType.NA, 128206, 360, 3, 1, 43, 472291599, false, 0, true, "Spielminute 43: Um das Team noch auf die Siegerstraße zu bringen, richtete der <span class=\"awayteam\">Napoli-Köpenick</span>-Trainer seine Mannen taktisch neu aus."));
        expectedMatchEvents.add(createExpectedMatchEvent(24, Matchdetails.eInjuryType.NA, 128206, 360, 5, 1, 44, 496877677, false, 0, true, "Bei <span class=\"awayteam\">Napoli-Köpenick</span> war man über den Rückstand verständlicherweise nicht erfreut, daher formierten sich die Spieler nach 44 Minuten auf Anweisung ihres Trainers neu."));
        expectedMatchEvents.add(createExpectedMatchEvent(25, Matchdetails.eInjuryType.NA, 0, 45, 3, 1, 45, 0, true, 0, true, "Zur Halbzeit stand es 4 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(26, Matchdetails.eInjuryType.NA, 2157350, 40, 4, 1, 45, 0, true, 55, false, "<span class=\"hometeam\">KJH</span> war in dieser Halbzeit das dominierende Team - die Spieler brachten den Ballbesitz auf 55 Prozent.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(27, Matchdetails.eInjuryType.NA, 0, 597, 3, 2, 46, 0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(28, Matchdetails.eInjuryType.NA, 2157350, 64, 4, 2, 46, 456933528, false, 94, false, "Ein erneuter Blick auf die Taktiktafel in der Pause sorgte dafür, dass die Spieler von <span class=\"hometeam\">KJH</span> nun <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=6#skill\" class=\"skill\">passabel</a> organisiert waren."));
        expectedMatchEvents.add(createExpectedMatchEvent(29, Matchdetails.eInjuryType.NA, 2157350, 361, 3, 2, 46, 473360703, false, 0, true, "Zufrieden mit dem Spielstand verordnete der <span class=\"hometeam\">KJH</span>-Trainer seiner Mannschaft in der 46. Minute eine neue taktische Ausrichtung."));
        expectedMatchEvents.add(createExpectedMatchEvent(31, Matchdetails.eInjuryType.NA, 2157350, 481, 2, 2, 51, 0, true, 1040303, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(32, Matchdetails.eInjuryType.NA, 2157350, 132, 3, 2, 52, 456920840, false, 472120229, false, "<span class=\"hometeam\">KJH</span> baute in der 52. Minute die Führung auf 5 - 0 aus, nachdem ein Verteidiger der Gäste den Ball leichtfertig an <a href=\"/Club/Players/Player.aspx?playerId=456920840\" title=\"Klaus Mahler\" class=\"homeplayer\">Klaus Mahler</a> verloren hatte. Dieser drang über links in den Strafraum ein und spielte dann noch gekonnt den Torwart aus, bevor er das Leder lässig über die Linie schob."));
        expectedMatchEvents.add(createExpectedMatchEvent(33, Matchdetails.eInjuryType.NA, 0, 464, 2, 2, 56, 0, true, 1648, false, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(34, Matchdetails.eInjuryType.NA, 2157350, 352, 4, 2, 60, 457053654, false, 461796896, false, "Darauf hatte <a href=\"/Club/Players/Player.aspx?playerId=461796896\" title=\"Lars Stindl\" class=\"homeplayer\">Lars Stindl</a> schon länger gewartet. Nach 60 Minuten war es soweit, er kam zu seinem Einsatz für <span class=\"hometeam\">KJH</span>. Im Gegenzug verließ <a href=\"/Club/Players/Player.aspx?playerId=457053654\" title=\"Enzo Leopold\" class=\"homeplayer\">Enzo Leopold</a> den Platz."));
        expectedMatchEvents.add(createExpectedMatchEvent(35, Matchdetails.eInjuryType.NA, 2157350, 61, 4, 2, 62, 75, false, 0, true, "Spielminute 62: Bei <span class=\"hometeam\">KJH</span> herrschte ein ziemliches Durcheinander auf dem Feld - die Spieler hatten Probleme, ihre Positionen zu halten, und waren <a href=\"/Help/Rules/AppDenominations.aspx?lt=skill&ll=3#skill\" class=\"skill\">armselig</a> organisiert."));
        expectedMatchEvents.add(createExpectedMatchEvent(36, Matchdetails.eInjuryType.NA, 2157350, 132, 2, 2, 63, 462788780, false, 472120229, false, "Ein schnell vorgetragener Angriff von <span class=\"hometeam\">KJH</span> über die linke Seite des Feldes wurde in der 63. Minute mit einem weiteren Tor zum 6 - 0 gekrönt. <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> hatte aus spitzem Winkel abgezogen. Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor."));
        expectedMatchEvents.add(createExpectedMatchEvent(37, Matchdetails.eInjuryType.NA, 2157350, 46, 1, 2, 63, 462788780, false, 0, true, "Damit konnte sich <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a> als Hattrickschütze notieren lassen."));
        expectedMatchEvents.add(createExpectedMatchEvent(38, Matchdetails.eInjuryType.NA, 128206, 494, 1, 2, 74, 0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(39, Matchdetails.eInjuryType.NA, 128206, 251, 4, 2, 83, 472291599, false, 473025501, false, "83 Minuten waren gespielt, als <a href=\"/Club/Players/Player.aspx?playerId=472291599\" title=\"Raven Rutherford\" class=\"awayplayer\">Raven Rutherford</a> sich in der Mitte prima durchsetzte und nur noch <a href=\"/Club/Players/Player.aspx?playerId=473025501\" title=\"J&#246;rg &#39;Colt&#39; Sievers\" class=\"homeplayer\">Jörg 'Colt' Sievers</a> vor sich hatte. Der blieb lange stehen und wehrte den Schuss ab. Es blieb beim 6 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(40, Matchdetails.eInjuryType.NA, 2157350, 133, 4, 2, 84, 473360703, false, 472120229, false, "Eine schöne Ballstafette auf dem rechten Flügel der Gastgeber konnte von der gegnerischen Abwehr nicht unterbunden werden. So gelangte das Objekt der Begierde schließlich zu\n" +
            "                    <a href=\"/Club/Players/Player.aspx?playerId=473360703\" title=\"Sergio Pinto\" class=\"homeplayer\">Sergio Pinto</a>, der ein weiteres Tor für <span class=\"hometeam\">KJH</span> erzielte. Die Führung erhöhte sich dadurch in der 84. Minute auf 7 - 0.\n" +
            "                    Ein wichtiger Pass zum richtigen Zeitpunkt: <a href=\"/Club/Players/Player.aspx?playerId=503221197\" title=\"Leonardo &#39;Leo&#39; Bittencourt\" class=\"homeplayer\">Leonardo 'Leo' Bittencourt</a> bereitete das Tor exzellent vor."));
        expectedMatchEvents.add(createExpectedMatchEvent(41, Matchdetails.eInjuryType.NA, 0, 75, 5, 2, 89, 0, true, 0, true, "Die Nachspielzeit beträgt 3 Minute(n)."));
        expectedMatchEvents.add(createExpectedMatchEvent(42, Matchdetails.eInjuryType.NA, 0, 599, 3, 2, 93, 0, true, 0, true, "Die Begegnung endete 7 - 0."));
        expectedMatchEvents.add(createExpectedMatchEvent(43, Matchdetails.eInjuryType.NA, 2157350, 601, 3, 2, 93, 0, true, 0, true, ""));
        expectedMatchEvents.add(createExpectedMatchEvent(44, Matchdetails.eInjuryType.NA, 2157350, 40, 1, 2, 93, 0, true, 53, false, "<span class=\"hometeam\">KJH</span> erreichte laut HT-Datenbank 53 Prozent Ballbesitz.<br ><br >"));
        expectedMatchEvents.add(createExpectedMatchEvent(45, Matchdetails.eInjuryType.NA, 2157350, 41, 2, 2, 93, 462788780, false, 0, true, "Der beste Spieler von <span class=\"hometeam\">KJH</span> war ohne jeden Zweifel <a href=\"/Club/Players/Player.aspx?playerId=462788780\" title=\"Daniel Stendel\" class=\"homeplayer\">Daniel Stendel</a>."));
        expectedMatchEvents.add(createExpectedMatchEvent(46, Matchdetails.eInjuryType.NA, 128206, 41, 3, 2, 93, 483281921, false, 0, true, "Nach diesem Spiel forderten die Fans von <span class=\"awayteam\">Napoli-Köpenick</span> eine \"<a href=\"/Club/Players/Player.aspx?playerId=483281921\" title=\"Cesare Parmendola\" class=\"awayplayer\">Cesare Parmendola</a>-Straße\" - er hatte eine herausragende Leistung abgeliefert."));

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

        for (var i = 0; i < matchdetails.getHighlights().size(); i++) {
            var event = matchdetails.getHighlights().get(i);
            var expectedEvent = expectedMatchEvents.get(i);

            assertThat(event.getMatchEventIndex()).isEqualTo(expectedEvent.getMatchEventIndex());
            assertThat(event.getInjuryType()).isEqualTo(expectedEvent.getInjuryType());
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
        assertThat(matchdetails.getInjuries()).isEmpty();
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

    private MatchEvent createExpectedMatchEvent(int eventIndex, Matchdetails.eInjuryType injuryType, int teamID, int matchEventID, int eventVariation, int matchPartId, int minute, int playerID, boolean playerIsFromHomeTeam, int assistingPlayerID, boolean assistingPlayerIsFromHomeTeam, String eventText) {
        var matchEvent = new  MatchEvent();
        matchEvent.setMatchEventIndex(eventIndex);
        matchEvent.setInjuryType(injuryType);
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
        assertThat(matchEvent.getInjuryType()).isEqualTo(other.getInjuryType());
    }
}
