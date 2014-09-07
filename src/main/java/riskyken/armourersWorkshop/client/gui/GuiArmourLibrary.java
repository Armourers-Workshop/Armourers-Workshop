package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.gui.controls.GuiScrollbar;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourLibrary extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/armourLibrary.png");
    
    private TileEntityArmourLibrary armourLibrary;
    private GuiList fileList;
    private GuiScrollbar scrollbar;
    private GuiTextField textFileName;
    
    public GuiArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary armourLibrary) {
        super(new ContainerArmourLibrary(invPlayer, armourLibrary));
        this.armourLibrary = armourLibrary;
        this.xSize = 176;
        this.ySize = 256;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButtonExt(0, guiLeft + 86, guiTop + 16, 50, 12, "Save"));
        buttonList.add(new GuiButtonExt(1, guiLeft + 86, guiTop + 16 + 13, 50, 12, "Load"));
        
        textFileName = new GuiTextField(fontRendererObj, guiLeft + 7, guiTop + 46, 161, 14);
        textFileName.setMaxStringLength(24);
        fileList = new GuiList(this.guiLeft + 7, this.guiTop + 63, 151, 96, 12);
        
        scrollbar = new GuiScrollbar(2, this.guiLeft + 158, this.guiTop + 63, 10, 96, "", false);
        buttonList.add(scrollbar);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        String filename = textFileName.getText().trim();
        
        if (!filename.equals("")) {
            switch (button.id) {
            case 0:
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiLoadSaveArmour(filename, false));
                textFileName.setText("");
                break;
            case 1:
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiLoadSaveArmour(filename, true));
                textFileName.setText("");
                break;
            }
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float tickTime) {
        super.drawScreen(mouseX, mouseY, tickTime);
        
        if (armourLibrary.fileNames != null) {
            fileList.clearList();
            for (int i = 0; i < armourLibrary.fileNames.size(); i++) {
                fileList.addListItem(new GuiFileListItem(armourLibrary.fileNames.get(i)));
            }
        }
        
        fileList.setScrollPercentage(scrollbar.getValue());
        fileList.drawList(mouseX, mouseY, tickTime);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        textFileName.mouseClicked(mouseX, mouseY, button);
        if (fileList.mouseClicked(mouseX, mouseY, button)) {
            textFileName.setText(fileList.getSelectedListEntry().getDisplayName());
        }
        scrollbar.mousePressed(mc, mouseX, mouseY);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        fileList.mouseMovedOrUp(mouseX, mouseY, button);
        scrollbar.mouseReleased(mouseX, mouseY);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (!textFileName.textboxKeyTyped(key, keyCode)) {
            super.keyTyped(key, keyCode);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_,int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        textFileName.drawTextBox();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, armourLibrary.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
}
