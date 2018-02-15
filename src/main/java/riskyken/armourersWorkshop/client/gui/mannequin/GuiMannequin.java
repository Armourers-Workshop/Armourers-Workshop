package riskyken.armourersWorkshop.client.gui.mannequin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabbed;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

@SideOnly(Side.CLIENT)
public class GuiMannequin extends GuiTabbed {
    
    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.MANNEQUIN);
    private static final ResourceLocation textureTabs = new ResourceLocation(LibGuiResources.MANNEQUIN_TABS);
    
    public final TileEntityMannequin tileEntity;
    private final String inventoryName;
    
    public GuiMannequinTabRotations tabRotations;
    public GuiMannequinTabInventory tabInventory;
    public GuiMannequinTabOffset tabOffset;
    public GuiMannequinTabSkinHair tabSkinAndHair;
    public GuiMannequinTabTexture tabTexture;
    public GuiMannequinTabExtraRenders tabExtraRenders;
    
    public GuiMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(new ContainerMannequin(invPlayer, tileEntity), true, textureTabs);
        this.tileEntity = tileEntity;
        this.inventoryName = tileEntity.getInventoryName();
        
        tabInventory = new GuiMannequinTabInventory(0, this, tileEntity);
        tabRotations = new GuiMannequinTabRotations(1, this, inventoryName, tileEntity.getBipedRotations());
        tabOffset = new GuiMannequinTabOffset(2, this, inventoryName, tileEntity);
        tabSkinAndHair = new GuiMannequinTabSkinHair(3, this, tileEntity);
        tabTexture = new GuiMannequinTabTexture(4, this, tileEntity);
        tabExtraRenders = new GuiMannequinTabExtraRenders(5, this, inventoryName, tileEntity);
        
        tabList.add(tabInventory);
        tabList.add(tabRotations);
        tabList.add(tabOffset);
        tabList.add(tabSkinAndHair);
        tabList.add(tabTexture);
        tabList.add(tabExtraRenders);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.inventory")).setIconLocation(78, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.rotations")).setIconLocation(94, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.offset")).setIconLocation(110, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.skinAndHair")).setIconLocation(126, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.name")).setIconLocation(142, 0).setAnimation(8, 150));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.extraRenders")).setIconLocation(158, 0).setAnimation(8, 150));
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String append = null;
        if (tileEntity.getGameProfile() != null) {
            append = tileEntity.getGameProfile().getName();
        }
        if (tileEntity.getIsDoll()) {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "doll", append, 4210752);
        } else {
            GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName(), append, 4210752);
        }
        
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawForegroundLayer(mouseX, mouseY);
            }
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
}
