package moe.plushie.armourers_workshop.core.skin.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compat.client.AbstractGameProfile;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.Strings;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlayerSkinDescriptor implements IDataSerializable.Immutable {

    public static final PlayerSkinDescriptor DEFAULT = new PlayerSkinDescriptor("", null, null, PlayerSkinModel.WIDE);

    private final static Cache<ItemStack, PlayerSkinDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    public static final IDataCodec<PlayerSkinDescriptor> CODEC = ExtraCodecs.serializable(PlayerSkinDescriptor::new);

    private final String url;
    private final String name;
    private final PlayerSkinModel model;

    private final OpenGameProfile profile;

    protected PlayerSkinDescriptor(String name, String url, OpenGameProfile profile, PlayerSkinModel model) {
        this.url = url;
        this.name = name;
        this.profile = profile;
        this.model = model;
    }

    public PlayerSkinDescriptor(IDataSerializer serializer) {
        this.url = serializer.read(CodingKeys.URL);
        this.name = UserInfo.readName(serializer);
        this.profile = null;
        this.model = serializer.read(CodingKeys.MODEL);
    }

    public static PlayerSkinDescriptor fromURL(String url) {
        return new PlayerSkinDescriptor(null, url, null, PlayerSkinModel.WIDE);
    }

    public static PlayerSkinDescriptor fromName(String name) {
        return new PlayerSkinDescriptor(name, null, null, PlayerSkinModel.WIDE);
    }

    public static PlayerSkinDescriptor fromProfile(OpenGameProfile profile) {
        return new PlayerSkinDescriptor(profile.name(), null, profile, PlayerSkinModel.WIDE);
    }

    public static PlayerSkinDescriptor fromPlayer(Player player) {
        return fromProfile(AbstractGameProfile.wrap(player.getGameProfile()));
    }

    public static PlayerSkinDescriptor of(ItemStack itemStack) {
        if (!itemStack.is(ModItems.MANNEQUIN.get())) {
            return DEFAULT;
        }
        var entityData = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityData == null || !entityData.contains(Constants.Key.ENTITY_TEXTURE)) {
            return DEFAULT;
        }
        var descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new PlayerSkinDescriptor(entityData.tag().getOptionalCompound(Constants.Key.ENTITY_TEXTURE).map(TagSerializer::new).orElseGet(TagSerializer::new));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
        return descriptor;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        if (url != null) {
            serializer.write(CodingKeys.URL, url);
        }
        if (name != null) {
            serializer.write(CodingKeys.NAME, name);
        }
        serializer.write(CodingKeys.MODEL, model);
    }

    public PlayerSkinDescriptor withModel(PlayerSkinModel model) {
        if (this.model != model) {
            return new PlayerSkinDescriptor(name, url, profile, model);
        }
        return this;
    }

    public boolean isEmpty() {
        return Strings.isEmpty(url) && Strings.isEmpty(name) && model == PlayerSkinModel.WIDE;
    }

    @Nullable
    public String url() {
        return url;
    }

    @Nullable
    public String name() {
        return name;
    }

    @Nullable
    public OpenGameProfile profile() {
        return profile;
    }

    public Optional<String> value() {
        if (url != null) {
            return Optional.of(url);
        }
        if (name != null) {
            return Optional.of(name);
        }
        return Optional.empty();
    }

    public Optional<Source> source() {
        if (url != null) {
            return Optional.of(Source.URL);
        }
        if (name != null) {
            return Optional.of(Source.USER);
        }
        return Optional.empty();
    }

    public PlayerSkinModel model() {
        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerSkinDescriptor that)) return false;
        return model == that.model && Objects.equals(url, that.url) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, url, name);
    }

    @Override
    public String toString() {
        if (!isEmpty()) {
            if (url != null) {
                return url;
            }
            if (name != null) {
                return name;
            }
        }
        return "<default>";
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> URL = IDataSerializerKey.create("URL", IDataCodec.STRING, null);
        public static final IDataSerializerKey<String> NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, null);
        public static final IDataSerializerKey<PlayerSkinModel> MODEL = IDataSerializerKey.create("Model", PlayerSkinModel.CODEC, PlayerSkinModel.WIDE);

        public static final IDataSerializerKey<UserInfo> USER = IDataSerializerKey.create("User", UserInfo.CODEC, null);
    }

    private static class UserInfo implements Immutable {

        public static final IDataCodec<UserInfo> CODEC = ExtraCodecs.serializable(UserInfo::new);

        private final String name;

        public UserInfo(IDataSerializer serializer) {
            this.name = serializer.read(CodingKeys.NAME);
        }

        private static String readName(IDataSerializer serializer) {
            var userInfo = serializer.read(CodingKeys.USER);
            if (userInfo != null) {
                return userInfo.name;
            }
            return serializer.read(CodingKeys.NAME);
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
}
