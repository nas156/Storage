package com.test.storage.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TagNotFoundOnFileException extends RuntimeException {
    private final String id;
    private final Set<String> missingTags;

    public TagNotFoundOnFileException(String id, Set<String> tags) {
        this.id = id;
        this.missingTags = tags;
    }

    public String getMessage() {
        return String.format("tags: %s not found on file with id: %s", missingTags.toString(), id);
    }

}
