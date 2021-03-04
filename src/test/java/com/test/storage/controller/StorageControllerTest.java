package com.test.storage.controller;

import com.test.storage.dto.FilteredPagedSearchResponseDTO;
import com.test.storage.dto.ResponseWithSuccessDTO;
import com.test.storage.dto.UploadFileResponseDTO;
import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.exception.custom.TagNotFoundOnFileException;
import com.test.storage.model.StoredFile;
import com.test.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StorageController.class)
public class StorageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StorageService storageService;

    @Test
    public void whenUploadValid_thenReturnResponseWithId() throws Exception {
        String id = "id";
        var response = new UploadFileResponseDTO(id);
        when(storageService.uploadFile(any())).thenReturn(response);
        mockMvc.perform(post("/file")
                .content("{\"name\":\"test.txt\",\"size\": 10}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    public void whenUploadEmptyFileName_thenReturnErrorResponse() throws Exception {
        mockMvc.perform(post("/file")
                .content("{\"name\":\"\",\"size\": 10}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("name of file must not be empty"));
    }

    @Test
    public void whenUploadSizeLessThanZero_thenReturnErrorResponse() throws Exception {
        mockMvc.perform(post("/file")
                .content("{\"name\":\"test\",\"size\": -1}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("size of file must not be less than 0"));
    }

    @Test
    public void whenUploadNullSizeOfFile_thenReturnErrorResponse() throws Exception {
        mockMvc.perform(post("/file")
                .content("{\"name\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("size of file must not be null"));
    }

    @Test
    public void whenUploadNullNameOfFile_thenReturnErrorResponse() throws Exception {
        mockMvc.perform(post("/file")
                .content("{\"size\": 10}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("name of file must not be null, name of file must not be empty"));
    }

    @Test
    public void whenDeleteValidId_thenReturnSuccessResponse() throws Exception {
        when(storageService.deleteFileById(anyString())).thenReturn(ResponseWithSuccessDTO.getSuccessResponse());
        mockMvc.perform(delete("/file/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void whenDeleteNotValidId_thenReturnErrorResponse() throws Exception {
        String id = "test";
        when(storageService.deleteFileById(anyString())).thenThrow(new FileNotFoundException(id));
        mockMvc.perform(delete("/file/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("file not found, id: " + id));
    }

    @Test
    public void whenAssignTagsValid_thenReturnSuccessResponse() throws Exception {
        when(storageService.assignTagsToFileById(anyString(), anySet()))
                .thenReturn(ResponseWithSuccessDTO.getSuccessResponse());
        mockMvc.perform(post("/file/test/tags")
                .content("[1,2,3]")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void whenRemoveTagsFromFileValid_thenReturnSuccessResponse() throws Exception {
        when(storageService.deleteTagsFromFileById(anyString(), anySet()))
                .thenReturn(ResponseWithSuccessDTO.getSuccessResponse());
        mockMvc.perform(delete("/file/test/tags")
                .content("[1,2,3]")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void whenRemoveNotValidTags_thenReturnErrorResponse() throws Exception {
        String id = "test";
        var tags = Set.of("tag1", "tag2");
        when(storageService.deleteTagsFromFileById(anyString(), anySet()))
                .thenThrow(new TagNotFoundOnFileException("test", tags));
        mockMvc.perform(delete("/file/" + id + "/tags")
                .content("[]")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("tags: [tag1, tag2] not found on file with id: test"));
    }

    @Test
    public void whenFilteredRequestValid_thenReturnFilteredRagedResponseDto() throws Exception {
        when(storageService.filteredAndPagedSearch(any(), anyInt(), anyInt(), anyString()))
                .thenReturn(new FilteredPagedSearchResponseDTO(5L, new ArrayList<>()));
        mockMvc.perform(get("/file"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.page").isArray());
    }
}
