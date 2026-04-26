using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using Client.Network;
using Client.Services;
using Network.Proto;

namespace Client.GUI.Windows;


public partial class MainWindow : Window, IReservationObserver
{
    private readonly GrpcProxy _proxy;
    private readonly INavigationListener _nav;
    private readonly ProtoUser _currentUser;
    private ProtoTrip? _selectedTrip;
    private List<ProtoSeat> _selectedSeats = new();

    public MainWindow(GrpcProxy proxy, INavigationListener nav, ProtoUser user)
    {
        _proxy = proxy;
        _nav = nav;
        _currentUser = user;
        InitializeComponent();
        _proxy.Observer = this;
        Title = $"Reservations | Office: {user.OfficeAddress} | Worker: {user.FullName}";
        TripTable.SelectionChanged += (_, _) =>
        {
            if (TripTable.SelectedItem is ProtoTrip trip)
            {
                _selectedTrip = trip;
                DrawSeats(trip.Id);
            }
        };

        RefreshTrips();
        RefreshReservations();
    }
    public void OnPushReceived(PushPayload push)
    {
        Dispatcher.Invoke(() =>
        {
            RefreshReservations(push.Reservations);
            if (_selectedTrip != null && _selectedTrip.Id == push.UpdatedTripId)
                DrawSeats(_selectedTrip.Id); // re-request seats only if viewing this trip
        });
    }
    private void ResetButton_Click(object sender, RoutedEventArgs e)
    {
        DestFilter.Clear(); StartTimeField.Clear(); EndTimeField.Clear();
        RefreshTrips();
    }
    private void TripTable_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (TripTable.SelectedItem is ProtoTrip trip)
        {
            _selectedTrip = trip;
            DrawSeats(trip.Id);
        }
    }

    private void LogoutButton_Click(object sender, RoutedEventArgs e)
    {
        _proxy.Observer = null;
        _proxy.Logout(_currentUser.Id);
        _nav.OnLogout();
    }

    private void ReserveButton_Click(object sender, RoutedEventArgs e)
    {
        if (_selectedTrip == null)  { ShowError("Select a trip first.");       return; }
        if (!_selectedSeats.Any())  { ShowError("Select at least one seat.");  return; }
        string name = ClientNameField.Text.Trim();
        if (string.IsNullOrWhiteSpace(name)) { ShowError("Enter client name."); return; }

        try
        {
            _proxy.MakeReservation(name, _selectedSeats.Select(s => s.Id).ToList(), _currentUser.Id);
            ClientNameField.Clear();
            DrawSeats(_selectedTrip.Id);
            RefreshReservations();
        }
        catch (Exception ex) { ShowError(ex.Message); }
    }

    private void CancelButton_Click(object sender, RoutedEventArgs e)
    {
        if (ResList.SelectedItem is not ProtoReservation selected)
        {
            ShowError("Select a reservation to cancel.");
            return;
        }
        try
        {
            _proxy.CancelReservation(selected.Id);
            RefreshReservations();
            if (_selectedTrip != null) DrawSeats(_selectedTrip.Id);
        }
        catch (Exception ex) { ShowError(ex.Message); }
    }
    private void FilterButton_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            var trips = _proxy.SearchTrips(
                DestFilter.Text, StartTimeField.Text, EndTimeField.Text);
            TripTable.ItemsSource = trips.Trips;
        }
        catch { ShowError("Dates must be: yyyy-MM-dd HH:mm"); }
    }
    private void DrawSeats(long tripId)
    {
        SeatGrid.Children.Clear();
        SeatGrid.RowDefinitions.Clear();
        _selectedSeats.Clear();

        var seats = _proxy.GetSeats(tripId).Seats.OrderBy(s => s.Number).ToList();
        int cols  = 4;
        int rows  = (int)Math.Ceiling(seats.Count / (double)cols);

        for (int r = 0; r < rows; r++)
            SeatGrid.RowDefinitions.Add(new RowDefinition { Height = new GridLength(60) });

        for (int i = 0; i < seats.Count; i++)
        {
            var seat = seats[i];
            var btn  = new Button
            {
                Content = seat.Number.ToString(),
                Margin  = new Thickness(4)
            };
            if (seat.Reserved)
            {
                btn.IsEnabled  = false;
                btn.Background = Brushes.Red;
                btn.Foreground = Brushes.White;
                btn.ToolTip    = $"Reservation #{seat.ReservationId}";
            }
            else
            {
                var captured = seat;
                btn.Click += (_, _) => ToggleSeat(btn, captured);
            }
            Grid.SetRow(btn, i / cols);
            Grid.SetColumn(btn, i % cols);
            SeatGrid.Children.Add(btn);
        }
    }
    
    private void ToggleSeat(Button btn, ProtoSeat seat)
    {
        if (_selectedSeats.Contains(seat))
        {
            _selectedSeats.Remove(seat);
            btn.Background = null;
        }
        else
        {
            _selectedSeats.Add(seat);
            btn.Background = Brushes.Gray;
        }
    }


    private void RefreshTrips()
    {
        TripTable.ItemsSource = _proxy.SearchTrips("", "", "").Trips;
    }

    private void RefreshReservations()
    {
        ResList.ItemsSource = _proxy.GetAllReservations().Reservations;
    }

    private void RefreshReservations(ReservationList list)
    {
        ResList.ItemsSource = list.Reservations; // from push — no extra request
    }

    private void ShowError(string msg) =>
        MessageBox.Show(msg, "Error", MessageBoxButton.OK, MessageBoxImage.Error);
}


