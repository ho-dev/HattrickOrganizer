package core.model.player;

import core.constants.player.PlayerAbility;
import core.model.HOProperties;
import core.util.AmountOfMoney;
import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

    private static final int HRF_ID = 1;
    private static final HODateTime HRF_DATE = HODateTime.now();

    @Test
    void player() {
        // given
        HOProperties properties = new HOProperties();

        final var playerId = 2;
        final var firstName = "firstname";
        final var nickName = "nickName";
        final var lastName = "lastName";
        final var arrivalDate = HODateTime.fromHT("2026-01-15 21:26:31");
        final var age = 96;
        final var ageDays = 18;
        final var stamina = PlayerAbility.DISASTROUS;
        final var form = PlayerAbility.WRETCHED;
        final var goalkeeperSkill = PlayerAbility.POOR;
        final var defendingSkill = PlayerAbility.WEAK;
        final var playmakingSkill = PlayerAbility.INADEQUATE;
        final var passingSkill = PlayerAbility.PASSABLE;
        final var wingerSkill = PlayerAbility.SOLID;
        final var scoringSkill = PlayerAbility.EXCELLENT;
        final var setPiecesSkill = PlayerAbility.FORMIDABLE;
        final var specialty = Specialty.Regainer.getValue();
        final var gentleness = 3;
        final var honesty = 4;
        final var aggressivity = 5;
        final var experience = 6;
        final var homeGrown = true;
        final var loyalty = 7;
        final var leadership = 8;
        final var wage = new AmountOfMoney(12345);
        final var countryId = 9;
        final var tsi = 9999;
        final var subWingerSkill = 1.1;
        final var subPassingSkill = 2.2;
        final var subPlaymakingSkill = 3.3;
        final var subSetPiecesSkill = 4.4;
        final var subScoringSkill = 5.5;
        final var subGoalkeeperSkill = 6.6;
        final var subDefendingSkill = 7.7;
        final var subExperience = 8.8;
        final var totalCards = 10;
        final var injuryWeeks = 11;
        final var friendlyGoals = 12;
        final var leagueGoals = 13;
        final var cupGameGoals = 14;
        final var totalGoals = 16;
        final var hatTricks = 17;
        final var currentTeamGoals = 18;
        final var currentTeamMatches = 19;
        final var assistsCurrentTeam = 20;
        final var careerAssists = 21;
        final var isExternallyRecruitedCoach = true;
        final var rating = 22;
        final var trainerType = TrainerType.Offensive;
        final var shirtNumber = 24;
        final var transferListed = true;
        final var internationalMatches = 25;
        final var u20InternationalMatches = 26;
        final var nationalTeamId = 27;
        final var lastMatchDate = "28";
        final var lastMatchId = 29;
        final var lastMatchPosition = MatchRoleID.rightWinger;
        final var lastMatchMinutes = 31;
        final var lastMatchRating = 32.6;
        final var expectedLastMatchRating = 65;
        final var lastMatchRatingEndOfGame = 33.6;
        final var expectedLastMatchRatingEndOfGame = 67;
        final var playerCategory = PlayerCategory.Forward;
        final var playerStatement = "playerStatement";
        final var ownerNotes = "ownerNotes";
        properties.put("id", String.valueOf(playerId));
        properties.put("firstname", firstName);
        properties.put("nickname", nickName);
        properties.put("lastname", lastName);
        properties.put("arrivaldate", arrivalDate.toHT());
        properties.put("ald", String.valueOf(age));
        properties.put("agedays", String.valueOf(ageDays));
        properties.put("uth", String.valueOf(stamina));
        properties.put("for", String.valueOf(form));
        properties.put("mlv", String.valueOf(goalkeeperSkill));
        properties.put("bac", String.valueOf(defendingSkill));
        properties.put("spe", String.valueOf(playmakingSkill));
        properties.put("fra", String.valueOf(passingSkill));
        properties.put("ytt", String.valueOf(wingerSkill));
        properties.put("mal", String.valueOf(scoringSkill));
        properties.put("fas", String.valueOf(setPiecesSkill));
        properties.put("speciality", String.valueOf(specialty));
        properties.put("gentleness", String.valueOf(gentleness));
        properties.put("honesty", String.valueOf(honesty));
        properties.put("aggressiveness", String.valueOf(aggressivity));
        properties.put("rut", String.valueOf(experience));
        properties.put("homegr", Boolean.toString(homeGrown));
        properties.put("loy", String.valueOf(loyalty));
        properties.put("led", String.valueOf(leadership));
        properties.put("sal", String.valueOf(wage.getSwedishKrona().setScale(0, RoundingMode.DOWN).intValueExact()));
        properties.put("countryid", String.valueOf(countryId));
        properties.put("mkt", String.valueOf(tsi));
        properties.put("yttsub", String.valueOf(subWingerSkill));
        properties.put("frasub", String.valueOf(subPassingSkill));
        properties.put("spesub", String.valueOf(subPlaymakingSkill));
        properties.put("fassub", String.valueOf(subSetPiecesSkill));
        properties.put("malsub", String.valueOf(subScoringSkill));
        properties.put("mlvsub", String.valueOf(subGoalkeeperSkill));
        properties.put("bacsub", String.valueOf(subDefendingSkill));
        properties.put("experiencesub", String.valueOf(subExperience));
        properties.put("warnings", String.valueOf(totalCards));
        properties.put("ska", String.valueOf(injuryWeeks));
        properties.put("gtt", String.valueOf(friendlyGoals));
        properties.put("gtl", String.valueOf(leagueGoals));
        properties.put("gtc", String.valueOf(cupGameGoals));
        properties.put("gev", String.valueOf(totalGoals));
        properties.put("hat", String.valueOf(hatTricks));
        properties.put("goalscurrentteam", String.valueOf(currentTeamGoals));
        properties.put("matchescurrentteam", String.valueOf(currentTeamMatches));
        properties.put("assistscurrentteam", String.valueOf(assistsCurrentTeam));
        properties.put("careerassists", String.valueOf(careerAssists));
        properties.put("lineupdisabled", String.valueOf(isExternallyRecruitedCoach));
        properties.put("rating", String.valueOf(rating));
        properties.put("trainertype", String.valueOf(trainerType.toInt()));
        properties.put("playernumber", String.valueOf(shirtNumber));
        properties.put("transferlisted", Boolean.toString(transferListed));
        properties.put("caps", String.valueOf(internationalMatches));
        properties.put("capsu20", String.valueOf(u20InternationalMatches));
        properties.put("nationalteamid", String.valueOf(nationalTeamId));
        properties.put("lastmatch_date", lastMatchDate);
        properties.put("lastmatch_id", String.valueOf(lastMatchId));
        properties.put("lastmatch_positioncode", String.valueOf(lastMatchPosition));
        properties.put("lastmatch_playedminutes", String.valueOf(lastMatchMinutes));
        properties.put("lastmatch_rating", String.valueOf(lastMatchRating));
        properties.put("lastmatch_ratingendofgame", String.valueOf(lastMatchRatingEndOfGame));
        properties.put("playercategoryid", String.valueOf(playerCategory.getId()));
        properties.put("statement", playerStatement);
        properties.put("ownernotes", ownerNotes);

        // when
        final var player = new Player(properties, HRF_DATE, HRF_ID);

        // then
        assertThat(player.getHrfId()).isEqualTo(HRF_ID);
        assertThat(player.getPlayerId()).isEqualTo(playerId);
        assertThat(player.getFirstName()).isEqualTo(firstName);
        assertThat(player.getNickName()).isEqualTo(nickName);
        assertThat(player.getLastName()).isEqualTo(lastName);
        assertThat(player.getArrivalDate()).isEqualTo(arrivalDate);
        assertThat(player.getAge()).isEqualTo(age);
        assertThat(player.getAgeDays()).isEqualTo(ageDays);
        assertThat(player.getStamina()).isEqualTo(stamina);
        assertThat(player.getForm()).isEqualTo(form);
        assertThat(player.getGoalkeeperSkill()).isEqualTo(goalkeeperSkill);
        assertThat(player.getDefendingSkill()).isEqualTo(defendingSkill);
        assertThat(player.getPlaymakingSkill()).isEqualTo(playmakingSkill);
        assertThat(player.getPassingSkill()).isEqualTo(passingSkill);
        assertThat(player.getWingerSkill()).isEqualTo(wingerSkill);
        assertThat(player.getScoringSkill()).isEqualTo(scoringSkill);
        assertThat(player.getSetPiecesSkill()).isEqualTo(setPiecesSkill);
        assertThat(player.getSpecialty()).isEqualTo(specialty);
        assertThat(player.getGentleness()).isEqualTo(gentleness);
        assertThat(player.getHonesty()).isEqualTo(honesty);
        assertThat(player.getAggressivity()).isEqualTo(aggressivity);
        assertThat(player.getExperience()).isEqualTo(experience);
        assertThat(player.isHomeGrown()).isEqualTo(homeGrown);
        assertThat(player.getWage()).isEqualTo(wage);
        assertThat(player.getCountryId()).isEqualTo(countryId);
        assertThat(player.getTsi()).isEqualTo(tsi);
        assertThat(player.getSubWingerSkill()).isEqualTo(subWingerSkill);
        assertThat(player.getSubPassingSkill()).isEqualTo(subPassingSkill);
        assertThat(player.getSubPlaymakingSkill()).isEqualTo(subPlaymakingSkill);
        assertThat(player.getSubSetPiecesSkill()).isEqualTo(subSetPiecesSkill);
        assertThat(player.getSubScoringSkill()).isEqualTo(subScoringSkill);
        assertThat(player.getSubGoalkeeperSkill()).isEqualTo(subGoalkeeperSkill);
        assertThat(player.getSubDefendingSkill()).isEqualTo(subDefendingSkill);
        assertThat(player.getSubExperience()).isEqualTo(subExperience);
        assertThat(player.getHrfDate()).isEqualTo(HRF_DATE);
        assertThat(player.getTotalCards()).isEqualTo(totalCards);
        assertThat(player.getInjuryWeeks()).isEqualTo(injuryWeeks);
        assertThat(player.getFriendlyGoals()).isEqualTo(friendlyGoals);
        assertThat(player.getLeagueGoals()).isEqualTo(leagueGoals);
        assertThat(player.getCupGameGoals()).isEqualTo(cupGameGoals);
        assertThat(player.getTotalGoals()).isEqualTo(totalGoals);
        assertThat(player.getHatTricks()).isEqualTo(hatTricks);
        assertThat(player.getCurrentTeamGoals()).isEqualTo(currentTeamGoals);
        assertThat(player.getCurrentTeamMatches()).isEqualTo(currentTeamMatches);
        assertThat(player.getCareerAssists()).isEqualTo(careerAssists);
        assertThat(player.getAssistsCurrentTeam()).isEqualTo(assistsCurrentTeam);
        assertThat(player.isExternallyRecruitedCoach()).isEqualTo(isExternallyRecruitedCoach);
        assertThat(player.getRating()).isEqualTo(rating);
        assertThat(player.getTrainerType()).isEqualTo(trainerType);
        assertThat(player.getShirtNumber()).isEqualTo(shirtNumber);
        assertThat(player.getTransferListed()).isEqualTo(1);
        assertThat(player.getInternationalMatches()).isEqualTo(internationalMatches);
        assertThat(player.getU20InternationalMatches()).isEqualTo(u20InternationalMatches);
        assertThat(player.getNationalTeamId()).isEqualTo(nationalTeamId);
        assertThat(player.getLastMatchDate()).isEqualTo(lastMatchDate);
        assertThat(player.getLastMatchId()).isEqualTo(lastMatchId);
        assertThat(player.getLastMatchPosition()).isEqualTo(lastMatchPosition);
        assertThat(player.getLastMatchRating()).isEqualTo(expectedLastMatchRating);
        assertThat(player.getLastMatchRatingEndOfGame()).isEqualTo(expectedLastMatchRatingEndOfGame);
        assertThat(player.getPlayerCategory()).isEqualTo(playerCategory);
        assertThat(player.getPlayerStatement()).isEqualTo(playerStatement);
        assertThat(player.getOwnerNotes()).isEqualTo(ownerNotes);
    }

    @Test
    void player_coachSkill() {
        // given
        HOProperties properties = new HOProperties();
        final var coachSkill = 23;
        final var expectedCoachSkill = coachSkill + 3;
        final var cost = new AmountOfMoney(23456);
        final var contractDate = LocalDate.now().toString();

        properties.put("trainerskilllevel", String.valueOf(coachSkill));
        properties.put("cost", String.valueOf(cost.getSwedishKrona().setScale(0, RoundingMode.DOWN).intValueExact()));
        properties.put("contractdate", contractDate);

        // when
        final var player = new Player(properties, HRF_DATE, HRF_ID);

        // then
        assertThat(player.getHrfId()).isEqualTo(HRF_ID);
        assertThat(player.getCoachSkill()).isEqualTo(expectedCoachSkill);
        assertThat(player.getWage()).isEqualTo(cost);
        assertThat(player.getContractDate()).isEqualTo(contractDate);
    }
}
