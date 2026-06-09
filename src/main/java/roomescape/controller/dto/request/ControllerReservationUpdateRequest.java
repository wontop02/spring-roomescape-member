package roomescape.controller.dto.request;

import java.time.LocalDate;
import roomescape.exception.custom.InvalidRequestArgumentException;
import roomescape.service.dto.request.ServiceReservationUpdateRequest;

public record ControllerReservationUpdateRequest(
        LocalDate date,
        Long timeId
) {

    public ControllerReservationUpdateRequest {
        validate(date, timeId);
    }

    public ServiceReservationUpdateRequest toServiceReservationRequest() {
        return new ServiceReservationUpdateRequest(date, timeId);
    }

    private void validate(LocalDate date, Long timeId) {
        if (date == null) {
            throw new InvalidRequestArgumentException("예약 날짜는 비어 있을 수 없습니다.");
        }
        if (timeId == null) {
            throw new InvalidRequestArgumentException("예약 시간은 비어 있을 수 없습니다.");
        }
    }
}
