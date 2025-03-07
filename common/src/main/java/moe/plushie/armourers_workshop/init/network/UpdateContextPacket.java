package moe.plushie.armourers_workshop.init.network;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModContext;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class UpdateContextPacket extends CustomPacket {

    private UUID owner = null;
    private ByteBuf buffer = null;

    private final int flags;

    public UpdateContextPacket(int flags, UUID player) {
        this.flags = flags;
        this.owner = player;
    }

    public UpdateContextPacket(IFriendlyByteBuf buffer) {
        this.flags = buffer.readByte();
        this.buffer = buffer.asByteBuf();
        this.buffer.retain();
    }

    public static UpdateContextPacket sync(Player player) {
        int flags = 0x01;
        if (EnvironmentManager.isDedicatedServer()) {
            flags |= 0x02;
            flags |= 0x04;
            // TODO: @SAGESSE The single player server is work?
        }
        return new UpdateContextPacket(flags, player.getUUID());
    }

    public static UpdateContextPacket config() {
        return new UpdateContextPacket(0x02, null);
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeByte(flags);
        if ((flags & 0x01) != 0) {
            buffer.writeUUID(ModContext.t2(owner));
            buffer.writeUUID(ModContext.t3(owner));
            buffer.writeLong(ModContext.time());
            buffer.writeUtf(ModConstants.MOD_NET_ID);
        }
        if ((flags & 0x02) != 0) {
            buffer.writeNbt(getConfig());
        }
        if ((flags & 0x04) != 0) {
            buffer.writeNbt(getDataPack());
        }
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        if (buffer == null) {
            return;
        }
        var reader = IFriendlyByteBuf.wrap(buffer);
        if ((flags & 0x01) != 0) {
            ModContext.init(reader.readUUID(), reader.readUUID());
            TickUtils.setTime(reader.readLong());
            checkNetworkVersion(reader.readUtf());
        }
        if ((flags & 0x02) != 0) {
            setConfig(reader.readNbt());
        }
        if ((flags & 0x04) != 0) {
            setDataPack(reader.readNbt());
        }
        buffer.release();
        buffer = null;
    }


    private void setConfig(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        var fields = new HashMap<String, Object>();
        var properties = new SkinProperties(tag);
        for (var entry : properties.entrySet()) {
            fields.put(entry.getKey(), entry.getValue());
        }
        ModConfigSpec.COMMON.apply(fields);
    }

    private CompoundTag getConfig() {
        var properties = new SkinProperties();
        ModConfigSpec.COMMON.snapshot().forEach(properties::put);
        return properties.serializeNBT();
    }

    private void setDataPack(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        var pack = new DataPack(new TagSerializer(tag));
    }

    private CompoundTag getDataPack() {
        var dataPack = new DataPack();
        var serializer = new TagSerializer();
        dataPack.serialize(serializer);
        return serializer.getTag();
    }

    private void checkNetworkVersion(String version) {
        if (!version.equals(ModConstants.MOD_NET_ID)) {
            ModLog.warn("network protocol conflict, server: {}, client: {}", version, ModConstants.MOD_NET_ID);
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<List<String>> ENTITIES = IDataSerializerKey.create("Entities", IDataCodec.STRING.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<List<CompoundTag>> PROFILES = IDataSerializerKey.create("Profiles", IDataCodec.COMPOUND_TAG.listOf(), Collections.emptyList());
    }

    private static class DataPack implements IDataSerializable.Immutable {

        public DataPack() {
        }

        public DataPack(IDataSerializer serializer) {
            // customized entity profile.
            var profiles = new LinkedHashMap<IEntityTypeProvider<?>, EntityProfile>();
            var profileTags = serializer.read(CodingKeys.PROFILES);
            profileTags.forEach(tag -> {
                var serializer1 = new TagSerializer(tag);
                var profile = new EntityProfile(serializer1);
                var entities = serializer1.read(CodingKeys.ENTITIES);
                entities.forEach(it -> profiles.put(IEntityTypeProvider.of(it), profile));
            });
            ModEntityProfiles.setCustomProfiles(profiles);
            // ...
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            // customized entity profile.
            var profileTags = new ArrayList<CompoundTag>();
            var profiles = new LinkedHashMap<EntityProfile, ArrayList<String>>();
            ModEntityProfiles.getCustomProfiles().forEach((entityType, profile) -> {
                profiles.computeIfAbsent(profile, e -> new ArrayList<>()).add(entityType.getRegistryName());
            });
            profiles.forEach((profile, entities) -> {
                var serializer1 = new TagSerializer();
                profile.serialize(serializer1);
                serializer1.write(CodingKeys.ENTITIES, entities);
                profileTags.add(serializer1.getTag());
            });
            serializer.write(CodingKeys.PROFILES, profileTags);
            // ...
        }
    }
}
