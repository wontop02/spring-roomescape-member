package roomescape.domain;

import java.time.LocalTime;
import java.util.Objects;
import roomescape.exception.CustomInvalidDomainException;
import roomescape.exception.ErrorCode;

public class ReservationTime {

    private final LocalTime startAt;

    public ReservationTime(LocalTime startAt) {
        validate(startAt);
        this.startAt = startAt;
    }

    private void validate(LocalTime startAt) {
        if (startAt == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_TIME_NULL);
        }
    }

    public boolean isPast(LocalTime localTime) {
        return startAt.isBefore(localTime);
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ReservationTime that = (ReservationTime) object;
        return Objects.equals(startAt, that.startAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(startAt);
    }
}
