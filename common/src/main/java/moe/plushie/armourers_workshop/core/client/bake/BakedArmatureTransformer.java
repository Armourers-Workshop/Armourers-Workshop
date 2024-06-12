package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.api.armature.IJointFilter;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.client.model.IModelProvider;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.ArmaturePlugin;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.render.EntityRendererStorage;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager2;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

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
        this.armature = armatureTransformer.getArmature();
        this.armatureTransformer = armatureTransformer;
        this.transforms = armatureTransformer.getTransforms();
    }

    public static BakedArmatureTransformer create(ArmatureTransformer transformer, EntityRenderer<?> entityRenderer) {
        if (transformer == null) {
            return null;
        }
        var context = transformer.getContext();
        var plugins = Lists.newArrayList(transformer.getPlugins());
        context.setEntityRenderer(entityRenderer);
        // we need tried load entity model from entity renderer.
        if (context.getEntityModel() == null && entityRenderer instanceof IModelProvider<?>) {
            context.setEntityModel(((IModelProvider<?>) entityRenderer).getModel(null));
        }
        plugins.removeIf(plugin -> !plugin.freeze());
        var armatureTransformer1 = new BakedArmatureTransformer(transformer);
        armatureTransformer1.setPlugins(plugins);
        return armatureTransformer1;
    }

    @Nullable
    public static BakedArmatureTransformer defaultBy(@Nullable Entity entity, @Nullable Model entityModel, @Nullable EntityRenderer<?> entityRenderer) {
        if (entity == null) {
            return null;
        }
        var entityType = entity.getType();
        // when the caller does not provide the entity renderer we need to query it from managers.
        if (entityRenderer == null) {
            entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        }
        // when the caller does not provide the entity model we need to query it from entity render.
        if (entityModel == null) {
            entityModel = getModel(entityRenderer);
        }
        return defaultBy(entityType, entityModel, entityRenderer);
    }

    public static BakedArmatureTransformer defaultBy(EntityType<?> entityType, Model entityModel, EntityRenderer<?> entityRenderer) {
        // in the normal, the entityRenderer only have a model type,
        // but some mods(Custom NPC) generate dynamically models,
        // so we need to be compatible with that
        var storage = EntityRendererStorage.of(entityRenderer);
        return storage.computeTransformerIfAbsent(entityModel, it -> {
            // if it can't transform this, it means we do not support this renderer.
            var model = ModelHolder.ofNullable(entityModel);
            var transformer = SkinRendererManager2.DEFAULT.getTransformer(entityType, model);
            return create(transformer, entityRenderer);
        });
    }

    public void prepare(Entity entity, SkinRenderContext context) {
        for (var plugin : plugins) {
            plugin.prepare(entity, context);
        }
    }

    public void activate(Entity entity, SkinRenderContext context) {
        for (var plugin : plugins) {
            plugin.activate(entity, context);
        }
    }

    public void deactivate(Entity entity, SkinRenderContext context) {
        for (var plugin : plugins) {
            plugin.deactivate(entity, context);
        }
    }

    public void applyTo(BakedArmature bakedArmature) {
        // safe updates
        if (bakedArmature.getArmature() == armature) {
            bakedArmature.setFilter(filter);
            bakedArmature.seTransforms(transforms);
        }
    }

    public void setPlugins(Collection<ArmaturePlugin> plugins) {
        this.plugins.clear();
        this.plugins.addAll(plugins);
    }

    public Collection<ArmaturePlugin> getPlugins() {
        return plugins;
    }

    public void setFilter(IJointFilter filter) {
        this.filter = filter;
    }

    public IJointFilter getFilter() {
        return filter;
    }

    public ArmatureTransformer getTransformer() {
        return armatureTransformer;
    }

    public Armature getArmature() {
        return armature;
    }

    private static EntityModel<?> getModel(EntityRenderer<?> entityRenderer) {
        if (entityRenderer instanceof RenderLayerParent) {
            return ((RenderLayerParent<?, ?>) entityRenderer).getModel();
        }
        return null;
    }
}
