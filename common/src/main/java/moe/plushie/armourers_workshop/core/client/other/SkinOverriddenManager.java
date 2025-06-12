package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    private static final List<OpenEquipmentSlot> ARMOUR_EQUIPMENT_SLOTS = Collections.immutableList(it -> {
        it.add(OpenEquipmentSlot.HEAD);
        it.add(OpenEquipmentSlot.CHEST);
        it.add(OpenEquipmentSlot.LEGS);
        it.add(OpenEquipmentSlot.FEET);
    });

    private static final List<ISkinProperty<Boolean>> OVERRIDDEN_PROPERTIES = Collections.immutableList(it -> {
        it.add(SkinProperty.OVERRIDE_MODEL_HEAD);
        it.add(SkinProperty.OVERRIDE_MODEL_CHEST);
        it.add(SkinProperty.OVERRIDE_MODEL_LEFT_ARM);
        it.add(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM);
        it.add(SkinProperty.OVERRIDE_MODEL_LEFT_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_LEFT_FRONT_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_RIGHT_FRONT_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_LEFT_HIND_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_RIGHT_HIND_LEG);
        it.add(SkinProperty.OVERRIDE_MODEL_TAIL);
        it.add(SkinProperty.OVERRIDE_OVERLAY_HAT);
        it.add(SkinProperty.OVERRIDE_OVERLAY_CLOAK);
        it.add(SkinProperty.OVERRIDE_OVERLAY_JACKET);
        it.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE);
        it.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE);
        it.add(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS);
        it.add(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS);
        it.add(SkinProperty.OVERRIDE_EQUIPMENT_HELMET);
        it.add(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE);
        it.add(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS);
        it.add(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS);
    });

    private static final Map<ISkinProperty<Boolean>, OpenEquipmentSlot> OVERRIDDEN_EQUIPMENT_TO_SLOT = Collections.immutableMap(it -> {
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_HELMET, OpenEquipmentSlot.HEAD);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE, OpenEquipmentSlot.CHEST);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS, OpenEquipmentSlot.LEGS);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS, OpenEquipmentSlot.FEET);
    });

    private static final Map<ISkinProperty<Boolean>, Collection<ISkinProperty<Boolean>>> OVERRIDDEN_MODEL_TO_OVERLAY = Collections.immutableMap(it -> {
        it.put(SkinProperty.OVERRIDE_MODEL_HEAD, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_HAT));
        it.put(SkinProperty.OVERRIDE_MODEL_CHEST, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_JACKET, SkinProperty.OVERRIDE_OVERLAY_CLOAK));
        it.put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE));
        it.put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE));
        it.put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS));
        it.put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS));
    });

    private final HashSet<ISkinProperty<Boolean>> disabledProperties = new HashSet<>();
    private final HashSet<ISkinProperty<Boolean>> disabledModelByProperties = new HashSet<>();

    private final HashSet<OpenEquipmentSlot> disabledEquipmentSlots = new HashSet<>();
    private final HashSet<OpenEquipmentSlot> disabledEquipmentSlotsByProperties = new HashSet<>();

    private final HashMap<OpenEquipmentSlot, ItemStack> disabledEquipmentItems = new HashMap<>();

    public void addEquipment(OpenEquipmentSlot slotType) {
        disabledEquipmentSlots.add(slotType);
    }

    public void removeEquipment(OpenEquipmentSlot slotType) {
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
    public boolean overrideEquipment(OpenEquipmentSlot slotType) {
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
        for (var equipmentSlot : ARMOUR_EQUIPMENT_SLOTS) {
            if (!overrideEquipment(equipmentSlot) || disabledEquipmentItems.containsKey(equipmentSlot)) {
                continue;
            }
            var itemStack = setItem(source, equipmentSlot, ItemStack.EMPTY);
            disabledEquipmentItems.put(equipmentSlot, itemStack);
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

    private ItemStack setItem(T source, OpenEquipmentSlot slotType, ItemStack itemStack) {
        // for the player, using `setItemSlot` will cause play sound.
        if (source instanceof Player player) {
            var itemStack1 = player.getItemBySlot(slotType);
            player.setItemSlotDirect(slotType, itemStack1);
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
