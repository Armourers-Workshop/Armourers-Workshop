package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FileProviderDialog extends ConfirmDialog {

    private final SkinFileList fileList = new SkinFileList(new CGRect(0, 0, 100, 100));

    private final File rootPath;
    private final String extension;

    private FileItem selectedFile;
    private String selectedPath;

    public FileProviderDialog(File rootPath, String extension) {
        super();
        this.rootPath = rootPath;
        this.extension = extension;
        this.setFrame(new CGRect(0, 0, 240, 200));
        this.setTitle(new NSString(getDisplayText("title")));
        this.setMessage(new NSString(getDisplayText("message", extension)));
        this.setup(bounds());
    }

    private void setup(CGRect rect) {
        CGPoint messageOffset = messageLabel.center().copy();
        messageOffset.y -= 10;
        messageLabel.setCenter(messageOffset);

        fileList.setFrame(rect.insetBy(40, 10, 40, 10));
        fileList.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        fileList.addTarget(this, UIControl.Event.VALUE_CHANGED, FileProviderDialog::selectFile);
        addSubview(fileList);

        confirmButton.setTooltip(new NSString(getDisplayText("tooltip", extension)), UIControl.State.DISABLED);
        confirmButton.setEnabled(false);

        selectPath("");
    }

    private void selectFile(UIControl control) {
        FileItem oldValue = selectedFile;
        FileItem newValue = (FileItem) fileList.getSelectedItem();
        selectedFile = newValue;
        confirmButton.setEnabled(newValue != null && !newValue.isDirectory());
        if (newValue != null && newValue.isDirectory() && oldValue == newValue) {
            selectPath(newValue.path);
        }
    }

    private void selectPath(String newSelectedPath) {
        if (Objects.equals(selectedPath, newSelectedPath)) {
            return;
        }
        selectedPath = newSelectedPath;
        File targetPath = new File(rootPath, selectedPath);
        ArrayList<FileItem> items = getSkinFiles(targetPath, false);
        String path = SkinFileUtils.getRelativePath(targetPath, rootPath, true);
        if (path != null && !path.equals("/")) {
            items.add(0, new FileItem("..", newSelectedPath + "/..", true));
        }
        fileList.reloadData(new ArrayList<>(items));
    }

    public File getSelectedFile() {
        if (selectedFile == null || selectedFile.isDirectory()) {
            return null;
        }
        return new File(rootPath, selectedFile.getPath());
    }

    private Component getDisplayText(String key, Object... args) {
        return TranslateUtils.title("inventory.armourers_workshop.skin-library.dialog.fileProvider." + key, args);
    }

    private ArrayList<FileItem> getSkinFiles(File directory, boolean recursive) {
        ArrayList<FileItem> fileList = new ArrayList<>();
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
            if (templateFiles == null) {
                return fileList; // Armour file list load failed, not found.
            }
        } catch (Exception e) {
            ModLog.error(extension + "file list load failed.");
            e.printStackTrace();
            return fileList;
        }

        for (File file : templateFiles) {
            String path = SkinFileUtils.getRelativePath(file, rootPath, true);
            String filename = file.getName();
            if (file.isDirectory()) {
                fileList.add(new FileItem(filename, path, true));
                continue;
            }
            if (filename.toLowerCase().endsWith(extension)) {
                String name = SkinFileUtils.getBaseName(filename);
                fileList.add(new FileItem(name, path, false));
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

    public static class FileItem implements Comparable<FileItem>, ISkinLibrary.Entry {

        private final String name;
        private final String path;
        private final boolean isDirectory;

        public FileItem(String name, String path, boolean isDirectory) {
            this.name = name;
            this.path = path;
            this.isDirectory = isDirectory;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public String getSkinIdentifier() {
            return null;
        }

        @Override
        public ISkinType getSkinType() {
            return null;
        }

        @Override
        public boolean isDirectory() {
            return isDirectory;
        }

        @Override
        public boolean isPrivateDirectory() {
            return false;
        }

        @Override
        public int compareTo(FileItem o) {
            if (isDirectory & !o.isDirectory) {
                return path.compareToIgnoreCase(o.path) - 1000000;
            } else if (!isDirectory & o.isDirectory) {
                return path.compareToIgnoreCase(o.path) + 1000000;
            }
            return path.compareToIgnoreCase(o.path);
        }
    }
}
