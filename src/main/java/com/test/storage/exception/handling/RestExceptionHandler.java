package com.test.storage.exception.handling;

import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.exception.custom.TagNotFoundOnFileException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> responseEntity(Exception exception, HttpStatus status) {
        return new ResponseEntity<>(new ApiErrorDTO(exception.getMessage()), status);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(FileNotFoundException ex) {
        return responseEntity(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotFoundOnFileException.class)
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        return responseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        String textOfError = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        if (textOfError.isEmpty()) {
            textOfError = exception.getMessage();
        }
        return new ResponseEntity<>(new ApiErrorDTO(textOfError), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(new ApiErrorDTO(ex.getMessage()), headers, status);
    }
}
