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
public class TextureDescriptor {

    public static final TextureDescriptor EMPTY = new TextureDescriptor();

    public static final IDataSerializer<TextureDescriptor> SERIALIZER = new IDataSerializer<TextureDescriptor>() {
        public void write(PacketBuffer buffer, TextureDescriptor descriptor) {
            buffer.writeNbt(descriptor.serializeNBT());
        }

        public TextureDescriptor read(PacketBuffer buffer) {
            return new TextureDescriptor(buffer.readNbt());
        }

        public TextureDescriptor copy(TextureDescriptor descriptor) {
            return descriptor;
        }
    };

    private Source source;
    private String url;
    private GameProfile profile;

    public TextureDescriptor() {
        this.source = Source.NONE;
    }

    public TextureDescriptor(@Nullable CompoundNBT nbt) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextureDescriptor that = (TextureDescriptor) o;
        return source == that.source && Objects.equals(url, that.url) && Objects.equals(profile, that.profile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, url, profile);
    }

    public enum Source {
        NONE,
        USER,
        URL
    }
}
