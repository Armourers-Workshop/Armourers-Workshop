package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.18, )")
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", shift = At.Shift.BEFORE))
    private void aw2$renderPre(Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPre(entity, g, h, poseStack, buffers, i);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", shift = At.Shift.AFTER))
    private void aw2$renderPost(Entity entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPost(entity, g, h, poseStack, buffers, i);
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void aw2$reloadResources(ResourceManager resourceManager, CallbackInfo ci) {
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> SkinRendererManager::reload);
    }
}
