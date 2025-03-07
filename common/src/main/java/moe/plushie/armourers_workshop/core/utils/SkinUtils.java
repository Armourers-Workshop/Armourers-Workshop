package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanOverride;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector4f;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public final class SkinUtils {

    public static Collection<String> getItemOverrides(SkinPartType partType) {
        var override = Objects.safeCast(partType, ICanOverride.class);
        if (override != null) {
            return override.getItemOverrides();
        }
        return Collections.emptyList();
    }

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

    public static void dropAllIfNeeded(Player player) {
        if (SkinUtils.shouldKeepWardrobe(player)) {
            return; // ignore
        }
        SkinWardrobe oldWardrobe = SkinWardrobe.of(player);
        if (oldWardrobe != null) {
            oldWardrobe.dropAll(player::spawnAtLocation);
            oldWardrobe.broadcast();
        }
    }

    public static <T extends Entity> Consumer<T> appendSkinIntoEntity(Consumer<T> consumer, ServerLevel serverLevel, ItemStack itemStack, @Nullable Player player) {
        //
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return consumer;
        }
        return consumer.andThen(entity -> {
            // only allow of the boat
            if (!(entity instanceof Boat || entity instanceof AbstractMinecart)) {
                return;
            }
            var wardrobe = SkinWardrobe.of(entity);
            if (wardrobe != null) {
                wardrobe.setItem(SkinSlotType.ANY, 0, descriptor.asItemStack());
                wardrobe.broadcast();
            }
        });
    }

    public static <T extends Entity> Consumer<ItemStack> appendSkinIntoItemStack(Consumer<ItemStack> consumer, T entity) {
        // only allow of the boat
        if (!(entity instanceof Boat || entity instanceof AbstractMinecart)) {
            return consumer;
        }
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe == null) {
            return consumer;
        }
        // ..
        var itemStack1 = wardrobe.getItem(SkinSlotType.ANY, 0);
        var descriptor = SkinDescriptor.of(itemStack1);
        if (descriptor.isEmpty()) {
            return consumer;
        }
        return consumer.andThen(itemStack -> itemStack.set(ModDataComponents.SKIN.get(), descriptor));
    }

    public static void copySkinWardrobe(Entity from, Entity to) {
        var oldWardrobe = SkinWardrobe.of(from);
        var newWardrobe = SkinWardrobe.of(to);
        if (newWardrobe != null && oldWardrobe != null) {
            var serializer = new TagSerializer(new CompoundTag(), to);
            oldWardrobe.serialize(serializer);
            newWardrobe.deserialize(serializer);
            if (!to.getLevel().isClientSide()) {
                newWardrobe.broadcast();
            }
        }
    }

    public static void copySkinFromOwner(Entity entity) {
        var projectile = Objects.safeCast(entity, Projectile.class);
        if (projectile == null) {
            return;
        }
        var owner = projectile.getOwner();
        if (entity instanceof ThrownTrident) {
            copySkin(owner, entity, SkinSlotType.TRIDENT, 0, SkinSlotType.ANY, 0);
            return;
        }
        if (entity instanceof AbstractArrow) {
            copySkin(owner, entity, SkinSlotType.BOW, 0, SkinSlotType.ANY, 0);
            return;
        }
        if (entity instanceof FishingHook && owner instanceof LivingEntity livingEntity) {
            var itemStack = livingEntity.getMainHandItem();
            if (!itemStack.is(Items.FISHING_ROD)) {
                itemStack = livingEntity.getOffhandItem();
            }
            copySkin(entity, itemStack, SkinSlotType.ANY, 0);
            return;
        }
        // no supported projectile entity.
    }

    public static void copySkin(Entity src, Entity dest, SkinSlotType fromSlotType, int fromIndex, SkinSlotType toSlotType, int toIndex) {
        var itemStack = getSkin(src, fromSlotType, fromIndex);
        if (itemStack.isEmpty()) {
            return;
        }
        copySkin(dest, itemStack, toSlotType, toIndex);
    }

    public static void copySkin(Entity dest, ItemStack itemStack, SkinSlotType toSlotType, int toIndex) {
        var wardrobe = SkinWardrobe.of(dest);
        if (wardrobe != null) {
            wardrobe.setItem(toSlotType, toIndex, itemStack.copy());
            wardrobe.broadcast();
        }
    }

    public static ItemStack getSkin(Entity entity, SkinSlotType slotType, int index) {
        var itemStack = ItemStack.EMPTY;
        if (entity instanceof LivingEntity livingEntity) {
            itemStack = getUsingItem(livingEntity);
        }
        // embedded skin is the highest priority
        var descriptor = SkinDescriptor.of(itemStack);
        if (Objects.equals(slotType.getSkinType(), descriptor.getType())) {
            return itemStack;
        }
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            var itemStack1 = wardrobe.getItem(slotType, index);
            descriptor = SkinDescriptor.of(itemStack1);
            if (Objects.equals(slotType.getSkinType(), descriptor.getType())) {
                return itemStack1;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getUsingItem(LivingEntity entity) {
        var itemStack = entity.getUseItem();
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
        var split = partIndexProp.split(":");
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
        OpenVector4f[] points = new OpenVector4f[]{new OpenVector4f(minX, minY, minZ, 1.0f), new OpenVector4f(maxX, minY, minZ, 1.0f), new OpenVector4f(maxX, maxY, minZ, 1.0f), new OpenVector4f(minX, maxY, minZ, 1.0f), new OpenVector4f(minX, minY, maxZ, 1.0f), new OpenVector4f(maxX, minY, maxZ, 1.0f), new OpenVector4f(maxX, maxY, maxZ, 1.0f), new OpenVector4f(minX, maxY, maxZ, 1.0f)};
        boolean isReset = false;
        for (var point : points) {
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

    private static float[][][] reorder(float[][][] values, int... indexes) {
        float[][][] newValues = new float[values.length][][];
        for (int i = 0; i < values.length; ++i) {
            float[][] faces = values[i];
            float[][] newFaces = new float[faces.length][];
            for (int j = 0; j < faces.length; ++j) {
                if (j < indexes.length) {
                    newFaces[indexes[j]] = faces[j];
                } else {
                    newFaces[j] = faces[j];
                }
            }
            newValues[i] = newFaces;
        }
        return newValues;
    }
}
