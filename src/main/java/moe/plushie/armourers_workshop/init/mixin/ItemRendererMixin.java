package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.model.WrappedBakedModel;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void hooked_getModel(ItemStack itemStack, World world, LivingEntity entity, CallbackInfoReturnable<IBakedModel> cir) {
        IBakedModel bakedModel = cir.getReturnValue();
        if (ClientWardrobeHandler.shouldRenderEmbeddedSkin(entity, world, itemStack, false)) {
            cir.setReturnValue(WrappedBakedModel.wrap(bakedModel, itemStack, entity, world));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hooked_render(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, boolean p_229111_3_, MatrixStack matrixStack, IRenderTypeBuffer buffers, int p_229111_6_, int p_229111_7_, IBakedModel bakedModel, CallbackInfo ci) {
        if (!(bakedModel instanceof WrappedBakedModel)) {
            return;
        }
        WrappedBakedModel resolvedBakedModel = (WrappedBakedModel)bakedModel;
        LivingEntity entity = resolvedBakedModel.getEntity();
        World world = resolvedBakedModel.getWorld();
        ClientWardrobeHandler.onRenderEmbeddedSkin(entity, world, itemStack, transformType, p_229111_3_, matrixStack, buffers, bakedModel, p_229111_6_, p_229111_7_, ci);
    }
}
