package riskyken.armourersWorkshop.common.painting.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;

public class ToolOptionCheck extends AbstractToolOption {

    private final boolean defaultCheck;
    
    public ToolOptionCheck(String optionName) {
        this(optionName, true);
    }
    
    public ToolOptionCheck(String optionName, boolean defaultCheck) {
        super(optionName);
        this.defaultCheck = defaultCheck;
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
        return new GuiCheckBox(id, x, y, getLocalisedLabel(), (Boolean) readFromNBT(compound));
    }
    
    public boolean readFromNBTBool(NBTTagCompound compound) {
        boolean checked = defaultCheck;
        if (compound != null && compound.hasKey(optionName)) {
            checked = compound.getBoolean(optionName);
        }
        return checked;
    }
    
    @Override
    public Object readFromNBT(NBTTagCompound compound) {
        return readFromNBT(compound, defaultCheck);
    }
    
    @Override
    public Object readFromNBT(NBTTagCompound compound, Object value) {
        boolean checked = (Boolean) value;
        if (compound != null && compound.hasKey(optionName)) {
            checked = compound.getBoolean(optionName);
        }
        return checked;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound, GuiButton control) {
        GuiCheckBox checkControl = (GuiCheckBox) control;
        writeToNBT(compound, checkControl.isChecked());
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, Object value) {
        compound.setBoolean(optionName, (Boolean) value);
    }
}
