package roomescape.exception.custom;

public class ReservationAlreadyExistsException extends CustomException {

    public ReservationAlreadyExistsException() {
        super("이미 중복된 예약이 존재합니다.");
    }
}
