package core.db

import core.model.player.CommentType
import module.training.Skills
import module.youth.YouthPlayer.ScoutComment
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

class YouthScoutCommentTable internal constructor(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("YOUTHPLAYER_ID")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getYouthPlayerId() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as ScoutComment?)!!.setYouthPlayerId(v as Int) })
            ).setType(
                Types.INTEGER
            ).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("INDEX")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getIndex() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any -> (p as ScoutComment?)!!.setIndex(v as Int) })
            ).setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Text")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getText() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as ScoutComment?)!!.setText(v as String?) })
            ).setType(Types.VARCHAR).setLength(255).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Type")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getType().value })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as ScoutComment?)!!.setType(CommentType.valueOf(v as Int?)) })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("Variation")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getVariation() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as ScoutComment?)!!.setVariation(v as Int?) })
            ).setType(Types.INTEGER).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SkillType")
                .setGetter(Function<Any?, Any?>({ p: Any? ->
                    Skills.ScoutCommentSkillTypeID.value(
                        (p as ScoutComment?)!!.getSkillType()
                    )
                })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? ->
                    (p as ScoutComment?)!!.setSkillType(
                        Skills.ScoutCommentSkillTypeID.valueOf(
                            v as Int?
                        )
                    )
                })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("SkillLevel")
                .setGetter(Function<Any?, Any?>({ p: Any? -> (p as ScoutComment?)!!.getSkillLevel() })).setSetter(
                BiConsumer<Any?, Any>({ p: Any?, v: Any? -> (p as ScoutComment?)!!.setSkillLevel(v as Int?) })
            ).setType(
                Types.INTEGER
            ).isNullable(true).build()
        )
    }

    fun storeYouthScoutComments(youthplayerId: Int, comments: List<ScoutComment>) {
        executePreparedDelete(youthplayerId)
        for (comment: ScoutComment in comments) {
            comment.stored = false
            comment.setYouthPlayerId(youthplayerId)
            store(comment)
        }
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "WHERE YOUTHPLAYER_ID=? order by INDEX")
    }

    fun loadYouthScoutComments(youthplayer_id: Int): List<ScoutComment?> {
        return load(ScoutComment::class.java, youthplayer_id)
    }

    companion object {
        /** tablename  */
        val TABLENAME: String = "YOUTHSCOUTCOMMENT"
    }
}
