package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import roomescape.entity.ReservationEntity;
import roomescape.exception.custom.ReservationAlreadyExistsException;

public class Reservations {

    private final List<Reservation> reservations;

    public Reservations(List<ReservationEntity> allReservations) {
        reservations = new ArrayList<>(allReservations.stream()
                .map(ReservationEntity::toDomain)
                .toList());
    }

    public void create(Reservation newReservation, LocalDateTime localDateTime) {
        validateCreate(newReservation, localDateTime);
        reservations.add(newReservation);
    }

    private void validateCreate(Reservation newReservation, LocalDateTime localDateTime) {
        newReservation.validateNotPast(localDateTime);
        validateUnique(newReservation);
    }

    private void validateUnique(Reservation newReservation) {
        boolean isDuplicated = reservations.stream()
                .anyMatch(reservation -> reservation.equals(newReservation));
        if (isDuplicated) {
            throw new ReservationAlreadyExistsException();
        }
    }

    public void update(Reservation beforeReservation, Reservation newReservation, LocalDateTime localDateTime) {
        validateUpdate(beforeReservation, newReservation, localDateTime);
        reservations.remove(beforeReservation);
        reservations.add(newReservation);
    }

    private void validateUpdate(Reservation beforeReservation, Reservation newReservation,
                                LocalDateTime localDateTime) {
        newReservation.validateNotPast(localDateTime);
        beforeReservation.validateAvailableModify(localDateTime);
        // 이전 예약과 새 예약이 같을 때는, 중복 예외 발생하지 않도록
        if (!beforeReservation.equals(newReservation)) {
            validateUnique(newReservation);
        }
    }

    public void delete(Reservation deleteReservation, LocalDateTime localDateTime) {
        validateDelete(deleteReservation, localDateTime);
        reservations.remove(deleteReservation);
    }

    private void validateDelete(Reservation deleteReservation, LocalDateTime localDateTime) {
        deleteReservation.validateNotPast(localDateTime);
    }

    public List<ReservationTime> unavailableTimes(LocalDate date, LocalDateTime now, Theme theme) {
        return reservations.stream()
                .filter(reservation -> reservation.getDate().equals(date)
                        && reservation.getTheme().equals(theme)
                        && reservation.isPast(now))
                .map(Reservation::getTime)
                .toList();
    }

    public List<Theme> themeRankingByReservationCounts(RankingPeriod rankingPeriod, int limit) {
        Map<Theme, Long> reservationCountsByTheme = reservationCountsByTheme(rankingPeriod);

        List<Theme> ranking = reservationCountsByTheme.entrySet().stream()
                .sorted(Entry.comparingByValue())
                .map(Entry::getKey)
                .toList()
                .reversed();

        if (ranking.size() < limit) {
            return ranking;
        }
        return ranking.subList(0, limit);
    }

    private Map<Theme, Long> reservationCountsByTheme(RankingPeriod rankingPeriod) {
        return reservations.stream()
                .filter(reservation -> !reservation.isPastDate(rankingPeriod.getStartDate())
                        && !reservation.isFutureDate(rankingPeriod.getEndDate()))
                .collect(Collectors.groupingBy(Reservation::getTheme, Collectors.counting()));
    }
}
