package moe.plushie.armourers_workshop.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class SkinUtils {

    private SkinUtils() {
    }

    public static Skin getSkinDetectSide(ItemStack stack, boolean serverSoftLoad, boolean clientRequestSkin) {
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        return getSkinDetectSide(skinPointer, serverSoftLoad, clientRequestSkin);
    }

    public static Skin getSkinDetectSide(ISkinDescriptor descriptor, boolean serverSoftLoad, boolean clientRequestSkin) {
        if (descriptor != null) {
            ISkinIdentifier skinIdentifier = descriptor.getIdentifier();
            return getSkinDetectSide(skinIdentifier, serverSoftLoad, clientRequestSkin);
        }
        return null;
    }

    public static Skin getSkinDetectSide(ISkinIdentifier skinIdentifier, boolean serverSoftLoad, boolean clientRequestSkin) {
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

    public static Skin getSkinForSide(ISkinIdentifier skinIdentifier, Side side, boolean softLoad, boolean requestSkin) {
        if (side == Side.CLIENT) {
            return getSkinOnClient(skinIdentifier, requestSkin);
        } else {
            return getSkinOnServer(skinIdentifier, softLoad);
        }
    }

    private static Skin getSkinOnServer(ISkinIdentifier skinIdentifier, boolean softLoad) {
        if (softLoad) {
            return CommonSkinCache.INSTANCE.softGetSkin(skinIdentifier);
        } else {
            return CommonSkinCache.INSTANCE.getSkin(skinIdentifier);
        }
    }

    @SideOnly(Side.CLIENT)
    private static Skin getSkinOnClient(ISkinIdentifier skinIdentifier, boolean requestSkin) {
        return ClientSkinCache.INSTANCE.getSkin(skinIdentifier, requestSkin);
    }

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

    public static double getFlapAngleForWings(Entity entity, Skin skin, int partIndex) {

        double maxAngle = SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skin.getProperties());
        double minAngle = SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skin.getProperties());
        double idleSpeed = SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skin.getProperties());
        double flyingSpeed = SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skin.getProperties());
        MovementType movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skin.getProperties()));

        if (skin.getSkinType() == SkinTypeRegistry.skinOutfit) {
            String partIndexProp = SkinProperties.PROP_OUTFIT_PART_INDEXS.getValue(skin.getProperties());
            if (!partIndexProp.equals("")) {
                int index = getSkinIndex(partIndexProp, skin, partIndex);
                if (index != -1) {
                    maxAngle = SkinProperties.PROP_WINGS_MAX_ANGLE.getValue(skin.getProperties(), index);
                    minAngle = SkinProperties.PROP_WINGS_MIN_ANGLE.getValue(skin.getProperties(), index);
                    idleSpeed = SkinProperties.PROP_WINGS_IDLE_SPEED.getValue(skin.getProperties(), index);
                    flyingSpeed = SkinProperties.PROP_WINGS_FLYING_SPEED.getValue(skin.getProperties(), index);
                    movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(skin.getProperties(), index));
                }
            }
        }

        double angle = 0;
        double flapTime = idleSpeed;

        if (entity != null) {
            if (entity.isAirBorne) {
                if (entity instanceof EntityPlayer) {
                    if (((EntityPlayer) entity).capabilities.isFlying) {
                        flapTime = flyingSpeed;
                    }
                } else {
                    flapTime = flyingSpeed;
                }
            }
            if (entity instanceof EntityMannequin) {
                if (((EntityMannequin) entity).isFlying()) {
                    flapTime = flyingSpeed;
                }
            }

            angle = (((double) System.currentTimeMillis() + entity.getEntityId()) % flapTime);
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
