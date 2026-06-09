package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;
import roomescape.domain.Reservations;

public class FakeReservationRepository implements ReservationRepository {

    public static final String RESERVATION_TABLE = "reservation";

    private final FakeDatabase fakeDatabase;
    private Long currentId = 0L;

    public FakeReservationRepository(FakeDatabase fakeDatabase) {
        this.fakeDatabase = fakeDatabase;
    }

    @Override
    public Reservation create(Reservation reservation) {
        Reservation reservationWithId = new Reservation(++currentId, reservation.getName(),
                reservation.getDate(), reservation.getTime(), reservation.getTheme());
        fakeDatabase.create(RESERVATION_TABLE, reservationWithId.getId(), reservationWithId);
        return reservationWithId;
    }

    @Override
    public Optional<Reservation> readById(Long id) {
        return Optional.ofNullable(fakeDatabase.read(RESERVATION_TABLE, id, Reservation.class));
    }

    @Override
    public Reservations readByName(String name) {
        List<Reservation> filtered = fakeDatabase.readAll(RESERVATION_TABLE, Reservation.class).stream()
                .filter(reservation -> reservation.getName().equals(name))
                .toList();
        return new Reservations(filtered);
    }

    @Override
    public Reservations readAll() {
        return new Reservations(fakeDatabase.readAll(RESERVATION_TABLE, Reservation.class));
    }

    @Override
    public void update(Reservation reservation) {
        fakeDatabase.create(RESERVATION_TABLE, reservation.getId(), reservation);
    }

    @Override
    public void delete(Reservation reservation) {
        fakeDatabase.delete(RESERVATION_TABLE, reservation.getId());
    }

    @Override
    public boolean existByTimeId(Long timeId) {
        return fakeDatabase.readAll(RESERVATION_TABLE, Reservation.class).stream()
                .anyMatch(reservation -> reservation.getTime().getId().equals(timeId));
    }

    @Override
    public boolean existByThemeId(Long themeId) {
        return fakeDatabase.readAll(RESERVATION_TABLE, Reservation.class).stream()
                .anyMatch(reservation -> reservation.getTheme().getId().equals(themeId));
    }

    @Override
    public boolean existBySlot(LocalDate date, Long timeId, Long themeId) {
        return fakeDatabase.readAll(RESERVATION_TABLE, Reservation.class).stream()
                .anyMatch(reservation -> reservation.getDate().equals(date)
                        && reservation.getTime().getId().equals(timeId)
                        && reservation.getTheme().getId().equals(themeId));
    }
}
