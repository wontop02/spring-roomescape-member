package roomescape.service.dto.response;

import java.time.LocalTime;
import roomescape.entity.ReservationTimeEntity;

public record ServiceReservationTimeResponse(
        Long id,
        LocalTime startAt
) {
    public static ServiceReservationTimeResponse from(ReservationTimeEntity reservationTimeEntity) {
        return new ServiceReservationTimeResponse(
                reservationTimeEntity.getId(),
                reservationTimeEntity.getStartAt()
        );
    }
}
