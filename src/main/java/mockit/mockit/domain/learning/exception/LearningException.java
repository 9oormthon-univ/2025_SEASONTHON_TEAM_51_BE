package mockit.mockit.domain.learning.exception;

import mockit.mockit.global.error.code.status.BaseErrorCode;
import mockit.mockit.global.exception.GeneralException;

public class LearningException extends GeneralException {

    public LearningException(BaseErrorCode code) { super(code); }
}
