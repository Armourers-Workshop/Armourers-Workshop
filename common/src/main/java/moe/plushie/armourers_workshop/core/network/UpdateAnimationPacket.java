package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.TickUtils;
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

    public static UpdateAnimationPacket play(Entity entity, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("target", entity.getId());
        tag.putString("name", name);
        return new UpdateAnimationPacket(Mode.PLAY, tag);
    }

    public static UpdateAnimationPacket stop(Entity entity, String name) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("target", entity.getId());
        tag.putString("name", name);
        return new UpdateAnimationPacket(Mode.STOP, tag);
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
                var renderData = getTargetRenderData(player);
                if (renderData != null) {
                    String name = value.getString("name");
                    ModLog.debug("play animation {}", value);
                    renderData.getAnimationManager().play(name, TickUtils.animationTicks());
                }
                break;
            }
            case STOP: {
                var renderData = getTargetRenderData(player);
                if (renderData != null) {
                    String name = value.getString("name");
                    ModLog.debug("stop animation {}", value);
                    renderData.getAnimationManager().stop(name);
                }
                break;
            }
            case MODERATOR: {
                break;
            }
        }
    }

    private SkinRenderData getTargetRenderData(Player player) {
        if (value.contains("target")) {
            int entityId = value.getInt("target");
            return SkinRenderData.of(player.getLevel().getEntity(entityId));
        }
        return null;
    }

    public enum Mode {
        PLAY, STOP, MODERATOR
    }
}
