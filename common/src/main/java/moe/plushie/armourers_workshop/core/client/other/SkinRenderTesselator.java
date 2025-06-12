package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, ticket);
        if (bakedSkin != null) {
            return create(bakedSkin);
        }
        return null;
    }

    public static SkinRenderTesselator create(BakedSkin bakedSkin) {
        var mannequin = PlaceholderManager.MANNEQUIN.get();
        var bakedArmature = BakedArmature.defaultBy(bakedSkin.type());
        if (bakedArmature == null || mannequin == null || mannequin.getLevel() == null) {
            return null;
        }
        return new SkinRenderTesselator(bakedSkin, bakedArmature, mannequin);
    }

    public int draw() {
        bakedSkin.setupAnim(mannequin, bakedArmature, this);
        var paintScheme = bakedSkin.resolve(mannequin, colorScheme());
        if (isUseItemTransforms()) {
            var itemTransform = bakedSkin.itemTransform();
            itemTransform.apply(poseStack, mannequin(), bakedSkin, this);
        }
        SkinRenderer.render(mannequin, bakedArmature, bakedSkin, paintScheme, this);
        return SkinRenderHelper.getRenderCount(bakedSkin);
    }

    public MannequinEntity mannequin() {
        return mannequin;
    }

    public BakedSkin skin() {
        return bakedSkin;
    }
}
