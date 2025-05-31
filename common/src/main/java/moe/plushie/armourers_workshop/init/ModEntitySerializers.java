package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IEntitySerializer;
import moe.plushie.armourers_workshop.api.registry.IEntryBuilder;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureModel;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModEntitySerializers {

    public static final EntityDataSerializer<CompoundTag> COMPOUND_TAG = EntityDataSerializers.COMPOUND_TAG;
    public static final EntityDataSerializer<Integer> INT = EntityDataSerializers.INT;
    public static final EntityDataSerializer<String> STRING = EntityDataSerializers.STRING;
    public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializers.BOOLEAN;
    public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializers.FLOAT;

    public static final EntityDataSerializer<EntityTextureDescriptor> PLAYER_TEXTURE = of(DataSerializers.PLAYER_TEXTURE).build("player_texture");
    public static final EntityDataSerializer<EntityTextureModel.Type> PLAYER_TEXTURE_MODEL = of(DataSerializers.PLAYER_TEXTURE_MODEL).build("player_texture_model");

    private static <T> IEntryBuilder<EntityDataSerializer<T>> of(IEntitySerializer<T> serializer) {
        return BuilderManager.getInstance().createEntitySerializerBuilder(serializer);
    }

    public static void init() {
    }
}
