package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public abstract class AbstractItemStackRenderer extends AbstractItemStackRendererImpl {

    @Override
    public final void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        renderByItem(itemStack, transformType, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(buffers), light, overlay);
    }

    public abstract void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay);
}
