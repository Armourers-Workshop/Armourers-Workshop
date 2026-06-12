package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkinModel;
import moe.plushie.armourers_workshop.init.platform.Platform;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModEntitySerializers {

    public static final EntityDataSerializer<Integer> INT = EntityDataSerializers.INT;
    public static final EntityDataSerializer<String> STRING = EntityDataSerializers.STRING;
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializers.BOOLEAN;
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializers.FLOAT;

    public static final IRegistryHolder<EntityDataSerializer<PlayerSkinDescriptor>> PLAYER_TEXTURE = normal(DataSerializers.PLAYER_TEXTURE).build("player_texture");
    public static final IRegistryHolder<EntityDataSerializer<PlayerSkinModel>> PLAYER_TEXTURE_MODEL = normal(DataSerializers.PLAYER_TEXTURE_MODEL).build("player_texture_model");

    private static <T> IEntitySerializerBuilder<T> normal(IEntityDataSerializer<T> serializer) {
        return Platform.get().common().builder().entitySerializer(serializer);
    }

    public static void init() {
    }
}
