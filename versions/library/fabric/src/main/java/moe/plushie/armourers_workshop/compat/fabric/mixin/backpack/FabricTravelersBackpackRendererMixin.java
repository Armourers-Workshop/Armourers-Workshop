package moe.plushie.armourers_workshop.compat.fabric.mixin.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.init.platform.fabric.addon.TravelersBackpackAddon;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.function.Function;

@Available("[1.20, 1.22)")
@Pseudo
@Mixin(BackpackLayer.class)
public class FabricTravelersBackpackRendererMixin {

    @Inject(method = "renderBackpackLayer", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void aw2$renderBackpack(HumanoidModel<?> humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack, CallbackInfo ci) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null && renderData.overriddenManager().contains(SkinProperty.OVERRIDE_MODEL_BACKPACK)) {
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
