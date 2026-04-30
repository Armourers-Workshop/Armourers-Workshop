package moe.plushie.armourers_workshop.compat.mixin.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

@Available("[16, 26)")
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRendererMixin {
    // nop
}
