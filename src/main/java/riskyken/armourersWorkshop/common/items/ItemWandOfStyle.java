package riskyken.armourersWorkshop.common.items;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.utils.ModLogger;

public class ItemWandOfStyle extends AbstractModItem {

    public ItemWandOfStyle() {
        super(LibItemNames.WAND_OF_STYLE);
        setSortPriority(8);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(LibItemResources.WAND_OF_STYLE);
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        ModLogger.log(entity.getClass());
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(entity)) {
            if (entity.worldObj.isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, entity.worldObj,
                    entity.getEntityId(), 0, 0);
        }
        
        return true;
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer entityPlayer, EntityLivingBase entityLivingBase) {
        ModLogger.log(entityLivingBase.getClass());
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(entityLivingBase)) {
            if (entityLivingBase.worldObj.isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(entityPlayer, ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, entityLivingBase.worldObj,
                    entityLivingBase.getEntityId(), 0, 0);
        }
        
        return false;
    }
}
