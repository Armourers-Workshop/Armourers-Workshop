package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot;

@Available("[1.21, )")
public class AbstractEquipmentSlot {

    private static final FastMapper<OpenEquipmentSlot, EquipmentSlot> MAPPER = FastMapper.builder(OpenEquipmentSlot.MAINHAND, EquipmentSlot.MAINHAND, it -> {
        it.put(OpenEquipmentSlot.MAINHAND, EquipmentSlot.MAINHAND);
        it.put(OpenEquipmentSlot.OFFHAND, EquipmentSlot.OFFHAND);
        it.put(OpenEquipmentSlot.FEET, EquipmentSlot.FEET);
        it.put(OpenEquipmentSlot.LEGS, EquipmentSlot.LEGS);
        it.put(OpenEquipmentSlot.CHEST, EquipmentSlot.CHEST);
        it.put(OpenEquipmentSlot.HEAD, EquipmentSlot.HEAD);
        it.put(OpenEquipmentSlot.BODY, EquipmentSlot.BODY);
    });

    public static OpenEquipmentSlot wrap(EquipmentSlot equipmentSlot) {
        return MAPPER.getKey(equipmentSlot);
    }

    public static EquipmentSlot unwrap(OpenEquipmentSlot equipmentSlot) {
        return MAPPER.getValue(equipmentSlot);
    }
}
