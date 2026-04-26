package Repository;


import Domain.Reservation;
import Util.DateTimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReservationRepository extends GenericRepository<Long, Reservation> {

    @Override
    public String getTableName() { return "reservations"; }

    @Override
    protected String buildInsertSql() {
        return "INSERT INTO reservations (clientName, userId, reservationTime) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Reservation reservation) throws SQLException {
        ps.setString(1, reservation.getClientName());
        ps.setLong(2, reservation.getUserId());
        ps.setString(3, DateTimeUtils.format(reservation.getReservationTime()));
        }

    @Override
    protected String buildUpdateSql() {
        return "UPDATE reservations SET clientName = ?, userId = ?, reservationTime = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Reservation reservation) throws SQLException {
        ps.setString(1, reservation.getClientName());
        ps.setLong(2, reservation.getUserId());
        ps.setString(3, DateTimeUtils.format(reservation.getReservationTime()));
        ps.setLong(4, reservation.getId());
        }

    @Override
    protected Reservation mapResultSetToEntity(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String clientName = rs.getString("clientName");
        long userId = rs.getLong("userId");
        LocalDateTime reservationTime = DateTimeUtils.parse(rs.getString("reservationTime"));
        Reservation reservation = new Reservation(id, clientName, userId, reservationTime);
        return reservation;
    }
    @Override
    protected Long extractGeneratedId(ResultSet keys) throws SQLException {
        return keys.getLong(1);
    }
}