package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;

@Environment(value = EnvType.CLIENT)
public class SkinOverriddenManager {

    // Convert the part to equipment slot
    private static final ImmutableMap<ISkinPartType, EquipmentSlot> PART_TO_EQUIPMENT_SLOTS = new ImmutableMap.Builder<ISkinPartType, EquipmentSlot>()
            .put(SkinPartTypes.BIPPED_HEAD, EquipmentSlot.HEAD)
            .put(SkinPartTypes.BIPPED_CHEST, EquipmentSlot.CHEST)
            .put(SkinPartTypes.BIPPED_LEFT_ARM, EquipmentSlot.CHEST)
            .put(SkinPartTypes.BIPPED_RIGHT_ARM, EquipmentSlot.CHEST)
            .put(SkinPartTypes.BIPPED_LEFT_FOOT, EquipmentSlot.FEET)
            .put(SkinPartTypes.BIPPED_RIGHT_FOOT, EquipmentSlot.FEET)
            .put(SkinPartTypes.BIPPED_LEFT_LEG, EquipmentSlot.LEGS)
            .put(SkinPartTypes.BIPPED_RIGHT_LEG, EquipmentSlot.LEGS)
            .build();

    private static final ImmutableList<EquipmentSlot> ARMOUR_EQUIPMENT_SLOTS = new ImmutableList.Builder<EquipmentSlot>()
            .add(EquipmentSlot.HEAD)
            .add(EquipmentSlot.CHEST)
            .add(EquipmentSlot.LEGS)
            .add(EquipmentSlot.FEET)
            .build();

    private final HashSet<ISkinPartType> disabledModels = new HashSet<>();
    private final HashSet<ISkinPartType> disabledOverlays = new HashSet<>();

    private final HashSet<EquipmentSlot> disabledEquipmentSlots = new HashSet<>();
    private final HashSet<EquipmentSlot> disabledEquipmentSlotsByPart = new HashSet<>();

    private final HashMap<EquipmentSlot, ItemStack> disabledEquipmentItems = new HashMap<>();

    public void addModel(ISkinPartType partType) {
        disabledModels.add(partType);
        disabledEquipmentSlotsByPart.add(getEquipmentSlot(partType));
    }

    public void addOverlay(ISkinPartType partType) {
        disabledOverlays.add(partType);
        disabledEquipmentSlotsByPart.add(getEquipmentSlot(partType));
    }

    public void addEquipment(EquipmentSlot slotType) {
        disabledEquipmentSlots.add(slotType);
    }

    public void removeEquipment(EquipmentSlot slotType) {
        disabledEquipmentSlots.remove(slotType);
    }

    // if it returns true, it means model is overwritten.
    public boolean overrideModel(ISkinPartType partType) {
        return disabledModels.contains(partType);
    }

    // if it returns true, it means overlay is overwritten.
    public boolean overrideOverlay(ISkinPartType partType) {
        return disabledModels.contains(partType)
                || disabledOverlays.contains(partType);
    }

    // if it returns true, it means equipment is overwritten.
    public boolean overrideEquipment(EquipmentSlot slotType) {
        return disabledEquipmentSlotsByPart.contains(slotType)
                || disabledEquipmentSlots.contains(slotType);
    }

    public boolean hasAnyPartOverride() {
        return !disabledModels.isEmpty();
    }

    public void clear() {
        disabledModels.clear();
        disabledOverlays.clear();
        disabledEquipmentSlots.clear();
        disabledEquipmentSlotsByPart.clear();
    }

    public void willRender(Entity entity) {
        for (EquipmentSlot slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!overrideEquipment(slotType) || disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            ItemStack itemStack = setItem(entity, slotType, ItemStack.EMPTY);
            disabledEquipmentItems.put(slotType, itemStack);
        }
    }

    public void didRender(Entity entity) {
        for (EquipmentSlot slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            ItemStack itemStack = disabledEquipmentItems.remove(slotType);
            setItem(entity, slotType, itemStack);
        }
    }

    private ItemStack setItem(Entity entity, EquipmentSlot slotType, ItemStack itemStack) {
        // for the player, using `setItemSlot` will cause play sound.
        if (entity instanceof Player) {
            Inventory inventory = ((Player) entity).getInventory();
            ItemStack itemStack1 = inventory.armor.get(slotType.getIndex());
            inventory.armor.set(slotType.getIndex(), itemStack);
            return itemStack1;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            ItemStack itemStack1 = livingEntity.getItemBySlot(slotType);
            livingEntity.setItemSlot(slotType, itemStack);
            return itemStack1;
        }
        return itemStack;
    }

    private EquipmentSlot getEquipmentSlot(ISkinPartType partType) {
        return PART_TO_EQUIPMENT_SLOTS.getOrDefault(partType, EquipmentSlot.OFFHAND);
    }
}
