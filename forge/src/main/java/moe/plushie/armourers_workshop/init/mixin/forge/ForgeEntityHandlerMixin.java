package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.common.IEntityHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IEntityHandler.class)
public interface ForgeEntityHandlerMixin extends IForgeEntity {

    @Override
    default ItemStack getPickedResult(HitResult target) {
        IEntityHandler handler = ObjectUtils.unsafeCast(this);
        return handler.getCustomPickResult(target);
    }
}
