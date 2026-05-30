package roomescape.facade;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceReservationTimeCreateRequest;
import roomescape.service.dto.response.ServiceReservationTimeAvailabilityResponse;
import roomescape.service.dto.response.ServiceReservationTimeResponse;

@Service
@Transactional(readOnly = true)
public class ReservationTimeFacade {

    private final ReservationTimeService reservationTimeService;
    private final ReservationService reservationService;
    private final ThemeService themeService;
    private final Clock clock;

    public ReservationTimeFacade(ReservationTimeService reservationTimeService, ReservationService reservationService,
                                 ThemeService themeService, Clock clock) {
        this.reservationTimeService = reservationTimeService;
        this.reservationService = reservationService;
        this.themeService = themeService;
        this.clock = clock;
    }

    @Transactional
    public ServiceReservationTimeResponse create(ServiceReservationTimeCreateRequest request) {
        ReservationTime reservationTimeWithoutId = request.toReservationTime();
        ReservationTime reservationTime = reservationTimeService.create(reservationTimeWithoutId);

        return ServiceReservationTimeResponse.from(reservationTime);
    }

    public List<ServiceReservationTimeResponse> readAll() {
        return reservationTimeService.readAll().stream()
                .map(ServiceReservationTimeResponse::from)
                .toList();
    }

    public List<ServiceReservationTimeAvailabilityResponse> readAvailabilityByDateAndTheme(LocalDate date,
                                                                                           Long themeId) {
        Theme theme = themeService.readTheme(themeId);
        Reservations reservations = reservationService.readAll();

        List<ReservationTime> unavailableTimes = reservations.unavailableTimes(date, LocalDateTime.now(clock), theme);

        return reservationTimeService.readAll().stream()
                .map(reservationTime -> {
                    if (unavailableTimes.contains(reservationTime)) {
                        return ServiceReservationTimeAvailabilityResponse.from(reservationTime, false);
                    }
                    return ServiceReservationTimeAvailabilityResponse.from(reservationTime, true);
                }).toList();
    }

    @Transactional
    public void delete(Long id) {
        reservationService.validateReferencedTime(id);
        reservationTimeService.delete(id);
    }
}
