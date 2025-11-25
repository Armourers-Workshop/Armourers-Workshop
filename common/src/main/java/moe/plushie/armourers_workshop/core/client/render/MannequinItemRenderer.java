package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.client.renderer.special.AbstractSpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.client.render.element.SpecialRenderElement;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class MannequinItemRenderer extends AbstractSpecialModelRenderer<ItemStack> {

    public static final IDataMapCodec<MannequinItemRenderer> MAP_CODEC = IDataMapCodec.unit(MannequinItemRenderer::new);

    private final OpenRectangle3f targetBox = new OpenRectangle3f(-0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 1.0f);

    @Override
    protected void abi$render(ItemStack itemStack, OpenItemDisplayContext displayContext, int lightmap, int overlay, IGraphicsContext context) {
        if (itemStack.isEmpty()) {
            return;
        }
        var itemModel = Minecraft.getInstance().getItemModel(itemStack, null, null, 0);
        var itemTransform = itemModel.getTransform(displayContext);
        var descriptor = PlayerSkinDescriptor.of(itemStack);

        var entity = MannequinRenderState.Placeholder.getEntity();
        var boundingBox = entity.getBoundingBox();
        var rect = new OpenRectangle3f(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX - boundingBox.minX, boundingBox.maxY - boundingBox.minY, boundingBox.maxZ - boundingBox.minZ);

        context.saveGraphicsState();

        context.translateCTM(0.5f, 0.5f, 0.5f); // reset to center
        context.rotateCTM(OpenVector3f.YP.rotationDegrees(180));

        if (ModDebugger.targetBounds) {
            context.draw(ShapeElement.arrow());
            context.draw(ShapeElement.stroke(targetBox, Colors.ORANGE));
        }

        var rotation = itemTransform.rotation();
        var resolvedRect = rect.offset(rect.midX(), rect.midY(), rect.midZ());
        resolvedRect.transform(new OpenMatrix4f(new OpenQuaternionf(rotation.x(), rotation.y(), rotation.z(), true)));
        var newScale = Math.min(targetBox.width() / resolvedRect.width(), targetBox.height() / resolvedRect.height());

        context.scaleCTM(newScale, newScale, newScale);
        context.translateCTM(-rect.midX(), -rect.midY(), -rect.midZ()); // to model center

        entity.setTextureDescriptor(descriptor);
        context.draw(SpecialRenderElement.entity(entity, 0.0f, 1.0f, lightmap, overlay));
        entity.setTextureDescriptor(PlayerSkinDescriptor.DEFAULT);

        context.restoreGraphicsState();
    }

    @Override
    protected ItemStack abi$extractArgument(ItemStack itemStack) {
        //var itemModel = Minecraft.getInstance().getItemModel(itemStack, null, null, 0);
        //var itemTransform = itemModel.getTransform(displayContext);
        //var descriptor = EntityTextureDescriptor.of(itemStack);
        return itemStack;
    }
}
