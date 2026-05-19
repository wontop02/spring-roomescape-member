package roomescape.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.CustomInvalidDomainException;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

public class ReservationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void nameBlankExceptionTest(String name) {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(
                () -> new Reservation(name, LocalDate.of(2026, 5, 2), reservationTime, theme))
                .isInstanceOf(CustomInvalidDomainException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_NAME_NULL.getMessage());
    }

    @Test
    void dateNullExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(() -> new Reservation("fizz", null, reservationTime, theme))
                .isInstanceOf(CustomInvalidDomainException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_DATE_NULL.getMessage());
    }

    @Test
    void reservationTimeNullExceptionTest() {
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(() -> new Reservation("fizz", LocalDate.of(2026, 5, 2), null, theme))
                .isInstanceOf(CustomInvalidDomainException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_TIME_NULL.getMessage());
    }

    @Test
    void themeNullExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        assertThatThrownBy(
                () -> new Reservation("fizz", LocalDate.of(2026, 5, 2), reservationTime, null))
                .isInstanceOf(CustomInvalidDomainException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_THEME_NULL.getMessage());
    }

    @Test
    void validateNotPastExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation pastReservation = new Reservation("fizz", LocalDate.of(2025, 5, 2),
                reservationTime, theme);
        assertThatThrownBy(
                () -> pastReservation.validateNotPast(LocalDateTime.of(2026, 5, 19, 10, 0)))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_PAST_TIME_RESERVATION_CREATE.getMessage());
    }

    @Test
    void validateAvailableModifyExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation pastReservation = new Reservation("fizz", LocalDate.of(2025, 5, 2),
                reservationTime, theme);
        assertThatThrownBy(
                () -> pastReservation.validateAvailableModify(LocalDateTime.of(2026, 5, 19, 10, 0)))
                .isInstanceOf(CustomInvalidRequestException.class)
                .hasMessage(ErrorCode.NOT_ALLOW_PAST_TIME_RESERVATION_MODIFY.getMessage());
    }
}
