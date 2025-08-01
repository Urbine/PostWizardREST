package net.ygbstudio.postdirector.wrappers;

import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDB implements DBManager {
	private final String dbHost;
	private final String dbName;
	private final String dbUser;
	private final String dbPassword;
	private final int dbPort;

	private Connection conn;
	private Statement statement;
	private String dbUrl;

	public MariaDB() {
		dbPort = 0;
		dbHost = "";
		dbName = "";
		dbUser = "";
		dbPassword = "";
	}

	public MariaDB(String dbURl) {
		this.dbUrl = dbURl;
		dbPort = 0;
		dbHost = "";
		dbName = "";
		dbUser = "";
		dbPassword = "";
	}

	public MariaDB(String dbHost, String dbName, Integer dbPort, String dbUser, String password) {
		this.dbPort = (dbPort != null) ? dbPort : 3306;
		if (dbHost.isBlank()
				|| dbName.isBlank()
				|| dbUser.isBlank()
				|| password.isBlank()) {
			throw new InvalidParameterException(
					"You cannot create a MariaDB object without host, name, user or password");
		}

		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbUser = dbUser;
		dbPassword = password;
	}

	private String setDBUrl() {
		return String.format("jdbc:mariadb://%s:%d/%s/?user=%s&password=%s",
				dbHost, dbPort, dbName, dbUser, dbPassword);
	}

	@Override
	public void setDBUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	@Override
	public Connection getConnection() throws SQLException {
		String driverClass = "org.mariadb.jdbc.Driver";
		String databaseUrl = (dbUrl == null) ? setDBUrl() : dbUrl;
		try {
			Class.forName(driverClass);
			if (conn == null)
				conn = DriverManager.getConnection(databaseUrl);

			return conn;
		} catch (ClassNotFoundException e) {
			System.err.println(
					e.getMessage()
							+ " while trying to get a connection to database "
							+ dbName
							+ " with driver: "
							+ driverClass);
		}
		return null;
	}

	@Override
	public void setStatement(Statement newSt) {
		statement = newSt;
	}

	@Override
	public Statement getStatement() throws SQLException {
		if (conn == null) {
			conn = this.getConnection();
		} else if (statement == null) {
			statement = conn.createStatement();
		}
		return statement;
	}

	@Override
	public JdbcRowSet createJdbcRowSet(String sql) throws SQLException {
		RowSetFactory factory = RowSetProvider.newFactory();
		JdbcRowSet jdbcRS = factory.createJdbcRowSet();
		jdbcRS.setUrl(dbUrl);
		jdbcRS.setUsername(dbUser);
		jdbcRS.setPassword(dbPassword);
		jdbcRS.setCommand(sql);
		return jdbcRS;
	}
}
