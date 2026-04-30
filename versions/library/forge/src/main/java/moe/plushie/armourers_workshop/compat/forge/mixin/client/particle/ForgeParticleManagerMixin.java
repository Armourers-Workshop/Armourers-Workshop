package moe.plushie.armourers_workshop.compat.forge.mixin.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleInstance;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleManager;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleManagerImpl;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Available("[21, 26)")
@Mixin(ParticleEngine.class)
public class ForgeParticleManagerMixin implements AbstractParticleManagerImpl {

    private final AbstractParticleManager aw2$particleManager = new AbstractParticleManager();

    @Inject(method = "tick", at = @At("HEAD"))
    private void aw2$tick(CallbackInfo ci) {
        aw2$particleManager.tick();
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"))
    private void aw2$render(LightTexture lightTexture, Camera camera, float f, Frustum frustum, Predicate<ParticleRenderType> predicate, CallbackInfo ci) {
        var tesselator = AbstractBufferSource.tesselator();
        aw2$particleManager.render(AbstractCamera.wrap(camera), f, AbstractGraphicsRenderer.wrap(new PoseStack(), tesselator.bufferSource()));
        tesselator.endBatch();
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void aw2$changeLevel(CallbackInfo ci) {
        aw2$particleManager.clear();
    }

    @Override
    public void aw2$add(AbstractParticleInstance particleInstance) {
        aw2$particleManager.add(particleInstance);
    }

    @Override
    public void aw2$remove(AbstractParticleInstance particleInstance) {
        aw2$particleManager.remove(particleInstance);
    }
}
