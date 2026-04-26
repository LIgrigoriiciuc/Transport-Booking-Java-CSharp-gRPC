using Client.Services;
using Grpc.Core;
using Grpc.Net.Client;
using Network.Proto;

namespace Client.Network;

public class GrpcProxy : IReservationService
{
    private readonly string _host;
    private readonly int _port;
    private GrpcChannel? _channel;
    private ReservationService.ReservationServiceClient? _client;
    private CancellationTokenSource? _pushCts;
    public IReservationObserver? Observer { get; set; }
    public GrpcProxy(string host, int port)
    {
        _host = host;
        _port = port;
    }

    public void Connect()
    {
        _channel = GrpcChannel.ForAddress($"http://{_host}:{_port}");
        _client  = new ReservationService.ReservationServiceClient(_channel);
    }
    
    public void Disconnect()
    {
        _pushCts?.Cancel();
        _channel?.Dispose();
    }
    private static T Call<T>(Func<T> action)
    {
        try { return action(); }
        catch (RpcException e) { throw new Exception(e.Status.Detail); }
    }
    private static void Call(Action action)
    {
        try { action(); }
        catch (RpcException e) { throw new Exception(e.Status.Detail); }
    }

    public ProtoUser Login(string username, string password)
    {
        var user = Call(() => _client!.Login(new LoginPayload
            { Username = username, Password = password }));
        StartPushSubscription(user.Id);
        return user;
    }

    public void Logout(long userId)
    {
        // stop push stream 
        _pushCts?.Cancel(); 
        _client!.Logout(new LogoutPayload { UserId = userId });
        Observer = null;
    }
    
    public TripList SearchTrips(string destination, string from, string to)
        => _client!.SearchTrips(new SearchTripsPayload
            { Destination = destination, From = from, To = to });

    public SeatList GetSeats(long tripId)
        => _client!.GetSeats(new GetSeatsPayload { TripId = tripId });

    public ReservationList GetAllReservations()
        => _client!.GetAllReservations(new Empty());

    public void MakeReservation(string clientName, List<long> seatIds, long userId)
    {
        var payload = new MakeReservationPayload
            { ClientName = clientName, UserId = userId };
        payload.SeatIds.AddRange(seatIds);
        _client!.MakeReservation(payload);
    }

    public void CancelReservation(long reservationId)
        => _client!.CancelReservation(
            new CancelReservationPayload { ReservationId = reservationId });
    private void StartPushSubscription(long userId)
    {
        _pushCts = new CancellationTokenSource();
        var token = _pushCts.Token;
        Task.Run((Func<Task>)(async () =>
        {
            try
            {
                var stream = _client!.SubscribeToPush(
                    new UserId { UserId_ = userId }, cancellationToken: token);
                await foreach (var push in stream.ResponseStream.ReadAllAsync(token))
                {
                    Observer?.OnPushReceived(push);
                }
            }
            catch (OperationCanceledException) { /* normal on logout */ }
            catch (RpcException e) when (e.StatusCode == StatusCode.Cancelled) { }
            catch (Exception e)
            {
                Console.WriteLine($"Push stream error: {e.Message}");
            }
        }), token);
    }






}