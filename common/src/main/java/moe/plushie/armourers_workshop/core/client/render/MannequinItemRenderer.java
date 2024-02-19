package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

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
    public void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        auto bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        auto transform = bakedModel.getTransform(transformType);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f); // reset to center

        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.of(itemStack);
        Vector3f rotation = new Vector3f(transform.rotation.x(), transform.rotation.y(), transform.rotation.z());
        Vector3f scale = new Vector3f(transform.scale.x(), transform.scale.y(), transform.scale.z());

        ExtendedItemRenderer.renderMannequin(descriptor, rotation, scale, 1, 1, 1, 0, light, poseStack, bufferSource);

        poseStack.popPose();
    }
}
