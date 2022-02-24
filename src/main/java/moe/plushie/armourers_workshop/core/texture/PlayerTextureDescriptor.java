package moe.plushie.armourers_workshop.core.texture;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.AWConstants;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Objects;


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
        if (nbt == null || nbt.isEmpty()) {
            this.source = Source.NONE;
            return;
        }
        this.source = Source.valueOf(nbt.getString(AWConstants.NBT.TEXTURE_TYPE));
        if (this.source == Source.URL && nbt.contains(AWConstants.NBT.TEXTURE_URL, Constants.NBT.TAG_STRING)) {
            this.url = nbt.getString(AWConstants.NBT.TEXTURE_URL);
        }
        if (this.source == Source.USER && nbt.contains(AWConstants.NBT.TEXTURE_PROFILE, Constants.NBT.TAG_COMPOUND)) {
            this.profile = NBTUtil.readGameProfile(nbt.getCompound(AWConstants.NBT.TEXTURE_PROFILE));
        }
        if (this.url == null && this.profile == null) {
            this.source = Source.NONE;
        }
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString(AWConstants.NBT.TEXTURE_TYPE, source.name());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTextureDescriptor that = (PlayerTextureDescriptor) o;
        return source == that.source && Objects.equals(url, that.url) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, url, getName());
    }

    public enum Source {
        NONE,
        USER,
        URL
    }
}
