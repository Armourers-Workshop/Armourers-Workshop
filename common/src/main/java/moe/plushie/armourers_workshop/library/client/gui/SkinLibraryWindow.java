package moe.plushie.armourers_workshop.library.client.gui;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import com.apple.library.uikit.UIView;
import com.google.common.base.Strings;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinComboBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinFileList;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.library.network.SaveSkinPacket;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class SkinLibraryWindow extends MenuWindow<SkinLibraryMenu> implements UITextFieldDelegate, ISkinLibraryListener {

    private final UICheckBox trackCheckBox = new UICheckBox(CGRect.ZERO);
    private final UIButton actionButton = new UIButton(CGRect.ZERO);

    private final UIButton localFileButton = buildIconButton(0, 0, 0, 0, 50, 30);
    private final UIButton remotePublicButton = buildIconButton(55, 0, 0, 31, 50, 30);
    private final UIButton remotePrivateButton = buildIconButton(110, 0, 0, 62, 50, 30);

    private final UIButton openFolderButton = buildIconButton(0, 0, 0, 93, 24, 24);
    private final UIButton deleteButton = buildIconButton(25, 0, 0, 118, 24, 24);
    private final UIButton refreshButton = buildIconButton(50, 0, 73, 93, 24, 24);
    private final UIButton newFolderButton = buildIconButton(75, 0, 73, 118, 24, 24);
    private final UIButton backButton = buildIconButton(138, 0, 146, 93, 24, 24);

    private final UITextField nameTextField = new UITextField(CGRect.ZERO);
    private final UITextField searchTextField = new UITextField(CGRect.ZERO);

    private final SkinComboBox skinTypeList = new SkinComboBox(CGRect.ZERO);
    private final SkinFileList fileList = new SkinFileList(new CGRect(0, 0, 100, 100));
    private final HashMap<String, CGPoint> contentOffsets = new HashMap<>();

    protected ISkinType skinType = SkinTypes.UNKNOWN;
    protected ISkinLibrary.Entry selectedFile = null;
    protected String selectedPath;
    protected ItemStack lastInputItem;

    protected SkinLibrary selectedLibrary;
    protected SkinLibraryManager.Client libraryManager = SkinLibraryManager.getClient();

    private final Inventory playerInventory;

    public SkinLibraryWindow(SkinLibraryMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 640, 480));
        this.libraryManager.addListener(this);
        this.selectedLibrary = libraryManager.getLocalSkinLibrary();
        this.selectedPath = selectedLibrary.getRootPath();
        this.playerInventory = inventory;
        this.inventoryView.removeFromSuperview();
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
        menu.reload(0, 0, (int) size.width, (int) size.height);
    }

    @Override
    public void init() {
        super.init();
        CGRect rect = bounds().insetBy(23, 5, 5, 5);

        titleView.setTextColor(new UIColor(0xcccccc));

        setupInputView(rect);
        setupInventoryView(rect);
        setupFileView(rect.insetBy(0, 162 + 5, 0, 0));

        reloadData(this);
        reloadStatus();
    }

    @Override
    public void menuDidChange() {
        super.menuDidChange();
        reloadStatus();
        reloadInputName();
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        renameItem(textField.text());
        return true;
    }

    private void setupInputView(CGRect rect) {
        int width = 162;

        UIView group1 = new UIView(new CGRect(rect.getMinX(), rect.getMinY(), width, 30));
        addSubview(group1);

        localFileButton.setTooltip(getDisplayText("rollover.localFiles"), UIControl.State.DISABLED);
        localFileButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(localFileButton);

        remotePublicButton.setTooltip(getDisplayText("rollover.notOnServer"), UIControl.State.DISABLED);
        remotePublicButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(remotePublicButton);

        remotePrivateButton.setTooltip(getDisplayText("rollover.notOnServer"), UIControl.State.DISABLED);
        remotePrivateButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::selectLibrary);
        group1.addSubview(remotePrivateButton);

        nameTextField.setFrame(new CGRect(rect.getMinX(), group1.frame().getMaxY() + 5, width, 20));
        nameTextField.setPlaceholder(getDisplayText("label.enterFileName"));
        nameTextField.setMaxLength(255);
        nameTextField.setDelegate(this);
        addSubview(nameTextField);

        UIView group3 = new UIView(new CGRect(rect.getMinX(), nameTextField.frame().getMaxY() + 5, width, 24));
        addSubview(group3);

        openFolderButton.setTooltip(getDisplayText("rollover.openLibraryFolder"), UIControl.State.NORMAL);
        openFolderButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::openFolder);
        group3.addSubview(openFolderButton);

        refreshButton.setTooltip(getDisplayText("rollover.refresh"), UIControl.State.NORMAL);
        refreshButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::refreshLibrary);
        group3.addSubview(refreshButton);

        deleteButton.setTooltip(getDisplayText("rollover.deleteSkin"), UIControl.State.NORMAL);
        deleteButton.setTooltip(getDisplayText("rollover.deleteSkinSelect"), UIControl.State.DISABLED);
        deleteButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::removeItem);
        group3.addSubview(deleteButton);

        newFolderButton.setTooltip(getDisplayText("rollover.newFolder"), UIControl.State.NORMAL);
        newFolderButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::addFolder);
        group3.addSubview(newFolderButton);

        backButton.setTooltip(getDisplayText("rollover.back"), UIControl.State.NORMAL);
        backButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::backFolder);
        group3.addSubview(backButton);
    }

    private void setupInventoryView(CGRect rect) {
        int width = 162;
        int height = 76;

        UIView group1 = new UIView(new CGRect(rect.getMinX(), rect.getMaxY() - height, width, height));
        group1.setOpaque(false);
        group1.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        group1.setContents(UIImage.of(ModTextures.SKIN_LIBRARY).uv(0, 180).build());
        addSubview(group1);

        UIView group2 = new UIView(new CGRect(rect.getMinX(), group1.frame().getMinY() - 31, width, 26));
        group2.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        addSubview(group2);

        trackCheckBox.setFrame(new CGRect(rect.getMinX(), group2.frame().getMinY() - 10, width, 10));
        trackCheckBox.setTitle(getDisplayText("trackFile"));
        trackCheckBox.setTitleColor(UIColor.WHITE);
        trackCheckBox.setSelected(false);
        trackCheckBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> self.reloadStatus());
        trackCheckBox.setAutoresizingMask(AutoresizingMask.flexibleTopMargin);
        addSubview(trackCheckBox);

        actionButton.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        actionButton.setTitle(getDisplayText("load"), UIControl.State.ALL);
        actionButton.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        actionButton.setFrame(new CGRect(23, 4, width - 54, 20));
        actionButton.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, SkinLibraryWindow::loadOrSaveItem);
        group2.addSubview(actionButton);

        UIImageView bg2 = new UIImageView(new CGRect(0, 4, 18, 18));
        UIImageView bg3 = new UIImageView(new CGRect(width - 26, 0, 26, 26));
        bg2.setOpaque(false);
        bg3.setOpaque(false);
        bg2.setImage(UIImage.of(ModTextures.SKIN_LIBRARY).uv(0, 162).build());
        bg3.setImage(UIImage.of(ModTextures.SKIN_LIBRARY).uv(18, 154).build());
        group2.addSubview(bg2);
        group2.addSubview(bg3);
    }

    private void setupFileView(CGRect rect) {
        float width = rect.getWidth();

        UIView group1 = new UIView(new CGRect(rect.getMinX(), rect.getMinY(), width, 20));
        group1.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        addSubview(group1);

        fileList.setFrame(rect.insetBy(22, 0, 0, 0));
        fileList.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        fileList.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinLibraryWindow::selectFile);
        addSubview(fileList);

        searchTextField.setFrame(new CGRect(0, 2, width - 86, 16));
        searchTextField.setPlaceholder(getDisplayText("label.typeToSearch"));
        searchTextField.setAutoresizingMask(AutoresizingMask.flexibleWidth);
        searchTextField.setMaxLength(255);
        searchTextField.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinLibraryWindow::reloadData);
        group1.addSubview(searchTextField);

        skinTypeList.setFrame(new CGRect(rect.getMaxX() - 80, rect.getMinY() + 2, 80, 16));
        skinTypeList.setMaxRows(12);
        skinTypeList.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin);
        skinTypeList.reloadSkins(SkinTypes.values());
        skinTypeList.setSelectedSkin(skinType);
        skinTypeList.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            self.skinType = self.skinTypeList.selectedSkin();
            self.reloadData(c);
        });
        addSubview(skinTypeList);
    }

    @Override
    public void deinit() {
        super.deinit();
        this.libraryManager.removeListener(this);
    }

    @Override
    public void libraryDidReload(ISkinLibrary library) {
        RenderSystem.recordRenderCall(() -> {
            if (selectedLibrary == library) {
                reloadData(this);
            }
            reloadStatus();
        });
    }

    public void reloadStatus() {
        boolean isFile = selectedFile != null && (!selectedFile.isDirectory() || !selectedFile.getName().equals(".."));
        boolean isLoadable = isFile && !selectedFile.isDirectory();
        boolean isAuthorized = isAuthorized();
        remotePublicButton.setEnabled(libraryManager.getPublicSkinLibrary().isReady());
        remotePrivateButton.setEnabled(libraryManager.getPrivateSkinLibrary().isReady());
        deleteButton.setEnabled(isAuthorized && isFile);
        newFolderButton.setEnabled(isAuthorized);
        openFolderButton.setEnabled(libraryManager.getLocalSkinLibrary() == selectedLibrary);
        if (hasInputSkin()) {
            actionButton.setEnabled(true);
            actionButton.setTitle(getDisplayText("save"), UIControl.State.ALL);
        } else {
            actionButton.setEnabled(isLoadable);
            actionButton.setTitle(getDisplayText("load"), UIControl.State.ALL);
        }
    }

    public void reloadInputName() {
        ItemStack itemStack = menu.getInputStack();
        if (this.lastInputItem == itemStack) {
            return;
        }
        this.lastInputItem = itemStack;
        String name = null;
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin != null) {
            name = bakedSkin.getSkin().getCustomName();
        }
        this.nameTextField.setText(Strings.nullToEmpty(name));
    }

    public void reloadData(Object value) {
        String keyword = searchTextField.text();
        ArrayList<SkinLibraryFile> results = selectedLibrary.search(keyword, skinType, selectedPath);
        fileList.setSelectedItem(null);
        fileList.reloadData(new ArrayList<>(results));
        fileList.setContentOffset(contentOffsets.getOrDefault(selectedPath, CGPoint.ZERO));
    }

    private NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key));
    }

    private NSString getDisplayText(String key, Object... args) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key, args));
    }

    private void selectLibrary(UIControl sender) {
        SkinLibrary newLibrary = libraryManager.getLocalSkinLibrary();
        if (sender == remotePublicButton) {
            newLibrary = libraryManager.getPublicSkinLibrary();
        }
        if (sender == remotePrivateButton) {
            newLibrary = libraryManager.getPrivateSkinLibrary();
            newLibrary.setRootPath("/private/" + menu.getPlayer().getStringUUID());
        }
        if (!newLibrary.isReady()) {
            newLibrary = libraryManager.getLocalSkinLibrary();
        }
        contentOffsets.clear();
        selectedLibrary = newLibrary;
        setSelectedPath(newLibrary.getRootPath());
        if (isAuthorized()) {
            deleteButton.setTooltip(getDisplayText("rollover.deleteSkinSelect"), UIControl.State.DISABLED);
            newFolderButton.setTooltip(null, UIControl.State.DISABLED);
        } else {
            deleteButton.setTooltip(getDisplayText("rollover.unauthorized"), UIControl.State.DISABLED);
            newFolderButton.setTooltip(getDisplayText("rollover.unauthorized"), UIControl.State.DISABLED);
        }
        libraryDidReload(newLibrary);
    }

    private void addFolder(UIControl sender) {
        InputDialog dialog = new InputDialog();
        dialog.setTitle(getDisplayText("dialog.newFolder.title"));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setPlaceholder(getDisplayText("dialog.newFolder.enterFolderName"));
        dialog.setMessage(getDisplayText("dialog.newFolder.invalidFolderName"));
        dialog.setConfirmText(getDisplayText("dialog.newFolder.create"));
        dialog.setCancelText(getDisplayText("dialog.newFolder.close"));
        dialog.setVerifier(value -> value.replaceAll("[:\\\\/]|^[.]+$", "_").equals(value));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                String newPath = SkinFileUtils.normalize(SkinFileUtils.concat(selectedPath, dialog.value()), true);
                selectedLibrary.mkdir(newPath);
            }
        });
    }

    private void openFolder(UIControl sender) {
        Util.getPlatform().openFile(EnvironmentManager.getSkinLibraryDirectory());
    }

    private void backFolder(UIControl sender) {
        ISkinLibrary.Entry entry = fileList.getItem(0);
        if (entry != null && entry.isDirectory() && entry.getName().equals("..")) {
            setSelectedPath(entry.getPath());
            reloadData(sender);
        }
    }

    private void loadOrSaveItem(UIControl button) {
        if (!menu.getOutputStack().isEmpty()) {
            return; // output has many items.
        }
        SkinDescriptor descriptor = SkinDescriptor.of(menu.getInputStack());
        if (descriptor.isEmpty()) {
            loadSkin();
            return;
        }
        String newName = nameTextField.text();
        if (newName.isEmpty()) {
            toast(getDisplayText("error.noFileName"));
            return; // must input name
        }
        String newPath = SkinFileUtils.normalize(SkinFileUtils.concat(selectedPath, newName + Constants.EXT), true);
        if (selectedLibrary.get(newPath) != null) {
            if (!isAuthorized()) {
                toast(getDisplayText("error.illegalOperation"));
                return;
            }
            overwriteItem(newPath, () -> saveSkin(descriptor, newPath));
            return;
        }
        saveSkin(descriptor, newPath);
    }

    private void removeItem(UIControl sender) {
        if (!(selectedFile instanceof SkinLibraryFile) || !isAuthorized()) {
            return;
        }
        SkinLibraryFile file = (SkinLibraryFile) selectedFile;

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setConfirmText(getDisplayText("dialog.delete.delete"));
        dialog.setCancelText(getDisplayText("dialog.delete.close"));
        dialog.setMessage(getDisplayText("dialog.delete.deleteFile", file.getName()));
        if (file.isDirectory()) {
            dialog.setMessage(getDisplayText("dialog.delete.deleteFolder", file.getName()));
        }
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                selectedLibrary.delete(file);
            }
        });
    }

    private void renameItem(String sender) {
        if (!(selectedFile instanceof SkinLibraryFile) || !isAuthorized()) {
            return;
        }
        SkinLibraryFile file = (SkinLibraryFile) selectedFile;
        if (sender.equals(file.getName())) {
            return; // not changes.
        }
        String ext = file.isDirectory() ? "" : Constants.EXT;
        String newPath = SkinFileUtils.normalize(file.getPath() + "/../" + sender + ext, true);
        if (selectedLibrary.get(newPath) != null) {
            overwriteItem(newPath, () -> selectedLibrary.rename(file, newPath));
            return;
        }
        selectedLibrary.rename(file, newPath);
    }

    private void overwriteItem(String path, Runnable handler) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(getDisplayText("dialog.overwrite.title"));
        dialog.setMessage(getDisplayText("dialog.overwrite.overwriteFile", SkinFileUtils.getBaseName(path)));
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.setConfirmText(getDisplayText("dialog.overwrite.ok"));
        dialog.setCancelText(getDisplayText("dialog.overwrite.close"));
        dialog.showInView(this, () -> {
            if (!dialog.isCancelled()) {
                handler.run();
            }
        });
    }

    private void toast(NSString message) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setTitle(new NSString(TranslateUtils.title("inventory.armourers_workshop.skin-library-global.panel.info")));
        dialog.setMessage(message);
        dialog.setMessageColor(new UIColor(0xff5555));
        dialog.showInView(this);
    }

    private void refreshLibrary(UIControl sender) {
        selectedLibrary.reload();
    }

    private void selectFile(UIControl sender) {
        ISkinLibrary.Entry oldValue = selectedFile;
        ISkinLibrary.Entry newValue = fileList.getSelectedItem();
        selectedFile = newValue;
        boolean isFile = newValue != null && (!newValue.isDirectory() || !newValue.getName().equals(".."));
        if (isFile) {
            nameTextField.setText(newValue.getName());
        } else {
            nameTextField.setText("");
        }
        reloadStatus();
        if (newValue != null && newValue.isDirectory() && oldValue == newValue) {
            setSelectedPath(newValue.getPath());
            reloadData(sender);
        }
    }

    private UIButton buildIconButton(int x, int y, int u, int v, int width, int height) {
        UIButton button = new UIButton(new CGRect(x, y, width, height));
        button.setImage(ModTextures.iconImage(u, v, width, height, ModTextures.SKIN_LIBRARY), UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        return button;
    }

    private void saveSkin(SkinDescriptor descriptor, String path) {
        // check skin load status
        ModLog.debug("save skin of '{}' to '{}'", descriptor.getIdentifier(), path);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null || !menu.shouldSaveStack()) {
            ModLog.debug("can't save unbaked skin of '{}'", descriptor);
            return; // skin not ready for using
        }
        // save 1: copy local skin to local library
        // save 2: upload local skin to server library
        // save 3: copy server skin to server library
        // save 4: download server skin to local library
        SaveSkinPacket packet = new SaveSkinPacket(descriptor.getIdentifier(), selectedLibrary.getNamespace() + ":" + path);
        if (!packet.isReady(playerInventory.player)) {
            ModLog.debug("can't save skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkManager.sendToServer(packet);
    }

    private void loadSkin() {
        if (selectedFile == null || selectedFile.isDirectory() || !menu.shouldLoadStack()) {
            return;
        }
        DataDomain source = DataDomain.DATABASE;
        if (trackCheckBox.isSelected()) {
            source = DataDomain.DATABASE_LINK;
        }
        // check skin load status
        String identifier = selectedFile.getSkinIdentifier();
        SkinDescriptor descriptor = new SkinDescriptor(identifier, selectedFile.getSkinType(), ColorScheme.EMPTY);
        ModLog.debug("load skin of '{}'", identifier);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null) {
            ModLog.debug("can't load unbaked skin of '{}'", identifier);
            return; // skin not ready for using
        }
        // load 1: upload local skin to database
        // load 2: copy server skin to database
        // load 3: make item stack(db/link)
        SaveSkinPacket packet = new SaveSkinPacket(descriptor.getIdentifier(), source.normalize(""));
        if (!packet.isReady(playerInventory.player)) {
            ModLog.debug("can't load skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkManager.sendToServer(packet);
    }

    private void setSelectedPath(String newSelectedPath) {
        if (Objects.equals(selectedPath, newSelectedPath)) {
            return;
        }
        contentOffsets.put(selectedPath, fileList.contentOffset());
        // when enter a new subdirectory,
        // we need to clear the content offset.
        if (newSelectedPath.startsWith(selectedPath)) {
            contentOffsets.remove(newSelectedPath);
        }
        selectedPath = newSelectedPath;
    }

    private boolean hasInputSkin() {
        return !SkinDescriptor.of(menu.getInputStack()).isEmpty();
    }

    private boolean isAuthorized() {
        // op can manage the public folder.
        if (selectedLibrary == libraryManager.getPublicSkinLibrary()) {
            return libraryManager.shouldMaintenanceFile(inventory.player);
        }
        return true;
    }
}
