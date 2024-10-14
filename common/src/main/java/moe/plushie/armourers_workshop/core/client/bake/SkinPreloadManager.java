package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.other.FindableSkinManager;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class SkinPreloadManager {

    private static Object lastInventoryVersion = null;

    public static void start() {
        lastInventoryVersion = null;
        FindableSkinManager.getInstance().start();
    }

    public static void stop() {
        FindableSkinManager.getInstance().stop();
    }

    public static void tick(boolean isPaused) {
        if (isPaused) {
            return;
        }
        // we need to preload all skin in the current player's inventory.
        if (lastInventoryVersion == null) {
            var player = EnvironmentManager.getPlayer();
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
            ModLog.debug("'{}' => start preload skin", descriptor.getIdentifier());
            SkinBakery.getInstance().loadSkin(descriptor, Tickets.PRELOAD);
        }
    }
}
