package moe.plushie.armourers_workshop.init.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.util.datafix.schemas.V1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mixin(V1460.class)
public class DataFixMixin {

    @Inject(method = "registerEntities", at = @At("RETURN"))
    public void hooked_registerEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        BiConsumer<String, Supplier<TypeTemplate>> ap = cir.getReturnValue()::put;
        ModConstants.entityTypes().forEach(key -> ap.accept(key.toString(), DSL::remainder));
    }

    @Inject(method = "registerBlockEntities", at = @At("RETURN"))
    public void hooked_registerBlockEntities(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        BiConsumer<String, Supplier<TypeTemplate>> ap = cir.getReturnValue()::put;
        ModConstants.blockEntityTypes().forEach(key -> ap.accept(key.toString(), DSL::remainder));
    }
}
