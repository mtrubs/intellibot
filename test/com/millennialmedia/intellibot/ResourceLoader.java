package com.millennialmedia.intellibot;

import java.net.URL;

/**
 * @author mrubino
 * @since 2014-06-19
 */
public class ResourceLoader {

    private ResourceLoader() {
    }

    public static String getResourcePath(String path) {
        URL resource = ResourceLoader.class.getClassLoader().getResource(path);
        return resource == null ? null : resource.getFile();
    }
}
