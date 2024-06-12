package moe.plushie.armourers_workshop.compatibility.core.data;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;

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

    protected static RegistryAccess findRegistryAccess() {
        // find registry access on the server.
        var server = EnvironmentManager.getServer();
        if (server != null) {
            return server.registryAccess();
        }
        // find registry access on the client.
        var client = EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> {
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
}
