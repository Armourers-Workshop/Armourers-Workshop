package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.common.inventory.ContainerDyeTable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class GuiDyeTable extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/dyeTable.png");
    
    private final TileEntityDyeTable tileEntity;
    private SkinDye[] rolloverDyes;
    
    public GuiDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        super(new ContainerDyeTable(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.xSize = 256;
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
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 46, this.ySize - 96 + 2, 4210752);
        Slot slot = (Slot) inventorySlots.inventorySlots.get(36);
        ItemStack skinStack = slot.getStack();
        ISkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        
        if (skinPointer != null) {
            GL11.glPushMatrix();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            RenderHelper.enableStandardItemLighting();
            float boxX = 210.5F;
            float boxY = 56;
            float scale = 40F;
            
            GL11.glTranslatef((float)boxX, (float)boxY, 50.0F);
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
            GL11.glDisable(GL11.GL_CULL_FACE);
            //GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawRect(guiLeft, guiTop, guiLeft + 50, guiTop + 50, 0xFFFFFFFF);
            int dyeSlot = mouseOverDyeSlot(mouseX, mouseY);
            dyeSlot = -1;
            if (dyeSlot != -1) {
                GL11.glPushMatrix();
                //GL11.glEnable(GL11.GL_CULL_FACE);
                ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                Color c = new Color(198,198,198, (int)(200 + alpha));
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
                ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true);
            } else {
                ItemStackRenderHelper.renderItemModelFromSkinPointer(skinPointer, true);
            }
            
            
            GL11.glPopMatrix();
        }
    }
    
    private int mouseOverDyeSlot(int mouseX, int mouseY) {
        for (int i = 0; i < 8; i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(37 + i);
            if (mouseX - guiLeft >= slot.xDisplayPosition & mouseX - guiLeft <= slot.xDisplayPosition + 16) {
                if (mouseY - guiTop >= slot.yDisplayPosition & mouseY - guiTop <= slot.yDisplayPosition + 16) {
                    return i;
                }
            }
        }
        return -1;
    }
}
