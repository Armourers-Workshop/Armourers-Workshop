package moe.plushie.armourers_workshop.compatibility.fabric;

import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.compatibility.core.AbstractMenuType;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractFriendlyByteBuf;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@Available("[1.21, )")
public class AbstractFabricMenuType<C extends AbstractContainerMenu> extends AbstractMenuType<C> {

    private final MenuType<C> type;
    private final IMenuProvider<C, Object> factory;
    private final IMenuSerializer<Object> serializer;

    public <T> AbstractFabricMenuType(IMenuProvider<C, T> factory, IMenuSerializer<T> serializer, StreamCodec<RegistryFriendlyByteBuf, RegistryFriendlyByteBuf> codec) {
        this.type = new ExtendedScreenHandlerType<>(this::createMenu, codec);
        this.factory = ObjectUtils.unsafeCast(factory);
        this.serializer = ObjectUtils.unsafeCast(serializer);
    }

    public static <C extends AbstractContainerMenu, T> AbstractFabricMenuType<C> create(IMenuProvider<C, T> factory, IMenuSerializer<T> serializer) {
        // the player is missing in the codec, so we need to defer processing.
        return new AbstractFabricMenuType<>(factory, serializer, StreamCodec.of(RegistryFriendlyByteBuf::writeBytes, (bufferIn) -> {
            // we need to keep writer/reader index
            var buffer = bufferIn.retainedDuplicate();
            var duplicated = new RegistryFriendlyByteBuf(buffer, bufferIn.registryAccess());
            // we need to tell decoder all data is processed.
            bufferIn.skipBytes(bufferIn.readableBytes());
            return duplicated;
        }));
    }

    protected C createMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        var value = serializer.read(AbstractFriendlyByteBuf.wrap(buf), inventory.player);
        return factory.createMenu(type, containerId, inventory, value);
    }

    @Override
    protected <T> InteractionResult openMenu(ServerPlayer player, Component title, T value) {
        player.openMenu(new ExtendedScreenHandlerFactory<RegistryFriendlyByteBuf>() {

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return factory.createMenu(type, containerId, inventory, value);
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
        return InteractionResult.SUCCESS;
    }

    public MenuType<C> getType() {
        return type;
    }
}
