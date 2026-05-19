package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;
import roomescape.entity.ReservationEntity;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;

public interface ReservationRepository {

    ReservationEntity create(Reservation reservation, ReservationTimeEntity timeEntity, ThemeEntity themeEntity);

    Optional<ReservationEntity> readById(Long id);

    List<ReservationEntity> readByName(String name);

    List<ReservationEntity> readAll();

    void update(Long id, LocalDate date, Long timeId);

    void delete(Long id);

    boolean existByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    boolean existByTimeId(Long timeId);

    boolean existByThemeId(Long themeId);
}
