package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.network.UpdateLibraryFilePacket;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.SkinFileStreamUtils;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;

public class SkinLibrary implements ISkinLibrary {

    private static final Executor workThread = ThreadUtils.newFixedThreadPool(1, "AW-SKIN/L-LD");

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
            if (file.getPath().equals(rootPath)) {
                return file;
            }
        }
        return null;
    }

    public void save(String path, Skin skin) {
        File file = new File(basePath, SkinFileUtils.normalize(path));
        if (file.exists() && !SkinFileUtils.deleteQuietly(file)) {
            ModLog.error("Can't remove file '{}'", file);
            return;
        }
        ModLog.debug("Save file '{}'", file);
        SkinFileStreamUtils.saveSkinToFile(file, skin);
        reload();
    }

    public void mkdir(String path) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, SkinFileUtils.normalize(path));
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
        File file = new File(basePath, SkinFileUtils.normalize(libraryFile.getPath()));
        if (!SkinFileUtils.deleteQuietly(file)) {
            ModLog.error("can't remove file '{}'", file);
            return;
        }
        if (libraryFile.isDirectory()) {
            ModLog.debug("remove '{}' folder and contents", libraryFile.getPath());
        } else {
            ModLog.debug("remove '{}' file", libraryFile.getPath());
        }
        reload();
    }

    public void rename(SkinLibraryFile libraryFile, String path) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, SkinFileUtils.normalize(libraryFile.getPath()));
        File targetFile = new File(basePath, SkinFileUtils.normalize(path));
        if (targetFile.exists() && !SkinFileUtils.deleteQuietly(targetFile)) {
            ModLog.error("can't remove file '{}'", file);
            return;
        }
        if (!file.renameTo(targetFile)) {
            ModLog.error("can't rename file '{}'", file);
            return;
        }
        ModLog.debug("move '{}' to '{}'", libraryFile.getPath(), path);
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
                if (oldFile.getLastModified() != newFile.getLastModified()) {
                    difference.changed.add(Pair.of(oldFile, newFile));
                }
                break;
            }
        }
        listeners.forEach(listener -> listener.libraryDidReload(this));
        listeners.forEach(listener -> listener.libraryDidChanges(this, difference));
    }

    public ArrayList<SkinLibraryFile> search(String keyword, ISkinType skinType, String rootPath) {
        String fixedRootPath = rootPath;
        ArrayList<SkinLibraryFile> files = new ArrayList<>();
        if (keyword != null) {
            keyword = keyword.toLowerCase();
        }
        if (!rootPath.equals("/")) {
            fixedRootPath = rootPath + "/";
        }
        ArrayList<SkinLibraryFile> removedChildDirs = new ArrayList<>();
        for (SkinLibraryFile file : getFiles()) {
            boolean isChild = file.isChildDirectory(fixedRootPath);
            boolean isMatches = file.matches(keyword, skinType);
            if (isMatches && isChild) {
                files.add(file);
                continue;
            }
            if (isMatches) {
                // when found a matching file, we must re-add the removed directory back into file list.
                String path = file.getPath();
                removedChildDirs.removeIf(dir -> path.startsWith(dir.getPath()) && files.add(dir));
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
            files.add(0, new SkinLibraryFile(domain, "..", SkinFileUtils.normalizeNoEndSeparator(rootPath + "/..", true)));
        }
        return files;
    }

    public ArrayList<SkinLibraryFile> getFiles() {
        synchronized (this) {
            return files;
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public String getNamespace() {
        return domain.namespace();
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void markBaseDir() {
        if (basePath.exists()) {
            return;
        }
        try {
            SkinFileUtils.forceMkdir(basePath);
        } catch (IOException e) {
            e.printStackTrace();
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
            send(UpdateLibraryFilePacket.Mode.RENAME, file.getPath(), path);
        }

        @Override
        public void delete(SkinLibraryFile file) {
            send(UpdateLibraryFilePacket.Mode.DELETE, null, file.getPath());
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
        public Collection<Entry> getAddedChanges() {
            return added;
        }

        @Override
        public Collection<Entry> getRemovedChanges() {
            return removed;
        }

        @Override
        public Collection<Pair<Entry, Entry>> getUpdatedChanges() {
            return changed;
        }
    }
}
