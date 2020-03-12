package moe.plushie.armourers_workshop.client.gui.hologramprojector;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTab;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabbed;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHologramProjector extends GuiTabbed<ContainerHologramProjector> {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.GUI_HOLOGRAM_PROJECTOR);

    private static final String DEGREE = "\u00b0";

    private static int activeTab;

    private final TileEntityHologramProjector tileEntity;
    private final String inventoryName;

    public GuiHologramProjectorTabInventory tabInventory;
    public GuiHologramProjectorTabOffset tabOffset;
    public GuiHologramProjectorTabAngle tabAngle;
    public GuiHologramProjectorTabRotationOffset tabRotationOffset;
    public GuiHologramProjectorTabRotationSpeed tabRotationSpeed;
    public GuiHologramProjectorTabExtra tabExtra;

    private boolean loadingGui;

    public GuiHologramProjector(InventoryPlayer invPlayer, TileEntityHologramProjector tileEntity) {
        super(new ContainerHologramProjector(invPlayer, tileEntity), true, TEXTURE_TAB_ICONS);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getName();
        tabController.setTabsPerSide(6);
        
        tabInventory = new GuiHologramProjectorTabInventory(0, this);
        tabOffset = new GuiHologramProjectorTabOffset(1, this, inventoryName, tileEntity);
        tabAngle = new GuiHologramProjectorTabAngle(2, this, inventoryName, tileEntity);
        tabRotationOffset = new GuiHologramProjectorTabRotationOffset(3, this, inventoryName, tileEntity);
        tabRotationSpeed = new GuiHologramProjectorTabRotationSpeed(4, this, inventoryName, tileEntity);
        tabExtra = new GuiHologramProjectorTabExtra(5, this, inventoryName, tileEntity);

        tabList.add(tabInventory);
        tabList.add(tabOffset);
        tabList.add(tabAngle);
        tabList.add(tabRotationOffset);
        tabList.add(tabRotationSpeed);
        tabList.add(tabExtra);

        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.inventory")).setIconLocation(64, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.offset")).setIconLocation(96, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.angle")).setIconLocation(176, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.rotationOffset")).setIconLocation(80, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.rotationSpeed")).setIconLocation(160, 0).setAnimation(4, 150));
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(inventoryName, "tab.extra")).setIconLocation(144, 0).setAnimation(8, 150));
        tabController.setActiveTabIndex(getActiveTab());

        tabChanged();
    }

    @Override
    public void initGui() {
        this.xSize = this.width;
        this.ySize = this.height;
        super.initGui();
        buttonList.clear();
        buttonList.add(tabController);
    }

    @Override
    public void drawDefaultBackground() {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
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
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntity.getName());
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
    }

    @Override
    protected int getActiveTab() {
        return activeTab;
    }

    @Override
    protected void setActiveTab(int value) {
        this.activeTab = value;
    }

    @Override
    public String getName() {
        return tileEntity.getName();
    }
}
