package roomescape.controller.dto.request;

import java.time.LocalDate;
import roomescape.exception.custom.InvalidRequestArgumentException;
import roomescape.service.dto.request.ServiceReservationCreateRequest;

public record ControllerReservationCreateRequest(
        String name,
        LocalDate date,
        Long timeId,
        Long themeId
) {

    public ControllerReservationCreateRequest {
        validate(name, date, timeId, themeId);
    }

    public ServiceReservationCreateRequest toServiceReservationRequest() {
        return new ServiceReservationCreateRequest(name, date, timeId, themeId);
    }

    private void validate(String name, LocalDate date, Long timeId, Long themeId) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestArgumentException("예약자 이름은 비어 있을 수 없습니다.");
        }
        if (date == null) {
            throw new InvalidRequestArgumentException("예약 날짜는 비어 있을 수 없습니다.");
        }
        if (timeId == null) {
            throw new InvalidRequestArgumentException("예약 시간은 비어 있을 수 없습니다.");
        }
        if (themeId == null) {
            throw new InvalidRequestArgumentException("테마는 비어 있을 수 없습니다.");
        }
    }
}
