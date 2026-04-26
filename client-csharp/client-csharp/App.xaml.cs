using System.IO;
using System.Windows;
using Client.GUI;
using Client.Network;
namespace Client;

public partial class App : Application
{
    private GrpcProxy _proxy;
    private AppNavigator _navigator;
    
    protected override void OnStartup(StartupEventArgs e)
    {
        base.OnStartup(e);

        string host = LoadSetting("server.host", "localhost");
        int port = int.TryParse(LoadSetting("server.port", "65535"), out int p) ? p : 65535;

        _proxy = new GrpcProxy(host, port);
        try { _proxy.Connect(); }
        catch (Exception ex)
        {
            MessageBox.Show($"Cannot connect to server: {ex.Message}");
            Shutdown();
            return;
        }
        _navigator = new AppNavigator(_proxy);
        _navigator.ShowLogin();
    }

    private static string LoadSetting(string key, string fallback)
    {
        try
        {
            var path = Path.Combine(AppContext.BaseDirectory, "client.properties");
            var lines = File.ReadAllLines(path);
            var line = lines.FirstOrDefault(l => l.StartsWith(key + "="));
            return line?.Split('=', 2)[1].Trim() ?? fallback;
        }
        catch
        {
            return fallback;
        }
    }
    protected override void OnExit(ExitEventArgs e)
    {
        _proxy?.Disconnect();
        base.OnExit(e);
    }


}