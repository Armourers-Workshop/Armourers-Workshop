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
public abstract class GuiTabPanel extends Gui {
    
    private final int tabId;
    protected final GuiScreen parent;
    protected final FontRenderer fontRenderer;
    protected final Minecraft mc;
    
    protected ArrayList<GuiButton> buttonList;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final boolean fullscreen;
    private GuiButton selectedButton;
    
    public GuiTabPanel(int tabId, GuiScreen parent, boolean fullscreen) {
        this.tabId = tabId;
        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.fullscreen = fullscreen;
        
        buttonList = new ArrayList<GuiButton>();
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
        buttonList.clear();
        if (fullscreen) {
            this.x = 0;
            this.y = 0;
            this.width = parent.width;
            this.height = parent.height;
        } else {
            this.x = xPos;
            this.y = yPos;
            this.width = width;
            this.height = height;
        }
    }
    
    public int getTabId() {
        return tabId;
    }
    
    public void tabChanged(int tabIndex) {
        
    }
    
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < buttonList.size(); i++) {
                GuiButton guiButton = buttonList.get(i);
                if (guiButton.mousePressed(mc, mouseX - x, mouseY - y)) {
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
        if (this.selectedButton != null && button == 0) {
            this.selectedButton.mouseReleased(mouseX - x, mouseY - y);
            this.selectedButton = null;
        }
    }
    
    public void drawForegroundLayer(int mouseX, int mouseY) {
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX - x, mouseY - y);
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        return false;
    }
    
    public abstract void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY);
}
