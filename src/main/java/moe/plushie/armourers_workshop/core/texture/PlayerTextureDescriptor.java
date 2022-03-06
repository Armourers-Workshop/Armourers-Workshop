package moe.plushie.armourers_workshop.core.texture;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("NullableProblems")
public class PlayerTextureDescriptor {

    public static final PlayerTextureDescriptor EMPTY = new PlayerTextureDescriptor();

    public static final IDataSerializer<PlayerTextureDescriptor> SERIALIZER = new IDataSerializer<PlayerTextureDescriptor>() {
        public void write(PacketBuffer buffer, PlayerTextureDescriptor descriptor) {
            buffer.writeNbt(descriptor.serializeNBT());
        }

        public PlayerTextureDescriptor read(PacketBuffer buffer) {
            return new PlayerTextureDescriptor(buffer.readNbt());
        }

        public PlayerTextureDescriptor copy(PlayerTextureDescriptor descriptor) {
            return descriptor;
        }
    };

    private final static Cache<ItemStack, PlayerTextureDescriptor> DESCRIPTOR_CACHES = CacheBuilder.newBuilder()
            .maximumSize(8)
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private Source source;
    private String url;
    private GameProfile profile;

    public PlayerTextureDescriptor() {
        this.source = Source.NONE;
    }

    public PlayerTextureDescriptor(String url) {
        this.source = Source.URL;
        this.url = url;
    }

    public PlayerTextureDescriptor(GameProfile profile) {
        this.source = Source.USER;
        this.profile = profile;
    }

    public PlayerTextureDescriptor(@Nullable CompoundNBT nbt) {
        if (nbt != null && nbt.contains(AWConstants.NBT.TEXTURE_URL, Constants.NBT.TAG_STRING)) {
            this.source = Source.URL;
            this.url = nbt.getString(AWConstants.NBT.TEXTURE_URL);
        }
        if (nbt != null && nbt.contains(AWConstants.NBT.TEXTURE_PROFILE, Constants.NBT.TAG_COMPOUND)) {
            this.source = Source.USER;
            this.profile = NBTUtil.readGameProfile(nbt.getCompound(AWConstants.NBT.TEXTURE_PROFILE));
        }
        if (this.url == null && this.profile == null) {
            this.source = Source.NONE;
        }
    }

    public static PlayerTextureDescriptor of(ItemStack itemStack) {
        if (itemStack.getItem() != AWItems.MANNEQUIN) {
            return EMPTY;
        }
        CompoundNBT entityTag = MannequinItem.getEntityTag(itemStack);
        if (entityTag == null || !entityTag.contains(AWConstants.NBT.MANNEQUIN_TEXTURE, Constants.NBT.TAG_COMPOUND)) {
            return EMPTY;
        }
        PlayerTextureDescriptor descriptor = DESCRIPTOR_CACHES.getIfPresent(itemStack);
        if (descriptor != null) {
            return descriptor;
        }
        descriptor = new PlayerTextureDescriptor(entityTag.getCompound(AWConstants.NBT.MANNEQUIN_TEXTURE));
        DESCRIPTOR_CACHES.put(itemStack, descriptor);
        return descriptor;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (url != null) {
            nbt.putString(AWConstants.NBT.TEXTURE_URL, url);
        }
        if (profile != null) {
            CompoundNBT nbt1 = new CompoundNBT();
            NBTUtil.writeGameProfile(nbt1, profile);
            nbt.put(AWConstants.NBT.TEXTURE_PROFILE, nbt1);
        }
        return nbt;
    }

    public boolean isEmpty() {
        return source == Source.NONE;
    }

    @Nullable
    public String getURL() {
        return url;
    }

    @Nullable
    public String getName() {
        if (profile != null) {
            return profile.getName();
        }
        return null;
    }

    @Nullable
    public GameProfile getProfile() {
        return profile;
    }

    public Source getSource() {
        return source;
    }

    public String getValue() {
        if (source == Source.URL) {
            return getURL();
        }
        if (source == Source.USER) {
            return getName();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTextureDescriptor that = (PlayerTextureDescriptor) o;
        return source == that.source && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, getValue());
    }

    public enum Source {
        NONE,
        USER,
        URL
    }
}
