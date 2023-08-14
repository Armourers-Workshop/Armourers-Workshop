package moe.plushie.armourers_workshop.core.client.other;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import manifold.ext.rt.api.auto;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SkinOverriddenManager {

    private static final ImmutableList<EquipmentSlot> ARMOUR_EQUIPMENT_SLOTS = new ImmutableList.Builder<EquipmentSlot>()
            .add(EquipmentSlot.HEAD)
            .add(EquipmentSlot.CHEST)
            .add(EquipmentSlot.LEGS)
            .add(EquipmentSlot.FEET)
            .build();

    private static final ImmutableList<ISkinProperty<Boolean>> OVERRIDDEN_PROPERTIES = new ImmutableList.Builder<ISkinProperty<Boolean>>()
            .add(SkinProperty.OVERRIDE_MODEL_HEAD)
            .add(SkinProperty.OVERRIDE_MODEL_CHEST)
            .add(SkinProperty.OVERRIDE_MODEL_LEFT_ARM)
            .add(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM)
            .add(SkinProperty.OVERRIDE_MODEL_LEFT_LEG)
            .add(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG)
            .add(SkinProperty.OVERRIDE_OVERLAY_HAT)
            .add(SkinProperty.OVERRIDE_OVERLAY_CLOAK)
            .add(SkinProperty.OVERRIDE_OVERLAY_JACKET)
            .add(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE)
            .add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE)
            .add(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS)
            .add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS)
            .add(SkinProperty.OVERRIDE_EQUIPMENT_HELMET)
            .add(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE)
            .add(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS)
            .add(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS)
            .build();

    private static final ImmutableMap<ISkinProperty<Boolean>, EquipmentSlot> OVERRIDDEN_EQUIPMENT_TO_SLOT = new ImmutableMap.Builder<ISkinProperty<Boolean>, EquipmentSlot>()
            .put(SkinProperty.OVERRIDE_EQUIPMENT_HELMET, EquipmentSlot.HEAD)
            .put(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE, EquipmentSlot.CHEST)
            .put(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS, EquipmentSlot.LEGS)
            .put(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS, EquipmentSlot.FEET)
            .build();

    private static final ImmutableMap<ISkinProperty<Boolean>, Collection<ISkinProperty<Boolean>>> OVERRIDDEN_MODEL_TO_OVERLAY = new ImmutableMap.Builder<ISkinProperty<Boolean>, Collection<ISkinProperty<Boolean>>>()
            .put(SkinProperty.OVERRIDE_MODEL_HEAD, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_HAT))
            .put(SkinProperty.OVERRIDE_MODEL_CHEST, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_JACKET, SkinProperty.OVERRIDE_OVERLAY_CLOAK))
            .put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE))
            .put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE))
            .put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS))
            .put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, ObjectUtils.map(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS))
            .build();

    private final HashSet<ISkinProperty<Boolean>> disabledProperties = new HashSet<>();
    private final HashSet<ISkinProperty<Boolean>> disabledModelByProperties = new HashSet<>();

    private final HashSet<EquipmentSlot> disabledEquipmentSlots = new HashSet<>();
    private final HashSet<EquipmentSlot> disabledEquipmentSlotsByProperties = new HashSet<>();

    private final HashMap<EquipmentSlot, ItemStack> disabledEquipmentItems = new HashMap<>();

    public void addEquipment(EquipmentSlot slotType) {
        disabledEquipmentSlots.add(slotType);
    }

    public void removeEquipment(EquipmentSlot slotType) {
        disabledEquipmentSlots.remove(slotType);
    }

    public void merge(ISkinProperties properties) {
        for (ISkinProperty<Boolean> property : OVERRIDDEN_PROPERTIES) {
            if (!properties.get(property)) {
                continue;
            }
            disabledProperties.add(property);
            // when equipment required hide, we need synchronize it to slot.
            auto equipmentSlot = OVERRIDDEN_EQUIPMENT_TO_SLOT.get(property);
            if (equipmentSlot != null) {
                disabledEquipmentSlotsByProperties.add(equipmentSlot);
            }
            // when model part required hide, we need synchronize it to overlay.
            auto overlayProperties = OVERRIDDEN_MODEL_TO_OVERLAY.get(property);
            if (overlayProperties != null) {
                disabledModelByProperties.add(property);
                disabledProperties.addAll(overlayProperties);
            }
        }
    }

    public boolean contains(ISkinProperty<Boolean> property) {
        return disabledProperties.contains(property);
    }

    // if it returns true, it means equipment is overwritten.
    public boolean overrideEquipment(EquipmentSlot slotType) {
        return disabledEquipmentSlots.contains(slotType) || disabledEquipmentSlotsByProperties.contains(slotType);
    }

    public boolean overrideAnyModel() {
        return !disabledModelByProperties.isEmpty();
    }

    public void clear() {
        disabledProperties.clear();
        disabledModelByProperties.clear();
        disabledEquipmentSlots.clear();
        disabledEquipmentSlotsByProperties.clear();
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
}
