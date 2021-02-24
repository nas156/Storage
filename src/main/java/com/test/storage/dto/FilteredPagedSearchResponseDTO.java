package com.test.storage.dto;

import com.test.storage.model.StoredFile;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilteredPagedSearchResponseDTO {
    private Long total;
    private List<StoredFile> page;
}
