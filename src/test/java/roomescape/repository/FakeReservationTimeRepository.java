package roomescape.repository;

import static roomescape.repository.FakeReservationRepository.RESERVATION_TABLE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.ReservationTime;
import roomescape.entity.ReservationEntity;
import roomescape.entity.ReservationTimeEntity;

public class FakeReservationTimeRepository implements ReservationTimeRepository {

    public static final String RESERVATION_TIME_TABLE = "reservationTime";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeReservationTimeRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public ReservationTimeEntity create(ReservationTime reservationTime) {
        ReservationTimeEntity reservationTimeEntity = new ReservationTimeEntity(++currentId,
                reservationTime.getStartAt());
        fakeDatabase.create(RESERVATION_TIME_TABLE, reservationTimeEntity.getId(), reservationTimeEntity);

        return reservationTimeEntity;
    }

    @Override
    public Optional<ReservationTimeEntity> read(Long id) {
        return Optional.ofNullable(fakeDatabase.read(RESERVATION_TIME_TABLE, id, ReservationTimeEntity.class));
    }

    @Override
    public List<ReservationTimeEntity> readAll() {
        return fakeDatabase.readAll(RESERVATION_TIME_TABLE, ReservationTimeEntity.class);
    }

    @Override
    public void delete(Long id) {
        fakeDatabase.delete(RESERVATION_TIME_TABLE, id);
    }

    @Override
    public List<Long> reservedTimeIdByDateAndTheme(LocalDate date, Long themeId) {
        List<Long> reservedTimeId = fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class).stream()
                .filter(reservation -> reservation.getDate().equals(date) && reservation.getTheme().getId()
                        .equals(themeId))
                .map(reservation -> reservation.getTime().getId())
                .toList();

        return fakeDatabase.readAll(RESERVATION_TIME_TABLE, ReservationTimeEntity.class).stream()
                .map(ReservationTimeEntity::getId)
                .filter(reservedTimeId::contains)
                .toList();
    }
}
