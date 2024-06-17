package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Model.class, EntityRenderer.class, PoseStack.class, PoseStack.Pose.class, RenderType.class})
public abstract class ClientDataAttachMixin implements IAssociatedObjectProvider {

    private Object aw2$associatedObject;

    @Override
    public <T> T getAssociatedObject() {
        // noinspection unchecked
        return (T) aw2$associatedObject;
    }

    @Override
    public <T> void setAssociatedObject(T data) {
        this.aw2$associatedObject = data;
    }
}
