package moe.plushie.armourers_workshop.compat.forge.mixin.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.blockentity.AbstractBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Available("[21, )")
@Mixin(AbstractBlockEntity.class)
public abstract class ForgeBlockEntityCapabilityMixin  {
}
