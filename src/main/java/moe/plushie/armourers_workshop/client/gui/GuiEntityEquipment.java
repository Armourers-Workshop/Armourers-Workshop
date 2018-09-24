package moe.plushie.armourers_workshop.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.inventory.ContainerEntityEquipment;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEntityEquipment extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibModInfo.ID, "textures/gui/entity-skin-inventory.png");
    
    private final EntitySkinCapability entitySkinCapability;
    
    public GuiEntityEquipment(InventoryPlayer playerInventory, EntitySkinCapability entitySkinCapability) {
        super(new ContainerEntityEquipment(playerInventory, entitySkinCapability));
        this.entitySkinCapability = entitySkinCapability;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.xSize = 176;
        this.ySize = 148;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, "entityEquipment");
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        ISkinType[] skinTypes = entitySkinCapability.getValidSkinTypes();
        for (int i = 0; i < skinTypes.length; i++) {
            drawTexturedModalRect(this.guiLeft + 7 + i * 18, this.guiTop + 20, 0, 148, 18, 18);
        }
    }
}
