package Service;


import Domain.Seat;
import Repository.Filter;
import Repository.Interfaces.ISeatRepository;
import Repository.SeatRepository;

import java.util.List;
import java.util.Optional;

public class SeatService extends GenericService<Long, Seat> {
    private final ISeatRepository seatRepository;
    public SeatService(SeatRepository seatRepository) {
        super(seatRepository);
        this.seatRepository = seatRepository;
    }

    public List<Seat> getByTripId(Long tripId) {
        Filter filter = new Filter();
        filter.addFilter("trip_id", tripId);
        return filter(filter);
    }
    public List<Seat> getByReservationId(Long reservationId) {
        Filter filter = new Filter();
        filter.addFilter("reservation_id", reservationId);
        return filter(filter);
    }
//    public List<Seat> getFreeByTripId(Long tripId) {
//        Filter filter = new Filter();
//        filter.addFilter("trip_id", tripId);
//        filter.addFilter("isReserved", 0);
//        return filter(filter);
//    }
    public int getFreeByTripId(Long tripId) {
        return seatRepository.countFreeSeats(tripId);
    }

    public List<Integer> getSeatNumbersByReservation(Long reservationId) {
        Filter filter = new Filter();
        filter.addFilter("reservation_id", reservationId);
        return filter(filter).stream()
                .map(Seat::getNumber)
                .toList();
    }

    public Optional<Long> getTripIdByReservationId(Long reservationId) {
        return getByReservationId(reservationId).stream()
                .findFirst()
                .map(Seat::getTripId);
    }


}
