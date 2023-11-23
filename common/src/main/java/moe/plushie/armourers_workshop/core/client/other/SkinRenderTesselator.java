package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRenderTesselator extends SkinRenderContext {

    private final BakedSkin bakedSkin;
    private final MannequinEntity mannequin;
    private final MannequinModel<?> mannequinModel;
    private final SkinRenderer<Entity, IModel> renderer;

    public SkinRenderTesselator(BakedSkin bakedSkin, MannequinEntity mannequin, MannequinModel<?> mannequinModel, SkinRenderer<Entity, IModel> renderer) {
        super(null);
        this.bakedSkin = bakedSkin;
        this.mannequin = mannequin;
        this.mannequinModel = mannequinModel;
        this.renderer = renderer;
    }

    public static SkinRenderTesselator create(SkinDescriptor descriptor, Ticket ticket) {
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, ticket);
        if (bakedSkin != null) {
            return create(bakedSkin);
        }
        return null;
    }

    public static SkinRenderTesselator create(BakedSkin bakedSkin) {
        auto mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        auto mannequinModel = SkinItemRenderer.getInstance().getMannequinModel();
        auto renderer = SkinRendererManager.getInstance().getRenderer(mannequin, mannequinModel, null);
        if (renderer == null || mannequin == null || mannequin.getLevel() == null) {
            return null;
        }
        return new SkinRenderTesselator(bakedSkin, mannequin, mannequinModel, renderer);
    }

    public int draw(PoseStack poseStack, MultiBufferSource buffers) {
        auto model = ModelHolder.of(mannequinModel);
        setPose(poseStack);
        setBuffers(buffers);
        setTransforms(mannequin, renderer.getOverrideModel(model));
        return renderer.render(mannequin, model, bakedSkin, getColorScheme(), this);
    }

    public MannequinEntity getMannequin() {
        return mannequin;
    }

    public BakedSkin getBakedSkin() {
        return bakedSkin;
    }

    public Rectangle3f getBakedRenderBounds() {
        return bakedSkin.getRenderBounds(mannequin, mannequinModel, getReferenced());
    }
}
