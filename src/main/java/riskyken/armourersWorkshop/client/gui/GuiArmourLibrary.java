package riskyken.armourersWorkshop.client.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.gui.controls.GuiScrollbar;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.SkinUploadHelper;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour.LibraryPacketType;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class GuiArmourLibrary extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    private static final int BUTTON_ID_SAVE = 0;
    private static final int BUTTON_ID_LOAD = 1;
    
    private static final int TITLE_HEIGHT = 15;
    private static final int PADDING = 5;
    private static final int INVENTORY_HEIGHT = 76;
    private static final int INVENTORY_WIDTH = 162;
    
    private static int scrollAmount = 0;
    
    private TileEntityArmourLibrary armourLibrary;
    private GuiIconButton fileSwitchlocal;
    private GuiIconButton fileSwitchRemotePublic;
    private GuiIconButton fileSwitchRemotePrivate;
    private FileSwitchType fileSwitchType;
    private GuiList fileList;
    private GuiButtonExt loadSaveButton;
    private GuiButtonExt openFolderButton;
    private GuiScrollbar scrollbar;
    private GuiLabeledTextField filenameTextbox;
    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownList;
    
    public GuiArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
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
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            slot.yDisplayPosition = this.height + 1 - PADDING - slotSize;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                slot.yDisplayPosition = this.height + 1 - INVENTORY_HEIGHT - PADDING + y * slotSize;
            }
        }
        
        buttonList.clear();
        
        fileSwitchlocal = new GuiIconButton(-1, PADDING, TITLE_HEIGHT + PADDING, 50, 30, "L", texture);
        fileSwitchRemotePublic = new GuiIconButton(-1, PADDING + 51 + PADDING, TITLE_HEIGHT + PADDING, 50, 30, "R", texture);
        fileSwitchRemotePrivate = new GuiIconButton(-1, PADDING + 102 + PADDING * 2, TITLE_HEIGHT + PADDING, 50, 30, "S", texture);
        fileSwitchlocal.setIconLocation(0, 0, 50, 30);
        fileSwitchRemotePublic.setIconLocation(0, 31, 50, 30);
        fileSwitchRemotePrivate.setIconLocation(0, 62, 50, 30);
        buttonList.add(fileSwitchlocal);
        buttonList.add(fileSwitchRemotePublic);
        buttonList.add(fileSwitchRemotePrivate);
        fileSwitchType = FileSwitchType.REMOTE_PUBLIC;
        
        loadSaveButton = new GuiButtonExt(BUTTON_ID_SAVE, PADDING * 2 + 18, this.height - INVENTORY_HEIGHT - PADDING * 2 - 2 - 20, 108, 20, "----LS--->");
        buttonList.add(loadSaveButton);
        
        
        openFolderButton = new GuiButtonExt(4, PADDING, guiTop + 80, 20, 20, "O");
        buttonList.add(openFolderButton);
        
        buttonList.add(new GuiButtonExt(-1, PADDING * 2 + 20, guiTop + 80, 20, 20, "D"));
        
        int listWidth = this.width - INVENTORY_WIDTH - 10 - PADDING * 3;
        int listHeight = this.height - TITLE_HEIGHT - 14 - PADDING * 3;
        int typeSwitchWidth = 80;
        
        filenameTextbox = new GuiLabeledTextField(fontRendererObj, PADDING, TITLE_HEIGHT + 30 + PADDING * 2, INVENTORY_WIDTH, 12);
        filenameTextbox.setMaxStringLength(24);
        filenameTextbox.setEmptyLabel("Enter file name.");
        
        searchTextbox = new GuiLabeledTextField(fontRendererObj, INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 1 + PADDING, listWidth - typeSwitchWidth - PADDING + 10, 12);
        searchTextbox.setMaxStringLength(24);
        searchTextbox.setEmptyLabel("Type to search...");

        fileList = new GuiList(INVENTORY_WIDTH + PADDING * 2, TITLE_HEIGHT + 14 + PADDING * 2, listWidth, listHeight, 12);
        
        scrollbar = new GuiScrollbar(2, INVENTORY_WIDTH + 10 + listWidth, TITLE_HEIGHT + 14 + PADDING * 2, 10, listHeight, "", false);
        scrollbar.setValue(scrollAmount);
        buttonList.add(scrollbar);
        
        dropDownList = new GuiDropDownList(5, this.width - typeSwitchWidth - PADDING, TITLE_HEIGHT + PADDING, typeSwitchWidth, "", null);
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        dropDownList.addListItem("*");
        dropDownList.setListSelectedIndex(0);
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            if (!skinType.isHidden()) {
                dropDownList.addListItem(SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType),
                        skinType.getRegistryName(), true);
            }
        }
        buttonList.add(dropDownList);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        String filename = filenameTextbox.getText().trim();
        
        if (button == fileSwitchlocal | button == fileSwitchRemotePublic | button == fileSwitchRemotePrivate) {
            fileSwitchlocal.setPressed(false);
            fileSwitchRemotePublic.setPressed(false);
            fileSwitchRemotePrivate.setPressed(false);
            if (button == fileSwitchlocal) {
                fileSwitchlocal.setPressed(true);
            }
            if (button == fileSwitchRemotePublic) {
                fileSwitchRemotePublic.setPressed(true);
            }
            if (button == fileSwitchRemotePrivate) {
                fileSwitchRemotePrivate.setPressed(true);
            }
        }
        
        if (button.id == 4) {
            openEquipmentFolder();
        }
        
        if (!filename.equals("")) {
            MessageClientGuiLoadSaveArmour message;
            switch (button.id) {
            case BUTTON_ID_SAVE:
                if (fileSwitchType == FileSwitchType.LOCAL) {
                    message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.CLIENT_SAVE);
                    PacketHandler.networkWrapper.sendToServer(message);
                } else {
                    message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.SERVER_SAVE);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
                
                filenameTextbox.setText("");
                break;
            case BUTTON_ID_LOAD:
                if (fileSwitchType == FileSwitchType.LOCAL) {
                    Skin itemData = SkinIOUtils.loadSkinFromFileName(filename + ".armour");
                    if (itemData != null) {
                        SkinUploadHelper.uploadSkinToServer(itemData);
                    }
                } else {
                    message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.SERVER_LOAD);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
                filenameTextbox.setText("");
                break;
            }
        }
        
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
    public void drawScreen(int mouseX, int mouseY, float tickTime) {
        super.drawScreen(mouseX, mouseY, tickTime);
        
        ArrayList<String> fileNames = armourLibrary.serverFileNames;
        if (fileSwitchType == FileSwitchType.LOCAL) {
            fileNames = armourLibrary.clientFileNames;
        }
        
        
        String typeFilter = dropDownList.getListSelectedItem().tag;
        if (fileNames!= null) {
            fileList.clearList();
            for (int i = 0; i < fileNames.size(); i++) {
                String fileName = fileNames.get(i);
                String[] splitName = fileName.split("\n");
                //ModLogger.log(splitName.length);
                
                if (typeFilter.isEmpty() | splitName[1].equals(typeFilter)) {
                    if (!searchTextbox.getText().equals("")) {
                        if (splitName[0].toLowerCase().contains(searchTextbox.getText().toLowerCase())) {
                            fileList.addListItem(new GuiFileListItem(splitName[0], splitName[1], true));
                        }
                    } else {
                        fileList.addListItem(new GuiFileListItem(splitName[0], splitName[1], true));
                    }
                }
            }
        }
        
        scrollAmount = scrollbar.getValue();
        fileList.setScrollPercentage(scrollbar.getPercentageValue());
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        
        
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
            if (fileList.mouseClicked(mouseX, mouseY, button)) {
                filenameTextbox.setText(fileList.getSelectedListEntry().getDisplayName());
            }
            
        }
        scrollbar.mousePressed(mc, mouseX, mouseY);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (!dropDownList.getIsDroppedDown()) {
            fileList.mouseMovedOrUp(mouseX, mouseY, button);
        }
        scrollbar.mouseReleased(mouseX, mouseY);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (!(searchTextbox.textboxKeyTyped(key, keyCode) | filenameTextbox.textboxKeyTyped(key, keyCode))) {
            super.keyTyped(key, keyCode);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float someFloat,int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        
        ModRenderHelper.enableAlphaBlend();
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - PADDING, 0, 180, INVENTORY_WIDTH, INVENTORY_HEIGHT);
        drawTexturedModalRect(PADDING, this.height - INVENTORY_HEIGHT - 18 - PADDING * 2 - 4, 0, 162, 18, 18);
        if (armourLibrary.isCreativeLibrary()) {
            
        }
        drawTexturedModalRect(PADDING + INVENTORY_WIDTH - 26, this.height - INVENTORY_HEIGHT - 26 - PADDING * 2, 18, 154, 26, 26);
        
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
    
    private enum FileSwitchType {
        LOCAL,
        REMOTE_PUBLIC,
        REMOTE_PRIVATE;
    }
}
