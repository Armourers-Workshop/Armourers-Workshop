package moe.plushie.armourers_workshop.compatibility.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Available("[1.18, )")
@Pseudo
@Mixin(targets = "me.shedaniel.rei.plugin.client.entry.ItemEntryDefinition$ItemEntryRenderer")
public class ItemRendererREIMixin {

    // https://github.com/shedaniel/RoughlyEnoughItems/blob/7.x-1.18/runtime/src/main/java/me/shedaniel/rei/plugin/client/entry/ItemEntryDefinition.java#L233
    @Redirect(method = "getExtraData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;", remap = true), remap = false)
    private BakedModel aw2$getModel(ItemRenderer itemRenderer, ItemStack itemStack, Level level, LivingEntity livingEntity, int id) {
        ClientWardrobeHandler.startRenderGuiItem(itemStack);
        var bakedModel = itemRenderer.getModel(itemStack, level, livingEntity, id);
        ClientWardrobeHandler.endRenderGuiItem(itemStack);
        return bakedModel;
    }
}
