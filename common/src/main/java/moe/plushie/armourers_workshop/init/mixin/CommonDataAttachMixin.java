package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ItemStack.class, Entity.class, BlockEntity.class})
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
