package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public abstract class GuiPanel extends Gui {
    
    protected final GuiScreen parent;
    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean enabled;
    protected boolean visible;
    protected GuiDialog dialog;
    
    protected ArrayList<GuiButton> buttonList;
    private GuiButton selectedButton;
    
    public GuiPanel(GuiScreen parent, int x, int y, int width, int height) {
        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.enabled = true;
        this.visible = true;
        
        buttonList = new ArrayList<GuiButton>();
    }
    
    public void initGui() {
        if (haveOpenDialog()) {
            this.dialog.initGui();
        }
    }
    
    public GuiPanel setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public GuiPanel setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    public GuiPanel setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public GuiPanel setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean haveOpenDialog() {
        return dialog != null;
    }
    
    public GuiDialog getDialog() {
        return dialog;
    }
    
    public void setDialog(GuiDialog dialog) {
        this.dialog = dialog;
    }
    
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!this.enabled | !this.visible) {
            return;
        }
        if (haveOpenDialog()) {
            this.dialog.mouseClicked(mouseX, mouseY, button);
            return;
        }
        if (button == 0) {
            for (int i = 0; i < buttonList.size(); i++) {
                GuiButton guiButton = buttonList.get(i);
                if (guiButton.mousePressed(mc, mouseX, mouseY)) {
                    ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(parent, guiButton, buttonList);
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        break;
                    }
                    this.selectedButton = event.button;
                    event.button.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(event.button);
                    if (parent.equals(this.mc.currentScreen)) {
                        MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(parent, event.button, this.buttonList));
                    }
                }
            }
        }
    }
    
    protected void actionPerformed(GuiButton button) {}
    
    public void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (!this.enabled | !this.visible) {
            return;
        }
        if (haveOpenDialog()) {
            this.dialog.mouseMovedOrUp(mouseX, mouseY, button);
            return;
        }
        if (this.selectedButton != null && button == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        if (!this.enabled | !this.visible) {
            return false;
        }
        if (haveOpenDialog()) {
            return this.dialog.keyTyped(c, keycode);
        }
        return false;
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
        if (!this.visible) {
            return;
        }
        drawbuttons(mouseX, mouseY);
        if (haveOpenDialog()) {
            this.dialog.draw(mouseX, mouseY, partialTickTime);
        }
    }
    
    public void update() {
        if (haveOpenDialog()) {
            this.dialog.update();
        }
    }
}
