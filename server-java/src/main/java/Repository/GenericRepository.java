package Repository;


import Domain.Entity;
import Repository.Interfaces.IRepository;
import Util.ConnectionHolder;
import Util.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericRepository<ID,E extends Entity<ID>> implements IRepository<ID, E> {
    @Override
    public List<E> filter(Filter filter){
        List<E> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " " + filter.buildWhere();
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql)) {
            filter.applyParameters(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    entities.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering " + getTableName(), e);
        }
        return entities;
    }
    @Override
    public List<E> getAll() {
        List<E> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();

        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all from " + getTableName(), e);
        }
        return entities;
    }

    @Override
    public Optional<E> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return Optional.of(mapResultSetToEntity(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding by id in " + getTableName(), e);
        }
    }

    @Override
    public void add(E e) {
        String sql = buildInsertSql();
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(ps, e);
            if (ps.executeUpdate() == 0)
                throw new SQLException("Insert failed, no rows affected.");
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next())
                    e.setId(extractGeneratedId(keys));
                else
                    throw new SQLException("Insert failed, no ID obtained.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error adding to " + getTableName(), ex);
        }
    }

    @Override
    public boolean update(E e) {
        String sql = buildUpdateSql();
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql)) {
            setUpdateParameters(ps, e);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error updating " + getTableName(), ex);
        }
    }

    @Override
    public boolean remove(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing from " + getTableName(), e);
        }
    }
    protected abstract String buildInsertSql();
    protected abstract void setInsertParameters(PreparedStatement ps, E e) throws SQLException;
    protected abstract String buildUpdateSql();
    protected abstract void setUpdateParameters(PreparedStatement preparedStatement, E e) throws SQLException;
    public abstract String getTableName();
    protected abstract E mapResultSetToEntity(ResultSet resultSet) throws SQLException;
    protected abstract ID extractGeneratedId(ResultSet keys) throws SQLException;
}
