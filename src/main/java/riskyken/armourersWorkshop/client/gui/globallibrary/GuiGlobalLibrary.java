package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class GuiGlobalLibrary extends GuiScreen {
    
    public final TileEntityGlobalSkinLibrary tileEntity;
    public final EntityPlayer player;
    public ArrayList<GuiPanel> panelList;
    
    public GuiGlobalLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
        this.player = Minecraft.getMinecraft().thePlayer;
        this.panelList = new ArrayList<GuiPanel>();
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        panelList.clear();
        
        panelList.add(new GuiGlobalLibraryPanelHeader(this, 2, 2, width - 4, 26));
        panelList.add(new GuiGlobalLibraryPanelSearchBox(this, 2, 31, width - 4, 23));
        panelList.add(new GuiGlobalLibraryPanelRecentlyUploaded(this, 2, 136, width / 2 - 5, height - 141));
        panelList.add(new GuiGlobalLibraryPanelSearchBox(this, width / 2 + 4, 136, width / 2 - 7, height - 141));
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).initGui();
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).mouseMovedOrUp(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) {
        boolean keyTyped = false;
        for (int i = 0; i < panelList.size(); i++) {
            if (panelList.get(i).keyTyped(c, keycode)) {
                keyTyped = true;
            }
        }
        if (!keyTyped) {
            super.keyTyped(c, keycode);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTickTime);
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).drawScreen(mouseX, mouseY, partialTickTime);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
