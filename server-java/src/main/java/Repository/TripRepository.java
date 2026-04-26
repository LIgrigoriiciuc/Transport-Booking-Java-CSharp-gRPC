package Repository;


import Domain.Trip;
import Util.DateTimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TripRepository extends GenericRepository<Long, Trip> {
    @Override
    public String getTableName() { return "trips"; }

    @Override
    protected String buildInsertSql() {
        return "INSERT INTO trips (destination, time, busNumber) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Trip trip) throws SQLException {
        ps.setString(1, trip.getDestination());
        ps.setString(2, DateTimeUtils.format(trip.getTime()));
        ps.setString(3, trip.getBusNumber());
    }

    @Override
    protected String buildUpdateSql() {
        return "UPDATE trips SET destination = ?, time = ?, busNumber = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Trip trip) throws SQLException {
        ps.setString(1, trip.getDestination());
        ps.setString(2, DateTimeUtils.format(trip.getTime()));
        ps.setString(3, trip.getBusNumber());
        ps.setLong(4, trip.getId());
    }

    @Override
    protected Trip mapResultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String destination = rs.getString("destination");
        LocalDateTime time = DateTimeUtils.parse(rs.getString("time"));
        String busNumber = rs.getString("busNumber");
        Trip trip = new Trip(id, destination, time, busNumber);
        return trip;
    }

    @Override
    protected Long extractGeneratedId(ResultSet keys) throws SQLException {
        return keys.getLong(1);
    }
}