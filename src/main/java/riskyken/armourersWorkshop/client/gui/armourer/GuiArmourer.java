package riskyken.armourersWorkshop.client.gui.armourer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabbed;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiTabbed {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.ARMOURER);
    
    public TileEntityArmourer tileEntity;
    private final String inventoryName;
    
    public GuiTabArmourerMain tabArmourerMain;
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(new ContainerArmourer(invPlayer, tileEntity), false);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getInventoryName();
        
        tabArmourerMain = new GuiTabArmourerMain(0, this);
        
        tabList.add(tabArmourerMain);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.main")).setIconLocation(0, 52));
        tabController.setActiveTabIndex(activeTab);
        
        tabChanged();
    }
    
    @Override
    public void initGui() {
        this.xSize = 256;
        this.ySize = 256;
        super.initGui();
        buttonList.clear();
        buttonList.add(tabController);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
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
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawForegroundLayer(mouseX, mouseY);
            }
        }
    }
}
