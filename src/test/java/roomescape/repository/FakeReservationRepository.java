package roomescape.repository;

import static roomescape.repository.FakeReservationTimeRepository.RESERVATION_TIME_TABLE;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;
import roomescape.entity.ReservationEntity;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;

public class FakeReservationRepository implements ReservationRepository {

    public static final String RESERVATION_TABLE = "reservation";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeReservationRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public ReservationEntity create(Reservation reservation, ReservationTimeEntity timeEntity,
                                    ThemeEntity themeEntity) {
        ReservationEntity reservationEntity = new ReservationEntity(++currentId, reservation.getName(),
                reservation.getDate(), timeEntity, themeEntity);
        fakeDatabase.create(RESERVATION_TABLE, reservationEntity.getId(), reservationEntity);
        return reservationEntity;
    }

    @Override
    public Optional<ReservationEntity> readById(Long id) {
        return Optional.ofNullable(fakeDatabase.read(RESERVATION_TABLE, id, ReservationEntity.class));
    }

    @Override
    public List<ReservationEntity> readByName(String name) {
        return fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class).stream()
                .filter(reservation -> reservation.getName().equals(name))
                .toList();
    }

    @Override
    public List<ReservationEntity> readAll() {
        return fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class).stream()
                .toList();
    }

    @Override
    public void update(Long id, LocalDate newDate, Long newTimeId) {
        ReservationEntity reservation = fakeDatabase.read(RESERVATION_TABLE, id, ReservationEntity.class);
        ReservationTimeEntity newReservationTimeEntity = fakeDatabase.read(RESERVATION_TIME_TABLE, newTimeId,
                ReservationTimeEntity.class);

        ReservationEntity updatedReservation = new ReservationEntity(id, reservation.getName(), newDate,
                newReservationTimeEntity,
                reservation.getTheme());

        fakeDatabase.create(RESERVATION_TABLE, updatedReservation.getId(), ReservationEntity.class);
    }

    @Override
    public void delete(Long id) {
        fakeDatabase.delete(RESERVATION_TABLE, id);
    }

    @Override
    public boolean existByTimeId(Long timeId) {
        List<ReservationEntity> reservations = fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class);

        return reservations.stream()
                .anyMatch(reservation -> reservation.getTime().getId().equals(timeId));
    }

    @Override
    public boolean existByThemeId(Long themeId) {
        List<ReservationEntity> reservations = fakeDatabase.readAll(RESERVATION_TABLE, ReservationEntity.class);

        return reservations.stream()
                .anyMatch(reservation -> reservation.getTheme().getId().equals(themeId));

    }
}
