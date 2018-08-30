package riskyken.armourersWorkshop.common.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import riskyken.armourersWorkshop.ArmourersWorkshop;
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
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        ModLogger.log(entity.getClass());
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(entity)) {
            if (entity.getEntityWorld().isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, entity.getEntityWorld(),
                    entity.getEntityId(), 0, 0);
        }
        
        return true;
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        ModLogger.log(target.getClass());
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(target)) {
            if (target.getEntityWorld().isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, target.getEntityWorld(),
                    target.getEntityId(), 0, 0);
        }
        return false;
    }
}
