package riskyken.armourersWorkshop.client.gui.skinlibrary;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog.DialogResult;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialogContainer;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.gui.controls.GuiScrollbar;
import riskyken.armourersWorkshop.client.gui.controls.IGuiListItem;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.items.ItemSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileList;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.SkinUploadHelper;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour.LibraryPacketType;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class GuiSkinLibrary extends AbstractGuiDialogContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    private static final int BUTTON_ID_LOAD_SAVE = 0;
    
    private static final int TITLE_HEIGHT = 15;
    private static final int PADDING = 5;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;
    
    private static int scrollAmount = 0;
    private static ISkinType lastSkinType;
    private static String lastSearchText = "";
    private static String currentFolder = "/";
    
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
    
    private GuiScrollbar scrollbar;
    private GuiLabeledTextField filenameTextbox;
    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownList;
    private int neiBump = 18;
    
    public GuiSkinLibrary(InventoryPlayer invPlayer, TileEntitySkinLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
        player = invPlayer.player;
        this.armourLibrary = armourLibrary;
    }
    
    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();
        
        String guiName = armourLibrary.getInventoryName();
        
        int slotSize = 18;
        
        if (!Loader.isModLoaded("NotEnoughItems")) {
            neiBump = 0;
        }
        
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            slot.yDisplayPosition = this.height + 1 - PADDING - slotSize - neiBump;
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.yDisplayPosition = this.height + 1 - INVENTORY_HEIGHT - PADDING + y * slotSize - neiBump;
            }
        }
        
        //Move library inventory slots.
        Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
        slot.yDisplayPosition = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
        slot.xDisplayPosition = PADDING + 1;
        slot = (Slot) inventorySlots.inventorySlots.get(37);
        slot.yDisplayPosition = this.height + 2 - INVENTORY_HEIGHT - PADDING * 3 - slotSize - neiBump;
        slot.xDisplayPosition = PADDING + INVENTORY_WIDTH - slotSize - 3;
        
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
        reloadButton.setIconLocation(75, 93, 24, 24);
        buttonList.add(reloadButton);
        
        deleteButton = new GuiIconButton(this, -1, PADDING * 3 + 40, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.deleteSkin"), texture);
        deleteButton.setIconLocation(0, 118, 24, 24);
        buttonList.add(deleteButton);
        
        newFolderButton = new GuiIconButton(this, -1, PADDING * 4 + 60, guiTop + 80, 24, 24, GuiHelper.getLocalizedControlName(guiName, "rollover.newFolder"), texture);
        newFolderButton.setIconLocation(75, 118, 24, 24);
        buttonList.add(newFolderButton);
        
        int listWidth = this.width - INVENTORY_WIDTH - PADDING * 5;
        int listHeight = this.height - TITLE_HEIGHT - 14 - PADDING * 3;
        int typeSwitchWidth = 80;
        
        listWidth = MathHelper.clamp_int(listWidth, 0, 200);
        
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
        
        filenameTextbox = new GuiLabeledTextField(fontRendererObj, PADDING, TITLE_HEIGHT + 30 + PADDING * 2, INVENTORY_WIDTH, 12);
        filenameTextbox.setMaxStringLength(100);
        filenameTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.enterFileName"));
        
        searchTextbox = new GuiLabeledTextField(fontRendererObj, INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 1 + PADDING, listWidth - typeSwitchWidth - PADDING + 10, 12);
        searchTextbox.setMaxStringLength(100);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "label.typeToSearch"));
        searchTextbox.setText(lastSearchText);
        
        scrollbar = new GuiScrollbar(2, INVENTORY_WIDTH + 10 + listWidth, TITLE_HEIGHT + 14 + PADDING * 2, 10, listHeight, "", false);
        scrollbar.setValue(scrollAmount);
        buttonList.add(scrollbar);
        
        dropDownList = new GuiDropDownList(5, INVENTORY_WIDTH + PADDING * 5 + listWidth - typeSwitchWidth - PADDING, TITLE_HEIGHT + PADDING, typeSwitchWidth, "", null);
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        dropDownList.addListItem("*");
        dropDownList.setListSelectedIndex(0);
        int addCount = 0;
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            if (!skinType.isHidden()) {
                dropDownList.addListItem(SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType),
                        skinType.getRegistryName(), true);
                addCount++;
                if (skinType == lastSkinType) {
                    dropDownList.setListSelectedIndex(addCount);
                }
            }
        }
        buttonList.add(dropDownList);
    }
    
    public TileEntitySkinLibrary getArmourLibrary() {
        return armourLibrary;
    }
    
    /**
     * Returns true if the player is trying to load and item
     * or false if they are trying to save.
     * @return true = loading, false = saving
     */
    private boolean isLoading() {
        Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
        ItemStack stack = slot.getStack();
        if (stack != null && !(stack.getItem() instanceof ItemSkinTemplate)) {
            return false;
        }
        return true;
    }
    
    private boolean isPlayerOp(EntityPlayer player) {
        MinecraftServer minecraftServer = FMLCommonHandler.instance().getMinecraftServerInstance();
        return minecraftServer.getConfigurationManager().func_152596_g(player.getGameProfile());
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
                deleteButton.setDisableText(GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "rollover.deleteSkinSelect"));
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
                deleteButton.setDisableText(GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "rollover.deleteSkinSelect"));
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
                ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
                libraryManager.reloadLibrary();
            } else {
                //TODO reload server library
            }
        }
        
        if (button == openFolderButton) {
            openEquipmentFolder();
        }
        
        if (button == deleteButton) {
            if (fileList.getSelectedListEntry() != null) {
                GuiFileListItem item = (GuiFileListItem) fileList.getSelectedListEntry();
                openDialog(new GuiDialogDelete(this, armourLibrary.getInventoryName() + ".dialog.delete", this, 190, 100, item.getFile().isDirectory(), item.getDisplayName()));
            }
        }
        
        if (button == newFolderButton) {
            openDialog(new GuiDialogNewFolder(this, armourLibrary.getInventoryName() + ".dialog.newFolder", this, 190, 120));
        }
        
        GuiFileListItem fileItem = (GuiFileListItem) fileList.getSelectedListEntry();
        
        boolean clientLoad = false;
        boolean publicList = true;
        MessageClientGuiLoadSaveArmour message;
        
        if (fileSwitchType == LibraryFileType.LOCAL && !mc.isIntegratedServerRunning()) {
            //Is playing on a server.
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
                        message = new MessageClientGuiLoadSaveArmour(file.fileName, file.filePath, LibraryPacketType.SERVER_LOAD, publicList);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }
                    filenameTextbox.setText("");
                } 
            }
            if (!filename.isEmpty()) {
                if (!isLoading()) {
                    if (fileExists(currentFolder, filename)) {
                        openDialog(new GuiDialogOverwrite(this, armourLibrary.getInventoryName() + ".dialog.overwrite", this, 190, 100, filename));
                        return;
                    }
                    if (clientLoad) {
                        message = new MessageClientGuiLoadSaveArmour(filename, currentFolder, LibraryPacketType.CLIENT_SAVE, false);
                        PacketHandler.networkWrapper.sendToServer(message);
                    } else {
                        message = new MessageClientGuiLoadSaveArmour(filename, currentFolder, LibraryPacketType.SERVER_SAVE, publicList);
                        PacketHandler.networkWrapper.sendToServer(message);
                    }
                    
                    //filenameTextbox.setText("");
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
        ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
        switch (libraryFileType) {
        case LOCAL:
            return libraryManager.getClientPublicFileList();
        case SERVER_PUBLIC:
            return libraryManager.getServerPublicFileList();
        case SERVER_PRIVATE:
            return libraryManager.getServerPrivateFileList(mc.thePlayer);
        }
        return null;
    }
    
    private void reloadLocalLibrary() {
        ArmourersWorkshop.proxy.libraryManager.reloadLibrary();
    }
    
    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiDialogNewFolder) {
                GuiDialogNewFolder newFolderDialog = (GuiDialogNewFolder) dialog;
                
                if (fileSwitchType == LibraryFileType.LOCAL) {
                    File dir = new File(SkinIOUtils.getSkinLibraryDirectory(), currentFolder);
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
                    File dir = new File(SkinIOUtils.getSkinLibraryDirectory(), currentFolder);
                    
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
                                ClientSkinCache.INSTANCE.clearIdForFileName(currentFolder + deleteDialog.getFileName());
                                dir.delete();
                                reloadLocalLibrary();
                            }
                        }
                    }
                } else {
                    MessageClientGuiSkinLibraryCommand message = new MessageClientGuiSkinLibraryCommand();
                    message.delete(new LibraryFile(deleteDialog.getFileName(), currentFolder, null, isFolder), fileSwitchType == LibraryFileType.SERVER_PUBLIC);
                    PacketHandler.networkWrapper.sendToServer(message);
                    //ClientSkinCache.INSTANCE.clearIdForFileName(currentFolder + deleteDialog.getName());
                }
            }
            
            if (dialog instanceof GuiDialogOverwrite) {
                GuiDialogOverwrite overwriteDialog = (GuiDialogOverwrite) dialog;
                MessageClientGuiLoadSaveArmour message;
                boolean clientLoad = false;
                boolean publicList = true;
                if (fileSwitchType == LibraryFileType.LOCAL && !mc.isIntegratedServerRunning()) {
                    //Is playing on a server.
                    clientLoad = true;
                }
                if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
                    publicList = false;
                }
                if (clientLoad) {
                    message = new MessageClientGuiLoadSaveArmour(overwriteDialog.getFileName(), currentFolder, LibraryPacketType.CLIENT_SAVE, false);
                    PacketHandler.networkWrapper.sendToServer(message);
                } else {
                    message = new MessageClientGuiLoadSaveArmour(overwriteDialog.getFileName(), currentFolder, LibraryPacketType.SERVER_SAVE, publicList);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
                ClientSkinCache.INSTANCE.clearIdForFileName(currentFolder + overwriteDialog.getFileName());
                // reloadLocalLibrary();
                // TODO clear name lookup list on clients or just removed this one name
            }
        }
        super.dialogResult(dialog, result);
    }
    
    public void setFileName(String text) {
        filenameTextbox.setText(text);
    }
    
    private void openEquipmentFolder() {
        File armourDir = new File(System.getProperty("user.dir"));
        File file = new File(armourDir, LibModInfo.ID);
        String filePath = file.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[] {"/usr/bin/open", filePath});
                return;
            } catch (IOException ioexception1) {
                ModLogger.log(Level.ERROR, "Couldn\'t open file: " + ioexception1);
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[] {filePath});
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
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
            oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {file.toURI()});
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
        int oldMouseX = mouseX;
        int oldMouseY = mouseY;
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        GL11.glPopAttrib();
        ILibraryManager libraryManager = ArmourersWorkshop.proxy.libraryManager;
        ArrayList<LibraryFile> files = libraryManager.getServerPublicFileList().getFileList();
        
        loadSaveButton.enabled = true;
        
        if (isLoading()) {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "load");
            if (fileList.getSelectedListEntry() == null || ((GuiFileListItem)fileList.getSelectedListEntry()).getFile().directory) {
                loadSaveButton.displayString = "";
                loadSaveButton.enabled = false;
            }
        } else {
            loadSaveButton.displayString = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "save");
        }
        if (!((Slot) inventorySlots.inventorySlots.get(36)).getHasStack() & !armourLibrary.isCreativeLibrary()) {
            loadSaveButton.displayString = "";
            loadSaveButton.enabled = false;
        }
        
        if (fileSwitchType == LibraryFileType.LOCAL) {
            files = libraryManager.getClientPublicFileList().getFileList();
            if (!mc.isIntegratedServerRunning()) {
                loadSaveButton.enabled = false;
                if (isLoading()) {
                    loadSaveButton.enabled = ConfigHandler.allowClientsToUploadSkins;
                } else {
                    loadSaveButton.enabled = ConfigHandler.allowClientsToDownloadSkins;
                }
            }
        } else {
            loadSaveButton.enabled = true;
        }
        
        if (fileSwitchType == LibraryFileType.SERVER_PRIVATE) {
            files = libraryManager.getServerPrivateFileList(mc.thePlayer).getFileList();
        }
        
        String typeFilter = dropDownList.getListSelectedItem().tag;
        ISkinType skinTypeFilter = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(typeFilter);
        lastSkinType = skinTypeFilter;
        lastSearchText = searchTextbox.getText();
        IGuiListItem selectedItem =  fileList.getSelectedListEntry();
        
        if (selectedItem != null) {
            //deleteButton.enabled = !((GuiFileListItem)selectedItem).getFile().readOnly;
        } else {
            //deleteButton.enabled = false;
        }
        
        fileList.setSelectedIndex(-1);
        
        fileList.clearList();
        
        if (!(currentFolder.equals("/") | currentFolder.equals(getPrivateRoot(player)))) {
            fileList.addListItem(new GuiFileListItem(new LibraryFile("../", "", null, true)));
            if (selectedItem != null && ((GuiFileListItem)selectedItem).getFile().fileName.equals("../")) {
                fileList.setSelectedIndex(0);
            }
        }
        
        if (files!= null) {
            for (int i = 0; i < files.size(); i++) {
                LibraryFile file = files.get(i);
                if (skinTypeFilter == null | skinTypeFilter == file.skinType) {
                    if (file.filePath.equals(currentFolder)) {
                        if (!searchTextbox.getText().equals("")) {
                            if (file.fileName.toLowerCase().contains(searchTextbox.getText().toLowerCase())) {
                                fileList.addListItem(new GuiFileListItem(file));
                                if (selectedItem != null && ((GuiFileListItem)selectedItem).getFile() == file) {
                                    fileList.setSelectedIndex(fileList.getSize() - 1);
                                }
                            }
                        } else {
                            fileList.addListItem(new GuiFileListItem(file));
                            if (selectedItem != null && ((GuiFileListItem)selectedItem).getFile() == file) {
                                fileList.setSelectedIndex(fileList.getSize() - 1);
                            }
                        }
                    }
                }
            }
        }
        
        scrollAmount = scrollbar.getValue();
        fileList.setScrollPercentage(scrollbar.getPercentageValue());
        
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton)button).drawRollover(mc, mouseX, mouseY);
            }
        }
        
        if (showModelPreviews()) {
            GuiFileListItem item = (GuiFileListItem) fileList.getSelectedListEntry();
            if (item != null && !item.getFile().isDirectory()) {
                Skin skin = ClientSkinCache.INSTANCE.getSkin(item.getFile().getFullName(), true);
                if (skin != null) {
                    SkinPointer skinPointer = new SkinPointer(skin.getSkinType(), skin.lightHash());
                    
                    int listRight = this.width - INVENTORY_WIDTH - PADDING * 5;
                    listRight = MathHelper.clamp_int(listRight, 0, 200);
                    listRight += INVENTORY_WIDTH + PADDING * 2 + 10;
                    
                    int listTop = TITLE_HEIGHT + 14 + PADDING * 2;
                    
                    int xSize = (this.width - listRight - PADDING) / 2;
                    int ySize = (this.height - listTop - PADDING) / 2;
                    
                    float x = listRight + xSize;
                    float y = listTop + ySize;
                    
                    float scale = 1F;
                    scale = 1 * Math.min(xSize, ySize);
                    
                    if (scale > 8) {
                        GL11.glPushMatrix();
                        GL11.glTranslatef((float)x, (float)y, 500.0F);
                        GL11.glScalef((float)(-scale), (float)scale, (float)scale);
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                        float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
                        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                        RenderHelper.enableStandardItemLighting();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                        GL11.glEnable(GL11.GL_NORMALIZE);
                        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                        ModRenderHelper.enableAlphaBlend();
                        ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, false);
                        GL11.glPopAttrib();
                        GL11.glPopMatrix();
                    }
                }
            }
        }
        GL11.glColor4f(1, 1, 1, 1);
        if (isDialogOpen()) {
            this.dialog.draw(oldMouseX, oldMouseY, partialTickTime);
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
    protected void mouseClicked(int mouseX, int mouseY, int button) {
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
    }
    
    private void goBackFolder() {
        String[] folderSplit = currentFolder.split("/");
        String currentFolder = "";
        for (int i = 0; i < folderSplit.length - 1; i++) {
            currentFolder += folderSplit[i] + "/";
        }
        setCurrentFolder(currentFolder);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (!isDialogOpen()) {
            if (!dropDownList.getIsDroppedDown()) {
                fileList.mouseMovedOrUp(mouseX, mouseY, button);
            }
            scrollbar.mouseReleased(mouseX, mouseY);
        }
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (keyCode == mc.gameSettings.keyBindScreenshot.getKeyCode()) {
            mc.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer()));
        }
        if (!isDialogOpen()) {
            if (!(searchTextbox.textboxKeyTyped(key, keyCode) | filenameTextbox.textboxKeyTyped(key, keyCode))) {
                if (keyCode == 200) {
                    //Up
                    fileList.setSelectedIndex(fileList.getSelectedIndex() - 1);
                }
                if (keyCode == 208) {
                    //Down
                    fileList.setSelectedIndex(fileList.getSelectedIndex() + 1);
                }
                super.keyTyped(key, keyCode);
            }
        } else {
            super.keyTyped(key, keyCode);
        }
    }
    

    @Override
    protected void drawGuiContainerBackgroundLayer(float someFloat,int mouseX, int mouseY) {
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        
        ModRenderHelper.enableAlphaBlend();
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - PADDING - neiBump, 0, 180, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        //Input slot
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - 18 - PADDING * 2 - 4 - neiBump, 0, 162, 18, 18);
        //Output slot
        drawTexturedModalRect(PADDING + INVENTORY_WIDTH - 26, this.height - INVENTORY_HEIGHT - 26 - PADDING * 2 - neiBump, 18, 154, 26, 26);
        
        ModRenderHelper.disableAlphaBlend();
        
        searchTextbox.drawTextBox();
        filenameTextbox.drawTextBox();
        fileList.drawList(mouseX, mouseY, 0);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (armourLibrary.isCreativeLibrary()) {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "1", 0xCCCCCC);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "0", 0xCCCCCC);
        }
        
        String filesLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.files");
        String filenameLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.filename");
        String searchLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.search");
        /*
        this.fontRendererObj.drawString(filesLabel, 7, 55, 4210752);
        this.fontRendererObj.drawString(filenameLabel, 152, 27, 4210752);
        this.fontRendererObj.drawString(searchLabel, 7, 27, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 48, this.ySize - 96 + 2, 4210752);
        */
    }
}
