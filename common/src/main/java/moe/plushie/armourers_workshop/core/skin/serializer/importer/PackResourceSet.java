package moe.plushie.armourers_workshop.core.skin.serializer.importer;

import moe.plushie.armourers_workshop.core.utils.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class PackResourceSet {

    private final Collection<PackResource> resources;

    public PackResourceSet(File file) throws IOException {
        this.resources = getResourcesFromFile(file);
    }

    @Nullable
    public PackResource firstResource() {
        for (var resource : resources) {
            return resource;
        }
        return null;
    }

    @Nullable
    public PackResource findResource(String regex) {
        var pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (var resource : resources) {
            if (pattern.matcher(resource.name()).find()) {
                return resource;
            }
        }
        return null;
    }

    @Nullable
    public PackResource getResource(String name) {
        for (var resource : resources) {
            if (resource.name().equalsIgnoreCase(name)) {
                return resource;
            }
        }
        return null;
    }

    public Collection<PackResource> getResources() {
        return resources;
    }

    protected Collection<PackResource> getResourcesFromFile(File file) throws IOException {
        if (file.isDirectory()) {
            return getResourcesFromDirectory(file);
        }
        if (file.getName().toLowerCase().endsWith(".zip")) {
            return getResourcesFromZip(file);
        }
        return getResourcesFromSet(file);
    }

    protected Collection<PackResource> getResourcesFromZip(File zipFile) throws IOException {
        var resources = new ArrayList<PackResource>();
        var file = new ZipFile(zipFile);
        var zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            var fileName = entry.getName();
            var fileEntry = entry;
            resources.add(new PackResource() {
                @Override
                public String name() {
                    return fileName;
                }

                @Override
                public InputStream inputStream() throws IOException {
                    return file.getInputStream(fileEntry);
                }
            });
        }
        return resources;
    }

    protected Collection<PackResource> getResourcesFromDirectory(File rootPath) throws IOException {
        var resources = new ArrayList<PackResource>();
        for (var entry : FileUtils.listFilesRecursive(rootPath)) {
            if (entry.isDirectory()) {
                continue;
            }
            var fileName = FileUtils.getRelativePath(entry, rootPath, true).substring(1);
            resources.add(new PackResource() {
                @Override
                public String name() {
                    return fileName;
                }

                @Override
                public InputStream inputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }

    protected Collection<PackResource> getResourcesFromSet(File... entries) throws IOException {
        var resources = new ArrayList<PackResource>();
        for (var entry : entries) {
            if (entry.isDirectory()) {
                continue;
            }
            var fileName = entry.getName();
            resources.add(new PackResource() {
                @Override
                public String name() {
                    return fileName;
                }

                @Override
                public InputStream inputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }
}
