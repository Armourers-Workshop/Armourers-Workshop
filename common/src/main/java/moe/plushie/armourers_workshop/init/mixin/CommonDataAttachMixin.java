package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Entity.class, ItemStack.class})
public abstract class CommonDataAttachMixin implements IAssociatedObjectProvider {

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
