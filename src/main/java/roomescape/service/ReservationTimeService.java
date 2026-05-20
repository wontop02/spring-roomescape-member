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
import roomescape.domain.Theme;
import roomescape.entity.ReservationTimeEntity;
import roomescape.entity.ThemeEntity;
import roomescape.exception.custom.CannotDeleteReservationTimeInUseException;
import roomescape.exception.custom.ThemeNotExistsException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceReservationTimeCreateRequest;
import roomescape.service.dto.response.ServiceReservationTimeAvailabilityResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;

@Service
@Transactional(readOnly = true)
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public ReservationTimeService(ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository,
                                  ReservationRepository reservationRepository, Clock clock) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    public ReservationTimes makeReservationTimes() {
        return new ReservationTimes(reservationTimeRepository.readAll());
    }

    @Transactional
    public ServiceReservationTimeResponse create(ReservationTimes reservationTimes,
                                                 ServiceReservationTimeCreateRequest request) {
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
            LocalDate date, Long themeId) {
        Theme theme = readTheme(themeId).toDomain();

        Reservations reservations = new Reservations(reservationRepository.readAll());
        List<ReservationTime> unavailableTimes = reservations.unavailableTimes(date, LocalDateTime.now(clock), theme);

        return reservationTimeRepository.readAll().stream()
                .map(reservationTimeEntity -> {
                    if (unavailableTimes.contains(reservationTimeEntity.toDomain())) {
                        return ServiceReservationTimeAvailabilityResponse.from(reservationTimeEntity, false);
                    }
                    return ServiceReservationTimeAvailabilityResponse.from(reservationTimeEntity, true);
                }).toList();
    }

    private ThemeEntity readTheme(Long themeId) {
        return themeRepository.read(themeId)
                .orElseThrow(ThemeNotExistsException::new);
    }

    @Transactional
    public void delete(Long id) {
        validateReferencedTime(id);
        reservationTimeRepository.delete(id);
    }

    private void validateReferencedTime(Long id) {
        if (reservationRepository.existByTimeId(id)) {
            throw new CannotDeleteReservationTimeInUseException();
        }
    }
}
