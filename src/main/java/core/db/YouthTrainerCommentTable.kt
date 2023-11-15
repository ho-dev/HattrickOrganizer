package core.db

import core.model.player.CommentType
import module.training.Skills
import module.youth.YouthTrainerComment
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class YouthTrainerCommentTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YOUTHPLAYER_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getYouthPlayerId() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthTrainerComment?)!!.setYouthPlayerId(v as Int) })
                ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MATCH_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getYouthMatchId() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthTrainerComment?)!!.setMatchId(v as Int) })
                ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("INDEX")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getIndex() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as YouthTrainerComment?)!!.setIndex(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Text")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getText() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthTrainerComment?)!!.setText(v as String?) })
            ).setType(
                Types.VARCHAR
            ).setLength(255).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Type")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getType().value }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthTrainerComment?)!!.setType(
                            CommentType.valueOf(
                                v as Int?
                            )
                        )
                    })
                ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Variation")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getVariation() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthTrainerComment?)!!.setVariation(v as Int?) })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SkillType")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getSkillType().getValue() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                        (p as YouthTrainerComment?)!!.setSkillType(
                            Skills.ScoutCommentSkillTypeID.valueOf(
                                v as Int?
                            )
                        )
                    })
                ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SkillLevel")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as YouthTrainerComment?)!!.getSkillLevel() }))
                .setSetter(
                    BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as YouthTrainerComment?)!!.setSkillLevel(v as Int?) })
                ).setType(
                Types.INTEGER
            ).isNullable(true).build()
        )
    }

    fun loadYouthTrainerComments(id: Int): List<YouthTrainerComment?> {
        return load(YouthTrainerComment::class.java, id)
    }

    companion object {
        /**
         * tablename
         */
        val TABLENAME: String = "YOUTHTRAINERCOMMENT"
    }
}
