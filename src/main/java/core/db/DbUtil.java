package core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtil {

    public static Integer getNullableInt(ResultSet rs, String cl) throws SQLException {
        Integer res = rs.getInt(cl);
        if (!rs.wasNull()) return  res;
        return null;
    }

    public static Boolean getNullableBoolean(ResultSet rs, String cl) throws SQLException {
        Boolean res = rs.getBoolean(cl);
        if (!rs.wasNull()) return  res;
        return null;
    }

}
