package roomescape.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import roomescape.entity.ReservationEntity;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

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
            throw new CustomInvalidRequestException(ErrorCode.DUPLICATED_RESERVATION);
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
}
