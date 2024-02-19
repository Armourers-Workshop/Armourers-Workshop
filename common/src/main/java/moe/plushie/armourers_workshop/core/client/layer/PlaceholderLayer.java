package moe.plushie.armourers_workshop.core.client.layer;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractRenderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class PlaceholderLayer<T extends Entity, M extends EntityModel<T>> extends AbstractRenderLayer<T, M> {

    public PlaceholderLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(T entity, float limbSwing, float limbSwingAmount, int packedLightIn, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, IPoseStack poseStack, IBufferSource bufferSource) {
        // none
    }
}
