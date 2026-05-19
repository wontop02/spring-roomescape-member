package roomescape.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.domain.ReservationTimes;
import roomescape.entity.ReservationTimeEntity;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;
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

    @Transactional
    public ServiceReservationTimeResponse create(ServiceReservationTimeCreateRequest request) {
        ReservationTimes reservationTimes = new ReservationTimes(readReservationTimes());

        ReservationTime reservationTime = request.toReservationTime();
        reservationTimes.validateCreate(reservationTime);

        ReservationTimeEntity reservationTimeEntity = reservationTimeRepository.create(request.toReservationTime());
        return ServiceReservationTimeResponse.from(reservationTimeEntity);
    }

    public List<ServiceReservationTimeResponse> readAll() {
        return readReservationTimes().stream()
                .map(ServiceReservationTimeResponse::from)
                .toList();
    }

    public List<ServiceReservationTimeAvailabilityResponse> readAvailabilityByDateAndTheme(
            LocalDate date, Long themeId) {
        validateExistTheme(themeId);
        validateNotPastDate(date);

        List<ReservationTimeEntity> allReservationTimeEntities = readReservationTimes();
        List<Long> reservedTimeIdByDateAndTheme = reservationTimeRepository.reservedTimeIdByDateAndTheme(date, themeId);

        return allReservationTimeEntities.stream()
                .map(reservationTime -> {
                    if (reservedTimeIdByDateAndTheme.contains(reservationTime.getId())) {
                        return ServiceReservationTimeAvailabilityResponse.from(reservationTime, false);
                    }
                    return ServiceReservationTimeAvailabilityResponse.from(reservationTime, true);
                }).toList();
    }

    private void validateExistTheme(Long themeId) {
        if (!themeRepository.existById(themeId)) {
            throw new CustomInvalidRequestException(ErrorCode.NOT_FOUND_THEME);
        }
    }

    private void validateNotPastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now(clock))) {
            throw new CustomInvalidRequestException(ErrorCode.PAST_RESERVATION_TIME_READ);
        }
    }

    @Transactional
    public void delete(Long id) {
        validateReferencedTime(id);
        reservationTimeRepository.delete(id);
    }

    private void validateReferencedTime(Long id) {
        if (reservationRepository.existByTimeId(id)) {
            throw new CustomInvalidRequestException(ErrorCode.REFERENCED_TIME);
        }
    }

    private List<ReservationTimeEntity> readReservationTimes() {
        return reservationTimeRepository.readAll();
    }
}
