package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import moe.plushie.armourers_workshop.core.render.model.MannequinArmorModel;
import moe.plushie.armourers_workshop.core.render.model.MannequinModel;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class MannequinEntityRenderer<T extends MannequinEntity> extends LivingRenderer<T, MannequinModel<T>> {

    private final MannequinModel<T> normalModel;
    private final MannequinModel<T> slimModel;
    private ResourceLocation textureLocation;

    public MannequinEntityRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new MannequinModel<>(0, false), 0.0f);
        this.addLayer(new BipedArmorLayer<>(this, new MannequinArmorModel<>(0.5f), new MannequinArmorModel<>(1.0f)));
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new SkinWardrobeArmorLayer<>(this));
        // Has two models by mannequin, only deciding which model using when texture specified.
        this.normalModel = this.model;
        this.slimModel = new MannequinModel<>(0, true);
    }

    @Override
    public void render(T entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
        this.textureLocation = PlayerTextureLoader.getInstance().getTextureLocation(entity);
        this.model = getResolvedModel(entity);
        this.model.setAllVisible(true);
        super.render(entity, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
    }

    public MannequinModel<T> getResolvedModel(T entity) {
        BakedEntityTexture texture = PlayerTextureLoader.getInstance().getTextureModel(getTextureLocation(entity));
        if (texture != null && texture.isSlim()) {
            return slimModel;
        }
        return normalModel;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return textureLocation;
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return false;
    }
}