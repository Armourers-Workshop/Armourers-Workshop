package moe.plushie.armourers_workshop.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class SkinFileUtils {

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

    /**
     * Removes the extension from a filename.
     */
    public static String removeExtension(final String filename) {
        return FilenameUtils.removeExtension(filename);
    }

    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * If the directory cannot be created (or does not already exist)
     * then an IOException is thrown.
     */
    public static void forceMkdir(final File directory) throws IOException {
        FileUtils.forceMkdir(directory);
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
        return FileUtils.deleteQuietly(file);
    }
}
