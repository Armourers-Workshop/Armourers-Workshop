package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.AWCore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Function;

public final class ContainerTypeBuilder<C extends Container, I> {

    private final Class<I> hostInterface;
    private final ContainerFactory<C, I> factory;

    private DataSerializer<I> serializer;
    private DataDeserializer<I> deserializer;

    private Function<I, ITextComponent> title = this::getDefaultTitle;

    private ContainerTypeBuilder(Class<I> hostInterface, ContainerFactory<C, I> factory) {
        this.hostInterface = hostInterface;
        this.factory = factory;
    }

    public static <C extends Container, I> ContainerTypeBuilder<C, I> create (ContainerFactory<C, I> factory, Class<I> hostInterface) {
        return new ContainerTypeBuilder<>(hostInterface, factory);
    }

    /**
     * Specifies a custom strategy for obtaining a custom container name.
     * <p>
     * The stratgy should return {@link StringTextComponent#EMPTY} if there's no custom name.
     */
    public ContainerTypeBuilder<C, I> withTitle(ITextComponent title) {
        this.title = h -> title;
        return this;
    }

    /**
     * Sets a serializer and deserializer for additional data that should be transmitted from server->client when the
     * container is being first opened.
     */
    public ContainerTypeBuilder<C, I> withDataCoder(DataSerializer<I> serializer, DataDeserializer<I> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        return this;
    }

    public ContainerType<C> build(String id) {
        ContainerType<C> containerType = IForgeContainerType.create(this::fromNetwork);
        containerType.setRegistryName(AWCore.getModId(), id);
        ContainerOpener.addOpener(containerType, this::open);
        return containerType;
    }


    /**
     * Opens a container that is based around a single tile entity. The tile entity's position is encoded in the packet
     * buffer.
     */
    private C fromNetwork(int containerId, PlayerInventory inventory, PacketBuffer buffer) {
        if (deserializer != null) {
            return factory.create(containerId, inventory, deserializer.deserialize(inventory.player, buffer));
        }
        return null;
    }

    private ITextComponent getDefaultTitle(I accessInterface) {
        if (accessInterface instanceof INameable) {
            return ((INameable) accessInterface).getDisplayName();
        }
        return StringTextComponent.EMPTY;
    }

    private boolean open(PlayerEntity player, I object) {
        if (!(player instanceof ServerPlayerEntity)) {
            // Cannot open containers on the client or for non-players
            return false;
        }
        INamedContainerProvider container = new SimpleNamedContainerProvider((wnd, p, pl) -> factory.create(wnd, p, object), title.apply(object));
        NetworkHooks.openGui((ServerPlayerEntity) player, container, buffer -> {
            if (serializer != null) {
                serializer.serialize(object, buffer);
            }
        });
        return true;
    }

    @FunctionalInterface
    public interface ContainerFactory<C, I> {
        C create(int containerId, PlayerInventory inventory, I hostObject);
    }

    @FunctionalInterface
    public interface DataSerializer<I> {
        void serialize(I object, PacketBuffer buffer);
    }

    @FunctionalInterface
    public interface DataDeserializer<I> {
        I deserialize(PlayerEntity player, PacketBuffer buffer);
    }

}
