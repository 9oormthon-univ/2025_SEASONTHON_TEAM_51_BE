package com.mockit.domain.member.exception;

import com.mockit.global.exception.GeneralException;
import com.mockit.global.error.code.status.BaseErrorCode;

public class MemberException extends GeneralException {

    public MemberException(BaseErrorCode code) { super(code); }
}
