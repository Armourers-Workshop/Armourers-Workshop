package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;

@SideOnly(Side.CLIENT)
public class GuiDebugTool extends GuiScreen {
    
    protected final int guiWidth;
    protected final int guiHeight;
    
    protected int guiLeft;
    protected int guiTop;
    
    private GuiCheckBox checkWireframe;
    private GuiCheckBox checkArmourerDebugRenders;
    private GuiCheckBox checkShowLodLevel;
    private GuiCheckBox checkShowSkinBlockBounds;
    
    public GuiDebugTool() {
        this.guiWidth = 180;
        this.guiHeight = 128;
    }
    
    @Override
    public void initGui() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
        
        checkWireframe = new GuiCheckBox(-1, guiLeft + 5, guiTop + 5, "wire frame", ConfigHandlerClient.wireframeRender);
        checkWireframe.setTextColour(0xFFEEEEEE);
        
        checkArmourerDebugRenders = new GuiCheckBox(-1, guiLeft + 5, guiTop + 15, "armourer debug renders", ConfigHandlerClient.showArmourerDebugRender);
        checkArmourerDebugRenders.setTextColour(0xFFEEEEEE);
        
        checkShowLodLevel = new GuiCheckBox(-1, guiLeft + 5, guiTop + 25, "show lod levels", ConfigHandlerClient.showLodLevels);
        checkShowLodLevel.setTextColour(0xFFEEEEEE);
        
        checkShowSkinBlockBounds = new GuiCheckBox(-1, guiLeft + 5, guiTop + 35, "show skin block bounds", ConfigHandlerClient.showSkinBlockBounds);
        checkShowSkinBlockBounds.setTextColour(0xFFEEEEEE);
        
        
        buttonList.add(checkWireframe);
        buttonList.add(checkArmourerDebugRenders);
        buttonList.add(checkShowLodLevel);
        buttonList.add(checkShowSkinBlockBounds);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkWireframe) {
            ConfigHandlerClient.wireframeRender = checkWireframe.isChecked();
        }
        if (button == checkArmourerDebugRenders) {
            ConfigHandlerClient.showArmourerDebugRender = checkArmourerDebugRenders.isChecked();
        }
        if (button == checkShowLodLevel) {
            ConfigHandlerClient.showLodLevels = checkShowLodLevel.isChecked();
        }
        if (button == checkShowSkinBlockBounds) {
            ConfigHandlerClient.showSkinBlockBounds = checkShowSkinBlockBounds.isChecked();
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + guiHeight, 0xC0101010, 0xD0101010);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
