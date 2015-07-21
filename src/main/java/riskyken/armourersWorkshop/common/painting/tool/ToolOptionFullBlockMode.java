package riskyken.armourersWorkshop.common.painting.tool;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;

public class ToolOptionFullBlockMode extends AbstractToolOption {

    private static final String TAG_FULL_BLOCK_MODE = "fullBlockMode";
    
    public ToolOptionFullBlockMode() {
        super(TAG_FULL_BLOCK_MODE);
    }

    @Override
    public int getDisplayWidth() {
        return 180;
    }

    @Override
    public int getDisplayHeight() {
        return 9;
    }

    @Override
    public GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound) {
        return new GuiCheckBox(id, x, y, getLocalisedLabel(), (Boolean) readFromNBT(compound));
    }

    @Override
    public Object readFromNBT(NBTTagCompound compound) {
        boolean checked = true;
        if (compound != null && compound.hasKey(TAG_FULL_BLOCK_MODE)) {
            checked = compound.getBoolean(TAG_FULL_BLOCK_MODE);
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
        compound.setBoolean(TAG_FULL_BLOCK_MODE, (Boolean) value);
    }
}
