package com.test.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseWithSuccessDTO {
    private final Boolean success;

    public static ResponseWithSuccessDTO getSuccessResponse() {
        return new ResponseWithSuccessDTO(true);
    }
}
