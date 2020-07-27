package core.db;

import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtil {

    public static @Nullable Integer getNullableInt(ResultSet rs, String cl) throws SQLException {
        Integer res = rs.getInt(cl);
        if (!rs.wasNull()) return  res;
        return null;
    }

    public static @Nullable Boolean getNullableBoolean(ResultSet rs, String cl) throws SQLException {
        Boolean res = rs.getBoolean(cl);
        if (!rs.wasNull()) return  res;
        return null;
    }

}
