package Network;

import Domain.*;
import Service.FacadeService;
import Util.DateTimeUtils;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import Network.Proto.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class NetworkGrpcServiceImpl extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final FacadeService facade;
    private final Object loginLock = new Object();
    private final Object reservationLock = new Object();
    private final Set<Long> loggedInUsers = ConcurrentHashMap.newKeySet();
    // active push streams — one per connected client
    private final CopyOnWriteArrayList<StreamObserver<PushPayload>> subscribers = new CopyOnWriteArrayList<>();
    public NetworkGrpcServiceImpl(FacadeService facade) {
        this.facade = facade;
    }

    @Override
    public void login(LoginPayload request, StreamObserver<ProtoUser> responseObserver) {
        try {
            synchronized (loginLock) {
                User user = facade.login(request.getUsername(), request.getPassword());
                if (user == null)
                    throw new RuntimeException("Authentication failed.");
                if (!loggedInUsers.add(user.getId()))          // add() returns false if already present
                    throw new RuntimeException("User already logged in.");
                Office office = facade.getOfficeById(user.getOfficeId());
                responseObserver.onNext(ProtoUtils.toProto(user, office));
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            responseObserver.onError(
                    Status.UNAUTHENTICATED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void logout(LogoutPayload request, StreamObserver<Empty> responseObserver) {
        loggedInUsers.remove(request.getUserId());   // ← release the slot
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }


    @Override
    public void searchTrips(SearchTripsPayload request,
                            StreamObserver<TripList> responseObserver) {
        try {
            LocalDateTime from = isBlank(request.getFrom())
                    ? null : DateTimeUtils.parse(request.getFrom());
            LocalDateTime to = isBlank(request.getTo())
                    ? null : DateTimeUtils.parse(request.getTo());

            List<Trip> trips     = facade.searchTrips(request.getDestination(), from, to);
            List<Integer> free   = trips.stream()
                    .map(t -> facade.countFreeSeats(t.getId())).toList();

            responseObserver.onNext(ProtoUtils.toTripList(trips, free));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSeats(GetSeatsPayload request,
                         StreamObserver<SeatList> responseObserver) {
        try {
            var seats = facade.getSeatsForTrip(request.getTripId());
            responseObserver.onNext(ProtoUtils.toSeatList(seats));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllReservations(Empty request,
                                   StreamObserver<ReservationList> responseObserver) {
        try {
            responseObserver.onNext(buildReservationList());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void cancelReservation(CancelReservationPayload request,
                                  StreamObserver<Empty> responseObserver) {
        long tripId;
        try {
            synchronized (reservationLock) {
                if (facade.getReservationById(request.getReservationId()) == null)
                    throw new RuntimeException("Reservation not found.");
                tripId = facade.getTripIdByReservation(request.getReservationId());
                facade.cancelReservation(request.getReservationId());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            notifyPush(tripId); // outside lock
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void makeReservation(MakeReservationPayload request,
                                StreamObserver<Empty> responseObserver) {
        long tripId;
        try {
            synchronized (reservationLock) {
                List<Seat> seats = request.getSeatIdsList().stream()
                        .map(facade::getSeatById).toList();
                for (Seat s : seats)
                    if (s.isReserved())
                        throw new RuntimeException("Seat " + s.getNumber() + " already reserved.");
                facade.makeReservationForSeats(
                        request.getClientName(), seats, request.getUserId());
                tripId = seats.get(0).getTripId();
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            notifyPush(tripId); // outside lock
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void subscribeToPush(Empty request,
                                StreamObserver<PushPayload> responseObserver) {
        // cast to get cancel callback
        ServerCallStreamObserver<PushPayload> serverStream =
                (ServerCallStreamObserver<PushPayload>) responseObserver;

        serverStream.setOnCancelHandler(() -> subscribers.remove(responseObserver));
        subscribers.add(responseObserver);
        // stream stays open until client disconnects
    }
    private void notifyPush(long tripId) {
        PushPayload push = PushPayload.newBuilder()
                .setUpdatedTripId(tripId)
                .setReservations(buildReservationList())
                .build();

        for (StreamObserver<PushPayload> sub : subscribers) {
            try { sub.onNext(push); }
            catch (Exception e) {
                subscribers.remove(sub); // dead stream
            }
        }
    }

    private ReservationList buildReservationList() {
        var reservations   = facade.getAllReservations();
        var seatsPerRes    = reservations.stream()
                .map(r -> facade.getSeatNumbersByReservation(r.getId())).toList();
        var users          = reservations.stream()
                .map(r -> facade.getUserById(r.getUserId())).toList();
        var tripIds        = reservations.stream()
                .map(r -> facade.getTripIdByReservation(r.getId())).toList();
        return ProtoUtils.toReservationList(reservations, seatsPerRes, users, tripIds);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}