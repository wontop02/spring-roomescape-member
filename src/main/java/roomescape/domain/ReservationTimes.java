package roomescape.domain;

import java.util.List;
import roomescape.entity.ReservationTimeEntity;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

public class ReservationTimes {

    private final List<ReservationTime> reservationTimes;

    public ReservationTimes(List<ReservationTimeEntity> allReservationTimes) {
        reservationTimes = allReservationTimes.stream()
                .map(ReservationTimeEntity::toDomain)
                .toList();
    }

    public void validateCreate(ReservationTime newReservationTime) {
        validateUnique(newReservationTime);
    }

    private void validateUnique(ReservationTime newReservationTime) {
        boolean isDuplicated = reservationTimes.stream()
                .anyMatch(reservationTime -> reservationTime.equals(newReservationTime));
        if (isDuplicated) {
            throw new CustomInvalidRequestException(ErrorCode.DUPLICATED_RESERVATION_TIME);
        }
    }
}
