package Repository;


import Domain.Office;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OfficeRepository extends GenericRepository<Long, Office> {

    @Override
    public String getTableName() {
        return "offices";
    }

    @Override
    protected String buildInsertSql() {
        return "INSERT INTO offices (address) VALUES (?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Office office) throws SQLException {
        ps.setString(1, office.getAddress());
    }

    @Override
    protected String buildUpdateSql() {
        return "UPDATE offices SET address = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Office office) throws SQLException {
        ps.setString(1, office.getAddress());
        ps.setLong(2, office.getId());
    }

    @Override
    protected Office mapResultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String address = rs.getString("address");
        Office office = new Office(id, address);
        return office;
    }
    @Override
    protected Long extractGeneratedId(ResultSet keys) throws SQLException {
        return keys.getLong(1);
    }
}