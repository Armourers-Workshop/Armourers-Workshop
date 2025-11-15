package moe.plushie.armourers_workshop.compat.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractHumanoidModelProvider;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.20, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractHumanoidEntityRendererImpl<T extends LivingEntity, S extends LivingEntityRenderState, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends AbstractLivingEntityRenderer<T, S, M> implements IEntityRenderer<T, S> {

    protected final M slimModel;
    protected final M normalModel;

    protected final RenderLayer<T, M> slimArmorLayer;
    protected final RenderLayer<T, M> normalArmorLayer;

    protected AbstractHumanoidEntityRendererImpl(Context context, AbstractHumanoidModelProvider<M, A> provider, float shadowRadius) {
        super(context, provider.createEntityModel(ModelLayers.PLAYER), shadowRadius);
        this.slimModel = provider.createEntityModel(ModelLayers.PLAYER_SLIM);
        this.normalModel = super.model;
        this.slimArmorLayer = new HumanoidArmorLayer<>(this, provider.createArmourModel(ModelLayers.PLAYER_SLIM_INNER_ARMOR), provider.createArmourModel(ModelLayers.PLAYER_SLIM_OUTER_ARMOR), provider.context().getModelManager());
        this.normalArmorLayer = new HumanoidArmorLayer<>(this, provider.createArmourModel(ModelLayers.PLAYER_INNER_ARMOR), provider.createArmourModel(ModelLayers.PLAYER_OUTER_ARMOR), provider.context().getModelManager());
        // setup the default layer.
        this.addLayer(normalArmorLayer);
        this.addLayer(new ItemInHandLayer<>(this, provider.context().getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, provider.context().getModelSet()));
        this.addLayer(new CustomHeadLayer<>(this, provider.context().getModelSet(), provider.context().getItemInHandRenderer()));
    }

    public static EntityRendererProvider.Context getEmptyContext() {
        var minecraft = Minecraft.getInstance();
        return new EntityRendererProvider.Context(minecraft.getEntityRenderDispatcher(),
                minecraft.getItemRenderer(),
                minecraft.getBlockRenderer(),
                minecraft.getEntityRenderDispatcher().getItemInHandRenderer(),
                minecraft.getResourceManager(),
                minecraft.getEntityModels(),
                minecraft.font);
    }
}
