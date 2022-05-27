package com.chess;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class Loader {
    public static URL load(String path) {
        try {
            return Path.of("src/main/resources/" + path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
