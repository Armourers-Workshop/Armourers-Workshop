package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabPanel<T extends GuiScreen> extends Gui {
    
    private final int tabId;
    protected final T parent;
    protected final FontRenderer fontRenderer;
    protected final Minecraft mc;
    
    protected ArrayList<GuiButton> buttonList;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    private final boolean fullscreen;
    private GuiButton selectedButton;
    
    public GuiTabPanel(int tabId, T parent, boolean fullscreen) {
        this.tabId = tabId;
        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRenderer;
        this.fullscreen = fullscreen;
        
        buttonList = new ArrayList<GuiButton>();
    }
    
    public GuiTabPanel(int tabId, T parent) {
        this(tabId, parent, false);
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
        buttonList.clear();
        if (isFullscreen()) {
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
    
    protected boolean isFullscreen() {
        return this.fullscreen;
    }
    
    public int getTabId() {
        return tabId;
    }
    
    public void tabChanged(int tabIndex) {
        
    }
    
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < buttonList.size(); i++) {
                GuiButton guiButton = buttonList.get(i);
                if (guiButton.mousePressed(mc, mouseX - x, mouseY - y)) {
                    ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(parent, guiButton, buttonList);
                    if (MinecraftForge.EVENT_BUS.post(event)) {
                        break;
                    }
                    this.selectedButton = event.getButton();
                    event.getButton().playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(event.getButton());
                    if (parent.equals(this.mc.currentScreen)) {
                        MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(parent, event.getButton(), this.buttonList));
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void actionPerformed(GuiButton button) {}
    
    public boolean mouseMovedOrUp(int mouseX, int mouseY, int button) {
        if (this.selectedButton != null && button == 0) {
            this.selectedButton.mouseReleased(mouseX - x, mouseY - y);
            this.selectedButton = null;
        }
        return false;
    }
    
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX - x, mouseY - y, partialTickTime);
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        return false;
    }
    
    public abstract void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY);

    public void update() {
    }
}
