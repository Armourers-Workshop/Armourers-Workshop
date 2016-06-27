package riskyken.armourersWorkshop.common.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;

public class ItemWandOfStyle extends AbstractModItem {

    public ItemWandOfStyle() {
        super(LibItemNames.WAND_OF_STYLE);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target,
            EnumHand hand) {
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(target)) {
            if (target.worldObj.isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, target.worldObj,
                    target.getEntityId(), 0, 0);
        }
        return false;
    }
}
