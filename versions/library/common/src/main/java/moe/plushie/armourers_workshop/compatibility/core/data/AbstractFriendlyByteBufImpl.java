package moe.plushie.armourers_workshop.compatibility.core.data;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

@Available("[1.21, )")
public abstract class AbstractFriendlyByteBufImpl implements IFriendlyByteBuf {

    private static Function<ByteBuf, RegistryFriendlyByteBuf> TRANSFORMER;

    protected final RegistryFriendlyByteBuf source;

    protected AbstractFriendlyByteBufImpl(RegistryFriendlyByteBuf source) {
        this.source = source;
    }

    protected static RegistryFriendlyByteBuf cast(ByteBuf buf) {
        RegistryFriendlyByteBuf source = ObjectUtils.safeCast(buf, RegistryFriendlyByteBuf.class);
        if (source == null) {
            source = TRANSFORMER.apply(buf);
        }
        return source;
    }

    public static void setServerboundTransformer(Function<ByteBuf, ? extends ByteBuf> transformer) {
        TRANSFORMER = ObjectUtils.unsafeCast(transformer);
    }

    public static void setClientboundTransformer(Function<ByteBuf, ? extends ByteBuf> transformer) {
        TRANSFORMER = ObjectUtils.unsafeCast(transformer);
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
}
