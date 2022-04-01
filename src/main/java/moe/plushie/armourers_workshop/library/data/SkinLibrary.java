package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateLibraryFilePacket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.init.common.ModLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SkinLibrary implements ISkinLibrary {

    static Executor executor = Executors.newFixedThreadPool(1);

    protected final File basePath;
    protected final String namespace;
    protected final ArrayList<ISkinLibraryListener> listeners = new ArrayList<>();

    protected boolean isReady = false;
    protected boolean isLoading = false;

    protected String rootPath = "/";
    protected ArrayList<SkinLibraryFile> files = new ArrayList<>();

    public SkinLibrary(String namespace, File path) {
        this.basePath = path;
        this.namespace = namespace;
    }

    public void addListener(ISkinLibraryListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISkinLibraryListener listener) {
        this.listeners.remove(listener);
    }

    public void reload() {
        if (basePath != null && !isLoading) {
            beginLoading();
            executor.execute(new SkinLibraryLoader(this, basePath, null));
        }
    }

    public void reset() {
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
        File file = new File(basePath, FilenameUtils.normalize(path));
        if (file.exists() && !FileUtils.deleteQuietly(file)) {
            ModLog.error("Can't remove file '{}'", file);
            return;
        }
        ModLog.debug("Save file '{}'", file);
        SkinIOUtils.saveSkinToFile(file, skin);
        reload();
    }

    public void mkdir(String path) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, FilenameUtils.normalize(path));
        if (!file.mkdirs()) {
            ModLog.error("Can't make new folder '{}'", file);
            return;
        }
        ModLog.debug("Create '{}' folder", path);
        reload();
    }

    public void delete(SkinLibraryFile libraryFile) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, FilenameUtils.normalize(libraryFile.getPath()));
        if (!FileUtils.deleteQuietly(file)) {
            ModLog.error("Can't remove file '{}'", file);
            return;
        }
        if (libraryFile.isDirectory()) {
            ModLog.debug("Remove '{}' folder and contents", libraryFile.getPath());
        } else {
            ModLog.debug("Remove '{}' file", libraryFile.getPath());
        }
        reload();
    }

    public void rename(SkinLibraryFile libraryFile, String path) {
        if (basePath == null) {
            return;
        }
        File file = new File(basePath, FilenameUtils.normalize(libraryFile.getPath()));
        File targetFile = new File(basePath, FilenameUtils.normalize(path));
        if (targetFile.exists() && !FileUtils.deleteQuietly(targetFile)) {
            ModLog.error("Can't remove file '{}'", file);
            return;
        }
        if (!file.renameTo(targetFile)) {
            ModLog.error("Can't rename file '{}'", file);
            return;
        }
        ModLog.debug("Move '{}' to '{}'", libraryFile.getPath(), path);
        reload();
    }

    public void reloadFiles(ArrayList<SkinLibraryFile> files) {
        ArrayList<ISkinLibraryListener> listeners;
        synchronized (this) {
            this.files = files;
            this.isReady = true;
            listeners = new ArrayList<>(this.listeners);
        }
        listeners.forEach(listener -> listener.libraryDidReload(this));
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
            files.add(0, new SkinLibraryFile(namespace, "..", (new File(rootPath)).getParent()));
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
        return namespace;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


    public static class Proxy extends SkinLibrary {

        public Proxy(String namespace) {
            super(namespace, null);
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
            NetworkHandler.getInstance().sendToServer(packet);
        }
    }
}
