package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.client.renderer.patched.item.RenderShield;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Available("[1.20, )")
@Pseudo
@Mixin(RenderShield.class)
public abstract class ForgeEpicFightShieldRendererMixin {

    @Redirect(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel aw2$getItemModel(ItemModelShaper modelShaper, ItemStack arg1, ItemStack itemStack, LivingEntityPatch<?> entityPatch, InteractionHand hand) {
        var entity = entityPatch.getOriginal();
        return Minecraft.getInstance().getItemRenderer().getModel(arg1, entity.getLevel(), entity, 0);
    }
}
