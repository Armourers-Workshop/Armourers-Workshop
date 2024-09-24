package moe.plushie.armourers_workshop.compatibility.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.init.platform.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.PlayerBlockPlaceEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Available("[1.16, )")
public class AbstractFabricBlockEvent {

    public static IEventHandler<BlockEvent.Place> placeFactory() {
        return (priority, receiveCancelled, subscriber) -> PlayerBlockPlaceEvents.BEFORE.register((context, blockState) -> {
            var player = context.getPlayer();
            var level = context.getLevel();
            var blockPos = context.getClickedPos();
            subscriber.accept(new BlockEvent.Place() {

                @Override
                public Entity getEntity() {
                    return player;
                }

                @Override
                public LevelAccessor getLevel() {
                    return level;
                }

                @Override
                public BlockPos getPos() {
                    return blockPos;
                }

                @Override
                public BlockState getState() {
                    return blockState;
                }

                @Override
                public IBlockSnapshot getSnapshot() {
                    return new IBlockSnapshot() {
                        @Override
                        public BlockState getState() {
                            return level.getBlockState(blockPos);
                        }

                        @Override
                        public CompoundTag getTag() {
                            BlockEntity oldBlockEntity = level.getBlockEntity(blockPos);
                            if (oldBlockEntity != null) {
                                return oldBlockEntity.saveFullData(level.registryAccess());
                            }
                            return null;
                        }
                    };
                }
            });
            return true;
        });
    }

    public static IEventHandler<BlockEvent.Break> breakFactory() {
        return (priority, receiveCancelled, subscriber) -> PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            subscriber.accept(new BlockEvent.Break() {

                @Override
                public Entity getEntity() {
                    return player;
                }

                @Override
                public LevelAccessor getLevel() {
                    return level;
                }

                @Override
                public BlockPos getPos() {
                    return pos;
                }

                @Override
                public BlockState getState() {
                    return null;
                }

                @Override
                public IBlockSnapshot getSnapshot() {
                    return new IBlockSnapshot() {
                        @Override
                        public BlockState getState() {
                            return state;
                        }

                        @Override
                        public CompoundTag getTag() {
                            if (blockEntity != null) {
                                return blockEntity.saveFullData(level.registryAccess());
                            }
                            return null;
                        }
                    };
                }
            });
            return true;
        });
    }
}
