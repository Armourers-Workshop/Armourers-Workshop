package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerProvider;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Item.class, ItemStack.class, Entity.class, BlockEntity.class})
public abstract class CommonDataAttachMixin implements IAssociatedContainerProvider {

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
