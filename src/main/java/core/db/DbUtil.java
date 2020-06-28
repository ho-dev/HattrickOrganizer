package core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtil {

    static public Integer getNullableInt(ResultSet rs, String cl) throws SQLException {
        Integer res = rs.getInt(cl);
        if (!rs.wasNull()) return  res;
        return null;
    }

}
