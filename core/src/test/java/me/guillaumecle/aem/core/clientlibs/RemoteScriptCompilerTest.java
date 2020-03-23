package me.guillaumecle.aem.core.clientlibs;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoteScriptCompilerTest {

    private RemoteScriptCompiler fixture;

    @BeforeEach
    void setup() {
        fixture = new RemoteScriptCompiler();
    }

    @Test
    void testSetReturnContextJS() {
        fixture.setReturnContext("hello.js.href");
        assertAll(() -> assertEquals("application/javascript", fixture.getMimeType(), "Test JS Mime Type"),
                () -> assertEquals("js", fixture.getOutputExtension(), "Test JS Output Extension"));
    }

    @Test
    void testSetReturnContextCSS() {
        fixture.setReturnContext("hello.css.href");
        assertAll(() -> assertEquals("text/css", fixture.getMimeType(), "Test CSS Mime Type"),
                () -> assertEquals("css", fixture.getOutputExtension(), "Test CSS Output Extension"));
    }

    @Test
    void testGetRemoteLibrary() throws IOException {
        String script = fixture.getRemoteLibrary("https://code.jquery.com/jquery-3.4.1.slim.min.js");
        assertAll(
            () -> assertNotNull(script),
            () -> assertTrue(script.contains("jQuery v3.4.1"), "Test contents of returned script")
        );
    }

    @Test
    void testGetRemoteLibrary404() throws IOException {
        assertThrows(IOException.class, () -> {
            fixture.getRemoteLibrary("https://code.jquery.com/jquery-3.4.1.slim.min.js.404");
        });
    }

    @Test
    void testWriteError() throws IOException {
        StringWriter out = new StringWriter();
        fixture.writeError(out, "hello.js", "Failed to fetch library");
        assertAll(
            () -> assertNotNull(out),
            () -> assertTrue(out.toString().contains("Input: hello.js"), "Test contents of error writer"),
            () -> assertTrue(out.toString().contains("Error: Failed to fetch library"), "Test contents of error writer")
        );
    }

    @Test
    void testParseResource() throws IOException {

        String n = System.lineSeparator();

        String contents = "" + n
            + "line1" + n
            + "line2" + n
            + "line3" + n
            + "";

        String[] resources = fixture.parseResource(new StringReader(contents));
        assertAll(
            () -> assertEquals(resources.length, 3, "Test resources length")
        );
    }
}