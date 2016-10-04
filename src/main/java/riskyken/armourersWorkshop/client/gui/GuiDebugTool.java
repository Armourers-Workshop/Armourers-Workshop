package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;

public class GuiDebugTool extends GuiScreen {
    
    protected final int guiWidth;
    protected final int guiHeight;
    
    protected int guiLeft;
    protected int guiTop;
    
    private GuiCheckBox checkWireframe;
    
    public GuiDebugTool() {
        this.guiWidth = 128;
        this.guiHeight = 128;
    }
    
    @Override
    public void initGui() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        buttonList.clear();
        
        checkWireframe = new GuiCheckBox(-1, guiLeft + 5, guiTop + 5, "wire frame", ConfigHandlerClient.wireframeRender);
        checkWireframe.setTextColour(0xFFEEEEEE);
        
        buttonList.add(checkWireframe);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkWireframe) {
            ConfigHandlerClient.wireframeRender = checkWireframe.isChecked();
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        drawGradientRect(this.guiLeft, this.guiTop, this.guiLeft + this.guiWidth, this.guiTop + guiHeight, 0xC0101010, 0xD0101010);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
