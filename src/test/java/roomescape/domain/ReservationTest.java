package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.custom.CannotCreatePastReservationException;
import roomescape.exception.custom.CannotModifyPastReservationException;
import roomescape.exception.custom.InvalidDomainValueException;
import roomescape.exception.custom.ReservationModificationTimeExpiredException;

public class ReservationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void nameBlankExceptionTest(String name) {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(
                () -> new Reservation(name, LocalDate.of(2026, 5, 2), reservationTime, theme))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void dateNullExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(() -> new Reservation("fizz", null, reservationTime, theme))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void reservationTimeNullExceptionTest() {
        Theme theme = new Theme("피즈의 모험", "모험 이야기", "url.jpg");
        assertThatThrownBy(() -> new Reservation("fizz", LocalDate.of(2026, 5, 2), null, theme))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void themeNullExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        assertThatThrownBy(
                () -> new Reservation("fizz", LocalDate.of(2026, 5, 2), reservationTime, null))
                .isInstanceOf(InvalidDomainValueException.class);
    }

    @Test
    void validateCreateNotPastExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation pastReservation = new Reservation("fizz", LocalDate.of(2025, 5, 2),
                reservationTime, theme);
        assertThatThrownBy(() -> pastReservation.validateNotPast(LocalDateTime.now()))
                .isInstanceOf(CannotCreatePastReservationException.class);
    }

    @Test
    void cannotModifyPastReservationExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation pastReservation = new Reservation("fizz", LocalDate.of(2025, 5, 2),
                reservationTime, theme);
        assertThatThrownBy(
                () -> pastReservation.validateAvailableModify(LocalDateTime.of(2026, 5, 19, 10, 0)))
                .isInstanceOf(CannotModifyPastReservationException.class);
    }

    @Test
    void modificationTimeExpiredExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation reservation = new Reservation("fizz", LocalDate.of(2026, 5, 2), reservationTime, theme);
        assertThatThrownBy(
                () -> reservation.validateAvailableModify(LocalDateTime.of(2026, 5, 2, 9, 30)))
                .isInstanceOf(ReservationModificationTimeExpiredException.class);
    }

    @Test
    void isPastTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출1", "방탈출1 설명", "url.jpg");
        Reservation pastReservation = new Reservation("fizz", LocalDate.of(2025, 5, 2),
                reservationTime, theme);

        assertThat(pastReservation.isPast(LocalDateTime.of(2025, 5, 3, 10, 0))).isTrue();
        assertThat(pastReservation.isPast(LocalDateTime.of(2025, 5, 1, 10, 0))).isFalse();
    }
}
