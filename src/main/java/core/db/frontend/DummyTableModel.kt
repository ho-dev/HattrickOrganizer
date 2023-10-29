// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31.10.2011 08:11:05
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DummyTableModel.java
package core.db.frontend

import java.util.*
import javax.swing.table.DefaultTableModel

internal class DummyTableModel(daten: Array<Array<Any?>>?, headers: Array<String?>?) : DefaultTableModel(daten, headers) {
    override fun getColumnClass(col: Int): Class<*> {
        val vector = dataVector.elementAt(0) as Vector<*>
        return vector.elementAt(col).javaClass
    }

    override fun isCellEditable(row: Int, col: Int): Boolean {
        return false
    }
}