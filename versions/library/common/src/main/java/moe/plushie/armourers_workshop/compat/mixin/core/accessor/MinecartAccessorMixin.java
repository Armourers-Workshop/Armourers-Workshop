package moe.plushie.armourers_workshop.compat.mixin.core.accessor;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.entity.MinecartAccessor;
import net.minecraft.world.entity.vehicle.Minecart;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, 26)")
@Mixin(Minecart.class)
public abstract class MinecartAccessorMixin implements MinecartAccessor {
}
