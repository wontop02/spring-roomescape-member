package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.ReservationTime;
import roomescape.entity.ReservationTimeEntity;

public interface ReservationTimeRepository {

    ReservationTimeEntity create(ReservationTime reservationTime);

    Optional<ReservationTimeEntity> read(Long id);

    List<ReservationTimeEntity> readAll();

    void delete(Long id);

    List<Long> reservedTimeIdByDateAndTheme(LocalDate date, Long themeId);
}
