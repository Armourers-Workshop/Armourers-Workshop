package riskyken.armourersWorkshop.utils;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
}
