package moe.plushie.armourers_workshop.compatibility.fabric.mixin.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.platform.fabric.addon.TravelersBackpackAddon;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.function.Function;

@Available("[1.20, )")
@Pseudo
@Mixin(TravelersBackpackFeature.class)
public class FabricTravelersBackpackRendererMixin {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void aw2$renderBackpack(PoseStack matrices, MultiBufferSource vertexConsumers, int light, AbstractClientPlayer entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null && renderData.getOverriddenManager().contains(SkinProperty.OVERRIDE_MODEL_BACKPACK)) {
            ci.cancel();
        }
    }

    static {
        // because the superclass of EntityComponentInitializer is an unknown type,
        // this leads we can't direct using the ComponentUtils api.
        // so we can only call it through reflection.
        Method[] methods = {null};
        Function<Player, ItemStack> getWearingBackpack = (player) -> {
            try {
                if (methods[0] == null) {
                    methods[0] = ComponentUtils.class.getDeclaredMethod("getWearingBackpack", Player.class);
                }
                return (ItemStack) methods[0].invoke(ComponentUtils.class, player);
            } catch (Exception e) {
                return ItemStack.EMPTY;
            }
        };
        TravelersBackpackAddon.register(getWearingBackpack);
    }
}
