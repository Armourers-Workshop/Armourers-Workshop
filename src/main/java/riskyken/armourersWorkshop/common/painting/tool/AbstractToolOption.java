package riskyken.armourersWorkshop.common.painting.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public abstract class AbstractToolOption {

    protected final String optionName;
    
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
    
    @SideOnly(Side.CLIENT)
    public abstract int getDisplayWidth();
    
    @SideOnly(Side.CLIENT)
    public abstract int getDisplayHeight();
    
    @SideOnly(Side.CLIENT)
    public abstract GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound);
    
    public abstract Object readFromNBT(NBTTagCompound compound);
    
    public abstract Object readFromNBT(NBTTagCompound compound, Object value);
    
    public abstract void writeToNBT(NBTTagCompound compound, GuiButton control);
    
    public abstract void writeToNBT(NBTTagCompound compound, Object value);
}
