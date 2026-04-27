package moe.plushie.armourers_workshop.compat.fabric;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.compat.core.AbstractMenuType;
import moe.plushie.armourers_workshop.compat.core.data.AbstractFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Available("[21, )")
public class AbstractFabricMenuType<C extends AbstractContainerMenu> extends AbstractMenuType<C> {

    private final MenuType<C> type;
    private final IMenuProvider<C, Object> factory;
    private final IMenuSerializer<Object> serializer;

    public <T> AbstractFabricMenuType(IMenuProvider<C, T> factory, IMenuSerializer<T> serializer) {
        this.type = new ExtendedScreenHandlerType<>(this::createMenu, codec());
        this.factory = Objects.unsafeCast(factory);
        this.serializer = Objects.unsafeCast(serializer);
    }

    protected C createMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        var value = serializer.read(AbstractFriendlyByteBuf.wrap(buf), inventory.player);
        return factory.createMenu(this, containerId, inventory, value);
    }

    @Override
    public <T> OpenInteractionResult openMenu(ServerPlayer player, Component title, T value) {
        player.openMenu(new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return factory.createMenu(AbstractFabricMenuType.this, containerId, inventory, value);
            }

            @Override
            public RegistryFriendlyByteBuf getScreenOpeningData(ServerPlayer player) {
                var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.registryAccess());
                serializer.write(AbstractFriendlyByteBuf.wrap(buf), player, value);
                return buf;
            }

            @Override
            public Component getDisplayName() {
                return title;
            }
        });
        return OpenInteractionResult.SUCCESS;
    }

    @Override
    public MenuType<C> get() {
        return type;
    }

    private StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> codec() {
        // the player is missing in the codec, so we need to defer processing.
        return StreamCodec.of(RegistryFriendlyByteBuf::writeBytes, (bufferIn) -> {
            // we need to keep writer/reader index
            var buffer = bufferIn.retainedDuplicate();
            var duplicated = new RegistryFriendlyByteBuf(buffer, bufferIn.registryAccess());
            // we need to tell decoder all data is processed.
            bufferIn.skipBytes(bufferIn.readableBytes());
            return duplicated;
        });
    }
}
