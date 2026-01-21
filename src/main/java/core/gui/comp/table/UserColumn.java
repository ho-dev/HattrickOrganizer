package core.gui.comp.table;

import core.model.TranslationFacility;

import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * User configuration of table columns
 */
public abstract class UserColumn {

	/** unique column id **/
	protected int id;
	
	/** columnName properties representation, not display!! **/
	protected String columnName;
	
	/** tooltip properties representation **/
	protected String tooltip;
	
	/** minimum width of the column **/
	protected int minWidth;
	
	/** preferred width of the column **/
	protected int preferredWidth;
	
	/** index of the column in the table **/
	protected int index = 0;
	
	/** if a column is shown in the table. Only displayed columns are saved in db. **/
	protected boolean display = false;

    protected boolean translateColumnName = true;
    protected boolean translateColumnTooltip = true;

	/**
	 * Sort order of the column
	 */
	SortOrder sortOrder;

	/**
	 * Sort priority
	 * Defines the order of the sort keys if more than one column are sorted
	 */
	Integer sortPriority;

    /**
	 * Constructor of an user column
	 * @param id  column identifier has to be unique in one table
	 * @param name Column name is displayed in the column header
	 * @param tooltip Column header tool tip
	 */
	protected UserColumn(int id,String name, String tooltip){
		this.id = id;
		columnName = name;
		this.tooltip = tooltip;
	}

	/**
	 * Constructor of an user column
	 * @param id  column identifier has to be unique in one table
	 * @param name Column name and tool tip
	 */
	protected UserColumn(int id,String name){
		this(id,name,name);
	}

	/**
	 * constructor is used by AbstractTable
	 */
	public UserColumn(){}
	
	/**
	 * returns the language dependency name of the column
	 * @return String
	 */
	public final String getColumnName() {
        return translateColumnName ? TranslationFacility.tr(columnName) : columnName;
    }
	
	/**
	 * Return  id
	 * @return int
	 */
	public final int getId() {
		return id;
	}

	/**
	 * returns the language dependency tooltip of the column
	 * @return String
	 */
	public final String getTooltip() {
		return (this.translateColumnTooltip)?TranslationFacility.tr(tooltip):tooltip;
	}
	
	/**
	 * Should a column be shown
	 * @return boolean
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * set a column to be showed
	 * @param display boolean
	 */
	public final void setDisplay(boolean display) {
		this.display = display;
		if (!display) {
			index = 0;
			sortPriority = null;
			sortOrder = null;
		}
	}

	/**
	 * return the current index of column
	 * only actual if user don´t move the column !!
	 * @return int
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * set index
	 * if columnModel should be saved index will set, or column is loaded
	 * @param index int
	 */
	public final void setIndex(int index) {
		this.index = index;
	}

	/**
	 * String representation
	 * use in UserColumnsPanel in OptionsPanel
	 */
	@Override
	public String toString(){
		return getTooltip();
	}

	/**
	 * Some columns must be displayed, so some columns are not editable in options dialog
	 * @return boolean
	 */
	public boolean canBeDisabled(){
		return true;
	}

	/**
	 * Column is not visible (width is reduced to zero).
	 * @return boolean
	 */
	public boolean isHidden(){return false;}

	/**
	 * set minWidth and prefWidth in the TableColumn
	 * @param column TableColumn
	 */
	public void setSize(TableColumn column){
		column.setMinWidth(minWidth);
		column.setPreferredWidth(preferredWidth);
	}
	
	/**
	 * set preferredWidth for saving to DB
	 * @param width int
	 */
	public void setPreferredWidth(int width){
		preferredWidth = width;
	}
	
	public int getPreferredWidth(){
		return preferredWidth;
	}

	public Integer getSortPriority() {
		return sortPriority;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortPriority(Integer sortPriority) {
		this.sortPriority = sortPriority;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

    public boolean isEditable() {return false;}
}
