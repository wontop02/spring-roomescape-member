package roomescape.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface ReservationRepository {

    Reservation create(Reservation reservation);

    Optional<Reservation> readById(Long id);

    Reservations readByName(String name);

    Reservations readAll();

    void update(Reservation reservation);

    void delete(Reservation reservation);

    boolean existByTimeId(Long timeId);

    boolean existByThemeId(Long themeId);

    boolean existBySlot(LocalDate date, Long timeId, Long themeId);
}
