package com.test.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UploadFileRequestDTO {

    @Pattern(regexp = "^(?:[\\wА-Яа-яі.]+[\\wА-Яа-яі|\\s-]*)?(?:\\.[A-Za-z0-9]+)?$",
            message = "wrong file name format, should be name.ext or .ext or name")
    @NotEmpty(message = "name of file must not be empty")
    @NotNull(message = "name of file must not be null")
    private String name;

    @Min(value = 0, message = "size of file must not be less than 0")
    @NotNull(message = "size of file must not be null")
    private Long size;

}
