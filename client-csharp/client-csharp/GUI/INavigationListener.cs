using Network.Proto;

namespace Client.Network;

public interface INavigationListener
{
    void OnLoginSuccess(ProtoUser user);
    void OnLogout();
}