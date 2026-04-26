package Domain;

import java.time.LocalDateTime;

public class Trip extends Entity<Long> {
    private String destination;
    private LocalDateTime time;
    private String busNumber;

    public Trip(Long id, String destination, LocalDateTime time, String busNumber) {
        super(id);
        this.destination = destination;
        this.time = time;
        this.busNumber = busNumber;
    }

    public String getDestination() { return destination; }
    public LocalDateTime getTime() { return time; }
    public String getBusNumber() { return busNumber; }
}