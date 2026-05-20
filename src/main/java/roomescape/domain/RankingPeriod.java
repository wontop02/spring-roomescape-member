package roomescape.domain;

import static roomescape.service.ThemeService.MAX_RANKING_PERIOD;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import roomescape.exception.CustomInvalidDomainException;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

public class RankingPeriod {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public RankingPeriod(LocalDate startDate, LocalDate endDate, LocalDate now) {
        validate(startDate, endDate, now);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validate(LocalDate startDate, LocalDate endDate, LocalDate now) {
        if (startDate == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_RANKING_START_DATE_NULL);
        }
        if (endDate == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_RANKING_END_DATE_NULL);
        }
        if (now == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_NOW_DATE_NULL);
        }
        validatePeriod(now);
    }

    private void validatePeriod(LocalDate now) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
