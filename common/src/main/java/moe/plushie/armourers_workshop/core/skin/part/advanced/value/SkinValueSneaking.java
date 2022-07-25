package moe.plushie.armourers_workshop.core.skin.part.advanced.value;

import moe.plushie.armourers_workshop.core.registry.AdvancedSkinRegistry;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class SkinValueSneaking extends AdvancedSkinRegistry.AdvancedSkinValue {

    public SkinValueSneaking() {
        super("sneaking");
    }

    @Override
    public float getValue(Level world, Entity entity, Skin skin, SkinPart skinPart) {
        if (entity != null) {
            if (entity.isShiftKeyDown()) {
                return 1F;
            }
        }
        return 0F;
    }
}
