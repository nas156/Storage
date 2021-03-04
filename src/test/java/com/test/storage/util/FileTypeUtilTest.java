package com.test.storage.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class FileTypeUtilTest {

    @Test
    void whenAudioFileName_thenReturnOptionalAudio() {
        var fileName = "test.mp3";
        var expectedType = "audio";
        String actualType = FileTypeUtil.getTypeOfFileByName(fileName).get();
        assertEquals(expectedType, actualType);
    }

    @Test
    void whenTextFileName_thenReturnOptionalText() {
        var fileName = "test.txt";
        var expectedType = "text";
        String actualType = FileTypeUtil.getTypeOfFileByName(fileName).get();
        assertEquals(expectedType, actualType);
    }

    @Test
    void whenImageFileName_thenReturnOptionalImage() {
        var fileName = "aaa.test.png";
        var expectedType = "image";
        String actualType = FileTypeUtil.getTypeOfFileByName(fileName).get();
        assertEquals(expectedType, actualType);
    }

    @Test
    void whenVideoFileName_thenReturnOptionalVideo() {
        var fileName = "aaa.test.avi";
        var expectedType = "video";
        String actualType = FileTypeUtil.getTypeOfFileByName(fileName).get();
        assertEquals(expectedType, actualType);
    }

    @Test
    void whenNoExtensionFileName_thenReturnOptionalEmpty() {
        var fileName = "mp3";
        Optional<String> actualType = FileTypeUtil.getTypeOfFileByName(fileName);
        assertTrue(actualType.isEmpty());
    }

    @Test
    void whenUnknownExtension_thenReturnOptionalEmpty() {
        var fileName = "test.kkk";
        Optional<String> actualType = FileTypeUtil.getTypeOfFileByName(fileName);
        assertTrue(actualType.isEmpty());
    }

}
