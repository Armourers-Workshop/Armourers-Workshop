package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.19, )")
@Mixin(Entity.class)
public class EntityJSMixin {

    private SkinWardrobeJS aw$wardrobe;

    public SkinWardrobeJS getWardrobe() {
        if (aw$wardrobe == null) {
            aw$wardrobe = new SkinWardrobeJS(ObjectUtils.unsafeCast(this));
        }
        return aw$wardrobe;
    }
}
