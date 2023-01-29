package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkCubeData {

    private final SkinCubes data;
    private final ChunkPalette palette;

    private final HashMap<ISkinCubeType, Section> sections = new HashMap<>();

    public ChunkCubeData(ChunkPalette palette, SkinCubes data) {
        this.palette = palette;
        this.data = data;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        int size = stream.readInt();
        for (int i = 0; i < size; ++i) {
            Section section = new Section(null);
            section.readFromStream(stream);
            sections.put(section.cubeType, section);
        }
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        int count = data.getCubeCount();
        for (int i = 0; i < count; ++i) {
            _add(i);
        }
        stream.writeInt(sections.size());
        for (Section section : sections.values()) {
            section.writeToStream(stream);
        }
    }

    private void _add(int index) {
        ISkinCubeType cubeType = data.getCube(index).getType();
        sections.computeIfAbsent(cubeType, Section::new).add(index);
    }

    public class Section {

        private ISkinCubeType cubeType;
        private final ArrayList<Integer> indexes = new ArrayList<>();

        public Section(ISkinCubeType cubeType) {
            this.cubeType = cubeType;
        }

        public void add(int index) {
            indexes.add(index);
        }

        public void readFromStream(ChunkInputStream stream) throws IOException {
            cubeType = SkinCubeTypes.byId(stream.readByte());
            int offset = data.getCubeCount();
            int length = stream.readInt();
            data.ensureCapacity(offset + length);
            for (int i = 0; i < length; ++i) {
                int f = 0;
                int x = stream.readByte();
                int y = stream.readByte();
                int z = stream.readByte();
                SkinCube cube = new SkinCube();
                cube.setType(cubeType);
                cube.setPos(new Vector3i(x, y, z));
                for (int j = 0; (f & 0x3f) != 0x3f && j < 6; ++j) {
                    int face = stream.readByte();
                    IPaintColor color = palette.readColor(stream);
                    for (Direction dir : Direction.values()) {
                        if ((face & (1 << dir.get3DDataValue())) != 0) {
                            cube.setPaintColor(dir, color);
                        }
                    }
                    f |= face;
                }
                data.setCube(offset + i, cube);
            }
        }

        public void writeToStream(ChunkOutputStream stream) throws IOException {
            stream.writeByte(cubeType.getId());
            stream.writeInt(indexes.size());
            HashMap<IPaintColor, Integer> colors = new HashMap<>();
            for (int index : indexes) {
                ISkinCube cube = data.getCube(index);
                IVector3i pos = cube.getPos();
                stream.writeByte(pos.getX());
                stream.writeByte(pos.getY());
                stream.writeByte(pos.getZ());
                // merge all color
                colors.clear();
                for (Direction dir : Direction.values()) {
                    IPaintColor paintColor = cube.getPaintColor(dir);
                    int face = colors.getOrDefault(paintColor, 0);
                    face |= 1 << dir.get3DDataValue();
                    colors.put(paintColor, face);
                }
                // save to the stream.
                for (Map.Entry<IPaintColor, Integer> entry : colors.entrySet()) {
                    stream.writeByte(entry.getValue());
                    palette.writeColor(entry.getKey(), stream);
                }
            }
        }
    }
}
