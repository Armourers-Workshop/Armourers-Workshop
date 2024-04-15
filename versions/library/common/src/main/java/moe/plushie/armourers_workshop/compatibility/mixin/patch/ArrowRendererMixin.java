package moe.plushie.armourers_workshop.compatibility.mixin.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.core.client.model.SinglePlaceholderModel;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, )")
@Mixin(ArrowRenderer.class)
public class ArrowRendererMixin<T extends AbstractArrow> implements IModelProvider<T> {

    @Unique
    private final SinglePlaceholderModel aw2$transformModel = new SinglePlaceholderModel();


    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    public void aw2$willRender(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPre(entity, partialTicks, poseStack, buffers, light);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.BEFORE))
    public void aw2$render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntity(entity, partialTicks, poseStack, buffers, light);
        if (!aw2$transformModel.isVisible()) {
            poseStack.scale(0, 0, 0);
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/AbstractArrow;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    public void aw2$didRender(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPost(entity, partialTicks, poseStack, buffers, light);
    }

    @Override
    public IModel getModel(T entity) {
        return aw2$transformModel;
    }
}
