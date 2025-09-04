package mockit.mockit.domain.trading.exception;

import mockit.mockit.global.error.code.status.BaseErrorCode;
import mockit.mockit.global.exception.GeneralException;

public class TradingException extends GeneralException {

    public TradingException(BaseErrorCode code) { super(code); }
}
