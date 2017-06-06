package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelCreateAccount;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelHeader;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelLogin;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelRecentlyUploaded;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchBox;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchResults;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSkinInfo;
import riskyken.armourersWorkshop.common.inventory.ContainerGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibrary extends GuiContainer {
    
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
    public GuiGlobalLibraryPanelSkinInfo panelSkinInfo;
    public GuiGlobalLibraryPanelLogin panelLogin;
    public GuiGlobalLibraryPanelCreateAccount panelCreateAccount;
    
    private Screen screen;
    
    public static enum Screen {
        HOME,
        SEARCH,
        UPLOAD,
        SKIN_INFO,
        FRIENDS,
        FAVOURITES,
        LOGON,
        CREATE_ACCOUNT
    }
    
    public GuiGlobalLibrary(TileEntityGlobalSkinLibrary tileEntity, InventoryPlayer inventoryPlayer) {
        super(new ContainerGlobalSkinLibrary(inventoryPlayer, tileEntity));
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
        
        panelSkinInfo = new GuiGlobalLibraryPanelSkinInfo(this, 5, 5, 100, 100);
        panelList.add(panelSkinInfo);
        
        panelLogin = new GuiGlobalLibraryPanelLogin(this, 5, 5, 500, 500);
        panelList.add(panelLogin);
        
        panelCreateAccount = new GuiGlobalLibraryPanelCreateAccount(this, 5, 5, 100, 100);
        panelList.add(panelCreateAccount);
        
        screen = Screen.HOME;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        setupPanels();
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).initGui();
        }
        if (screen == Screen.HOME) {
            ((GuiGlobalLibraryPanelRecentlyUploaded)panelRecentlyUploaded).updateRecentlyUploadedSkins();
        }
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(false);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setVisible(false);
                }
            }
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
            panelRecentlyUploaded.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelRecentlyUploaded.setVisible(true);
            break;
        case SEARCH:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelSearchResults.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelSearchResults.setVisible(true);
            break;
        case SKIN_INFO:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelSkinInfo.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelSkinInfo.setVisible(true);
            break;
        case LOGON:
            panelLogin.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelLogin.setVisible(true);
            break;
        default:
            break;
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).update();
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
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).draw(mouseX, mouseY, partialTickTime);
        }
    }
    
    public String getGuiName() {
        return "globalSkinLibrary";
    }
}
