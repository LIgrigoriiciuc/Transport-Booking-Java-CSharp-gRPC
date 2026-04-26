using Network.Proto;

namespace Client.Services;

public interface IReservationObserver
{
    void OnPushReceived(PushPayload push);
}