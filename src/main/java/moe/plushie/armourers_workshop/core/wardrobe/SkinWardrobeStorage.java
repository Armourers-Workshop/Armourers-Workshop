package moe.plushie.armourers_workshop.core.wardrobe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SkinWardrobeStorage implements Capability.IStorage<SkinWardrobe> {

    private final static Cache<Object, LazyOptional<SkinWardrobe>> CLIENT_CACHES = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.SECONDS).build();
    private final static Cache<Object, LazyOptional<SkinWardrobe>> SERVER_CACHES = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.SECONDS).build();

    public static Cache<Object, LazyOptional<SkinWardrobe>> getCaches(Entity entity) {
        if (entity.level != null && entity.level.isClientSide) {
            return CLIENT_CACHES;
        }
        return SERVER_CACHES;
    }

    public static void saveInventoryItems(Inventory inventory, CompoundNBT nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < itemStacks.size(); ++i) {
            itemStacks.set(i, inventory.getItem(i));
        }
        ItemStackHelper.saveAllItems(nbt, itemStacks);
    }

    public static void loadInventoryItems(Inventory inventory, CompoundNBT nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, itemStacks);
        for (int i = 0; i < itemStacks.size(); ++i) {
            ItemStack newItemStack = itemStacks.get(i);
            ItemStack oldItemStack = inventory.getItem(i);
            if (!Objects.equals(newItemStack, oldItemStack)) {
                inventory.setItem(i, newItemStack);
            }
        }
    }

    public static void saveVisibility(HashSet<EquipmentSlotType> visibility, CompoundNBT nbt) {
        if (visibility.isEmpty()) {
            return;
        }
        short value = 0;
        for (EquipmentSlotType slotType : visibility) {
            value |= 1 << slotType.getFilterFlag();
        }
        nbt.putShort("Visibility", value);
    }

    public static void loadVisibility(HashSet<EquipmentSlotType> visibility, CompoundNBT nbt) {
        short value = nbt.getShort("Visibility");
        visibility.clear();
        for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
            int mask = 1 << slotType.getFilterFlag();
            if ((value & mask) != 0) {
                visibility.add(slotType);
            }
        }
    }

    public static void saveSkinSlots(HashMap<SkinSlotType, Integer> slots, CompoundNBT nbt) {
        if (slots.isEmpty()) {
            return;
        }
        ListNBT value = new ListNBT();
        slots.forEach((slotType, count) -> {
            int index = slotType.ordinal() & 0xff;
            int encoded = index << 8 | count & 0xff;
            value.add(ShortNBT.valueOf((short) encoded));
        });
        if (!value.isEmpty()) {
            nbt.put("Slots", value);
        }
    }

    public static void loadSkinSlots(HashMap<SkinSlotType, Integer> slots, CompoundNBT nbt) {
        ListNBT value = nbt.getList("Slots", 2);
        if (value.isEmpty()) {
            return;
        }
        SkinSlotType[] slotTypes = SkinSlotType.values();
        for (int i = 0; i < value.size(); ++i) {
            short encoded = value.getShort(i);
            int index =     (encoded >> 8) & 0xff;
            if (index < slotTypes.length) {
                slots.put(slotTypes[index], encoded & 0xff);
            }
        }
    }

    @Nullable
    @Override
    public INBT writeNBT(Capability<SkinWardrobe> capability, SkinWardrobe instance, Direction side) {
        return null;
    }

    @Override
    public void readNBT(Capability<SkinWardrobe> capability, SkinWardrobe instance, Direction side, INBT nbt) {
    }
}
