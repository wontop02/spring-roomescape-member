package roomescape.facade;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservations;
import roomescape.entity.ThemeEntity;
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

    public ReservationTimeFacade(ReservationTimeService reservationTimeService, ReservationService reservationService,
                                 ThemeService themeService) {
        this.reservationTimeService = reservationTimeService;
        this.reservationService = reservationService;
        this.themeService = themeService;
    }

    @Transactional
    public ServiceReservationTimeResponse create(ServiceReservationTimeCreateRequest request) {
        return reservationTimeService.create(request);
    }

    public List<ServiceReservationTimeResponse> readAll() {
        return reservationTimeService.readAll();
    }

    public List<ServiceReservationTimeAvailabilityResponse> readAvailabilityByDateAndTheme(LocalDate date,
                                                                                           Long themeId) {
        ThemeEntity themeEntity = themeService.readTheme(themeId);
        Reservations reservations = reservationService.reservations();

        return reservationTimeService.readAvailabilityByDateAndTheme(reservations, date, themeEntity);
    }

    @Transactional
    public void delete(Long id) {
        reservationService.validateReferencedTime(id);
        reservationTimeService.delete(id);
    }
}
