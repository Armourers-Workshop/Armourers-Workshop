package moe.plushie.armourers_workshop.client.gui.controls;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiDialog extends Gui implements IDialogCallback {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.COMMON);

    protected final GuiScreen parent;
    protected final String name;
    protected final IDialogCallback callback;
    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected ArrayList<GuiButton> buttonList;
    private GuiButton selectedButton;

    protected AbstractGuiDialog dialog;
    int oldMouseX;
    int oldMouseY;

    protected GuiSlotHandler slotHandler;

    public AbstractGuiDialog(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        this.parent = parent;
        this.name = name;
        this.callback = callback;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;
        this.buttonList = new ArrayList<GuiButton>();
        if (parent instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer) parent;
            slotHandler = new GuiSlotHandler(guiContainer);
        }
    }

    public void initGui() {
        this.x = (this.parent.width - this.width) / 2;
        this.y = (this.parent.height  - this.height) / 2;
        
        
        if (isDialogOpen()) {
            dialog.initGui();
        }
        if (slotHandler != null) {
            slotHandler.initGui();
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isDialogOpen()) {
            dialog.mouseClicked(mouseX, mouseY, button);
        } else {
            if (mouseX < this.x | mouseX >= this.x + this.width | mouseY < this.y | mouseY >= this.y + this.height) {
                // mouse click outside of dialog
                // returnDialogResult(DialogResult.CANCEL);
            }
            if (slotHandler != null) {
                try {
                    updateSlots(false);
                    slotHandler.mouseClicked(mouseX, mouseY, button);
                    updateSlots(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (button == 0) {
                for (int i = 0; i < this.buttonList.size(); i++) {
                    GuiButton guiButton = this.buttonList.get(i);
                    if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
                        this.selectedButton = guiButton;
                        guiButton.playPressSound(this.mc.getSoundHandler());
                        this.actionPerformed(guiButton);
                    }
                }
            }
        }
    }

    protected void actionPerformed(GuiButton button) {
    }

    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (isDialogOpen()) {
            dialog.mouseMovedOrUp(mouseX, mouseY, button);
        } else {
            if (slotHandler != null) {
                updateSlots(false);
                slotHandler.mouseReleased(mouseX, mouseY, button);
                updateSlots(true);
            }
            if (this.selectedButton != null && button == 0) {
                this.selectedButton.mouseReleased(mouseX, mouseY);
                this.selectedButton = null;
            }
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (isDialogOpen()) {
            dialog.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        } else {
            if (slotHandler != null) {
                updateSlots(false);
                slotHandler.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
                updateSlots(true);
            }
        }
    }

    public void returnDialogResult(DialogResult result) {
        if (callback != null) {
            callback.dialogResult(this, result);
        }
    }

    public boolean keyTyped(char c, int keycode) {
        if (isDialogOpen()) {
            return dialog.keyTyped(c, keycode);
        } else {
            if (keycode == 1 || keycode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
                returnDialogResult(DialogResult.CANCEL);
                return true;
            }
            return false;
        }
    }

    protected void drawParentCoverBackground() {
        drawGradientRect(0, 0, this.parent.width, this.parent.height, 0xC0101010, 0xD0101010);
    }

    protected void drawbuttons(int mouseX, int mouseY, float partialTickTime) {
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX, mouseY, partialTickTime);
        }
        for (int i = 0; i < buttonList.size(); i++) {
            if (buttonList.get(i) instanceof GuiIconButton) {
                ((GuiIconButton) buttonList.get(i)).drawRollover(mc, mouseX, mouseY);
            }
        }
    }

    public void drawFull(int mouseX, int mouseY, float partialTickTime) {
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        drawBackground(mouseX, mouseY, partialTickTime);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        // drawItems(mouseX, mouseY, partialTickTime);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
        
        drawForeground(mouseX, mouseY, partialTickTime);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        
        if (isDialogOpen()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            //RPGFramework.getLogger().info(oldMouseX);
            //GL11.glTranslatef(x, y, 0);
            dialog.drawFull(oldMouseX, oldMouseY, 0);
            //GL11.glTranslatef(-x, -y, 0);
        }
    }

    protected void updateSlots(boolean restore) {

    }

    public void drawItems(int mouseX, int mouseY, float partialTickTime) {
        if (slotHandler != null) {
            GuiContainer guiContainer = (GuiContainer) parent;

            for (int i1 = 0; i1 < guiContainer.inventorySlots.inventorySlots.size(); ++i1) {
                Slot slot = guiContainer.inventorySlots.inventorySlots.get(i1);

                if (slot.isEnabled()) {
                    slotHandler.drawSlot(slot);
                }

                if (slotHandler.isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled()) {
                    slotHandler.hoveredSlot = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    int j1 = slot.xPos;
                    int k1 = slot.yPos;
                    GlStateManager.colorMask(true, true, true, false);
                    this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }
        }
    }

    public void draw(int mouseX, int mouseY, float partialTickTime) {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GlStateManager.pushAttrib();
        // RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1, 1, 1, 1);
        // GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        // GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        // RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // GL11.glDisable(GL11.GL_LIGHTING);
        // GL11.glDisable(GL11.GL_DEPTH_TEST);
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }

        drawBackground(mouseX, mouseY, partialTickTime);
        GlStateManager.translate(-x, -y, 0);
        drawForeground(mouseX, mouseY, partialTickTime);

        if (isDialogOpen()) {
            GL11.glColor4f(1, 1, 1, 1);
             GL11.glTranslatef(x, y, 0);
             ModLogger.log(mouseX);
            dialog.drawFull(oldMouseX, oldMouseY, 0);
            GL11.glTranslatef(-x, -y, 0);
        }
        GL11.glPopAttrib();
        // GL11.glPopAttrib();

        GlStateManager.popAttrib();
    }

    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        drawParentCoverBackground();
        int textureWidth = 128;
        int textureHeight = 128;
        int borderSize = 4;
        mc.renderEngine.bindTexture(TEXTURE);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, textureWidth, textureHeight, borderSize, zLevel);
    }

    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        drawbuttons(mouseX, mouseY, partialTickTime);
        if (slotHandler != null) {
            updateSlots(false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 10);
            slotHandler.drawScreen(mouseX, mouseY, partialTickTime);
            updateSlots(true);
            GlStateManager.popMatrix();
        }
        GlStateManager.disableDepth();
    }

    protected void drawTitle() {
        drawTitle(name);
    }

    protected void drawTitle(String text) {
        String title = GuiHelper.getLocalizedControlName(text, "title");
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
    }

    public void openDialog(AbstractGuiDialog dialog) {
        this.dialog = dialog;
        dialog.initGui();
    }

    protected boolean isDialogOpen() {
        return dialog != null;
    }

    protected void closeDialog() {
        this.dialog = null;
    }

    public void update() {
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.CANCEL) {
            closeDialog();
        }
    }
}