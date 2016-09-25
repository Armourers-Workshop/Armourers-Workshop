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
        
        double maxAngle = skin.getProperties().getPropertyDouble(Skin.KEY_WINGS_MAX_ANGLE, 35D);
        double minAngle = skin.getProperties().getPropertyDouble(Skin.KEY_WINGS_MIN_ANGLE, 0D);
        double idleSpeed = skin.getProperties().getPropertyDouble(Skin.KEY_WINGS_IDLE_SPEED, 6000D);
        double flyingSpeed = skin.getProperties().getPropertyDouble(Skin.KEY_WINGS_FLYING_SPEED, 350D);
        
        double angle = maxAngle;
        double flapTime = idleSpeed;
        
        if (entity != null) {
            if (entity.isAirBorne) {
                if (entity instanceof EntityPlayer) {
                    if (((EntityPlayer)entity).capabilities.isFlying) {
                        flapTime = flyingSpeed;
                    }
                } else {
                    flapTime = flyingSpeed;
                }
            }
            angle = (((double)System.currentTimeMillis() + entity.getEntityId()) % flapTime);
            angle = Math.sin(angle / flapTime * Math.PI * 2);
        }
        
        return angle * maxAngle - maxAngle;
    }
}
