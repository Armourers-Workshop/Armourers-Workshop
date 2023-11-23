package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinFileHeader;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SkinLibraryLoader implements Runnable {

    private static final HashMap<String, CachedFileHeader> CACHED_FILE_HEADERS = new HashMap<>();

    private final File libraryDirectory = EnvironmentManager.getSkinLibraryDirectory();
    private final File basePath;
    private final SkinLibrary library;
    private final ISkinLibraryListener completeHandler;

    public SkinLibraryLoader(SkinLibrary library, File basePath, ISkinLibraryListener completeHandler) {
        this.basePath = basePath;
        this.library = library;
        this.completeHandler = completeHandler;
    }

    public ArrayList<SkinLibraryFile> getSkinFiles(File directory, boolean recursive) {
        ArrayList<SkinLibraryFile> fileList = new ArrayList<>();
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
            if (templateFiles == null) {
                return fileList; // Armour file list load failed, not found.
            }
        } catch (Exception e) {
            ModLog.error("armour file list load failed.");
            e.printStackTrace();
            return fileList;
        }

        for (File file : templateFiles) {
            String path = SkinFileUtils.getRelativePath(file, libraryDirectory, true);
            String filename = file.getName();
            if (file.isDirectory()) {
                fileList.add(new SkinLibraryFile(library.domain, filename, path));
                continue;
            }
            if (filename.toLowerCase().endsWith(Constants.EXT)) {
                String name = SkinFileUtils.getBaseName(filename);
                ISkinFileHeader header = getSkinFileHeader(file);
                if (header == null) {
                    continue; // Armour file load fail.
                }
                fileList.add(new SkinLibraryFile(library.domain, name, path, header));
            }
        }
        Collections.sort(fileList);

        if (recursive) {
            for (File file : templateFiles) {
                if (file.isDirectory()) {
                    fileList.addAll(getSkinFiles(file, true));
                }
            }
        }

        return fileList;
    }

    private ISkinFileHeader getSkinFileHeader(File file) {
        long modifiedTime = file.lastModified();
        String key = file.getAbsolutePath();
        CachedFileHeader cache = CACHED_FILE_HEADERS.get(key);
        if (cache != null && cache.isValid(modifiedTime)) {
            return cache.getHeader();
        }
        ISkinFileHeader header = SkinFileStreamUtils.readHeaderFromFile(file);
        if (header != null) {
            if (header instanceof SkinFileHeader) {
                ((SkinFileHeader) header).setLastModified((int) modifiedTime);
            }
            CACHED_FILE_HEADERS.put(key, new CachedFileHeader(modifiedTime, header));
            return header;
        }
        return null;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        ModLog.debug("loading skin library");
        ArrayList<SkinLibraryFile> files = getSkinFiles(basePath, true);
        library.reloadFiles(files);
        library.endLoading();
        ModLog.debug(String.format("finished loading %d client library skins in %d ms", files.size(), System.currentTimeMillis() - startTime));
        if (completeHandler != null) {
            completeHandler.libraryDidReload(null);
        }
    }

    public static class CachedFileHeader {

        private final long modifiedTime;
        private final ISkinFileHeader header;

        public CachedFileHeader(long modifiedTime, ISkinFileHeader header) {
            this.modifiedTime = modifiedTime;
            this.header = header;
        }

        public boolean isValid(long modifiedTime) {
            return this.modifiedTime == modifiedTime;
        }

        public ISkinFileHeader getHeader() {
            return header;
        }
    }
}
