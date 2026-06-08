package roomescape.exception.custom;

public class ReservationModificationTimeExpiredException extends RuntimeException {

    public ReservationModificationTimeExpiredException() {
        super("예약 수정, 삭제가 가능한 시간이 지났습니다.");
    }
}
