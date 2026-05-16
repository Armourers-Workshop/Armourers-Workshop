package moe.plushie.armourers_workshop.compat.fabric.mixin.addon;

import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.core.data.SlotManager;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Available("[20, 26)")
@Conditional("accessories")
@Pseudo
@Mixin(AccessoriesAPI.class)
public class FabricAccessoriesApiMixin {

    static {
        SlotManager.registerArmorSlots(LivingEntity.class, entity -> Collections.compactMap(AccessoriesCapability.getOptionally(entity).map(AccessoriesCapability::getAllEquipped).orElse(Collections.emptyList()), it -> {
            var itemStack = it.stack();
            if (itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            var ref = it.reference();
            var container = ref.slotContainer();
            if (container == null || !container.shouldRender(ref.slot())) {
                return ItemStack.EMPTY;
            }
            return itemStack;
        }));
    }
}
