package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import net.minecraft.Util;

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
        this.setTitle(NSString.localizedString("skin-library.dialog.fileProvider.title"));
        this.setMessage(NSString.localizedString("skin-library.dialog.fileProvider.message", extension));
        this.setup(bounds());
    }

    private void setup(CGRect rect) {
        var messageOffset = messageLabel.center().copy();
        messageOffset.y -= 10;
        messageLabel.setCenter(messageOffset);

        fileList.setFrame(rect.insetBy(40, 10, 40, 10));
        fileList.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        fileList.addTarget(this, UIControl.Event.VALUE_CHANGED, FileProviderDialog::selectFile);
        addSubview(fileList);

        confirmButton.setTooltip(NSString.localizedString("skin-library.dialog.fileProvider.tooltip", extension), UIControl.State.DISABLED);
        confirmButton.setEnabled(false);

        selectPath("");
    }

    private void setupEmptyView() {
        var rect = fileList.bounds();
        var emptyView = new UIView(fileList.frame());
        emptyView.setAutoresizingMask(fileList.autoresizingMask());

        var bg1 = new UIImageView(rect);
        bg1.setImage(UIImage.of(ModTextures.LIST).fixed(11, 11).clip(1, 1, 1, 1).build());
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        emptyView.addSubview(bg1);

        var top = (rect.height - 50) / 2;
        var titleView = new UILabel(new CGRect(10, top, rect.width - 20, 30));
        titleView.setText(NSString.localizedString("skin-library.dialog.fileProvider.emptyFolder", extension, rootPath.getName()));
        titleView.setTextColor(UIColor.GRAY);
        titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        titleView.setTextHorizontalAlignment(NSTextAlignment.Horizontal.CENTER);
        titleView.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        titleView.setNumberOfLines(0);
        emptyView.addSubview(titleView);

        var openButton = new UIButton(new CGRect((rect.width - 100) / 2, top + 30, 100, 20));
        openButton.setTitle(NSString.localizedString("skin-library.dialog.fileProvider.openFolder"), UIControl.State.ALL);
        openButton.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        openButton.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        openButton.setAutoresizingMask(AutoresizingMask.flexibleTopMargin | AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin);
        openButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, FileProviderDialog::openFolder);
        emptyView.addSubview(openButton);

        addSubview(emptyView);
    }

    private void selectFile(UIControl control) {
        var oldValue = selectedFile;
        var newValue = (FileItem) fileList.getSelectedItem();
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
        var targetPath = new File(rootPath, selectedPath);
        var items = getSkinFiles(targetPath, false);
        var path = SkinFileUtils.getRelativePath(targetPath, rootPath, true);
        if (path != null && !path.equals("/")) {
            items.add(0, new FileItem("..", newSelectedPath + "/..", true));
        }
        fileList.reloadData(new ArrayList<>(items));
        if (items.isEmpty()) {
            setupEmptyView();
        }
    }

    private void openFolder(UIControl sender) {
        Util.getPlatform().openFile(rootPath);
    }

    public File getSelectedFile() {
        if (selectedFile == null || selectedFile.isDirectory()) {
            return null;
        }
        return new File(rootPath, selectedFile.getPath());
    }

    private ArrayList<FileItem> getSkinFiles(File directory, boolean recursive) {
        var fileList = new ArrayList<FileItem>();
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

        for (var file : templateFiles) {
            var path = SkinFileUtils.getRelativePath(file, rootPath, true);
            var filename = file.getName();
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
            for (var file : templateFiles) {
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
