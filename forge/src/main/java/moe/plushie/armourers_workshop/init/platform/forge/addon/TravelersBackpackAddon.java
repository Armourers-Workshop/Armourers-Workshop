package moe.plushie.armourers_workshop.init.platform.forge.addon;

import moe.plushie.armourers_workshop.core.data.EntityEquipmentManager;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class TravelersBackpackAddon {

    public static void register(Function<Player, ItemStack> provider) {
        EntityEquipmentManager.register(new EntityEquipmentManager.Provider() {
            @Override
            public Iterable<ItemStack> getArmorSlots(Entity entity) {
                if (entity instanceof Player player) {
                    return Collections.singleton(provider.apply(player));
                }
                return null;
            }
        });
    }
}
