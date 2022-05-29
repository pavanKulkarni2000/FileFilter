package com.example.filefilter;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String mime;
        try {
             mime = Files.probeContentType(Paths.get("attachment.mp3"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(4, 2 + 2);
    }
}