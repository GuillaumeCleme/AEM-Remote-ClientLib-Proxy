package me.guillaumecle.aem.core.clientlibs;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoteScriptCompilerTest{

    private RemoteScriptCompiler fixture;

    @BeforeEach
    void setup() {
        fixture = new RemoteScriptCompiler();
    }

    @Test
    void testSetReturnContextJS(){
        fixture.setReturnContext("hello.js.href");
        assertAll(
            () -> assertEquals("application/javascript", fixture.getMimeType(), "Test JS Mime Type"),
            () -> assertEquals("js", fixture.getOutputExtension(), "Test JS Output Extension")
        );
    }

    @Test
    void testSetReturnContextCSS(){
        fixture.setReturnContext("hello.css.href");
        assertAll(
            () -> assertEquals("text/css", fixture.getMimeType(), "Test CSS Mime Type"),
            () -> assertEquals("css", fixture.getOutputExtension(), "Test CSS Output Extension")
        );
    }



}