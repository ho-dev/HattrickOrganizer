package core.db

import core.db.DBManager.PreparedStatementBuilder
import core.model.player.*
import core.util.HODateTime
import core.util.HOLogger
import module.training.Skills.HTSkillID
import module.youth.YouthPlayer
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class YouthPlayerTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        val tmp: ArrayList<ColumnDescriptor> = ArrayList<ColumnDescriptor>(
            java.util.List.of<ColumnDescriptor>(
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("HRF_ID")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getHrfid() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setHrfid(v as Int) })
                ).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ID")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getId() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setId(v as Int) })
                ).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FirstName")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getFirstName() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setFirstName(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("NickName")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getNickName() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setNickName(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LastName")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getLastName() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setLastName(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(100).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Age")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getAgeYears() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setAgeYears(v as Int) })
                ).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("AgeDays")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getAgeDays() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setAgeDays(v as Int) })
                ).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ArrivalDate")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        HODateTime.toDbTimestamp(
                            (p as YouthPlayer?)!!.getArrivalDate()
                        )
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setArrivalDate(v as HODateTime?) })
                ).setType(
                    Types.TIMESTAMP
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PromotionDate")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        HODateTime.toDbTimestamp(
                            (p as YouthPlayer?)!!.getPromotionDate()
                        )
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setPromotionDate(v as HODateTime?) })
                ).setType(
                    Types.TIMESTAMP
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CanBePromotedIn")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getCanBePromotedIn() }))
                    .setSetter(
                        BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setCanBePromotedIn(v as Int) })
                    ).setType(
                    Types.INTEGER
                ).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerNumber")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getPlayerNumber() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setPlayerNumber(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(10).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Statement")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getStatement() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setStatement(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("OwnerNotes")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getOwnerNotes() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setOwnerNotes(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("PlayerCategoryID")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getPlayerCategoryID() }))
                    .setSetter(
                        BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setPlayerCategoryID(v as Int) })
                    ).setType(
                    Types.INTEGER
                ).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Cards")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getCards() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthPlayer?)!!.setCards(v as Int) })
                ).setType(Types.INTEGER).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("InjuryLevel")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getInjuryLevel() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setInjuryLevel(
                            (v as Int?)!!
                        )
                    })
                ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Specialty")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        Specialty.getValue(
                            (p as YouthPlayer?)!!.getSpecialty()
                        )
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setSpecialty(
                            Specialty.getSpecialty(
                                v as Int?
                            )
                        )
                    })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CareerGoals")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getCareerGoals() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setCareerGoals(
                            (v as Int?)!!
                        )
                    })
                ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("CareerHattricks")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getCareerHattricks() }))
                    .setSetter(
                        BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                            (p as YouthPlayer?)!!.setCareerHattricks(
                                (v as Int?)!!
                            )
                        })
                    ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("LeagueGoals")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getLeagueGoals() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setLeagueGoals(
                            (v as Int?)!!
                        )
                    })
                ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("FriendlyGoals")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getFriendlyGoals() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setFriendlyGoals(
                            (v as Int?)!!
                        )
                    })
                ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ScoutId")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getScoutId() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setScoutId((v as Int?)!!) })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ScoutingRegionID")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getScoutingRegionID() }))
                    .setSetter(
                        BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                            (p as YouthPlayer?)!!.setScoutingRegionID(
                                (v as Int?)!!
                            )
                        })
                    ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("ScoutName")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getScoutName() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setScoutName(v as String?) })
                ).setType(
                    Types.VARCHAR
                ).setLength(255).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YouthMatchID")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getYouthMatchID() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setYouthMatchID(v as Int?) })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("positionCode")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getPositionCode() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setPositionCode(v as Int?) })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("playedMinutes")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getPlayedMinutes() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setPlayedMinutes(
                            (v as Int?)!!
                        )
                    })
                ).setType(Types.INTEGER).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("rating")
                    .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthPlayer?)!!.getRating() })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setRating(v as Double?) })
                ).setType(Types.DOUBLE).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YouthMatchDate")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        HODateTime.toDbTimestamp(
                            (p as YouthPlayer?)!!.getYouthMatchDate()
                        )
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setYouthMatchDate(v as HODateTime?) })
                ).setType(
                    Types.TIMESTAMP
                ).isNullable(true).build()
            )
        )
        for (skillId: HTSkillID in YouthPlayer.skillIds) {
            tmp.addAll(createColumnDescriptors(skillId))
        }
        columns = tmp.toTypedArray<ColumnDescriptor>()
    }

    private fun createColumnDescriptors(skillId: HTSkillID): Collection<ColumnDescriptor> {
        val prefix: String = skillId.toString()
        return ArrayList<ColumnDescriptor>(
            java.util.List.of<ColumnDescriptor>(
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix)
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).getCurrentLevel()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setCurrentLevel(
                            skillId,
                            v as Int?
                        )
                    })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "Max")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).getMax()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthPlayer?)!!.setMax(skillId, v as Int?) })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "Start")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).getStartLevel()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setStartLevel(
                            skillId,
                            v as Int?
                        )
                    })
                ).setType(
                    Types.INTEGER
                ).isNullable(true).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "IsMaxReached")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).isMaxReached()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any ->
                        (p as YouthPlayer?)!!.setIsMaxReached(
                            skillId,
                            v as Boolean
                        )
                    })
                ).setType(
                    Types.BOOLEAN
                ).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "Value")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).getCurrentValue()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any ->
                        (p as YouthPlayer?)!!.setCurrentValue(
                            skillId,
                            v as Double
                        )
                    })
                ).setType(
                    Types.DOUBLE
                ).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "StartValue")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).getStartValue()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any ->
                        (p as YouthPlayer?)!!.setStartValue(
                            skillId,
                            v as Double
                        )
                    })
                ).setType(
                    Types.DOUBLE
                ).isNullable(false).build(),
                ColumnDescriptor.Builder.Companion.newInstance().setColumnName(prefix + "Top3")
                    .setGetter(Function<Any?, Any?>({ p: Any? ->
                        (p as YouthPlayer?)!!.getSkillInfo(skillId).isTop3()
                    })).setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthPlayer?)!!.setIsTop3(
                            skillId,
                            v as Boolean?
                        )
                    })
                ).setType(
                    Types.BOOLEAN
                ).isNullable(true).build()
            )
        )
    }

    override val constraintStatements: Array<String?>
        get() {
            return arrayOf(" PRIMARY KEY (HRF_ID, ID)")
        }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder {
        return PreparedDeleteStatementBuilder(this, "WHERE HRF_ID=?")
    }

    /**
     * delete youth players
     */
    fun deleteYouthPlayers(hrfId: Int) {
        executePreparedDelete(hrfId)
    }

    /**
     * store youth player
     * @param hrfId int
     * @param player YouthPlayer
     */
    fun storeYouthPlayer(hrfId: Int, player: YouthPlayer) {
        player.setHrfid(hrfId)
        store(player)
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "WHERE HRF_ID=?")
    }

    /**
     * load youth player of HRF file id
     */
    fun loadYouthPlayers(hrfID: Int): List<YouthPlayer?> = load(YouthPlayer::class.java, hrfID)

    private val loadYouthPlayerOfMatchDateStatementBuilder: PreparedSelectStatementBuilder =
        PreparedSelectStatementBuilder(this, " WHERE ID=? AND YOUTHMATCHDATE=?")

    fun loadYouthPlayerOfMatchDate(id: Int, date: Timestamp?): YouthPlayer? {
        return loadOne(
            YouthPlayer::class.java,
            adapter.executePreparedQuery(loadYouthPlayerOfMatchDateStatementBuilder.getStatement(), id, date)
        )
    }

    private val loadMinScoutingDateStatementBuilder: PreparedStatementBuilder = PreparedStatementBuilder(
        "select min(ArrivalDate) from $tableName where PromotionDate is NULL"
    )

    init {
        idColumns = 2
    }

    fun loadMinScoutingDate(): Timestamp? {
        try {
            val rs: ResultSet? = adapter.executePreparedQuery(loadMinScoutingDateStatementBuilder.getStatement())
            if (rs != null) {
                if (rs.next()) {
                    return rs.getTimestamp(1)
                }
            }
        } catch (e: Exception) {
            HOLogger.instance().log(javaClass, e)
        }
        return null
    }

    companion object {
        /**
         * tablename
         */
        val TABLENAME: String = "YOUTHPLAYER"
    }
}
