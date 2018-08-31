package riskyken.armourers_workshop.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import riskyken.armourers_workshop.api.common.skin.data.ISkinPointer;
import riskyken.armourers_workshop.client.render.ModRenderHelper;
import riskyken.armourers_workshop.client.render.SkinItemRenderHelper;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.inventory.ContainerDyeTable;
import riskyken.armourers_workshop.common.inventory.slot.SlotDyeBottle;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.skin.data.SkinDye;
import riskyken.armourers_workshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class GuiDyeTable extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/dyeTable.png");
    
    private final TileEntityDyeTable tileEntity;
    private SkinDye[] rolloverDyes;
    
    public GuiDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        super(new ContainerDyeTable(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 320;
        this.ySize = 190;
        rolloverDyes = new SkinDye[8];
        for (int i = 0; i < 8; i++) {
            rolloverDyes[i] = new SkinDye();
            for (int j = 0; j < 8; j++) {
                rolloverDyes[i].addDye(j, new byte[] {(byte)255, (byte)255, (byte)255, (byte)0});
            }
            rolloverDyes[i].addDye(i, new byte[] {(byte)255, (byte)255, (byte)255, (byte)255});
        }
    }
    
    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f1, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 256, this.ySize);
        drawTexturedModalRect(this.guiLeft + 182 + 56, this.guiTop, 174, 0, 82, this.ySize);
        //drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0x88FFFFFF);
        if (ConfigHandler.lockDyesOnSkins) {
            ModRenderHelper.enableAlphaBlend();
            for (int i = 0; i < 8; i++) {
                SlotDyeBottle dyeSlot = (SlotDyeBottle) inventorySlots.getSlot(37 + i);
                if (dyeSlot.isLocked()) {
                    drawRect(this.guiLeft + dyeSlot.xPos, this.guiTop + dyeSlot.yPos, this.guiLeft + dyeSlot.xPos + 16, this.guiTop + dyeSlot.yPos + 16, 0x88FF0000);
                }
            }
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntity.getName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 46, this.ySize - 96 + 2, 4210752);
        Slot slot = (Slot) inventorySlots.inventorySlots.get(45);
        ItemStack skinStack = slot.getStack();
        ISkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        
        if (skinPointer != null) {
            GL11.glPushMatrix();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            RenderHelper.enableStandardItemLighting();
            float boxX = 242.5F;
            float boxY = 102;
            float scale = 11F;
            
            GL11.glTranslatef((float)boxX, (float)boxY, 500.0F);
            GL11.glScalef(-scale, scale, scale);
            float rotation = (float)((double)System.currentTimeMillis() / 10 % 360);
            float fade = (float) ((double)System.currentTimeMillis() / 400 % Math.PI * 2);
            float change = (float) Math.sin(fade);
            
            float alpha = change * 50;
            //ModLogger.log(alpha);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glRotatef(rotation, 0, 1, 0);
            //GL11.glEnable(GL11.GL_ALPHA_TEST);
            //GL11.glEnable(GL11.GL_DEPTH_TEST);
            //ModRenderHelper.enableAlphaBlend();
            //GL11.glDisable(GL11.GL_CULL_FACE);
            //GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawRect(guiLeft, guiTop, guiLeft + 50, guiTop + 50, 0xFFFFFFFF);
            int dyeSlot = mouseOverDyeSlot(mouseX, mouseY);
            dyeSlot = -1;
            if (dyeSlot != -1) {
                GL11.glPushMatrix();
                //GL11.glEnable(GL11.GL_CULL_FACE);
                SkinItemRenderHelper.renderSkinAsItem(skinPointer, true, false, 140, 176);
                //ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true, false);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                Color c = new Color(198,198,198, (int)(240));
                RenderHelper.disableStandardItemLighting();
                
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                drawRect(152, 20, 250, 95, c.getRGB());
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                RenderHelper.enableStandardItemLighting();
                GL11.glPushMatrix();
                GL11.glTranslatef((float)boxX, (float)boxY, 200.0F);
                GL11.glScalef(-scale, scale, scale);
                GL11.glRotatef(-20, 1, 0, 0);
                GL11.glRotatef(rotation, 0, 1, 0);
                for (int i = 0; i < 8; i++) {
                    if (i != dyeSlot) {
                        skinPointer.getSkinDye().addDye(i, rolloverDyes[dyeSlot].getDyeColour(i));
                    }
                }
                GL11.glColor3f(1F, 1F, 1F);
                SkinItemRenderHelper.renderSkinAsItem(skinPointer, true, false, 140, 176);
            } else {
                ModRenderHelper.enableAlphaBlend();
                SkinItemRenderHelper.renderSkinAsItem(skinPointer, true, false, 140, 176);
            }
            GL11.glPopMatrix();
        }
        
        GL11.glDisable(GL11.GL_DEPTH_TEST);

    }
    
    private int mouseOverDyeSlot(int mouseX, int mouseY) {
        for (int i = 0; i < 8; i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(37 + i);
            if (mouseX - guiLeft >= slot.xPos & mouseX - guiLeft <= slot.xPos + 16) {
                if (mouseY - guiTop >= slot.yPos & mouseY - guiTop <= slot.yPos + 16) {
                    return i;
                }
            }
        }
        return -1;
    }
}
