package riskyken.armourersWorkshop.common.painting.tool;

import cpw.mods.fml.client.config.GuiSlider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class ToolOptionRadius extends AbstractToolOption {
    
    private static final String TAG_RADIUS = "radius";

    public ToolOptionRadius() {
        super(TAG_RADIUS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getDisplayHeight() {
        return 20;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound) {
        GuiSlider sliderControl = new GuiSlider(id, x, y, getLocalisedLabel() + " ", 2, 6, (Integer) readFromNBT(compound), null);
        sliderControl.showDecimal = false;
        return sliderControl;
    }

    @Override
    public Object readFromNBT(NBTTagCompound compound) {
        int intensityValue = 2;
        if (compound.hasKey(TAG_RADIUS)) {
            intensityValue = compound.getInteger(TAG_RADIUS);
        }
        return intensityValue;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, GuiButton control) {
        GuiSlider sliderControl = (GuiSlider) control;
        writeToNBT(compound, sliderControl.getValueInt());
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, Object value) {
        compound.setInteger(TAG_RADIUS, (Integer) value);
    }
}
