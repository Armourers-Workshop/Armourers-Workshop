package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;

@SideOnly(Side.CLIENT)
public abstract class AbstractGuiDialog extends Gui {
    
    protected final GuiScreen parent;
    protected final IDialogCallback callback;
    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    protected ArrayList<GuiButton> buttonList;
    private GuiButton selectedButton;
    
    public AbstractGuiDialog(GuiScreen parent, IDialogCallback callback, int width, int height) {
        this.parent = parent;
        this.callback = callback;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.width = width;
        this.height = height;
        this.buttonList = new ArrayList<GuiButton>();
        initGui();
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
                    guiButton.func_146113_a(this.mc.getSoundHandler());
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
    
    protected void drawbuttons(int mouseX, int mouseY) {
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX, mouseY);
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
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawBackground(mouseX, mouseY, partialTickTime);
        drawForeground(mouseX, mouseY, partialTickTime);
        GL11.glPopAttrib();
    }
    
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        drawParentCoverBackground();
    }
    
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        drawbuttons(mouseX, mouseY);
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
