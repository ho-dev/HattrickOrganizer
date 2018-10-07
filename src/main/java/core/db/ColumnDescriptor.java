package core.db;

/**
 * 
 * @author Thorsten Dietz
 *
 */
public final class ColumnDescriptor {

	private String columnName;
	private int type;
	private int length;
	private boolean nullable;
	private boolean primaryKey;
	
	
	public ColumnDescriptor(String columnName,int type,boolean nullable){
		this(columnName,type,nullable,0);
	}
	
	public ColumnDescriptor(String columnName,int type,boolean nullable,int length){
		this(columnName,type,nullable,false,length);
	}
	
	public ColumnDescriptor(String columnName,int type,boolean nullable, boolean primaryKey){
		this(columnName,type,nullable,primaryKey,0);
	}
	public ColumnDescriptor(String columnName,int type,boolean nullable, boolean primaryKey, int length){
		this.columnName = columnName;
		this.type 		= type;
		this.nullable 	= nullable;
		this.primaryKey = primaryKey;
		this.length		= length;
	}
	public final String getColumnName() {
		return columnName;
	}

	public final boolean isNullable() {
		return nullable;
	}

	public final boolean isPrimaryKey() {
		return primaryKey;
	}

	public final int getType() {
		return type;
	}
	
	protected final String getCreateString(DBInfo dbInfo){
		StringBuffer sql = new StringBuffer(50);
		sql.append(" ");
		sql.append(getColumnName());
		sql.append(" ");
		sql.append(dbInfo.getTypeName(getType()));
		if(length>0){
			sql.append("(");
			sql.append(length);
			sql.append(")");
		}
		if(! nullable)
			sql.append(" NOT NULL");
		if(primaryKey)
			sql.append(" PRIMARY KEY");
		return sql.toString();
	}
	
	
}
