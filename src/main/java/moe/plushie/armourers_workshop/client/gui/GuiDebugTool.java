package moe.plushie.armourers_workshop.client.gui;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.render.SkinItemRenderHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    private GuiCheckBox checkShowSkinRenderBounds;
    private GuiCheckBox checkDebugItemRenders;
    private GuiCheckBox checkSortOrderTooltips;
    
    public GuiDebugTool() {
        this.guiWidth = 180;
        this.guiHeight = 138;
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
        
        checkShowSkinRenderBounds = new GuiCheckBox(-1, guiLeft + 5, guiTop + 45, "show skin render bounds", ConfigHandlerClient.showSkinRenderBounds);
        checkShowSkinRenderBounds.setTextColour(0xFFEEEEEE);
        
        checkDebugItemRenders = new GuiCheckBox(-1, guiLeft + 5, guiTop + 55, "show item debug renders", SkinItemRenderHelper.debugShowFullBounds);
        checkDebugItemRenders.setTextColour(0xFFEEEEEE);
        
        checkSortOrderTooltips = new GuiCheckBox(-1, guiLeft + 5, guiTop + 65, "show sort order tooltip", ConfigHandlerClient.showSortOrderToolTip);
        checkSortOrderTooltips.setTextColour(0xFFEEEEEE);
        
        buttonList.add(checkWireframe);
        buttonList.add(checkArmourerDebugRenders);
        buttonList.add(checkShowLodLevel);
        buttonList.add(checkShowSkinBlockBounds);
        buttonList.add(checkShowSkinRenderBounds);
        buttonList.add(checkDebugItemRenders);
        buttonList.add(checkSortOrderTooltips);
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
        if (button == checkShowSkinRenderBounds) {
            ConfigHandlerClient.showSkinRenderBounds = checkShowSkinRenderBounds.isChecked();
        }
        if (button == checkDebugItemRenders) {
            SkinItemRenderHelper.debugShowFullBounds = checkDebugItemRenders.isChecked();
            SkinItemRenderHelper.debugShowPartBounds = checkDebugItemRenders.isChecked();
            SkinItemRenderHelper.debugShowTextureBounds = checkDebugItemRenders.isChecked();
            SkinItemRenderHelper.debugShowTargetBounds = checkDebugItemRenders.isChecked();
        }
        if (button == checkSortOrderTooltips) {
            ConfigHandlerClient.showSortOrderToolTip = checkSortOrderTooltips.isChecked();
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + guiHeight, 0xC0101010, 0xD0101010);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
