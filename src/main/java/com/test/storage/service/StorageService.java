package com.test.storage.service;

import com.test.storage.dto.FilteredPagedSearchResponseDTO;
import com.test.storage.dto.ResponseWithSuccessDTO;
import com.test.storage.dto.UploadFileRequestDTO;
import com.test.storage.dto.UploadFileResponseDTO;
import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.exception.custom.TagNotFoundOnFileException;
import com.test.storage.model.StoredFile;
import com.test.storage.repository.StorageRepository;
import com.test.storage.util.FileTypeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class StorageService {

    final StorageRepository storageRepository;
    final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public StorageService(StorageRepository storageRepository, ElasticsearchOperations elasticsearchOperations) {
        this.storageRepository = storageRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public UploadFileResponseDTO uploadFile(UploadFileRequestDTO fileToUpload) {
        Set<String> tags = new LinkedHashSet<>();
        FileTypeUtil.getTypeOfFileByName(fileToUpload.getName()).map(tags::add);
        StoredFile storedFile = storageRepository.save(StoredFile.builder()
                .fileSize(fileToUpload.getSize())
                .fileName(fileToUpload.getName())
                .tags(tags)
                .build());
        return new UploadFileResponseDTO(storedFile.getId());
    }


    public ResponseWithSuccessDTO deleteFileById(String id) {
        var file = storageRepository.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        storageRepository.delete(file);
        return ResponseWithSuccessDTO.getSuccessResponse();
    }

    public ResponseWithSuccessDTO assignTagsToFileById(String id, Set<String> tags) {
        var storedFile = storageRepository.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        storedFile.getTags().addAll(tags);
        storageRepository.save(storedFile);
        return ResponseWithSuccessDTO.getSuccessResponse();
    }

    public FilteredPagedSearchResponseDTO filteredAndPagedSearch(
            Optional<Set<String>> tags,
            Integer page,
            Integer size,
            String nameFilter) {

        // can`t avoid empty tags set
        var filesPage = tags
                .map(tag -> storageRepository
                        .findAllByTagsAndFileNameContaining(tag, nameFilter, PageRequest.of(page, size)))
                .orElse(storageRepository.findAllByFileNameContaining(nameFilter, PageRequest.of(page, size)));

        return new FilteredPagedSearchResponseDTO(filesPage.getTotalElements(), filesPage.getContent());
    }

    public ResponseWithSuccessDTO deleteTagsFromFileById(String id, Set<String> tags) {
        var file = storageRepository.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        var fileTags = file.getTags();
        if (!fileTags.containsAll(tags)) {
            tags.removeAll(fileTags);
            throw new TagNotFoundOnFileException(id, tags);
        }
        fileTags.removeAll(tags);
        storageRepository.save(file);
        return ResponseWithSuccessDTO.getSuccessResponse();
    }
}
