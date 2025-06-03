package dev.cantrella.ms_code_elevate.api.application.exception;

public class BookNotFoundException extends BookApplicationException{
    public BookNotFoundException(String id) {
        super("Book with id " + id + " not found.");
    }
}
