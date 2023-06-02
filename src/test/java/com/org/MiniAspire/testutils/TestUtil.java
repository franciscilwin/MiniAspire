package com.org.MiniAspire.testutils;

public class TestUtil {
    public static String createURLWithPort(String uri, Integer port) {
        return "http://localhost:" + port + uri;
    }
}
