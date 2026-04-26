package Util;


import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionHolder implements AutoCloseable {
    private final Connection connection;
    private final boolean owned;

    public ConnectionHolder(Connection connection, boolean owned) {
        this.connection = connection;
        this.owned = owned;
    }

    public Connection get() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (owned) {
            connection.close();
        }
    }
}