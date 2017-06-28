package riskyken.armourersWorkshop.client.gui.armourer;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerBlockUtils extends GuiTabPanel {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.ARMOURER);
    
    private final TileEntityArmourer tileEntity;
    
    public GuiTabArmourerBlockUtils(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer)parent).tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getInventoryName();
        buttonList.add(new GuiButtonExt(10, 10, 20, 70, 16, GuiHelper.getLocalizedControlName(guiName, "clear")));
        //buttonList.add(new GuiButtonExt(11, guiLeft + 177, guiTop + 46, 70, 16, GuiHelper.getLocalizedControlName(guiName, "westToEast")));
        //buttonList.add(new GuiButtonExt(12, guiLeft + 177, guiTop + 66, 70, 16, GuiHelper.getLocalizedControlName(guiName, "eastToWest")));
        //buttonList.add(new GuiButtonExt(13, guiLeft + 177, guiTop + 76, 70, 16, "Add Noise"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 10) {
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id)); 
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
    }
}
