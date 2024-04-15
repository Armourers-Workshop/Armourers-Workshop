package moe.plushie.armourers_workshop.compatibility.mixin.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Available("[1.20, )")
@Mixin(BoatRenderer.class)
public class BoatRendererMixin<T extends Boat> implements IModelProvider<T> {

    private Map<Boat.Type, IModel> aw2$boatModels = new HashMap<>();

    @Shadow
    @Final
    private Map<Boat.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "HEAD"))
    public void aw2$willRender(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPre(entity, partialTicks, poseStack, buffers, light);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ListModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V", shift = At.Shift.AFTER))
    private void aw2$render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource buffers, int i, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntity(entity, g, poseStack, buffers, i);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "RETURN"))
    public void aw2$didRender(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        ClientWardrobeHandler.onRenderEntityPost(entity, partialTicks, poseStack, buffers, light);
    }

    @Unique
    @Override
    public IModel getModel(T entity) {
        return aw2$boatModels.computeIfAbsent(entity.getVariant(), it -> {
            Model model = boatResources.get(it).getSecond();
            return ModelHolder.of(model);
        });
    }
}
