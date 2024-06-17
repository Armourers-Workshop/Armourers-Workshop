package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.texture.SkinPreviewData;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChunkPreviewData {

    private final ChunkCubeData chunkCubeData;

    public ChunkPreviewData(ChunkCubeData chunkCubeData) {
        this.chunkCubeData = chunkCubeData;
    }

    public SkinPreviewData readFromStream(ChunkInputStream stream) throws IOException {
        ChunkTransform chunkTransform = new ChunkTransform();
        ArrayList<Pair<ISkinTransform, SkinCubes>> sections = new ArrayList<>();
        while (true) {
            int count = stream.readVarInt();
            if (count == 0) {
                break;
            }
            int id = stream.readVarInt();
            chunkTransform.readFromStream(stream);
            for (int i = 0; i < count; ++i) {
                SkinCubes cubes = chunkCubeData.readReferenceFromStream(stream);
                sections.add(Pair.of(chunkTransform.build(), cubes));
            }
        }
        return new SkinPreviewData(sections);
    }

    public void writeToStream(SkinPreviewData previewData, ChunkOutputStream stream) throws IOException {
        // freeze and combine the transform/cubes data.
        LinkedHashMap<ChunkTransform, ArrayList<SkinCubes>> sections = new LinkedHashMap<>();
        previewData.forEach((transform, cubeData) -> {
            ChunkTransform chunkTransform = ChunkTransform.flat(transform);
            sections.computeIfAbsent(chunkTransform, k -> new ArrayList<>()).add(cubeData);
        });
        for (Map.Entry<ChunkTransform, ArrayList<SkinCubes>> section : sections.entrySet()) {
            stream.writeVarInt(section.getValue().size());
            stream.writeVarInt(0);
            section.getKey().writeToStream(stream);
            for (SkinCubes cubes : section.getValue()) {
                chunkCubeData.writeReferenceToStream(cubes, stream);
            }
        }
        stream.writeVarInt(0);
    }
}
