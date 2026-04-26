package Service;


import Domain.Reservation;
import Domain.ReservationDetail;
import Domain.Seat;
import Repository.Interfaces.IReservationRepository;
import Repository.ReservationRepository;

import javax.imageio.event.IIOReadProgressListener;
import java.util.List;

public class ReservationService extends GenericService<Long, Reservation> {

    private final SeatService seatService;
    private final IReservationRepository reservationRepository;
    public ReservationService(ReservationRepository reservationRepository,
                              SeatService seatService) {
        super(reservationRepository);
        this.seatService = seatService;
        this.reservationRepository = reservationRepository;
    }

    public void reserveSeats(String clientName, List<Seat> chosenSeats, Long userId) {
        if (clientName == null || clientName.isBlank())
            throw new IllegalArgumentException("Client name cannot be empty.");
        if (chosenSeats.isEmpty())
            throw new IllegalArgumentException("Must select at least one seat.");
        Reservation reservation = new Reservation(clientName, userId);
        repository.add(reservation);
        for (Seat seat : chosenSeats) {
            seat.setReserved(true);
            seat.setReservationId(reservation.getId());
            seatService.update(seat);
        }
    }

    public void cancel(long reservationId) {
        List<Seat> seats = seatService.getByReservationId(reservationId);
        for (Seat seat : seats) {
            seat.setReserved(false);
            seat.setReservationId(null);
            seatService.update(seat);
        }
        repository.remove(reservationId);
    }
    public List<ReservationDetail> getAllWithDetails() {
        return reservationRepository.findAllWithDetails();
    }


}