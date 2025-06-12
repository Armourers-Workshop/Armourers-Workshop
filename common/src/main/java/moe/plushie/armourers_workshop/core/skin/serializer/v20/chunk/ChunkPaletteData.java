package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenSliceAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class ChunkPaletteData implements ChunkVariable {

    private SkinPaintColor[] paintColors;
    private final LinkedHashMap<Integer, ChunkColorSection> sections = new LinkedHashMap<>();

    private OpenSliceAccessor<SkinPaintColor> paintColorAccessor;

    private int freezeCount = 0;

    private int colorUsedIndex = 1;
    private int textureUsedIndex = 4;

    private int flags = 0;
    private int reserved = 0;

    private boolean resolved = false;

    public ChunkPaletteData(ChunkFileData fileProvider) {
        // add dependency resolve.
        fileProvider.addDependency(() -> resolved);
    }

    public void copyFrom(ChunkPaletteData palette) {
        sections.clear();
        sections.putAll(palette.sections);

        flags = palette.flags;
        reserved = palette.reserved;

        colorUsedIndex = palette.colorUsedIndex;
        textureUsedIndex = palette.textureUsedIndex;
    }

    public ChunkColorSection.ColorRef writeColor(int rawValue) {
        return _mutableSectionAt(SkinPaintTypes.NORMAL, 3).putColor(rawValue);
    }

    public ChunkColorSection.ColorRef writeColor(SkinPaintColor color) {
        int rawValue = color.rawValue();
        return _mutableSectionAt(color.paintType(), 3).putColor(rawValue);
    }

    public SkinPaintColor readColor(int index) {
        if (paintColors == null || paintColorAccessor == null) {
            return SkinPaintColor.WHITE;
        }
        var paintColor = paintColors[index];
        if (paintColor != null) {
            return paintColor;
        }
        paintColor = paintColorAccessor.get(index);
        paintColors[index] = paintColor;
        return paintColor;
    }

    public SkinPaintColor readColor(ChunkDataInputStream stream) throws IOException {
        return readColor(stream.readFixedInt(colorUsedIndex));
    }


    public ChunkTextureData.TextureRef writeTexture(OpenVector2f uv, SkinTextureData provider) {
        // texture + black(0x000000) + 0(used bytes)
        return _mutableSectionAt(SkinPaintTypes.TEXTURE, 0).putTexture(uv, provider);
    }

    public ChunkTextureData.TextureRef readTexture(OpenVector2f uv) {
        // texture + black(0x000000) + 0(used bytes)
        return _sectionAt(SkinPaintTypes.TEXTURE, 0).textureRefAt(uv);
    }

    public ChunkTextureData.OptionsRef writeTextureOptions(long options) {
        // texture + black(0x000000) + 0(used bytes)
        return _mutableSectionAt(SkinPaintTypes.TEXTURE, 0).putTextureOptions(options);
    }


    @Override
    public boolean freeze() {
        // in the second call, it means that all the colors have been written to the palette,
        // at this time the data can be frozen.
        freezeCount += 1;
        if (freezeCount <= 1) {
            return false;
        }
        if (resolved) {
            return true;
        }
        // an optimize to reduce order dependence on HashMap.
        var offset = 0;
        var sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(this::_key));
        for (var section : sortedSections) {
            // we can't freeze multiple times.
            if (!section.isResolved()) {
                section.freeze(offset);
            }
            offset += section.size();
        }
        colorUsedIndex = _used(offset);
        textureUsedIndex = 4;
        flags = (colorUsedIndex & 0x0f) | ((textureUsedIndex & 0x0f) << 4);
        for (var section : sortedSections) {
            section.freezeIndex(colorUsedIndex, textureUsedIndex);
        }
        resolved = true;
        return true;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        int offset = 0;
        int colorOffset = 0;
        flags = stream.readVarInt();
        reserved = stream.readVarInt();
        while (true) {
            var section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.put(_key(section), section);
            section.freeze(offset);
            offset += section.size();
            if (!section.isTexture()) {
                colorOffset += section.size();
            }
        }
        // yep, we have a fixed color table.
        paintColors = new SkinPaintColor[colorOffset];
        paintColorAccessor = new OpenSliceAccessor<>(Collections.compactMap(sections.values(), ColorAccessor::new));
        // regenerate index use info.
        colorUsedIndex = flags & 0x0f;
        textureUsedIndex = (flags >> 4) & 0x0f;
    }

    @Override
    public void writeToStream(ChunkOutputStream stream) throws IOException {
        // we need to make sure section in offset order.
        var sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(ChunkColorSection::startIndex));
        stream.writeVarInt(flags);
        stream.writeVarInt(reserved);
        for (var section : sortedSections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    private ChunkColorSection readSectionFromStream(ChunkInputStream stream) throws IOException {
        var total = stream.readVarInt();
        if (total == 0) {
            return null;
        }
        var paintType = SkinPaintTypes.byId(stream.readByte());
        var usedBytes = stream.readByte();
        var section = new ChunkColorSection.Immutable(total, usedBytes, paintType);
        section.readFromStream(stream);
        return section;
    }

    private void writeSectionToStream(ChunkColorSection section, ChunkOutputStream stream) throws IOException {
        if (section == null) {
            stream.writeVarInt(0);
            return;
        }
        stream.writeVarInt(section.size());
        stream.writeByte(section.paintType().id());
        stream.writeByte(section.usedBytes());
        section.writeToStream(stream);
    }

    public int colorIndexBytes() {
        return colorUsedIndex;
    }

    public int textureIndexBytes() {
        return textureUsedIndex;
    }

    public boolean isResolved() {
        return resolved;
    }

    private Integer _key(ChunkColorSection section) {
        return section.paintType().id() << 24 | section.usedBytes();
    }

    private ChunkColorSection _sectionAt(SkinPaintType paintType, int usedBytes) {
        return sections.get(paintType.id() << 24 | usedBytes);
    }

    private ChunkColorSection.Mutable _mutableSectionAt(SkinPaintType paintType, int usedBytes) {
        return (ChunkColorSection.Mutable) sections.computeIfAbsent(paintType.id() << 24 | usedBytes, k -> new ChunkColorSection.Mutable(usedBytes, paintType));
    }

    private int _used(int size) {
        for (int i = 1; i < 4; ++i) {
            if ((size >>> i * 8) == 0) {
                return i;
            }
        }
        return 4;
    }

    public static class ColorAccessor implements OpenSliceAccessor.Provider<SkinPaintColor> {

        private final ChunkColorSection section;

        public ColorAccessor(ChunkColorSection section) {
            this.section = section;
        }

        @Override
        public SkinPaintColor get(int index) {
            return section.colorAt(index);
        }

        @Override
        public int startIndex() {
            return section.startIndex();
        }

        @Override
        public int endIndex() {
            return section.endIndex();
        }
    }
}

