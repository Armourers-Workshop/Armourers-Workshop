package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.FileUtils;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.network.UpdateLibraryFilePacket;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

public class SkinLibrary implements ISkinLibrary {

    private static final Executor workThread = Executors.newFixedThreadPool(1, "AW-SKIN/L-LD");

    protected final File basePath;
    protected final DataDomain domain;
    protected final ArrayList<ISkinLibraryListener> listeners = new ArrayList<>();

    protected boolean isReady = false;
    protected boolean isLoading = false;

    protected String rootPath = "/";
    protected ArrayList<SkinLibraryFile> files = new ArrayList<>();

    public SkinLibrary(DataDomain domain, File path) {
        this.domain = domain;
        this.basePath = path;
    }

    public void addListener(ISkinLibraryListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISkinLibraryListener listener) {
        this.listeners.remove(listener);
    }

    public void reload() {
        ModLog.debug("reload {} library", domain.namespace());
        if (basePath != null && !isLoading) {
            beginLoading();
            workThread.execute(new SkinLibraryLoader(this, basePath, null));
        }
    }

    public void reset() {
        ModLog.debug("clear {} library", domain.namespace());
        files = new ArrayList<>();
        isLoading = false;
        isReady = false;
    }

    public void beginLoading() {
        isLoading = true;
    }

    public void endLoading() {
        isLoading = false;
    }

    public SkinLibraryFile get(String rootPath) {
        for (SkinLibraryFile file : files) {
            if (file.path().equals(rootPath)) {
                return file;
            }
        }
        return null;
    }

    public void save(String path, Skin skin, SkinFileOptions options) {
        File file = new File(basePath, FileUtils.normalize(path));
        if (file.exists() && !FileUtils.deleteQuietly(file)) {
            ModLog.error("Can't remove file '{}'", file);
            return;
        }
        ModLog.debug("Save file '{}'", file);
        try {
            FileUtils.forceMkdirParent(file);
            if (file.exists()) {
                FileUtils.deleteQuietly(file);
            }
            try (var outputStream = new FileOutputStream(file)) {
                SkinSerializer.writeToStream(skin, options, outputStream);
            }
            reload();
        } catch (Exception e) {
            ModLog.debug("Save file '{}' failed, {}", file, e);
        }
    }

    public void mkdir(String path) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, FileUtils.normalize(path));
        if (!file.mkdirs()) {
            ModLog.error("can't make new folder '{}'", file);
            return;
        }
        ModLog.debug("create '{}' folder", path);
        reload();
    }

    public void delete(SkinLibraryFile libraryFile) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, FileUtils.normalize(libraryFile.path()));
        if (!FileUtils.deleteQuietly(file)) {
            ModLog.error("can't remove file '{}'", file);
            return;
        }
        if (libraryFile.isDirectory()) {
            ModLog.debug("remove '{}' folder and contents", libraryFile.path());
        } else {
            ModLog.debug("remove '{}' file", libraryFile.path());
        }
        reload();
    }

    public void rename(SkinLibraryFile libraryFile, String path) {
        if (basePath == null) {
            return;
        }
        var file = new File(basePath, FileUtils.normalize(libraryFile.path()));
        var targetFile = new File(basePath, FileUtils.normalize(path));
        if (targetFile.exists() && !FileUtils.deleteQuietly(targetFile)) {
            ModLog.error("can't remove file '{}'", file);
            return;
        }
        if (!file.renameTo(targetFile)) {
            ModLog.error("can't rename file '{}'", file);
            return;
        }
        ModLog.debug("move '{}' to '{}'", libraryFile.path(), path);
        reload();
    }

    public void reloadFiles(ArrayList<SkinLibraryFile> files) {
        ArrayList<SkinLibraryFile> oldFiles = this.files;
        ArrayList<ISkinLibraryListener> listeners;
        synchronized (this) {
            this.files = files;
            this.isReady = true;
            listeners = new ArrayList<>(this.listeners);
        }
        // compare the file changes.
        Difference difference = new Difference();
        difference.added.addAll(files);
        difference.removed.addAll(oldFiles);
        for (SkinLibraryFile newFile : files) {
            for (SkinLibraryFile oldFile : oldFiles) {
                if (!oldFile.isSameFile(newFile)) {
                    continue;
                }
                difference.added.remove(newFile);
                difference.removed.remove(oldFile);
                if (oldFile.lastModified() != newFile.lastModified()) {
                    difference.changed.add(Pair.of(oldFile, newFile));
                }
                break;
            }
        }
        listeners.forEach(listener -> listener.libraryDidReload(this));
        listeners.forEach(listener -> listener.libraryDidChanges(this, difference));
    }

    public ArrayList<SkinLibraryFile> search(String keyword, SkinType skinType, String rootPath) {
        var fixedRootPath = rootPath;
        var files = new ArrayList<SkinLibraryFile>();
        if (keyword != null) {
            keyword = keyword.toLowerCase();
        }
        if (!rootPath.equals("/")) {
            fixedRootPath = rootPath + "/";
        }
        var removedChildDirs = new ArrayList<SkinLibraryFile>();
        for (var file : files()) {
            boolean isChild = file.isChildDirectory(fixedRootPath);
            boolean isMatches = file.matches(keyword, skinType);
            if (isMatches && isChild) {
                files.add(file);
                continue;
            }
            if (isMatches) {
                // when found a matching file, we must re-add the removed directory back into file list.
                String path = file.path();
                removedChildDirs.removeIf(dir -> path.startsWith(dir.path()) && files.add(dir));
                continue;
            }
            if (isChild && file.isDirectory()) {
                // although this directory does not match the search, we must display
                // when its subtree contains matches the search content.
                removedChildDirs.add(file);
            }
        }
        Collections.sort(files);
        if (!rootPath.equals(this.rootPath)) {
            files.add(0, new SkinLibraryFile(domain, "..", FileUtils.normalizeNoEndSeparator(rootPath + "/..", true)));
        }
        return files;
    }

    public ArrayList<SkinLibraryFile> files() {
        synchronized (this) {
            return files;
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public String namespace() {
        return domain.namespace();
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String rootPath() {
        return rootPath;
    }

    public void markBaseDir() {
        if (basePath.exists()) {
            return;
        }
        try {
            FileUtils.forceMkdir(basePath);
        } catch (IOException e) {
            ModLog.error("can't make directory '{}'", basePath, e);
        }
    }

    public static class Proxy extends SkinLibrary {

        public Proxy(DataDomain domain) {
            super(domain, null);
        }

        @Override
        public void reload() {
            send(UpdateLibraryFilePacket.Mode.RELOAD, null, null);
        }

        @Override
        public void mkdir(String path) {
            send(UpdateLibraryFilePacket.Mode.MKDIR, null, path);
        }

        @Override
        public void rename(SkinLibraryFile file, String path) {
            send(UpdateLibraryFilePacket.Mode.RENAME, file.path(), path);
        }

        @Override
        public void delete(SkinLibraryFile file) {
            send(UpdateLibraryFilePacket.Mode.DELETE, null, file.path());
        }

        private void send(UpdateLibraryFilePacket.Mode mode, String source, String destination) {
            UpdateLibraryFilePacket packet = new UpdateLibraryFilePacket(mode, source, destination);
            NetworkManager.sendToServer(packet);
        }
    }

    public static class Difference implements ISkinLibrary.Difference {

        final ArrayList<Entry> added = new ArrayList<>();
        final ArrayList<Entry> removed = new ArrayList<>();
        final ArrayList<Pair<Entry, Entry>> changed = new ArrayList<>();

        public Difference() {

        }

        @Override
        public Collection<Entry> addedChanges() {
            return added;
        }

        @Override
        public Collection<Entry> removedChanges() {
            return removed;
        }

        @Override
        public Collection<Pair<Entry, Entry>> updatedChanges() {
            return changed;
        }
    }
}
