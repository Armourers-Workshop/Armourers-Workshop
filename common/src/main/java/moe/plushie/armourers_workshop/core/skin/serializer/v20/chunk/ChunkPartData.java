package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ChunkPartData {

    final ChunkCubeData chunkCubes;

    public ChunkPartData(ChunkCubeData chunkCubes) {
        this.chunkCubes = chunkCubes;
    }

    public Collection<SkinPart> readFromStream(ChunkInputStream stream, IOConsumer2<ChunkReader, SkinPart.Builder> consumer) throws IOException {
        ChunkTransform chunkTransform = new ChunkTransform();
        ArrayList<Pair<Integer, SkinPart.Builder>> pairs = new ArrayList<>();
        HashMap<Integer, Integer> relationship = new HashMap<>();
        int count = stream.readVarInt();
        for (int i = 0; i < count; ++i) {
            int id = stream.readVarInt();
            int parentId = stream.readVarInt();
            String name = stream.readString();
            ISkinPartType partType = stream.readType(SkinPartTypes::byName);
            chunkTransform.readFromStream(stream);
            SkinCubes cubes = chunkCubes.readReferenceFromStream(stream);
            SkinPart.Builder builder = new SkinPart.Builder(partType);
            builder.name(name);
            builder.cubes(cubes);
            builder.transform(chunkTransform.build());
            pairs.add(Pair.of(id, builder));
            relationship.put(id, parentId);
        }
        return stream.readChunk(ChunkPartReader::new, it -> {
            HashMap<Integer, SkinPart> mapping = new HashMap<>();
            ArrayList<SkinPart> parts = new ArrayList<>();
            for (Pair<Integer, SkinPart.Builder> pair : pairs) {
                it.prepare(pair.getKey());
                consumer.accept(it, pair.getValue());
                SkinPart part = pair.getValue().build();
                mapping.put(pair.getKey(), part);
                parts.add(part);
            }
            for (Map.Entry<Integer, Integer> entry : relationship.entrySet()) {
                SkinPart child = mapping.get(entry.getKey());
                SkinPart parent = mapping.get(entry.getValue());
                if (child != null && parent != null && child != parent) {
                    parent.addPart(child);
                    parts.remove(child); // not a root part.
                }
            }
            return parts;
        });
    }

    public void writeToStream(ChunkOutputStream stream, Collection<SkinPart> parts, IOConsumer2<ChunkWriter, SkinPart> consumer) throws IOException {
        HashMap<Integer, Integer> relationship = new HashMap<>();
        ArrayList<Pair<Integer, SkinPart>> pairs = new ArrayList<>();
        eachPart(parts, 0, (parent, part) -> {
            int id = pairs.size() + 1;
            pairs.add(Pair.of(id, part));
            relationship.put(id, parent);
            return id;
        });
        stream.writeVarInt(pairs.size());
        for (Pair<Integer, SkinPart> pair : pairs) {
            int id = pair.getKey();
            SkinPart part = pair.getRight();
            stream.writeVarInt(id);
            stream.writeVarInt(relationship.getOrDefault(id, 0));
            stream.writeString(part.getName());
            stream.writeType(part.getType());
            ChunkTransform transform = ChunkTransform.of(part.getTransform());
            transform.writeToStream(stream);
            chunkCubes.writeReferenceToStream(part.getCubeData(), stream);
        }
        stream.writeChunk(ChunkPartWriter::new, it -> {
            for (Pair<Integer, SkinPart> pair : pairs) {
                it.prepare(pair.getKey());
                consumer.accept(it, pair.getValue());
            }
        });
    }

    private void eachPart(Collection<SkinPart> parts, Integer parent, BiFunction<Integer, SkinPart, Integer> consumer) {
        for (SkinPart part : parts) {
            Integer value = consumer.apply(parent, part);
            eachPart(part.getParts(), value, consumer);
        }
    }
}

