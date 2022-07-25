package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.common.INBTRepresentable;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.data.SkinDataStorage;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SkinWardrobe implements ISkinWardrobe, INBTRepresentable<CompoundTag> {

    private final BitSet flags = new BitSet(6);

    private final HashMap<SkinSlotType, Integer> skinSlots = new HashMap<>();

    private final SimpleContainer inventory = new SimpleContainer(SkinSlotType.getTotalSize());

    private final WeakReference<Entity> entity;
    private final EntityProfile profile;

    private int id; // a.k.a entity id

    public SkinWardrobe(Entity entity, EntityProfile profile) {
        this.id = entity.getId();
        this.entity = new WeakReference<>(entity);
        this.profile = profile;
    }

    @Nullable
    public static SkinWardrobe of(@Nullable Entity entity) {
        if (entity != null) {
            return SkinDataStorage.getWardrobe(entity).orElse(null);
        }
        return null;
    }

    public static Optional<SkinWardrobe> create(Entity entity) {
        EntityProfile profile = ModEntityProfiles.getProfile(entity);
        if (profile != null) {
            return Optional.of(new SkinWardrobe(entity, profile));
        }
        return Optional.empty();
    }

    public EntityProfile getProfile() {
        return profile;
    }

    public int getFreeSlot(SkinSlotType slotType) {
        int unlockedSize = getUnlockedSize(slotType);
        for (int i = 0; i < unlockedSize; ++i) {
            if (inventory.getItem(slotType.getIndex() + i).isEmpty()) {
                return i;
            }
        }
        return unlockedSize - 1;
    }

    public ItemStack getItem(SkinSlotType slotType, int slot) {
        if (slot >= getUnlockedSize(slotType)) {
            return ItemStack.EMPTY;
        }
        return inventory.getItem(slotType.getIndex() + slot);
    }

    public void setItem(SkinSlotType slotType, int slot, ItemStack itemStack) {
        if (slot >= getUnlockedSize(slotType)) {
            return;
        }
        inventory.setItem(slotType.getIndex() + slot, itemStack);
    }

    public void dropAll(@Nullable Consumer<ItemStack> consumer) {
        int containerSize = inventory.getContainerSize();
        int ignoredStart = SkinSlotType.DYE.getIndex() + 8;
        int ignoredEnd = SkinSlotType.DYE.getIndex() + SkinSlotType.DYE.getMaxSize();
        for (int i = 0; i < containerSize; ++i) {
            if (i >= ignoredStart && i < ignoredEnd) {
                continue;
            }
            ItemStack itemStack = inventory.getItem(i);
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

//    public void sendToAll() {
//        NetworkManager.sendToAll(UpdateWardrobePacket.sync(this));
//    }

    public void sendToServer() {
        NetworkManager.sendToServer(UpdateWardrobePacket.sync(this));
    }

    public void broadcast() {
        NetworkManager.sendToTracking(UpdateWardrobePacket.sync(this), getEntity());
    }

    public void broadcast(ServerPlayer player) {
        NetworkManager.sendTo(UpdateWardrobePacket.sync(this), player);
    }

    public boolean shouldRenderEquipment(EquipmentSlot slotType) {
        return !flags.get(slotType.getFilterFlag());
    }

    public void setRenderEquipment(EquipmentSlot slotType, boolean enable) {
        if (enable) {
            flags.clear(slotType.getFilterFlag());
        } else {
            flags.set(slotType.getFilterFlag());
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

    public BitSet getFlags() {
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
        Integer modifiedSize = skinSlots.get(slotType);
        if (modifiedSize != null) {
            return Math.min(slotType.getMaxSize(), modifiedSize);
        }
        ISkinType type = slotType.getSkinType();
        if (type != null) {
            return Math.min(slotType.getMaxSize(), profile.getMaxCount(type));
        }
        return slotType.getMaxSize();
    }

//    public int getFlags(int bits) {
//        return 0;
//    }
//
//    public void setFlags(int bits, int value) {
//    }

    public Container getInventory() {
        return inventory;
    }

    @Nullable
    public Entity getEntity() {
        return entity.get();
    }

    public int getId() {
        Entity entity = getEntity();
        if (entity != null) {
            id = entity.getId();
        }
        return id;
    }

    public boolean isEditable(Player player) {
        if (!ModPermissions.OPEN.accept(ModMenuTypes.WARDROBE, getEntity(), player)) {
            return false;
        }
        // can't edit another player's wardrobe
        Entity entity = getEntity();
        if (entity instanceof Player && entity.getId() != player.getId()) {
            return false;
        }
        if (!ModConfig.Common.canOpenWardrobe(entity, player)) {
            return false;
        }
        return getProfile().isEditable();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        SkinWardrobeStorage.saveSkinSlots(skinSlots, nbt);
//        SkinWardrobeStorage.saveVisibility(armourFlags, nbt);
        SkinWardrobeStorage.saveFlags(flags, nbt);
        SkinWardrobeStorage.saveInventoryItems(inventory, nbt);
        SkinWardrobeStorage.saveDataFixer(this, nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
       SkinWardrobeStorage.loadSkinSlots(skinSlots, nbt);
//        SkinWardrobeStorage.loadVisibility(armourFlags, nbt);
        SkinWardrobeStorage.loadFlags(flags, nbt);
        SkinWardrobeStorage.loadInventoryItems(inventory, nbt);
        SkinWardrobeStorage.loadDataFixer(this, nbt);
    }
}

