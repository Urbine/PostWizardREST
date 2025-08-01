package net.ygbstudio.postdirector.wrappers;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;
import javax.sql.rowset.RowSetFactory;

// Third-party classes
import org.sqlite.SQLiteConfig;

public class SQLiteDB implements DBManager {
    private final Path dbName;

    private Connection conn;
    private Properties sqliteProperties;
    private Statement statement;
    private String dbUrl;
    private SQLiteConfig sqliteConfig;

    public SQLiteDB(Path dbname) {
        if (dbname == null)
            throw new InvalidParameterException("Please provide a name or path for your SQLite database.");
        this.dbName = dbname;
    }

    public SQLiteDB(String dbname) {
        if (dbname.isBlank())
            throw new InvalidParameterException("Please provide a name or path for your SQLite database.");
        this.dbName = Path.of(dbname);
    }

    public SQLiteDB(String dbName, Properties sqliteProperties) {
        if (dbName.isBlank() || sqliteProperties.isEmpty())
            throw new InvalidParameterException("Please provide a name and/or properties for your SQLite database.");
        this.dbName = Path.of(dbName);
        this.sqliteProperties = sqliteProperties;
    }

    public static Connection getMemDB() throws SQLException{
        return DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    public static Connection getMemDBRO() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite::memory:?jdbc.explicit_readonly=true");
    }

    public static Connection getTempDB() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:");
    }

    private String setDBUrl() {
        return String.format("jdbc:sqlite:%s", this.dbName);
    }

    @Override
    public void setDBUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String driverClass = "org.sqlite.JDBC";
        String databaseUrl = (dbUrl == null) ? setDBUrl() : dbName.toString();

        try {
            Class.forName(driverClass);
            if (sqliteProperties == null && conn == null) {
                conn = DriverManager.getConnection(databaseUrl);
            } else if (sqliteProperties != null && conn == null) {
                conn = DriverManager.getConnection(databaseUrl, sqliteProperties);
            }

            return this.conn;

        } catch (ClassNotFoundException e) {
            System.err.println(
                    e.getMessage()
                            + " while trying to get a connection to database "
                            + dbName
                            + " with driver: "
                            + driverClass
            );
            throw new RuntimeException();
        }
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
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public JdbcRowSet createJdbcRowSet(String sql) throws SQLException {
        RowSetFactory factory = RowSetProvider.newFactory();
        JdbcRowSet jdbcRS = factory.createJdbcRowSet();
        jdbcRS.setUrl(dbUrl);
        jdbcRS.setCommand(sql);
        return jdbcRS;
    }

    public SQLiteConfig getSQLiteConfig() {
        sqliteConfig = new SQLiteConfig();
        return sqliteConfig;
    }

    public void applyConfig() {
        if (sqliteConfig != null) {
            sqliteProperties = sqliteConfig.toProperties();
        }
    }

}
