package moe.plushie.armourers_workshop.common.painting.tool;

import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ToolOptionCheck extends ToolOption<Boolean> {
    
    public ToolOptionCheck(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getDisplayWidth() {
        return 180;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getDisplayHeight() {
        return 9;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound) {
        return new GuiCheckBox(id, x, y, getLocalisedLabel(), (boolean) readFromNBT(compound, defaultValue));
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void writeGuiControlToNBT(GuiButton button, NBTTagCompound compound) {
        writeToNBT(compound, ((GuiCheckBox)button).isChecked());
    }
}
