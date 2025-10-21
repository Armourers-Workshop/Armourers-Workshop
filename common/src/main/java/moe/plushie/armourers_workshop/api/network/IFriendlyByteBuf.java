package moe.plushie.armourers_workshop.api.network;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public interface IFriendlyByteBuf {

    static IFriendlyByteBuf wrap(ByteBuf buf) {
        return AbstractFriendlyByteBuf.wrap(buf);
    }

    int readByte();

    void writeByte(int value);

    int readInt();

    void writeInt(int value);

    long readLong();

    void writeLong(long value);

    float readFloat();

    void writeFloat(float value);

    double readDouble();

    void writeDouble(double value);

    boolean readBoolean();

    void writeBoolean(boolean value);

    int readVarInt();

    void writeVarInt(int value);

    ByteBuf readBytes(int length);

    void writeBytes(ByteBuf buf);

    String readUtf(); // Short.MAX_VALUE

    void writeUtf(String value);

    UUID readUUID();

    void writeUUID(UUID uuid);

    <T extends Enum<T>> T readEnum(Class<T> clazz);

    void writeEnum(Enum<?> value);

    IResourceLocation readResourceLocation();

    void writeResourceLocation(IResourceLocation value);

    ItemStack readItem();

    void writeItem(ItemStack value);

    BlockPos readBlockPos();

    void writeBlockPos(BlockPos pos);

    GlobalPos readGlobalPos();

    void writeGlobalPos(GlobalPos pos);

    BlockHitResult readBlockHitResult();

    void writeBlockHitResult(BlockHitResult result);

    CompoundTag readNbt();

    void writeNbt(CompoundTag tag);

    <T> T readNbtWithCodec(IDataCodec<T> codec);

    <T> void writeNbtWithCodec(IDataCodec<T> codec, T value);

    Component readComponent();

    void writeComponent(Component component);

    /**
     * Returns a copy of this buffer's readable bytes. Modifying the content of the returned buffer or this buffer does not affect each other at all.
     */
    IFriendlyByteBuf copy();

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content of the returned buffer or this buffer affects each other's content while they maintain separate indexes and marks.
     */
    IFriendlyByteBuf slice();

    /**
     * Returns a retained slice of this buffer's readable bytes. Modifying the content of the returned buffer or this buffer affects each other's content while they maintain separate indexes and marks.
     */
    IFriendlyByteBuf retainedSlice();

    /**
     * Returns a buffer which shares the whole region of this buffer. Modifying the content of the returned buffer or this buffer affects each other's content while they maintain separate indexes and marks.
     */
    IFriendlyByteBuf duplicate();

    /**
     * Returns a retained buffer which shares the whole region of this buffer. Modifying the content of the returned buffer or this buffer affects each other's content while they maintain separate indexes and marks.
     */
    IFriendlyByteBuf retainedDuplicate();

    /**
     * Exposes this buffer's readable bytes as an ByteBuf.
     */
    ByteBuf asByteBuf();
}
