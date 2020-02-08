package moe.plushie.armourers_workshop.client.gui.newgui;

import java.awt.Point;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabController extends GuiButtonExt {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.TABS);
    private static final ResourceLocation ICONS = new ResourceLocation(LibGuiResources.ICONS);
    
    private GuiScreen parent;
    private boolean fullscreen;
    private int activeTab = -1;
    private ArrayList<GuiTab> tabs = new ArrayList<GuiTab>();
    private int tabSpacing = 27;
    private boolean editMode = false;
    private int tabsPerSide = 5;
    
    public GuiTabController(GuiScreen parent, boolean fullscreen, int xPos, int yPos, int width, int height) {
        super(0, xPos, yPos, width, height, "");
        this.parent = parent;
        this.fullscreen = fullscreen;
        if (!fullscreen) {
            tabSpacing = 25;
        }
    }
    
    public GuiTabController(GuiScreen parent, boolean fullscreen) {
        this(parent, fullscreen, 0, 0, 0, 0);
    }
    
    public void setTabSpacing(int tabSpacing) {
        this.tabSpacing = tabSpacing;
    }
    
    public void initGui(int xPos, int yPos, int width, int height) {
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
    
    public void clearTabs() {
        tabs.clear();
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
    
    private int getYOffSet() {
        if (!fullscreen) {
            return 5;
        } else {
            return (int) ((float)height / 2F - ((float)tabs.size() * tabSpacing) / 2F);
        }
    }
    
    public Point getTabPos(int index) {
        int yOffset = getYOffSet();
        int xOffset = -4;
        int count = 0;
        boolean movedRight = false;
        for (int i = 0; i < tabs.size(); i++) {
            GuiTab tab = tabs.get(i);
            if (tab.visible) {
                if (i == index) {
                    return new Point(this.x + xOffset, this.y + count * tabSpacing + yOffset);
                }
                count++;
            }
            if (count >= getTabsPerSide() & !movedRight) {
                count = 0;
                xOffset += width - tabSpacing;
                movedRight = true;
            }
        }
        return null;
    }
    
    public boolean isTabLeft(int index) {
        int count = 0;
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).visible) {
                if (i == index) {
                    return count < getTabsPerSide();
                }
                count++;
            }
        }
        return true;
    }
    
    public int getTabsPerSide() {
        return tabsPerSide ;
    }
    
    public void setTabsPerSide(int count) {
        tabsPerSide = count;
    }
    
    public GuiTab getActiveTab() {
        if (activeTab >= 0 & activeTab < tabs.size()) {
            return tabs.get(activeTab);
        }
        return null;
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        for (int i = 0; i < tabs.size(); i++) {
            Point point = getTabPos(i);
            if (point != null) {
                if (tabs.get(i).isMouseOver(point.x, point.y, mouseX, mouseY)) {
                    if (tabs.get(i).mousePress(point.x, point.y, mouseX, mouseY)) {
                        activeTab = i;
                    }
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (int i = 0; i < tabs.size(); i++) {
            Point point = getTabPos(i);
            if (point != null) {
                mc.renderEngine.bindTexture(TEXTURE);
                tabs.get(i).render(i, point.x, point.y, mouseX, mouseY, activeTab == i, ICONS, isTabLeft(i));
            }
        }
    }
    
    public boolean isEditMode() {
        return editMode;
    }
    
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    
    public void drawHoverText(Minecraft mc, int mouseX, int mouseY) {
        GuiTab hoverTab = null;
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (int i = 0; i < tabs.size(); i++) {
            Point point = getTabPos(i);
            if (point != null) {
                if (tabs.get(i).isMouseOver(point.x, point.y, mouseX, mouseY)) {
                    hoverTab = tabs.get(i);
                }
            }
        }
        
        if (hoverTab != null) {
            ArrayList<String> textList = new ArrayList<String>();
            textList.add(hoverTab.getName());
            GuiHelper.drawHoveringText(textList, mouseX, mouseY, mc.fontRenderer, parent.width, parent.height, zLevel);
        }
    }
}
