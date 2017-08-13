package riskyken.armourersWorkshop.client.gui.globallibrary;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelHeader;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelHome;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelLogin;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchBox;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchResults;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSkinInfo;
import riskyken.armourersWorkshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelUpload;
import riskyken.armourersWorkshop.common.addons.ModAddonManager;
import riskyken.armourersWorkshop.common.inventory.ContainerGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibrary extends GuiContainer {
    
    public final TileEntityGlobalSkinLibrary tileEntity;
    public final EntityPlayer player;
    public ArrayList<GuiPanel> panelList;
    
    public Executor jsonDownloadExecutor = Executors.newFixedThreadPool(2);
    public Executor skinDownloadExecutor = Executors.newFixedThreadPool(2);
    
    private static final int PADDING = 5;
    private boolean isNEIVisible;
    
    public GuiGlobalLibraryPanelHeader panelHeader;
    public GuiGlobalLibraryPanelSearchBox panelSearchBox;
    public GuiGlobalLibraryPanelHome panelHome;
    public GuiGlobalLibraryPanelSearchResults panelSearchResults;
    public GuiGlobalLibraryPanelSkinInfo panelSkinInfo;
    public GuiGlobalLibraryPanelLogin panelLogin;
    public GuiGlobalLibraryPanelUpload panelUpload;
    
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
        
        panelHome = new GuiGlobalLibraryPanelHome(this, 2, 136, width / 2 - 5, height - 141);
        panelList.add(panelHome);
        
        panelSearchResults = new GuiGlobalLibraryPanelSearchResults(this, 5, 5, 100, 100);
        panelList.add(panelSearchResults);
        
        panelSkinInfo = new GuiGlobalLibraryPanelSkinInfo(this, 5, 5, 100, 100);
        panelList.add(panelSkinInfo);
        
        panelLogin = new GuiGlobalLibraryPanelLogin(this, 5, 5, 500, 500);
        panelList.add(panelLogin);
        
        panelUpload = new GuiGlobalLibraryPanelUpload(this, 5, 5, 100, 100);
        panelList.add(panelUpload);
        
        screen = Screen.HOME;
        isNEIVisible = ModAddonManager.addonNEI.isVisible();
    }
    
    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();
        buttonList.clear();
        setupPanels();
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).initGui();
        }
        if (screen == Screen.HOME) {
            ((GuiGlobalLibraryPanelHome)panelHome).updateSkinPanels();
        }
    }
    
    public void setPlayerSlotVisibility(boolean visible) {
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(visible);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setVisible(visible);
                }
            }
        }
    }
    
    public void setPlayerSlotLocation(int xPos, int yPos) {
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(xPos + x * 18, yPos + 58);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(xPos + x * 18, yPos + y * 18);
                }
            }
        }
    }
    
    private void setupPanels() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).setVisible(false);
        }
        setPlayerSlotVisibility(false);
        int yOffset = PADDING;
        panelHeader.setPosition(PADDING, PADDING).setSize(width - PADDING * 2, 26);
        panelHeader.setVisible(true);
        yOffset += PADDING + 26;
        int neiBump = 0;
        if (isNEIVisible) {
            neiBump = 18;
        }
        
        switch (screen) {
        case HOME:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelHome.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING - neiBump);
            panelHome.setVisible(true);
            break;
        case SEARCH:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelSearchResults.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING - neiBump);
            panelSearchResults.setVisible(true);
            break;
        case SKIN_INFO:
            panelSearchBox.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += PADDING + 23;
            panelSkinInfo.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING - neiBump);
            panelSkinInfo.setVisible(true);
            break;
        case LOGON:
            panelLogin.setPosition(PADDING, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING);
            panelLogin.setVisible(true);
            break;
        case UPLOAD:
            panelUpload.setPosition(5, yOffset).setSize(width - PADDING * 2, height - yOffset - PADDING - neiBump);
            panelUpload.setVisible(true);
            setPlayerSlotVisibility(true);
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
        checkNEIVisibility();
    }
    
    private void checkNEIVisibility() {
        if (isNEIVisible != ModAddonManager.addonNEI.isVisible()) {
            isNEIVisible = !isNEIVisible;
            initGui();
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
