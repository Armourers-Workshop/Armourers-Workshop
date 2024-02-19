package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.common.ITagRepresentable;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.21, )")
public class AbstractForgeCapabilityManager {

    // migrate `ForgeCap` to `neoforge:attachments`.
    public static void migrate(Entity entity, CompoundTag src, CompoundTag dest, String reason) {
        // fast check by the old key.
        if (!src.contains(Constants.Key.OLD_CAPABILITY, Constants.TagFlags.COMPOUND)) {
            return;
        }
        CompoundTag oldCaps = src.getCompound(Constants.Key.OLD_CAPABILITY);
        CompoundTag newCaps = new CompoundTag();
        // we only copy we own data.
        Serializer.DATA_KEYS.forEach(key -> {
            Tag tag = oldCaps.get(key);
            if (tag != null) {
                newCaps.put(key, tag);
            }
        });
        if (newCaps.isEmpty()) {
            return;
        }
        // add or merge to `neoforge:attachments`.
        if (dest.contains(Constants.Key.NEW_CAPABILITY, Constants.TagFlags.COMPOUND)) {
            CompoundTag caps1 = dest.getCompound(Constants.Key.NEW_CAPABILITY);
            caps1.merge(newCaps);
        } else {
            dest.put(Constants.Key.NEW_CAPABILITY, newCaps);
        }
    }

    public static <T> IRegistryKey<ICapabilityType<T>> register(ResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory) {
        if (type == SkinWardrobe.class) {
            return ObjectUtils.unsafeCast(createWardrobeCapabilityType(registryName, ObjectUtils.unsafeCast(factory)));
        }
        throw new AssertionError();
    }

    private static IRegistryKey<ICapabilityType<SkinWardrobe>> createWardrobeCapabilityType(ResourceLocation registryName, Function<Entity, Optional<SkinWardrobe>> provider) {
        EntityCapability<SkinWardrobe, Void> capability = EntityCapability.createVoid(registryName, SkinWardrobe.class);
        ICapabilityType<SkinWardrobe> capabilityType = entity -> Optional.ofNullable(entity.getCapability(capability));
        return new Proxy<>(registryName, SkinWardrobe.class, provider, capabilityType, () -> capability);
    }

    public static class Proxy<T extends ITagRepresentable<CompoundTag>> implements IRegistryKey<ICapabilityType<T>> {

        final ResourceLocation registryName;
        final Supplier<EntityCapability<T, Void>> capability;

        final Class<T> type;
        final ICapabilityType<T> capabilityType;
        final IRegistryKey<AttachmentType<Serializer<T>>> attachmentType;

        protected Proxy(ResourceLocation registryName, Class<T> type, Function<Entity, Optional<T>> factory, ICapabilityType<T> capabilityType, Supplier<EntityCapability<T, Void>> capability) {
            this.type = type;
            this.attachmentType = Serializer.register(registryName, factory);
            this.capability = capability;
            this.capabilityType = capabilityType;
            this.registryName = registryName;
            this.register();
        }

        public void register() {
            AbstractForgeEventBus.observer(RegisterCapabilitiesEvent.class, event -> {
                for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
                    event.registerEntity(capability.get(), entityType, (entity, context) -> entity.getData(attachmentType).getValue());
                }
            });
        }

        @Override
        public ICapabilityType<T> get() {
            return capabilityType;
        }

        @Override
        public ResourceLocation getRegistryName() {
            return registryName;
        }
    }

    public static class Serializer<T extends ITagRepresentable<CompoundTag>> implements INBTSerializable<CompoundTag> {

        protected static final ArrayList<String> DATA_KEYS = new ArrayList<>();

        protected final T value;

        protected Serializer(T value) {
            this.value = value;
        }

        public static <T extends ITagRepresentable<CompoundTag>> IRegistryKey<AttachmentType<Serializer<T>>> register(ResourceLocation registryName, Function<Entity, Optional<T>> factory) {
            DATA_KEYS.add(registryName.toString());
            Function<IAttachmentHolder, Serializer<T>> transformer = holder -> new Serializer<>(factory.apply((Entity) holder).orElse(null));
            return AbstractForgeRegistries.ATTACHMENT_TYPES.register(registryName.getPath(), () -> AttachmentType.serializable(transformer).build());
        }

        @Override
        public CompoundTag serializeNBT() {
            return value.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            value.deserializeNBT(tag);
        }

        public T getValue() {
            return value;
        }
    }
}
