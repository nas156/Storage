package com.test.storage.exception.handling;


import com.test.storage.dto.ResponseWithSuccessDTO;
import lombok.Getter;


public class ApiErrorDTO extends ResponseWithSuccessDTO {
    @Getter
    private final String error;

    public ApiErrorDTO(String error) {
        super(false);
        this.error = error;
    }
}
