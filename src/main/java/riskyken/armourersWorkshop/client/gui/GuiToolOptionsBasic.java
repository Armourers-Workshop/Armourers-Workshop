package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.client.config.GuiSlider.ISlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.utils.UtilItems;

@SideOnly(Side.CLIENT)
public class GuiToolOptionsBasic extends GuiToolOptions implements ISlider {

    private GuiSlider slider;
    
    public GuiToolOptionsBasic(ItemStack stack) {
        super(stack);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        slider = new GuiSlider(0, this.guiLeft + 12, this.guiTop + 25, "Intensity ", 1, 64, UtilItems.getIntensityFromStack(stack, 16), this);
        slider.showDecimal = false;
        buttonList.add(slider);
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        //PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate((byte)0, slider.getValueInt()));
    }
}
