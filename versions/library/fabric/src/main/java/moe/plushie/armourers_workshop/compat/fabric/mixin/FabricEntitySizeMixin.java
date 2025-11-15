package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.platform.fabric.event.EntityLifecycleEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[1.16, )")
@Mixin(Entity.class)
public abstract class FabricEntitySizeMixin {

    @ModifyVariable(method = "refreshDimensions", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"), ordinal = 1)
    private EntityDimensions aw2$refreshDimensions(EntityDimensions dimensions) {
        var entity = Entity.class.cast(this);
        return EntityLifecycleEvents.SIZE.invoker().resize(entity, entity.getPose(), dimensions, dimensions);
    }
}
