package riskyken.armourersWorkshop.client.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import org.apache.logging.log4j.Level;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.gui.controls.GuiScrollbar;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
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
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourLibrary extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    private static final int BUTTON_ID_SAVE = 0;
    private static final int BUTTON_ID_LOAD = 1;
    
    private static int scrollAmount = 0;
    
    private TileEntityArmourLibrary armourLibrary;
    private GuiList fileList;
    private GuiButtonExt saveButton;
    private GuiButtonExt loadButton;
    private GuiButtonExt openFolderButton;
    private GuiScrollbar scrollbar;
    private GuiTextField filenameTextbox;
    private GuiTextField searchTextbox;
    private GuiCheckBox checkClientFiles;
    private GuiDropDownList dropDownList;
    
    public GuiArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
        this.armourLibrary = armourLibrary;
        this.xSize = 256;
        this.ySize = 256;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = armourLibrary.getInventoryName();
        buttonList.clear();
        saveButton = new GuiButtonExt(BUTTON_ID_SAVE, guiLeft + 152, guiTop + 100, 60, 16, GuiHelper.getLocalizedControlName(guiName, "save"));
        buttonList.add(saveButton);
        
        loadButton = new GuiButtonExt(BUTTON_ID_LOAD, guiLeft + 152, guiTop + 120, 60, 16, GuiHelper.getLocalizedControlName(guiName, "load"));
        buttonList.add(loadButton);
        
        openFolderButton = new GuiButtonExt(4, guiLeft + 152, guiTop + 80, 96, 16, GuiHelper.getLocalizedControlName(guiName, "openFolder"));
        buttonList.add(openFolderButton);
        
        filenameTextbox = new GuiTextField(fontRendererObj, guiLeft + 152, guiTop + 36, 96, 14);
        filenameTextbox.setMaxStringLength(24);
        
        searchTextbox = new GuiTextField(fontRendererObj, guiLeft + 7, guiTop + 36, 131, 14);
        searchTextbox.setMaxStringLength(24);
        
        fileList = new GuiList(this.guiLeft + 7, this.guiTop + 80, 131, 80, 12);
        
        scrollbar = new GuiScrollbar(2, this.guiLeft + 138, this.guiTop + 80, 10, 80, "", false);
        scrollbar.setValue(scrollAmount);
        buttonList.add(scrollbar);
        
        checkClientFiles = new GuiCheckBox(3, this.guiLeft + 152, this.guiTop + 63, GuiHelper.getLocalizedControlName(guiName, "showClientFiles"), false);
        buttonList.add(checkClientFiles);
        
        dropDownList = new GuiDropDownList(5, this.guiLeft + 7, this.guiTop + 63, 141, "", null);
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
        
        if (button.id == 4) {
            openEquipmentFolder();
        }
        
        if (!filename.equals("")) {
            MessageClientGuiLoadSaveArmour message;
            switch (button.id) {
            case BUTTON_ID_SAVE:
                if (checkClientFiles.isChecked()) {
                    message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.CLIENT_SAVE);
                    PacketHandler.networkWrapper.sendToServer(message);
                } else {
                    message = new MessageClientGuiLoadSaveArmour(filename, LibraryPacketType.SERVER_SAVE);
                    PacketHandler.networkWrapper.sendToServer(message);
                }
                
                filenameTextbox.setText("");
                break;
            case BUTTON_ID_LOAD:
                if (checkClientFiles.isChecked()) {
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
        if (checkClientFiles.isChecked()) {
            fileNames = armourLibrary.clientFileNames;
        }
        
        if (checkClientFiles.isChecked()) {
            saveButton.enabled = ConfigHandler.allowClientsToDownloadSkins;
            loadButton.enabled = ConfigHandler.allowClientsToUploadSkins;
        } else {
            saveButton.enabled = true;
            loadButton.enabled = true;
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
                            fileList.addListItem(new GuiFileListItem(splitName[0], splitName[1]));
                        }
                    } else {
                        fileList.addListItem(new GuiFileListItem(splitName[0], splitName[1]));
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
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (armourLibrary.isCreativeLibrary()) {
            drawTexturedModalRect(this.guiLeft + 225, this.guiTop + 100, 10, 10, 18, 18);
        }
        searchTextbox.drawTextBox();
        filenameTextbox.drawTextBox();
        fileList.drawList(mouseX, mouseY, 0);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (armourLibrary.isCreativeLibrary()) {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "1");
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName() + "0");
        }
        
        String filesLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.files");
        String filenameLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.filename");
        String searchLabel = GuiHelper.getLocalizedControlName(armourLibrary.getInventoryName(), "label.search");
        
        this.fontRendererObj.drawString(filesLabel, 7, 55, 4210752);
        this.fontRendererObj.drawString(filenameLabel, 152, 27, 4210752);
        this.fontRendererObj.drawString(searchLabel, 7, 27, 4210752);
        
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 48, this.ySize - 96 + 2, 4210752);
    }
}
