package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.compatibility.AbstractLivingEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

@Environment(value = EnvType.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends AbstractLivingEntityRenderer<T, MannequinModel<T>> {

    public static boolean enableLimitScale = false;

    private final AbstractEntityRendererContext context;

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;

    private MannequinEntityRenderer<T> mannequinRenderer;

    private ResourceLocation texture;
    private BakedEntityTexture bakedTexture;

    private boolean enableChildRenderer = false;

    public MannequinEntityRenderer(AbstractEntityRendererContext context) {
        super(context, new MannequinModel<>(context, 0, false), 0.0f);
        this.addLayer(ClientNativeManager.getFactory().createHumanoidArmorLayer(this, context, MannequinArmorModel.innerModel(context), MannequinArmorModel.outerModel(context)));
        this.addLayer(ClientNativeManager.getFactory().createItemInHandLayer(this, context));
        this.addLayer(ClientNativeManager.getFactory().createElytraLayer(this, context));
        this.addLayer(ClientNativeManager.getFactory().createCustomHeadLayer(this, context));
        // two models by mannequin, only deciding which model using when texture specified.
        this.normalModel = this.model;
        this.slimModel = new MannequinModel<>(context, 0, true);
        this.context = context;
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.hasCustomName();
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffers, int packedLightIn) {
        // when mannequin holding mannequin recursive rendering occurs, and we will enable the child renderer.
        if (this.enableChildRenderer) {
            this.getChildRenderer().render(entity, p_225623_2_, partialTicks, poseStackIn, buffers, packedLightIn);
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        PlayerTextureLoader textureLoader = PlayerTextureLoader.getInstance();
        this.enableChildRenderer = true;
        this.texture = textureLoader.getTextureLocation(entity);
        this.bakedTexture = textureLoader.getTextureModel(texture);
        this.model = getModel();
        this.model.setAllVisible(entity.isModelVisible());
        super.render(entity, p_225623_2_, partialTicks, poseStackIn, buffers, packedLightIn);
        this.enableChildRenderer = false;
        if (ModDebugger.mannequinCulling) {
            poseStack.pushPose();
            AABB box = entity.getBoundingBoxForCulling();
            double tx = -box.minX - (box.maxX - box.minX) / 2;
            double ty = -box.minY;
            double tz = -box.minZ - (box.maxZ - box.minZ) / 2;
            poseStack.translate((float) tx, (float) ty, (float) tz);
            RenderSystem.drawBoundingBox(poseStack, box, UIColor.YELLOW, buffers);
            poseStack.popPose();
        }
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float p_225620_3_) {
        float f = 0.9375f; // from player renderer (maybe 15/16)
        if (!enableLimitScale) {
            f *= entity.getScale();
        }
        poseStack.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }

    @Override
    public MannequinModel<T> getModel() {
        if (bakedTexture != null && bakedTexture.isSlimModel()) {
            return slimModel;
        }
        return normalModel;
    }

    public MannequinEntityRenderer<T> getChildRenderer() {
        if (mannequinRenderer == null) {
            mannequinRenderer = new MannequinEntityRenderer<>(context);
        }
        return mannequinRenderer;
    }
}
