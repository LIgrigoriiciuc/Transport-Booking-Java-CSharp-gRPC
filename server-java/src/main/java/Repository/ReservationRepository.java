package Repository;


import Domain.Reservation;
import Domain.ReservationDetail;
import Repository.Interfaces.IReservationRepository;
import Util.ConnectionHolder;
import Util.DatabaseConnection;
import Util.DateTimeUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReservationRepository extends GenericRepository<Long, Reservation> implements IReservationRepository {

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
    //fix N+1 query issue
    @Override
    public List<ReservationDetail> findAllWithDetails() {
        String sql = """
        SELECT r.id, r.client_name, r.reservation_time,
               u.username,
               s.trip_id,
               GROUP_CONCAT(s.number ORDER BY s.number) AS seat_numbers
        FROM reservations r
        JOIN users u ON u.id = r.user_id
        JOIN seats s ON s.reservation_id = r.id
        GROUP BY r.id, r.client_name, r.reservation_time, u.username, s.trip_id
        """;
        List<ReservationDetail> results = new ArrayList<>();
        try (ConnectionHolder holder = DatabaseConnection.getConnection();
             PreparedStatement ps = holder.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                List<Integer> seats = Arrays.stream(rs.getString("seat_numbers").split(","))
                        .map(Integer::parseInt)
                        .toList();
                results.add(new ReservationDetail(
                        rs.getLong("id"),
                        rs.getString("client_name"),
                        rs.getString("reservation_time"),
                        rs.getString("username"),
                        rs.getLong("trip_id"),
                        seats
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservation details", e);
        }
        return results;
    }
}