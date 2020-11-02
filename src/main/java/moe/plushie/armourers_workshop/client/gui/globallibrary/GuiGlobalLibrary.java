package moe.plushie.armourers_workshop.client.gui.globallibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelHeader;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelHome;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelInfo;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelJoin;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelModeration;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelProfile;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchBox;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSearchResults;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSkinEdit;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelSkinInfo;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelUpload;
import moe.plushie.armourers_workshop.client.gui.globallibrary.panels.GuiGlobalLibraryPanelUserSkins;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.inventory.ContainerGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibrary extends ModGuiContainer<ContainerGlobalSkinLibrary> {

    public final TileEntityGlobalSkinLibrary tileEntity;
    public final EntityPlayer player;
    public ArrayList<GuiPanel> panelList;
    private int oldMouseX;
    private int oldMouseY;

    public Executor jsonDownloadExecutor = Executors.newFixedThreadPool(2);
    public Executor uploadExecutor = Executors.newFixedThreadPool(1);

    private static final int PADDING = 5;
    private boolean isNEIVisible;

    public GuiGlobalLibraryPanelHeader panelHeader;
    public GuiGlobalLibraryPanelSearchBox panelSearchBox;
    public GuiGlobalLibraryPanelHome panelHome;
    public GuiGlobalLibraryPanelSearchResults panelSearchResults;
    public GuiGlobalLibraryPanelSkinInfo panelSkinInfo;
    public GuiGlobalLibraryPanelUpload panelUpload;
    public GuiGlobalLibraryPanelJoin panelJoinBeta;
    public GuiGlobalLibraryPanelUserSkins panelUserSkins;
    public GuiGlobalLibraryPanelSkinEdit panelSkinEdit;
    public GuiGlobalLibraryPanelInfo panelInfo;
    public GuiGlobalLibraryPanelModeration panelModeration;
    public GuiGlobalLibraryPanelProfile panelProfile;

    private Screen screen;

    public static enum Screen {
        HOME, SEARCH, UPLOAD, SKIN_INFO, USER_SKINS, FAVOURITES, JOIN, SKIN_EDIT, INFO, MODERATION, PROFILE
    }

    public GuiGlobalLibrary(TileEntityGlobalSkinLibrary tileEntity, InventoryPlayer inventoryPlayer) {
        super(new ContainerGlobalSkinLibrary(inventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.player = Minecraft.getMinecraft().player;
        this.panelList = new ArrayList<GuiPanel>();

        panelHeader = new GuiGlobalLibraryPanelHeader(this, 0, 0, width, 26);
        panelList.add(panelHeader);

        panelSearchBox = new GuiGlobalLibraryPanelSearchBox(this, 0, 25, width, 23);
        panelList.add(panelSearchBox);

        panelHome = new GuiGlobalLibraryPanelHome(this, 2, 136, width / 2 - 5, height - 141);
        panelList.add(panelHome);

        panelSearchResults = new GuiGlobalLibraryPanelSearchResults(this, 5, 5, 100, 100);
        panelList.add(panelSearchResults);

        panelSkinInfo = new GuiGlobalLibraryPanelSkinInfo(this, 5, 5, 100, 100);
        panelList.add(panelSkinInfo);

        panelUpload = new GuiGlobalLibraryPanelUpload(this, 5, 5, 100, 100);
        panelList.add(panelUpload);

        panelJoinBeta = new GuiGlobalLibraryPanelJoin(this, 5, 5, 100, 100);
        panelList.add(panelJoinBeta);

        panelUserSkins = new GuiGlobalLibraryPanelUserSkins(this, 5, 5, 100, 100);
        panelList.add(panelUserSkins);

        panelSkinEdit = new GuiGlobalLibraryPanelSkinEdit(this, 5, 5, 100, 100);
        panelList.add(panelSkinEdit);

        panelInfo = new GuiGlobalLibraryPanelInfo(this);
        panelList.add(panelInfo);

        panelModeration = new GuiGlobalLibraryPanelModeration(this);
        panelList.add(panelModeration);

        panelProfile = new GuiGlobalLibraryPanelProfile(this);
        panelList.add(panelProfile);

        screen = Screen.HOME;
        isNEIVisible = ModAddonManager.addonNEI.isVisible();

        if (!PlushieAuth.startedRemoteUserCheck()) {
            PlushieAuth.doRemoteUserCheck();
        }
    }

    @Override
    public void initGui() {
        ScaledResolution reso = new ScaledResolution(mc);
        this.xSize = reso.getScaledWidth();
        this.ySize = reso.getScaledHeight();
        super.initGui();
        buttonList.clear();
        setupPanels();
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).initGui();
        }
        if (screen == Screen.HOME) {
            panelHome.updateSkinPanels();
        }
    }

    public void setSlotVisibility(boolean visible) {
        for (int x = 0; x < 9; x++) {
            Slot slot = inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable) slot).setVisible(visible);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable) slot).setVisible(visible);
                }
            }
        }

        Slot slot = getInputSlot();
        if (slot instanceof SlotHidable) {
            ((SlotHidable) slot).setVisible(visible);
        }
        slot = getOutputSlot();
        if (slot instanceof SlotHidable) {
            ((SlotHidable) slot).setVisible(visible);
        }
    }

    public void setPlayerSlotLocation(int xPos, int yPos) {
        for (int x = 0; x < 9; x++) {
            Slot slot = inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable) slot).setDisplayPosition(xPos + x * 18, yPos + 58);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable) slot).setDisplayPosition(xPos + x * 18, yPos + y * 18);
                }
            }
        }
    }

    public void setInputSlotLocation(int xPos, int yPos) {
        Slot slot = getInputSlot();
        if (slot instanceof SlotHidable) {
            ((SlotHidable) slot).setDisplayPosition(xPos, yPos);
        }
    }

    public void setOutputSlotLocation(int xPos, int yPos) {
        Slot slot = getOutputSlot();
        if (slot instanceof SlotHidable) {
            ((SlotHidable) slot).setDisplayPosition(xPos, yPos);
        }
    }

    public SlotHidable getInputSlot() {
        return (SlotHidable) inventorySlots.inventorySlots.get(36);
    }

    public SlotHidable getOutputSlot() {
        return (SlotHidable) inventorySlots.inventorySlots.get(37);
    }

    @Override
    public ContainerGlobalSkinLibrary getContainer() {
        return (ContainerGlobalSkinLibrary) inventorySlots;
    }

    private void setupPanels() {
        for (int i = 0; i < panelList.size(); i++) {
            panelList.get(i).setVisible(false);
        }
        setSlotVisibility(false);
        int yOffset = 1;
        panelHeader.setPosition(1, 1).setSize(width - 2, 26);
        panelHeader.setVisible(true);
        yOffset += panelHeader.getHeight() + 1;
        int neiBump = 0;
        if (isNEIVisible) {
            neiBump = 18;
        }
        if (ModAddonManager.addonJEI.isModLoaded()) {
            neiBump = 24;
        }

        switch (screen) {
        case HOME:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            panelHome.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelHome.setVisible(true);
            break;
        case SEARCH:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            panelSearchResults.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelSearchResults.setVisible(true);
            break;
        case SKIN_INFO:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            panelSkinInfo.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelSkinInfo.setVisible(true);
            break;
        case UPLOAD:
            panelUpload.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelUpload.setVisible(true);
            setSlotVisibility(true);
            break;
        case JOIN:
            panelJoinBeta.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelJoinBeta.setVisible(true);
            break;
        case USER_SKINS:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            panelUserSkins.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelUserSkins.setVisible(true);
            break;
        case SKIN_EDIT:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, 23);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            panelSkinEdit.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelSkinEdit.setVisible(true);
            break;
        case FAVOURITES:
            panelSearchBox.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelSearchBox.setVisible(true);
            yOffset += panelSearchBox.getHeight() + 1;
            break;
        case INFO:
            panelInfo.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelInfo.setVisible(true);
            break;
        case MODERATION:
            panelModeration.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelModeration.setVisible(true);
            break;
        case PROFILE:
            panelProfile.setPosition(1, yOffset).setSize(width - 2, height - yOffset - 1 - neiBump);
            panelProfile.setVisible(true);
            break;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        PlushieAuth.updateAccessToken();
        for (GuiPanel panel : panelList) {
            panel.update();
        }
    }

    public void switchScreen(Screen screen) {
        this.screen = screen;
        setupPanels();
        for (GuiPanel panel : panelList) {
            panel.initGui();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (!isDialogOpen()) {
            for (GuiPanel panel : panelList) {
                if (panel.mouseClicked(mouseX, mouseY, button)) {
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (!isDialogOpen()) {
            for (GuiPanel panel : panelList) {
                panel.mouseMovedOrUp(mouseX, mouseY, state);
            }
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        boolean keyTyped = false;
        if (!isDialogOpen()) {
            for (GuiPanel panel : panelList) {
                if (panel.keyTyped(c, keycode)) {
                    keyTyped = true;
                }
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
        GlStateManager.disableDepth();
        GlStateManager.pushAttrib();
        for (GuiPanel panel : panelList) {
            panel.drawBackground(mouseX, mouseY, partialTickTime);
        }
        GlStateManager.popAttrib();
        GlStateManager.pushAttrib();
        for (GuiPanel panel : panelList) {
            panel.draw(mouseX, mouseY, partialTickTime);
        }
        GlStateManager.popAttrib();
        GlStateManager.pushAttrib();
        for (GuiPanel panel : panelList) {
            panel.drawForeground(mouseX, mouseY, partialTickTime);
        }
        GlStateManager.popAttrib();
        GlStateManager.enableDepth();
    }

    public String getGuiName() {
        return getName();
    }

    public void gotSkinFromServer(Skin skin) {
        panelUpload.uploadSkin(skin);
    }

    @Override
    public String getName() {
        return LibBlockNames.GLOBAL_SKIN_LIBRARY;
    }
}
