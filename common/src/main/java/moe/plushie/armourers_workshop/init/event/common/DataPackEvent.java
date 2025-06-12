package moe.plushie.armourers_workshop.init.event.common;

import moe.plushie.armourers_workshop.core.data.DataPackType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface DataPackEvent {

    interface Reloading extends DataPackEvent {

        DataPackType type();
    }

    /**
     * Fires when a player joins the server or when the reload command is ran,
     * before tags and crafting recipes are sent to the client. Send datapack data
     * to clients when this event fires.
     */
    interface Sync extends DataPackEvent {

        /**
         * Gets the player that is joining the server, or null when syncing for all players, such as when the reload command runs.
         *
         * @return The player to sync datapacks to. Null when syncing for all players.
         */
        @Nullable
        Player player();
    }
}
