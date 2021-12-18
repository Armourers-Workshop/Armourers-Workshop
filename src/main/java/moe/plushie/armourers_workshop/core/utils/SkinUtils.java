package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinIdentifier;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.type.SkinTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public final class SkinUtils {

    private SkinUtils() {
    }

    public static Skin getSkinDetectSide(ItemStack stack, boolean serverSoftLoad, boolean clientRequestSkin) {
//        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//        return getSkinDetectSide(skinPointer, serverSoftLoad, clientRequestSkin);
        return null;
    }

    public static Skin getSkinDetectSide(ISkinDescriptor descriptor, boolean serverSoftLoad, boolean clientRequestSkin) {
        if (descriptor != null) {
            ISkinIdentifier skinIdentifier = descriptor.getIdentifier();
            return getSkinDetectSide(skinIdentifier, serverSoftLoad, clientRequestSkin);
        }
        return null;
    }

    public static Skin getSkinDetectSide(ISkinIdentifier skinIdentifier, boolean serverSoftLoad, boolean clientRequestSkin) {
//        if (skinIdentifier != null) {
//            if (ArmourersWorkshop.isDedicated()) {
//                return getSkinForSide(skinIdentifier, Side.SERVER, serverSoftLoad, clientRequestSkin);
//            } else {
//                Side side = FMLCommonHandler.instance().getEffectiveSide();
//                return getSkinForSide(skinIdentifier, side, serverSoftLoad, clientRequestSkin);
//            }
//        }
        return null;
    }

//    public static Skin getSkinForSide(ISkinIdentifier skinIdentifier, Side side, boolean softLoad, boolean requestSkin) {
//        if (side == Side.CLIENT) {
//            return getSkinOnClient(skinIdentifier, requestSkin);
//        } else {
//            return getSkinOnServer(skinIdentifier, softLoad);
//        }
//    }

//    private static Skin getSkinOnServer(ISkinIdentifier skinIdentifier, boolean softLoad) {
//        if (softLoad) {
//            return CommonSkinCache.INSTANCE.softGetSkin(skinIdentifier);
//        } else {
//            return CommonSkinCache.INSTANCE.getSkin(skinIdentifier);
//        }
//    }

//    @SideOnly(Side.CLIENT)
//    private static Skin getSkinOnClient(ISkinIdentifier skinIdentifier, boolean requestSkin) {
//        return ClientSkinCache.INSTANCE.getSkin(skinIdentifier, requestSkin);
//    }

    public static Skin copySkin(Skin skin) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SkinIOUtils.saveSkinToStream(outputStream, skin);
        byte[] skinData = outputStream.toByteArray();
        Skin skinCopy = SkinIOUtils.loadSkinFromStream(new ByteArrayInputStream(skinData));
        return skinCopy;
    }

    private static int getSkinIndex(String partIndexProp, Skin skin, int partIndex) {
        String[] split = partIndexProp.split(":");
        for (int i = 0; i < split.length; i++) {
            int count = Integer.parseInt(split[i]);
            if (partIndex < count) {
                return i;
            }
        }
        return -1;
    }


}
