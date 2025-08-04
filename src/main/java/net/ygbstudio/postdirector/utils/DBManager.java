package net.ygbstudio.postdirector.utils;

import java.sql.*;

import javax.sql.rowset.JdbcRowSet;

/**
 * Interface for managing database connections and operations.
 * Provides methods to get connections, statements, and execute queries.
 * 
 * @author Yoham Gabriel @ YGB Studio
 */
public interface DBManager {

	/**
	 * Gets a connection to the database.
	 * 
	 * @return a Connection object to the database
	 * @throws SQLException if a database access error occurs
	 */
	Connection getConnection() throws SQLException;

	/**
	 * Closes the database connection.
	 * 
	 * @throws SQLException if a database access error occurs
	 */
	Statement getStatement() throws SQLException;

	/**
	 * Closes the database connection.
	 * 
	 * @throws SQLException if a database access error occurs
	 */
	void setStatement(Statement newSt) throws SQLException;

	/**
	 * Sets the database URL for the connection.
	 * 
	 * @param dbUrl the database URL to set
	 */
	void setDBUrl(String dbUrl);

	/**
	 * Gets the database URL for the connection.
	 * 
	 * @return the database URL
	 */
	JdbcRowSet createJdbcRowSet(String sql) throws SQLException;

	/**
	 * Gets the database metadata.
	 * 
	 * @return a DatabaseMetaData object containing metadata about the database
	 * @throws SQLException if a database access error occurs
	 */
	default DatabaseMetaData getDBMetaData() throws SQLException {
		return this.getConnection().getMetaData();
	}

	/**
	 * Executes a query on the database.
	 * 
	 * @param query the SQL query to execute
	 * @return a ResultSet object containing the results of the query
	 * @throws SQLException if a database access error occurs
	 */
	default ResultSet queryDB(String query) throws SQLException {
		return this.getStatement().executeQuery(query);
	}

	/**
	 * Updates the database with the provided query.
	 * 
	 * @param query the SQL update query to execute
	 * @return the number of rows affected by the update
	 * @throws SQLException if a database access error occurs
	 */
	default int updateDB(String query) throws SQLException {
		return this.getStatement().executeUpdate(query);
	}

	/**
	 * Prepares a SQL statement for execution.
	 * 
	 * @param sql the SQL statement to prepare
	 * @return a PreparedStatement object for executing the SQL statement
	 * @throws SQLException if a database access error occurs
	 */
	default PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.getConnection().prepareStatement(sql);
	}

}
