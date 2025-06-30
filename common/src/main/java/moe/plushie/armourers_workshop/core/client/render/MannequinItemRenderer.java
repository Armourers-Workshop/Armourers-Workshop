package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class MannequinItemRenderer extends AbstractItemStackRenderer {

    private static MannequinItemRenderer INSTANCE;

    public static MannequinItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MannequinItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, OpenItemDisplayContext itemDisplayContext, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        var bakedModel = EnvironmentManager.getClient().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        var transform = bakedModel.getTransform(itemDisplayContext);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f); // reset to center

        var descriptor = EntityTextureDescriptor.of(itemStack);
        var rotation = new OpenVector3f(transform.rotation.x(), transform.rotation.y(), transform.rotation.z());

        ExtendedItemRenderer.renderMannequin(descriptor, rotation, 1, 1, 1, 0, light, poseStack, bufferSource);

        poseStack.popPose();
    }
}
