using System.Windows;
using System.Windows.Input;
using Client.Network;

namespace Client.GUI.Windows;

public partial class LoginWindow: Window

{
    private readonly GrpcProxy _proxy;
    private readonly INavigationListener _nav;

    public LoginWindow(GrpcProxy proxy, INavigationListener nav)
    {
        _proxy = proxy;
        _nav   = nav;
        InitializeComponent();
        UsernameField.KeyDown += (_, e) => { if (e.Key == Key.Enter) HandleLogin(); };
        PasswordField.KeyDown += (_, e) => { if (e.Key == Key.Enter) HandleLogin(); };
    }
    private void LoginButton_Click(object sender, RoutedEventArgs e) => HandleLogin();

    private void HandleLogin()
    {
        try
        {
            ErrorLabel.Content = "";
            var user = _proxy.Login(UsernameField.Text.Trim(), PasswordField.Password);
            _nav.OnLoginSuccess(user);
        }
        catch (Exception ex)
        {
            ErrorLabel.Content  = ex.Message;
            PasswordField.Clear();
        }
    }



}