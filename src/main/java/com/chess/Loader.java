package com.chess;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Utility class which helps load game resourses
 */
public class Loader {

    /**
     * Converts relative path to a resource file to Java URL object
     * @param path to a resource
     * @return file Java URL object
     */
    public static URL load(String path) {
        try {
            return Path.of("src/main/resources/" + path).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
