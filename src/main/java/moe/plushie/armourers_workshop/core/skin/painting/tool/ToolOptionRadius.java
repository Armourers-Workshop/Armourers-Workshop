package moe.plushie.armourers_workshop.core.skin.painting.tool;//package moe.plushie.armourers_workshop.core.skin.painting.tool;
//
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fml.client.config.GuiSlider;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//public class ToolOptionRadius extends ToolOption<Integer> {
//
//    public ToolOptionRadius(String key, Integer defaultValue) {
//        super(key, defaultValue);
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public int getDisplayWidth() {
//        return 150;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public int getDisplayHeight() {
//        return 20;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound) {
//        GuiSlider sliderControl = new GuiSlider(id, x, y, getLocalisedLabel() + " ", 1, 6, (Integer) readFromNBT(compound, defaultValue), null);
//        sliderControl.showDecimal = false;
//        return sliderControl;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void writeGuiControlToNBT(GuiButton button, NBTTagCompound compound) {
//        writeToNBT(compound, ((GuiSlider)button).getValueInt());
//    }
//}
