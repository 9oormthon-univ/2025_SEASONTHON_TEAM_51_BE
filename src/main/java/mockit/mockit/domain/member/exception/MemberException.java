package mockit.mockit.domain.member.exception;

import mockit.mockit.global.error.code.status.BaseErrorCode;
import mockit.mockit.global.exception.GeneralException;

public class MemberException extends GeneralException {

    public MemberException(BaseErrorCode code) { super(code); }
}
