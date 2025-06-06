package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class SkinOverriddenManager<T> {

    private static final List<EquipmentSlot> ARMOUR_EQUIPMENT_SLOTS = Collections.immutableList(builder -> {
        builder.add(EquipmentSlot.HEAD);
        builder.add(EquipmentSlot.CHEST);
        builder.add(EquipmentSlot.LEGS);
        builder.add(EquipmentSlot.FEET);
    });

    private static final List<ISkinProperty<Boolean>> OVERRIDDEN_PROPERTIES = Collections.immutableList(builder -> {
        builder.add(SkinProperty.OVERRIDE_MODEL_HEAD);
        builder.add(SkinProperty.OVERRIDE_MODEL_CHEST);
        builder.add(SkinProperty.OVERRIDE_MODEL_LEFT_ARM);
        builder.add(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM);
        builder.add(SkinProperty.OVERRIDE_MODEL_LEFT_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_LEFT_FRONT_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_RIGHT_FRONT_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_LEFT_HIND_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_RIGHT_HIND_LEG);
        builder.add(SkinProperty.OVERRIDE_MODEL_TAIL);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_HAT);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_CLOAK);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_JACKET);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS);
        builder.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS);
        builder.add(SkinProperty.OVERRIDE_EQUIPMENT_HELMET);
        builder.add(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE);
        builder.add(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS);
        builder.add(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS);
    });

    private static final Map<ISkinProperty<Boolean>, EquipmentSlot> OVERRIDDEN_EQUIPMENT_TO_SLOT = Collections.immutableMap(builder -> {
        builder.put(SkinProperty.OVERRIDE_EQUIPMENT_HELMET, EquipmentSlot.HEAD);
        builder.put(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE, EquipmentSlot.CHEST);
        builder.put(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS, EquipmentSlot.LEGS);
        builder.put(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS, EquipmentSlot.FEET);
    });

    private static final Map<ISkinProperty<Boolean>, Collection<ISkinProperty<Boolean>>> OVERRIDDEN_MODEL_TO_OVERLAY = Collections.immutableMap(builder -> {
        builder.put(SkinProperty.OVERRIDE_MODEL_HEAD, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_HAT));
        builder.put(SkinProperty.OVERRIDE_MODEL_CHEST, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_JACKET, SkinProperty.OVERRIDE_OVERLAY_CLOAK));
        builder.put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE));
        builder.put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE));
        builder.put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS));
        builder.put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS));
    });

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

    public void addProperty(ISkinProperty<Boolean> property) {
        disabledProperties.add(property);
        // when equipment required hide, we need synchronize it to slot.
        var equipmentSlot = OVERRIDDEN_EQUIPMENT_TO_SLOT.get(property);
        if (equipmentSlot != null) {
            disabledEquipmentSlotsByProperties.add(equipmentSlot);
        }
        // when model part required hide, we need synchronize it to overlay.
        var overlayProperties = OVERRIDDEN_MODEL_TO_OVERLAY.get(property);
        if (overlayProperties != null) {
            disabledModelByProperties.add(property);
            disabledProperties.addAll(overlayProperties);
        }
    }

    public void merge(ISkinProperties properties) {
        for (var property : OVERRIDDEN_PROPERTIES) {
            if (properties.get(property)) {
                addProperty(property);
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

    public boolean overrideHandModel(OpenItemDisplayContext transformType) {
        if (transformType.isLeftHand()) {
            return contains(SkinProperty.OVERRIDE_MODEL_LEFT_ARM);
        }
        if (transformType.isRightHand()) {
            return contains(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM);
        }
        return false;
    }

    public void clear() {
        disabledProperties.clear();
        disabledModelByProperties.clear();
        disabledEquipmentSlots.clear();
        disabledEquipmentSlotsByProperties.clear();
    }

    public void willRender(T source) {
        for (var slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!overrideEquipment(slotType) || disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            var itemStack = setItem(source, slotType, ItemStack.EMPTY);
            disabledEquipmentItems.put(slotType, itemStack);
        }
    }

    public void didRender(T source) {
        for (var slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            var itemStack = disabledEquipmentItems.remove(slotType);
            setItem(source, slotType, itemStack);
        }
    }

    private ItemStack setItem(T source, EquipmentSlot slotType, ItemStack itemStack) {
        // for the player, using `setItemSlot` will cause play sound.
        if (source instanceof Player player) {
            var inventory = player.getInventory();
            var itemStack1 = inventory.armor.get(slotType.getIndex());
            inventory.armor.set(slotType.getIndex(), itemStack);
            return itemStack1;
        }
        if (source instanceof LivingEntity livingEntity) {
            var itemStack1 = livingEntity.getItemBySlot(slotType);
            livingEntity.setItemSlot(slotType, itemStack);
            return itemStack1;
        }
        return itemStack;
    }
}
