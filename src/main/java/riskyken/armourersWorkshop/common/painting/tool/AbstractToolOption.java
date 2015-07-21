package riskyken.armourersWorkshop.common.painting.tool;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public abstract class AbstractToolOption {

    private final String optionName;
    
    public AbstractToolOption(String optionName) {
        this.optionName = optionName;
    }
    
    public String getOptionName() {
        return optionName;
    }
    
    public String getUnlocalisedlabel() {
        return "tooloption." + LibModInfo.ID.toLowerCase() + ":" + getOptionName();
    }
    
    public String getLocalisedLabel() {
        return StatCollector.translateToLocal(getUnlocalisedlabel());
    }
    
    public abstract int getDisplayWidth();
    
    public abstract int getDisplayHeight();
    
    public abstract GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound);
    
    public abstract Object readFromNBT(NBTTagCompound compound);
    
    public abstract void writeToNBT(NBTTagCompound compound, GuiButton control);
    
    public abstract void writeToNBT(NBTTagCompound compound, Object value);
}
