package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.common.IDeltaTracker;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

@OnlyIn(Dist.CLIENT)
public class PreloadableSkinManager {

    private static Object lastInventoryVersion = null;

    public static void start() {
        lastInventoryVersion = null;
    }

    public static void stop() {
    }

    public static void tick(IDeltaTracker deltaTracker) {
        if (deltaTracker.isPaused()) {
            return;
        }
        // we need to preload all skin in the current player's inventory.
        if (lastInventoryVersion == null) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var inventory = player.getInventory();
                var inventoryVersion = inventory.getTimesChanged();
                preloadInventory(inventory);
                lastInventoryVersion = inventoryVersion;
            }
        }
        // we need to preload the server required the skins.
        preloadConfig();
    }

    private static void preloadConfig() {

    }

    private static void preloadInventory(Inventory inventory) {
        var size = inventory.getContainerSize();
        for (var i = 0; i < size; i++) {
            var descriptor = SkinDescriptor.of(inventory.getItem(i));
            if (descriptor.isEmpty()) {
                continue;
            }
            ModLog.debug("'{}' => start preload skin", descriptor.identifier());
            SkinBakery.getInstance().loadSkin(TicketManager.PRELOAD.get(descriptor));
        }
    }
}
