package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SliceRandomlyAccessor;
import moe.plushie.armourers_workshop.utils.math.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ChunkPaletteData implements ChunkVariable {

    private IPaintColor[] paintColors;
    private final HashMap<Integer, ChunkColorSection> sections = new HashMap<>();

    private SliceRandomlyAccessor<IPaintColor> paintColorAccessor;

    private int freezeCount = 0;

    private int colorUsedIndex = 1;
    private int textureUsedIndex = 4;

    private int flags = 0;
    private int reserved = 0;

    private boolean resolved = false;

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

    public ChunkColorSection.ColorRef writeColor(IPaintColor color) {
        int rawValue = color.getRawValue();
        return _mutableSectionAt(color.getPaintType(), 3).putColor(rawValue);
    }

    public IPaintColor readColor(int index) {
        if (paintColors == null || paintColorAccessor == null) {
            return PaintColor.WHITE;
        }
        IPaintColor paintColor = paintColors[index];
        if (paintColor != null) {
            return paintColor;
        }
        paintColor = paintColorAccessor.get(index);
        paintColors[index] = paintColor;
        return paintColor;
    }

    public IPaintColor readColor(ChunkInputStream stream) throws IOException {
        return readColor(ChunkColorSection.ColorRef.readFromStream(colorUsedIndex, stream));
    }

    public IPaintColor readColorFromStream(byte[] bytes, int offset) {
        return readColor(ChunkColorSection.ColorRef.readFromStream(colorUsedIndex, offset, bytes));
    }

    public ChunkColorSection.TextureRef writeTexture(Vector2f uv, ITextureProvider provider) {
        // texture + black(0x000000) + 0(used bytes)
        return _mutableSectionAt(SkinPaintTypes.TEXTURE, 0).putTexture(uv, provider);
    }

    public ChunkColorSection.TextureRef readTexture(Vector2f uv) {
        // texture + black(0x000000) + 0(used bytes)
        return _sectionAt(SkinPaintTypes.TEXTURE, 0).getTexture(uv);
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
        int offset = 0;
        ArrayList<ChunkColorSection> sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(this::_key));
        for (ChunkColorSection section : sortedSections) {
            // we can't freeze multiple times.
            if (!section.isResolved()) {
                section.freeze(offset);
            }
            offset += section.getTotal();
        }
        colorUsedIndex = _used(offset);
        textureUsedIndex = 4;
        flags = (colorUsedIndex & 0x0f) | ((textureUsedIndex & 0x0f) << 4);
        for (ChunkColorSection section : sortedSections) {
            section.freezeIndex(colorUsedIndex, textureUsedIndex);
        }
        resolved = true;
        return true;
    }

    public void readFromStream(IInputStream stream) throws IOException {
        int offset = 0;
        int colorOffset = 0;
        flags = stream.readVarInt();
        reserved = stream.readVarInt();
        while (true) {
            ChunkColorSection section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.put(_key(section), section);
            section.freeze(offset);
            offset += section.getTotal();
            if (!section.isTexture()) {
                colorOffset += section.getTotal();
            }
        }
        // yep, we have a fixed color table.
        paintColors = new IPaintColor[colorOffset];
        paintColorAccessor = new SliceRandomlyAccessor<>(ObjectUtils.map(sections.values(), ColorAccessor::new));
        // regenerate index use info.
        colorUsedIndex = flags & 0x0f;
        textureUsedIndex = (flags >> 4) & 0x0f;
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        // we need to make sure section in offset order.
        ArrayList<ChunkColorSection> sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(ChunkColorSection::getStartIndex));
        stream.writeVarInt(flags);
        stream.writeVarInt(reserved);
        for (ChunkColorSection section : sortedSections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    private ChunkColorSection readSectionFromStream(IInputStream stream) throws IOException {
        int total = stream.readVarInt();
        if (total == 0) {
            return null;
        }
        ISkinPaintType paintType = SkinPaintTypes.byId(stream.readByte());
        int usedBytes = stream.readByte();
        ChunkColorSection.Immutable section = new ChunkColorSection.Immutable(total, usedBytes, paintType);
        section.readFromStream(stream);
        return section;
    }

    private void writeSectionToStream(ChunkColorSection section, IOutputStream stream) throws IOException {
        if (section == null) {
            stream.writeVarInt(0);
            return;
        }
        stream.writeVarInt(section.getTotal());
        stream.writeByte(section.getPaintType().getId());
        stream.writeByte(section.getUsedBytes());
        section.writeToStream(stream);
    }

    public int getColorIndexBytes() {
        return colorUsedIndex;
    }

    public int getTextureIndexBytes() {
        return textureUsedIndex;
    }

    public boolean isResolved() {
        return resolved;
    }

    private Integer _key(ChunkColorSection section) {
        return section.getPaintType().getId() << 24 | section.getUsedBytes();
    }

    private ChunkColorSection _sectionAt(ISkinPaintType paintType, int usedBytes) {
        return sections.get(paintType.getId() << 24 | usedBytes);
    }

    private ChunkColorSection.Mutable _mutableSectionAt(ISkinPaintType paintType, int usedBytes) {
        return (ChunkColorSection.Mutable) sections.computeIfAbsent(paintType.getId() << 24 | usedBytes, k -> new ChunkColorSection.Mutable(usedBytes, paintType));
    }

    private int _used(int size) {
        for (int i = 1; i < 4; ++i) {
            if ((size >>> i * 8) == 0) {
                return i;
            }
        }
        return 4;
    }

    public static class ColorAccessor implements SliceRandomlyAccessor.Provider<IPaintColor> {

        private final ChunkColorSection section;

        public ColorAccessor(ChunkColorSection section) {
            this.section = section;
        }

        @Override
        public IPaintColor get(int index) {
            return section.getColor(index);
        }

        @Override
        public int getStartIndex() {
            return section.getStartIndex();
        }

        @Override
        public int getEndIndex() {
            return section.getEndIndex();
        }
    }
}

