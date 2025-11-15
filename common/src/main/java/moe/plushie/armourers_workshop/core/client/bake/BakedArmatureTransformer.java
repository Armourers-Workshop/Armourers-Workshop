package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJointFilter;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformer;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerContext;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;

public class BakedArmatureTransformer {

    public static final BakedArmatureTransformer EMPTY = new BakedArmatureTransformer();

    private final Armature armature;
    private final ArmatureTransformer armatureTransformer;
    private final IJointTransform[] transforms;
    private final ArrayList<ArmaturePlugin> plugins = new ArrayList<>();

    private IJointFilter filter;

    private BakedArmatureTransformer() {
        this.armature = null;
        this.armatureTransformer = null;
        this.transforms = null;
    }

    public BakedArmatureTransformer(ArmatureTransformer armatureTransformer) {
        this.armature = armatureTransformer.armature();
        this.armatureTransformer = armatureTransformer;
        this.transforms = armatureTransformer.transforms();
    }

    public static BakedArmatureTransformer create(ArmatureTransformer transformer, IEntityRenderer<?, ?> entityRenderer) {
        if (transformer == null) {
            return null;
        }
        var context = transformer.context();
        var plugins = Collections.newList(transformer.plugins());
        context.setEntityRenderer(entityRenderer);
        // we need tried load entity model from entity renderer.
        if (context.entityModel() == null && entityRenderer instanceof IEntityModel.Provider<?> provider) {
            context.setEntityModel(provider.abi$getEntityModel(null));
        }
        plugins.removeIf(plugin -> !plugin.freeze());
        var armatureTransformer1 = new BakedArmatureTransformer(transformer);
        armatureTransformer1.setPlugins(plugins);
        return armatureTransformer1;
    }


    public void prepare(EntityRenderState renderState, Entity entity, float partialTicks) {
        for (var plugin : plugins) {
            plugin.prepare(renderState, entity, partialTicks);
        }
    }

    public void activate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        for (var plugin : plugins) {
            plugin.activate(renderState, lightmap, overlay, context);
        }
    }

    public void deactivate(EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        for (var plugin : plugins) {
            plugin.deactivate(renderState, lightmap, overlay, context);
        }
    }

    public void applyTo(BakedArmature bakedArmature) {
        // safe updates
        if (bakedArmature.armature() == armature) {
            bakedArmature.setFilter(filter);
            bakedArmature.seTransforms(transforms);
        }
    }

    public void setPlugins(Collection<ArmaturePlugin> plugins) {
        this.plugins.clear();
        this.plugins.addAll(plugins);
    }

    public Collection<ArmaturePlugin> plugins() {
        return plugins;
    }

    public void setFilter(IJointFilter filter) {
        this.filter = filter;
    }

    public IJointFilter filter() {
        return filter;
    }

    public ArmatureTransformer transformer() {
        return armatureTransformer;
    }

    public Armature armature() {
        return armature;
    }

    public ArmatureTransformerContext context() {
        return armatureTransformer.context();
    }
}
