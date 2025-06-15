package moe.plushie.armourers_workshop.gametest.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;

public class Launcher {

    public static void init() {
        try {
            var properties = loadProperties();
            if (properties == null) {
                return; // the junit test is disabled.
            }
            var agentClass = properties.getProperty("junit.dli.main.class");
            if (agentClass == null) {
                return; // the junit test is disabled.
            }
            var urls = new ArrayList<URL>();
            for (var path : properties.getProperty("junit.dli.classpath", "").split(File.pathSeparator)) {
                urls.add(new File(path).toURI().toURL());
            }
            var args = properties.getProperty("junit.dli.args", "").split("\\s+");
            var thread = Thread.currentThread();
            var oldLoader = thread.getContextClassLoader();
            var newLoader = new URLClassLoader(urls.toArray(new URL[0]), oldLoader);
            try {
                thread.setContextClassLoader(newLoader);
                var constructor = newLoader.loadClass(agentClass).getConstructor(String[].class);
                ((Runnable) constructor.newInstance((Object) args)).run();
            } finally {
                thread.setContextClassLoader(oldLoader);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties loadProperties() throws IOException {
        var filePath = System.getProperty("junit.dli.config");
        if (filePath == null) {
            return null; // not provided config.
        }
        var file = new File(filePath);
        if (!file.exists()) {
            return null; // the config not exists.
        }
        var properties = new Properties();
        properties.loadFromXML(new FileInputStream(filePath));
        return properties;
    }
}
