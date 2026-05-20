package roomescape.controller.dto.request;

import java.time.LocalTime;
import roomescape.exception.custom.InvalidRequestArgumentException;
import roomescape.service.dto.request.ServiceReservationTimeCreateRequest;

public record ControllerReservationTimeCreateRequest(
        LocalTime startAt
) {

    public ControllerReservationTimeCreateRequest {
        validate(startAt);
    }

    public ServiceReservationTimeCreateRequest toServiceReservationTimeRequest() {
        return new ServiceReservationTimeCreateRequest(startAt);
    }

    private void validate(LocalTime startAt) {
        if (startAt == null) {
            throw new InvalidRequestArgumentException("예약 시간은 비어 있을 수 없습니다.");
        }
    }
}
