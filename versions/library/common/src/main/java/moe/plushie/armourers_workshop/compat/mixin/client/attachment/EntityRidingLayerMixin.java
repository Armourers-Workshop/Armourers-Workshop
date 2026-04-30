package moe.plushie.armourers_workshop.compat.mixin.client.attachment;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Available("[21, )")
@Mixin(Entity.class)
public class EntityRidingLayerMixin {

    @ModifyVariable(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getPassengerRidingPosition(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/Vec3;", shift = At.Shift.AFTER))
    private Vec3 aw2$positionRider(Vec3 pos, Entity passenger, Entity.MoveFunction moveFunction) {
        var entity = Entity.class.cast(this);
        var level = entity.level();
        if (!level.isClientSide()) {
            return pos; // only work client side
        }
        var index = entity.getPassengers().indexOf(passenger);
        var offset = entity.getCustomRidding(index);
        if (offset == null) {
            return pos; // can't found custom riding position.
        }
        return entity.position().add(offset.x(), offset.y(), offset.z());
    }
}
