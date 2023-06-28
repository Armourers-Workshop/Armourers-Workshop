package moe.plushie.armourers_workshop.compatibility.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStorage;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
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

@Available("[1.18, 1.20)")
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void aw2$getModel(ItemStack itemStack, Level level, LivingEntity entity, int i, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel = cir.getReturnValue();
        EmbeddedSkinStack embeddedStack = ClientWardrobeHandler.getEmbeddedSkinStack(entity, level, itemStack, null);
        if (embeddedStack != null) {
            cir.setReturnValue(BakedModelStorage.wrap(bakedModel, itemStack, embeddedStack, entity, level));
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void aw2$render(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean p_229111_3_, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay, BakedModel bakedModel, CallbackInfo ci) {
        BakedModelStorage storage = BakedModelStorage.unwrap(bakedModel);
        if (storage == null) {
            return;
        }
        BakedModel resolvedModel = storage.getOriginModel();
        LivingEntity entity = storage.getEntity();
        Level level = storage.getLevel();
        EmbeddedSkinStack embeddedStack = storage.getEmbeddedStack();
        ClientWardrobeHandler.renderEmbeddedSkin(entity, level, itemStack, embeddedStack, ItemTransforms.ofType(transformType), p_229111_3_, poseStack, buffers, resolvedModel, packedLight, overlay, ci);
    }

    @Inject(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void aw2$renderGuiItemPre(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        ClientWardrobeHandler.startRenderGuiItem(itemStack);
    }

    @Inject(method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;II)V", at = @At("RETURN"))
    private void aw2$renderGuiItemPost(ItemStack itemStack, int i, int j, CallbackInfo ci) {
        ClientWardrobeHandler.endRenderGuiItem(itemStack);
    }

    @Inject(method = "tryRenderGuiItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"))
    private void aw2$tryRenderGuiItemPre(LivingEntity livingEntity, ItemStack itemStack, int i, int j, int k, int l, CallbackInfo ci) {
        ClientWardrobeHandler.startRenderGuiItem(itemStack);
    }

    @Inject(method = "tryRenderGuiItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", at = @At("RETURN"))
    private void aw2$tryRenderGuiItemPost(LivingEntity livingEntity, ItemStack itemStack, int i, int j, int k, CallbackInfo ci) {
        ClientWardrobeHandler.endRenderGuiItem(itemStack);
    }
}
