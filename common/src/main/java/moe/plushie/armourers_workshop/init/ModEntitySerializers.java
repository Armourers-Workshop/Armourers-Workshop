package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntityDataSerializer;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IEntitySerializerBuilder;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModEntitySerializers {

    public static final EntityDataSerializer<Integer> INT = EntityDataSerializers.INT;
    public static final EntityDataSerializer<String> STRING = EntityDataSerializers.STRING;
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializers.BOOLEAN;
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializers.FLOAT;

    public static final IRegistryHolder<EntityDataSerializer<EntityTextureDescriptor>> PLAYER_TEXTURE = normal(DataSerializers.PLAYER_TEXTURE).build("player_texture");
    public static final IRegistryHolder<EntityDataSerializer<EntityTextureDescriptor.Model>> PLAYER_TEXTURE_MODEL = normal(DataSerializers.PLAYER_TEXTURE_MODEL).build("player_texture_model");

    private static <T> IEntitySerializerBuilder<T> normal(IEntityDataSerializer<T> serializer) {
        return BuilderManager.getInstance().createEntitySerializerBuilder(serializer);
    }

    public static void init() {
    }
}
