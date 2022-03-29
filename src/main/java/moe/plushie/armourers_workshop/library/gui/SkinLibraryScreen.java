package moe.plushie.armourers_workshop.library.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.*;
import moe.plushie.armourers_workshop.core.handler.ItemTooltipHandler;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.container.SkinLibraryContainer;
import moe.plushie.armourers_workshop.library.data.SkinLibrary;
import moe.plushie.armourers_workshop.library.data.SkinLibraryFile;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SkinLibraryScreen extends AWAbstractContainerScreen<SkinLibraryContainer> {

    protected int inventoryTop = 0;
    protected int inventoryLeft = 0;
    protected int inventoryRight = 0;

    protected int fileListTop = 0;
    protected int fileListLeft = 0;
    protected int fileListRight = 0;

    protected Button pendingTooltipButton;

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
    protected String selectedPath = "/";
    protected ISkinLibrary.Entry selectedFile = null;


    public SkinLibraryScreen(SkinLibraryContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
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

        this.localFileButton = addIconButton(inventoryLeft, 23, 0, 0, 50, 30, "rollover.localFiles", button -> {
        });
        this.remotePublicButton = addIconButton(inventoryLeft + 55, 23, 0, 31, 50, 30, "rollover.remotePublicFiles", "rollover.notOnServer", button -> {
        });
        this.remotePrivateButton = addIconButton(inventoryRight - 50, 23, 0, 62, 50, 30, "rollover.remotePrivateFiles", "rollover.notOnServer", button -> {
        });

        this.nameTextField = addTextField(inventoryLeft + 2, 59, menu.inventoryWidth - 4, 20, "label.enterFileName");
        this.nameTextField.setReturnHandler(this::changeFilename);
        this.searchTextField = addTextField(fileListLeft + 1, 25, (fileListRight - fileListLeft - 86), 14, "label.typeToSearch");
        this.searchTextField.setResponder(this::changeSearchKeyword);

        this.openFolderButton = addIconButton(inventoryLeft, 85, 0, 93, 24, 24, "rollover.openLibraryFolder", this::openLibrary);
        this.refreshButton = addIconButton(inventoryLeft + 25, 85, 73, 93, 24, 24, "rollover.refresh", this::refreshLibrary);
        this.deleteButton = addIconButton(inventoryLeft + 25 * 2, 85, 0, 118, 24, 24, "rollover.deleteSkin", "rollover.deleteSkinSelect", this::removeLibraryItem);
        this.newFolderButton = addIconButton(inventoryLeft + 25 * 3, 85, 73, 118, 24, 24, "rollover.newFolder", this::newLibraryItem);
        this.backButton = addIconButton(inventoryRight - 24, 85, 146, 93, 24, 24, "rollover.back", this::backUpLibrary);

        this.checkBox = addOption(inventoryLeft, inventoryTop - 41, "trackFile");
        this.actionButton = addTextButton(inventoryLeft + 23, inventoryTop - 28, menu.inventoryWidth - 54, 20, "load", b -> {
        });

        this.fileList = addFileList(fileListLeft, fileListTop, fileListRight - fileListLeft, height - 5 - fileListTop);
        this.skinTypeList = addComboList(fileListRight - 80, 24, 80, 16);

        this.refresh();
        this.reloadStatus();
    }

    public void reloadStatus() {
        this.remotePublicButton.setEnabled(false);
        this.remotePrivateButton.setEnabled(false);
        this.deleteButton.setEnabled(false);
    }

    public void refresh() {
        ArrayList<SkinLibraryFile> results = buildSearchResult(searchTextField.getValue(), skinType);
        this.fileList.setSelectedItem(null);
        this.fileList.reloadData(new ArrayList<>(results));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        for (IGuiEventListener widget : children) {
            if (widget instanceof AWTextField) {
                ((AWTextField) widget).render(matrixStack, mouseX, mouseY, partialTicks);
                GL11.glColor4f(1f, 1f, 1f, 1f); //
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
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xCCCCCC);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderTooltip(matrixStack, mouseX, mouseY);
        if (pendingTooltipButton != null) {
            super.renderTooltip(matrixStack, pendingTooltipButton.getMessage(), mouseX, mouseY);
            pendingTooltipButton = null;
        }
        SkinDescriptor descriptor = fileList.getHoveredSkin(mouseX, mouseY);
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin != null) {
            int size = 144;
            int dy = MathHelper.clamp(mouseY - 16, 0, height - size);
            int dx = MathHelper.clamp(fileListLeft - size, 0, width - size);
            ArrayList<ITextComponent> tooltips = ItemTooltipHandler.createSkinInfo(bakedSkin);
            GuiUtils.drawContinuousTexturedBox(matrixStack, RenderUtils.TEX_GUI_PREVIEW, dx, dy, 0, 0, size, size, 62, 62, 4, 400);
            RenderUtils.drawText(matrixStack, font, tooltips, dx + 4, dy + 4, size - 8, size - 8, 7, 400, 15728880);
            IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            SkinItemRenderer.renderSkin(descriptor, dx, dy, size, size, 150, 45, 0, matrixStack, buffers);
            buffers.endBatch();
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        nameTextField.setFocus(false);
        searchTextField.setFocus(false);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected ITextComponent getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.skin-library" + "." + key);
    }

    protected void openLibrary(Button sender) {
        Util.getPlatform().openFile(AWCore.getSkinLibraryDirectory());
    }

    protected void newLibraryItem(Button sender) {

    }

    protected void removeLibraryItem(Button sender) {

    }

    protected void refreshLibrary(Button sender) {
        refresh();
    }

    private ArrayList<SkinLibraryFile> buildSearchResult(String keyword, ISkinType skinType) {
        SkinLibrary library = null;
        SkinLibraryManager libraryManager = SkinLibraryManager.getInstance();
        if (libraryManager instanceof SkinLibraryManager.Client) {
            library = ((SkinLibraryManager.Client) libraryManager).getLocalSkinLibrary();
        }
        if (library != null) {
            return library.search(keyword, skinType, selectedPath);
        }
        return new ArrayList<>();
    }


    private void backUpLibrary(Button sender) {
        ISkinLibrary.Entry entry = fileList.getItem(0);
        if (entry != null && entry.isDirectory() && entry.getName().equals("..")) {
            selectedPath = entry.getPath();
            refresh();
        }
    }

    private void changeFile(Button sender) {
        ISkinLibrary.Entry oldValue = selectedFile;
        ISkinLibrary.Entry newValue = fileList.getSelectedItem();
        setSelectedFile(newValue);
        if (newValue != null && newValue.isDirectory() && oldValue == newValue) {
            selectedPath = newValue.getPath();
            refresh();
        }
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {
        super.setFocused(p_231035_1_);
    }

    public void setSelectedFile(ISkinLibrary.Entry file) {
        selectedFile = file;
        boolean isFile = file != null && (!file.isDirectory() || !file.getName().equals(".."));
        if (isFile) {
            nameTextField.setValue(file.getName());
        } else {
            nameTextField.setValue("");
        }
        nameTextField.moveCursorToStart();
        nameTextField.setHighlightPos(0);
        deleteButton.setEnabled(isFile);
        actionButton.active = isFile && !file.isDirectory();
    }

    private void changeSearchKeyword(String sender) {
        refresh();
    }


    private void changeFilename(String sender) {
        ModLog.debug("change file name to {}", sender);
        refresh();
    }


    private AWComboBox addComboList(int x, int y, int width, int height) {
        int selectedIndex = 0;
        ArrayList<ISkinType> skinTypes = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinType skinType : SkinTypes.values()) {
            ITextComponent title = TranslateUtils.title("skinType." + skinType.getRegistryName());
            if (skinType == SkinTypes.UNKNOWN) {
                title = new StringTextComponent("*");
            }
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(title);
            if (skinType == this.skinType) {
                selectedIndex = items.size();
            }
            items.add(item);
            skinTypes.add(skinType);
        }
        AWComboBox comboBox = new AWComboBox(x, y, width, height, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int newValue = ((AWComboBox) button).getSelectedIndex();
                skinType = skinTypes.get(newValue);
                refresh();
            }
        });
        comboBox.setMaxRowCount(17);
        addButton(comboBox);
        return comboBox;
    }

    private AWFileList addFileList(int x, int y, int width, int height) {
        AWFileList fileList = new AWFileList(x, y, width, height, 14, StringTextComponent.EMPTY, this::changeFile);
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
        Button.ITooltip tooltipRenderer = (button, matrixStack, mouseX, mouseY) -> this.pendingTooltipButton = button;
        AWImageButton button = new AWImageExtendedButton(x, y, width, height, u, v, RenderUtils.TEX_SKIN_LIBRARY, handler, tooltipRenderer, tooltip);
        if (key2 != null) {
            button.setDisabledMessage(getDisplayText(key2));
        }
        addButton(button);
        return button;
    }

    private AWCheckBox addOption(int x, int y, String key) {
        AWCheckBox checkBox = new AWCheckBox(x, y, 9, 9, getDisplayText(key), false, button -> {
        });
        checkBox.setTextColour(0xffffff);
        addButton(checkBox);
        return checkBox;
    }


}
