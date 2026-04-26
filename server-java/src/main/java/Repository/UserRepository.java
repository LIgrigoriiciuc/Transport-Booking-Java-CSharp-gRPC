package Repository;

import Domain.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository extends GenericRepository<Long, User> {

    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    protected String buildInsertSql() {
        return "INSERT INTO users (username, password, fullName) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        }

    @Override
    protected String buildUpdateSql() {
        return "UPDATE users SET username = ?, password = ?, fullName = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setLong(4, user.getId());
        }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullName"),
                rs.getLong("officeId"));
        return user;
    }
    @Override
    protected Long extractGeneratedId(ResultSet keys) throws SQLException {
        return keys.getLong(1);
    }
}
