package moe.plushie.armourers_workshop.compat.fabric.event.common;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockSnapshot;
import moe.plushie.armourers_workshop.api.registry.IEventHandler;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.event.common.BlockEvent;
import moe.plushie.armourers_workshop.init.platform.fabric.event.PlayerBlockPlaceEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
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
                public Entity entity() {
                    return player;
                }

                @Override
                public LevelAccessor level() {
                    return level;
                }

                @Override
                public BlockPos blockPos() {
                    return blockPos;
                }

                @Override
                public BlockState blockState() {
                    return blockState;
                }

                @Override
                public IBlockSnapshot snapshot() {
                    return new IBlockSnapshot() {
                        @Override
                        public BlockState state() {
                            return level.getBlockState(blockPos);
                        }

                        @Override
                        public CompoundTag tag() {
                            var oldBlockEntity = level.getBlockEntity(blockPos);
                            if (oldBlockEntity != null) {
                                var serializer = new TagSerializer(SerializationContext.from(level));
                                oldBlockEntity.saveFullData(serializer);
                                return serializer.tag();
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
                public Entity entity() {
                    return player;
                }

                @Override
                public LevelAccessor level() {
                    return level;
                }

                @Override
                public BlockPos blockPos() {
                    return pos;
                }

                @Override
                public BlockState blockState() {
                    return null;
                }

                @Override
                public IBlockSnapshot snapshot() {
                    return new IBlockSnapshot() {
                        @Override
                        public BlockState state() {
                            return state;
                        }

                        @Override
                        public CompoundTag tag() {
                            if (blockEntity != null) {
                                var serializer = new TagSerializer(SerializationContext.from(level));
                                blockEntity.saveFullData(serializer);
                                return serializer.tag();
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
