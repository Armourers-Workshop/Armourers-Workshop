package moe.plushie.armourers_workshop.client.gui.skinlibrary;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiFileListItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiScrollbar;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.controls.IGuiListItem;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinLibrary;
import moe.plushie.armourers_workshop.common.library.ILibraryManager;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.library.LibraryFileList;
import moe.plushie.armourers_workshop.common.library.LibraryFileType;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.SkinUploadHelper;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiLoadSaveArmour.LibraryPacketType;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand;
import moe.plushie.armourers_workshop.common.skin.ISkinHolder;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSkinLibrary extends ModGuiContainer<ContainerSkinLibrary> implements IDialogCallback {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.GUI_SKIN_LIBRARY);
    private static final int BUTTON_ID_LOAD_SAVE = 0;

    private static final int TITLE_HEIGHT = 15;
    private static final int PADDING = 5;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;

    private static int scrollAmount = 0;
    private static ISkinType lastSkinType;
    private static String lastSearchText = "";
    private static String currentFolder = "/";
    private static boolean trackFile = false;

    private TileEntitySkinLibrary armourLibrary;
    private final EntityPlayer player;
    private GuiIconButton fileSwitchlocal;
    private GuiIconButton fileSwitchRemotePublic;
    private GuiIconButton fileSwitchRemotePrivate;
    public static LibraryFileType fileSwitchType;
    private GuiList fileList;
    private GuiButtonExt loadSaveButton;

    private GuiIconButton openFolderButton;
    private GuiIconButton deleteButton;
    private GuiIconButton reloadButton;
    private GuiIconButton newFolderButton;
    private GuiIconButton backButton;

    private GuiScrollbar scrollbar;
    private GuiLabeledTextField filenameTextbox;
    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownList;
    private GuiCheckBox checkBoxTrack;

    private boolean isNEIVisible;

    private int neiBump = 18;

    public GuiSkinLibrary(InventoryPlayer invPlayer, TileEntitySkinLibrary armourLibrary) {
        super(new ContainerSkinLibrary(invPlayer, armourLibrary));
        player = invPlayer.player;
        this.armourLibrary = armourLibrary;
        isNEIVisible = ModAddonManager.addonNEI.isVisible();
    }

    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();

        String guiName = armourLibrary.getName();

        int slotSize = 18;
        
        neiBump = 0;
        if (ModAddonManager.addonNEI.isVisible()) {
            neiBump = 18;
        }
        if (ModAddonManager.addonJEI.isModLoaded()) {
            neiBump = 20;
        }
        
        // Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = inventorySlots.inventorySlots.get(x);
            slot.yPos = this.height + 1 - PADDING - slotSize - neiBump;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.yPos = this.height + 1 - INVENTORY_HEIGHT - PADDING + y * slotSize - neiBump;
            }
        }

        // Move library inventory slots.
        Slot slot = inventorySlots.inventorySlots.get(36);
        slot.yPos = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
        slot.xPos = PADDING + 1;
        slot = inventorySlots.inventorySlots.get(37);
        slot.yPos = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
        slot.xPos = PADDING + INVENTORY_WIDTH - slotSize - 3;

        buttonList.clear();

        fileSwitchlocal = new GuiIconButton(this, -1, PADDING, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.localFiles"), texture);
        fileSwitchRemotePublic = new GuiIconButton(this, -1, PADDING + 51 + PADDING, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.remotePublicFiles"), texture);
        fileSwitchRemotePrivate = new GuiIconButton(this, -1, PADDING + 102 + PADDING * 2, TITLE_HEIGHT + PADDING, 50, 30, GuiHelper.getLocalizedControlName(guiName, "rollover.remotePrivateFiles"), texture);
        fileSwitchlocal.setIconLocation(0, 0, 50, 30);
        fileSwitchRemotePublic.setIconLocation(0, 31, 50, 30);
        fileSwitchRemotePrivate.setIconLocation(0, 62, 50, 30);

        openFolderButton = new GuiIconButton(this, 4, PADDING, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.openLibraryFolder"), texture);
        openFolderButton.setIconLocation(0, 93, 24, 24);
        buttonList.add(openFolderButton);

        reloadButton = new GuiIconButton(this, -1, PADDING * 2 + 20, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.refresh"), texture);
        reloadButton.setIconLocation(73, 93, 24, 24);
        buttonList.add(reloadButton);

        deleteButton = new GuiIconButton(this, -1, PADDING * 3 + 40, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.deleteSkin"), texture);
        deleteButton.setIconLocation(0, 118, 24, 24);
        buttonList.add(deleteButton);
        deleteButton.enabled = false;

        newFolderButton = new GuiIconButton(this, -1, PADDING * 4 + 60, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.newFolder"), texture);
        newFolderButton.setIconLocation(73, 118, 24, 24);
        buttonList.add(newFolderButton);

        backButton = new GuiIconButton(this, -1, INVENTORY_WIDTH + PADDING - 24, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.back"), texture);
        backButton.setIconLocation(146, 93, 24, 24);
        buttonList.add(backButton);

        int listWidth = this.width - INVENTORY_WIDTH - PADDING * 5;
        int listHeight = this.height - TITLE_HEIGHT - 14 - PADDING * 3;
        int typeSwitchWidth = 80;

        listWidth = MathHelper.clamp(listWidth, 0, 200);

        fileList = new GuiList(INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 14 + PADDING * 2, listWidth, listHeight, 14);

        if (mc.isSingleplayer()) {
            fileSwitchRemotePublic.enabled = false;
            fileSwitchRemotePrivate.enabled = false;
            fileSwitchRemotePublic.setDisableText(GuiHelper.getLocalizedControlName(guiName, "rollover.notOnServer"));
            fileSwitchRemotePrivate.setDisableText(GuiHelper.getLocalizedControlName(guiName, "rollover.notOnServer"));
            setFileSwitchType(LibraryFileType.LOCAL);
        } else {
            setFileSwitchType(LibraryFileType.SERVER_PUBLIC);
        }

        buttonList.add(fileSwitchlocal);
        buttonList.add(fileSwitchRemotePublic);
        buttonList.add(fileSwitchRemotePrivate);

        loadSaveButton = new GuiButtonExt(BUTTON_ID_LOAD_SAVE, PADDING * 2 + 18, this.height - INVENTORY_HEIGHT - PADDING * 2 - 2 - 20 - neiBump, 108, 20, "----LS--->");
        buttonList.add(loadSaveButton);

        filenameTextbox = new GuiLabeledTextField(fontRenderer, PADDING, TITLE_HEIGHT + 30 + PADDING * 2, INVENTORY_WIDTH, 12);
        filenameTextbox.setMaxStringLength(100);
        filenameTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.enterFileName"));

        searchTextbox = new GuiLabeledTextField(fontRenderer, INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 1 + PADDING, listWidth - typeSwitchWidth - PADDING + 10, 12);
        searchTextbox.setMaxStringLength(100);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.typeToSearch"));
        searchTextbox.setText(lastSearchText);

        scrollbar = new GuiScrollbar(2, INVENTORY_WIDTH + 10 + listWidth, TITLE_HEIGHT + 14 + PADDING * 2, 10, listHeight, "", false);
        scrollbar.setValue(scrollAmount);
        scrollbar.setAmount(fileList.getSlotHeight());
        buttonList.add(scrollbar);

        dropDownList = new GuiDropDownList(5, INVENTORY_WIDTH + PADDING * 5 + listWidth - typeSwitchWidth - PADDING, TITLE_HEIGHT + PADDING, typeSwitchWidth, "", null);
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        dropDownList.addListItem("*");
        dropDownList.setListSelectedIndex(0);
        int addCount = 0;
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            if (!skinType.isHidden()) {
                dropDownList.addListItem(SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType), skinType.getRegistryName(), true);
                addCount++;
                if (skinType == lastSkinType) {
                    dropDownList.setListSelectedIndex(addCount);
                }
            }
        }
        buttonList.add(dropDownList);

        checkBoxTrack = new GuiCheckBox(-1, PADDING, this.height - INVENTORY_HEIGHT - PADDING * 5 - 2 - 20 - neiBump, GuiHelper.getLocalizedControlName(guiName, "trackFile"), trackFile);
        checkBoxTrack.setTextColour(0xCCCCCC);
        buttonList.add(checkBoxTrack);
    }

    public TileEntitySkinLibrary getArmourLibrary() {
        return armourLibrary;
    }

    /**
     * Returns true if the player is trying to load and item or false if they are
     * trying to save.
     * 
     * @return true = loading, false = saving
     */
    private boolean isLoading() {
        Slot slot = inventorySlots.inventorySlots.get(36);
        ItemStack stack = slot.getStack();
        if (armourLibrary.isCreativeLibrary()) {
            if (stack == null) {
                return true;
            }
            if (stack.isEmpty()) {
                return true;
            }
            if (stack.getItem() instanceof ISkinHolder) {
                return true;
            }
            return false;
        } else {
            if (stack.getItem() instanceof ISkinHolder) {
                return true;
            }
            return false;
        }
    }

    private void setFileSwitchType(LibraryFileType type) {
        if (fileSwitchType == type) {
            return;
        }
        fileSwitchlocal.setPressed(false);
        fileSwitchRemotePublic.setPressed(false);
        fileSwitchRemotePrivate.setPressed(false);
        fileSwitchType = type;
        switch (type) {
        case LOCAL:
            fileSwitchlocal.setPressed(true);
            currentFolder = "/";
            setupLibraryEditButtons();
            break;
        case SERVER_PUBLIC:
            fileSwitchRemotePublic.setPressed(true);
            currentFolder = "/";
            setupLibraryEditButtons();
            break;
        case SERVER_PRIVATE:
            fileSwitchRemotePrivate.setPressed(true);
            currentFolder = getPrivateRoot(player);
            setupLibraryEditButtons();
            break;
        }
    }

    private void setupLibraryEditButtons() {
        IGuiListItem listItem = fileList.getSelectedListEntry();
        reloadButton.enabled = newFolderButton.enabled = openFolderButton.enabled = deleteButton.enabled = false;

        if (fileSwitchType == LibraryFileType.LOCAL) {
            reloadButton.enabled = openFolderButton.enabled = newFolderButton.enabled = true;
            reloadButton.enabled = true;
            if (listItem != null && !listItem.getDisplayName().equals("../")) {
                deleteButton.enabled = true;
            } else {
                deleteButton.setDisableText(GuiHelper.getLocalizedControlName(armourLibrary.getName(), "rollover.deleteSkinSelect"));
            }
        }

        if (fileSwitchType == LibraryFileType.SERVER_PUBLIC) {
            openFolderButton.setDisableText("");
            reloadButton.setDisableText("");
            deleteButton.setDisableText("");
            newFolderButton.setDisableText("");
        }

        if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
            openFolderButton.setDisableText("");
            reloadButton.setDisableText("");
            newFolderButton.enabled = true;
            if (listItem != null && !listItem.getDisplayName().equals("../")) {
                deleteButton.enabled = true;
            } else {
                deleteButton.setDisableText(GuiHelper.getLocalizedControlName(armourLibrary.getName(), "rollover.deleteSkinSelect"));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        String filename = filenameTextbox.getText().trim();

        if (button == fileSwitchlocal | button == fileSwitchRemotePublic | button == fileSwitchRemotePrivate) {
            if (button == fileSwitchlocal) {
                setFileSwitchType(LibraryFileType.LOCAL);
            }
            if (button == fileSwitchRemotePublic) {
                setFileSwitchType(LibraryFileType.SERVER_PUBLIC);
            }
            if (button == fileSwitchRemotePrivate) {
                setFileSwitchType(LibraryFileType.SERVER_PRIVATE);
            }
        }

        if (button == reloadButton) {
            if (fileSwitchType == LibraryFileType.LOCAL) {
                ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;
                libraryManager.reloadLibrary();
            } else {
                // TODO reload server library
            }
        }

        if (button == openFolderButton) {
            openEquipmentFolder();
        }

        if (button == deleteButton) {
            if (fileList.getSelectedListEntry() != null) {
                GuiFileListItem item = (GuiFileListItem) fileList.getSelectedListEntry();
                openDialog(new GuiDialogDelete(this, armourLibrary.getName() + ".dialog.delete", this, 190, 100, item.getFile().isDirectory(), item.getDisplayName()));
            }
        }

        if (button == newFolderButton) {
            openDialog(new GuiDialogNewFolder(this, armourLibrary.getName() + ".dialog.newFolder", this, 190, 120));
        }

        if (button == backButton) {
            goBackFolder();
        }

        if (button == checkBoxTrack) {
            trackFile = checkBoxTrack.isChecked();
        }

        GuiFileListItem fileItem = (GuiFileListItem) fileList.getSelectedListEntry();

        boolean clientLoad = false;
        boolean publicList = true;
        MessageClientGuiLoadSaveArmour message;

        if (fileSwitchType == LibraryFileType.LOCAL && !mc.isIntegratedServerRunning()) {
            // Is playing on a server.
            clientLoad = true;
        }

        if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
            publicList = false;
        }

        if (button == loadSaveButton) {
            if (fileItem != null && !fileItem.getFile().isDirectory()) {
                LibraryFile file = fileItem.getFile();
                if (isLoading()) {
                    if (clientLoad) {
                        Skin itemData = SkinIOUtils.loadSkinFromFileName(file.filePath + filename + SkinIOUtils.SKIN_FILE_EXTENSION);
                        if (itemData != null) {
                            SkinUploadHelper.uploadSkinToServer(itemData);
                        }
                    } else {
                        message = new MessageClientGuiLoadSaveArmour(file.fileName, file.filePath, LibraryPacketType.SERVER_LOAD, publicList, trackFile);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }
                    filenameTextbox.setText("");
                }
            }
            if (!filename.isEmpty()) {
                if (!isLoading()) {
                    if (fileExists(currentFolder, filename)) {
                        openDialog(new GuiDialogOverwrite(this, armourLibrary.getName() + ".dialog.overwrite", this, 190, 100, filename));
                        return;
                    }
                    if (clientLoad) {
                        message = new MessageClientGuiLoadSaveArmour(filename, currentFolder, LibraryPacketType.CLIENT_SAVE, false, trackFile);
                        PacketHandler.networkWrapper.sendToServer(message);
                    } else {
                        message = new MessageClientGuiLoadSaveArmour(filename, currentFolder, LibraryPacketType.SERVER_SAVE, publicList, trackFile);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }

                    // filenameTextbox.setText("");
                }
            }
        }

        setupLibraryEditButtons();
    }

    private boolean fileExists(String path, String name) {
        LibraryFileList fileList = getFileList(fileSwitchType);
        ArrayList<LibraryFile> files = fileList.getFileList();
        for (int i = 0; i < files.size(); i++) {
            LibraryFile file = files.get(i);
            if (file.getFullName().equalsIgnoreCase(path + name)) {
                return true;
            }
        }
        return false;
    }

    private LibraryFileList getFileList(LibraryFileType libraryFileType) {
        ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;
        switch (libraryFileType) {
        case LOCAL:
            return libraryManager.getClientPublicFileList();
        case SERVER_PUBLIC:
            return libraryManager.getServerPublicFileList();
        case SERVER_PRIVATE:
            return libraryManager.getServerPrivateFileList(mc.player);
        }
        return null;
    }

    private void reloadLocalLibrary() {
        ArmourersWorkshop.getProxy().libraryManager.reloadLibrary();
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiDialogNewFolder) {
                GuiDialogNewFolder newFolderDialog = (GuiDialogNewFolder) dialog;

                if (fileSwitchType == LibraryFileType.LOCAL) {
                    File dir = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), currentFolder);
                    dir = new File(dir, newFolderDialog.getFolderName());
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    reloadLocalLibrary();
                    ModLogger.log(String.format("making folder call %s in %s", newFolderDialog.getFolderName(), currentFolder));
                    ModLogger.log("full path: " + dir.getAbsolutePath());
                } else {
                    MessageClientGuiSkinLibraryCommand message = new MessageClientGuiSkinLibraryCommand();
                    message.newFolder(new LibraryFile(newFolderDialog.getFolderName(), currentFolder, null, true), fileSwitchType == LibraryFileType.SERVER_PUBLIC);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
            }

            if (dialog instanceof GuiDialogDelete) {
                GuiDialogDelete deleteDialog = (GuiDialogDelete) dialog;
                boolean isFolder = deleteDialog.isFolder();
                String name = deleteDialog.getFileName();

                if (fileSwitchType == LibraryFileType.LOCAL) {
                    File dir = new File(ArmourersWorkshop.getProxy().getSkinLibraryDirectory(), currentFolder);

                    if (deleteDialog.isFolder()) {
                        dir = new File(dir, name + "/");
                    } else {
                        dir = new File(dir, name + SkinIOUtils.SKIN_FILE_EXTENSION);
                    }

                    if (dir.isDirectory() == isFolder) {
                        if (dir.exists()) {
                            if (isFolder) {
                                try {
                                    FileUtils.deleteDirectory(dir);
                                    reloadLocalLibrary();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LibraryFile libraryFile = new LibraryFile(currentFolder + deleteDialog.getFileName());
                                ClientSkinCache.INSTANCE.markSkinAsDirty(new SkinIdentifier(0, libraryFile, 0, null));
                                dir.delete();
                                reloadLocalLibrary();
                            }
                        }
                    }
                } else {
                    MessageClientGuiSkinLibraryCommand message = new MessageClientGuiSkinLibraryCommand();
                    message.delete(new LibraryFile(deleteDialog.getFileName(), currentFolder, null, isFolder), fileSwitchType == LibraryFileType.SERVER_PUBLIC);
                    PacketHandler.networkWrapper.sendToServer(message);
                    // ClientSkinCache.INSTANCE.clearIdForFileName(currentFolder +
                    // deleteDialog.getName());
                }
            }

            if (dialog instanceof GuiDialogOverwrite) {
                GuiDialogOverwrite overwriteDialog = (GuiDialogOverwrite) dialog;
                MessageClientGuiLoadSaveArmour message;
                boolean clientLoad = false;
                boolean publicList = true;
                if (fileSwitchType == LibraryFileType.LOCAL && !mc.isIntegratedServerRunning()) {
                    // Is playing on a server.
                    clientLoad = true;
                }
                if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
                    publicList = false;
                }
                if (clientLoad) {
                    message = new MessageClientGuiLoadSaveArmour(overwriteDialog.getFileName(), currentFolder, LibraryPacketType.CLIENT_SAVE, false, trackFile);
                    PacketHandler.networkWrapper.sendToServer(message);
                } else {
                    message = new MessageClientGuiLoadSaveArmour(overwriteDialog.getFileName(), currentFolder, LibraryPacketType.SERVER_SAVE, publicList, trackFile);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
                LibraryFile libraryFile = new LibraryFile(currentFolder + overwriteDialog.getFileName());
                ClientSkinCache.INSTANCE.markSkinAsDirty(new SkinIdentifier(0, libraryFile, 0, null));
                // reloadLocalLibrary();
                // TODO clear name lookup list on clients or just removed this one name
            }
        }
        closeDialog();
    }

    public void setFileName(String text) {
        filenameTextbox.setText(text);
    }

    private void openEquipmentFolder() {
        File armourDir = new File(System.getProperty("user.dir"));
        File file = ArmourersWorkshop.getProxy().getSkinLibraryDirectory();
        String filePath = file.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[] { "/usr/bin/open", filePath });
                return;
            } catch (IOException ioexception1) {
                ModLogger.log(Level.ERROR, "Couldn\'t open file: " + ioexception1);
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] { filePath });
            try {
                Runtime.getRuntime().exec(s1);
                return;
            } catch (IOException ioexception) {
                ModLogger.log(Level.ERROR, "Couldn\'t open file: " + ioexception);
            }
        }

        boolean openedFailed = false;

        try {
            Class oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
            oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { file.toURI() });
        } catch (Throwable throwable) {
            ModLogger.log(Level.ERROR, "Couldn\'t open link: " + throwable);
            openedFailed = true;
        }

        if (openedFailed) {
            ModLogger.log("Opening via system class!");
            Sys.openURL("file://" + filePath);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        // GlStateManager.pushAttrib();
        GlStateManager.disableLighting();

        GlStateManager.resetColor();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        // GlStateManager.popAttrib();
        RenderHelper.disableStandardItemLighting();
        ILibraryManager libraryManager = ArmourersWorkshop.getProxy().libraryManager;
        ArrayList<LibraryFile> files = libraryManager.getServerPublicFileList().getFileList();

        loadSaveButton.enabled = true;

        // ModLogger.log(isLoading());

        if (isLoading()) {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getName(), "load");
            if (fileList.getSelectedListEntry() == null || ((GuiFileListItem) fileList.getSelectedListEntry()).getFile().directory) {
                loadSaveButton.displayString = "";
                loadSaveButton.enabled = false;
            }
        } else {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getName(), "save");
        }
        if (!inventorySlots.inventorySlots.get(36).getHasStack() & !armourLibrary.isCreativeLibrary()) {
            loadSaveButton.displayString = "";
            loadSaveButton.enabled = false;
        }

        if (fileSwitchType == LibraryFileType.LOCAL) {
            files = libraryManager.getClientPublicFileList().getFileList();
            if (!mc.isIntegratedServerRunning()) {
                loadSaveButton.enabled = false;
                if (isLoading()) {
                    loadSaveButton.enabled = ConfigHandler.allowUploadingSkins;
                } else {
                    loadSaveButton.enabled = ConfigHandler.allowDownloadingSkins;
                }
            }
        } else {
            loadSaveButton.enabled = true;
        }

        if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
            files = libraryManager.getServerPrivateFileList(mc.player).getFileList();
        }

        String typeFilter = dropDownList.getListSelectedItem().tag;
        ISkinType skinTypeFilter = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(typeFilter);
        lastSkinType = skinTypeFilter;
        lastSearchText = searchTextbox.getText();
        IGuiListItem selectedItem = fileList.getSelectedListEntry();

        if (selectedItem != null) {
            // deleteButton.enabled = !((GuiFileListItem)selectedItem).getFile().readOnly;
        } else {
            // deleteButton.enabled = false;
        }

        fileList.setSelectedIndex(-1);

        fileList.clearList();

        if (!(currentFolder.equals("/") | currentFolder.equals(getPrivateRoot(player)))) {
            fileList.addListItem(new GuiFileListItem(new LibraryFile("../", "", null, true)));
            if (selectedItem != null && ((GuiFileListItem) selectedItem).getFile().fileName.equals("../")) {
                fileList.setSelectedIndex(0);
            }
        }

        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                LibraryFile file = files.get(i);
                if (file.isDirectory() | (skinTypeFilter == SkinTypeRegistry.skinUnknown | skinTypeFilter == file.skinType)) {
                    if (file.filePath.equals(currentFolder)) {
                        if (!searchTextbox.getText().equals("")) {
                            if (file.fileName.toLowerCase().contains(searchTextbox.getText().toLowerCase())) {
                                fileList.addListItem(new GuiFileListItem(file));
                                if (selectedItem != null && ((GuiFileListItem) selectedItem).getFile() == file) {
                                    fileList.setSelectedIndex(fileList.getSize() - 1);
                                }
                            }
                        } else {
                            fileList.addListItem(new GuiFileListItem(file));
                            if (selectedItem != null && ((GuiFileListItem) selectedItem).getFile() == file) {
                                fileList.setSelectedIndex(fileList.getSize() - 1);
                            }
                        }
                    }
                }
            }
        }

        int scrollNeeded = fileList.getTotalListHeight();
        scrollNeeded -= fileList.getVisibleHeight();
        scrollNeeded = Math.max(0, scrollNeeded);
        scrollbar.setSliderMaxValue(scrollNeeded);

        scrollAmount = scrollbar.getValue();
        fileList.setScrollAmount(scrollbar.getValue());

        if (showModelPreviews()) {
            GuiFileListItem item = (GuiFileListItem) fileList.getSelectedListEntry();
            if (item != null && !item.getFile().isDirectory()) {
                SkinIdentifier identifier = new SkinIdentifier(0, new LibraryFile(item.getFile().getFullName()), 0, null);
                Skin skin = ClientSkinCache.INSTANCE.getSkin(identifier, true);
                if (skin != null) {
                    SkinDescriptor skinPointer = new SkinDescriptor(identifier);

                    int listRight = this.width - INVENTORY_WIDTH - PADDING * 5;
                    listRight = MathHelper.clamp(listRight, 0, 200);
                    listRight += INVENTORY_WIDTH + PADDING * 2 + 10;

                    int listTop = TITLE_HEIGHT + 14 + PADDING * 2;

                    int xSize = (this.width - listRight - PADDING) / 2;
                    int ySize = (this.height - listTop - PADDING) / 2;

                    float x = listRight + xSize;
                    float y = listTop + ySize;

                    float scale = 1F;
                    scale = 1 * Math.min(xSize, ySize);

                    ScaledResolution scaledResolution = new ScaledResolution(mc);

                    int startX = listRight + PADDING;
                    int startY = listTop + PADDING;

                    int tarW = (int) (x + xSize);
                    int tarH = (int) (y + ySize);

                    drawRect(startX, startY, tarW, tarH, 0x77777777);

                    if (scale > 8) {
                        GlStateManager.pushMatrix();
                        GL11.glTranslatef(x, y, 500.0F);
                        GL11.glScalef(10, 10, -10);
                        GL11.glRotatef(30, 1, 0, 0);
                        GL11.glRotatef(45, 0, 1, 0);
                        float rotation = (float) ((double) System.currentTimeMillis() / 10 % 360);
                        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);

                        GlStateManager.pushAttrib();
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GlStateManager.enableRescaleNormal();
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableNormalize();
                        ModRenderHelper.enableAlphaBlend();
                        SkinItemRenderHelper.renderSkinAsItem(skin, skinPointer, true, false, tarW - startX, tarH - startY);
                        ModRenderHelper.disableAlphaBlend();
                        GlStateManager.disableNormalize();
                        GlStateManager.disableColorMaterial();
                        GlStateManager.disableRescaleNormal();
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.resetColor();

        if (dropDownList.getIsDroppedDown()) {
            super.drawScreen(0, 0, partialTickTime);
        } else {
            super.drawScreen(mouseX, mouseY, partialTickTime);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.resetColor();
        GlStateManager.disableLighting();
        dropDownList.drawForeground(mc, mouseX, mouseY, partialTickTime);
        if (!isDialogOpen()) {
            for (int i = 0; i < buttonList.size(); i++) {
                GuiButton button = buttonList.get(i);
                if (button instanceof GuiIconButton) {
                    ((GuiIconButton) button).drawRollover(mc, mouseX, mouseY);
                }
            }
        }
    }

    private String getPrivateRoot(EntityPlayer player) {
        String privateRoot = "/private/";
        if (ConfigHandler.remotePlayerId != null) {
            privateRoot += ConfigHandler.remotePlayerId.toString() + "/";
        } else {
            privateRoot += player.getUniqueID().toString() + "/";
        }
        return privateRoot;
    }

    public static boolean showModelPreviews() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!ConfigHandler.libraryShowsModelPreviews) {
            return false;
        }
        if (mc.isIntegratedServerRunning()) {
            return true;
        } else {
            return fileSwitchType != LibraryFileType.LOCAL;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (!isDialogOpen()) {
            searchTextbox.mouseClicked(mouseX, mouseY, button);
            filenameTextbox.mouseClicked(mouseX, mouseY, button);

            if (button == 1) {
                if (searchTextbox.isFocused()) {
                    searchTextbox.setText("");
                }
                if (filenameTextbox.isFocused()) {
                    filenameTextbox.setText("");
                }
            }
            if (!dropDownList.getIsDroppedDown()) {

                GuiFileListItem oldItem = (GuiFileListItem) fileList.getSelectedListEntry();

                if (fileList.mouseClicked(mouseX, mouseY, button)) {
                    GuiFileListItem item = (GuiFileListItem) fileList.getSelectedListEntry();
                    if (!item.getFile().isDirectory()) {
                        filenameTextbox.setText(item.getDisplayName());
                    } else {
                        if (item.getFile().fileName.equals("../") & oldItem == item) {
                            goBackFolder();
                        } else if (oldItem == item) {
                            setCurrentFolder(item.getFile().getFullName() + "/");
                        }
                    }
                }
            }
            scrollbar.setValue(scrollAmount);
            scrollbar.mousePressed(mc, mouseX, mouseY);

            setupLibraryEditButtons();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    private void setCurrentFolder(String currentFolder) {
        this.currentFolder = currentFolder;
        fileList.setSelectedIndex(-1);
        scrollAmount = 0;
        scrollbar.setValue(scrollAmount);
    }

    private void goBackFolder() {
        String[] folderSplit = currentFolder.split("/");
        String currentFolder = "";
        for (int i = 0; i < folderSplit.length - 1; i++) {
            currentFolder += folderSplit[i] + "/";
        }
        if (currentFolder.equals("")) {
            currentFolder = "/";
        }
        setCurrentFolder(currentFolder);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (!isDialogOpen()) {
            if (!dropDownList.getIsDroppedDown()) {
                fileList.mouseMovedOrUp(mouseX, mouseY, state);
            }
            scrollbar.mouseReleased(mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
        if (keyCode == mc.gameSettings.keyBindScreenshot.getKeyCode()) {
            mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.gameDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
        }
        if (!isDialogOpen()) {
            if (!(searchTextbox.textboxKeyTyped(key, keyCode) | filenameTextbox.textboxKeyTyped(key, keyCode))) {
                if (keyCode == 200) {
                    // Up
                    fileList.setSelectedIndex(fileList.getSelectedIndex() - 1);
                }
                if (keyCode == 208) {
                    // Down
                    fileList.setSelectedIndex(fileList.getSelectedIndex() + 1);
                }
                super.keyTyped(key, keyCode);
            }
        } else {
            super.keyTyped(key, keyCode);
        }
        checkNEIVisibility();
    }

    private void checkNEIVisibility() {
        if (isNEIVisible != ModAddonManager.addonNEI.isVisible()) {
            isNEIVisible = !isNEIVisible;
            initGui();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float someFloat, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);

        ModRenderHelper.enableAlphaBlend();
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - PADDING - neiBump, 0, 180, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        // Input slot
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - 18 - PADDING * 2 - 4 - neiBump, 0, 162, 18, 18);
        // Output slot
        drawTexturedModalRect(PADDING + INVENTORY_WIDTH - 26, this.height - INVENTORY_HEIGHT - 26 - PADDING * 2 - neiBump, 18, 154, 26, 26);

        ModRenderHelper.disableAlphaBlend();

        searchTextbox.drawTextBox();
        filenameTextbox.drawTextBox();
        fileList.drawList(mouseX, mouseY, 0);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (armourLibrary.isCreativeLibrary()) {
            GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, armourLibrary.getName() + "1", 0xCCCCCC);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, armourLibrary.getName() + "0", 0xCCCCCC);
        }

        String filesLabel = GuiHelper.getLocalizedControlName(armourLibrary.getName(), "label.files");
        String filenameLabel = GuiHelper.getLocalizedControlName(armourLibrary.getName(), "label.filename");
        String searchLabel = GuiHelper.getLocalizedControlName(armourLibrary.getName(), "label.search");
        /*
         * this.fontRendererObj.drawString(filesLabel, 7, 55, 4210752);
         * this.fontRendererObj.drawString(filenameLabel, 152, 27, 4210752);
         * this.fontRendererObj.drawString(searchLabel, 7, 27, 4210752);
         * this.fontRendererObj.drawString(I18n.format("container.inventory", new
         * Object[0]), 48, this.ySize - 96 + 2, 4210752);
         */
    }

    @Override
    public String getName() {
        return armourLibrary.getName();
    }
}
