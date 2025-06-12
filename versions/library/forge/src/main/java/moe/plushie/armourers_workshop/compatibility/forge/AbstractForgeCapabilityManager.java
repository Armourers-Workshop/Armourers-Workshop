package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityCapability;
import moe.plushie.armourers_workshop.api.common.IEntityCapability;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeStorage;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModBlockEntityTypes;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeCapabilityManager {

    public static <T> IRegistryHolder<IEntityCapability<T>> registerEntity(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        if (type == SkinWardrobe.class) {
            return Objects.unsafeCast(createWardrobeCapability(registryName, Objects.unsafeCast(factory)));
        }
        throw new AssertionError();
    }

    public static <T> IRegistryHolder<IBlockEntityCapability<T>> registerBlockEntity(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        if (registryName.equals(ModConstants.key("item"))) {
            return Objects.unsafeCast(createSidedCapability(registryName, Capabilities.ItemHandler.BLOCK));
        }
        if (registryName.equals(ModConstants.key("fluid"))) {
            return Objects.unsafeCast(createSidedCapability(registryName, Capabilities.FluidHandler.BLOCK));
        }
        if (registryName.equals(ModConstants.key("energy"))) {
            return Objects.unsafeCast(createSidedCapability(registryName, Capabilities.EnergyStorage.BLOCK));
        }
        throw new AssertionError();
    }

    private static IRegistryHolder<IEntityCapability<SkinWardrobe>> createWardrobeCapability(IResourceLocation registryName, Function<Entity, Optional<SkinWardrobe>> provider) {
        EntityCapability<SkinWardrobe, Void> capability = EntityCapability.createVoid(registryName.toLocation(), SkinWardrobe.class);
        IEntityCapability<SkinWardrobe> capability1 = entity -> Optional.ofNullable(entity.getCapability(capability));
        return new HolderProxy<>(registryName, SkinWardrobe.class, provider, capability1, () -> capability);
    }

    private static <T> IRegistryHolder<IBlockEntityCapability<T>> createSidedCapability(IResourceLocation registryName, BlockCapability<T, Direction> capability) {
        // register into skinnable block
        IBlockEntityCapability<T> capability1 = capability::getCapability;
        AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
            event.registerBlockEntity(capability, ModBlockEntityTypes.SKINNABLE.get().get(), (entity, context) -> {
                return entity.getCapability(capability1, context);
            });
        });
        return TypedRegistry.Entry.castValue(registryName, capability1);
    }

    private static class HolderProxy<T extends IDataSerializable.Mutable> implements IRegistryHolder<IEntityCapability<T>> {

        final IResourceLocation registryName;
        final Supplier<EntityCapability<T, Void>> capability;

        final Class<T> type;
        final IEntityCapability<T> capabilityType;
        final IRegistryHolder<AttachmentType<CapabilitySerializer<T>>> attachmentType;

        protected HolderProxy(IResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory, IEntityCapability<T> capabilityType, Supplier<EntityCapability<T, Void>> capability) {
            this.type = type;
            this.attachmentType = CapabilitySerializer.register(registryName, factory);
            this.capability = capability;
            this.capabilityType = capabilityType;
            this.registryName = registryName;
            this.register();
        }

        public void register() {
            // register into all entity.
            AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
                for (var entityType : BuiltInRegistries.ENTITY_TYPE) {
                    event.registerEntity(capability.get(), entityType, (entity, context) -> entity.getData(attachmentType).getValue());
                }
            });
        }

        @Override
        public IEntityCapability<T> get() {
            return capabilityType;
        }

        @Override
        public IResourceLocation registryName() {
            return registryName;
        }
    }

    private static class CapabilitySerializer<T extends IDataSerializable.Mutable> implements INBTSerializable<CompoundTag> {

        protected static final ArrayList<String> DATA_KEYS = new ArrayList<>();

        protected final T value;
        protected final WeakReference<Entity> entity;

        protected CapabilitySerializer(Entity entity, T value) {
            this.value = value;
            this.entity = new WeakReference<>(entity);
        }

        public static <T extends IDataSerializable.Mutable> IRegistryHolder<AttachmentType<CapabilitySerializer<T>>> register(IResourceLocation registryName, Function<Entity, Optional<T>> factory) {
            DATA_KEYS.add(registryName.toString());
            Function<IAttachmentHolder, CapabilitySerializer<T>> transformer = holder -> new CapabilitySerializer<>((Entity) holder, factory.apply((Entity) holder).orElse(null));
            return AbstractForgeRegistries.ATTACHMENT_TYPES.register(registryName.path(), () -> AttachmentType.serializable(transformer).build());
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            if (value != null) {
                var tag = new CompoundTag();
                value.serialize(SkinWardrobeStorage.encoder(entity.get(), tag));
                return tag;
            }
            return null;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
            if (value != null) {
                value.deserialize(SkinWardrobeStorage.decoder(entity.get(), tag));
            }
        }

        public T getValue() {
            return value;
        }
    }
}
