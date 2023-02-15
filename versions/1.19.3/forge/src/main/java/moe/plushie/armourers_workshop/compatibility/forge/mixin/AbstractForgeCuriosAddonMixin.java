package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.init.platform.forge.addon.CuriosAddon;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import top.theillusivec4.curios.api.CuriosApi;

@Pseudo
@Mixin(CuriosApi.class)
public class AbstractForgeCuriosAddonMixin {

    static {
        CuriosAddon.register((entity, filter) -> CuriosApi.getCuriosHelper().findCurios(entity, filter), slot -> {
            if (slot.slotContext().visible()) {
                return slot.stack();
            }
            return ItemStack.EMPTY;
        });
    }
}
