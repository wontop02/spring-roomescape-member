package roomescape.domain;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {

    ReservationTime create(ReservationTime reservationTime);

    Optional<ReservationTime> readById(Long id);

    List<ReservationTime> readAll();

    void delete(ReservationTime reservationTime);

    boolean existByStartAt(LocalTime startAt);
}
