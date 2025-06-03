package dev.cantrella.ms_code_elevate.api.application.exception;

public abstract class BookApplicationException extends RuntimeException {
    public BookApplicationException(String message) {
        super(message);
    }
}
