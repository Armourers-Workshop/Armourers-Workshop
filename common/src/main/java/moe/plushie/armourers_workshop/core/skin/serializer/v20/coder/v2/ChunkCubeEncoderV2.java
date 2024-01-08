package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v2;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer2;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeEncoder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import moe.plushie.armourers_workshop.utils.texture.TextureOptions;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChunkCubeEncoderV2 extends ChunkCubeEncoder {

    private ISkinCube cube;

    private final SortedMap<Vector2f> startValues = new SortedMap<>();
    private final SortedMap<Vector2f> endValues = new SortedMap<>();
    private final SortedMap<TextureOptions> optionsValues = new SortedMap<>();

    @Override
    public int begin(ISkinCube cube) {
        // merge all values
        for (Direction dir : Direction.values()) {
            ITextureKey value = cube.getTexture(dir);
            if (value == null) {
                continue;
            }
            ITextureProvider provider = value.getProvider();
            TextureBox.Entry entry = ObjectUtils.safeCast(value, TextureBox.Entry.class);
            if (entry != null) {
                startValues.put(0x80, entry.getParent(), provider);
                // box need options?
                continue;
            }
            int face = 1 << dir.get3DDataValue();
            float u = value.getU();
            float v = value.getV();
            float s = value.getWidth();
            float t = value.getHeight();
            startValues.put(face, new Vector2f(u, v), provider);
            endValues.put(face, new Vector2f(u + s, v + t), provider);
            if (value.getOptions() instanceof TextureOptions) {
                optionsValues.put(face, (TextureOptions) value.getOptions(), provider);
            }
        }
        this.cube = cube;
        return startValues.size() + endValues.size() + optionsValues.size();
    }

    @Override
    public void end(ChunkPaletteData palette, ChunkOutputStream stream) throws IOException {
        // rectangle(24B) + transform(64b)
        stream.writeRectangle3f(cube.getShape());
        stream.writeTransformf(cube.getTransform());

        // face: <texture ref>
        optionsValues.forEach((key, value) -> {
            stream.writeByte(0x40 | value);
            stream.writeVariable(palette.writeTextureOptions(key.getLeft(), key.getRight()));
        });
        startValues.forEach((key, value) -> {
            stream.writeByte(value);
            stream.writeVariable(palette.writeTexture(key.getLeft(), key.getRight()));
        });
        endValues.forEach((key, value) -> {
            stream.writeByte(value);
            stream.writeVariable(palette.writeTexture(key.getLeft(), key.getRight()));
        });

        startValues.clear();
        endValues.clear();
        optionsValues.clear();
        cube = null;
    }

    public static class SortedMap<T> {

        private final LinkedHashMap<Pair<T, ITextureProvider>, Integer> impl = new LinkedHashMap<>();

        public void forEach(IOConsumer2<Pair<T, ITextureProvider>, Integer> consumer) throws IOException {
            for (Map.Entry<Pair<T, ITextureProvider>, Integer> entry : impl.entrySet()) {
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }

        public void put(int face, T pos, ITextureProvider provider) {
            Pair<T, ITextureProvider> index = Pair.of(pos, provider);
            int newFace = impl.getOrDefault(index, 0);
            newFace |= face;
            impl.put(index, newFace);
        }

        public void clear() {
            impl.clear();
        }

        public int size() {
            return impl.size();
        }
    }
}
