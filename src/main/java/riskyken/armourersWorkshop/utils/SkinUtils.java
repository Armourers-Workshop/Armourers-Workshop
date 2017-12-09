package riskyken.armourersWorkshop.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.wings.SkinWings.MovementType;

public final class SkinUtils {
    
    private SkinUtils() {
    }
    
    public static Skin getSkinDetectSide(ItemStack stack, boolean serverSoftLoad, boolean clientRequestSkin) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        return getSkinDetectSide(skinPointer, serverSoftLoad, clientRequestSkin);
    }
    
    public static Skin getSkinDetectSide(SkinPointer skinPointer, boolean serverSoftLoad, boolean clientRequestSkin) {
        if (skinPointer != null) {
            SkinIdentifier skinIdentifier = skinPointer.getIdentifier();
            return getSkinDetectSide(skinIdentifier, serverSoftLoad, clientRequestSkin);
        }
        return null;
    }
    
    public static Skin getSkinDetectSide(SkinIdentifier skinIdentifier, boolean serverSoftLoad, boolean clientRequestSkin) {
        if (skinIdentifier != null) {
            if (ArmourersWorkshop.isDedicated()) {
                return getSkinForSide(skinIdentifier, Side.SERVER, serverSoftLoad, clientRequestSkin);
            } else {
                Side side = FMLCommonHandler.instance().getEffectiveSide();
                return getSkinForSide(skinIdentifier, side, serverSoftLoad, clientRequestSkin);
            }
        }
        return null;
    }
    
    public static Skin getSkinForSide(SkinIdentifier skinIdentifier, Side side, boolean softLoad, boolean requestSkin) {
        if (side == Side.CLIENT) {
            return getSkinOnClient(skinIdentifier, requestSkin);
        } else {
            return getSkinOnServer(skinIdentifier, softLoad);
        }
    }
    
    private static Skin getSkinOnServer(SkinIdentifier skinIdentifier, boolean softLoad) {
        if (softLoad) {
            return CommonSkinCache.INSTANCE.softGetSkin(skinIdentifier);
        } else {
            return CommonSkinCache.INSTANCE.getSkin(skinIdentifier);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private static Skin getSkinOnClient(SkinIdentifier skinIdentifier, boolean requestSkin) {
        return ClientSkinCache.INSTANCE.getSkin(skinIdentifier, requestSkin);
    }
    
    public static Skin copySkin(Skin skin) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SkinIOUtils.saveSkinToStream(outputStream, skin);
        byte[] skinData = outputStream.toByteArray();
        Skin skinCopy = SkinIOUtils.loadSkinFromStream(new ByteArrayInputStream(skinData));
        return skinCopy;
    }
    
    public static double getFlapAngleForWings(Entity entity, Skin skin) {
        
        double maxAngle = SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skin.getProperties());
        double minAngle = SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skin.getProperties());
        double idleSpeed = SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skin.getProperties());
        double flyingSpeed = SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skin.getProperties());
        MovementType movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skin.getProperties()));
        
        double angle = 0;
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
            if (movmentType == MovementType.EASE) {
                angle = Math.sin(angle / flapTime * Math.PI * 2);
            }
            if (movmentType == MovementType.LINEAR) {
                angle = angle / flapTime;
            }
        }
        

        
        double fullAngle = maxAngle - minAngle;
        if (movmentType == MovementType.LINEAR) {
            return fullAngle * angle;
        }
        
        return -minAngle - fullAngle * ((angle + 1D) / 2);
    }
}
