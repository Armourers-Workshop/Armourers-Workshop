package riskyken.armourersWorkshop.common.painting.tool;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        return I18n.format(getUnlocalisedlabel());
    }
    
    @SideOnly(Side.CLIENT)
    public abstract int getDisplayWidth();
    
    @SideOnly(Side.CLIENT)
    public abstract int getDisplayHeight();
    
    @SideOnly(Side.CLIENT)
    public abstract GuiButton getGuiControl(int id, int x, int y, NBTTagCompound compound);
    
    public abstract Object readFromNBT(NBTTagCompound compound);
    
    public abstract void writeToNBT(NBTTagCompound compound, GuiButton control);
    
    public abstract void writeToNBT(NBTTagCompound compound, Object value);
}
