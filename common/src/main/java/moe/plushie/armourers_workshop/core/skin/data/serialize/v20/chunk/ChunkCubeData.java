package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import net.minecraft.core.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkCubeData {

    private final SkinCubes data;
    private final ChunkPalette palette;

    private final HashMap<ISkinCubeType, Section> sections = new HashMap<>();

    public ChunkCubeData(ChunkPalette palette) {
        this.data = null;
        this.palette = palette;
//        this.palette = palette;
//        this.data = data;
    }

    public ChunkCubeData(ChunkPalette palette, SkinCubes data) {
        this.palette = palette;
        this.data = data;
    }

    public void add(SkinCubes cubes) {
    }


    public void readFromStream(ChunkInputStream stream) throws IOException {
        while (true) {
            Section section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.put(section.cubeType, section);
        }
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        int count = data.getCubeCount();
        for (int i = 0; i < count; ++i) {
            _add(i);
        }
        for (Section section : sections.values()) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    private Section readSectionFromStream(ChunkInputStream stream) throws IOException {
        int length = stream.readInt();
        if (length == 0) {
            return null;
        }
        ISkinCubeType cubeType = SkinCubeTypes.byId(stream.readByte());
        int offset = data.getCubeCount();
//        data.ensureCapacity(offset + length);
//        for (int i = 0; i < length; ++i) {
//            int f = 0;
//            int x = stream.readByte();
//            int y = stream.readByte();
//            int z = stream.readByte();
//            SkinCube cube = new SkinCube();
//            cube.setType(cubeType);
//            cube.setPos(new Vector3i(x, y, z));
//            for (int j = 0; (f & 0x3f) != 0x3f && j < 6; ++j) {
//                int face = stream.readByte();
//                IPaintColor color = palette.readColor(stream);
//                for (Direction dir : Direction.values()) {
//                    if ((face & (1 << dir.get3DDataValue())) != 0) {
//                        cube.setPaintColor(dir, color);
//                    }
//                }
//                f |= face;
//            }
//            data.setCube(offset + i, cube);
//        }
        return new Section(cubeType);
    }

    private void writeSectionToStream(Section section, ChunkOutputStream stream) throws IOException {
        if (section == null) {
            stream.writeInt(0);
            return;
        }
        stream.writeInt(section.indexes.size());
        stream.writeByte(section.cubeType.getId());
        HashMap<IPaintColor, Integer> colors = new HashMap<>();
        for (int index : section.indexes) {
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
    private void _add(int index) {
        ISkinCubeType cubeType = data.getCube(index).getType();
        sections.computeIfAbsent(cubeType, Section::new).add(index);
    }

    public static class Section {

        private final ISkinCubeType cubeType;
        private final ArrayList<Integer> indexes = new ArrayList<>();

        public Section(ISkinCubeType cubeType) {
            this.cubeType = cubeType;
        }

        public void add(int index) {
            indexes.add(index);
        }
    }
}
