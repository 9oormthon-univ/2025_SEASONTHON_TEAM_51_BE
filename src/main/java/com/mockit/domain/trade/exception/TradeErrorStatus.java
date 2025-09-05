package com.mockit.domain.trade.exception;

import com.mockit.global.error.code.status.BaseErrorCode;
import com.mockit.global.error.code.status.ErrorReasonDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TradeErrorStatus implements BaseErrorCode {

    INSUFFICIENT_CASH(HttpStatus.BAD_REQUEST, "TRD_001", "현금이 부족합니다."),
    INSUFFICIENT_QTY(HttpStatus.BAD_REQUEST, "TRD_002", "보유 수량이 부족합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "TRD_003", "주문을 찾을 수 없습니다."),
    DUPLICATE_CLIENT_ORDER(HttpStatus.CONFLICT, "TRD_004", "중복 clientOrderId 입니다."),
    INVALID_ORDER_STATE(HttpStatus.BAD_REQUEST, "TRD_005", "유효하지 않은 주문 상태입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TradeErrorStatus(HttpStatus status, String code, String msg) {
        this.httpStatus = status; this.code = code; this.message = msg;
    }

    @Override public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus).code(code).message(message).isSuccess(false).build();
    }
}
