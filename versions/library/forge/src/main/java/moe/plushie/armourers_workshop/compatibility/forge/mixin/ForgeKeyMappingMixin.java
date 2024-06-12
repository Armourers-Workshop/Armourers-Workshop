package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeKeyMapping;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Available("[1.21, )")
@Mixin(KeyMappingLookup.class)
public class ForgeKeyMappingMixin {

    @Inject(method = "getAll", at = @At("RETURN"))
    private void aw$getAll(InputConstants.Key keyCode, CallbackInfoReturnable<List<KeyMapping>> cir) {
        var mappings = AbstractForgeKeyMapping.findKeysByCode(keyCode);
        if (mappings != null) {
            cir.getReturnValue().addAll(mappings);
        }
    }
}
