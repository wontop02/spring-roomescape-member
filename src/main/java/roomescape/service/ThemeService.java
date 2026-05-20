package roomescape.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.RankingPeriod;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

@Service
@Transactional(readOnly = true)
public class ThemeService {

    public static final int RANKING_LIMIT = 10;
    public static final int MAX_RANKING_PERIOD = 366;

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public ThemeService(ThemeRepository themeRepository, ReservationRepository reservationRepository, Clock clock) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    @Transactional
    public ServiceThemeResponse create(ServiceThemeCreateRequest requestDto) {
        Theme theme = requestDto.toTheme();
        return ServiceThemeResponse.from(themeRepository.create(theme));
    }

    public List<ServiceThemeResponse> readAll() {
        return themeRepository.readAll().stream()
                .map(ServiceThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        validateReferencedTheme(id);
        themeRepository.delete(id);
    }

    private void validateReferencedTheme(Long id) {
        if (reservationRepository.existByThemeId(id)) {
            throw new CustomInvalidRequestException(ErrorCode.REFERENCED_THEME);
        }
    }

    public List<ServiceThemeResponse> readRanking(LocalDate startDate, LocalDate endDate) {
        RankingPeriod rankingPeriod = new RankingPeriod(startDate, endDate, LocalDate.now(clock));
        Reservations reservations = new Reservations(reservationRepository.readAll());
        List<Theme> ranking = reservations.themeRankingByReservationCounts(rankingPeriod, RANKING_LIMIT);

        return themeRepository.readAll().stream()
                .filter(themeEntity -> ranking.contains(themeEntity.toDomain()))
                .map(ServiceThemeResponse::from)
                .toList();
    }
}
