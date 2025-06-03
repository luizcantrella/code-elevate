package dev.cantrella.ms_code_elevate.api.infra.adapter.in.web.exception;

import dev.cantrella.ms_code_elevate.api.application.exception.BookApplicationException;
import dev.cantrella.ms_code_elevate.api.application.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ProblemDetail handleBookNotFoundException(BookNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                e.getMessage()
        );
    }
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUncaughtException(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno no servidor"
        );
        problem.setTitle("Internal Server Error");

        return problem;
    }
}
