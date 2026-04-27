package moe.plushie.armourers_workshop.compat.mixin.patch;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.api.client.state.ILivingEntityRenderState;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Available("[16, )")
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends ILivingEntityRenderState, M extends IEntityModel<S>> implements ILivingEntityRenderer<T, S, M> {

    @Shadow
    @Final
    public List<Object> layers;

    @Shadow
    protected EntityModel<?> model;

    @Override
    public M abi$getModel() {
        return Objects.unsafeCast(model);
    }

    @Override
    public List<Object> abi$getLayers() {
        return layers;
    }
}
