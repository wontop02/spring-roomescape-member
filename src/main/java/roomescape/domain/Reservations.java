package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reservations {

    private static final int RANKING_LIMIT = 10;

    private final List<Reservation> reservations;

    public Reservations(List<Reservation> allReservations) {
        reservations = new ArrayList<>(allReservations);
    }

    public Stream<Reservation> stream() {
        return reservations.stream();
    }

    public List<ReservationTime> unavailableTimes(LocalDate date, LocalDateTime now, Theme theme) {
        return reservations.stream()
                .filter(reservation -> reservation.getDate().equals(date)
                        && reservation.getTheme().equals(theme)
                        || reservation.isPast(now))
                .map(Reservation::getTime)
                .toList();
    }

    public List<Theme> themeRankingByReservationCounts(RankingPeriod rankingPeriod) {
        Map<Theme, Long> reservationCountsByTheme = reservationCountsByTheme(rankingPeriod);

        List<Theme> ranking = reservationCountsByTheme.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .map(Entry::getKey)
                .toList()
                .reversed();

        if (ranking.size() < RANKING_LIMIT) {
            return ranking;
        }
        return ranking.subList(0, RANKING_LIMIT);
    }

    private Map<Theme, Long> reservationCountsByTheme(RankingPeriod rankingPeriod) {
        return reservations.stream()
                .filter(reservation -> !reservation.isPastDate(rankingPeriod.getStartDate())
                        && !reservation.isFutureDate(rankingPeriod.getEndDate()))
                .collect(Collectors.groupingBy(Reservation::getTheme, Collectors.counting()));
    }
}
