package roomescape.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.RankingPeriod;
import roomescape.domain.Reservations;
import roomescape.domain.Theme;
import roomescape.entity.ThemeEntity;
import roomescape.exception.custom.ThemeNotExistsException;
import roomescape.repository.ThemeRepository;
import roomescape.service.dto.request.ServiceThemeCreateRequest;
import roomescape.service.dto.response.ServiceThemeResponse;

@Service
@Transactional(readOnly = true)
public class ThemeService {

    public static final int RANKING_LIMIT = 10;
    public static final int MAX_RANKING_PERIOD = 366;

    private final ThemeRepository themeRepository;
    private final Clock clock;

    public ThemeService(ThemeRepository themeRepository, Clock clock) {
        this.themeRepository = themeRepository;
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

    public List<ServiceThemeResponse> readRanking(LocalDate startDate, LocalDate endDate, Reservations reservations) {
        RankingPeriod rankingPeriod = new RankingPeriod(startDate, endDate, LocalDate.now(clock));
        List<Theme> ranking = reservations.themeRankingByReservationCounts(rankingPeriod, RANKING_LIMIT);

        return themeRepository.readAll().stream()
                .filter(themeEntity -> ranking.contains(themeEntity.toDomain()))
                .map(ServiceThemeResponse::from)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        themeRepository.delete(id);
    }

    public ThemeEntity readTheme(Long themeId) {
        return themeRepository.read(themeId)
                .orElseThrow(ThemeNotExistsException::new);
    }
}
