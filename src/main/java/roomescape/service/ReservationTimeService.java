package roomescape.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationTimes;
import roomescape.domain.Reservations;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.exception.custom.ReservationTimeNotExistsException;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.dto.request.ServiceReservationTimeCreateRequest;
import roomescape.service.dto.response.ServiceReservationTimeAvailabilityResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;

@Service
@Transactional(readOnly = true)
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationTimes reservationTimes;
    private final Clock clock;

    public ReservationTimeService(ReservationTimeRepository reservationTimeRepository, Clock clock) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationTimes = new ReservationTimes(
                reservationTimeRepository.readAll().stream().map(ReservationTimeEntity::toDomain).toList());
        this.clock = clock;
    }

    @Transactional
    public ServiceReservationTimeResponse create(ServiceReservationTimeCreateRequest request) {
        ReservationTime reservationTime = request.toReservationTime();
        reservationTimes.create(reservationTime);

        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(request.toReservationTime());
        return ServiceReservationTimeResponse.from(reservationTimeEntity);
    }

    public List<ServiceReservationTimeResponse> readAll() {
        return reservationTimeRepository.readAll().stream()
                .map(ServiceReservationTimeResponse::from)
                .toList();
    }

    public List<ServiceReservationTimeAvailabilityResponse> readAvailabilityByDateAndTheme(
            Reservations reservations, LocalDate date, ThemeEntity themeEntity) {
        List<ReservationTime> unavailableTimes = reservations.unavailableTimes(date, LocalDateTime.now(clock),
                themeEntity.toDomain());

        return reservationTimeRepository.readAll().stream()
                .map(reservationTimeEntity -> {
                    if (unavailableTimes.contains(reservationTimeEntity.toDomain())) {
                        return ServiceReservationTimeAvailabilityResponse.from(reservationTimeEntity, false);
                    }
                    return ServiceReservationTimeAvailabilityResponse.from(reservationTimeEntity, true);
                }).toList();
    }

    @Transactional
    public void delete(Long id) {
        reservationTimeRepository.delete(id);
    }

    public ReservationTimeEntity readReservationTime(Long timeId) {
        return reservationTimeRepository.read(timeId)
                .orElseThrow(ReservationTimeNotExistsException::new);
    }
}
