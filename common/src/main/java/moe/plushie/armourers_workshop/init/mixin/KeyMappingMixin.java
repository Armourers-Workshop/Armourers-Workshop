package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.client.key.IKeyScopeProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    public void aw$same(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> cir) {
        IKeyScopeProvider lhs = ObjectUtils.safeCast(this, IKeyScopeProvider.class);
        IKeyScopeProvider rhs = ObjectUtils.safeCast(keyMapping, IKeyScopeProvider.class);
        if (lhs == null && rhs == null) {
            return;
        }
        String scope1 = ObjectUtils.flatMap(lhs, IKeyScopeProvider::getScope, "");
        String scope2 = ObjectUtils.flatMap(rhs, IKeyScopeProvider::getScope, "");
        if (!scope1.equals(scope2)) {
            cir.setReturnValue(false);
        }
    }
}
