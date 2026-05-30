package roomescape.domain;

import java.util.ArrayList;
import java.util.List;
import roomescape.exception.custom.ReservationTimeAlreadyExistsException;

public class ReservationTimes {

    private final List<ReservationTime> reservationTimes;

    public ReservationTimes(List<ReservationTime> allReservationTimes) {
        reservationTimes = new ArrayList<>(allReservationTimes);
    }

    public void create(ReservationTime newReservationTime) {
        validateCreate(newReservationTime);
        reservationTimes.add(newReservationTime);
    }

    private void validateCreate(ReservationTime newReservationTime) {
        validateUnique(newReservationTime);
    }

    private void validateUnique(ReservationTime newReservationTime) {
        boolean isDuplicated = reservationTimes.stream()
                .anyMatch(reservationTime -> reservationTime.equals(newReservationTime));
        if (isDuplicated) {
            throw new ReservationTimeAlreadyExistsException();
        }
    }
}
