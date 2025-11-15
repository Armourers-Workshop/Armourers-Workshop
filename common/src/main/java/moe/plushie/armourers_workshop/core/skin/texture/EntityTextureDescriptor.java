package moe.plushie.armourers_workshop.core.skin.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class EntityTextureDescriptor implements IDataSerializable.Immutable {

    public static final EntityTextureDescriptor EMPTY = new EntityTextureDescriptor(null, null, null);

    private final static Cache<ItemStack, EntityTextureDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    public static final IDataCodec<EntityTextureDescriptor> CODEC = ExtraCodecs.serializable(EntityTextureDescriptor::new);

    private Source source;
    private String value;
    private GameProfile profile;

    public EntityTextureDescriptor(Source source, String value, GameProfile profile) {
        this.source = source;
        this.value = value;
        this.profile = profile;
    }

    public EntityTextureDescriptor(IDataSerializer serializer) {
        var url = serializer.read(CodingKeys.URL);
        if (url != null) {
            this.source = Source.URL;
            this.value = url;
        }
        var info = serializer.read(CodingKeys.USER);
        if (info != null) {
            this.source = Source.USER;
            this.value = info.name;
        }
        if (this.value == null && this.profile == null) {
            this.source = null;
        }
    }

    public static EntityTextureDescriptor of(ItemStack itemStack) {
        if (!itemStack.is(ModItems.MANNEQUIN.get())) {
            return EMPTY;
        }
        var entityData = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityData == null || !entityData.contains(Constants.Key.ENTITY_TEXTURE)) {
            return EMPTY;
        }
        var descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new EntityTextureDescriptor(entityData.tag().getOptionalCompound(Constants.Key.ENTITY_TEXTURE).map(TagSerializer::new).orElseGet(TagSerializer::new));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
        return descriptor;
    }

    public static EntityTextureDescriptor fromURL(String url) {
        return new EntityTextureDescriptor(Source.URL, url, null);
    }

    public static EntityTextureDescriptor fromName(String name) {
        return new EntityTextureDescriptor(Source.USER, name, null);
    }

    public static EntityTextureDescriptor fromProfile(GameProfile profile) {
        return new EntityTextureDescriptor(Source.USER, profile.name(), profile);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        if (source == Source.URL && value != null) {
            serializer.write(CodingKeys.URL, value);
        }
        if (source == Source.USER && value != null) {
            serializer.write(CodingKeys.USER, new UserInfo(value));
        }
    }

    public boolean isEmpty() {
        return source == null;
    }

    @Nullable
    public String url() {
        if (source == Source.URL) {
            return value;
        }
        return null;
    }

    @Nullable
    public String name() {
        if (source == Source.USER) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    public GameProfile profile() {
        return profile;
    }

    public Optional<String> value() {
        return Optional.ofNullable(value);
    }

    public Optional<Source> source() {
        return Optional.ofNullable(source);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityTextureDescriptor that)) return false;
        return source == that.source && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, value);
    }

    @Override
    public String toString() {
        if (!isEmpty()) {
            return value;
        }
        return "<empty>";
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> URL = IDataSerializerKey.create("URL", IDataCodec.STRING, null);
        public static final IDataSerializerKey<UserInfo> USER = IDataSerializerKey.create("User", UserInfo.CODEC, null);
        public static final IDataSerializerKey<String> NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, "");
    }

    private static class UserInfo implements IDataSerializable.Immutable {

        public static final IDataCodec<UserInfo> CODEC = ExtraCodecs.serializable(UserInfo::new);

        private final String name;

        public UserInfo(String name) {
            this.name = name;
        }

        public UserInfo(IDataSerializer serializer) {
            this.name = serializer.read(CodingKeys.NAME);
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            serializer.write(CodingKeys.NAME, name);
        }
    }

    public enum Source {
        USER,
        URL
    }

    public enum Model {
        WIDE,
        SLIM
    }
}
