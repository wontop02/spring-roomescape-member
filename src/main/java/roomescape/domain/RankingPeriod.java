package roomescape.domain;

import static roomescape.service.ThemeService.MAX_RANKING_PERIOD;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

public class RankingPeriod {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public RankingPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void validatePeriod(LocalDate now) {
        if (startDate.isAfter(endDate)) {
            throw new CustomInvalidRequestException(ErrorCode.INVALID_RANKING_PERIOD);
        }
        if (!endDate.isBefore(now)) {
            throw new CustomInvalidRequestException(ErrorCode.FUTURE_RANKING_PERIOD);
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > MAX_RANKING_PERIOD) {
            throw new CustomInvalidRequestException(ErrorCode.LONG_RANKING_PERIOD);
        }
    }
}
