package riskyken.armourersWorkshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.wardrobe.GuiWardrobe;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinWardrobe;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeSkins extends GuiTabPanel {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    
    public GuiTabWardrobeSkins(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        
        //Top half of GUI. (active tab)
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);

        
        int sloImageSize = 18;
        GuiContainer guiContainer = (GuiContainer) parent;
        ContainerSkinWardrobe skinWardrobe = (ContainerSkinWardrobe) guiContainer.inventorySlots;
        
        for (int i = 0; i <  skinWardrobe.getSkinSlots(); i++) {
            Slot slot = (Slot) skinWardrobe.inventorySlots.get(i);
            this.drawTexturedModalRect(this.x + slot.xDisplayPosition - 1,
                    this.y + slot.yDisplayPosition - 1,
                    238, 194, sloImageSize, sloImageSize);
        }
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        // Draw player preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
