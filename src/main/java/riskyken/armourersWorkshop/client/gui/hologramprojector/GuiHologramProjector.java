package riskyken.armourersWorkshop.client.gui.hologramprojector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabbed;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerHologramProjector;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;

@SideOnly(Side.CLIENT)
public class GuiHologramProjector extends GuiTabbed {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.HOLOGRAM_PROJECTOR);
    private static final ResourceLocation textureTabs = new ResourceLocation(LibGuiResources.HOLOGRAM_PROJECTOR_TABS);
    
    private static final String DEGREE  = "\u00b0";
    
    private final TileEntityHologramProjector tileEntity;
    private final String inventoryName;
    
    public GuiHologramProjectorTabInventory tabInventory;
    public GuiHologramProjectorTabOffset tabOffset;
    public GuiHologramProjectorTabRotationOffset tabRotationOffset;
    public GuiHologramProjectorTabRotationSpeed tabRotationSpeed;
    
    private boolean loadingGui;
    
    public GuiHologramProjector(InventoryPlayer invPlayer, TileEntityHologramProjector tileEntity) {
        super(new ContainerHologramProjector(invPlayer, tileEntity), true, textureTabs);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getInventoryName();
        
        tabInventory = new GuiHologramProjectorTabInventory(0, this);
        tabOffset = new GuiHologramProjectorTabOffset(1, this, inventoryName, tileEntity);
        tabRotationOffset = new GuiHologramProjectorTabRotationOffset(2, this, inventoryName, tileEntity);
        tabRotationSpeed = new GuiHologramProjectorTabRotationSpeed(3, this, inventoryName, tileEntity);
        
        tabList.add(tabInventory);
        tabList.add(tabOffset);
        tabList.add(tabRotationOffset);
        tabList.add(tabRotationSpeed);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.inventory")).setIconLocation(0, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.offset")).setIconLocation(32, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.rotationOffset")).setIconLocation(32, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.rotationSpeed")).setIconLocation(32, 52));
        tabController.setActiveTabIndex(activeTab);
        
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
    public void drawDefaultBackground() {}

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(texture);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawBackgroundLayer(partialTickTime, mouseX, mouseY);
            }
        }
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName());
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawForegroundLayer(mouseX, mouseY);
            }
        }
    }
}
