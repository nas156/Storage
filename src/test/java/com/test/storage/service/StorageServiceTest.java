package com.test.storage.service;

import com.test.storage.dto.ResponseWithSuccessDTO;
import com.test.storage.dto.UploadFileRequestDTO;
import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.exception.custom.TagNotFoundOnFileException;
import com.test.storage.model.StoredFile;
import com.test.storage.repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    private StorageRepository storageRepository;
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        this.storageRepository = mock(StorageRepository.class);
        this.storageService = new StorageService(storageRepository);
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

    @Test
    public void whenAssignTagsValidId_thenAddTagsAndReturnResponseWithSuccess() {
        Set<String> tagsToAssign = Set.of("tag1", "tag2");
        Set<String> fileTags = new LinkedHashSet<>();
        var uploadedFile = StoredFile.builder()
                .fileName("testfile")
                .fileSize(10L)
                .tags(fileTags)
                .build();
        when(storageRepository.findById(anyString())).thenReturn(Optional.of(uploadedFile));
        ResponseWithSuccessDTO response = storageService.assignTagsToFileById("id", tagsToAssign);
        assertEquals(2, uploadedFile.getTags().size());
        assertTrue(response.getSuccess());
    }

    @Test
    public void whenAssignTagsNotValidId_thenThrowFileNotFoundException() {
        when(storageRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(FileNotFoundException.class, () -> storageService.assignTagsToFileById("id", new LinkedHashSet<>()));
    }

    @Test
    public void whenPagedRequest_thenReturnValidFilteredAndPagedSearchDto() {
        var storedFilesList = new ArrayList<StoredFile>();
        for (int i = 0; i < 5; i++) {
            storedFilesList.add(StoredFile.builder()
                    .fileName("test")
                    .fileSize((long) i)
                    .tags(new LinkedHashSet<>())
                    .build());
        }
        var filesPage = new PageImpl<>(storedFilesList);
        when(storageRepository.findAllByFileNameContaining(anyString(), any())).thenReturn(filesPage);
        var response = storageService.filteredAndPagedSearch(Optional.empty(), 0, 10, "test");
        assertEquals(5, response.getTotal());
        assertEquals(storedFilesList, response.getPage());
    }

    @Test
    public void whenDeletingTagsValid_ThenDeleteTagsAndReturnResponseSuccess() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        var fileTags = new LinkedHashSet<String>();
        fileTags.add(tag1);
        fileTags.add(tag2);
        fileTags.add(tag3);
        var deleteTags = new LinkedHashSet<String>();
        deleteTags.add(tag1);
        deleteTags.add(tag2);
        var storedFile = StoredFile.builder()
                .fileName("test")
                .fileSize(10L)
                .tags(fileTags)
                .build();
        when(storageRepository.findById(anyString())).thenReturn(Optional.of(storedFile));
        var response = storageService.deleteTagsFromFileById("id", deleteTags);
        assertEquals(1, fileTags.size());
        assertTrue(response.getSuccess());
    }

    @Test
    public void whenDeleteTagsNotValid_thenThrowTagNotFoundOnFileException() {
        String tag1 = "tag1";
        String tag2 = "tag2";
        String tag3 = "tag3";
        var fileTags = new LinkedHashSet<String>();
        fileTags.add(tag1);
        fileTags.add(tag3);
        var deleteTags = new LinkedHashSet<String>();
        deleteTags.add(tag1);
        deleteTags.add(tag2);
        var storedFile = StoredFile.builder()
                .fileName("test")
                .fileSize(10L)
                .tags(fileTags)
                .build();
        when(storageRepository.findById(anyString())).thenReturn(Optional.of(storedFile));
        assertThrows(TagNotFoundOnFileException.class, () -> storageService.deleteTagsFromFileById("id", deleteTags));
    }

}
