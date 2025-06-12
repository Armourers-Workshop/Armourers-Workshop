package moe.plushie.armourers_workshop.compatibility.forge.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeCommonEventsImpl;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.21, )")
public class AbstractForgeBlockEvent {

    public static IEventHandler<BlockEvent.Place> placeFactory() {
        return AbstractForgeCommonEventsImpl.BLOCK_PLACE.map(event -> new BlockEvent.Place() {

            @Override
            public Entity entity() {
                return event.getEntity();
            }

            @Override
            public LevelAccessor level() {
                return event.getLevel();
            }

            @Override
            public BlockPos blockPos() {
                return event.getPos();
            }

            @Override
            public BlockState blockState() {
                return event.getState();
            }

            @Override
            public IBlockSnapshot snapshot() {
                return new IBlockSnapshot() {
                    @Override
                    public BlockState state() {
                        return event.getBlockSnapshot().getState();
                    }

                    @Override
                    public CompoundTag tag() {
                        return event.getBlockSnapshot().getTag();
                    }
                };
            }
        });
    }

    public static IEventHandler<BlockEvent.Break> breakFactory() {
        return AbstractForgeCommonEventsImpl.BLOCK_BREAK.map(event -> new BlockEvent.Break() {

            @Override
            public Entity entity() {
                return event.getPlayer();
            }

            @Override
            public LevelAccessor level() {
                return event.getLevel();
            }

            @Override
            public BlockPos blockPos() {
                return event.getPos();
            }

            @Override
            public BlockState blockState() {
                return null;
            }

            @Override
            public IBlockSnapshot snapshot() {
                var level = event.getLevel();
                return new IBlockSnapshot() {
                    @Override
                    public BlockState state() {
                        return event.getState();
                    }

                    @Override
                    public CompoundTag tag() {
                        var blockEntity = level.getBlockEntity(event.getPos());
                        if (blockEntity != null) {
                            return blockEntity.saveFullData(level.registryAccess());
                        }
                        return null;
                    }
                };
            }
        });
    }
}
