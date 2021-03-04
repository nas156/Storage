package com.test.storage.util;

import org.apache.tika.Tika;

import java.util.Arrays;
import java.util.Optional;

public class FileTypeUtil {

    private final static String[] EXPECTED_TYPES = {"audio", "video", "text", "document", "image"};

    private FileTypeUtil() {

    }

    public static Optional<String> getTypeOfFileByName(String fileName) {
        if (fileName.contains(".")) {
            return Optional.ofNullable(getFileTagsBasedOnExt(fileName));
        }
        return Optional.empty();
    }

    private static String getFileTagsBasedOnExt(String fileExt) {
        // provided string is like type/format
        String fileType = new Tika().detect(fileExt).replaceAll("/.*", "");
        System.out.println(fileType);
        return Arrays.asList(EXPECTED_TYPES).contains(fileType) ? fileType : null;
    }


}
