package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStroage;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
@Available("[1.20, )")
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void aw2$getModel(ItemStack itemStack, Level level, LivingEntity entity, int i, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        EmbeddedSkinStack embeddedStack = ClientWardrobeHandler.getEmbeddedSkinStack(entity, level, itemStack, null);
        if (embeddedStack != null) {
            cir.setReturnValue(BakedModelStroage.wrap(bakedModel, itemStack, embeddedStack, entity, level));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void aw2$render(ItemStack itemStack, ItemDisplayContext transformType, boolean p_229111_3_, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay, BakedModel bakedModel, CallbackInfo ci) {
        BakedModelStroage stroage = BakedModelStroage.unwrap(bakedModel);
        if (stroage == null) {
            return;
        }
        BakedModel resolvedModel = stroage.getOriginModel();
        LivingEntity entity = stroage.getEntity();
        Level level = stroage.getLevel();
        EmbeddedSkinStack embeddedStack = stroage.getEmbeddedStack();
        ClientWardrobeHandler.renderEmbeddedSkin(entity, level, itemStack, embeddedStack, ItemTransforms.ofType(transformType), p_229111_3_, poseStack, buffers, resolvedModel, packedLight, overlay, ci);
    }
}
