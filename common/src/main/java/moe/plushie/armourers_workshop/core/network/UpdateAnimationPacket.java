package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class UpdateAnimationPacket extends CustomPacket {

    private final Mode mode;
    private final CompoundTag value;

    public UpdateAnimationPacket(Mode mode, CompoundTag value) {
        this.mode = mode;
        this.value = value;
    }

    public UpdateAnimationPacket(IFriendlyByteBuf buffer) {
        this.mode = buffer.readEnum(Mode.class);
        this.value = buffer.readNbt();
    }

    public static UpdateAnimationPacket play(Selector selector, String name, int playCount) {
        var tag = selector.save();
        tag.putString("name", name);
        tag.putInt("count", playCount);
        return new UpdateAnimationPacket(Mode.PLAY, tag);
    }

    public static UpdateAnimationPacket stop(Selector selector, String name) {
        var tag = selector.save();
        tag.putString("name", name);
        return new UpdateAnimationPacket(Mode.STOP, tag);
    }

    public static UpdateAnimationPacket mapping(Selector selector, String from, String to) {
        var tag = selector.save();
        tag.putString("from", from);
        tag.putString("to", to);
        return new UpdateAnimationPacket(Mode.MAPPING, tag);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeEnum(mode);
        buffer.writeNbt(value);
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        switch (mode) {
            case PLAY: {
                var animationManager = getTargetRenderData(player);
                if (animationManager != null) {
                    var name = value.getString("name");
                    var playCount = value.getInt("count");
                    ModLog.debug("play animation {}", value);
                    animationManager.play(name, TickUtils.animationTicks(), playCount);
                }
                break;
            }
            case STOP: {
                var animationManager = getTargetRenderData(player);
                if (animationManager != null) {
                    String name = value.getString("name");
                    ModLog.debug("stop animation {}", value);
                    animationManager.stop(name);
                }
                break;
            }
            case MAPPING: {
                var animationManager = getTargetRenderData(player);
                if (animationManager != null) {
                    String from = value.getString("from");
                    String to = value.getString("to");
                    ModLog.debug("remapping animation {} to {}", from, to);
                    animationManager.mapping(from, to);
                }
                break;
            }
            case MODERATOR: {
                break;
            }
        }
    }

    private AnimationManager getTargetRenderData(Player player) {
        if (value.contains("entity")) {
            int entityId = value.getInt("entity");
            return AnimationManager.of(player.getLevel().getEntity(entityId));
        }
        if (value.contains("block")) {
            var blockPos = value.getOptionalBlockPos("block", BlockPos.ZERO);
            return AnimationManager.of(player.getLevel().getBlockEntity(blockPos));
        }
        return null;
    }

    public enum Mode {
        PLAY, STOP, MAPPING, MODERATOR
    }

    public static class Selector {

        private final int id;
        private final BlockPos pos;

        public Selector(Entity entity) {
            this.id = entity.getId();
            this.pos = null;
        }

        public Selector(BlockPos blockPos) {
            this.id = -1;
            this.pos = blockPos;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            if (pos != null) {
                tag.putOptionalBlockPos("block", pos, null);
            } else {
                tag.putInt("entity", id);
            }
            return tag;
        }
    }
}
