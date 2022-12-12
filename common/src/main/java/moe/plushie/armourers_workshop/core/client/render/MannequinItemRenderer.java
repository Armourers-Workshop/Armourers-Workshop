package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractItemEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureDescriptor;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public class MannequinItemRenderer extends AbstractItemEntityRenderer {

    private static MannequinItemRenderer INSTANCE;

    public static MannequinItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MannequinItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStackIn, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(itemStack);
        ItemTransform transform = bakedModel.getTransforms().getTransform(transformType);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f); // reset to center

        PlayerTextureDescriptor descriptor = PlayerTextureDescriptor.of(itemStack);
        Vector3f rotation = new Vector3f(transform.rotation.x(), transform.rotation.y(), transform.rotation.z());
        Vector3f scale = new Vector3f(transform.scale.x(), transform.scale.y(), transform.scale.z());
        ExtendedItemRenderer.renderMannequin(descriptor, rotation, scale, 1, 1, 1, 0, light, poseStack, renderTypeBuffer);

        poseStack.popPose();
    }
}
