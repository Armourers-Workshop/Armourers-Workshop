package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import moe.plushie.armourers_workshop.init.platform.fabric.addon.TrinketsAddon;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Pseudo
@Mixin(TrinketsApi.class)
public class AbstractFabricTrinketsAddonMixin {

    static {
        // because the superclass of TrinketComponent is an unknown type,
        // this leads we can't direct using the TrinketComponent api.
        // so we can only call it through reflection.
        Method[] methods = {null};
        BiFunction<Object, Predicate<ItemStack>, Object> getEquipped = (component, filter) -> {
            try {
                if (methods[0] == null) {
                    methods[0] = TrinketComponent.class.getDeclaredMethod("getEquipped", Predicate.class);
                }
                return methods[0].invoke(component, filter);
            } catch (Exception e) {
                return null;
            }
        };
        BiFunction<LivingEntity, Predicate<ItemStack>, List<Tuple<SlotReference, ItemStack>>> provider = (entity, filter) -> {
            Optional<Object> component = ObjectUtils.unsafeCast(TrinketsApi.getTrinketComponent(entity));
            Object value = component.map(it -> getEquipped.apply(it, filter)).orElse(null);
            if (value != null) {
                return ObjectUtils.unsafeCast(value);
            }
            return null;
        };
        TrinketsAddon.register(provider::apply, Tuple::getB);
    }
}
