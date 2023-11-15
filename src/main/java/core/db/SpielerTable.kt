package core.db

import core.constants.player.PlayerSkill
import core.db.DBManager.PreparedStatementBuilder
import core.model.enums.MatchType
import core.model.player.*
import core.util.HODateTime
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class SpielerTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.newInstance().setColumnName("HRF_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.hrfId })
                .setSetter(BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.hrfId = v as Int })
                .setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SpielerID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.playerId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.playerId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Datum")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.hrfDate?.toDbTimestamp() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.hrfDate = v as HODateTime? })
                .setType(Types.TIMESTAMP).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GelbeKarten")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.totalCards }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.totalCards = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FirstName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.firstName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.firstName = v as String? })
                .setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NickName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.nickName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.nickName = v as String? })
                .setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastName }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastName = v as String? })
                .setType(Types.VARCHAR).isNullable(false).setLength(100).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Age")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.age }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.age = (v as Int) }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Kondition")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.stamina }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.stamina = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Form")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.form }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.form = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Torwart")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.goalkeeperSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.goalkeeperSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.newInstance().setColumnName("Verteidigung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.defendingSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.defendingSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Spielaufbau")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.playmakingSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.playmakingSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Fluegel")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.wingerSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.wingerSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Torschuss")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.scoringSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.scoringSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Passpiel")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.passingSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.passingSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Standards")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.setPiecesSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.setPiecesSkill = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubTorwart")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.KEEPER) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.KEEPER,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubVerteidigung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.DEFENDING) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.DEFENDING,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubSpielaufbau")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.PLAYMAKING) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.PLAYMAKING,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubFluegel")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.WINGER) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.WINGER,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubTorschuss")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.SCORING) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.SCORING,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubPasspiel")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.PASSING) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.PASSING,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubStandards")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getSub4Skill(PlayerSkill.SET_PIECES) })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any ->
                        (p as Player?)!!.setSubskill4PlayerSkill(
                            PlayerSkill.SET_PIECES,
                            (v as Float).toDouble()
                        )
                    }).setType(
                Types.REAL
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iSpezialitaet")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.specialty }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.specialty = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iCharakter")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.gentleness }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.gentleness = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iAnsehen")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.honesty }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.honesty = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("iAgressivitaet")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.agressivity }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.agressivity = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Fuehrung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.leadership }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.leadership = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Erfahrung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.experience }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.experience = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Gehalt")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.wage }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.wage = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Land")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.nationalityId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.nationalityId = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Marktwert")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getMarketValue() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.tsi = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Verletzt")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.injuryWeeks }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.injuryWeeks = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ToreFreund")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.friendlyGoals }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.friendlyGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ToreLiga")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.leagueGoals }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.leagueGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TorePokal")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.cupGameGoals }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.cupGameGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ToreGesamt")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.totalGoals }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.totalGoals = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Hattrick")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.hatTricks }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.hatTricks = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Bewertung")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.rating }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.rating = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrainerTyp")
                .setGetter(Function<Any?, Any?> { p: Any? ->
                    TrainerType.toInt(
                        (p as Player?)!!.trainerType
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any ->
                    (p as Player?)!!.trainerType = TrainerType.fromInt(v as Int)
                }).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Trainer")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.coachSkill }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.coachSkill = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerNumber")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.shirtNumber }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.shirtNumber = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TransferListed")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.transferListed }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.transferListed = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Caps")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.internationalMatches }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.internationalMatches = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CapsU20")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.u20InternationalMatches }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.u20InternationalMatches = v as Int })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AgeDays")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.ageDays }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.ageDays = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TrainingBlock")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.hasTrainingBlock() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.setTrainingBlock(v as Boolean) })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Loyalty")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.loyalty }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.loyalty = v as Int }).setType(Types.INTEGER)
                .isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HomeGrown")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.homeGrown }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as Player?)!!.homeGrown = v as Boolean })
                .setType(Types.BOOLEAN).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NationalTeamID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.nationalTeamId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.nationalTeamId = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SubExperience")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.subExperience }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.setSubExperience(v as Double?) })
                .setType(Types.DOUBLE).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatchDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchDate }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchDate = v as String? })
                .setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatchRating")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchRating }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchRating = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatchId")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchId }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchId = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LAST_MATCH_TYPE")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchType?.id }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                    (p as Player?)!!.lastMatchType = MatchType.getById(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArrivalDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.arrivalDate }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.arrivalDate = v as String? })
                .setType(Types.VARCHAR).isNullable(true).setLength(100).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("GoalsCurrentTeam")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.currentTeamGoals }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.currentTeamGoals = (v as Int?)!! })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerCategory")
                .setGetter(Function<Any?, Any?> { p: Any? ->
                    PlayerCategory.idOf(
                        (p as Player?)!!.playerCategory
                    )
                }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? ->
                    (p as Player?)!!.playerCategory = PlayerCategory.valueOf(v as Int?)
                }).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Statement")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.playerStatement }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.playerStatement = v as String? })
                .setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("OwnerNotes")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.ownerNotes }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.ownerNotes = v as String? })
                .setType(Types.VARCHAR).isNullable(true).setLength(512).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatch_PlayedMinutes")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchMinutes }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchMinutes = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatch_PositionCode")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchPosition }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchPosition = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastMatch_RatingEndOfGame").setGetter(
                Function<Any?, Any?> { p: Any? -> (p as Player?)!!.lastMatchRatingEndOfGame }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.lastMatchRatingEndOfGame = v as Int? })
                .setType(
                    Types.INTEGER
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MotherclubId")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getOrDownloadMotherclubId() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.motherClubId = (v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MotherclubName")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.getOrDownloadMotherClubName() })
                .setSetter(
                    BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.motherClubName = (v as String?) })
                .setType(Types.VARCHAR).isNullable(true).setLength(255).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MatchesCurrentTeam")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.currentTeamMatches }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.currentTeamMatches = v as Int? })
                .setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LineupDisabled")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.isLineupDisabled() }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.setLineupDisabled(v as Boolean?) })
                .setType(
                    Types.BOOLEAN
                ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ContractDate")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as Player?)!!.contractDate }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as Player?)!!.contractDate = v as String? })
                .setType(Types.VARCHAR).isNullable(true).setLength(100).build()
        )
    }

    override val createIndexStatement: Array<String?>
        get() = arrayOf(
            "CREATE INDEX iSpieler_1 ON " + tableName + "(" + columns[1].columnName + "," + columns[2].columnName + ")",
            "CREATE INDEX iSpieler_2 ON " + tableName + "(" + columns[0].columnName + ")"
        )

    /**
     * Store a list of records
     * @param players list of players
     */
    fun store(players: List<Player?>?) {
        if (players != null) {
            for (p in players) {
                store(p)
            }
        }
    }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder {
        return PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=?")
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "WHERE HRF_ID=?")
    }

    /**
     * load players of a hrf (download)
     * @param hrfID id of hrf
     * @return list of pLayers
     */
    fun loadPlayers(hrfID: Int): List<Player?> {
        return load(Player::class.java, hrfID)
    }

    private val loadAllPlayersStatementBuilder = PreparedSelectStatementBuilder(
        this, " t inner join (" +
                "    select SPIELERID, max(DATUM) as MaxDate from " +
                tableName +
                "    group by SPIELERID" +
                ") tm on t.SPIELERID = tm.SPIELERID and t.DATUM = tm.MaxDate"
    )

    /**
     * load all players of database
     * @return List of latest records stored in database of all players.
     */
    fun loadAllPlayers(): List<Player?> {
        return load(
            Player::class.java,
            adapter.executePreparedQuery(loadAllPlayersStatementBuilder.getStatement()),
            -1
        )
    }

    private val getLetzteBewertung4SpielerStatementBuilder = PreparedStatementBuilder(
        "SELECT Bewertung from $tableName WHERE SpielerID=? AND Bewertung>0 ORDER BY Datum DESC  LIMIT 1"
    )

    /**
     * Get latest rating of player
     */
    fun getLatestRatingOfPlayer(playerId: Int): Int {
        var bewertung = 0
        try {
            val rs = adapter.executePreparedQuery(getLetzteBewertung4SpielerStatementBuilder.getStatement(), playerId)
            if (rs != null && rs.next()) {
                bewertung = rs.getInt("Bewertung")
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, "DatenbankZugriff.getLetzteBewertung4Spieler : $playerId : $e")
        }
        return bewertung
    }

    private val getSpielerNearDateBeforeStatementBuilder =
        PreparedSelectStatementBuilder(this, "WHERE Datum<=? AND Datum>=? AND SpielerID=? ORDER BY Datum DESC LIMIT 1")
    private val getSpielerNearDateAfterStatementBuilder =
        PreparedSelectStatementBuilder(this, "WHERE Datum>=? AND SpielerID=? ORDER BY Datum LIMIT 1")

    fun getSpielerNearDate(playerId: Int, time: Timestamp?): Player? {
        var player: Player?

        //6 Tage   //1209600000  //14 Tage vorher
        val spanne = 518_400_000
        if (time == null) {
            return null
        }

        //--- Zuerst x Tage vor dem Datum suchen -------------------------------
        //x Tage vorher
        val time2 = Timestamp(time.getTime() - spanne)
        player = loadOne(
            Player::class.java,
            adapter.executePreparedQuery(
                getSpielerNearDateBeforeStatementBuilder.getStatement(),
                time,
                time2,
                playerId
            )
        )

        //--- Dann ein HRF später versuchen, Dort muss er dann eigenlich vorhanden sein! ---
        if (player == null) {
            player = loadOne(
                Player::class.java,
                adapter.executePreparedQuery(getSpielerNearDateAfterStatementBuilder.getStatement(), time, playerId)
            )
        }

        //----Dann noch die dopplete Spanne vor der Spanne suchen---------------
        if (player == null) {
            //x Tage vorher
            val time3 = Timestamp(time2.getTime() - spanne * 2)
            player = loadOne(
                Player::class.java,
                adapter.executePreparedQuery(
                    getSpielerNearDateBeforeStatementBuilder.getStatement(),
                    time2,
                    time3,
                    playerId
                )
            )
        }
        return player
    }

    //------------------------------------------------------------------------------
    private val getSpielerFirstHRFStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE SpielerID=? AND Datum>? ORDER BY Datum ASC LIMIT 1")

    /**
     * load first player appearance
     */
    fun getSpielerFirstHRF(spielerid: Int, after: Timestamp?): Player? {
        val ret = loadOne(
            Player::class.java,
            adapter.executePreparedQuery(getSpielerFirstHRFStatementBuilder.getStatement(), spielerid, after)
        )
        if (ret != null) {
            ret.isGoner = true
        }
        return ret
    }

    private val getTrainerTypeStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE HRF_ID=? AND TrainerTyp >=0 AND Trainer >0 order by Trainer desc")

    init {
        idColumns = 2
    }

    fun getTrainerType(hrfID: Int): Int {
        val rs: ResultSet? = adapter.executePreparedQuery(getTrainerTypeStatementBuilder.getStatement(), hrfID)
        try {
            if (rs != null) {
                if (rs.next()) {
                    return rs.getInt("TrainerTyp")
                }
            }
        } catch (ignored: Exception) {
        }
        return -99
    }

    companion object {
        /** Table name  */
        const val TABLENAME = "SPIELER"
    }
}