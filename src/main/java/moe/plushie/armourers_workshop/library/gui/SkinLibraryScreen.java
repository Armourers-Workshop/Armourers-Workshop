package moe.plushie.armourers_workshop.library.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinLibraryListener;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.*;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.SaveSkinPacket;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.container.SkinLibraryContainer;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.apache.commons.io.FilenameUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinLibraryScreen extends AWAbstractContainerScreen<SkinLibraryContainer> implements ISkinLibraryListener {

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

    protected ExtendedButton actionButton;

    protected ISkinType skinType = SkinTypes.UNKNOWN;
    protected ISkinLibrary.Entry selectedFile = null;
    protected String selectedPath;

    protected SkinLibrary selectedLibrary;
    protected SkinLibraryManager.Client libraryManager = SkinLibraryManager.getClient();

    public SkinLibraryScreen(SkinLibraryContainer container, PlayerInventory inventory, ITextComponent title) {
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

    public void reloadData(Object value) {
        String keyword = searchTextField.getValue();
        ArrayList<SkinLibraryFile> results = selectedLibrary.search(keyword, skinType, selectedPath);
        this.fileList.setSelectedItem(null);
        this.fileList.reloadData(new ArrayList<>(results));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        for (IGuiEventListener widget : children) {
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
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xcccccc);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
        SkinDescriptor descriptor = fileList.getHoveredSkin(mouseX, mouseY);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            int size = 144;
            int dy = MathHelper.clamp(mouseY - 16, 0, height - size);
            int dx = MathHelper.clamp(fileListLeft - size, 0, width - size);
            ArrayList<ITextProperties> tooltips = new ArrayList<>(ItemTooltipHandler.createSkinInfo(bakedSkin));
            GuiUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, dx, dy, 0, 0, size, size, 62, 62, 4, 400);
            RenderUtils.drawShadowText(matrixStack, tooltips, dx + 4, dy + 4, size - 8, 400, font, 7, 0xffffff);
            IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            SkinItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, dx, dy, 500, size, size, 150, 45, 0, matrixStack, buffers);
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
    }

    protected ITextComponent getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key);
    }

    protected ITextComponent getDisplayText(String key, Object... args) {
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
                String newPath = FilenameUtils.normalize(FilenameUtils.concat(selectedPath, dialog1.getText()), true);
                selectedLibrary.mkdir(newPath);
            }
        });
    }

    private void openFolder(Button sender) {
        Util.getPlatform().openFile(AWCore.getSkinLibraryDirectory());
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
        String newPath = FilenameUtils.normalize(FilenameUtils.concat(selectedPath, newName + AWConstants.EXT), true);
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
        String ext = file.isDirectory() ? "" : AWConstants.EXT;
        String newPath = FilenameUtils.normalize(file.getPath() + "/../" + sender + ext, true);
        if (selectedLibrary.get(newPath) != null) {
            overwriteItem(newPath, () -> selectedLibrary.rename(file, newPath));
            return;
        }
        selectedLibrary.rename(file, newPath);
    }

    private void toast(ITextComponent message) {
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
        dialog.setMessage(getDisplayText("dialog.overwrite.overwriteFile", FilenameUtils.getBaseName(path)));
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
    public void setFocused(@Nullable IGuiEventListener item) {
        IGuiEventListener oldItem = super.getFocused();
        if (oldItem != item && oldItem instanceof Widget) {
            if (((Widget) oldItem).isFocused()) {
                oldItem.changeFocus(false);
            }
        }
        super.setFocused(item);
    }

    private AWComboBox addComboList(int x, int y, int width, int height) {
        int selectedIndex = 0;
        ArrayList<ISkinType> skinTypes = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinType skinType : SkinTypes.values()) {
            AWComboBox.ComboItem item = new SkinTypeComboItem(skinType);
            if (skinType == this.skinType) {
                selectedIndex = items.size();
            }
            items.add(item);
            skinTypes.add(skinType);
        }
        AWComboBox comboBox = new AWComboBox(x, y, width, height, items, selectedIndex, button -> {
            int newValue = ((AWComboBox) button).getSelectedIndex();
            skinType = skinTypes.get(newValue);
            reloadData(button);
        });
        comboBox.setMaxRowCount(17);
        addButton(comboBox);
        return comboBox;
    }

    private AWFileList addFileList(int x, int y, int width, int height) {
        AWFileList fileList = new AWFileList(x, y, width, height, 14, StringTextComponent.EMPTY, this::selectFile);
        addButton(fileList);
        return fileList;
    }

    private AWTextField addTextField(int x, int y, int width, int height, String key) {
        AWTextField textField = new AWTextField(font, x, y, width, height, getDisplayText(key));
        textField.setMaxLength(255);
        addWidget(textField);
        return textField;
    }

    private ExtendedButton addTextButton(int x, int y, int width, int height, String key, Button.IPressable handler) {
        ITextComponent title = getDisplayText(key);
        ExtendedButton button = new ExtendedButton(x, y, width, height, title, handler);
        addButton(button);
        return button;
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.IPressable handler) {
        return addIconButton(x, y, u, v, width, height, key, null, handler);
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, String key2, Button.IPressable handler) {
        ITextComponent tooltip = getDisplayText(key);
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
        if (!packet.isReady()) {
            ModLog.debug("can't save skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkHandler.getInstance().sendToServer(packet);
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
        if (!packet.isReady()) {
            ModLog.debug("can't load skin of '{}'", descriptor);
            toast(getDisplayText("error.illegalOperation"));
            return;
        }
        NetworkHandler.getInstance().sendToServer(packet);
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

    public static class SkinTypeComboItem extends AWComboBox.ComboItem {

        protected final ISkinType skinType;

        public SkinTypeComboItem(ISkinType skinType) {
            super(getTitleFromSkinType(skinType));
            this.skinType = skinType;
        }

        @Override
        public void renderLabels(MatrixStack matrixStack, int x, int y, int width, int height, boolean isHovered, boolean isTopRender) {
            if (!isTopRender) {
                ResourceLocation texture = AWCore.getItemIcon(skinType);
                if (texture != null) {
                    RenderSystem.enableAlphaTest();
                    RenderUtils.resize(matrixStack, x - 2, y - 1, 0, 0, 9, 9, 16, 16, 16, 16, texture);
                    x += 9;
                }
            }
            super.renderLabels(matrixStack, x, y, width, height, isHovered, isTopRender);
        }

        private static ITextComponent getTitleFromSkinType(ISkinType skinType) {
            if (skinType == SkinTypes.UNKNOWN) {
                return TranslateUtils.title("inventory.armourers_workshop.all");
            }
            return TranslateUtils.title("skinType." + skinType.getRegistryName());
        }
    }
}
