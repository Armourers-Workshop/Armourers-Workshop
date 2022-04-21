package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    private static boolean isRenderInGUI = false;

    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private ItemStack hooked_renderSkinStackForModel(ItemStack oldValue) {
        return ClientWardrobeHandler.getRenderSkinStack(oldValue, isRenderInGUI);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private ItemStack hooked_renderSkinStack(ItemStack oldValue) {
        return ClientWardrobeHandler.getRenderSkinStack(oldValue, isRenderInGUI);
    }

    @Inject(method = "renderStatic(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/world/World;II)V", at = @At("HEAD"), cancellable = true)
    private void hooked_renderStatic(@Nullable LivingEntity entity, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, boolean p_229109_4_, MatrixStack matrixStack, IRenderTypeBuffer buffers, @Nullable World world, int packedLight, int p_229109_9_, CallbackInfo callback) {
        ClientWardrobeHandler.onRenderSkinStack(entity, itemStack, transformType, p_229109_4_, matrixStack, buffers, world, packedLight, p_229109_9_, callback);
    }

    // for disable render skin in the GUI, we need tow hooks for private methods.
    // this raises the upgrade risk, that we may need to find a better way.

    @Inject(method = "tryRenderGuiItem", at = @At("HEAD"))
    private void hooked_renderGuiItemPre(@Nullable LivingEntity p_239387_1_, ItemStack p_239387_2_, int p_239387_3_, int p_239387_4_, CallbackInfo callback) {
        isRenderInGUI = true;
    }

    @Inject(method = "tryRenderGuiItem", at = @At("RETURN"))
    private void hooked_renderGuiItemPost(@Nullable LivingEntity p_239387_1_, ItemStack p_239387_2_, int p_239387_3_, int p_239387_4_, CallbackInfo callback) {
        isRenderInGUI = false;
    }
}
