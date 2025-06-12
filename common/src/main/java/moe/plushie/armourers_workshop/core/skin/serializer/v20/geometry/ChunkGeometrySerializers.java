package moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileOptions;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkGeometrySlice;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl.ChunkGeometrySerializerV1;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl.ChunkGeometrySerializerV2;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.impl.ChunkGeometrySerializerV3;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ChunkGeometrySerializers {

    private static final Map<SkinGeometryType, ChunkGeometrySerializer> SERIALIZERS = Collections.immutableMap(it -> {
        it.put(SkinGeometryTypes.BLOCK_SOLID, new ChunkGeometrySerializerV1());
        it.put(SkinGeometryTypes.BLOCK_GLOWING, new ChunkGeometrySerializerV1());
        it.put(SkinGeometryTypes.BLOCK_GLASS, new ChunkGeometrySerializerV1());
        it.put(SkinGeometryTypes.BLOCK_GLASS_GLOWING, new ChunkGeometrySerializerV1());
        it.put(SkinGeometryTypes.CUBE, new ChunkGeometrySerializerV2());
        it.put(SkinGeometryTypes.CUBE_CULL, new ChunkGeometrySerializerV2());
        it.put(SkinGeometryTypes.MESH, new ChunkGeometrySerializerV3());
        it.put(SkinGeometryTypes.MESH_CULL, new ChunkGeometrySerializerV3());
    });

    public static ChunkGeometrySerializer getSerializer(SkinGeometryType geometryType) {
        return SERIALIZERS.get(geometryType);
    }

    public static int getStride(SkinGeometryType geometryType, int options, ChunkPaletteData palette) {
        return getSerializer(geometryType).stride(geometryType, options, palette);
    }

    public static ChunkGeometrySerializer.Encoder<?> createEncoder(SkinGeometryType geometryType) {
        return getSerializer(geometryType).encoder(geometryType);
    }

    public static ChunkGeometrySerializer.Decoder<?> createDecoder(SkinGeometryType geometryType, ChunkGeometrySlice slice) {
        return getSerializer(geometryType).decoder(geometryType, slice);
    }

    public static ChunkContext createEncodeContext(Skin skin, SkinFileOptions options) {
        var context = new ChunkContext(options);
        context.setFastEncoder(canFastEncoding(skin.id(), skin.parts()));
        return context;
    }

    public static ChunkContext createDecodeContext(SkinFileOptions options) {
        return new ChunkContext(options);
    }

    public static boolean canFastEncoding(int skinOwner, List<SkinPart> parts) {
        // when the skin have multiple data owner, we can't enable fast encoder,
        // because it must to recompile and resort it.
        var owners = new HashSet<>();
        owners.add(skinOwner);
        Collections.eachTree(parts, SkinPart::children, part -> {
            var geometries = part.geometries();
            if (!geometries.isEmpty()) {
                owners.add(geometries.id());
            }
        });
        return owners.size() <= 1;
    }
}
