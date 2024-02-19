package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinRenderTesselator extends SkinRenderContext {

    private final BakedSkin bakedSkin;
    private final BakedArmature bakedArmature;
    private final MannequinEntity mannequin;

    public SkinRenderTesselator(BakedSkin bakedSkin, BakedArmature bakedArmature, MannequinEntity mannequin) {
        super(null);
        this.bakedSkin = bakedSkin;
        this.mannequin = mannequin;
        this.bakedArmature = bakedArmature;
    }

    public static SkinRenderTesselator create(SkinDescriptor descriptor, Ticket ticket) {
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, ticket);
        if (bakedSkin != null) {
            return create(bakedSkin);
        }
        return null;
    }

    public static SkinRenderTesselator create(BakedSkin bakedSkin) {
        auto mannequin = PlaceholderManager.MANNEQUIN.get();
        auto bakedArmature = BakedArmature.defaultBy(bakedSkin.getType());
        if (bakedArmature == null || mannequin == null || mannequin.getLevel() == null) {
            return null;
        }
        return new SkinRenderTesselator(bakedSkin, bakedArmature, mannequin);
    }

    public int draw(IPoseStack poseStack, IBufferSource bufferSource) {
        setPose(poseStack);
        setBuffers(bufferSource);
//        setTransforms(mannequin, renderer.getOverrideModel(model));
//        bakedArmature.setupAnim(mannequin, null, this);
        bakedSkin.setupAnim(mannequin, getAnimationTicks(), getReferenced());
        return SkinRenderer.render(mannequin, bakedArmature, bakedSkin, getColorScheme(), this);
    }

    public MannequinEntity getMannequin() {
        return mannequin;
    }

    public BakedSkin getBakedSkin() {
        return bakedSkin;
    }

    public Rectangle3f getBakedRenderBounds() {
        return bakedSkin.getRenderBounds(getReferenced());
    }
}
