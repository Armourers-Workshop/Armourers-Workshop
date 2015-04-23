package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWandOfStyle extends AbstractModItem {

    public ItemWandOfStyle() {
        super(LibItemNames.WAND_OF_STYLE);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.WAND_OF_STYLE);
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        if (EntitySkinHandler.INSTANCE.isValidEntity(entity)) {
            if (entity.worldObj.isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.ENTITY_SKIN_INVENTORY, entity.worldObj, entity.getEntityId(), 0, 0);
        }
        return false;
    }
}
