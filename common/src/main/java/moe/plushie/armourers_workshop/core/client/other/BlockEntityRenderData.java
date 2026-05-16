package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityRenderData extends EntitySlotsHandler<BlockEntity> {

    private static final DataContainer.Key<BlockEntityRenderData> KEY = DataContainer.key("RenderData", BlockEntityRenderData::new);

    public BlockEntityRenderData(BlockEntity blockEntity) {
        super(blockEntity, new BlockEntityProvider(), new WardrobeProvider());
    }

    public static BlockEntityRenderData of(BlockEntity entity) {
        return of(entity, true);
    }

    public static BlockEntityRenderData of(BlockEntity entity, boolean lazy) {
        if (entity != null) {
            if (lazy) {
                return DataContainer.of(entity, KEY);
            }
            return DataContainer.get(entity, KEY);
        }
        return null;
    }

    public void tick(BlockEntity blockEntity) {
        tick(blockEntity, null);
    }

    public void remove(BlockEntity blockEntity) {
        clear(blockEntity);
    }
}
