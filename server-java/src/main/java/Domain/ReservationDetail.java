package Domain;

import java.util.List;

public record ReservationDetail(
        long id,
        String clientName,
        String reservationTime,
        String userUsername,
        long tripId,
        List<Integer> seatNumbers
) {}
