package roomescape.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import roomescape.exception.custom.ReservationTimeAlreadyExistsException;

public class ReservationTimesTest {

    @Test
    void createValidateUniqueExceptionTest() {
        ReservationTimes reservationTimes = new ReservationTimes(new ArrayList<>());

        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        reservationTimes.create(reservationTime);

        assertThatThrownBy(() -> reservationTimes.create(reservationTime))
                .isInstanceOf(ReservationTimeAlreadyExistsException.class);
    }
}
