package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.skin.ISkinDescriptor;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

public final class SkinUtils {

    public static final float[][][] FACE_UVS = new float[][][]{
            {{1, 0}, {1, 1}, {0, 1}, {0, 0}}, // -y <- down
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +y <- up
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // -z <- north
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}}, // +z <- south
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
            {{0, 0}, {0, 1}, {1, 1}, {1, 0}},
//            {{1, 0}, {1, 1}, {0, 1}, {0, 0}}, // -x <- west
//            {{1, 0}, {1, 1}, {0, 1}, {0, 0}}, // +x <- east
    };

    public static final float[][][] FACE_VERTEXES = new float[][][]{
            {{1, 1, 1}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 1, 0}},  // -y <- down
            {{0, 0, 1}, {0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, -1, 0}}, // +y <- up
            {{0, 0, 0}, {0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, -1}}, // -z <- north
            {{1, 0, 1}, {1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {0, 0, 1}},  // +z <- south
            {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}},  // -x <- west
            {{0, 0, 1}, {0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {-1, 0, 0}}, // +x <- east
    };

    private SkinUtils() {
    }

    public static float[][] getRenderUVs(Direction direction) {
        return FACE_UVS[direction.get3DDataValue()];
    }

    public static float[][] getRenderVertexes(Direction direction) {
        return FACE_VERTEXES[direction.get3DDataValue()];
    }

//    public static Skin getSkinDetectSide(ItemStack stack, boolean serverSoftLoad, boolean clientRequestSkin) {
//        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//        return getSkinDetectSide(skinPointer, serverSoftLoad, clientRequestSkin);
//        return null;
//    }

//    public static Skin getSkinDetectSide(ISkinDescriptor descriptor, boolean serverSoftLoad, boolean clientRequestSkin) {
//        if (descriptor != null) {
//            ISkinIdentifier skinIdentifier = descriptor.getIdentifier();
//            return getSkinDetectSide(skinIdentifier, serverSoftLoad, clientRequestSkin);
//        }
//        return null;
//    }

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

//    public static Skin getSkinDetectSide(ISkinIdentifier skinIdentifier, boolean serverSoftLoad, boolean clientRequestSkin) {
//        if (skinIdentifier != null) {
//            if (ArmourersWorkshop.isDedicated()) {
//                return getSkinForSide(skinIdentifier, Side.SERVER, serverSoftLoad, clientRequestSkin);
//            } else {
//                Side side = FMLCommonHandler.instance().getEffectiveSide();
//                return getSkinForSide(skinIdentifier, side, serverSoftLoad, clientRequestSkin);
//            }
//        }
//        return null;
//    }

//    public static void apply(PoseStack poseStack, SkinPart skinPart, float partialTicks, @Nullable Entity entity) {
//        ISkinPartType partType = skinPart.getType();
//        if (!(partType instanceof ICanRotation)) {
//            return;
//        }
//        List<SkinMarker> markers = skinPart.getMarkers();
//        if (markers == null || markers.size() == 0) {
//            return;
//        }
//        SkinMarker marker = markers.get(0);
//        Vector3i point = marker.getPosition();
//
//        float angle = (float) getRotationDegrees(skinPart, partialTicks, entity);
//        Vector3f offset = new Vector3f(point.getX() + 0.5f, point.getY() + 0.5f, point.getZ() + 0.5f);
//        if (!((ICanRotation) partType).isMirror()) {
//            angle = -angle;
//        }
//
//        poseStack.translate(offset.x(), offset.y(), offset.z());
//        poseStack.mul(getRotationMatrix(marker).rotationDegrees(angle));
//        poseStack.translate(-offset.x(), -offset.y(), -offset.z());
//    }
//
//    public static double getRotationDegrees(SkinPart skinPart, float partialTicks, @Nullable Entity entity) {
//        SkinProperties properties = skinPart.getProperties();
//        if (properties == null) {
//            return 0;
//        }
//
//        double maxAngle = properties.get(SkinProperty.WINGS_MAX_ANGLE);
//        double minAngle = properties.get(SkinProperty.WINGS_MIN_ANGLE);
//        String movementTypeName = properties.get(SkinProperty.WINGS_MOVMENT_TYPE);
//        SkinProperty.MovementType movementType = SkinProperty.MovementType.valueOf(movementTypeName);
//
//        double flapTime = properties.get(SkinProperty.WINGS_IDLE_SPEED);
//        if (entity instanceof LivingEntity && ((LivingEntity) entity).isFallFlying()) {
//            flapTime = properties.get(SkinProperty.WINGS_FLYING_SPEED);
//        }
//
//        double angle = partialTicks % flapTime;
//
//        if (movementType == SkinProperty.MovementType.EASE) {
//            angle = Math.sin(angle / flapTime * Math.PI * 2);
//        }
//        if (movementType == SkinProperty.MovementType.LINEAR) {
//            angle = angle / flapTime;
//        }
//
//        double fullAngle = maxAngle - minAngle;
//        if (movementType == SkinProperty.MovementType.LINEAR) {
//            return fullAngle * angle;
//        }
//
//        return -minAngle - fullAngle * ((angle + 1D) / 2);
//    }
//
//    public static Vector3f getRotationMatrix(SkinMarker marker) {
//        switch (marker.getDirection()) {
//            case UP:
//                return Vector3f.YP;
//            case DOWN:
//                return Vector3f.YN;
//            case SOUTH:
//                return Vector3f.ZN;
//            case NORTH:
//                return Vector3f.ZP;
//            case EAST:
//                return Vector3f.XP;
//            case WEST:
//                return Vector3f.XN;
//        }
//        return Vector3f.YP;
//    }

    public static boolean shouldKeepWardrobe(Player entity) {
        if (entity.isSpectator()) {
            return true;
        }
        // 0 = use keep inventory rule
        // 1 = never drop
        // 2 = always drop
        int keep = ModConfig.Common.prefersWardrobeDropOnDeath;
        if (keep == 1) {
            return true;
        }
        if (keep == 2) {
            return false;
        }
        return entity.getLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
    }

    public static void dropAll(Player player) {
        if (SkinUtils.shouldKeepWardrobe(player)) {
            return; // ignore
        }
        SkinWardrobe oldWardrobe = SkinWardrobe.of(player);
        if (oldWardrobe != null) {
            oldWardrobe.dropAll(player::spawnAtLocation);
        }
    }

    public static void copySkinFromOwner(Entity entity) {
        Projectile projectile = ObjectUtils.safeCast(entity, Projectile.class);
        if (projectile == null) {
            return;
        }
        Entity owner = projectile.getOwner();
        if (entity instanceof ThrownTrident) {
            copySkin(owner, entity, SkinSlotType.TRIDENT, 0);
            return;
        }
        if (entity instanceof AbstractArrow) {
            copySkin(owner, entity, SkinSlotType.BOW, 0);
            return;
        }
        // no supported projectile entity.
    }

    public static void copySkin(Entity src, Entity dest, SkinSlotType slotType, int index) {
        ItemStack itemStack = getSkin(src, slotType, index);
        if (itemStack.isEmpty()) {
            return;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(dest);
        if (wardrobe != null) {
            wardrobe.setItem(slotType, index, itemStack.copy());
            wardrobe.broadcast();
        }
    }

    public static Skin copySkin(Skin skin) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SkinFileStreamUtils.saveSkinToStream(outputStream, skin);
        byte[] skinData = outputStream.toByteArray();
        Skin skinCopy = SkinFileStreamUtils.loadSkinFromStream(new ByteArrayInputStream(skinData));
        return skinCopy;
    }

    public static ItemStack getSkin(Entity entity, SkinSlotType slotType, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (entity instanceof LivingEntity) {
            itemStack = getUsingItem((LivingEntity) entity);
        }
        // embedded skin is the highest priority
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (Objects.equals(slotType.getSkinType(), descriptor.getType())) {
            return itemStack;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            ItemStack itemStack1 = wardrobe.getItem(slotType, index);
            descriptor = SkinDescriptor.of(itemStack1);
            if (Objects.equals(slotType.getSkinType(), descriptor.getType())) {
                return itemStack1;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getUsingItem(LivingEntity entity) {
        ItemStack itemStack = entity.getUseItem();
        if (!itemStack.isEmpty()) {
            return itemStack;
        }
        itemStack = entity.getMainHandItem();
        if (itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return itemStack;
        }
        return ItemStack.EMPTY;
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


    public static VoxelShape apply(VoxelShape shape, OpenMatrix4f matrix) {
        float minX = (float) shape.min(Direction.Axis.X);
        float minY = (float) shape.min(Direction.Axis.Y);
        float minZ = (float) shape.min(Direction.Axis.Z);
        float maxX = (float) shape.max(Direction.Axis.X);
        float maxY = (float) shape.max(Direction.Axis.Y);
        float maxZ = (float) shape.max(Direction.Axis.Z);
        Vector4f[] points = new Vector4f[]{
                new Vector4f(minX, minY, minZ, 1.0f),
                new Vector4f(maxX, minY, minZ, 1.0f),
                new Vector4f(maxX, maxY, minZ, 1.0f),
                new Vector4f(minX, maxY, minZ, 1.0f),
                new Vector4f(minX, minY, maxZ, 1.0f),
                new Vector4f(maxX, minY, maxZ, 1.0f),
                new Vector4f(maxX, maxY, maxZ, 1.0f),
                new Vector4f(minX, maxY, maxZ, 1.0f)
        };
        boolean isReset = false;
        for (Vector4f point : points) {
            point.transform(matrix);
            if (isReset) {
                minX = Math.min(minX, point.x());
                minY = Math.min(minY, point.y());
                minZ = Math.min(minZ, point.z());
                maxX = Math.max(maxX, point.x());
                maxY = Math.max(maxY, point.y());
                maxZ = Math.max(maxZ, point.z());
            } else {
                minX = point.x();
                minY = point.y();
                minZ = point.z();
                maxX = point.x();
                maxY = point.y();
                maxZ = point.z();
                isReset = true;
            }
        }
        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
