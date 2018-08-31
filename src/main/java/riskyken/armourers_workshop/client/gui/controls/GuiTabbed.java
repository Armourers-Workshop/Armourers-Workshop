package riskyken.armourers_workshop.client.gui.controls;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabbed extends GuiContainer {
    
    protected GuiTabController tabController;
    protected ArrayList<GuiTabPanel> tabList;
    protected static int activeTab = 0;
    
    public GuiTabbed(Container container, boolean fullscreen, ResourceLocation texture) {
        super(container);
        tabController = new GuiTabController(this, fullscreen, texture);
        tabList = new ArrayList<GuiTabPanel>();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        
        tabController.initGui(guiLeft - 17, guiTop, xSize, ySize);
        
        tabController.setActiveTabIndex(activeTab);
        
        for (int i = 0; i < tabList.size(); i++) {
            tabList.get(i).initGui(guiLeft, guiTop, xSize, ySize);
        }
        buttonList.add(tabController);
        
        tabChanged();
    }

    protected void tabChanged() {
        this.activeTab = tabController.getActiveTabIndex();
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            tab.tabChanged(activeTab);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.mouseClicked(mouseX, mouseY, button);
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.mouseMovedOrUp(mouseX, mouseY, state);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == tabController) {
            tabChanged();
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        boolean keyTyped = false;
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                keyTyped = tab.keyTyped(c, keycode);
            }
        }
        if (!keyTyped) {
            super.keyTyped(c, keycode);
        }
    }
}
