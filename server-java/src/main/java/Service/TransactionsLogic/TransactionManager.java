package Service.TransactionsLogic;


import Util.ConnectionHolder;
import Util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class TransactionManager {

    public void run(UnitOfWork work) {
        try (ConnectionHolder holder = DatabaseConnection.getConnection()) {
            Connection conn = holder.get();
            conn.setAutoCommit(false);
            DatabaseConnection.bindConnection(conn);
            try {
                work.execute();
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
                DatabaseConnection.unbindConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain connection for transaction", e);
        }
    }

    public <T> T runWithResult(Callable<T> task) {
        try (ConnectionHolder holder = DatabaseConnection.getConnection()) {
            Connection conn = holder.get();
            conn.setAutoCommit(false);
            DatabaseConnection.bindConnection(conn);
            try {
                T result = task.call();
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
                DatabaseConnection.unbindConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not obtain connection for transaction", e);
        }
    }
}