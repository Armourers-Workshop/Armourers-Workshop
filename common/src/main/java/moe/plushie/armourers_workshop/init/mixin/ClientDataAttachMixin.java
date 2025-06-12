package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Model.class, EntityRenderer.class, PoseStack.class, PoseStack.Pose.class, RenderType.class, SoundEvent.class})
public abstract class ClientDataAttachMixin implements IAssociatedContainerProvider {

    private IAssociatedContainerProvider aw2$associatedContainer;

    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        if (aw2$associatedContainer != null) {
            return aw2$associatedContainer.getAssociatedObject(key);
        }
        return key.defaultValue();
    }

    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        if (aw2$associatedContainer == null) {
            aw2$associatedContainer = new DataContainer.Builtin();
        }
        aw2$associatedContainer.setAssociatedObject(key, value);
    }
}
