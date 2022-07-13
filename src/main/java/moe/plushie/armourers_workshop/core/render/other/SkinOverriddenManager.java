package moe.plushie.armourers_workshop.core.render.other;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.HashSet;

@OnlyIn(Dist.CLIENT)
public class SkinOverriddenManager {

    // Convert the part to equipment slot
    private static final ImmutableMap<ISkinPartType, EquipmentSlotType> PART_TO_EQUIPMENT_SLOTS = new ImmutableMap.Builder<ISkinPartType, EquipmentSlotType>()
            .put(SkinPartTypes.BIPED_HEAD, EquipmentSlotType.HEAD)
            .put(SkinPartTypes.BIPED_CHEST, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_RIGHT_ARM, EquipmentSlotType.CHEST)
            .put(SkinPartTypes.BIPED_LEFT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_RIGHT_FOOT, EquipmentSlotType.FEET)
            .put(SkinPartTypes.BIPED_LEFT_LEG, EquipmentSlotType.LEGS)
            .put(SkinPartTypes.BIPED_RIGHT_LEG, EquipmentSlotType.LEGS)
            .build();

    private static final ImmutableList<EquipmentSlotType> ARMOUR_EQUIPMENT_SLOTS = new ImmutableList.Builder<EquipmentSlotType>()
            .add(EquipmentSlotType.HEAD)
            .add(EquipmentSlotType.CHEST)
            .add(EquipmentSlotType.LEGS)
            .add(EquipmentSlotType.FEET)
            .build();

    private final HashSet<ISkinPartType> disabledModels = new HashSet<>();
    private final HashSet<ISkinPartType> disabledOverlays = new HashSet<>();

    private final HashSet<EquipmentSlotType> disabledEquipmentSlots = new HashSet<>();
    private final HashSet<EquipmentSlotType> disabledEquipmentSlotsByPart = new HashSet<>();

    private final HashMap<EquipmentSlotType, ItemStack> disabledEquipmentItems = new HashMap<>();

    public void addModel(ISkinPartType partType) {
        disabledModels.add(partType);
        disabledEquipmentSlotsByPart.add(getEquipmentSlot(partType));
    }

    public void addOverlay(ISkinPartType partType) {
        disabledOverlays.add(partType);
        disabledEquipmentSlotsByPart.add(getEquipmentSlot(partType));
    }

    public void addEquipment(EquipmentSlotType slotType) {
        disabledEquipmentSlots.add(slotType);
    }

    public void removeEquipment(EquipmentSlotType slotType) {
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
    public boolean overrideEquipment(EquipmentSlotType slotType) {
        return disabledEquipmentSlotsByPart.contains(slotType)
                || disabledEquipmentSlots.contains(slotType);
    }

    public void clear() {
        disabledModels.clear();
        disabledOverlays.clear();
        disabledEquipmentSlots.clear();
        disabledEquipmentSlotsByPart.clear();
    }

    public void willRender(Entity entity) {
        for (EquipmentSlotType slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!overrideEquipment(slotType) || disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            ItemStack itemStack = setItem(entity, slotType, ItemStack.EMPTY);
            disabledEquipmentItems.put(slotType, itemStack);
        }
    }

    public void didRender(Entity entity) {
        for (EquipmentSlotType slotType : ARMOUR_EQUIPMENT_SLOTS) {
            if (!disabledEquipmentItems.containsKey(slotType)) {
                continue;
            }
            ItemStack itemStack = disabledEquipmentItems.remove(slotType);
            setItem(entity, slotType, itemStack);
        }
    }

    private ItemStack setItem(Entity entity, EquipmentSlotType slotType, ItemStack itemStack) {
        // for the player, using setItemSlot will cause play sound.
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            ItemStack itemStack1 = player.inventory.armor.get(slotType.getIndex());
            player.inventory.armor.set(slotType.getIndex(), itemStack);
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

    private EquipmentSlotType getEquipmentSlot(ISkinPartType partType) {
        return PART_TO_EQUIPMENT_SLOTS.getOrDefault(partType, EquipmentSlotType.OFFHAND);
    }
}
