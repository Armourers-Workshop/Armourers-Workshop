package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStroage;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
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
    //#if MC >= 11800
    private void aw2$getModel(ItemStack itemStack, Level level, LivingEntity entity, int i, CallbackInfoReturnable<BakedModel> cir)
    //#else
    //# private void aw2$getModel(ItemStack itemStack, Level level, LivingEntity entity, CallbackInfoReturnable<BakedModel> cir)
    //#endif
    {
        BakedModel bakedModel = cir.getReturnValue();
        EmbeddedSkinStack embeddedStack = ClientWardrobeHandler.getEmbeddedSkinStack(entity, level, itemStack, null);
        if (embeddedStack != null) {
            cir.setReturnValue(BakedModelStroage.wrap(bakedModel, itemStack, embeddedStack, entity, level));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void aw2$render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean p_229111_3_, PoseStack poseStackIn, MultiBufferSource buffers, int packedLight, int overlay, BakedModel bakedModel, CallbackInfo ci) {
        BakedModelStroage stroage = BakedModelStroage.unwrap(bakedModel);
        if (stroage == null) {
            return;
        }
        BakedModel resolvedModel = stroage.getOriginModel();
        LivingEntity entity = stroage.getEntity();
        Level level = stroage.getLevel();
        IPoseStack poseStack = MatrixUtils.of(poseStackIn);
        EmbeddedSkinStack embeddedStack = stroage.getEmbeddedStack();
        ClientWardrobeHandler.renderEmbeddedSkin(entity, level, itemStack, embeddedStack, transformType, p_229111_3_, poseStack, buffers, resolvedModel, packedLight, overlay, ci);
    }
}
