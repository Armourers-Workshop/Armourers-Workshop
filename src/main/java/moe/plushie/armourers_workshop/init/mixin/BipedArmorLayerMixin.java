package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedArmorLayer.class)
public class BipedArmorLayerMixin<T extends LivingEntity, A extends BipedModel<T>> {

    @Inject(method = "renderArmorPiece", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/renderer/entity/layers/BipedArmorLayer;setPartVisibility(Lnet/minecraft/client/renderer/entity/model/BipedModel;Lnet/minecraft/inventory/EquipmentSlotType;)V"), cancellable = true)
    private void hooked_renderArmorPiece(MatrixStack matrixStack, IRenderTypeBuffer renderType, T entity, EquipmentSlotType slotType, int p_241739_5_, A model, CallbackInfo callback) {
        ClientWardrobeHandler.onRenderArmorEquipment(entity, model, slotType, matrixStack, renderType, callback);
    }
}