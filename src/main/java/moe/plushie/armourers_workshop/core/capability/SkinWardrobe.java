package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.ISkinWardrobe;
import moe.plushie.armourers_workshop.core.data.SkinDataStorage;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SkinWardrobe implements ISkinWardrobe, INBTSerializable<CompoundNBT> {

    private final HashSet<EquipmentSlotType> armourFlags = new HashSet<>();
    private final HashMap<SkinSlotType, Integer> skinSlots = new HashMap<>();

    private final Inventory inventory = new Inventory(SkinSlotType.getTotalSize());

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
            return SkinDataStorage.getWardrobe(entity);
        }
        return null;
    }

    public EntityProfile getProfile() {
        return profile;
    }

    public int getFreeSlot(SkinSlotType slotType) {
        int unlockedSize = getUnlockedSize(slotType);
        for (int i = 0; i < unlockedSize; ++i) {
            if (inventory.getItem(slotType.getIndex() + i).isEmpty()) {
                return i + 1;
            }
        }
        return Integer.MAX_VALUE;
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

    public void sendToAll() {
        NetworkHandler.getInstance().sendToAll(UpdateWardrobePacket.sync(this));
    }

    public void sendToServer() {
        NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.sync(this));
    }

    public void broadcast(ServerPlayerEntity player) {
        NetworkHandler.getInstance().sendTo(UpdateWardrobePacket.sync(this), player);
    }
    public boolean shouldRenderEquipment(EquipmentSlotType slotType) {
        return !armourFlags.contains(slotType);
    }

    public void setRenderEquipment(EquipmentSlotType slotType, boolean enable) {
        if (enable) {
            armourFlags.remove(slotType);
        } else {
            armourFlags.add(slotType);
        }
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

    public Inventory getInventory() {
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

    public boolean isEditable(PlayerEntity player) {
        // can't edit another player's wardrobe
        Entity entity = getEntity();
        if (entity instanceof PlayerEntity && entity.getId() != player.getId()) {
            return false;
        }
        return getProfile().isEditable();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        SkinWardrobeStorage.saveSkinSlots(skinSlots, nbt);
        SkinWardrobeStorage.saveVisibility(armourFlags, nbt);
        SkinWardrobeStorage.saveInventoryItems(inventory, nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        SkinWardrobeStorage.loadSkinSlots(skinSlots, nbt);
        SkinWardrobeStorage.loadVisibility(armourFlags, nbt);
        SkinWardrobeStorage.loadInventoryItems(inventory, nbt);
    }
}

