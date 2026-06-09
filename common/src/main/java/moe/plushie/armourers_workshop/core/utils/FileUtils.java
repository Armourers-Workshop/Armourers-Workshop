package moe.plushie.armourers_workshop.core.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {


    public static List<File> listFiles(File directory) {
        try {
            var files = directory.listFiles();
            if (files != null) {
                return Collections.newList(files);
            }
        } catch (Exception ignored) {
            // ignore
        }
        return Collections.emptyList();
    }

    public static List<File> listFilesRecursive(File directory) {
        var allFiles = new ArrayList<File>();
        allFiles.add(directory);
        for (int i = 0; i < allFiles.size(); ++i) {
            var path = allFiles.get(i);
            if (path.isDirectory()) {
                allFiles.addAll(listFiles(path));
            }
        }
        allFiles.remove(0);
        return allFiles;
    }

    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * If the directory cannot be created (or does not already exist)
     * then an IOException is thrown.
     */
    public static void forceMkdir(final File directory) throws IOException {
        org.apache.commons.io.FileUtils.forceMkdir(directory);
    }

    /**
     * Makes any necessary but nonexistent parent directories for a given File. If the parent directory cannot be
     * created then an IOException is thrown.
     */
    public static void forceMkdirParent(final File file) throws IOException {
        final File parent = file.getParentFile();
        if (parent != null) {
            forceMkdir(parent);
        }
    }

    /**
     * Deletes a file, never throwing an exception. If file is a directory, delete it and all sub-directories.
     */
    public static boolean deleteQuietly(final File file) {
        return org.apache.commons.io.FileUtils.deleteQuietly(file);
    }


    /**
     * Normalizes a path, removing double and single dot path steps.
     */
    public static String normalize(final String filename) {
        return FilenameUtils.normalize(filename);
    }

    /**
     * Normalizes a path, removing double and single dot path steps.
     */
    public static String normalize(final String filename, final boolean unixSeparator) {
        return FilenameUtils.normalize(filename, unixSeparator);
    }

    /**
     * Normalizes a path, removing double and single dot path steps,
     * and removing any final directory separator.
     */
    public static String normalizeNoEndSeparator(final String filename, final boolean unixSeparator) {
        return FilenameUtils.normalizeNoEndSeparator(filename, unixSeparator);
    }

    /**
     * Concatenates a filename to a base path using normal command line style rules.
     */
    public static String concat(final String basePath, final String fullFilenameToAdd) {
        return FilenameUtils.concat(basePath, fullFilenameToAdd);
    }

    /**
     * Gets the base name, minus the full path and extension, from a full filename.
     */
    public static String getBaseName(final String filename) {
        return FilenameUtils.getBaseName(filename);
    }


    public static String removeParentPath(final String path, final String rootPath) {
        if (path.startsWith(rootPath)) {
            return path.substring(rootPath.length());
        }
        return path;
    }

    /**
     * Removes the extension from a filename.
     */
    public static String removeExtension(final String filename) {
        return FilenameUtils.removeExtension(filename);
    }

    /**
     * Gets the extension of a filename.
     */
    public static String getExtension(final String filename) {
        return FilenameUtils.getExtension(filename);
    }

    public static String getRelativePath(final String path, final String rootPath) {
        if (path.equals(rootPath)) {
            return "/";
        }
        if (path.startsWith(rootPath)) {
            return path.substring(rootPath.length());
        }
        return path;
    }

    public static String getRelativePath(final String path, final String rootPath, final boolean unixSeparator) {
        return normalize(getRelativePath(path, rootPath), unixSeparator);
    }

    public static String getRelativePath(final File path, final File rootPath) {
        return getRelativePath(path.getAbsolutePath(), rootPath.getAbsolutePath());
    }

    public static String getRelativePath(final File path, final File rootPath, final boolean unixSeparator) {
        return normalize(getRelativePath(path, rootPath), unixSeparator);
    }


    public static void setLastModifiedTime(File path, long time) {
        boolean ignored = path.setLastModified(time);
    }

    public static long getLastModifiedTime(File path) {
        return path.lastModified();
    }

    public static long getCreationTime(File path) {
        try {
            var attrs = Files.readAttributes(path.toPath(), BasicFileAttributes.class);
            return attrs.creationTime().toMillis();
        } catch (IOException e) {
            return 0;
        }
    }
}
