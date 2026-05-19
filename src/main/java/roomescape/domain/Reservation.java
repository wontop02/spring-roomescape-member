package roomescape.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.exception.CustomInvalidDomainException;
import roomescape.exception.CustomInvalidRequestException;
import roomescape.exception.ErrorCode;

public class Reservation {

    private final String name;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    public Reservation(String name, LocalDate date, ReservationTime time, Theme theme) {
        validate(name, date, time, theme);
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validate(String name, LocalDate date, ReservationTime time, Theme theme) {
        if (name == null || name.isBlank()) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_NAME_NULL);
        }
        if (date == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_DATE_NULL);
        }
        if (time == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_TIME_NULL);
        }
        if (theme == null) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_THEME_NULL);
        }
    }

    public void validateNotPast(LocalDateTime localDateTime) {
        if (isPast(localDateTime)) {
            throw new CustomInvalidRequestException(ErrorCode.NOT_ALLOW_PAST_TIME_RESERVATION_CREATE);
        }
    }

    public void validateAvailableModify(LocalDateTime localDateTime) {
        if (isPast(localDateTime)) {
            throw new CustomInvalidRequestException(ErrorCode.NOT_ALLOW_PAST_TIME_RESERVATION_MODIFY);
        }
    }

    private boolean isPast(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        if (date.isBefore(nowDate)) {
            return true;
        }
        if (date.isAfter(nowDate)) {
            return false;
        }
        return time.isPast(nowTime);
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Reservation that = (Reservation) object;
        return Objects.equals(date, that.date) && Objects.equals(time, that.time)
                && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, theme);
    }
}
