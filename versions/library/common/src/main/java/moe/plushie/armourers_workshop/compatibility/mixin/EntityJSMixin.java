package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.data.SkinDataStorage;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Available("[1.19, )")
@Mixin(Entity.class)
public class EntityJSMixin {

    @Unique
    public SkinWardrobeJS getWardrobe() {
        return SkinDataStorage.getWardrobeJS(ObjectUtils.unsafeCast(this)).orElse(null);
    }
}
