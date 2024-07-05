package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.core.client.texture.SmartResourceManager;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {

    @Inject(method = "openAllSelected", at = @At("RETURN"), cancellable = true)
    private void aw2$open(CallbackInfoReturnable<List<PackResources>> cir) {
        var values = new ArrayList<>(cir.getReturnValue());
        values.add(SmartResourceManager.getInstance());
        cir.setReturnValue(values);
    }
}
