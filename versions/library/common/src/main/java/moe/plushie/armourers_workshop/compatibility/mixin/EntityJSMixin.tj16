package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.16, 1.19)")
@Pseudo
@Mixin(targets = {
        "dev.latvian.kubejs.entity.EntityJS",
        "dev.latvian.mods.kubejs.entity.EntityJS"
})
public class EntityJSMixin {

    private SkinWardrobeJS aw$skinWardrobe;

    @Shadow
    @Final
    public Entity minecraftEntity;

    public SkinWardrobeJS getWardrobe() {
        if (aw$skinWardrobe == null) {
            aw$skinWardrobe = new SkinWardrobeJS(minecraftEntity);
        }
        return aw$skinWardrobe;
    }
}
