package Repository.Interfaces;

import Domain.Seat;

public interface ISeatRepository extends IRepository<Long, Seat> {
    public int countFreeSeats(Long tripId);
}
