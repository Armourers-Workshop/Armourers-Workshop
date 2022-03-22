package moe.plushie.armourers_workshop.core.capability;

import com.google.common.cache.Cache;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.common.IWardrobe;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unused")
public class SkinWardrobe implements IWardrobe, INBTSerializable<CompoundNBT> {

    private final HashSet<EquipmentSlotType> armourFlags = new HashSet<>();
    private final HashMap<SkinSlotType, Integer> skinSlots = new HashMap<>();

    private final Inventory inventory = new Inventory(SkinSlotType.getTotalSize());

    private final WeakReference<Entity> entity;
    private final EntityProfile profile;

    private SkinWardrobeState state;
    private int id; // a.k.a entity id

    public SkinWardrobe(Entity entity, EntityProfile profile) {
        this.id = entity.getId();
        this.entity = new WeakReference<>(entity);
        this.profile = profile;
        this.inventory.addListener(inventory -> {
            if (state != null) {
                state.invalidateAll();
            }
        });
    }

    @Nullable
    public static SkinWardrobe of(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        Object key = entity.getId();
        Cache<Object, LazyOptional<SkinWardrobe>> caches = WardrobeStorage.getCaches(entity);
        LazyOptional<SkinWardrobe> wardrobe = caches.getIfPresent(key);
        if (wardrobe != null) {
            return wardrobe.resolve().orElse(null);
        }
        wardrobe = entity.getCapability(WardrobeProvider.WARDROBE_KEY);
        wardrobe.addListener(self -> caches.invalidate(key));
        caches.put(key, wardrobe);
        return wardrobe.resolve().orElse(null);
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

    public int getUnlockedSize(SkinSlotType slotType) {
        if (slotType == SkinSlotType.DYE) {
            return 8;
        }
        Integer modifiedSize = skinSlots.get(slotType);
        if (modifiedSize != null) {
            return Math.min(slotType.getSize(), modifiedSize);
        }
        ISkinType type = slotType.getSkinType();
        if (type != null) {
            return Math.min(slotType.getSize(), profile.getMaxCount(type));
        }
        return slotType.getSize();
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        WardrobeStorage.saveSkinSlots(skinSlots, nbt);
        WardrobeStorage.saveVisibility(armourFlags, nbt);
        WardrobeStorage.saveInventoryItems(inventory, nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        WardrobeStorage.loadSkinSlots(skinSlots, nbt);
        WardrobeStorage.loadVisibility(armourFlags, nbt);
        WardrobeStorage.loadInventoryItems(inventory, nbt);
        invalidateAll();
    }

    public void invalidateAll() {
        // apply the client
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            if (state != null) {
                state.invalidateAll();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public SkinWardrobeState snapshot() {
        if (state == null) {
            state = new SkinWardrobeState(inventory);
        }
        Entity entity = getEntity();
        if (entity != null) {
            if (state.tick(entity)) {
                state.reload(entity);
            }
        }
        return state;
    }
}

