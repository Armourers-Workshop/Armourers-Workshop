package moe.plushie.armourers_workshop.compat.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.core.entity.AbstractEntityHandler;
import moe.plushie.armourers_workshop.init.platform.fabric.proxy.ClientProxyImpl;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[16, 26)")
@Mixin(ClientProxyImpl.class)
public abstract class FabricEntityHandlerMixin {

    @Inject(method = "onInitializeClient", at = @At("TAIL"), remap = false)
    private void aw2$onInitializeClient(CallbackInfo ci) {
        ClientPickBlockGatherCallback.EVENT.register((player, result) -> {
            if (result instanceof EntityHitResult hitResult && hitResult.getEntity() instanceof AbstractEntityHandler handler) {
                return handler.getPickedResult(result);
            }
            return ItemStack.EMPTY;
        });
    }
}
