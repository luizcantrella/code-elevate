package dev.cantrella.ms_code_elevate.api.infra.adapter.in.web;

import dev.cantrella.ms_code_elevate.api.application.exception.BookNotFoundException;
import dev.cantrella.ms_code_elevate.api.infra.adapter.in.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = mock(WebRequest.class);

    @Test
    void handleBookNotFoundException_shouldReturnNotFoundProblemDetail() {
        String bookId = "1";
        BookNotFoundException exception = new BookNotFoundException(bookId);

        ProblemDetail result = handler.handleBookNotFoundException(exception);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatus());
        assertEquals("Book with id " + bookId + " not found.", result.getDetail());
    }

    @Test
    void handleUncaughtException_shouldReturnInternalServerErrorProblemDetail() {
        Exception exception = new Exception("Unexpected error");

        ProblemDetail result = handler.handleUncaughtException(exception, webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Ocorreu um erro interno no servidor", result.getDetail());
        assertEquals("Internal Server Error", result.getTitle());
    }

    @Test
    void handleUncaughtException_shouldUseGenericMessageWhenExceptionMessageIsNull() {
        Exception exception = new Exception();

        ProblemDetail result = handler.handleUncaughtException(exception, webRequest);

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Ocorreu um erro interno no servidor", result.getDetail());
    }
}