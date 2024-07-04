package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkContext;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSection;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkCubeSelector;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v1.ChunkCubeDecoderV1;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v1.ChunkCubeEncoderV1;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v2.ChunkCubeDecoderV2;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v2.ChunkCubeEncoderV2;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.util.HashSet;
import java.util.List;

public class ChunkCubeCoders {

    private static final ThreadLocal<ChunkCubeEncoderV1> SHARED_ENCODER_V1 = ThreadLocal.withInitial(ChunkCubeEncoderV1::new);
    private static final ThreadLocal<ChunkCubeEncoderV2> SHARED_ENCODER_V2 = ThreadLocal.withInitial(ChunkCubeEncoderV2::new);


    public static ChunkCubeEncoder createEncoder(ISkinCubeType cubeType) {
        // ..
        if (cubeType == SkinCubeTypes.TEXTURE) {
            return SHARED_ENCODER_V2.get();
        }
        return SHARED_ENCODER_V1.get();
    }

    public static ChunkCubeDecoder createDecoder(int startIndex, int endIndex, ChunkCubeSelector selector, ChunkCubeSection.Immutable section) {
        // ..
        var cubeType = section.getCubeType();
        if (cubeType == SkinCubeTypes.TEXTURE) {
            return new ChunkCubeDecoderV2(startIndex, endIndex, selector, section);
        }
        return new ChunkCubeDecoderV1(startIndex, endIndex, selector, section);
    }

    public static ChunkContext createEncodeContext(Skin skin) {
        var context = new ChunkContext(skin.getVersion());
        context.setFastEncoder(canFastEncoding(skin.getParts()));
        return context;
    }

    public static ChunkContext createDecodeContext(int fileVersion) {
        return new ChunkContext(fileVersion);
    }

    public static int getStride(int options, ISkinCubeType cubeType, ChunkPaletteData palette) {
        // ..
        if (cubeType == SkinCubeTypes.TEXTURE) {
            return ChunkCubeDecoderV2.getStride(options, palette);
        }
        return ChunkCubeDecoderV1.getStride(options, palette);
    }

    public static boolean canFastEncoding(List<SkinPart> parts) {
        // when the skin have multiple data owner, we can't enable fast encoder,
        // because it must to recompile and resort it.
        var owners = new HashSet<Integer>();
        ObjectUtils.search(parts, SkinPart::getParts, part -> {
            var cubeData = part.getCubeData();
            if (cubeData.getCubeTotal() != 0) {
                owners.add(cubeData.getOwner());
            }
        });
        return owners.size() <= 1 && !owners.contains(-1);
    }
}
