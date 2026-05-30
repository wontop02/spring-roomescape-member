package roomescape.facade;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservations;
import roomescape.service.ReservationService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

@Service
@Transactional(readOnly = true)
public class ThemeFacade {

    private final ThemeService themeService;
    private final ReservationService reservationService;

    public ThemeFacade(ThemeService themeService, ReservationService reservationService, Clock clock) {
        this.themeService = themeService;
        this.reservationService = reservationService;
    }

    @Transactional
    public ServiceThemeResponse create(ServiceThemeCreateRequest request) {
        return themeService.create(request);
    }

    public List<ServiceThemeResponse> readAll() {
        return themeService.readAll();
    }

    public List<ServiceThemeResponse> readRanking(LocalDate startDate, LocalDate endDate) {
        Reservations reservations = reservationService.reservations();
        return themeService.readRanking(startDate, endDate, reservations);
    }

    @Transactional
    public void delete(Long id) {
        reservationService.validateReferencedTheme(id);
        themeService.delete(id);
    }
}
