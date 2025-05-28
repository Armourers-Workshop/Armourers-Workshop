package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.geometry.ChunkGeometrySerializers;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;

public class ChunkGeometryData implements ChunkVariable {

    private final int id;
    private final ChunkPaletteData palette;
    private final LinkedHashMap<Integer, ChunkGeometrySection> sections = new LinkedHashMap<>();
    private final IdentityHashMap<SkinGeometrySet<?>, Collection<ChunkGeometrySelector>> pending = new IdentityHashMap<>();

    private final LinkedHashMap<SkinGeometryType, ChunkGeometrySerializer.Encoder<?>> encoders = new LinkedHashMap<>();

    public ChunkGeometryData(int id, ChunkPaletteData palette) {
        this.id = id;
        this.palette = palette;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean freeze() {
        // it needs resolve texture first.
        if (!palette.isResolved()) {
            return false;
        }
        // an optimize to reduce order dependence on HashMap.
        int offset = 0;
        var sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(this::_key));
        for (var section : sortedSections) {
            // we can't freeze multiple times.
            if (!section.isResolved()) {
                section.freeze(offset);
            }
            offset += section.getGeometryTotal();
        }
        // cleanup write context.
        pending.clear();
        return true;
    }

    public void readFromStream(ChunkDataInputStream stream) throws IOException {
        int offset = 0;
        while (true) {
            var section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.put(_key(section), section);
            section.freeze(offset);
            offset += section.getGeometryTotal();
        }
    }

    @Override
    public void writeToStream(ChunkOutputStream stream) throws IOException {
        // we need to make sure section in offset order.
        var sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(ChunkGeometrySection::getIndex));
        for (var section : sortedSections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    public SkinGeometrySet<?> readReferenceFromStream(ChunkDataInputStream stream) throws IOException {
        var selectors = new ArrayList<ChunkGeometrySelector>();
        int count = stream.readVarInt();
        for (int i = 0; i < count; ++i) {
            int index = stream.readInt();
            int size = stream.readInt();
            var section = _sectionAt(index);
            if (section != null) {
                int offset = index - section.getIndex();
                selectors.add(new ChunkGeometrySelector(section, offset, offset + size));
            }
        }
        return new ChunkGeometrySliceSet(id, selectors, palette);
    }

    public void writeReferenceToStream(SkinGeometrySet<?> geometries, ChunkDataOutputStream streamIn) throws IOException {
        // for the fast encoder mode,
        // we will reuse the geometry data.
        if (streamIn.getContext().isEnableFastEncoder() && geometries instanceof ChunkGeometrySliceSet slices) {
            var selectors = pending.computeIfAbsent(geometries, k -> slices.getSelectors());
            palette.copyFrom(slices.getPalette());
            streamIn.writeVarInt(selectors.size());
            for (var selector : selectors) {
                var section = selector.getSection();
                sections.put(_key(section), section);
                streamIn.writeVariable(selector);
            }
            return;
        }
        // the geometry set maybe will be occurred reused,
        // so we just need encode the geometry set at first call.
        var selectors = pending.computeIfAbsent(geometries, k -> new ArrayList<>());
        if (selectors.isEmpty()) {
            var changes = _encodeGeometryData(geometries, streamIn.getContext());
            changes.forEach((section, startIndex) -> {
                // we record the once total at the start encode,
                // and then record the total again at the end encode.
                var endIndex = section.getGeometryTotal();
                selectors.add(new ChunkGeometrySelector(section, startIndex, endIndex));
            });
        }
        // write all selector into the stream.
        streamIn.writeVarInt(selectors.size());
        for (var selector : selectors) {
            streamIn.writeVariable(selector);
        }
    }

    private ChunkGeometrySection.Immutable readSectionFromStream(ChunkDataInputStream stream) throws IOException {
        int geometryTotal = stream.readVarInt();
        if (geometryTotal == 0) {
            return null;
        }
        var geometryType = SkinGeometryTypes.byId(stream.readVarInt());
        var geometryOptions = stream.readVarInt();
        var section = new ChunkGeometrySection.Immutable(geometryTotal, geometryOptions, geometryType, palette);
        section.readFromStream(stream);
        return section;
    }

    private void writeSectionToStream(ChunkGeometrySection section, ChunkOutputStream stream) throws IOException {
        // when an empty section, write 0 to indicate skip.
        if (section == null || section.isEmpty()) {
            stream.writeVarInt(0);
            return;
        }
        stream.writeVarInt(section.getGeometryTotal());
        stream.writeVarInt(section.getGeometryType().getId());
        stream.writeVarInt(section.getGeometryOptions());
        section.writeToStream(stream);
    }

    private LinkedHashMap<ChunkGeometrySection, Integer> _encodeGeometryData(SkinGeometrySet<?> geometries, ChunkContext context) throws IOException {
        var changes = new LinkedHashMap<ChunkGeometrySection, Integer>();
        for (var geometry : geometries) {
            var geometryType = geometry.getType();
            var geometryEncoder = _encoderByType(geometryType);
            var geometryOptions = geometryEncoder.begin(Objects.unsafeCast(geometry));
            var section = _mutableSectionAt(geometryType, geometryOptions, context);
            changes.putIfAbsent(section, section.getGeometryTotal()); // section, startIndex
            section.write(geometryEncoder, palette);
        }
        return changes;
    }

    private Integer _key(ChunkGeometrySection section) {
        return _key(section.getGeometryType(), section.getGeometryOptions());
    }

    private Integer _key(SkinGeometryType geometryType, int options) {
        return geometryType.getId() << 24 | options;
    }

    private ChunkGeometrySection _sectionAt(int index) {
        for (var section : sections.values()) {
            int startIndex = section.getIndex();
            int endIndex = section.getGeometryTotal() + startIndex;
            if (startIndex <= index && index < endIndex) {
                return section;
            }
        }
        return null;
    }

    private ChunkGeometrySection.Mutable _mutableSectionAt(SkinGeometryType geometryType, int options, ChunkContext context) {
        var key = _key(geometryType, options);
        var section = sections.computeIfAbsent(key, it -> new ChunkGeometrySection.Mutable(options, geometryType, context));
        return (ChunkGeometrySection.Mutable) section;
    }

    private ChunkGeometrySerializer.Encoder<?> _encoderByType(SkinGeometryType geometryType) {
        return encoders.computeIfAbsent(geometryType, ChunkGeometrySerializers::createEncoder);
    }
}
