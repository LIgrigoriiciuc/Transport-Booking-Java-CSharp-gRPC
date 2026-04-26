package Domain;

public class Seat extends Entity<Long> {
    private Integer number;
    private boolean isReserved;
    private Long tripId;
    private Long reservationId;
    public Seat(Long id, Integer number, boolean isReserved, Long tripId, Long reservationId) {
        super(id);
        this.number = number;
        this.isReserved = isReserved;
        this.tripId = tripId;
        this.reservationId = reservationId;
    }

    public Integer getNumber() { return number; }
    public boolean isReserved() { return isReserved; }
    public Long getTripId() { return tripId; }
    public Long getReservationId() { return reservationId; }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}