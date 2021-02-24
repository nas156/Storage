package com.test.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UploadFileRequestDTO {

    @Pattern(regexp = "^[\\wА-Яа-яі]+[\\wА-Яа-яі|\\s-]*\\.[A-Za-z0-9]+$", message = "wrong file name format, should be name.ext")
    @NotEmpty(message = "name of file must not be empty")
    private String name;

    @Min(value = 1, message = "size of file must be larger then 0")
    private Long size;

}
