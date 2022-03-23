package moe.plushie.armourers_workshop.client.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinVertexBufferBuilder2;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderTypeBuffers.class)
public class RenderTypeBuffersMixin {

    @Inject(method = "put", at = @At(value = "RETURN"))
    private static void hooked_put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> fixedBuffers, RenderType renderType, CallbackInfo callbackInfo) {
        if (renderType == RenderType.armorEntityGlint()) {
            fixedBuffers.put(SkinVertexBufferBuilder2.MERGER, new SkinVertexBufferBuilder2());
        }
    }
}
