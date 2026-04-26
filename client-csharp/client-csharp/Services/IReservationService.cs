using Network.Proto;

namespace Client.Services;

public interface IReservationService
{
    ProtoUser Login(string username, string password);
    void Logout(long userId);
    TripList SearchTrips(string destination, string from, string to);
    SeatList GetSeats(long tripId);
    ReservationList GetAllReservations();
    void MakeReservation(string clientName, List<long> seatIds, long userId);
    void CancelReservation(long reservationId);
}