package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReservationsTest {

    @Test
    void unavailableTimesTest() {
        ReservationTime reservationTime1 = new ReservationTime(LocalTime.of(10, 0));
        ReservationTime reservationTime2 = new ReservationTime(LocalTime.of(11, 0));

        Theme theme = new Theme("방탈출", "설명", "url");
        Reservation reservation1 = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime1, theme);
        Reservation reservation2 = new Reservation("fizz", LocalDate.of(2999, 5, 2), reservationTime2, theme);

        Reservations reservations = new Reservations(List.of(reservation1, reservation2));

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

        Reservations reservations = new Reservations(List.of(reservation1, reservation2, reservation3));

        List<Theme> ranking = reservations.themeRankingByReservationCounts(new RankingPeriod(
                LocalDate.of(2999, 5, 2),
                LocalDate.of(2999, 5, 3),
                LocalDate.of(2999, 5, 4)));

        assertThat(ranking.get(0)).isEqualTo(theme1);
        assertThat(ranking.get(1)).isEqualTo(theme2);
    }
}
