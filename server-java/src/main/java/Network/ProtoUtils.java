package Network;

import Domain.*;
import Network.Proto.*;
import Util.DateTimeUtils;

import java.util.List;

public class ProtoUtils {
    public static ProtoUser toProto(User user, Office office) {
        return ProtoUser.newBuilder()
                .setId(user.getId())
                .setFullName(user.getFullName())
                .setOfficeAddress(office != null ? office.getAddress() : "")
                .build();
    }
    public static ProtoTrip toProto(Trip trip, int freeSeats) {
        return ProtoTrip.newBuilder()
                .setId(trip.getId())
                .setDestination(trip.getDestination())
                .setTime(DateTimeUtils.format(trip.getTime()))
                .setBusNumber(trip.getBusNumber())
                .setFreeSeats(freeSeats)
                .build();
    }
    public static ProtoSeat toProto(Seat seat) {
        return ProtoSeat.newBuilder()
                .setId(seat.getId())
                .setNumber(seat.getNumber())
                .setReserved(seat.isReserved())
                .setTripId(seat.getTripId())
                .setReservationId(seat.getReservationId() != null ? seat.getReservationId() : 0)
                .build();
    }

    public static ProtoReservation toProto(Reservation r, List<Integer> seatNumbers,
                                           User user, Long tripId) {
        return ProtoReservation.newBuilder()
                .setId(r.getId())
                .setClientName(r.getClientName())
                .setReservationTime(DateTimeUtils.format(r.getReservationTime()))
                .setTripId(tripId)
                .setUserUsername(user.getUsername())
                .addAllSeatNumbers(seatNumbers)
                .build();
    }
    public static TripList toTripList(List<Domain.Trip> trips, List<Integer> freeSeats) {
        TripList.Builder builder = TripList.newBuilder();
        for (int i = 0; i < trips.size(); i++)
            builder.addTrips(toProto(trips.get(i), freeSeats.get(i)));
        return builder.build();
    }

    public static SeatList toSeatList(List<Seat> seats) {
        SeatList.Builder builder = SeatList.newBuilder();
        seats.forEach(s -> builder.addSeats(toProto(s)));
        return builder.build();
    }

    public static ReservationList toReservationList(List<ReservationDetail> details) {
        ReservationList.Builder builder = ReservationList.newBuilder();
        for (ReservationDetail d : details)
            builder.addReservations(ProtoReservation.newBuilder()
                    .setId(d.id())
                    .setClientName(d.clientName())
                    .setReservationTime(d.reservationTime())
                    .setTripId(d.tripId())
                    .setUserUsername(d.userUsername())
                    .addAllSeatNumbers(d.seatNumbers())
                    .build());
        return builder.build();
    }
}

