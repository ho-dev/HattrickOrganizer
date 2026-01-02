package core.db;

import core.util.HOLogger;

import java.sql.Types;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Thorsten Dietz
 */
public final class ColumnDescriptor {

    private final String columnName;
    private final int type;

    private final int length;
    private final boolean nullable;
    private final boolean primaryKey;

    public Function<Object, Object> getter;
    public BiConsumer<Object, Object> setter;

    public ColumnDescriptor(Builder builder) {
        this.columnName = builder.columnName;
        this.type = builder.type;
        this.length = builder.length;
        this.nullable = builder.nullable;
        this.primaryKey = builder.primaryKey;
        this.getter = builder.getter;
        this.setter = builder.setter;
    }

    /**
     * Get value of the column from the object
     * The defined getter method is applied to get the value.
     * If it is a string value and its length exceeds the defined column length the string is shortened
     * and a warning message is logged.
     * @param object T The object from which the value is fetched
     * @return Object value
     * @param <T> The storable object type
     */
    public <T extends AbstractTable.Storable> Object getLimitedValue(T object) {
        var val = this.getter.apply(object);
        if ( this.type == Types.VARCHAR && val instanceof String stringValue){
            if ( stringValue.length()>  this.length){
                HOLogger.instance().warning(this.getClass(), "Shorten value of column " + this.columnName + " to column length: " + this.length + ". Complete string is: " + stringValue);
                return stringValue.substring(0,this.length);
            }
        }
        return val;
    }

    public static class Builder {
        private String columnName;
        private int type = Types.VARCHAR;
        private int length;
        private boolean nullable = true;
        private boolean primaryKey = false;

        private Function<Object, Object> getter;
        private BiConsumer<Object, Object> setter;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder setType(int type) {
            this.type = type;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Builder isNullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public Builder isPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public Builder setGetter(Function<Object, Object> getter) {
            this.getter = getter;
            return this;
        }

        public Builder setSetter(BiConsumer<Object, Object> setter) {
            this.setter = setter;
            return this;
        }

        public ColumnDescriptor build() {
            return new ColumnDescriptor(this);
        }
    }

    public ColumnDescriptor(String columnName, int type, boolean nullable) {
        this(columnName, type, nullable, 0);
    }

    public ColumnDescriptor(String columnName, int type, boolean nullable, int length) {
        this(columnName, type, nullable, false, length);
    }

    public ColumnDescriptor(String columnName, int type, boolean nullable, boolean primaryKey) {
        this(columnName, type, nullable, primaryKey, 0);
    }

    public ColumnDescriptor(String columnName, int type, boolean nullable, boolean primaryKey, int length) {
        this.columnName = columnName;
        this.type = type;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
        this.length = length;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public int getType() {
        return type;
    }
    public int getLength() {
        return length;
    }

    String getCreateString(DBInfo dbInfo) {
        StringBuilder sql = new StringBuilder(50);
        sql.append(" ");
        sql.append(getColumnName());
        sql.append(" ");
        sql.append(dbInfo.getTypeName(getType()));

        if (length > 0) {
            sql.append("(");
            sql.append(length);
            sql.append(")");
        }
        if (!nullable)
            sql.append(" NOT NULL");

        if (primaryKey)
            sql.append(" PRIMARY KEY");
        return sql.toString();
    }

}
