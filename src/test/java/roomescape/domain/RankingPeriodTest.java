package roomescape.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import roomescape.exception.custom.InvalidDomainValueException;
import roomescape.exception.custom.RankingPeriodEndDateBeforeStartDateException;
import roomescape.exception.custom.RankingPeriodExceedsLimitException;
import roomescape.exception.custom.RankingPeriodPastDateOnlyException;

public class RankingPeriodTest {

    @Test
    void validateStartDateNotNullException() {
        assertThatThrownBy(() -> new RankingPeriod(null, LocalDate.of(2002, 5, 2), LocalDate.now()))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void validateEndDateNotNullException() {
        assertThatThrownBy(() -> new RankingPeriod(LocalDate.of(2002, 5, 2), null, LocalDate.now()))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void rankingPeriodEndDateBeforeStartDateExceptionTest() {
        assertThatThrownBy(() -> new RankingPeriod(LocalDate.of(2002, 5, 2), LocalDate.of(2002, 5, 1), LocalDate.now()))
                .isInstanceOf(RankingPeriodEndDateBeforeStartDateException.class);
    }

    @Test
    void rankingPeriodPastDateOnlyExceptionTest() {
        assertThatThrownBy(() -> new RankingPeriod(LocalDate.now().minusDays(1), LocalDate.now(), LocalDate.now()))
                .isInstanceOf(RankingPeriodPastDateOnlyException.class);
    }

    @Test
    void rankingPeriodExceedsLimitExceptionTest() {
        assertThatThrownBy(() -> new RankingPeriod(LocalDate.of(2000, 5, 2), LocalDate.of(2002, 5, 1), LocalDate.now()))
                .isInstanceOf(RankingPeriodExceedsLimitException.class);
    }
}
