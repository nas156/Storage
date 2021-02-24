package com.test.storage.service;

import com.test.storage.dto.UploadFileRequestDTO;
import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.model.StoredFile;
import com.test.storage.repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    private StorageRepository storageRepository;
    private StorageService storageService;
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setUp() {
        this.storageRepository = mock(StorageRepository.class);
        this.elasticsearchOperations = mock(ElasticsearchOperations.class);
        this.storageService = new StorageService(storageRepository, elasticsearchOperations);
    }

    @Test
    public void whenDeleteNotPresentFile_thenThrowFileNotFoundException() {
        when(storageRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> storageService.deleteFileById(" "));
    }

    @Test
    public void whenDeleteValid_thenReturnResponseWithSuccess() {
        var storedFile = StoredFile.builder()
                .fileName("testfile")
                .fileSize(10L)
                .tags(new HashSet<>())
                .build();
        when(storageRepository.findById(anyString())).thenReturn(Optional.of(storedFile));
        doNothing().when(storageRepository).delete(any(StoredFile.class));
        assertTrue(storageService.deleteFileById("any").getSuccess());
    }

    @Test
    public void whenUpload_thenReturnValidId() {
        String expectedId = "id";
        var uploadedFile = StoredFile.builder()
                .fileName("testname")
                .fileSize(10L)
                .id(expectedId)
                .tags(new HashSet<>())
                .build();
        var uploadFileRequest = new UploadFileRequestDTO("aaa", 10L);
        when(storageRepository.save(any(StoredFile.class))).thenReturn(uploadedFile);
        assertEquals(expectedId, storageService.uploadFile(uploadFileRequest).getID());
    }

}
