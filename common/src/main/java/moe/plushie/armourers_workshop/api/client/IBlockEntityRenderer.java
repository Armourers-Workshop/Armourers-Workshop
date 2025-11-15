package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.client.state.IBlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public interface IBlockEntityRenderer<T extends BlockEntity, S extends IBlockEntityRenderState> {

    interface Provider<T extends BlockEntity> {

        IBlockEntityRenderer<T, ?> create(Context context);
    }

    interface Context extends Supplier<Object> {
    }
}
