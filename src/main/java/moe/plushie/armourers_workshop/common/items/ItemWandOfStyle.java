package moe.plushie.armourers_workshop.common.items;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.skin.entity.EntitySkinHandler;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemWandOfStyle extends AbstractModItem {

    public ItemWandOfStyle() {
        super(LibItemNames.WAND_OF_STYLE);
        setSortPriority(8);
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity((EntityLivingBase) entity, player)) {
                if (entity.getEntityWorld().isRemote) {
                    return true;
                }
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.WARDROBE_ENTITY, entity.getEntityWorld(), entity.getEntityId(), 0, 0);
                return true;
            } else {
                if (entity.getEntityWorld().isRemote) {
                    ModLogger.log(String.format("Entity \"%s\" is not supported with the wand of style.", entity.getClass().getName()));
                    ModLogger.log("Please ask the mod author if you would like support added for this entity.");
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (EntitySkinHandler.INSTANCE.canUseWandOfStyleOnEntity(target, playerIn)) {
            if (target.getEntityWorld().isRemote) {
                return true;
            }
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.WARDROBE_ENTITY, target.getEntityWorld(), target.getEntityId(), 0, 0);
            return true;
        } else {
            if (target.getEntityWorld().isRemote) {
                ModLogger.log(String.format("Entity \"%s\" is not supported with the wand of style.", target.getClass().getName()));
                ModLogger.log("Please ask the mod author if you would like support added for this entity.");
            }
        }
        return false;
    }
}
