package moe.plushie.armourers_workshop.api.network;

import io.netty.buffer.ByteBuf;
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

    float readFloat();

    void writeFloat(float value);

    double readDouble();

    void writeDouble(double value);

    boolean readBoolean();

    void writeBoolean(boolean value);


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

    Component readComponent();

    void writeComponent(Component component);

    ByteBuf asByteBuf();
}
