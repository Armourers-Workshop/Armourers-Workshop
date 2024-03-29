package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class SeatEntityRenderer<T extends SeatEntity> extends AbstractEntityRenderer<T> {

    public SeatEntityRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        if (ModDebugger.skinnable) {
            ShapeTesselator.stroke(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, UIColor.ORANGE, poseStack, buffers);
            ShapeTesselator.vector(Vector3f.ZERO, poseStack, buffers);
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
