package com.test.storage.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {
    private final String id;

    public FileNotFoundException(String id) {
        this.id = id;
    }

    @Override
    public String getMessage(){
        return "file not found, id: " + id;
    }
}
