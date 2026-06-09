package roomescape.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import roomescape.domain.ReservationTime;

public class FakeReservationTimeRepository implements ReservationTimeRepository {

    public static final String RESERVATION_TIME_TABLE = "reservationTime";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeReservationTimeRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public ReservationTime create(ReservationTime reservationTime) {
        ReservationTime reservationTimeWithId = new ReservationTime(++currentId, reservationTime.getStartAt());
        fakeDatabase.create(RESERVATION_TIME_TABLE, reservationTimeWithId.getId(), reservationTimeWithId);
        return reservationTimeWithId;
    }

    @Override
    public Optional<ReservationTime> readById(Long id) {
        return Optional.ofNullable(fakeDatabase.read(RESERVATION_TIME_TABLE, id, ReservationTime.class));
    }

    @Override
    public List<ReservationTime> readAll() {
        return fakeDatabase.readAll(RESERVATION_TIME_TABLE, ReservationTime.class);
    }

    @Override
    public void delete(ReservationTime reservationTime) {
        fakeDatabase.delete(RESERVATION_TIME_TABLE, reservationTime.getId());
    }

    @Override
    public boolean existByStartAt(LocalTime startAt) {
        return fakeDatabase.readAll(RESERVATION_TIME_TABLE, ReservationTime.class).stream()
                .anyMatch(rt -> rt.getStartAt().equals(startAt));
    }
}
