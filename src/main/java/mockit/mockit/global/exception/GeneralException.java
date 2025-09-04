package mockit.mockit.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mockit.mockit.global.error.code.status.BaseErrorCode;
import mockit.mockit.global.error.code.status.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
