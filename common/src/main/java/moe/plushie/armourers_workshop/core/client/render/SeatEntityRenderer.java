package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import me.sagesse.minecraft.client.renderer.EntityRenderer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractEntityRendererContext;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public class SeatEntityRenderer<T extends SeatEntity> extends EntityRenderer<T> {

    public SeatEntityRenderer(AbstractEntityRendererContext context) {
        super(context);
    }

    @Override
    public void render(T entity, float p_225623_2_, float partialTicks, IPoseStack poseStack, MultiBufferSource buffers, int packedLightIn) {
        if (ModDebugger.skinnableBlock) {
            RenderSystem.drawPoint(poseStack, buffers);
            RenderSystem.drawBoundingBox(poseStack, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, UIColor.ORANGE, buffers);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return null;
    }
}
