package riskyken.armourersWorkshop.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;

public final class SkinUtils {
    
    private SkinUtils() {
    }
    
    public static Skin getSkinDetectSide(ItemStack stack, boolean serverSoftLoad, boolean clientRequestSkin) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer != null) {
            return getSkinDetectSide(skinPointer, serverSoftLoad, clientRequestSkin);
        }
        return null;
    }
    
    public static Skin getSkinDetectSide(SkinPointer skinPointer, boolean serverSoftLoad, boolean clientRequestSkin) {
        if (ArmourersWorkshop.isDedicated()) {
            return getSkinForSide(skinPointer, Side.SERVER, serverSoftLoad, clientRequestSkin);
        } else {
            Side side = FMLCommonHandler.instance().getEffectiveSide();
            return getSkinForSide(skinPointer, side, serverSoftLoad, clientRequestSkin);
        }
    }
    
    public static Skin getSkinForSide(SkinPointer skinPointer, Side side, boolean softLoad, boolean requestSkin) {
        if (side == Side.CLIENT) {
            return getSkinOnClient(skinPointer, requestSkin);
        } else {
            return getSkinOnServer(skinPointer, softLoad);
        }
    }
    
    private static Skin getSkinOnServer(SkinPointer skinPointer, boolean softLoad) {
        if (softLoad) {
            return SkinDataCache.INSTANCE.softGetSkin(skinPointer.getSkinId());
        } else {
            return SkinDataCache.INSTANCE.getEquipmentData(skinPointer.getSkinId());
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static Skin getSkinOnClient(SkinPointer skinPointer, boolean requestSkin) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer, requestSkin);
    }
    
    public static double getFlapAngleForWings(Entity entity, Skin skin) {
        double maxAngle = 35D;
        
        double angle = 45D;
        double flapTime = 6000D;
        
        if (entity != null) {
            if (entity.isAirBorne) {
                if (entity instanceof EntityPlayer) {
                    if (((EntityPlayer)entity).capabilities.isFlying) {
                        flapTime = 350;
                    }
                } else {
                    flapTime = 350;
                }
            }
            angle = (((double)System.currentTimeMillis() + entity.getEntityId()) % flapTime);
        } else {
            //rotation = ((double)System.currentTimeMillis() / 10D % 100D);
        }
        angle = Math.sin(angle / flapTime * Math.PI * 2);
        return angle * maxAngle - maxAngle;
    }
}
