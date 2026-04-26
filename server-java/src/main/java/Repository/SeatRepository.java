package Repository;


import Domain.Seat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SeatRepository extends GenericRepository<Long, Seat> {

    @Override
    public String getTableName() { return "seats"; }

    @Override
    protected String buildInsertSql() {
        return "INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Seat seat) throws SQLException {
        ps.setInt(1, seat.getNumber());
        ps.setInt(2, seat.isReserved() ? 1 : 0);
        ps.setLong(3, seat.getTripId());
        if (seat.getReservationId() != null) ps.setLong(4, seat.getReservationId());
        else ps.setNull(4, Types.INTEGER);
    }

    @Override
    protected String buildUpdateSql() {
        return "UPDATE seats SET number = ?, isReserved = ?, trip_id = ?, reservation_id = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Seat seat) throws SQLException {
        ps.setInt(1, seat.getNumber());
        ps.setInt(2, seat.isReserved() ? 1 : 0);
        ps.setLong(3, seat.getTripId());
        if (seat.getReservationId() != null) ps.setLong(4, seat.getReservationId());
        else ps.setNull(4, Types.INTEGER);
        ps.setLong(5, seat.getId());
        }

    @Override
    protected Seat mapResultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        int number = rs.getInt("number");
        boolean isReserved = rs.getInt("isReserved") == 1;
        long tripId = rs.getLong("trip_id");
        Long reservationId = rs.getObject("reservation_id") != null ? rs.getLong("reservation_id") : null;
        Seat seat = new Seat(id, number, isReserved, tripId, reservationId);
        return seat;
    }
    @Override
    protected Long extractGeneratedId(ResultSet keys) throws SQLException {
        return keys.getLong(1);
    }
}