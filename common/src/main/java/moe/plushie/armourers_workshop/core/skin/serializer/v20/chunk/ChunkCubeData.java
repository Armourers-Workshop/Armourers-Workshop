package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ChunkCubeData implements ChunkVariable {

    private static final AtomicInteger ID = new AtomicInteger(0);

    private final int owner;
    private final ChunkPaletteData palette;
    private final LinkedHashMap<Integer, ChunkCubeSection> sections = new LinkedHashMap<>();
    private final IdentityHashMap<SkinCubes, Collection<ChunkCubeSelector>> pending = new IdentityHashMap<>();

    public ChunkCubeData(ChunkPaletteData palette) {
        this.owner = ID.incrementAndGet();
        this.palette = palette;
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
            offset += section.getCubeTotal();
        }
        // cleanup write context.
        pending.clear();
        return true;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        int offset = 0;
        while (true) {
            var section = readSectionFromStream(stream);
            if (section == null) {
                break;
            }
            sections.put(_key(section), section);
            section.freeze(offset);
            offset += section.getCubeTotal();
        }
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        // we need to make sure section in offset order.
        var sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(ChunkCubeSection::getIndex));
        for (var section : sortedSections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    public SkinCubes readReferenceFromStream(ChunkInputStream stream) throws IOException {
        var selectors = new ArrayList<ChunkCubeSelector>();
        int count = stream.readVarInt();
        for (int i = 0; i < count; ++i) {
            int index = stream.readInt();
            int size = stream.readInt();
            var section = _sectionAt(index);
            if (section != null) {
                int offset = index - section.getIndex();
                selectors.add(new ChunkCubeSelector(section, offset, offset + size));
            }
        }
        return new ChunkCubeSlices(owner, selectors, palette);
    }

    public void writeReferenceToStream(SkinCubes cubes, ChunkOutputStream streamIn) throws IOException {
        // for the fast encoder mode,
        // we will reuse the cube data.
        if (streamIn.getContext().isEnableFastEncoder() && cubes instanceof ChunkCubeSlices slices) {
            var selectors = pending.computeIfAbsent(cubes, k -> slices.getSelectors());
            palette.copyFrom(slices.getPalette());
            streamIn.writeVarInt(selectors.size());
            for (var selector : selectors) {
                var section = selector.getSection();
                sections.put(_key(section), section);
                streamIn.writeVariable(selector);
            }
            return;
        }
        // the cubes maybe will be occurred reused,
        // so we just need encode the cubes at first call.
        var selectors = pending.computeIfAbsent(cubes, k -> new ArrayList<>());
        if (selectors.isEmpty()) {
            var changes = _encodeCubeData(cubes, streamIn.getContext());
            changes.forEach((section, startIndex) -> {
                // we record the once total at the start encode,
                // and then record the total again at the end encode.
                var endIndex = section.getCubeTotal();
                selectors.add(new ChunkCubeSelector(section, startIndex, endIndex));
            });
        }
        // write all selector into the stream.
        streamIn.writeVarInt(selectors.size());
        for (var selector : selectors) {
            streamIn.writeVariable(selector);
        }
    }

    private ChunkCubeSection.Immutable readSectionFromStream(ChunkInputStream stream) throws IOException {
        int cubeTotal = stream.readVarInt();
        if (cubeTotal == 0) {
            return null;
        }
        var cubeType = SkinCubeTypes.byId(stream.readVarInt());
        var options = stream.readVarInt();
        var section = new ChunkCubeSection.Immutable(cubeTotal, options, cubeType, palette);
        section.readFromStream(stream);
        return section;
    }

    private void writeSectionToStream(ChunkCubeSection section, IOutputStream stream) throws IOException {
        // when an empty section, write 0 to indicate skip.
        if (section == null || section.isEmpty()) {
            stream.writeVarInt(0);
            return;
        }
        stream.writeVarInt(section.getCubeTotal());
        stream.writeVarInt(section.getCubeType().getId());
        stream.writeVarInt(section.getCubeOptions());
        section.writeToStream(stream);
    }

    private LinkedHashMap<ChunkCubeSection, Integer> _encodeCubeData(SkinCubes cubes, ChunkContext context) throws IOException {
        var changes = new LinkedHashMap<ChunkCubeSection, Integer>();
        int size = cubes.getCubeTotal();
        for (int i = 0; i < size; ++i) {
            var cube = cubes.getCube(i);
            var cubeType = cube.getType();
            var cubeEncoder = ChunkCubeCoders.createEncoder(cubeType);
            var section = _mutableSectionAt(cubeType, cubeEncoder.begin(cube), context);
            changes.putIfAbsent(section, section.getCubeTotal()); // section, startIndex
            section.write(cubeEncoder, palette);
        }
        return changes;
    }

    private Integer _key(ChunkCubeSection section) {
        return _key(section.getCubeType(), section.getCubeOptions());
    }

    private Integer _key(ISkinCubeType cubeType, int options) {
        return cubeType.getId() << 24 | options;
    }

    private ChunkCubeSection _sectionAt(int index) {
        for (var section : sections.values()) {
            int startIndex = section.getIndex();
            int endIndex = section.getCubeTotal() + startIndex;
            if (startIndex <= index && index < endIndex) {
                return section;
            }
        }
        return null;
    }

    private ChunkCubeSection.Mutable _mutableSectionAt(ISkinCubeType cubeType, int options, ChunkContext context) {
        var key = _key(cubeType, options);
        var section = sections.computeIfAbsent(key, it -> new ChunkCubeSection.Mutable(options, cubeType, context));
        return (ChunkCubeSection.Mutable) section;
    }
}
