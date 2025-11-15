package moe.plushie.armourers_workshop.compat.core.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;
import java.util.function.Function;

public class AbstractFriendlyByteBuf extends AbstractFriendlyByteBufImpl {

    protected AbstractFriendlyByteBuf(ByteBuf source) {
        super(cast(source));
    }

    protected AbstractFriendlyByteBuf(ByteBuf source, Function<ByteBuf, ByteBuf> transform) {
        super(map(source, transform));
    }

    public static AbstractFriendlyByteBuf wrap(ByteBuf source) {
        return new AbstractFriendlyByteBuf(source);
    }

    @Override
    public int readByte() {
        return source.readByte();
    }

    @Override
    public void writeByte(int value) {
        source.writeByte(value);
    }

    @Override
    public int readInt() {
        return source.readInt();
    }

    @Override
    public void writeInt(int value) {
        source.writeInt(value);
    }

    @Override
    public long readLong() {
        return source.readLong();
    }

    @Override
    public void writeLong(long value) {
        source.writeLong(value);
    }

    @Override
    public float readFloat() {
        return source.readFloat();
    }

    @Override
    public void writeFloat(float value) {
        source.writeFloat(value);
    }

    @Override
    public double readDouble() {
        return source.readDouble();
    }

    @Override
    public void writeDouble(double value) {
        source.writeDouble(value);
    }

    @Override
    public boolean readBoolean() {
        return source.readBoolean();
    }

    @Override
    public void writeBoolean(boolean value) {
        source.writeBoolean(value);
    }

    @Override
    public int readVarInt() {
        return source.readVarInt();
    }

    @Override
    public void writeVarInt(int value) {
        source.writeVarInt(value);
    }

    @Override
    public ByteBuf readBytes(int length) {
        return source.readBytes(length);
    }

    @Override
    public void writeBytes(ByteBuf value) {
        source.writeBytes(value);
    }

    @Override
    public String readUtf() {
        return source.readUtf(Short.MAX_VALUE);
    }

    @Override
    public void writeUtf(String value) {
        source.writeUtf(value);
    }

    @Override
    public UUID readUUID() {
        return source.readUUID();
    }

    @Override
    public void writeUUID(UUID value) {
        source.writeUUID(value);
    }

    @Override
    public <T extends Enum<T>> T readEnum(Class<T> clazz) {
        return source.readEnum(clazz);
    }

    @Override
    public <T extends Enum<T>> void writeEnum(T value) {
        source.writeEnum(value);
    }

    @Override
    public BlockPos readBlockPos() {
        return source.readBlockPos();
    }

    @Override
    public void writeBlockPos(BlockPos value) {
        source.writeBlockPos(value);
    }

    @Override
    public BlockHitResult readBlockHitResult() {
        return source.readBlockHitResult();
    }

    @Override
    public void writeBlockHitResult(BlockHitResult result) {
        source.writeBlockHitResult(result);
    }

    @Override
    public CompoundTag readNbt() {
        return source.readNbt();
    }

    @Override
    public void writeNbt(CompoundTag tag) {
        source.writeNbt(tag);
    }

    @Override
    public AbstractFriendlyByteBuf copy() {
        return new AbstractFriendlyByteBuf(map(source, ByteBuf::copy));
    }

    @Override
    public AbstractFriendlyByteBuf slice() {
        return new AbstractFriendlyByteBuf(source, ByteBuf::slice);
    }

    @Override
    public AbstractFriendlyByteBuf retainedSlice() {
        return new AbstractFriendlyByteBuf(source, ByteBuf::retainedSlice);
    }

    @Override
    public AbstractFriendlyByteBuf duplicate() {
        return new AbstractFriendlyByteBuf(source, ByteBuf::duplicate);
    }

    @Override
    public AbstractFriendlyByteBuf retainedDuplicate() {
        return new AbstractFriendlyByteBuf(source, ByteBuf::retainedDuplicate);
    }

    @Override
    public ByteBuf asByteBuf() {
        return source;
    }
}
