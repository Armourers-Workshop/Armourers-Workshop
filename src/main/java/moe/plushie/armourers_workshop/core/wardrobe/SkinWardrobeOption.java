package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum SkinWardrobeOption {

    ARMOUR_HEAD(EquipmentSlotType.HEAD),
    ARMOUR_CHEST(EquipmentSlotType.CHEST),
    ARMOUR_LEGS(EquipmentSlotType.LEGS),
    ARMOUR_FEET(EquipmentSlotType.FEET),

    MANNEQUIN_IS_CHILD(MannequinEntity.Option.IS_CHILD),
    MANNEQUIN_IS_FLYING(MannequinEntity.Option.IS_FLYING),
    MANNEQUIN_IS_VISIBLE(MannequinEntity.Option.IS_VISIBLE),
    MANNEQUIN_NO_CLIP(MannequinEntity.Option.NO_CLIP),
    MANNEQUIN_EXTRA_RENDER(MannequinEntity.Option.EXTRA_RENDER);

    private final Function<SkinWardrobe, Boolean> getter;
    private final BiConsumer<SkinWardrobe, Boolean> setter;


    SkinWardrobeOption(EquipmentSlotType slotType) {
        this.getter = (wardrobe) -> wardrobe.shouldRenderEquipment(slotType);
        this.setter = (wardrobe, value) -> wardrobe.setRenderEquipment(slotType, value);
    }

    SkinWardrobeOption(MannequinEntity.Option option) {
        this.getter = (wardrobe) -> convert(wardrobe).map(e -> e.getOption(option)).orElse(false);
        this.setter = (wardrobe, value) -> convert(wardrobe).ifPresent(e -> e.setOption(option, value));
    }

    private static Optional<MannequinEntity> convert(SkinWardrobe wardrobe) {
        Entity entity = wardrobe.getEntity();
        if (entity instanceof MannequinEntity) {
            return Optional.of((MannequinEntity) entity);
        }
        return Optional.empty();
    }

    public boolean get(SkinWardrobe wardrobe) {
        return getter.apply(wardrobe);
    }

    public void set(SkinWardrobe wardrobe, boolean value) {
        setter.accept(wardrobe, value);
    }
}
