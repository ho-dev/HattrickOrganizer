package core.gui.comp.entry

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.awt.Color

internal class CheckBoxTableEntryTest {
    @Test
    fun testCheckBoxEntryCompareTo() {

        val checkBoxTableEntry = CheckBoxTableEntry(true, true, Color.RED, Color.BLUE)
        val checkBoxTableEntryTrue = CheckBoxTableEntry(true, true, Color.RED, Color.BLUE)
        val checkBoxTableEntryFalse = CheckBoxTableEntry(true, false, Color.BLUE, Color.RED)
        val checkBoxTableEntryNull = CheckBoxTableEntry(true, null, Color.RED, Color.BLUE)

        Assertions.assertEquals(0, checkBoxTableEntryTrue.compareTo(checkBoxTableEntry))
        Assertions.assertEquals(1, checkBoxTableEntryTrue.compareTo(checkBoxTableEntryFalse))
        Assertions.assertEquals(1, checkBoxTableEntryTrue.compareTo(checkBoxTableEntryNull))
        Assertions.assertEquals(1, checkBoxTableEntryFalse.compareTo(checkBoxTableEntryNull))
        Assertions.assertEquals(-1, checkBoxTableEntryNull.compareTo(checkBoxTableEntryFalse))
        Assertions.assertEquals(-1, checkBoxTableEntryNull.compareTo(checkBoxTableEntry))
    }
}
