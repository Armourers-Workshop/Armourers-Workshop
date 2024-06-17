package moe.plushie.armourers_workshop.core.skin.transformer;

import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class SkinSerializerV21 {

    public static Collection<IResource> getResourcesFromFile(File file) throws IOException {
        if (file.isDirectory()) {
            return getResourcesFromDirectory(file);
        }
        if (file.getName().toLowerCase().endsWith(".zip")) {
            return getResourcesFromZip(file);
        }
        return getResourcesFromSet(file);
    }


    public static Collection<IResource> getResourcesFromZip(File zipFile) throws IOException {
        var resources = new ArrayList<IResource>();
        var file = new ZipFile(zipFile);
        var zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
        ZipEntry entry;
        while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            String fileName = entry.getName();
            ZipEntry fileEntry = entry;
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getSource() {
                    return "";
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return file.getInputStream(fileEntry);
                }
            });
        }
        return resources;
    }

    public static Collection<IResource> getResourcesFromDirectory(File rootPath) throws IOException {
        var resources = new ArrayList<IResource>();
        for (var entry : SkinFileUtils.listAllFiles(rootPath)) {
            if (entry.isDirectory()) {
                continue;
            }
            var fileName = SkinFileUtils.getRelativePath(entry, rootPath, true).substring(1);
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getSource() {
                    return "";
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }

    public static Collection<IResource> getResourcesFromSet(File... entries) throws IOException {
        var resources = new ArrayList<IResource>();
        for (var entry : entries) {
            if (entry.isDirectory()) {
                continue;
            }
            var fileName = entry.getName();
            resources.add(new IResource() {
                @Override
                public String getName() {
                    return fileName;
                }

                @Override
                public String getSource() {
                    return "";
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return new FileInputStream(entry);
                }
            });
        }
        return resources;
    }
}

