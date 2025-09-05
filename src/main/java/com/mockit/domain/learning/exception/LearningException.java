package com.mockit.domain.learning.exception;

import com.mockit.global.error.code.status.BaseErrorCode;
import com.mockit.global.exception.GeneralException;

public class LearningException extends GeneralException {

    public LearningException(BaseErrorCode code) { super(code); }
}
