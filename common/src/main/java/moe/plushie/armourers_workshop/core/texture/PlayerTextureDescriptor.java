package moe.plushie.armourers_workshop.core.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerTextureDescriptor {

    public static final PlayerTextureDescriptor EMPTY = new PlayerTextureDescriptor(Source.NONE, null, null);

    public static final UUID NIL_UUID = new UUID(0, 0);
    private final static Cache<ItemStack, PlayerTextureDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private Source source;
    private String value;
    private GameProfile profile;

    public PlayerTextureDescriptor(Source source, String value, GameProfile profile) {
        this.source = source;
        this.value = value;
        this.profile = profile;
    }

    public PlayerTextureDescriptor(@Nullable CompoundTag nbt) {
        if (nbt != null && nbt.contains(Constants.Key.TEXTURE_URL, Constants.TagFlags.STRING)) {
            this.source = Source.URL;
            this.value = nbt.getString(Constants.Key.TEXTURE_URL);
        }
        if (nbt != null && nbt.contains(Constants.Key.TEXTURE_PROFILE, Constants.TagFlags.COMPOUND)) {
            CompoundTag tag = nbt.getCompound(Constants.Key.TEXTURE_PROFILE);
            if (tag.contains("Name", Constants.TagFlags.STRING)) {
                this.source = Source.USER;
                this.value = tag.getString("Name");
            }
            //if (tag.hasUUID("Id")) {
            //    this.source = Source.USER;
            //    this.profile = nbt.getOptionalGameProfile(Constants.Key.TEXTURE_PROFILE, null);
            //}
        }
        if (this.value == null && this.profile == null) {
            this.source = Source.NONE;
        }
    }

    public static PlayerTextureDescriptor of(ItemStack itemStack) {
        if (!itemStack.is(ModItems.MANNEQUIN.get())) {
            return EMPTY;
        }
        CompoundTag entityTag = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityTag == null || !entityTag.contains(Constants.Key.ENTITY_TEXTURE, Constants.TagFlags.COMPOUND)) {
            return EMPTY;
        }
        PlayerTextureDescriptor descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new PlayerTextureDescriptor(entityTag.getCompound(Constants.Key.ENTITY_TEXTURE));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
        return descriptor;
    }

    public static PlayerTextureDescriptor fromURL(String url) {
        return new PlayerTextureDescriptor(Source.URL, url, null);
    }

    public static PlayerTextureDescriptor fromName(String name) {
        return new PlayerTextureDescriptor(Source.USER, name, null);
    }

    public static PlayerTextureDescriptor fromProfile(GameProfile profile) {
        return new PlayerTextureDescriptor(Source.USER, profile.getName(), profile);
    }

    public static PlayerTextureDescriptor fromPlayer(Player player) {
        return fromProfile(player.getGameProfile());
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        switch (source) {
            case URL:
                if (value != null) {
                    nbt.putString(Constants.Key.TEXTURE_URL, value);
                }
                break;
            case USER:
                if (value != null) {
                    CompoundTag tag = new CompoundTag();
                    tag.putString("Name", value);
                    nbt.put(Constants.Key.TEXTURE_PROFILE, tag);
                }
                //if (profile != null) {
                //    nbt.putOptionalGameProfile(Constants.Key.TEXTURE_PROFILE, profile, null);
                //}
                break;
            case NONE:
                break;
        }
        return nbt;
    }

    public boolean isEmpty() {
        return source == Source.NONE;
    }

    @Nullable
    public String getURL() {
        if (source == Source.URL) {
            return value;
        }
        return null;
    }

    @Nullable
    public String getName() {
        if (source == Source.USER) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Nullable
    public GameProfile getProfile() {
        return profile;
    }

    public String getValue() {
        return value;
    }

    public Source getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTextureDescriptor that = (PlayerTextureDescriptor) o;
        return source == that.source && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, value);
    }

    public enum Source {
        NONE,
        USER,
        URL
    }
}
