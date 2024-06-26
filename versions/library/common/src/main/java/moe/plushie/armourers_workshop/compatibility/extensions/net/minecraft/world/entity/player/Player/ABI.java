package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.player.Player;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class ABI {

    // copy from GiveCommand
    public static void giveItem(@This Player player, ItemStack itemStack) {
        boolean bl = player.getInventory().add(itemStack);
        ItemEntity itemEntity;
        if (bl && itemStack.isEmpty()) {
            itemStack.setCount(1);
            itemEntity = player.drop(itemStack, false);
            if (itemEntity != null) {
                itemEntity.makeFakeItem();
            }
            player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.containerMenu.broadcastChanges();
        } else {
            itemEntity = player.drop(itemStack, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
    }
}
