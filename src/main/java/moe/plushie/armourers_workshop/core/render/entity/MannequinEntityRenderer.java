package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.model.MannequinModel;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends LivingRenderer<T, MannequinModel<T>> {

    public static boolean enableLimitScale = false;

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;

    private MannequinEntityRenderer<T> mannequinRenderer;

    private ResourceLocation texture;
    private BakedEntityTexture bakedTexture;

    private boolean enableChildRenderer = false;

    public MannequinEntityRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new MannequinModel<>(0, false), 0.0f);
        this.addLayer(new BipedArmorLayer<>(this, new MannequinArmorModel<>(0.5f), new MannequinArmorModel<>(1.0f)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new HeadLayer<>(this));
        // two models by mannequin, only deciding which model using when texture specified.
        this.normalModel = this.model;
        this.slimModel = new MannequinModel<>(0, true);
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return entity.hasCustomName();
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int packedLightIn) {
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
    }

    @Override
    protected void scale(T entity, MatrixStack matrixStack, float p_225620_3_) {
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
        if (bakedTexture != null && bakedTexture.isSlim()) {
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