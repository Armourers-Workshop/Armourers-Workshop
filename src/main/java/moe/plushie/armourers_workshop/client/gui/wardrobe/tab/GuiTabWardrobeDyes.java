package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.style.GuiResourceManager;
import moe.plushie.armourers_workshop.client.gui.style.GuiStyle;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinWardrobe;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeDyes extends GuiTabPanel {

    private static final ResourceLocation GUI_JSON = new ResourceLocation(LibGuiResources.JSON_WARDROBE);
    
    private final GuiStyle guiStyle;
    private EntityPlayer entityPlayer;
    private IEntitySkinCapability skinCapability;
    private IWardrobeCap wardrobeCapability;
    
    public GuiTabWardrobeDyes(int tabId, GuiScreen parent, EntityPlayer entityPlayer, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCapability) {
        super(tabId, parent, false);
        this.guiStyle = GuiResourceManager.getGuiJsonInfo(GUI_JSON);
        this.entityPlayer = entityPlayer;
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        //Top half of GUI. (active tab)
        //this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        //this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);
        
        // TODO Render dye slot numbers.
        
        int sloImageSize = 18;
        int sloSize = 16;
        int slotCount = 0;
        GuiContainer guiContainer = (GuiContainer) parent;
        ContainerSkinWardrobe skinWardrobe = (ContainerSkinWardrobe) guiContainer.inventorySlots;
        for (int i = skinWardrobe.getIndexDyeStart(); i <  skinWardrobe.getIndexDyeEnd(); i++) {
            Slot slot = skinWardrobe.inventorySlots.get(i);
            // Draw dye bottle slot.
            this.drawTexturedModalRect(this.x + slot.xPos - 1,  this.y + slot.yPos - 1, 238, 194, sloImageSize, sloImageSize);
            // Draw dye number.
            this.drawTexturedModalRect(this.x + slot.xPos,  this.y + slot.yPos + 18, 112 + slotCount * 16, 240, sloSize, sloSize);
            slotCount++;
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
