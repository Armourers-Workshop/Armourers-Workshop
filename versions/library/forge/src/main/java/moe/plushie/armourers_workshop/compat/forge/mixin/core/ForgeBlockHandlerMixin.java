package moe.plushie.armourers_workshop.compat.forge.mixin.core;

import moe.plushie.armourers_workshop.compat.core.block.AbstractBlock;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.class)
public abstract class ForgeBlockHandlerMixin implements AbstractForgeBlock {
}
