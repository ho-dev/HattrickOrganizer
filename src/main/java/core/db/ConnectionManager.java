package core.db;

import core.util.ExceptionUtils;
import core.util.HOLogger;

import java.sql.*;

/**
 * Provides the connection functions to the database
 */
public class ConnectionManager {
	Connection connection;
	private Statement statement;
	private DBInfo dbInfo;

	private StatementCache statementCache;

	/**
	 * Closes the connection
	 */
	public final void disconnect() {
		try {
			var statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statement.execute("SHUTDOWN");
			connection.close();
			connection = null;
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "JDBCAdapter.disconnect : " + e);
			connection = null;
		}
	}

	/**
	 * Execute a SQL Select statement
	 * 
	 * @param sqlStatement
	 *            Sql query with placeholders
	 * 
	 * @return ResultSet of the query
	 */
	public final ResultSet executeQuery(String sqlStatement) {
		try {
			if (connection.isClosed()) {
				return null;
			}
			return statement.executeQuery(sqlStatement);
		} catch (Exception e) {
			HOLogger.instance().error(
					getClass(),
					"executeQuery : " + e + "\nStatement: " + sqlStatement + "\n"
							+ ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public final PreparedStatement createPreparedStatement(String sql) {
		return statementCache.getPreparedStatement(sql);
	}

	public final ResultSet executePreparedQuery(String query, Object... params) {
		return executePreparedQuery(statementCache.getPreparedStatement(query), params);
    }

	private ResultSet executePreparedQuery(PreparedStatement preparedStatement, Object... params) {
		try {
			if (connection.isClosed()) {
				return null;
			}
			int i = 0;
			for (var p : params) {
				preparedStatement.setObject(++i, p);
			}
			return preparedStatement.executeQuery();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "executePreparedQuery : " + e
					+ "\nStatement: " + preparedStatement
					+ "\n" + ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	/**
	 * Executes an SQL INSERT, UPDATE or DELETE statement. In addition, SQL
	 * statements that return nothing, such as SQL DDL statements, can be
	 * executed.
	 * 
	 * @param sqlStatement
	 *            INSERT, UPDATE or DELETE statement
	 * 
	 * @return either the row count for SQL Data Manipulation Language (DML)
	 *         statements or 0 for SQL statements that return nothing
	 * 
	 */
	public final int executeUpdate(String sqlStatement) {
		int ret = 0;

		try {
			if (connection.isClosed()) {
				return 0;
			}
			// HOLogger.instance().log(getClass(), Sql );
			ret = statement.executeUpdate(sqlStatement);
			return ret;
		} catch (Exception e) {
			HOLogger.instance().error(
					getClass(),
					"JDBCAdapter.executeUpdate : " + e + "\nStatement: " + sqlStatement + "\n"
							+ ExceptionUtils.getStackTrace(e));
			return 0;
		}
	}

	public final int executePreparedUpdate(String insert, Object... params) {
		return executePreparedUpdate(statementCache.getPreparedStatement(insert), params);
	}

	private int executePreparedUpdate(PreparedStatement preparedStatement, Object... params) {
		int ret;

		try {
			if (connection.isClosed()) {
				return 0;
			}
			int i = 0;
			for ( var p: params) {
				preparedStatement.setObject(++i, p);
			}
			ret = preparedStatement.executeUpdate();
			return ret;
		} catch (Exception e) {
			HOLogger.instance().error(
					getClass(),
					"JDBCAdapter.executeUpdate : " + e + "\nStatement: " + preparedStatement.toString() + "\n"
							+ ExceptionUtils.getStackTrace(e));
			return 0;
		}
	}

	/**
	 * Connects to the requested database
	 * 
	 * @param URL
	 *            The path to the Server
	 * @param User
	 *            User
	 * @param PWD
	 *            Password
	 * @param Driver
	 *            The driver to user
	 * 
	 */
	public final void connect(String URL, String User, String PWD, String Driver) throws Exception {
		// Initialise the Database Driver Object
		Class.forName(Driver);
		var connection = DriverManager.getConnection(URL, User, PWD);
		connect(connection);
	}

	public final void connect(Connection conn) throws Exception {
		try {
			connection = conn;
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			statementCache = new StatementCache(this);
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					HOLogger.instance().error(getClass(), "ConnectionManager.connect : " + ex.getMessage());
				}
			}
			HOLogger.instance().error(getClass(), "ConnectionManager.connect : " + e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @return DBInfo
	 * @throws Exception
	 */
	public DBInfo getDBInfo() throws Exception {
		if (dbInfo == null)
			dbInfo = new DBInfo(connection.getMetaData());
		return dbInfo;
	}

	public Object[] getAllTableNames() {
		try {
			return getDBInfo().getAllTablesNames();
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "JDBCAdapter.getAllTableNames : " + e);
			return new String[] { e.getMessage() };
		}
	}
}
