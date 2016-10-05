package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class GuiGlobalLibrary extends GuiScreen {
    
    public final TileEntityGlobalSkinLibrary tileEntity;
    public final EntityPlayer player;
    public ArrayList<GuiPanel> panelList;
    
    public Executor jsonDownloadExecutor = Executors.newFixedThreadPool(1);
    public Executor skinDownloadExecutor = Executors.newFixedThreadPool(1);
    
    private static final int PADDING = 5;
    
    public GuiGlobalLibraryPanelHeader panelHeader;
    public GuiGlobalLibraryPanelSearchBox panelSearchBox;
    public GuiGlobalLibraryPanelRecentlyUploaded panelRecentlyUploaded;
    public GuiGlobalLibraryPanelSearchResults panelSearchResults;
    
    private Screen screen;
    
    public static enum Screen {
        HOME,
        SEARCH,
        UPLOAD,
        SKIN_INFO,
        FRIENDS,
        FAVOURITES
    }
    
    public GuiGlobalLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
        this.player = Minecraft.getMinecraft().thePlayer;
        this.panelList = new ArrayList<GuiPanel>();
        
        panelHeader = new GuiGlobalLibraryPanelHeader(this, 2, 2, width - 4, 26);
        panelList.add(panelHeader);
        
        panelSearchBox = new GuiGlobalLibraryPanelSearchBox(this, 2, 31, width - 4, 23);
        panelList.add(panelSearchBox);
        
        panelRecentlyUploaded = new GuiGlobalLibraryPanelRecentlyUploaded(this, 2, 136, width / 2 - 5, height - 141);
        panelList.add(panelRecentlyUploaded);
        
        panelSearchResults = new GuiGlobalLibraryPanelSearchResults(this, 5, 5, 100, 100);
        panelList.add(panelSearchResults);
        
        screen = Screen.HOME;
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        setupPanels();
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).initGui();
        }
        if (screen == Screen.HOME) {
            ((GuiGlobalLibraryPanelRecentlyUploaded)panelRecentlyUploaded).updateRecentlyUploadedSkins();
        }
    }
    
    private void setupPanels() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).setVisible(false);
        }
        int yOffset = PADDING;
        panelHeader.setPosition(PADDING, PADDING).setSize(width - PADDING * 2, 26);
        panelHeader.setVisible(true);
        yOffset += PADDING + 26;
        
        switch (screen) {
        case HOME:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelRecentlyUploaded.setPosition(5, yOffset).setSize(width / 2, height - yOffset - PADDING);
            panelRecentlyUploaded.setVisible(true);
            break;
        case SEARCH:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelSearchResults.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelSearchResults.setVisible(true);
            break;
        default:
            break;
        }
    }
    
    @Override
    public void updateScreen() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).updatePanel();
        }
    }
    
    public void switchScreen(Screen screen) {
        this.screen = screen;
        setupPanels();
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
