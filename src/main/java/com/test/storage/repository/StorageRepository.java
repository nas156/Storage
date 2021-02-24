package com.test.storage.repository;

import com.test.storage.model.StoredFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends ElasticsearchRepository<StoredFile, String> {

    List<StoredFile> findAll();

}
