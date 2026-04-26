package Service;


import Domain.*;
import Service.TransactionsLogic.TransactionManager;

import java.time.LocalDateTime;
import java.util.List;

public class FacadeService {

    private final AuthService userService;
    private final TripService tripService;
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final OfficeService officeService;
    private final TransactionManager txManager;

    public FacadeService(AuthService userService,
                            TripService tripService,
                            SeatService seatService,
                            ReservationService reservationService,
                            OfficeService officeService,
                            TransactionManager txManager) {
        this.userService = userService;
        this.tripService = tripService;
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.officeService = officeService;
        this.txManager = txManager;
    }

    public User login(String user, String pass) {
        //return txManager.runWithResult(() -> {
        return userService.login(user, pass);
        //});
    }
//    public void logout() {
//        userService.logout();
//    }

    public List<Trip> searchTrips(String destination, LocalDateTime from, LocalDateTime to) {
        return tripService.search(destination, from, to);
    }

    public List<Seat> getSeatsForTrip(Long tripId) {
        return seatService.getByTripId(tripId);
    }

    public void makeReservationForSeats(String clientName, List<Seat> chosenSeats, Long userId) {
        txManager.run(() -> {
            reservationService.reserveSeats(clientName, chosenSeats, userId);
        });
    }

    public void cancelReservation(Long reservationId) {
        txManager.run(()-> {
            reservationService.cancel(reservationId);
        });
    }

    public List<Reservation> getAllReservations() {
        return reservationService.getAll();
    }
    public Office getOfficeById(Long id){
        return officeService.findById(id).orElse(null);
    }
    public int countFreeSeats(Long tripId) {
        return seatService.getFreeByTripId(tripId).size();
    }
    public Seat getSeatById(Long seatId) {
        return seatService.findById(seatId).orElse(null);
    }
    public Reservation getReservationById(Long reservationId) {
        return reservationService.findById(reservationId).orElse(null);
    }

    public User getUserById(Long userId) {
        return userService.findById(userId).orElse(null);
    }
    public List<Integer> getSeatNumbersByReservation(Long reservationId) {
        return seatService.getSeatNumbersByReservation(reservationId);
    }
    public Long getTripIdByReservation(Long reservationId) {
        return seatService.getTripIdByReservationId(reservationId).orElse(null);
    }

}