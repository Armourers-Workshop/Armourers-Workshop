package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class SeatEntityRenderer<T extends SeatEntity> extends AbstractEntityRenderer<T> {

    public SeatEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, IPoseStack poseStack, IBufferSource bufferSource, int packedLightIn) {
        if (ModDebugger.skinnable) {
            ShapeTesselator.stroke(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, UIColor.ORANGE, poseStack, bufferSource);
            ShapeTesselator.vector(Vector3f.ZERO, poseStack, bufferSource);
        }
    }

    @Override
    public boolean shouldShowName(T entity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
