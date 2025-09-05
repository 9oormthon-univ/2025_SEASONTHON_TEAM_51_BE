package com.mockit.domain.trading.exception;

import com.mockit.global.exception.GeneralException;
import com.mockit.global.error.code.status.BaseErrorCode;

public class TradingException extends GeneralException {

    public TradingException(BaseErrorCode code) { super(code); }
}
