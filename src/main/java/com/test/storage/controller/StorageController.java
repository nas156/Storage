package com.test.storage.controller;

import com.test.storage.dto.FilteredPagedSearchResponseDTO;
import com.test.storage.dto.ResponseWithSuccessDTO;
import com.test.storage.dto.UploadFileRequestDTO;
import com.test.storage.dto.UploadFileResponseDTO;
import com.test.storage.model.StoredFile;
import com.test.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/file")
public class StorageController {

    final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/all")
    public List<StoredFile> getAll() {
        return storageService.getAllStoredFiles();
    }

    @PostMapping
    public UploadFileResponseDTO uploadFile(@RequestBody @Valid UploadFileRequestDTO fileToUpload) {
        return storageService.uploadFile(fileToUpload);
    }

    @DeleteMapping(value = "/{ID}")
    public ResponseWithSuccessDTO deleteFileById(@PathVariable(value = "ID") String id) {
        return storageService.deleteFileById(id);
    }

    @PostMapping(value = "/{ID}/tags")
    public ResponseWithSuccessDTO assignTags(
            @PathVariable(value = "ID") String id,
            @RequestBody Set<String> tags) {

        return storageService.assignTagsToFileById(id, tags);
    }

    @GetMapping
    public FilteredPagedSearchResponseDTO filteredQuery(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Optional<Set<String>> tags,
            @RequestParam(defaultValue = "", value = "q") String nameFilter) {

        return storageService.filteredAndPagedSearch(tags.orElse(new HashSet<>()), page, size, nameFilter);
    }

    @DeleteMapping("/{ID}/tags")
    public ResponseWithSuccessDTO deleteTagsFromFile(
            @PathVariable(value = "ID") String id,
            @RequestBody Set<String> tags) {

        return storageService.deleteTagsFromFileById(id, tags);
    }
}
