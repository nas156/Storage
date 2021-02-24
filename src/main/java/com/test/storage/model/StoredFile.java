package com.test.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "files")
public class StoredFile {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "filename")
    private String fileName;

    @Field(type = FieldType.Long, name = "filesize")
    private Long fileSize;

    @Field(type = FieldType.Auto)
    private Set<String> tags;

}
