package moe.plushie.armourers_workshop.compat.core.data;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

@Available("[1.21, )")
public abstract class AbstractFriendlyByteBufImpl implements IFriendlyByteBuf {

    protected final RegistryFriendlyByteBuf source;

    protected AbstractFriendlyByteBufImpl(RegistryFriendlyByteBuf source) {
        this.source = source;
    }

    protected static RegistryFriendlyByteBuf cast(ByteBuf buf) {
        // hitting
        if (buf instanceof RegistryFriendlyByteBuf source) {
            return source;
        }
        return new RegistryFriendlyByteBuf(buf, findRegistryAccess());
    }

    protected static RegistryFriendlyByteBuf map(ByteBuf buf, Function<ByteBuf, ByteBuf> transform) {
        if (buf instanceof RegistryFriendlyByteBuf source) {
            return new RegistryFriendlyByteBuf(transform.apply(buf), source.registryAccess());
        }
        return new RegistryFriendlyByteBuf(transform.apply(buf), findRegistryAccess());
    }

    protected static RegistryAccess findRegistryAccess() {
        // find registry access on the server.
        var server = EnvironmentManager.getServer();
        if (server != null) {
            return server.registryAccess();
        }
        // find registry access on the client.
        var client = EnvironmentExecutor.callOnClient(() -> () -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                return connection.registryAccess();
            }
            return null;
        });
        return client.orElse(null);
    }

    @Override
    public GlobalPos readGlobalPos() {
        return source.readGlobalPos();
    }

    @Override
    public void writeGlobalPos(GlobalPos value) {
        source.writeGlobalPos(value);
    }

    @Override
    public ItemStack readItem() {
        return ItemStack.STREAM_CODEC.decode(source);
    }

    @Override
    public void writeItem(ItemStack value) {
        ItemStack.STREAM_CODEC.encode(source, value);
    }

    @Override
    public Component readComponent() {
        return ComponentSerialization.STREAM_CODEC.decode(source);
    }

    @Override
    public void writeComponent(Component value) {
        ComponentSerialization.STREAM_CODEC.encode(source, value);
    }

    @Override
    public <T> T readNbtWithCodec(IDataCodec<T> codec) {
        var dataResult = codec.codec().parse(NbtOps.INSTANCE, readNbt());
        return dataResult.getOrThrow(string -> new DecoderException("Failed to decode json: " + string));
    }

    @Override
    public <T> void writeNbtWithCodec(IDataCodec<T> codec, T value) {
        var dataResult = codec.codec().encodeStart(NbtOps.INSTANCE, value);
        writeNbt((CompoundTag) dataResult.getOrThrow(string -> new EncoderException("Failed to encode: " + string + " " + value)));
    }
}
