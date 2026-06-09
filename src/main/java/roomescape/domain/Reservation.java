package roomescape.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.exception.custom.CannotCreatePastReservationException;
import roomescape.exception.custom.CannotModifyPastReservationException;
import roomescape.exception.custom.InvalidDomainValueException;
import roomescape.exception.custom.ReservationModificationTimeExpiredException;

public class Reservation {

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;

    public Reservation(String name, LocalDate date, ReservationTime time, Theme theme) {
        this(null, name, date, time, theme);
    }

    public Reservation(Long id, String name, LocalDate date, ReservationTime time, Theme theme) {
        validate(name, date, time, theme);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validate(String name, LocalDate date, ReservationTime time, Theme theme) {
        if (name == null || name.isBlank()) {
            throw new InvalidDomainValueException("예약자 이름은 비어 있을 수 없습니다.");
        }
        if (date == null) {
            throw new InvalidDomainValueException("예약 날짜는 비어 있을 수 없습니다.");
        }
        if (time == null) {
            throw new InvalidDomainValueException("예약 시간은 비어 있을 수 없습니다.");
        }
        if (theme == null) {
            throw new InvalidDomainValueException("테마는 비어 있을 수 없습니다.");
        }
    }

    public void validateNotPast(LocalDateTime now) {
        if (isPast(now)) {
            throw new CannotCreatePastReservationException();
        }
    }

    public boolean isSameSlot(Reservation otherReservation) {
        return (date.isEqual(otherReservation.date)) && (time.equals(otherReservation.time)) && (theme.equals(
                otherReservation.theme));
    }

    public void validateAvailableModify(LocalDateTime now) {
        if (isPast(now)) {
            throw new CannotModifyPastReservationException();
        }
        LocalDateTime reservationDateTime = LocalDateTime.of(this.date, this.time.getStartAt());

        boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        long limitHours = 1;
        if (isWeekend) {
            limitHours = 2;
        }

        if (reservationDateTime.isBefore(now.plusHours(limitHours))) {
            throw new ReservationModificationTimeExpiredException();
        }
    }

    public boolean isPast(LocalDateTime now) {
        LocalDate nowDate = now.toLocalDate();
        LocalTime nowTime = now.toLocalTime();

        if (isPastDate(nowDate)) {
            return true;
        }
        if (isFutureDate(nowDate)) {
            return false;
        }
        return time.isPast(nowTime);
    }

    public boolean isPastDate(LocalDate otherDate) {
        return date.isBefore(otherDate);
    }

    public boolean isFutureDate(LocalDate otherDate) {
        return date.isAfter(otherDate);
    }

    public Long getId() {
        return id;
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
        return Objects.equals(name, that.name) && Objects.equals(date, that.date)
                && Objects.equals(time, that.time) && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, time, theme);
    }
}
