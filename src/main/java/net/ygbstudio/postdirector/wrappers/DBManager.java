package net.ygbstudio.postdirector.wrappers;

import java.sql.*;

import javax.sql.rowset.JdbcRowSet;

public interface DBManager {

    Connection getConnection() throws SQLException;

    Statement getStatement() throws SQLException;

    void setStatement(Statement newSt) throws SQLException;

    void setDBUrl(String dbUrl);

    JdbcRowSet createJdbcRowSet(String sql) throws SQLException;

    default DatabaseMetaData getDBMetaData() throws SQLException{
        return this.getConnection().getMetaData();
    }

    default ResultSet queryDB(String query) throws SQLException {
        return this.getStatement().executeQuery(query);
    }

    default int updateDB(String query) throws SQLException {
        return this.getStatement().executeUpdate(query);
    }

    default PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.getConnection().prepareStatement(sql);
    }

}
