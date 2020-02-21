package errors;

import lombok.Getter;
import symbols.ErrorType;

final class LexicalError extends Error
{
    @Getter private final String errorMessage;
    @Getter private final ErrorType errorType;

    public LexicalError(String errorMessage)
    {
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.LexicalError;
    }
}
