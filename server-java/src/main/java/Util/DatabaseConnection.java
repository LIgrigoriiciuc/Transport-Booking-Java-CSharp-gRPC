package Util;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {
    private static final HikariDataSource dataSource;
    private static final ThreadLocal<Connection> transactionConnection = new ThreadLocal<>();

    public static void bindConnection(Connection conn) {
        transactionConnection.set(conn);
    }

    public static void unbindConnection() {
        transactionConnection.remove();
    }

    static {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null)
                throw new RuntimeException("app.properties not found in classpath");

            props.load(input);
            String dbUrl = props.getProperty("db.url");
            if (dbUrl == null || dbUrl.isBlank())
                throw new RuntimeException("Property 'db.url' is missing or empty in app.properties");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30_000);
            config.setIdleTimeout(600_000);
            config.setMaxLifetime(1_800_000);

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }
    public static ConnectionHolder getConnection() throws SQLException {
        Connection txConn = transactionConnection.get();
        if (txConn != null)
            return new ConnectionHolder(txConn, false);
        return new ConnectionHolder(dataSource.getConnection(), true);
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}