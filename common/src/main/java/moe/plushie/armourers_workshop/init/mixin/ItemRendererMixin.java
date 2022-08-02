package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStroage;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void hooked_getModel(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        if (ClientWardrobeHandler.shouldRenderEmbeddedSkin(entity, level, itemStack, false)) {
            cir.setReturnValue(BakedModelStroage.wrap(bakedModel, itemStack, entity, level));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hooked_render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean p_229111_3_, PoseStack matrixStack, MultiBufferSource buffers, int packedLight, int overlay, BakedModel bakedModel, CallbackInfo ci) {
        BakedModelStroage stroage = BakedModelStroage.unwrap(bakedModel);
        if (stroage == null) {
            return;
        }
        BakedModel resolvedModel = stroage.getOriginModel();
        LivingEntity entity = stroage.getEntity();
        Level level = stroage.getLevel();
        ClientWardrobeHandler.onRenderEmbeddedSkin(entity, level, itemStack, transformType, p_229111_3_, matrixStack, buffers, resolvedModel, packedLight, overlay, ci);
    }
}