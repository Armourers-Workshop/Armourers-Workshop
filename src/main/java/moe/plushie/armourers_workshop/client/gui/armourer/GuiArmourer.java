package moe.plushie.armourers_workshop.client.gui.armourer;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.AbstractGuiDialog.DialogResult;
import moe.plushie.armourers_workshop.client.gui.AbstractGuiDialog.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerBlockUtils;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerDisplaySettings;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerSkinSettings;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabbed;
import moe.plushie.armourers_workshop.client.gui.newgui.GuiTab;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerArmourer;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiTabbed implements IDialogCallback {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.ARMOURER);
    private static final ResourceLocation textureTabs = new ResourceLocation(LibGuiResources.ARMOURER_TABS);
    
    public final TileEntityArmourer tileEntity;
    private final String inventoryName;
    
    protected AbstractGuiDialog dialog;
    int oldMouseX;
    int oldMouseY;
    
    public GuiTabArmourerMain tabMain;
    public GuiTabArmourerDisplaySettings tabDisplaySettings;
    public GuiTabArmourerSkinSettings tabSkinSettings;
    public GuiTabArmourerBlockUtils tabBlockUtils;
    
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(new ContainerArmourer(invPlayer, tileEntity), false, textureTabs);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getName();
        
        tabMain = new GuiTabArmourerMain(0, this);
        tabDisplaySettings = new GuiTabArmourerDisplaySettings(1, this);
        tabSkinSettings = new GuiTabArmourerSkinSettings(2, this);
        tabBlockUtils = new GuiTabArmourerBlockUtils(3, this);
        
        tabList.add(tabMain);
        tabList.add(tabDisplaySettings);
        tabList.add(tabSkinSettings);
        tabList.add(tabBlockUtils);
        
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.main")).setIconLocation(52, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.displaySettings")).setIconLocation(52 + 16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.skinSettings")).setIconLocation(52 + 32, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.blockUtils")).setIconLocation(52 + 48, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        
        tabController.setActiveTabIndex(getActiveTab());
        
        tabChanged();
        skinTypeUpdate(tileEntity.getSkinType());
    }
    
    private void setSlotVisibility(boolean visible) {
        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Object slot = inventorySlots.inventorySlots.get(i);
            if (slot != null && slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(visible);
            }
        }
    }
    
    @Override
    protected void tabChanged() {
        super.tabChanged();
        setSlotVisibility(getActiveTab() == 0);
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 224;
        super.initGui();
        if (isDialogOpen()) {
            dialog.initGui();
        }
        buttonList.clear();
        buttonList.add(tabController);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        if (tileEntity.loadedArmourItem) {
            // got new settings from the server, update the tabs
            tileEntity.loadedArmourItem = false;
            SkinProperties skinProperties = tileEntity.getSkinProps();
            tabMain.resetValues(skinProperties);
            tabSkinSettings.resetValues(skinProperties);
            skinTypeUpdate(tileEntity.getSkinType());
        }
    }
    
    public void skinTypeUpdate(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinBow | skinType == SkinTypeRegistry.skinSword) {
            tabController.getTab(2).setVisable(false);
        } else {
            tabController.getTab(2).setVisable(true);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        if (isDialogOpen()) {
            mouseX = mouseY = 0;
        }
        super.drawScreen(mouseX, mouseY, partialTickTime);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(texture);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawBackgroundLayer(partialTickTime, mouseX, mouseY);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.disableDepth();
        GuiHelper.renderLocalizedGuiName(fontRenderer, this.xSize, tileEntity.getName());
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawForegroundLayer(mouseX, mouseY, 0);
            }
        }
        if (isDialogOpen()) {
            GL11.glTranslatef(-guiLeft, -guiTop, 0);
            dialog.draw(oldMouseX, oldMouseY, 0);
            GL11.glTranslatef(guiLeft, guiTop, 0);
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
        GlStateManager.enableDepth();
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (isDialogOpen()) {
            dialog.mouseClicked(mouseX, mouseY, button);
        } else {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick) {
        if (isDialogOpen()) {
            dialog.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        } else {
            super.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isDialogOpen()) {
            dialog.mouseMovedOrUp(mouseX, mouseY, state);
        } else {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) throws IOException {
        if (isDialogOpen()) {
            dialog.keyTyped(c, keycode);
        } else {
            super.keyTyped(c, keycode);
        }
    }
    
    public void openDialog(AbstractGuiDialog dialog) {
        this.dialog = dialog;
        dialog.initGui();
    }
    
    protected boolean isDialogOpen() {
        return dialog != null;
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                if (tab instanceof IDialogCallback) {
                    ((IDialogCallback)tab).dialogResult(dialog, result);
                }
            }
        }
        this.dialog = null;
    }
}
