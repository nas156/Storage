package com.test.storage.repository;

import com.test.storage.model.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StorageRepository extends ElasticsearchRepository<StoredFile, String> {

    List<StoredFile> findAll();

    Page<StoredFile> findAllByFileNameContaining(String fileName, Pageable pageable);

    Page<StoredFile> findAllByTagsAndFileNameContaining(Set<String> tags, String fileName, Pageable pageable);

}
