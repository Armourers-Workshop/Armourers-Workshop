package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Inject(method = "put", at = @At(value = "RETURN"))
    private static void hooked_put(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> fixedBuffers, RenderType renderType, CallbackInfo callbackInfo) {
        if (renderType == RenderType.armorEntityGlint()) {
            fixedBuffers.put(SkinVertexBufferBuilder.MERGER, new SkinVertexBufferBuilder());
        }
    }
}