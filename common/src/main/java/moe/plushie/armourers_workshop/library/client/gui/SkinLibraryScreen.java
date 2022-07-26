package moe.plushie.armourers_workshop.library.client.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.gui.widget.*;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.network.SaveSkinPacket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.library.menu.SkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class SkinLibraryScreen extends AWAbstractContainerScreen<SkinLibraryMenu> implements ISkinLibraryListener {

    protected int inventoryTop = 0;
    protected int inventoryLeft = 0;
    protected int inventoryRight = 0;

    protected int fileListTop = 0;
    protected int fileListLeft = 0;
    protected int fileListRight = 0;

    protected AWFileList fileList;

    protected AWImageButton localFileButton;
    protected AWImageButton remotePublicButton;
    protected AWImageButton remotePrivateButton;

    protected AWImageButton openFolderButton;
    protected AWImageButton deleteButton;
    protected AWImageButton refreshButton;
    protected AWImageButton newFolderButton;
    protected AWImageButton backButton;

    protected AWTextField nameTextField;
    protected AWTextField searchTextField;

    protected AWCheckBox checkBox;
    protected AWComboBox skinTypeList;

    protected AWExtendedButton actionButton;

    protected ISkinType skinType = SkinTypes.UNKNOWN;
    protected ISkinLibrary.Entry selectedFile = null;
    protected String selectedPath;
    protected ItemStack lastInputItem;

    protected SkinLibrary selectedLibrary;
    protected SkinLibraryManager.Client libraryManager = SkinLibraryManager.getClient();

    public SkinLibraryScreen(SkinLibraryMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.libraryManager.addListener(this);
        this.selectedLibrary = libraryManager.getLocalSkinLibrary();
        this.selectedPath = selectedLibrary.getRootPath();
    }

    @Override
    protected void init() {
        this.imageWidth = width;
        this.imageHeight = height;
        this.menu.reload(0, 0, width, height);

        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
        this.inventoryLabelX = 5;
        this.inventoryLabelY = imageHeight - 96;
        this.inventoryTop = height - menu.inventoryHeight - 5;
        this.inventoryLeft = 5;
        this.inventoryRight = inventoryLeft + menu.inventoryWidth;
        this.fileListTop = 24 + 16 + 5;
        this.fileListLeft = inventoryRight + 5;
        this.fileListRight = width - 5;

        this.localFileButton = addIconButton(inventoryLeft, 23, 0, 0, 50, 30, "rollover.localFiles", this::selectLibrary);
        this.remotePublicButton = addIconButton(inventoryLeft + 55, 23, 0, 31, 50, 30, "rollover.remotePublicFiles", "rollover.notOnServer", this::selectLibrary);
        this.remotePrivateButton = addIconButton(inventoryRight - 50, 23, 0, 62, 50, 30, "rollover.remotePrivateFiles", "rollover.notOnServer", this::selectLibrary);

        this.nameTextField = addTextField(inventoryLeft + 2, 59, menu.inventoryWidth - 4, 20, "label.enterFileName");
        this.nameTextField.setReturnHandler(this::renameItem);
        this.searchTextField = addTextField(fileListLeft + 1, 25, (fileListRight - fileListLeft - 86), 14, "label.typeToSearch");
        this.searchTextField.setResponder(this::reloadData);

        this.openFolderButton = addIconButton(inventoryLeft, 85, 0, 93, 24, 24, "rollover.openLibraryFolder", this::openFolder);
        this.refreshButton = addIconButton(inventoryLeft + 25, 85, 73, 93, 24, 24, "rollover.refresh", this::refreshLibrary);
        this.deleteButton = addIconButton(inventoryLeft + 25 * 2, 85, 0, 118, 24, 24, "rollover.deleteSkin", "rollover.deleteSkinSelect", this::removeItem);
        this.newFolderButton = addIconButton(inventoryLeft + 25 * 3, 85, 73, 118, 24, 24, "rollover.newFolder", this::addFolder);
        this.backButton = addIconButton(inventoryRight - 24, 85, 146, 93, 24, 24, "rollover.back", this::backFolder);

        this.checkBox = addOption(inventoryLeft, inventoryTop - 41, "trackFile");
        this.actionButton = addTextButton(inventoryLeft + 23, inventoryTop - 28, menu.inventoryWidth - 54, 20, "load", this::loadOrSaveItem);

        this.fileList = addFileList(fileListLeft, fileListTop, fileListRight - fileListLeft, height - 5 - fileListTop);
        this.skinTypeList = addComboList(fileListRight - 80, 24, 80, 16);

        this.reloadData(this);
        this.reloadStatus();
    }

    @Override
    public void removed() {
        super.removed();
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
        if (fileList == null) {
            return;
        }
        boolean isFile = selectedFile != null && (!selectedFile.isDirectory() || !selectedFile.getName().equals(".."));
        boolean isLoadable = isFile && !selectedFile.isDirectory();
        boolean isAuthorized = isAuthorized();
        this.remotePublicButton.setEnabled(libraryManager.getPublicSkinLibrary().isReady());
        this.remotePrivateButton.setEnabled(libraryManager.getPrivateSkinLibrary().isReady());
        this.deleteButton.setEnabled(isAuthorized && isFile);
        this.newFolderButton.setEnabled(isAuthorized);
        this.openFolderButton.setEnabled(libraryManager.getLocalSkinLibrary() == selectedLibrary);
        if (hasInputSkin()) {
            this.actionButton.active = true;
            this.actionButton.setMessage(getDisplayText("save"));
        } else {
            this.actionButton.active = isLoadable;
            this.actionButton.setMessage(getDisplayText("load"));
        }
    }

    public void reloadInputName() {
        ItemStack itemStack = menu.getInputStack();
        if (this.lastInputItem == itemStack) {
            return;
        }
        this.lastInputItem = itemStack;
        String name = null;
        BakedSkin bakedSkin = BakedSkin.of(itemStack);
        if (bakedSkin != null) {
            name = bakedSkin.getSkin().getCustomName();
        }
        this.nameTextField.setValue(Strings.nullToEmpty(name));
    }

    public void reloadData(Object value) {
        String keyword = searchTextField.getValue();
        ArrayList<SkinLibraryFile> results = selectedLibrary.search(keyword, skinType, selectedPath);
        this.fileList.setSelectedItem(null);
        this.fileList.reloadData(new ArrayList<>(results));
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        for (GuiEventListener widget : children) {
            if (widget instanceof AWTextField) {
                ((AWTextField) widget).render(matrixStack, mouseX, mouseY, partialTicks);
                GL11.glColor4f(1f, 1f, 1f, 1f);
            }
        }
        RenderSystem.enableBlend();

        RenderUtils.bind(RenderUtils.TEX_SKIN_LIBRARY);

        RenderUtils.blit(matrixStack, inventoryLeft, inventoryTop, 0, 180, menu.inventoryWidth, menu.inventoryHeight);
        RenderUtils.blit(matrixStack, inventoryLeft, inventoryTop - 27, 0, 162, 18, 18);
        RenderUtils.blit(matrixStack, inventoryRight - 26, inventoryTop - 31, 18, 154, 26, 26);

        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xcccccc);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
        SkinDescriptor descriptor = fileList.getHoveredSkin(mouseX, mouseY);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            int size = 144;
            int dy = MathUtils.clamp(mouseY - 16, 0, height - size);
            int dx = MathUtils.clamp(fileListLeft - size, 0, width - size);
            ArrayList<FormattedText> tooltips = new ArrayList<>(ItemTooltipManager.createSkinInfo(bakedSkin));
            RenderUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, dx, dy, 0, 0, size, size, 62, 62, 4, 400);
            RenderUtils.drawShadowText(matrixStack, tooltips, dx + 4, dy + 4, size - 8, 400, font, 7, 0xffffff);
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, dx, dy, 500, size, size, 150, 45, 0, matrixStack, buffers);
            buffers.endBatch();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        nameTextField.setFocus(false);
        searchTextField.setFocus(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
        super.slotClicked(p_184098_1_, p_184098_2_, p_184098_3_, p_184098_4_);
        reloadStatus();
        reloadInputName();
    }

    protected Component getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key);
    }

    protected Component getDisplayText(String key, Object... args) {
        return TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key, args);
    }

    private void selectLibrary(Button sender) {
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
        selectedLibrary = newLibrary;
        selectedPath = newLibrary.getRootPath();
        if (isAuthorized()) {
            this.deleteButton.setDisabledMessage(getDisplayText("rollover.deleteSkinSelect"));
            this.newFolderButton.setDisabledMessage(null);
        } else {
            this.deleteButton.setDisabledMessage(getDisplayText("rollover.unauthorized"));
            this.newFolderButton.setDisabledMessage(getDisplayText("rollover.unauthorized"));
        }
        libraryDidReload(newLibrary);
    }

    private void addFolder(Button sender) {
        AWInputDialog dialog = new AWInputDialog(getDisplayText("dialog.newFolder.title"));
        dialog.setMessageColor(0xffff5555);
        dialog.setPlaceholderText(getDisplayText("dialog.newFolder.enterFolderName"));
        dialog.setMessage(getDisplayText("dialog.newFolder.invalidFolderName"));
        dialog.setConfirmText(getDisplayText("dialog.newFolder.create"));
        dialog.setCancelText(getDisplayText("dialog.newFolder.close"));
        dialog.setValueTester(value -> value.replaceAll("[:\\\\/]|^[.]+$", "_").equals(value));
        present(dialog, dialog1 -> {
            if (!dialog1.isCancelled()) {
                String newPath = SkinFileUtils.normalize(SkinFileUtils.concat(selectedPath, dialog1.getText()), true);
                selectedLibrary.mkdir(newPath);
            }
        });
    }

    private void openFolder(Button sender) {
        Util.getPlatform().openFile(EnvironmentManager.getSkinLibraryDirectory());
    }

    private void backFolder(Button sender) {
        ISkinLibrary.Entry entry = fileList.getItem(0);
        if (entry != null && entry.isDirectory() && entry.getName().equals("..")) {
            selectedPath = entry.getPath();
            reloadData(sender);
        }
    }

    private void loadOrSaveItem(Button button) {
        if (!menu.getOutputStack().isEmpty()) {
            return; // output has many items.
        }
        SkinDescriptor descriptor = SkinDescriptor.of(menu.getInputStack());
        if (descriptor.isEmpty()) {
            loadSkin();
            return;
        }
        String newName = nameTextField.getValue();
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

    private void removeItem(Button sender) {
        if (!(selectedFile instanceof SkinLibraryFile) || !isAuthorized()) {
            return;
        }
        SkinLibraryFile file = (SkinLibraryFile) selectedFile;
        AWConfirmDialog dialog = new AWConfirmDialog(getDisplayText("dialog.delete.title"));
        dialog.setMessageColor(0xffff5555);
        dialog.setConfirmText(getDisplayText("dialog.delete.delete"));
        dialog.setCancelText(getDisplayText("dialog.delete.close"));
        dialog.setMessage(getDisplayText("dialog.delete.deleteFile", file.getName()));
        if (file.isDirectory()) {
            dialog.setMessage(getDisplayText("dialog.delete.deleteFolder", file.getName()));
        }
        present(dialog, dialog1 -> {
            if (!dialog1.isCancelled()) {
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

    private void toast(Component message) {
        AWConfirmDialog dialog = new AWConfirmDialog(TranslateUtils.title("inventory.armourers_workshop.skin-library-global.panel.info"));
        dialog.setMessageColor(0xffff5555);
        dialog.setMessage(message);
        present(dialog, null);
    }

    private void overwriteItem(String path, Runnable handler) {
        AWConfirmDialog dialog = new AWConfirmDialog(getDisplayText("dialog.overwrite.title"));
        dialog.setMessageColor(0xffff5555);
        dialog.setConfirmText(getDisplayText("dialog.overwrite.ok"));
        dialog.setCancelText(getDisplayText("dialog.overwrite.close"));
        dialog.setMessage(getDisplayText("dialog.overwrite.overwriteFile", SkinFileUtils.getBaseName(path)));
        present(dialog, r -> {
            if (!r.isCancelled()) {
                handler.run();
            }
        });
    }

    private void refreshLibrary(Button sender) {
        selectedLibrary.reload();
    }

    private void selectFile(Button sender) {
        ISkinLibrary.Entry oldValue = selectedFile;
        ISkinLibrary.Entry newValue = fileList.getSelectedItem();
        selectedFile = newValue;
        boolean isFile = newValue != null && (!newValue.isDirectory() || !newValue.getName().equals(".."));
        if (isFile) {
            nameTextField.setValue(newValue.getName());
        } else {
            nameTextField.setValue("");
        }
        reloadStatus();
        if (newValue != null && newValue.isDirectory() && oldValue == newValue) {
            selectedPath = newValue.getPath();
            reloadData(sender);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener item) {
        GuiEventListener oldItem = super.getFocused();
        if (oldItem != item && oldItem instanceof AbstractWidget) {
            if (((AbstractWidget) oldItem).isFocused()) {
                oldItem.changeFocus(false);
            }
        }
        super.setFocused(item);
    }

    private AWComboBox addComboList(int x, int y, int width, int height) {
        AWComboBox comboBox = new AWSkinTypeComboBox(x, y, width, height, SkinTypes.values(), this.skinType, newSkinType -> {
            skinType = newSkinType;
            reloadData(null);
        });
        comboBox.setMaxRowCount(17);
        addButton(comboBox);
        return comboBox;
    }

    private AWFileList addFileList(int x, int y, int width, int height) {
        AWFileList fileList = new AWFileList(x, y, width, height, 14, TextComponent.EMPTY, this::selectFile);
        addButton(fileList);
        return fileList;
    }

    private AWTextField addTextField(int x, int y, int width, int height, String key) {
        AWTextField textField = new AWTextField(font, x, y, width, height, getDisplayText(key));
        textField.setMaxLength(255);
        addButton(textField);
        return textField;
    }

    private AWExtendedButton addTextButton(int x, int y, int width, int height, String key, Button.OnPress handler) {
        Component title = getDisplayText(key);
        AWExtendedButton button = new AWExtendedButton(x, y, width, height, title, handler);
        addButton(button);
        return button;
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.OnPress handler) {
        return addIconButton(x, y, u, v, width, height, key, null, handler);
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, String key2, Button.OnPress handler) {
        Component tooltip = getDisplayText(key);
        AWImageButton button = new AWImageExtendedButton(x, y, width, height, u, v, RenderUtils.TEX_SKIN_LIBRARY, handler, this::addHoveredButton, tooltip);
        if (key2 != null) {
            button.setDisabledMessage(getDisplayText(key2));
        }
        addButton(button);
        return button;
    }

    private AWCheckBox addOption(int x, int y, String key) {
        AWCheckBox checkBox = new AWCheckBox(x, y, 9, 9, getDisplayText(key), false, b -> reloadStatus());
        checkBox.setTextColour(0xffffff);
        addButton(checkBox);
        return checkBox;
    }

    private void saveSkin(SkinDescriptor descriptor, String path) {
        // check skin load status
        ModLog.debug("save skin of '{}' to '{}'", descriptor.getIdentifier(), path);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null || !menu.shouldSaveStack()) {
            ModLog.debug("can't save unbaked skin of '{}'", descriptor);
            return; // skin not ready for using
        }
        // save 1: copy local skin to local library
        // save 2: upload local skin to server library
        // save 3: copy server skin to server library
        // save 4: download server skin to local library
        SaveSkinPacket packet = new SaveSkinPacket(descriptor.getIdentifier(), selectedLibrary.getNamespace() + ":" + path);
        if (!packet.isReady(inventory.player)) {
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
        if (checkBox.isSelected()) {
            source = DataDomain.DATABASE_LINK;
        }
        // check skin load status
        String identifier = selectedFile.getNamespace() + ":" + selectedFile.getPath();
        SkinDescriptor descriptor = new SkinDescriptor(identifier, selectedFile.getSkinType(), ColorScheme.EMPTY);
        ModLog.debug("load skin of '{}'", identifier);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            ModLog.debug("can't load unbaked skin of '{}'", identifier);
            return; // skin not ready for using
        }
        // load 1: upload local skin to database
        // load 2: copy server skin to database
        // load 3: make item stack(db/link)
        SaveSkinPacket packet = new SaveSkinPacket(descriptor.getIdentifier(), source.normalize(""));
        if (!packet.isReady(inventory.player)) {
            ModLog.debug("can't load skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkManager.sendToServer(packet);
    }

    private boolean hasInputSkin() {
        return !SkinDescriptor.of(menu.getInputStack()).isEmpty();
    }

    private boolean isAuthorized() {
        // op can manage the public folder.
        if (selectedLibrary == libraryManager.getPublicSkinLibrary()) {
            return ModConfig.Common.allowLibraryRemoteManage && menu.getPlayer().hasPermissions(5);
        }
        return true;
    }
}
