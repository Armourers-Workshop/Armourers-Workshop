package moe.plushie.armourers_workshop.compat.client.gui.element;

import com.mojang.blaze3d.platform.Lighting;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

@Available("[18, 26)")
@OnlyIn(Dist.CLIENT)
public interface AbstractEntityGuiElementImpl {

    default void renderEntityInInventory(Entity entity, int lightmap, int overlay, OpenVector3f translation, OpenQuaternionf quaternion2, IPoseStack poseStack, IBufferSource bufferSource) {
        var poseStack1 = AbstractPoseStack.unwrap(poseStack);
        var bufferSource1 = AbstractBufferSource.unwrap(bufferSource);
        var orientation = AbstractPoseStack.copyQuaternion(quaternion2.conjugate().multiply(OpenVector3f.YP.rotationDegrees(180.0f)));
        var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        Lighting.setupForEntityInInventory();
        dispatcher.setRenderShadow(false);
        dispatcher.overrideCameraOrientation(orientation);
        dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, poseStack1, bufferSource1, lightmap);
        dispatcher.setRenderShadow(true);
        bufferSource.endBatch();
        Lighting.setupFor3DItems();
    }
}
