package moe.plushie.armourers_workshop.client.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedArmorLayer.class)
public class BipedArmorLayerMixin<T extends LivingEntity, A extends BipedModel<T>> {

    @Inject(method = "renderArmorPiece", at = @At("HEAD"), cancellable = true)
    private void hooked_renderArmorPiece(MatrixStack matrixStack, IRenderTypeBuffer renderType, T entity, EquipmentSlotType slotType, int p_241739_5_, A p_241739_6_, CallbackInfo callback) {
        ClientWardrobeHandler.onRenderEquipment(entity, slotType, matrixStack, renderType, callback);
    }
}