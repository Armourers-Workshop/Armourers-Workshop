package moe.plushie.armourers_workshop.library.data;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinLibraryCallback;
import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class SkinLibraryLoader implements Runnable {

    private final File libraryDirectory = AWCore.getSkinLibraryDirectory();
    private final File basePath;
    private final SkinLibrary library;
    private final ISkinLibraryCallback completeHandler;

    public SkinLibraryLoader(SkinLibrary library, File basePath, ISkinLibraryCallback completeHandler) {
        this.basePath = basePath;
        this.library = library;
        this.completeHandler = completeHandler;
    }

    public ArrayList<SkinLibraryFile> getSkinFiles(String domain, File directory, boolean recursive) {
        ArrayList<SkinLibraryFile> fileList = new ArrayList<>();
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
            if (templateFiles == null) {
                return fileList; // Armour file list load failed, not found.
            }
        } catch (Exception e) {
            ModLog.error("Armour file list load failed.");
            e.printStackTrace();
            return fileList;
        }

        for (File file : templateFiles) {
            String path = file.getAbsolutePath().replace(libraryDirectory.getAbsolutePath(), "");
            String filename = file.getName();
            if (file.isDirectory()) {
                fileList.add(new SkinLibraryFile(domain, filename, path));
                continue;
            }
            if (filename.toLowerCase().endsWith(".armour")) {
                String name = FilenameUtils.removeExtension(filename);
                Pair<ISkinType, ISkinProperties> header = SkinIOUtils.getTypeNameFromFile(file);
                if (header == null) {
                    continue; // Armour file load fail.
                }
                fileList.add(new SkinLibraryFile(domain, name, path, header));
            }
        }
        Collections.sort(fileList);

        if (recursive) {
            for (File file : templateFiles) {
                if (file.isDirectory()) {
                    fileList.addAll(getSkinFiles(domain, file, true));
                }
            }
        }

        return fileList;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        ModLog.debug("Loading library skins");
        ArrayList<SkinLibraryFile> files = getSkinFiles(library.getNamespace(), basePath, true);
        library.reloadFiles(files);
        library.endLoading();
        ModLog.debug(String.format("Finished loading %d client library skins in %d ms", files.size(), System.currentTimeMillis() - startTime));
        if (completeHandler != null) {
            completeHandler.libraryDidReload(null);
        }
    }

}
