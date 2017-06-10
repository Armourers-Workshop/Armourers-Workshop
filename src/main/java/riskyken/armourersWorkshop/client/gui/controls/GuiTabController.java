package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class GuiTabController extends GuiButtonExt {

    protected static final ResourceLocation tabTextures = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/mannequinTabs.png");
    
    private GuiScreen parent;
    private boolean fullscreen;
    private int activeTab = -1;
    private ArrayList<GuiTab> tabs = new ArrayList<GuiTab>();
    
    public GuiTabController(GuiScreen parent, boolean fullscreen, int xPos, int yPos, int width, int height) {
        super(0, xPos, yPos, width, height, "");
        this.parent = parent;
        this.fullscreen = fullscreen;
    }
    
    public GuiTabController(GuiScreen parent, boolean fullscreen) {
        this(parent, fullscreen, 0, 0, 0, 0);
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
        if (fullscreen) {
            this.xPosition = 0;
            this.yPosition = 0;
            this.width = parent.width;
            this.height = parent.height;
        } else {
            this.xPosition = xPos;
            this.yPosition = yPos;
            this.width = width;
            this.height = height;
        }
    }
    
    public void setActiveTabIndex(int index) {
        if (index < getTabCount() - 1) {
            activeTab = index;
        } else {
            activeTab = getTabCount() - 1;
        }
        if (getTabCount() == 0) {
            activeTab = -1;
        }
    }
    
    public void addTab(GuiTab tab) {
        tabs.add(tab);
    }
    
    public int getTabCount() {
        return tabs.size();
    }
    
    public int getActiveTabIndex() {
        return activeTab;
    }
    
    public String getActiveTabName() {
        GuiTab tab = getActiveTab();
        if (tab != null) {
            return tab.getName();
        }
        return "";
    }
    
    public GuiTab getTab(int index) {
        if (index >= 0 & index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }
    
    public GuiTab getActiveTab() {
        if (activeTab >= 0 & activeTab < tabs.size()) {
            return tabs.get(activeTab);
        }
        return null;
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int yOffset = (int) ((float)height / 2F - ((float)tabs.size() * 27F) / 2F);
        if (!fullscreen) {
            yOffset = 0;
        }
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.isMouseOver(this.xPosition - 4, this.yPosition + i * 27  + yOffset, mouseX, mouseY)) {
                if (tab.enabled) {
                    activeTab = i;
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(tabTextures);
        int yOffset = (int) ((float)height / 2F - ((float)tabs.size() * 27F) / 2F);
        
        if (!fullscreen) {
            yOffset = 0;
        }
        
        GuiTab hoverTab = null;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.isMouseOver(this.xPosition - 4, this.yPosition + i * 27 + yOffset, mouseX, mouseY)) {
                hoverTab = tab;
            }
            tab.render(this.xPosition - 4, this.yPosition + i * 27 + yOffset, mouseX, mouseY, activeTab == i);
        }
        
        if (hoverTab != null) {
            ArrayList<String> textList = new ArrayList<String>();
            textList.add(hoverTab.getName());
            GuiHelper.drawHoveringText(textList, mouseX, mouseY, Minecraft.getMinecraft().fontRenderer, width, height, zLevel);
        }
    }
}
