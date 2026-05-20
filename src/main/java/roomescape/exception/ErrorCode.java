package roomescape.exception;

public enum ErrorCode {

    //    NOT_FOUND_RESERVATION("[ERROR] 해당 ID의 예약을 찾을 수 없습니다."),
//    DUPLICATED_RESERVATION("[ERROR] 해당 시간에 예약이 이미 존재합니다."),
//    NOT_ALLOW_PAST_TIME_RESERVATION_CREATE("[ERROR] 지나간 시간에는 예약할 수 없습니다."),
//    NOT_ALLOW_PAST_TIME_RESERVATION_MODIFY("[ERROR] 지나간 시간의 예약은 수정, 삭제할 수 없습니다."),
//
//    NOT_FOUND_RESERVATION_TIME("[ERROR] 해당 ID의 예약 시간을 찾을 수 없습니다."),
//    DUPLICATED_RESERVATION_TIME("[ERROR] 동일한 예약 시간이 이미 존재합니다."),
//    REFERENCED_TIME("[ERROR] 현재 해당 예약 시간을 사용하는 예약이 존재합니다."),
//
//    NOT_FOUND_THEME("[ERROR] 해당 ID의 테마를 찾을 수 없습니다."),
//    REFERENCED_THEME("[ERROR] 현재 해당 테마를 사용하는 예약이 존재합니다."),
//    FUTURE_RANKING_PERIOD("[ERROR] 오늘 날짜 이전까지만 랭킹 조회가 가능합니다."),
//    INVALID_RANKING_PERIOD("[ERROR] 종료 날짜가 시작 날짜보다 빠릅니다."),
//    LONG_RANKING_PERIOD(
//            String.format("[ERROR] 조회 기간이 최대 기간을 초과했습니다. 기간이 1년(%s일) 이내가 되도록 다시 요청해 주세요.", MAX_RANKING_PERIOD)),
//
//    INVALID_METHOD_REQUEST("[ERROR] 지원하지 않는 메서드입니다."),
//    INVALID_URL_REQUEST("[ERROR] 잘못된 경로입니다."),
//    INVALID_JSON_REQUEST("[ERROR] 요청 본문(JSON)의 형식이 올바르지 않거나 읽을 수 없습니다."),
//    TYPE_MISMATCH_REQUEST("[ERROR] 요청 파라미터 또는 경로 변수의 타입이 올바르지 않습니다."),

    INTERNAL_SERVER_ERROR("[ERROR] 서버 내부에서 에러가 발생했습니다."),

//    NOT_ALLOW_NAME_NULL("[ERROR] 이름은 비어 있을 수 없습니다."),
//    NOT_ALLOW_DATE_NULL("[ERROR] 날짜는 비어 있을 수 없습니다."),
//    NOT_ALLOW_TIME_NULL("[ERROR] 예약 시간은 비어 있을 수 없습니다."),
//    NOT_ALLOW_THEME_NULL("[ERROR] 테마는 비어 있을 수 없습니다."),
//    NOT_ALLOW_DESCRIPTION_NULL("[ERROR] 설명은 비어 있을 수 없습니다."),
//    NOT_ALLOW_THUMBNAIL_NULL("[ERROR] 썸네일은 비어 있을 수 없습니다."),
//    NOT_ALLOW_RANKING_START_DATE_NULL("[ERROR] 랭킹 조회 시작 날짜는 비어 있을 수 없습니다."),
//    NOT_ALLOW_RANKING_END_DATE_NULL("[ERROR] 랭킹 조회 종료 날짜는 비어 있을 수 없습니다."),
//    NOT_ALLOW_NOW_DATE_NULL("[ERROR] 현재 날짜는 비어 있을 수 없습니다."),
    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
