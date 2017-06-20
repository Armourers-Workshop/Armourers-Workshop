package riskyken.armourersWorkshop.client.gui.mannequin;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabController;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

@SideOnly(Side.CLIENT)
public class GuiMannequin extends GuiContainer {
    
    private static final ResourceLocation texture = new ResourceLocation(LibGuiResources.MANNEQUIN);
    
    public final TileEntityMannequin tileEntity;
    private final EntityPlayer player;
    private final String inventoryName;
    
    private boolean guiLoaded = false;
    private GuiTabController tabController;
    private static int activeTab = 0;
    
    private ArrayList<GuiTabPanel> tabList;
    public GuiMannequinTabRotations tabRotations;
    public GuiMannequinTabInventory tabInventory;
    public GuiMannequinTabOffset tabOffset;
    public GuiMannequinTabSkinHair tabSkinAndHair;
    public GuiMannequinTabTexture tabTexture;
    public GuiMannequinTabExtraRenders tabExtraRenders;
    
    public GuiMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(new ContainerMannequin(invPlayer, tileEntity));
        this.tileEntity = tileEntity;
        this.player = invPlayer.player;
        this.inventoryName = tileEntity.getInventoryName();
        tabList = new ArrayList<GuiTabPanel>();
        
        tabInventory = new GuiMannequinTabInventory(0, this);
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
    }
    
    @Override
    public void initGui() {
        this.xSize = this.width;
        this.ySize = this.height;
        super.initGui();
        buttonList.clear();
        guiLoaded = false;
        
        tabController = new GuiTabController(this);
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.inventory")).setIconLocation(0, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.rotations")).setIconLocation(16, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.offset")).setIconLocation(32, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.skinAndHair")).setIconLocation(48, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.name")).setIconLocation(64, 52));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(inventoryName, "tab.extraRenders")).setIconLocation(80, 52));
        tabController.setActiveTabIndex(activeTab);
        
        for (int i = 0; i < tabList.size(); i++) {
            tabList.get(i).initGui();
        }
        buttonList.add(tabController);
        
        tabChanged();
        guiLoaded = true;
    }
    
    private void tabChanged() {
        this.activeTab = tabController.getActiveTabIndex();
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            tab.tabChanged(activeTab);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.mouseClicked(mouseX, mouseY, button);
            }
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.mouseMovedOrUp(mouseX, mouseY, button);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == tabController) {
            tabChanged();
        }
    }
    
    @Override
    protected void keyTyped(char c, int keycode) {
        boolean keyTyped = false;
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                keyTyped = tab.keyTyped(c, keycode);
            }
        }
        if (!keyTyped) {
            super.keyTyped(c, keycode);
        }
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
