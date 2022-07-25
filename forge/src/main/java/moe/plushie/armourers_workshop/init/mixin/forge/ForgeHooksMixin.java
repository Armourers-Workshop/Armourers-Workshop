package moe.plushie.armourers_workshop.init.mixin.forge;

import moe.plushie.armourers_workshop.api.extend.IExtendedEntityHandler;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @ModifyVariable(method = "onPickBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getPickedResult(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack hooked_onPickBlock(ItemStack itemStack, HitResult target) {
        EntityHitResult hitResult = ObjectUtils.safeCast(target, EntityHitResult.class);
        if (hitResult != null) {
            IExtendedEntityHandler handler = ObjectUtils.safeCast(hitResult.getEntity(), IExtendedEntityHandler.class);
            if (handler != null) {
                return handler.getCustomPickResult(target);
            }
        }
        return itemStack;
    }
}
