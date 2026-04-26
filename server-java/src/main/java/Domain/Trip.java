package Domain;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trip trip)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(destination, trip.destination) && Objects.equals(time, trip.time) && Objects.equals(busNumber, trip.busNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), destination, time, busNumber);
    }
}