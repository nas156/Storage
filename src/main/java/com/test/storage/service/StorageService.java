package com.test.storage.service;

import com.test.storage.dto.FilteredPagedSearchResponseDTO;
import com.test.storage.dto.ResponseWithSuccessDTO;
import com.test.storage.dto.UploadFileRequestDTO;
import com.test.storage.dto.UploadFileResponseDTO;
import com.test.storage.exception.custom.FileNotFoundException;
import com.test.storage.exception.custom.TagNotFoundOnFileException;
import com.test.storage.model.StoredFile;
import com.test.storage.repository.StorageRepository;
import org.apache.tika.Tika;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StorageService {

    final StorageRepository storageRepository;
    final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public StorageService(StorageRepository storageRepository, ElasticsearchOperations elasticsearchOperations) {
        this.storageRepository = storageRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<StoredFile> getAllStoredFiles() {
        return storageRepository.findAll();
    }

    public UploadFileResponseDTO uploadFile(UploadFileRequestDTO fileToUpload) {
        Set<String> tags = new HashSet<>();
        tags.add(getFileTagsBasedOnType(fileToUpload.getName()));
        StoredFile storedFile = storageRepository.save(StoredFile.builder()
                .fileSize(fileToUpload.getSize())
                .fileName(fileToUpload.getName())
                .tags(tags)
                .build());
        return new UploadFileResponseDTO(storedFile.getId());
    }

    private String getFileTagsBasedOnType(String fileName) {
        String fileType = new Tika().detect(fileName);
        // provided string is like type/format
        return fileType.split("/")[0];
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
            Set<String> tags,
            Integer page,
            Integer size,
            String nameFilter) {

        var filterQuery = QueryBuilders.boolQuery();
        tags.forEach(tag -> filterQuery.must(QueryBuilders.termQuery("tags", tag)));
        filterQuery.must(QueryBuilders.wildcardQuery("filename", prepareStringForSearch(nameFilter)));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(filterQuery)
                .withPageable(PageRequest.of(page, size))
                .build();

        var total = elasticsearchOperations.count(searchQuery, StoredFile.class);
        SearchHits<StoredFile> searchSuggestions =
                elasticsearchOperations.search(searchQuery,
                        StoredFile.class);

        var files = searchSuggestions
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new FilteredPagedSearchResponseDTO(total, files);
    }

    private String prepareStringForSearch(String stringToPrepare) {
        return String.format("*%s*.*", stringToPrepare);
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
