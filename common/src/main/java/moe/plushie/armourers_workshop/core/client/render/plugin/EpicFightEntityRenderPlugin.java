package moe.plushie.armourers_workshop.core.client.render.plugin;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.ILivingEntityRenderer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.other.thirdparty.EpicFlightModel;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.init.event.client.RenderLivingEntityEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EpicFightEntityRenderPlugin<T extends LivingEntity, S extends LivingEntityRenderState> extends EntityRenderPlugin<T, S> {

    private IEntityModel<?> entityModel;
    private ILivingEntityRenderer<T, S, ?> entityRenderer;

    private IPoseStack overridePoseStack;
    private IPoseStack originPoseStack;

    private EpicFlightModel transformerModel;
    private BiConsumer<String, OpenPoseStack.Pose> transformProvider;

    public EpicFightEntityRenderPlugin(EntityRendererContext rendererContext) {
        super(rendererContext);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void prepare(RenderLivingEntityEvent.Setup<T, S> event, Consumer<EpicFightEntityRenderPlugin<T, S>> handler) {
        _prepare(EpicFightEntityRenderPlugin.class, event, handler, EpicFightEntityRenderPlugin::new);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void activate(RenderLivingEntityEvent.Pre<T, S> event, Consumer<EpicFightEntityRenderPlugin<T, S>> handler) {
        _activate(EpicFightEntityRenderPlugin.class, event, handler);
    }

    public static <T extends LivingEntity, S extends LivingEntityRenderState> void deactivate(RenderLivingEntityEvent.Post<T, S> event, Consumer<EpicFightEntityRenderPlugin<T, S>> handler) {
        _deactivate(EpicFightEntityRenderPlugin.class, event, handler);
    }

    @Override
    protected void init(S renderState, float partialTicks, IEntityRenderer<T, S> renderer) {
        if (renderer instanceof ILivingEntityRenderer<T, S, ?> renderer1) {
            init(renderState, partialTicks, renderer1);
        }
    }

    protected void init(S renderState, float partialTicks, ILivingEntityRenderer<T, S, ?> renderer) {
        super.init(renderState, partialTicks, renderer);
        this.entityRenderer = renderer;
    }

    @Override
    protected void activate(S renderState, int lightmap, int overlay, IGraphicsContext context) {
        updateTransformerIfNeeded();
        this.originPoseStack = context.ctm();
        super.activate(renderState, lightmap, overlay, context);
    }

    private void updateTransformerIfNeeded() {
        var entityModel = entityRenderer.abi$getModel();
        if (this.entityModel == entityModel) {
            return;
        }
        this.entityModel = entityModel;
        this.transformerModel = EpicFlightModel.of(entityModel);
        this.transformer = EntityRendererContext.of(entityRenderer).createTransformer(transformerModel, SkinRendererManager.EPIC_FIGHT);
    }

    @Override
    public BakedArmature getArmature(BakedArmature armature) {
        // the transformer status is abnormal when transform provider is null.
        if (transformProvider != null) {
            return super.getArmature(armature);
        }
        return null;
    }

    public void setFirstPerson(boolean isFirstPerson) {
        if (transformer == null) {
            return;
        }
        if (isFirstPerson) {
            transformer.setFilter(joint -> !joint.name().equals("Head") && !joint.name().equals("Chest") && !joint.name().equals("Torso"));
        } else {
            transformer.setFilter(null);
        }
    }

    public void setMesh(Object mesh) {
        if (transformerModel != null) {
            transformerModel.linkTo(mesh);
        }
    }

    public void setTransformProvider(BiConsumer<String, OpenPoseStack.Pose> newTransformProvider) {
        transformProvider = newTransformProvider;
        if (transformerModel != null) {
            EpicFlightTransformProvider.of(transformerModel).linkTo(newTransformProvider);
        }
    }

    public BiConsumer<String, OpenPoseStack.Pose> getTransformProvider() {
        return transformProvider;
    }

    public void setOverridePose(IPoseStack pose) {
        overridePoseStack = pose;
    }

    public IPoseStack overridePose() {
        if (overridePoseStack != null) {
            return overridePoseStack;
        }
        return originPoseStack;
    }
}
