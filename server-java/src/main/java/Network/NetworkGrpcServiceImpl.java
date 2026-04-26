package Network;
import Domain.*;
import Service.FacadeService;
import Util.DateTimeUtils;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Network.Proto.*;
public class NetworkGrpcServiceImpl extends ReservationServiceGrpc.ReservationServiceImplBase {

    private final FacadeService facade;
    private final Object loginLock = new Object();
    private final Object reservationLock = new Object();
    private final Set<Long> loggedInUsers = ConcurrentHashMap.newKeySet();
    //active push streams — one per connected client
    private final ConcurrentHashMap<Long, StreamObserver<PushPayload>> subscribers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService evictScheduler = Executors.newSingleThreadScheduledExecutor();

    public NetworkGrpcServiceImpl(FacadeService facade) {
        this.facade = facade;
    }

    //in gRPC, unlike a normal method that just returns a value, you push resposes back through a StreamObserver<T> callback object
    @Override
    public void login(LoginPayload request, StreamObserver<ProtoUser> responseObserver) {
        try {
            User user = facade.login(request.getUsername(), request.getPassword());
            if (!loggedInUsers.add(user.getId()))
                throw new RuntimeException("User already logged in.");
            try {
                Office office = facade.getOfficeById(user.getOfficeId());
                responseObserver.onNext(ProtoUtils.toProto(user, office));
                responseObserver.onCompleted(); //signals I'm done sending
            } catch (Exception e) {
                loggedInUsers.remove(user.getId()); //rollback immediately
                throw e;
            }
            //if they don't subscribe within 5 seconds, kick them out
            evictScheduler.schedule(() -> {
                if (!subscribers.containsKey(user.getId()))
                    loggedInUsers.remove(user.getId());
            }, 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    public void logout(LogoutPayload request, StreamObserver<Empty> responseObserver) {
        StreamObserver<PushPayload> pushStream;
        synchronized (loginLock) {
            loggedInUsers.remove(request.getUserId());
            pushStream = subscribers.remove(request.getUserId());
        }
        if (pushStream != null)
            pushStream.onCompleted(); //closing their push stream
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }


    @Override
    public void searchTrips(SearchTripsPayload request, StreamObserver<TripList> responseObserver) {
        try {
            LocalDateTime from = request.getFrom().isBlank() ? null : DateTimeUtils.parse(request.getFrom());
            LocalDateTime to = request.getTo().isBlank() ? null : DateTimeUtils.parse(request.getTo());
            List<Trip> trips = facade.searchTrips(request.getDestination(), from, to);
            List<Integer> free = trips.stream().map(t -> facade.countFreeSeats(t.getId())).toList();
            responseObserver.onNext(ProtoUtils.toTripList(trips, free));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getSeats(GetSeatsPayload request, StreamObserver<SeatList> responseObserver) {
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
    public void getAllReservations(Empty request, StreamObserver<ReservationList> responseObserver) {
        try {
            responseObserver.onNext(buildReservationList());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
    private ReservationList buildReservationList() {
        return ProtoUtils.toReservationList(facade.getAllReservationDetails());
    }

    @Override
    public void cancelReservation(CancelReservationPayload request, StreamObserver<Empty> responseObserver) {
        long tripId;
        try {
            synchronized (reservationLock) {
                if (facade.getReservationById(request.getReservationId()) == null)
                    throw new RuntimeException("Reservation already cancelled.");
                tripId = facade.getTripIdByReservation(request.getReservationId());
                facade.cancelReservation(request.getReservationId());
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            notifyPush(tripId);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void makeReservation(MakeReservationPayload request, StreamObserver<Empty> responseObserver) {
        long tripId;
        try {
            synchronized (reservationLock) {
                List<Seat> seats = request.getSeatIdsList().stream().map(facade::getSeatById).toList();
                for (Seat s : seats)
                    if (s.isReserved())
                        throw new RuntimeException("Seat " + s.getNumber() + " already reserved.");
                facade.makeReservationForSeats(request.getClientName(), seats, request.getUserId());
                tripId = seats.get(0).getTripId();
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
            notifyPush(tripId);
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void subscribeToPush(UserId request, StreamObserver<PushPayload> responseObserver) {
        ServerCallStreamObserver<PushPayload> serverStream = (ServerCallStreamObserver<PushPayload>) responseObserver;
        synchronized (loginLock) {
            if (!loggedInUsers.contains(request.getUserId())) {
                responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Not logged in").asRuntimeException());
                return;
            }
            subscribers.put(request.getUserId(), responseObserver);
        }
            //covers most cases of exit
        serverStream.setOnCancelHandler(() -> {
            synchronized (loginLock) {
                subscribers.remove(request.getUserId());
                loggedInUsers.remove(request.getUserId());
            }
        });

    }

    private void notifyPush(long tripId) {
        PushPayload push = PushPayload.newBuilder().setUpdatedTripId(tripId).setReservations(buildReservationList()).build();
        for (var entry : subscribers.entrySet()) {
            try { entry.getValue().onNext(push); }
            catch (Exception e) { subscribers.remove(entry.getKey()); }
        }
    }
    public void shutdown() {
        evictScheduler.shutdown();
    }
}