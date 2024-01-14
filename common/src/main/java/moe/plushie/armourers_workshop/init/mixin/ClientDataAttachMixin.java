package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Model.class, EntityRenderer.class})
public abstract class ClientDataAttachMixin implements IAssociatedObjectProvider {

    private Object aw$associatedObject;

    @Override
    public <T> T getAssociatedObject() {
        // noinspection unchecked
        return (T) aw$associatedObject;
    }

    @Override
    public <T> void setAssociatedObject(T data) {
        this.aw$associatedObject = data;
    }
}
