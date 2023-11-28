package core.db

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.*

class ColumnDescriptorTest {

    private fun createDbInfo(): DBInfo {
        return DBInfo(null)
    }

    @Test
    fun testBuilderSetsValuesCorrectly() {
        class LocalStorer(var v:Int)
        val storer = LocalStorer(12)

        val descriptor = ColumnDescriptor.Builder.newInstance()
            .setColumnName("TEST")
            .setType(Types.VARCHAR)
            .setLength(42)
            .isNullable(true)
            .isPrimaryKey(true)
            .setGetter { o -> (o as LocalStorer).v }
            .setSetter { o, v -> (o as LocalStorer).v = (v as Int) }
            .build()

        Assertions.assertEquals("TEST", descriptor.columnName)
        Assertions.assertEquals(Types.VARCHAR, descriptor.type)
        Assertions.assertEquals(true, descriptor.isNullable)
        Assertions.assertEquals(true, descriptor.isPrimaryKey)
        Assertions.assertEquals(12, descriptor.getter.apply(storer))

        // Invoke setter
        descriptor.setter.accept(storer, 66)
        Assertions.assertEquals(66, storer.v)

        // Check length
        Assertions.assertEquals(" TEST VARCHAR(42) PRIMARY KEY",
            descriptor.getCreateString(createDbInfo()))
    }

    @Test
    fun testColumnDescriptorNameTypeAndNullable() {
        val col = arrayOf(
            arrayOf<Any>("NAME", Types.BOOLEAN, true, " NAME BOOLEAN"),
            arrayOf<Any>("NAME", Types.BOOLEAN, false, " NAME BOOLEAN NOT NULL")
        )

        col.forEach {
            val columnDescriptor = ColumnDescriptor(it[0] as String, it[1] as Int, it[2] as Boolean)
            Assertions.assertEquals(it[3] as String, columnDescriptor.getCreateString(createDbInfo()))
        }
    }

    @Test
    fun testColumnDescriptorNameTypeNullableAndPrimaryKey() {
        val col = arrayOf(
            arrayOf<Any>("NAME", Types.BOOLEAN, false, true, " NAME BOOLEAN NOT NULL PRIMARY KEY"),
            arrayOf<Any>("NAME", Types.BOOLEAN, false, false, " NAME BOOLEAN NOT NULL"),
            arrayOf<Any>("NAME", Types.BOOLEAN, true, true, " NAME BOOLEAN PRIMARY KEY"),
            arrayOf<Any>("NAME", Types.BOOLEAN, true, false, " NAME BOOLEAN")
        )

        col.forEach {
            val columnDescriptor = ColumnDescriptor(it[0] as String, it[1] as Int, it[2] as Boolean, it[3] as Boolean)
            Assertions.assertEquals(it[4] as String, columnDescriptor.getCreateString(createDbInfo()))
        }
    }
}
