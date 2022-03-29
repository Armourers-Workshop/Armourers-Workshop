package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinLibraryCallback;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SkinLibrary {

    static Executor executor = Executors.newFixedThreadPool(1);

    protected File path;
    protected ArrayList<SkinLibraryFile> files = new ArrayList<>();

    public SkinLibrary(File path) {
        this.path = path;
    }

    public void addListener(ISkinLibraryCallback listener) {

    }

    public void removeListener(ISkinLibraryCallback listener) {

    }

    public void reload() {
        executor.execute(new SkinLibraryLoader(this, null));
    }

    public void reloadFiles(ArrayList<SkinLibraryFile> files) {
        synchronized (this) {
            this.files = files;
        }
    }

    public ArrayList<SkinLibraryFile> search(String keyword, ISkinType skinType, String rootPath) {
        ArrayList<SkinLibraryFile> files = new ArrayList<>();
        if (keyword != null) {
            keyword = keyword.toLowerCase();
        }
        ArrayList<SkinLibraryFile> removedChildDirs = new ArrayList<>();
        for (SkinLibraryFile file : getFiles()) {
            boolean isChild = file.isChildDirectory(rootPath);
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
        if (!rootPath.equals("/")) {
            files.add(0, new SkinLibraryFile("..", (new File(rootPath)).getParent()));
        }
        return files;
    }

    public ArrayList<SkinLibraryFile> getFiles() {
        synchronized (this) {
            return files;
        }
    }

    public File getPath() {
        return path;
    }
}
