package Repository.Interfaces;

import Domain.Reservation;
import Domain.ReservationDetail;

import java.util.List;

public interface IReservationRepository extends IRepository<Long, Reservation> {
    List<ReservationDetail> findAllWithDetails();
}
