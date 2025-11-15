package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class SkinOverriddenManager {

    private static final List<OpenEquipmentSlot> ARMOUR_EQUIPMENT_SLOTS = Collections.immutableList(it -> {
        it.add(OpenEquipmentSlot.HEAD);
        it.add(OpenEquipmentSlot.CHEST);
        it.add(OpenEquipmentSlot.LEGS);
        it.add(OpenEquipmentSlot.FEET);
    });

    private static final List<SkinProperty<Boolean>> OVERRIDDEN_PROPERTIES = Collections.immutableList(it -> {
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

    private static final Map<SkinProperty<Boolean>, OpenEquipmentSlot> OVERRIDDEN_EQUIPMENT_TO_SLOT = Collections.immutableMap(it -> {
        it.put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, OpenEquipmentSlot.OFFHAND);
        it.put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, OpenEquipmentSlot.MAINHAND);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_HELMET, OpenEquipmentSlot.HEAD);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE, OpenEquipmentSlot.CHEST);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS, OpenEquipmentSlot.LEGS);
        it.put(SkinProperty.OVERRIDE_EQUIPMENT_BOOTS, OpenEquipmentSlot.FEET);
    });

    private static final Map<SkinProperty<Boolean>, Collection<SkinProperty<Boolean>>> OVERRIDDEN_MODEL_TO_OVERLAY = Collections.immutableMap(it -> {
        it.put(SkinProperty.OVERRIDE_MODEL_HEAD, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_HAT));
        it.put(SkinProperty.OVERRIDE_MODEL_CHEST, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_JACKET, SkinProperty.OVERRIDE_OVERLAY_CLOAK));
        it.put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE));
        it.put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE));
        it.put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS));
        it.put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, Collections.newList(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS));
    });

    private final HashSet<SkinProperty<Boolean>> disabledProperties = new HashSet<>();
    private final HashSet<SkinProperty<Boolean>> disabledModelByProperties = new HashSet<>();

    private final HashSet<OpenEquipmentSlot> disabledEquipmentSlots = new HashSet<>();
    private final HashSet<OpenEquipmentSlot> disabledEquipmentSlotsByProperties = new HashSet<>();

    public void addEquipment(OpenEquipmentSlot slotType) {
        disabledEquipmentSlots.add(slotType);
    }

    public void removeEquipment(OpenEquipmentSlot slotType) {
        disabledEquipmentSlots.remove(slotType);
    }

    public void addProperty(SkinProperty<Boolean> property) {
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

    public void merge(SkinProperties properties) {
        for (var property : OVERRIDDEN_PROPERTIES) {
            if (properties.get(property)) {
                addProperty(property);
            }
        }
    }

    public boolean contains(SkinProperty<Boolean> property) {
        return disabledProperties.contains(property);
    }

    // if it returns true, it means equipment is overwritten.
    public boolean shouldOverrideEquipment(OpenEquipmentSlot slotType) {
        return disabledEquipmentSlots.contains(slotType) || disabledEquipmentSlotsByProperties.contains(slotType);
    }

    public boolean shouldOverrideAnyModel() {
        return !disabledModelByProperties.isEmpty();
    }

    public void clear() {
        disabledProperties.clear();
        disabledModelByProperties.clear();
        disabledEquipmentSlots.clear();
        disabledEquipmentSlotsByProperties.clear();
    }
}
