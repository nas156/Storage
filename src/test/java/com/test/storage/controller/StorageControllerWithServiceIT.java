package com.test.storage.controller;

import com.test.storage.model.StoredFile;
import com.test.storage.repository.StorageRepository;
import com.test.storage.service.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StorageController.class)
@Import(StorageService.class)
public class StorageControllerWithServiceIT {

    @MockBean
    StorageRepository storageRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void whenUploadValid_thenReturnResponseWithId() throws Exception {
        StoredFile storedFile = StoredFile.builder()
                .fileSize(10L)
                .fileName("test.txt")
                .tags(new LinkedHashSet<>())
                .id("id")
                .build();
        when(storageRepository.save(any())).thenReturn(storedFile);
        mockMvc.perform(post("/file")
                .content("{\"name\":\"test.txt\",\"size\": 10}")
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value("id"));
    }

    @Test
    public void whenDeleteValidId_thenReturnSuccessResponse() throws Exception {
        doNothing().when(storageRepository).delete(any());
        when(storageRepository.findById(anyString())).thenReturn(Optional.of(new StoredFile()));
        mockMvc.perform(delete("/file/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void whenDeleteNotValidId_thenReturnErrorResponse() throws Exception {
        String id = "test";
        when(storageRepository.findById(anyString())).thenReturn(Optional.empty());
        mockMvc.perform(delete("/file/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("file not found, id: " + id));
    }


}
