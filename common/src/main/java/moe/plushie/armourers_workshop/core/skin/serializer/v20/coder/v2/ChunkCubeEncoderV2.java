package moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.v2;

import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk.ChunkPaletteData;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeEncoder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.LinkedHashMap;

import manifold.ext.rt.api.auto;

public class ChunkCubeEncoderV2 extends ChunkCubeEncoder {

    private ISkinCube cube;

    private final SortedMap startValues = new SortedMap();
    private final SortedMap endValues = new SortedMap();

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
                startValues.put(entry.getParent(), provider, 0x80);
                continue;
            }
            float u = value.getU();
            float v = value.getV();
            float s = value.getWidth();
            float t = value.getHeight();
            startValues.put(new Vector2f(u, v), provider, 1 << dir.get3DDataValue());
            endValues.put(new Vector2f(u + s, v + t), provider, 1 << dir.get3DDataValue());
        }
        this.cube = cube;
        return this.startValues.size() + this.endValues.size();
    }

    @Override
    public void end(ChunkPaletteData palette, ChunkOutputStream stream) throws IOException {
        // origin(12B)/size(12B)
        IRectangle3f shape = cube.getShape();
        stream.writeFloat(shape.getX());
        stream.writeFloat(shape.getY());
        stream.writeFloat(shape.getZ());
        stream.writeFloat(shape.getWidth());
        stream.writeFloat(shape.getHeight());
        stream.writeFloat(shape.getDepth());

        // translate(12B)/rotation(12B)/scale(12B)/pivot(12B)
        ITransformf transform = cube.getTransform();
        stream.writeVector3f(transform.getTranslate());
        stream.writeVector3f(transform.getRotation());
        stream.writeVector3f(transform.getScale());
        stream.writeVector3f(transform.getPivot());

        // face: <texture ref>
        for (auto entry : Iterables.concat(startValues.entrySet(), endValues.entrySet())) {
            stream.writeByte(entry.getValue());
            stream.writeVariable(palette.writeTexture(entry.getKey().getLeft(), entry.getKey().getRight()));
        }

        startValues.clear();
        endValues.clear();
        cube = null;
    }

    public static class SortedMap extends LinkedHashMap<Pair<Vector2f, ITextureProvider>, Integer> {

        public void put(Vector2f pos, ITextureProvider provider, int flag) {
            Pair<Vector2f, ITextureProvider> index = Pair.of(pos, provider);
            int face = getOrDefault(index, 0);
            face |= flag;
            put(index, face);
        }
    }
}
