package com.mockit.domain.market.exception;

import com.mockit.global.error.code.status.BaseErrorCode;
import com.mockit.global.error.code.status.ErrorReasonDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MarketErrorStatus implements BaseErrorCode {

    SYMBOL_NOT_FOUND(HttpStatus.NOT_FOUND, "MKT_001", "종목을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MarketErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .isSuccess(false)
                .build();
    }
}
