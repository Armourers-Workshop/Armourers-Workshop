package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataInputStream;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChunkPalette implements ChunkVariable {

    private final byte[] buffer = new byte[8];

    private final ArrayList<Section> sections = new ArrayList<>();
    private final HashMap<Integer, Section> fastSections = new HashMap<>();
    private final ArrayList<IPaintColor> allColors = new ArrayList<>();

    private int freezeCount = 0;
    private int usedBytes = 1;
    private boolean resolved = false;

    public void writeColor(int color, ChunkOutputStream stream) throws IOException {
        stream.writeVariable(_index(color));
    }

    public void writeColor(IPaintColor color, ChunkOutputStream stream) throws IOException {
        stream.writeVariable(_index(color.getRawValue()));
    }

    public IPaintColor readColor(ChunkInputStream stream) throws IOException {
        int index = _readFixedInt(usedBytes, stream);
        return allColors.get(index);
    }

    public void readFromStream(IDataInputStream stream) throws IOException {
        while (true) {
            Section section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.add(section);
            // only read
            for (int color : section.colors) {
                allColors.add(PaintColor.of(color, section.paintType));
            }
        }
    }

    @Override
    public void writeToStream(IDataOutputStream stream) throws IOException {
        for (Section section : sections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    @Override
    public boolean freeze() {
        // in the second call, it means that all the colors have been written to the palette,
        // at this time the data can be frozen.
        freezeCount += 1;
        if (freezeCount <= 1) {
            return false;
        }
        int usedCount = 0;
        for (Section section : sections) {
            section.offset[0] = usedCount;
            usedCount += section.colors.size();
        }
        usedBytes = _used(usedCount);
        resolved = true;
        return true;
    }

    private Index _index(int value) {
        return fastSections.computeIfAbsent(value & 0xff000000 | 0x03, k -> {
            ISkinPaintType paintType = PaintColor.getPaintType(value);
            Section section = new Section(paintType, 0x03);
            sections.add(section);
            return section;
        }).put(value);
    }

    private int _used(int size) {
        for (int i = 1; i < 4; ++i) {
            if ((size >>> i * 8) == 0) {
                return i;
            }
        }
        return 4;
    }

    private int _readFixedInt(int usedBytes, IDataInputStream stream) throws IOException {
        stream.readFully(buffer, 4 - usedBytes, usedBytes);
        int value = 0;
        for (int i = 4 - usedBytes; i < 4; ++i) {
            value = (value << 8) | (buffer[i] & 0xff);
        }
        return value;
    }

    private void _writeFixedInt(int value, int usedBytes, IDataOutputStream stream) throws IOException {
        buffer[0] = (byte) (value >>> 24);
        buffer[1] = (byte) (value >>> 16);
        buffer[2] = (byte) (value >>> 8);
        buffer[3] = (byte) (value >>> 0);
        stream.write(buffer, 4 - usedBytes, usedBytes);
    }


    private Section readSectionFromStream(IDataInputStream stream) throws IOException {
        int length = stream.readInt();
        if (length == 0) {
            return null;
        }
        ISkinPaintType paintType = SkinPaintTypes.byId(stream.readByte());
        int usedBytes = stream.readByte();
        Section section = new Section(paintType, usedBytes);
        for (int i = 0; i < length; ++i) {
            section.colors.add(_readFixedInt(usedBytes, stream));
        }
        return section;
    }

    private void writeSectionToStream(Section section, IDataOutputStream stream) throws IOException {
        if (section == null) {
            stream.writeInt(0);
            return;
        }
        stream.writeInt(section.colors.size());
        stream.writeByte(section.paintType.getId());
        stream.writeByte(section.usedBytes);
        for (int color : section.colors) {
            _writeFixedInt(color, section.usedBytes, stream);
        }
    }

    public class Section {

        private final int[] offset = {0};
        private final ArrayList<Integer> colors = new ArrayList<>();
        private final HashMap<Integer, Index> fastColors = new HashMap<>();

        private final int usedBytes;
        private final ISkinPaintType paintType;

        public Section(ISkinPaintType paintType, int usedBytes) {
            this.paintType = paintType;
            this.usedBytes = usedBytes;
        }

        public Index put(int value) {
            // if the transparent channel not used, clear it.
            if (usedBytes == 3) {
                value |= 0xff000000;
            }
            return fastColors.computeIfAbsent(value, k -> {
                Index index = new Index(colors.size(), offset);
                colors.add(k);
                return index;
            });
        }
    }

    public class Index implements ChunkVariable {

        private final int value;
        private final int[] offset;

        public Index(int value, int[] offset) {
            this.value = value;
            this.offset = offset;
        }

        @Override
        public void writeToStream(IDataOutputStream stream) throws IOException {
            _writeFixedInt(offset[0] + value, usedBytes, stream);
        }

        @Override
        public boolean freeze() {
            return resolved;
        }
    }
}

