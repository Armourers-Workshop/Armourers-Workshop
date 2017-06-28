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
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

@SideOnly(Side.CLIENT)
public class GuiArmourer extends GuiTabbed {

    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.ARMOURER);
    private static final ResourceLocation textureTabs = new ResourceLocation(LibGuiResources.ARMOURER_TABS);
    
    public final TileEntityArmourer tileEntity;
    private final String inventoryName;
    
    public GuiTabArmourerMain tabMain;
    public GuiTabArmourerDisplaySettings tabDisplaySettings;
    public GuiTabArmourerSkinSettings tabSkinSettings;
    public GuiTabArmourerBlockUtils tabBlockUtils;
    
    
    public GuiArmourer(InventoryPlayer invPlayer, TileEntityArmourer tileEntity) {
        super(new ContainerArmourer(invPlayer, tileEntity), false, textureTabs);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getInventoryName();
        
        tabMain = new GuiTabArmourerMain(0, this);
        tabDisplaySettings = new GuiTabArmourerDisplaySettings(1, this);
        tabSkinSettings = new GuiTabArmourerSkinSettings(2, this);
        tabBlockUtils = new GuiTabArmourerBlockUtils(3, this);
        
        tabList.add(tabMain);
        tabList.add(tabDisplaySettings);
        tabList.add(tabSkinSettings);
        tabList.add(tabBlockUtils);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.main")).setIconLocation(52, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.displaySettings")).setIconLocation(52 + 16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.skinSettings")).setIconLocation(52 + 32, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.blockUtils")).setIconLocation(52 + 48, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        
        tabController.setActiveTabIndex(activeTab);
        
        tabChanged();
    }
    
    @Override
    public void initGui() {
        this.xSize = 176;
        this.ySize = 224;
        super.initGui();
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
        }
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
        GuiHelper.renderLocalizedGuiName(fontRendererObj, this.xSize, tileEntity.getInventoryName());
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawForegroundLayer(mouseX, mouseY);
            }
        }
    }
}
