package riskyken.armourersWorkshop.common.addons;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class AddonAquaTweaks extends ModAddon {

    public AddonAquaTweaks() {
        super("AquaTweaks", "Aqua Tweaks");
    }
    
    @Override
    public void init() {
        NBTTagCompound compound = new NBTTagCompound();
        
        compound.setString("modid", LibModInfo.ID);
        compound.setString("block", "block.mannequin");
        FMLInterModComms.sendMessage(getModId(), "registerAquaConnectable", compound);
        
        compound = new NBTTagCompound();
        compound.setString("modid", LibModInfo.ID);
        compound.setString("block", "block.doll");
        FMLInterModComms.sendMessage(getModId(), "registerAquaConnectable", compound);
        
        compound = new NBTTagCompound();
        compound.setString("modid", LibModInfo.ID);
        compound.setString("block", "block.miniArmourer");
        FMLInterModComms.sendMessage(getModId(), "registerAquaConnectable", compound);
        
        compound = new NBTTagCompound();
        compound.setString("modid", LibModInfo.ID);
        compound.setString("block", "block.skinnable");
        FMLInterModComms.sendMessage(getModId(), "registerAquaConnectable", compound);
    }
}
