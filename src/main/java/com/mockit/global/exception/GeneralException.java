package com.mockit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mockit.global.error.code.status.BaseErrorCode;
import com.mockit.global.error.code.status.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
