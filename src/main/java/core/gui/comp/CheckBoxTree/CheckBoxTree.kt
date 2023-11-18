package core.gui.comp.CheckBoxTree

import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.*
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.event.EventListenerList
import javax.swing.tree.*

// code is from https://stackoverflow.com/a/21851201/289466 by user SomethingSomething on Stack Overflow.
// public methods checkNode and isChecked are added
class CheckBoxTree : JTree() {
    // Defining data structure that will enable to fast check-indicate the state of each node
    // It totally replaces the "selection" mechanism of the JTree
    class CheckedNode(var isSelected: Boolean, var hasChildren: Boolean, var allChildrenSelected: Boolean)

    var nodesCheckingState: MutableMap<TreePath, CheckedNode>? = null
    private var checkedPaths: MutableSet<TreePath> = HashSet()

    // Defining a new event type for the checking mechanism and preparing event-handling mechanism
    private var listenerList = EventListenerList()

    class CheckChangeEvent(source: Any?) : EventObject(source)
    interface CheckChangeEventListener : EventListener {
        fun checkStateChanged(event: CheckChangeEvent?)
    }

    fun addCheckChangeEventListener(listener: CheckChangeEventListener) {
        listenerList.add(CheckChangeEventListener::class.java, listener)
    }

    fun fireCheckChangeEvent(evt: CheckChangeEvent?) {
        val listeners = listenerList.listenerList
        for (i in listeners.indices) {
            if (listeners[i] === CheckChangeEventListener::class.java) {
                (listeners[i + 1] as CheckChangeEventListener).checkStateChanged(evt)
            }
        }
    }

    override fun setModel(newModel: TreeModel) {
        super.setModel(newModel)
        resetCheckingState()
    }

    // New method that returns only the checked paths (totally ignores original "selection" mechanism)
    fun getCheckedPaths(): Array<TreePath> {
        return checkedPaths.toTypedArray<TreePath>()
    }

    // Returns true in case that the node is selected, has children but not all of them are selected
    fun isSelectedPartially(path: TreePath): Boolean {
        val cn = nodesCheckingState!![path]
        return cn!!.isSelected && cn.hasChildren && !cn.allChildrenSelected
    }

    private fun resetCheckingState() {
        nodesCheckingState = HashMap()
        checkedPaths = HashSet()
        val node = model.root as DefaultMutableTreeNode
        addSubtreeToCheckingStateTracking(node)
    }

    // Creating data structure of the current model for the checking mechanism
    private fun addSubtreeToCheckingStateTracking(node: DefaultMutableTreeNode) {
        val path = node.path
        val tp = TreePath(path)
        val cn = CheckedNode(false, node.childCount > 0, false)
        nodesCheckingState!![tp] = cn
        for (i in 0 until node.childCount) {
            addSubtreeToCheckingStateTracking(tp.pathByAddingChild(node.getChildAt(i)).lastPathComponent as DefaultMutableTreeNode)
        }
    }

    // Overriding cell renderer by a class that ignores the original "selection" mechanism
    // It decides how to show the nodes due to the checking-mechanism
    private inner class CheckBoxCellRenderer : JPanel(), TreeCellRenderer {
        var checkBox: JCheckBox

        init {
            this.setLayout(BorderLayout())
            checkBox = JCheckBox()
            add(checkBox, BorderLayout.CENTER)
            setOpaque(false)
        }

        override fun getTreeCellRendererComponent(
            tree: JTree, value: Any,
            selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int,
            hasFocus: Boolean
        ): Component {
            val node = value as DefaultMutableTreeNode
            val obj = node.userObject
            val tp = TreePath(node.path)
            val cn = nodesCheckingState!![tp] ?: return this
            checkBox.setSelected(cn.isSelected)
            checkBox.setText(obj.toString())
            checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected)
            return this
        }
    }

    init {
        // Disabling toggling by double-click
        setToggleClickCount(0)
        // Overriding cell renderer by new one defined above
        val cellRenderer = CheckBoxCellRenderer()
        setCellRenderer(cellRenderer)

        // Overriding selection model by an empty one
        val dtsm: DefaultTreeSelectionModel = object : DefaultTreeSelectionModel() {
            // Totally disabling the selection mechanism
            override fun setSelectionPath(path: TreePath) {}
            override fun addSelectionPath(path: TreePath) {}
            override fun removeSelectionPath(path: TreePath) {}
            override fun setSelectionPaths(pPaths: Array<TreePath>) {}
        }
        // Calling checking mechanism on mouse click
        addMouseListener(object : MouseListener {
            override fun mouseClicked(arg0: MouseEvent) {
                val tp = getPathForLocation(arg0.x, arg0.y) ?: return
                val checkMode = !nodesCheckingState!![tp]!!.isSelected
                checkSubTree(tp, checkMode)
                updatePredecessorsWithCheckMode(tp, checkMode)
                // Firing the check change event
                fireCheckChangeEvent(CheckChangeEvent(Any()))
                // Repainting tree after the data structures were updated
                this@CheckBoxTree.repaint()
            }

            override fun mouseEntered(arg0: MouseEvent) {}
            override fun mouseExited(arg0: MouseEvent) {}
            override fun mousePressed(arg0: MouseEvent) {}
            override fun mouseReleased(arg0: MouseEvent) {}
        })
        setSelectionModel(dtsm)
    }

    // When a node is checked/unchecked, updating the states of the predecessors
    private fun updatePredecessorsWithCheckMode(tp: TreePath, check: Boolean) {
        val parentPath = tp.parentPath ?: return
        // If it is the root, stop the recursive calls and return
        val parentCheckedNode = nodesCheckingState!![parentPath]
        val parentNode = parentPath.lastPathComponent as DefaultMutableTreeNode
        parentCheckedNode!!.allChildrenSelected = true
        parentCheckedNode.isSelected = false
        for (i in 0 until parentNode.childCount) {
            val childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i))
            val childCheckedNode = nodesCheckingState!![childPath]
            // It is enough that even one subtree is not fully selected
            // to determine that the parent is not fully selected
            if (!childCheckedNode!!.allChildrenSelected) {
                parentCheckedNode.allChildrenSelected = false
            }
            // If at least one child is selected, selecting also the parent
            if (childCheckedNode.isSelected) {
                parentCheckedNode.isSelected = true
            }
        }
        if (parentCheckedNode.isSelected) {
            checkedPaths.add(parentPath)
        } else {
            checkedPaths.remove(parentPath)
        }
        // Go to upper predecessor
        updatePredecessorsWithCheckMode(parentPath, check)
    }

    // Recursively checks/unchecks a subtree
    private fun checkSubTree(tp: TreePath, check: Boolean) {
        val cn = nodesCheckingState!![tp]
        cn!!.isSelected = check
        val node = tp.lastPathComponent as DefaultMutableTreeNode
        for (i in 0 until node.childCount) {
            checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check)
        }
        cn.allChildrenSelected = check
        if (check) {
            checkedPaths.add(tp)
        } else {
            checkedPaths.remove(tp)
        }
    }

    fun checkNode(node: DefaultMutableTreeNode, check: Boolean) {
        val tp = TreePath(node.path)
        checkSubTree(tp, check)
        updatePredecessorsWithCheckMode(tp, check)
    }

    fun isChecked(node: DefaultMutableTreeNode): Boolean {
        val tp = TreePath(node.path)
        val cn = nodesCheckingState!![tp]
        return cn!!.isSelected
    }
}
