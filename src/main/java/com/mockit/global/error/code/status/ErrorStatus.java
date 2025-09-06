package com.mockit.global.error.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    STOCK_PRICE_FETCH_FAILED(HttpStatus.BAD_REQUEST, "TRADE400", "가격이 없습니다."),
    PRICE_REQUIRED_FOR_LIMIT_ORDER(HttpStatus.BAD_REQUEST, "TRADE401", "지정가는 없을 수 없습니다."),
    STOCK_NOT_FOUND(HttpStatus.BAD_REQUEST, "TRADE402", "해당 Stock을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "TRADE403", "주문을 찾을 수 없습니다."),
    ORDER_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "TRADE404", "주문자가 아닙니다."),
    ORDER_ALREADY_FILLED_OR_CANCELED(HttpStatus.BAD_REQUEST, "TRADE405", "이미 체결되거나 취소된 주문입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
