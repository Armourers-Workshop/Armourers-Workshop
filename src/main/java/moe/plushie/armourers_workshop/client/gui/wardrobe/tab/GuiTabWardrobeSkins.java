package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinWardrobe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeSkins extends GuiTabPanel {

    //private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    
    public GuiTabWardrobeSkins(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        
        //Top half of GUI. (active tab)
        //this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        //this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);

        
        int sloImageSize = 18;
        GuiContainer guiContainer = (GuiContainer) parent;
        ContainerSkinWardrobe skinWardrobe = (ContainerSkinWardrobe) guiContainer.inventorySlots;
        
        for (int i = skinWardrobe.getIndexSkinsStart(); i <  skinWardrobe.getIndexSkinsEnd(); i++) {
            Slot slot = skinWardrobe.inventorySlots.get(i);
            this.drawTexturedModalRect(this.x + slot.xPos - 1,
                    this.y + slot.yPos - 1,
                    238, 194, sloImageSize, sloImageSize);
        }
        for (int i = skinWardrobe.getIndexMannequinHandsStart(); i <  skinWardrobe.getIndexMannequinHandsEnd(); i++) {
            Slot slot = skinWardrobe.inventorySlots.get(i);
            this.drawTexturedModalRect(this.x + slot.xPos - 1,
                    this.y + slot.yPos - 1,
                    238, 194, sloImageSize, sloImageSize);
        }
        
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        // Draw player preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
