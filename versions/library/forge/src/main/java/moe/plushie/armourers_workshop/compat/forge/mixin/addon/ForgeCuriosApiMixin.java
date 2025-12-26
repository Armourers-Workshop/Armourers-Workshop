package moe.plushie.armourers_workshop.compat.forge.mixin.addon;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.core.data.SlotManager;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Objects;

@Available("[1.18, )")
@Conditional("curios")
@Pseudo
@Mixin(CuriosApi.class)
public class ForgeCuriosApiMixin {

    static {
        SlotManager.registerArmorSlots(LivingEntity.class, entity -> Collections.compactMap(CuriosApi.getCuriosHelper().findCurios(entity, Objects::nonNull), it -> {
            if (it.slotContext().visible()) {
                return it.stack();
            }
            return ItemStack.EMPTY;
        }));
    }
}
