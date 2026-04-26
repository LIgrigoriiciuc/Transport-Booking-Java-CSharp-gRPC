using System.Windows;
using Client.GUI.Windows;
using Client.Network;
using Network.Proto;

namespace Client.GUI;

public class AppNavigator : INavigationListener

{
    private readonly GrpcProxy _proxy;
    private Window? _current;
    public AppNavigator(GrpcProxy proxy)
    {
        _proxy = proxy;
    }
    public void ShowLogin()
    {
        SwitchTo(new LoginWindow(_proxy, this));
    }

    public void OnLoginSuccess(ProtoUser user)
    {
        SwitchTo(new MainWindow(_proxy, this, user));
    }

    public void OnLogout()
    {
        ShowLogin();
    }

    private void SwitchTo(Window next)
    {
        var old = _current;
        _current = next;
        next.Show();
        old?.Close();
    }

}