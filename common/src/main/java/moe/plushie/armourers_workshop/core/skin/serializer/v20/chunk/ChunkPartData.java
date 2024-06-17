package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

public class ChunkPartData {

    final ChunkCubeData chunkCubes;

    public ChunkPartData(ChunkCubeData chunkCubes) {
        this.chunkCubes = chunkCubes;
    }

    public Collection<SkinPart> readFromStream(ChunkInputStream stream, IOConsumer2<ChunkReader, SkinPart.Builder> consumer) throws IOException {
        var chunkTransform = new ChunkTransform();
        var pairs = new ArrayList<Pair<Integer, SkinPart.Builder>>();
        var relationship = new LinkedHashMap<Integer, Integer>();
        int count = stream.readVarInt();
        for (var i = 0; i < count; ++i) {
            var id = stream.readVarInt();
            var parentId = stream.readVarInt();
            var name = stream.readString();
            var partType = stream.readType(SkinPartTypes::byName);
            chunkTransform.readFromStream(stream);
            var cubes = chunkCubes.readReferenceFromStream(stream);
            var builder = new SkinPart.Builder(partType);
            builder.name(name);
            builder.cubes(cubes);
            builder.transform(chunkTransform.build());
            pairs.add(Pair.of(id, builder));
            relationship.put(id, parentId);
        }
        return stream.readChunk(ChunkPartReader::new, it -> {
            var mapping = new HashMap<Integer, SkinPart>();
            var parts = new ArrayList<SkinPart>();
            for (var pair : pairs) {
                it.prepare(pair.getKey());
                consumer.accept(it, pair.getValue());
                var part = pair.getValue().build();
                mapping.put(pair.getKey(), part);
                parts.add(part);
            }
            for (var entry : relationship.entrySet()) {
                var child = mapping.get(entry.getKey());
                var parent = mapping.get(entry.getValue());
                if (child != null && parent != null && child != parent) {
                    parent.addPart(child);
                    parts.remove(child); // not a root part.
                }
            }
            return parts;
        });
    }

    public void writeToStream(ChunkOutputStream stream, Collection<SkinPart> parts, IOConsumer2<ChunkWriter, SkinPart> consumer) throws IOException {
        var relationship = new HashMap<Integer, Integer>();
        var pairs = new ArrayList<Pair<Integer, SkinPart>>();
        eachPart(parts, 0, (parent, part) -> {
            var id = pairs.size() + 1;
            pairs.add(Pair.of(id, part));
            relationship.put(id, parent);
            return id;
        });
        stream.writeVarInt(pairs.size());
        for (var pair : pairs) {
            var id = pair.getKey();
            var part = pair.getRight();
            stream.writeVarInt(id);
            stream.writeVarInt(relationship.getOrDefault(id, 0));
            stream.writeString(part.getName());
            stream.writeType(part.getType());
            var transform = ChunkTransform.of(part.getTransform());
            transform.writeToStream(stream);
            chunkCubes.writeReferenceToStream(part.getCubeData(), stream);
        }
        stream.writeChunk(ChunkPartWriter::new, it -> {
            for (var pair : pairs) {
                it.prepare(pair.getKey());
                consumer.accept(it, pair.getValue());
            }
        });
    }

    private void eachPart(Collection<SkinPart> parts, Integer parent, BiFunction<Integer, SkinPart, Integer> consumer) {
        for (var part : parts) {
            var value = consumer.apply(parent, part);
            eachPart(part.getParts(), value, consumer);
        }
    }
}

