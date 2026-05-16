package moe.plushie.armourers_workshop.compat.mixin.core.other;

import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Item.class, ItemStack.class, Entity.class, BlockEntity.class})
@Implements(@Interface(iface = IAssociatedContainer.class, prefix = "aw2$"))
public abstract class CommonDataAttachMixin {

    @Unique
    private IAssociatedContainer aw2$associatedContainer;

    public <T> T aw2$getAssociatedObject(IAssociatedContainer.Key<T> key) {
        if (aw2$associatedContainer != null) {
            return aw2$associatedContainer.getAssociatedObject(key);
        }
        return key.defaultValue();
    }

    public <T> void aw2$setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        if (aw2$associatedContainer == null) {
            aw2$associatedContainer = new DataContainer.Builtin();
        }
        aw2$associatedContainer.setAssociatedObject(key, value);
    }
}
