package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import java.awt.*;

@Environment(value = EnvType.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends LivingEntityRenderer<T, MannequinModel<T>> {

    public static boolean enableLimitScale = false;

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;

    private MannequinEntityRenderer<T> mannequinRenderer;

    private ResourceLocation texture;
    private BakedEntityTexture bakedTexture;

    private boolean enableChildRenderer = false;

    public MannequinEntityRenderer(EntityRenderDispatcher rendererManager) {
        super(rendererManager, new MannequinModel<>(0, false), 0.0f);
        this.addLayer(new HumanoidArmorLayer<>(this, new MannequinArmorModel<>(0.5f), new MannequinArmorModel<>(1.0f)));
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new CustomHeadLayer<>(this));
        // two models by mannequin, only deciding which model using when texture specified.
        this.normalModel = this.model;
        this.slimModel = new MannequinModel<>(0, true);
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.hasCustomName();
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int packedLightIn) {
        // when mannequin holding mannequin recursive rendering occurs, and we will enable the child renderer.
        if (this.enableChildRenderer) {
            this.getChildRenderer().render(entity, p_225623_2_, partialTicks, matrixStack, buffers, packedLightIn);
            return;
        }
        PlayerTextureLoader textureLoader = PlayerTextureLoader.getInstance();
        this.enableChildRenderer = true;
        this.texture = textureLoader.getTextureLocation(entity);
        this.bakedTexture = textureLoader.getTextureModel(texture);
        this.model = getModel();
        this.model.setAllVisible(entity.isModelVisible());
        super.render(entity, p_225623_2_, partialTicks, matrixStack, buffers, packedLightIn);
        this.enableChildRenderer = false;
        if (ModDebugger.mannequinCulling) {
            matrixStack.pushPose();
            AABB box = entity.getBoundingBoxForCulling();
            matrixStack.translate(-box.minX - (box.maxX - box.minX) / 2, -box.minY, -box.minZ - (box.maxZ - box.minZ) / 2);
            RenderUtils.drawBoundingBox(matrixStack, box, Color.YELLOW, buffers);
            matrixStack.popPose();
        }
    }

    @Override
    protected void scale(T entity, PoseStack matrixStack, float p_225620_3_) {
        float f = 0.9375f; // from player renderer (maybe 15/16)
        if (!enableLimitScale) {
            f *= entity.getScale();
        }
        matrixStack.scale(f, f, f);
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
            mannequinRenderer = new MannequinEntityRenderer<>(entityRenderDispatcher);
        }
        return mannequinRenderer;
    }
}