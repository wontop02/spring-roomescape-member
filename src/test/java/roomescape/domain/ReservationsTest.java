package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import roomescape.exception.custom.ReservationAlreadyExistsException;

public class ReservationsTest {

    private Reservations reservations = new Reservations(new ArrayList<>());

    @Test
    void validateUniqueExceptionTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));
        Theme theme = new Theme("방탈출", "설명", "url");
        Reservation reservation = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime, theme);

        reservations.create(reservation, LocalDateTime.now());

        assertThatThrownBy(() -> reservations.create(reservation, LocalDateTime.now()))
                .isInstanceOf(ReservationAlreadyExistsException.class);
    }

    @Test
    void unavailableTimesTest() {
        ReservationTime reservationTime1 = new ReservationTime(LocalTime.of(10, 0));
        ReservationTime reservationTime2 = new ReservationTime(LocalTime.of(11, 0));

        Theme theme = new Theme("방탈출", "설명", "url");
        Reservation reservation1 = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime1, theme);
        Reservation reservation2 = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime2, theme);

        reservations.create(reservation1, LocalDateTime.now());
        reservations.create(reservation2, LocalDateTime.now());

        List<ReservationTime> unavailableTimes = reservations.unavailableTimes(LocalDate.of(2999, 5, 2),
                LocalDateTime.now(), theme);

        assertThat(unavailableTimes.get(0)).isEqualTo(reservationTime1);
        assertThat(unavailableTimes.get(1)).isEqualTo(reservationTime2);
    }

    @Test
    void themeRankingByReservationCountsTest() {
        ReservationTime reservationTime = new ReservationTime(LocalTime.of(10, 0));

        Theme theme1 = new Theme("방탈출1", "설명1", "url1");
        Theme theme2 = new Theme("방탈출2", "설명2", "url2");

        Reservation reservation1 = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime, theme1);
        Reservation reservation2 = new Reservation("fizz", LocalDate.of(2999, 5, 3), reservationTime, theme1);
        Reservation reservation3 = new Reservation("fizz", LocalDate.of(2999, 5, 3), reservationTime, theme2);

        reservations.create(reservation1, LocalDateTime.now());
        reservations.create(reservation2, LocalDateTime.now());
        reservations.create(reservation3, LocalDateTime.now());

        List<Theme> ranking = reservations.themeRankingByReservationCounts(new RankingPeriod(
                LocalDate.of(2999, 5, 2),
                LocalDate.of(2999, 5, 3),
                LocalDate.of(2999, 5, 4)), 10);

        assertThat(ranking.get(0)).isEqualTo(theme1);
        assertThat(ranking.get(1)).isEqualTo(theme2);
    }
}
