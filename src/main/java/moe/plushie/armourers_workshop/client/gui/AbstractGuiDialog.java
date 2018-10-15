package moe.plushie.armourers_workshop.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiDialog extends Gui {
    
    protected static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/dialog.png");
    
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
    
    public AbstractGuiDialog(GuiScreen parent, String name, IDialogCallback callback, int width, int height) {
        this.parent = parent;
        this.name = name;
        this.callback = callback;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;
        this.buttonList = new ArrayList<GuiButton>();
        //initGui();
    }
    
    public void initGui() {
        this.x = this.parent.width / 2 - this.width / 2;
        this.y = this.parent.height / 2 - this.height / 2;
    }
    
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX < this.x | mouseX >= this.x + this.width | mouseY < this.y | mouseY >= this.y + this.height) {
            // mouse click outside of dialog
            // returnDialogResult(DialogResult.CANCEL);
        }
        if (button == 0) {
            for (int i = 0; i < this.buttonList.size(); i++) {
                GuiButton guiButton = this.buttonList.get(i);
                if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guiButton;
                    //guiButton.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(guiButton);
                }
            }
        }
    }
    
    protected void actionPerformed(GuiButton button) {}
    
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (this.selectedButton != null && button == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }
    
    public void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
    }
    
    public void returnDialogResult(DialogResult result) {
        if (callback != null) {
            callback.dialogResult(this, result);
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        if (keycode == 1 || keycode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            returnDialogResult(DialogResult.CANCEL);
            return true;
        }
        return false;
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
    
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        //RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawBackground(mouseX, mouseY, partialTickTime);
        drawForeground(mouseX, mouseY, partialTickTime);
        GL11.glPopAttrib();
    }
    
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        drawParentCoverBackground();
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(texture);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, textureWidth, textureHeight, borderSize, zLevel);
    }
    
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        drawbuttons(mouseX, mouseY, partialTickTime);
    }
    
    protected void drawTitle() {
        String title = GuiHelper.getLocalizedControlName(name, "title");
        int titleWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, x + width / 2 - titleWidth / 2, y + 6, 4210752);
    }
    
    public void update() {}
    
    public interface IDialogCallback {
        
        public void dialogResult(AbstractGuiDialog dialog, DialogResult result);
        
    }
    
    public enum DialogResult {
        OK,
        CANCEL
    }
}
