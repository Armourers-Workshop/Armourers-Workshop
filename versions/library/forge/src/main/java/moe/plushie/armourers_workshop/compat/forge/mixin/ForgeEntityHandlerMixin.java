package moe.plushie.armourers_workshop.compat.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.entity.AbstractEntityHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.21, 1.26)")
@Mixin(AbstractEntityHandler.class)
public interface ForgeEntityHandlerMixin extends IEntityExtension {

    @Override
    default ItemStack getPickedResult(HitResult result) {
        var handler = (AbstractEntityHandler) this;
        return handler.getPickedResult(result);
    }
}
