package moe.plushie.armourers_workshop.client.gui.armourer;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerBlockUtils;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerDisplaySettings;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerSkinSettings;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTab;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabbed;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiControl.IScreenSize;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
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
public class GuiArmourer extends GuiTabbed<ContainerArmourer> implements IDialogCallback, IScreenSize {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.GUI_ARMOURER);
    
    public final TileEntityArmourer tileEntity;
    private final String inventoryName;
    
    public GuiTabArmourerMain tabMain;
    public GuiTabArmourerDisplaySettings tabDisplaySettings;
    public GuiTabArmourerSkinSettings tabSkinSettings;
    public GuiTabArmourerBlockUtils tabBlockUtils;
    private static int activeTab;
    
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(new ContainerArmourer(invPlayer, tileEntity), false, TEXTURE_TAB_ICONS);
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
        
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.main")).setIconLocation(0, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.displaySettings")).setIconLocation(16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.skinSettings")).setIconLocation(32, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.blockUtils")).setIconLocation(48, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3).setAnimation(8, 150));
        
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
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
        GlStateManager.enableDepth();
    }

    @Override
    protected int getActiveTab() {
        return activeTab;
    }

    @Override
    protected void setActiveTab(int value) {
        activeTab = value;
    }

    @Override
    public String getName() {
        return tileEntity.getName();
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
        closeDialog();
    }

    @Override
    public Rectangle_I_2D getSize() {
        return new Rectangle_I_2D(guiLeft, guiTop, width, height);
    }
}
