package roomescape.facade;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.RankingPeriod;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;
import roomescape.service.ReservationService;
import roomescape.service.ThemeService;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

@Service
@Transactional(readOnly = true)
public class ThemeFacade {

    private final ThemeService themeService;
    private final ReservationService reservationService;
    private final Clock clock;

    public ThemeFacade(ThemeService themeService, ReservationService reservationService, Clock clock) {
        this.themeService = themeService;
        this.reservationService = reservationService;
        this.clock = clock;
    }

    @Transactional
    public ServiceThemeResponse create(ServiceThemeCreateRequest request) {
        Theme themeWithoutId = request.toTheme();
        Theme theme = themeService.create(themeWithoutId);

        return ServiceThemeResponse.from(theme);
    }

    public List<ServiceThemeResponse> readAll() {
        return themeService.readAll().stream()
                .map(ServiceThemeResponse::from)
                .toList();
    }

    public List<ServiceThemeResponse> readRanking(LocalDate startDate, LocalDate endDate) {
        Reservations reservations = reservationService.readAll();
        RankingPeriod rankingPeriod = new RankingPeriod(startDate, endDate, LocalDate.now(clock));
        List<Theme> ranking = reservations.themeRankingByReservationCounts(rankingPeriod);

        return themeService.readAll().stream()
                .filter(ranking::contains)
                .map(ServiceThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        reservationService.validateReferencedTheme(id);
        themeService.delete(id);
    }
}
