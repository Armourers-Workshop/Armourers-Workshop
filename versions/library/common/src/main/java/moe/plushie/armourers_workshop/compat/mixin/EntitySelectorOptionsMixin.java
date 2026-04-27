package moe.plushie.armourers_workshop.compat.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.AbstractEntitySelectorParser;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Available("[16, )")
@Mixin(EntitySelectorOptions.class)
public abstract class EntitySelectorOptionsMixin {

    @Shadow
    private static void register(String string, EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> predicate, Component component) {
        throw new UnsupportedOperationException();
    }

    @Inject(method = "bootStrap", at = @At("TAIL"))
    private static void aw2$bootStrap(CallbackInfo ci) {
        AbstractEntitySelectorParser.register(it -> register(it.getName(), it::parse, it::canUse, it.getDescription()));
    }
}
