package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

@SideOnly(Side.CLIENT)
public abstract class GuiDialog extends Gui {
    
    protected final GuiPanel parent;
    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    protected ArrayList<GuiButton> buttonList;
    private GuiButton selectedButton;
    
    public GuiDialog(GuiPanel parent, int width, int height) {
        this.parent = parent;
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
            closeDialog();
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
    
    public void closeDialog() {
        parent.setDialog(null);
    }
    
    public boolean keyTyped(char c, int keycode) {
        return false;
    }
    
    protected void drawParentCoverBackground() {
        drawGradientRect(this.parent.x, this.parent.y, this.parent.x + this.parent.width, this.parent.y + this.parent.height, 0xC0101010, 0xD0101010);
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
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        drawParentCoverBackground();
        drawbuttons(mouseX, mouseY);
        GL11.glPopAttrib();
    }
    
    public void update() {}
}
