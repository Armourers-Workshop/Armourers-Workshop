package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.plushieWrapper.common.entity.EntityLivingBasePointer;
import riskyken.plushieWrapper.common.entity.EntityPlayerPointer;
import riskyken.plushieWrapper.common.item.ItemStackPointer;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWandOfStyle extends AbstractModItemNew {

    public ItemWandOfStyle() {
        super(LibItemNames.WAND_OF_STYLE);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
        iconList.add(LibItemResources.WAND_OF_STYLE);
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStackPointer itemStackPointer,
            EntityPlayerPointer player,
            EntityLivingBasePointer entity) {
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(entity.getEntityLivingBase())) {
            if (entity.getEntityLivingBase().worldObj.isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(player.getEntityPlayer(), ArmourersWorkshop.instance,
                    LibGuiIds.ENTITY_SKIN_INVENTORY, entity.getEntityLivingBase().worldObj,
                    entity.getEntityLivingBase().getEntityId(), 0, 0);
        }
        return false;
    }
}
