package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractLivingEntityRenderer;
import moe.plushie.armourers_workshop.core.client.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.client.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends AbstractLivingEntityRenderer<T, MannequinModel<T>> {

    public static boolean enableLimitScale = false;
    public static boolean enableLimitYRot = false;

    private final Context context;

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;

    private final RenderLayer<T, MannequinModel<T>> normalArmorLayer;
    private final RenderLayer<T, MannequinModel<T>> slimArmorLayer;

    private MannequinEntityRenderer<T> mannequinRenderer;

    private IResourceLocation texture;
    private BakedEntityTexture bakedTexture;

    private boolean modelState = false;
    private boolean enableChildRenderer = false;

    public MannequinEntityRenderer(Context context) {
        super(context, MannequinModel.normal(context), 0.0f);
        // two models by mannequin, only deciding which model using when texture specified.
        var provider = getLayerProvider();
        this.context = context;
        this.normalModel = super.getModel();
        this.slimModel = MannequinModel.slim(context);
        this.normalArmorLayer = provider.createHumanoidArmorLayer(context, MannequinArmorModel.normalInner(context), MannequinArmorModel.normalOuter(context));
        this.slimArmorLayer = provider.createHumanoidArmorLayer(context, MannequinArmorModel.slimInner(context), MannequinArmorModel.slimOuter(context));
        // add
        this.addLayer(normalArmorLayer);
        this.addLayer(provider.createItemInHandLayer(context));
        this.addLayer(provider.createElytraLayer(context));
        this.addLayer(provider.createCustomHeadLayer(context));
    }

    @Override
    public boolean shouldShowName(T entity) {
        return entity.hasCustomName();
    }

    @Override
    public void render(T entity, float f, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int packedLightIn) {
        // when mannequin holding mannequin recursive rendering occurs, and we will enable the child renderer.
        if (this.enableChildRenderer) {
            this.getChildRenderer().render(entity, f, partialTicks, poseStack, bufferSource, packedLightIn);
            return;
        }
        var textureLoader = PlayerTextureLoader.getInstance();
        this.texture = textureLoader.getTextureLocation(entity);
        this.bakedTexture = textureLoader.getTextureModel(texture);
        this.applyTextureModel(bakedTexture);
        super.getModel().setAllVisible(entity.isModelVisible());
        this.enableChildRenderer = true;
        super.render(entity, f, partialTicks, poseStack, bufferSource, packedLightIn);
        this.enableChildRenderer = false;
        if (ModDebugger.mannequinCulling) {
            poseStack.pushPose();
            var box = entity.getBoundingBoxForCulling();
            double tx = -box.minX - (box.maxX - box.minX) / 2;
            double ty = -box.minY;
            double tz = -box.minZ - (box.maxZ - box.minZ) / 2;
            poseStack.translate((float) tx, (float) ty, (float) tz);
            ShapeTesselator.stroke(box, UIColor.YELLOW, poseStack, bufferSource);
            poseStack.popPose();
        }
    }

    @Override
    public float getEntityScale(T entity) {
        float f = 0.9375f; // from player renderer (maybe 15/16)
        if (!enableLimitScale) {
            f *= entity.getScale();
        }
        return f;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture.toLocation();
    }

    public MannequinEntityRenderer<T> getChildRenderer() {
        if (mannequinRenderer == null) {
            mannequinRenderer = new MannequinEntityRenderer<>(context);
        }
        return mannequinRenderer;
    }

    private void applyTextureModel(BakedEntityTexture texture) {
        boolean newModelState = texture != null && texture.isSlimModel();
        if (modelState == newModelState) {
            return;
        }
        modelState = newModelState;
        if (modelState) {
            setModel(slimModel);
            replaceLayer(slimArmorLayer, normalArmorLayer);
        } else {
            setModel(normalModel);
            replaceLayer(normalArmorLayer, slimArmorLayer);
        }
    }

    private void replaceLayer(RenderLayer<T, MannequinModel<T>> toLayer, RenderLayer<T, MannequinModel<T>> fromLayer) {
        int index = layers.indexOf(fromLayer);
        if (index >= 0) {
            layers[index] = toLayer;
        }
    }
}
