package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.compat.core.entity.AbstractEntityHandler;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractEntityHandler.class)
public interface ForgeEntityHandlerMixin extends AbstractForgeEntity {

    @Override
    default ItemStack getPickedResult(HitResult target) {
        var handler = (AbstractEntityHandler) this;
        return handler.getPickedResult(target);
    }
}
