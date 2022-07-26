package moe.plushie.armourers_workshop.init.platform.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerBlockPlaceEvents {

    public static final Event<OnPlace> BEFORE = EventFactory.createArrayBacked(OnPlace.class, callbacks -> (blockPlaceContext, blockState) -> {
        for (OnPlace callback : callbacks) {
            if (!callback.place(blockPlaceContext, blockState)) {
                return false;
            }
        }
        return true;
    });

    @FunctionalInterface
    public interface OnPlace {
        boolean place(BlockPlaceContext blockPlaceContext, BlockState blockState);
    }
}
