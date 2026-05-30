package roomescape.domain;

import java.time.LocalTime;
import java.util.Objects;
import roomescape.exception.custom.InvalidDomainValueException;

public class ReservationTime {

    private final Long id;
    private final LocalTime startAt;

    public ReservationTime(LocalTime startAt) {
        this(null, startAt);
    }

    public ReservationTime(Long id, LocalTime startAt) {
        validate(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    private void validate(LocalTime startAt) {
        if (startAt == null) {
            throw new InvalidDomainValueException("예약 시간은 비어 있을 수 없습니다.");
        }
    }

    public boolean isPast(LocalTime localTime) {
        return startAt.isBefore(localTime);
    }

    public LocalTime getStartAt() {
        return startAt;
    }

    public Long getId() {
        return id;
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
