package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModLog;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;

@Environment(value = EnvType.CLIENT)
public class SkinPreloadManager {

    private static Object lastInventoryVersion = null;

    public static void start() {
        lastInventoryVersion = null;
    }

    public static void stop() {

    }

    public static void tick(boolean isPaused) {
        if (isPaused) {
            return;
        }
        // we need to preload all skin in the current player's inventory.
        if (lastInventoryVersion == null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                Inventory inventory = player.getInventory();
                int inventoryVersion = inventory.getTimesChanged();
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
        int size = inventory.getContainerSize();
        for (int i = 0; i < size; i++) {
            SkinDescriptor descriptor = SkinDescriptor.of(inventory.getItem(i));
            if (descriptor.isEmpty()) {
                continue;
            }
            ModLog.debug("'{}' => start preload skin", descriptor.getIdentifier());
            SkinBakery.getInstance().loadSkin(descriptor, Tickets.PRELOAD);
        }
    }
}
