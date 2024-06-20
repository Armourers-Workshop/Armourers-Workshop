package moe.plushie.armourers_workshop.compatibility.core.data;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.BlockHitResult;

import java.util.UUID;

public class AbstractFriendlyByteBuf extends AbstractFriendlyByteBufImpl {

    protected AbstractFriendlyByteBuf(ByteBuf source) {
        super(cast(source));
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
    public void writeEnum(Enum<?> value) {
        source.writeEnum(value);
    }

    @Override
    public IResourceLocation readResourceLocation() {
        return OpenResourceLocation.parse(readUtf());
    }

    @Override
    public void writeResourceLocation(IResourceLocation value) {
        writeUtf(value.toString());
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
    public ByteBuf asByteBuf() {
        return source;
    }
}
