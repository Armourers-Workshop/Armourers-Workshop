package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.core.data.EntityCollisionContainer;
import moe.plushie.armourers_workshop.core.data.EntityCollisionShape;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.data.SimpleContainer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SkinWardrobe implements IDataSerializable.Mutable {

    private final BitSet flags = new BitSet(6);

    private final HashMap<SkinSlotType, Integer> skinSlots = new HashMap<>();

    private final SimpleContainer inventory = new SimpleContainer(SkinSlotType.getTotalSize());
    private final EntityCollisionContainer collision;

    private final WeakReference<Entity> entity;

    private int id; // a.k.a entity id
    private EntityProfile profile; // maybe synced to new profile from server side.

    public SkinWardrobe(Entity entity, EntityProfile profile) {
        this.id = entity.getId();
        this.entity = new WeakReference<>(entity);
        this.collision = new EntityCollisionContainer(inventory, entity);
        this.profile = profile;
    }

    @Nullable
    public static SkinWardrobe of(@Nullable Entity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).wardrobe().orElse(null);
        }
        return null;
    }

    public static Optional<SkinWardrobe> create(Entity entity) {
        var profile = ModEntityProfiles.getProfile(entity);
        if (profile != null) {
            return Optional.of(new SkinWardrobe(entity, profile));
        }
        return Optional.empty();
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        SkinWardrobeStorage.saveSkinSlots(skinSlots, serializer);
        SkinWardrobeStorage.saveFlags(flags, serializer);
        SkinWardrobeStorage.saveInventoryItems(inventory, serializer);
        SkinWardrobeStorage.saveBoundingBox(collision, serializer);
        SkinWardrobeStorage.saveDataFixer(this, serializer);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        collision.beginUpdates();
        SkinWardrobeStorage.loadSkinSlots(skinSlots, serializer);
        SkinWardrobeStorage.loadFlags(flags, serializer);
        SkinWardrobeStorage.loadInventoryItems(inventory, serializer);
        SkinWardrobeStorage.loadBoundingBox(collision, this, serializer);
        SkinWardrobeStorage.loadDataFixer(this, serializer);
        collision.endUpdates();
    }

    public void setProfile(EntityProfile profile) {
        this.profile = profile;
    }

    public EntityProfile profile() {
        return profile;
    }

    public int getFreeSlot(SkinSlotType slotType) {
        var unlockedSize = getUnlockedSize(slotType);
        for (var i = 0; i < unlockedSize; ++i) {
            if (inventory.getItem(slotType.index() + i).isEmpty()) {
                return i;
            }
        }
        return unlockedSize - 1;
    }

    public ItemStack getItem(SkinSlotType slotType, int slot) {
        if (slot < 0 || slot >= getUnlockedSize(slotType)) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(slotType.index() + slot);
    }

    public void setItem(SkinSlotType slotType, int slot, ItemStack itemStack) {
        if (slot < 0 || slot >= getUnlockedSize(slotType)) {
            return;
        }
        inventory.setItem(slotType.index() + slot, itemStack);
    }

    public void dropAll(@Nullable Consumer<ItemStack> consumer) {
        var containerSize = inventory.getContainerSize();
        var ignoredStart = SkinSlotType.DYE.index() + 8;
        var ignoredEnd = SkinSlotType.DYE.index() + SkinSlotType.DYE.maxSize();
        for (int i = 0; i < containerSize; ++i) {
            if (i >= ignoredStart && i < ignoredEnd) {
                continue;
            }
            var itemStack = inventory.getItem(i);
            if (itemStack.isEmpty()) {
                continue;
            }
            if (consumer != null) {
                consumer.accept(itemStack);
            }
            inventory.setItem(i, ItemStack.EMPTY);
        }
    }

    public void clear() {
        inventory.clearContent();
    }

    public void sendToServer() {
        NetworkManager.sendToServer(UpdateWardrobePacket.sync(this));
    }

    public void broadcast() {
        NetworkManager.sendToTracking(UpdateWardrobePacket.sync(this), entity());
    }

    public void broadcast(ServerPlayer player) {
        NetworkManager.sendTo(UpdateWardrobePacket.sync(this), player);
    }

    public void setCollisionShape(EntityCollisionShape shape) {
        collision.setResult(shape);
    }

    public EntityCollisionShape collisionShape() {
        return collision.result();
    }

    public boolean shouldRenderEquipment(OpenEquipmentSlot slotType) {
        return !flags.get(slotType.filterFlag());
    }

    public void setRenderEquipment(OpenEquipmentSlot slotType, boolean enable) {
        if (enable) {
            flags.clear(slotType.filterFlag());
        } else {
            flags.set(slotType.filterFlag());
        }
    }

    public boolean shouldRenderExtra() {
        return !flags.get(6);
    }

    public void setRenderExtra(boolean value) {
        if (value) {
            flags.clear(6);
        } else {
            flags.set(6);
        }
    }

    public BitSet flags() {
        return flags;
    }

    public void setUnlockedSize(SkinSlotType slotType, int size) {
        if (slotType != SkinSlotType.DYE) {
            skinSlots.put(slotType, size);
        }
    }

    public int getUnlockedSize(SkinSlotType slotType) {
        if (slotType == SkinSlotType.DYE) {
            return 8;
        }
        var modifiedSize = skinSlots.get(slotType);
        if (modifiedSize != null) {
            return Math.min(slotType.maxSize(), modifiedSize);
        }
        return Math.min(slotType.maxSize(), profile.getMaxCount(slotType));
    }

    public int getMaximumSize(SkinSlotType slotType) {
        if (slotType == SkinSlotType.DYE) {
            return 8;
        }
        return slotType.maxSize();
    }

    public Container inventory() {
        return inventory;
    }

    @Nullable
    public Entity entity() {
        return entity.get();
    }

    public int id() {
        var entity = entity();
        if (entity != null) {
            id = entity.getId();
        }
        return id;
    }

    public boolean isEditable(Player player) {
        if (!ModPermissions.OPEN.accept(ModMenuTypes.WARDROBE, entity(), player)) {
            return false;
        }
        // can't edit another player's wardrobe
        var entity = entity();
        if (entity instanceof Player && entity.getId() != player.getId()) {
            return false;
        }
        if (!ModConfig.Common.canOpenWardrobe(entity, player)) {
            return false;
        }
        return !profile().isLocked();
    }

    public boolean isSupported(SkinSlotType slotType) {
        return profile.isSupported(slotType);
    }
}

