package moe.plushie.armourers_workshop.compat.forge.mixin.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.platform.forge.addon.TravelersBackpackAddon;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.21, 1.22)")
@Pseudo
@Mixin(BackpackLayer.class)
public class ForgeTravelersBackpackRendererMixin {

    @Inject(method = "renderBackpackLayer", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void aw2$renderBackpack(HumanoidModel<?> humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack, CallbackInfo ci) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null && renderData.overriddenManager().contains(SkinProperty.OVERRIDE_MODEL_BACKPACK)) {
            ci.cancel();
        }
    }

    static {
        TravelersBackpackAddon.register(AttachmentUtils::getWearingBackpack);
    }
}
