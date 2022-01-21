package moe.plushie.armourers_workshop.core.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;

public class SkinMannequinEntity extends LivingEntity {

    public SkinMannequinEntity() {
        super(EntityType.ARMOR_STAND, null);
    }


    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return null;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slotType) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotType, ItemStack itemStack) {
    }

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }
}
