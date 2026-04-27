package moe.plushie.armourers_workshop.compat.mixin.bugfix;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.compat.client.renderer.AbstractRenderPipeline;
import moe.plushie.armourers_workshop.core.utils.FieldAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[21, 26)")
@Conditional("iris >= 1.8.12-snapshot")
@Mixin(AbstractRenderPipeline.class)
public class MergeRenderingMixin {

    @Unique
    private static final FieldAccessor<Boolean> MERGE_RENDERING = FieldAccessor.create("net.irisshaders.iris.vertices.ImmediateState.mergeRendering");

    @Inject(method = "endBatch", at = @At("HEAD"), remap = false)
    private void aw2$endBatchPre(CallbackInfo ci) {
        MERGE_RENDERING.set(false);
    }
}
