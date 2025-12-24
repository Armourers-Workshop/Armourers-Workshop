package moe.plushie.armourers_workshop.compat.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.item.AbstractItemDisplayContext;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Available("[1.20, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractItemSpecialRendererImpl {

    public abstract AbstractSpecialModelRenderer<?> renderer();

    public BlockEntityWithoutLevelRenderer bake(Object context) {
        return new Wrapper<>(renderer());
    }

    private static class Wrapper<T> extends BlockEntityWithoutLevelRenderer {

        private final AbstractSpecialModelRenderer<T> source;

        public Wrapper(AbstractSpecialModelRenderer<T> source) {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            this.source = source;
        }

        @Override
        public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int lightmap, int overlay) {
            var data = source.extractArgument(itemStack);
            var context = AbstractGraphicsRenderer.wrap(poseStack, bufferSource);
            source.render(data, AbstractItemDisplayContext.wrap(transformType), lightmap, overlay, context);
        }
    }
}
