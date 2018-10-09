package moe.plushie.armourers_workshop.client.gui;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.inventory.ContainerEntityEquipment;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiEntityEquipment extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibModInfo.ID, "textures/gui/entity-skin-inventory.png");
    
    private final EntitySkinCapability entitySkinCapability;
    
    private int sizeBottom = 90;
    private int sizeTop = 58;
    
    public GuiEntityEquipment(InventoryPlayer playerInventory, EntitySkinCapability entitySkinCapability) {
        super(new ContainerEntityEquipment(playerInventory, entitySkinCapability));
        this.entitySkinCapability = entitySkinCapability;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    @Override
    public void initGui() {
        int maxForType = 1;
        ISkinType[] skinTypes = entitySkinCapability.getValidSkinTypes();
        for (int i = 0; i < skinTypes.length; i++) {
            maxForType = Math.max(maxForType, entitySkinCapability.getSlotCountForSkinType(skinTypes[i]));
        }
        sizeTop = 28 + maxForType * 18;
        
        this.xSize = 176;
        this.ySize = sizeBottom + sizeTop;
        super.initGui();
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
        
        GuiUtils.drawContinuousTexturedBox(this.guiLeft, this.guiTop, 0, 0, this.xSize, sizeTop, this.xSize, 58, 4, 4, 4, 0, zLevel);
        GuiUtils.drawContinuousTexturedBox(this.guiLeft, this.guiTop + sizeTop, 0, 58, this.xSize, sizeBottom, this.xSize, sizeBottom, 0, 4, 4, 4, zLevel);
        
        ISkinType[] skinTypes = entitySkinCapability.getValidSkinTypes();
        for (int i = 0; i < skinTypes.length; i++) {
            for (int j = 0; j < entitySkinCapability.getSlotCountForSkinType(skinTypes[i]); j++) {
                drawTexturedModalRect(this.guiLeft + 7 + i * 18, this.guiTop + 20 + j * 18, 0, 148, 18, 18);
            }
        }
    }
}
