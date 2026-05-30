package roomescape.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import roomescape.exception.custom.InvalidDomainValueException;
import roomescape.exception.custom.RankingPeriodEndDateBeforeStartDateException;
import roomescape.exception.custom.RankingPeriodExceedsLimitException;
import roomescape.exception.custom.RankingPeriodPastDateOnlyException;

public class RankingPeriod {

    public static final int MAX_RANKING_PERIOD = 366;

    private final LocalDate startDate;
    private final LocalDate endDate;

    public RankingPeriod(LocalDate startDate, LocalDate endDate, LocalDate now) {
        validate(startDate, endDate, now);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validate(LocalDate startDate, LocalDate endDate, LocalDate now) {
        if (startDate == null) {
            throw new InvalidDomainValueException("랭킹 조회 시작 날짜는 비어 있을 수 없습니다.");
        }
        if (endDate == null) {
            throw new InvalidDomainValueException("랭킹 조회 종료 날짜는 비어 있을 수 없습니다.");
        }
        validatePeriod(startDate, endDate, now);
    }

    private void validatePeriod(LocalDate startDate, LocalDate endDate, LocalDate now) {
        if (startDate.isAfter(endDate)) {
            throw new RankingPeriodEndDateBeforeStartDateException();
        }
        if (!endDate.isBefore(now)) {
            throw new RankingPeriodPastDateOnlyException();
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > MAX_RANKING_PERIOD) {
            throw new RankingPeriodExceedsLimitException();
        }
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
