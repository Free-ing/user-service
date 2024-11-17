package Freeing.user_service.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateAnswerException extends RuntimeException {
    public DuplicateAnswerException(String message) {
        super(message);
    }
}