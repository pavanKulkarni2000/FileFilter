package com.example.filefilter;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG = "ExampleInstrumentedTest";

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String type_subtype = null;
        if (extension != null) {
            type_subtype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (type_subtype != null) {
            type = type_subtype.split("/")[0];
        }
        return type;
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Uri uri = Uri.parse("/sdcard");
        System.out.println("uri: " + uri.toString());

        assertEquals(getMimeType("dsicj.doc"), "application");
        assertEquals(getMimeType("dsicj.docx"), "application");
        assertEquals(getMimeType("dsicj.txt"), "text");
        assertEquals(getMimeType("dsicj.ogg"), "audio");
        assertEquals(getMimeType("dsicj.config"), "application");
        assertEquals("com.example.filefilter", appContext.getPackageName());
    }
}