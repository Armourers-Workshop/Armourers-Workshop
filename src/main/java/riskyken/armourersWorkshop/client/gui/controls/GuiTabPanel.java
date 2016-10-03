package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public abstract class GuiTabPanel extends Gui {
    
    private final int tabId;
    protected final GuiScreen parent;
    protected final FontRenderer fontRenderer;
    protected final Minecraft mc;
    
    protected ArrayList<GuiButton> buttonList;
    protected int width;
    protected int height;
    private GuiButton selectedButton;
    
    public GuiTabPanel(int tabId, GuiScreen parent) {
        this.tabId = tabId;
        this.parent = parent;
        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = mc.fontRendererObj;
        
        buttonList = new ArrayList<GuiButton>();
    }
    
    public void initGui() {
        buttonList.clear();
        this.width = parent.width;
        this.height = parent.height;
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
                if (guiButton.mousePressed(mc, mouseX, mouseY)) {
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
    
    public void drawForegroundLayer(int mouseX, int mouseY) {
        for (int i = 0; i < buttonList.size(); i++) {
            buttonList.get(i).drawButton(mc, mouseX, mouseY);
        }
    }
    
    public boolean keyTyped(char c, int keycode) {
        return false;
    }
    
    public abstract void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY);
}
