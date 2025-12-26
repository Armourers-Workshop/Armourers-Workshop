package moe.plushie.armourers_workshop.compat.fabric.mixin.addon;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.core.data.SlotManager;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Available("[1.18, )")
@Conditional("trinkets")
@Pseudo
@Mixin(TrinketsApi.class)
public class FabricTrinketsApiMixin {

    static {
        // because the superclass of TrinketComponent is an unknown type,
        // this leads we can't direct using the TrinketComponent api.
        // so we can only call it through reflection.
        Method[] methods = {null};
        BiFunction<Object, Predicate<ItemStack>, List<Tuple<SlotReference, ItemStack>>> getEquipped = (component, filter) -> {
            try {
                if (methods[0] == null) {
                    methods[0] = TrinketComponent.class.getDeclaredMethod("getEquipped", Predicate.class);
                }
                Object results = methods[0].invoke(component, filter);
                return Objects.unsafeCast(results);
            } catch (Exception e) {
                return null;
            }
        };
        SlotManager.registerArmorSlots(LivingEntity.class, entity -> {
            Object component = TrinketsApi.getTrinketComponent(entity).orElse(null);
            if (component != null) {
                var items = getEquipped.apply(component, Objects::nonNull);
                if (items != null) {
                    return Collections.compactMap(items, Tuple::getB);
                }
            }
            return Collections.emptyList();
        });
    }
}
