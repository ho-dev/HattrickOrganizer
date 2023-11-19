package core.db

import core.gui.comp.table.HOTableModel
import core.gui.model.UserColumnFactory
import core.util.HOLogger
import java.sql.*
import java.util.function.BiConsumer
import java.util.function.Function

internal class UserColumnsTable(adapter: JDBCAdapter) : AbstractTable(TABLENAME, adapter) {
    override fun initColumns() {
        columns = arrayOf<ColumnDescriptor>(
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COLUMN_ID")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _UserColumn?)!!.id }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as _UserColumn?)!!.id = (v as Int) }).setType(Types.INTEGER)
                .isPrimaryKey(true).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("MODELL_INDEX")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _UserColumn?)!!.modelIndex }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as _UserColumn?)!!.modelIndex = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("TABLE_INDEX")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _UserColumn?)!!.index }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any -> (p as _UserColumn?)!!.index = (v as Int) })
                .setType(Types.INTEGER).isNullable(false).build(),
            ColumnDescriptor.Builder.Companion.newInstance().setColumnName("COLUMN_WIDTH")
                .setGetter(Function<Any?, Any?> { p: Any? -> (p as _UserColumn?)!!.preferredWidth }).setSetter(
                BiConsumer<Any?, Any> { p: Any?, v: Any? -> (p as _UserColumn?)!!.preferredWidth = (v as Int?) })
                .setType(Types.INTEGER).isNullable(true).build()
        )
    }

    override fun createPreparedDeleteStatementBuilder(): PreparedDeleteStatementBuilder {
        return PreparedDeleteStatementBuilder(this, "WHERE COLUMN_ID BETWEEN ? AND ?")
    }

    override fun createPreparedSelectStatementBuilder(): PreparedSelectStatementBuilder {
        return PreparedSelectStatementBuilder(this, "WHERE COLUMN_ID BETWEEN ? AND ?")
    }

    fun deleteModel(modelId: Int) {
        executePreparedDelete(modelId * 1000, modelId * 1000 + 999)
    }

    fun saveModel(model: HOTableModel) {
        deleteModel(model.id.value)
        val dbcolumns = model.columns
        if (dbcolumns != null) {
            for (i in dbcolumns.indices) {
                if (model.id.value == 2 && dbcolumns[i].id == UserColumnFactory.ID) {
                    dbcolumns[i].setDisplay(true) // force ID column
                }
                if (dbcolumns[i].isDisplay()) {
                    val _userColumn = _UserColumn()
                    _userColumn.modelIndex = i
                    _userColumn.id = (model.id.value * 1000 + dbcolumns[i].id)
                    _userColumn.preferredWidth = (dbcolumns[i].preferredWidth)
                    _userColumn.index = (dbcolumns[i].index)
                    store(_userColumn)
                }
            }
        }
    }

    fun insertDefault(model: HOTableModel) {
        val dbColumns = model.columns
        if (dbColumns != null) {
            for (i in dbColumns.indices) {
                dbColumns[i].index = i

                // By default make all columns visible, except ID.
                if (dbColumns[i].id != UserColumnFactory.ID) {
                    dbColumns[i].setDisplay(true)
                }
            }
        }
    }

    fun loadModel(model: HOTableModel) {
        var count = 0
        val userColumns = load(_UserColumn::class.java, model.id.value * 1000, model.id.value * 1000 + 999)
        if (userColumns.isNotEmpty()) { // user may not delete all columns
            val modelColumns = model.columns
            if (modelColumns != null) {
                if (model.userCanDisableColumns() && !DBManager.firstStart) {
                    for (modelColumn in modelColumns) {
                        modelColumn.setDisplay(!modelColumn.isEditable)
                    }
                }
                for (userColumn in userColumns) {
                    val modelIndex: Int = userColumn.modelIndex
                    if (modelIndex < modelColumns.size) {
                        val modelColumn = modelColumns[modelIndex]
                        modelColumn.preferredWidth = userColumn.preferredWidth!!
                        modelColumn.setDisplay(true)
                        modelColumn.index = userColumn.index
                        count++
                    }
                }
            } else {
                HOLogger.instance().error(javaClass, "No column found when loading model.")
            }
        }
        if (count == 0) {
            insertDefault(model)
        }
    }

    /**
     * kind of a clone of abstract class UserColumn used to load and store user column information
     */
    private class _UserColumn : Storable() {
        var id = 0
        /**
         * return index of the user column in the model's array definition
         * @return int
         */
        /**
         * set the index of the user column in the model's array definition
         * @param modelIndex int
         */
        var modelIndex = 0

        /**
         * set index
         * if columnModel should be saved index will set, or column is loaded
         * @param index int
         */
        var index = 0
        var preferredWidth: Int? = null
    }

    companion object {
        const val TABLENAME = "USERCOLUMNS"
    }
}