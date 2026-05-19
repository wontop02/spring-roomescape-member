package roomescape.service.dto.response;

import roomescape.entity.ReservationTimeEntity;

public record ServiceReservationTimeAvailabilityResponse(
        ServiceReservationTimeResponse time,
        boolean available
) {
    public static ServiceReservationTimeAvailabilityResponse from(ReservationTimeEntity time, boolean available) {
        return new ServiceReservationTimeAvailabilityResponse(
                ServiceReservationTimeResponse.from(time),
                available);
    }
}
