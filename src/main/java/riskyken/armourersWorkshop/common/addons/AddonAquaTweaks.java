package riskyken.armourersWorkshop.common.addons;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.EventState;
import cpw.mods.fml.common.event.FMLInterModComms;

public class AddonAquaTweaks extends AbstractAddon {

    @Override
    public void preInit() {
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

    @Override
    public void postInit() {
    }
    
    @Override
    public String getModId() {
        return "AquaTweaks";
    }

    @Override
    public String getModName() {
        return "Aqua Tweaks";
    }

    @Override
    public void onWeaponRender(ItemRenderType type, EventState state) {
    }
}
