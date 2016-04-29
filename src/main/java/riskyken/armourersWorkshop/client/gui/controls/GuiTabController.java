package riskyken.armourersWorkshop.client.gui.controls;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;

@SideOnly(Side.CLIENT)
public class GuiTabController extends GuiScreen {

    private GuiScreen parent;
    private int activeTab = -1;
    private ArrayList<GuiTab> tabs = new ArrayList<GuiTab>();
    
    public GuiTabController(GuiScreen parent) {
        this.parent = parent;
    }
    
    public void setActiveTab(int tabIndex) {
        if (tabIndex < getTabCount() - 1) {
            activeTab = tabIndex;
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
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
