package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeEncoder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class ChunkCubeData implements ChunkVariable {

    private final ChunkPaletteData palette;
    private final HashMap<Integer, ChunkCubeSection> sections = new HashMap<>();
    private final HashMap<ChunkCubeSection, Integer> changes = new HashMap<>();
    private final IdentityHashMap<SkinCubes, Collection<ChunkCubeSelector>> pending = new IdentityHashMap<>();

    public ChunkCubeData(ChunkPaletteData palette) {
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
        ArrayList<ChunkCubeSection> sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(this::_key));
        for (ChunkCubeSection section : sortedSections) {
            // we can't freeze multiple times.
            if (!section.isResolved()) {
                section.freeze(offset);
            }
            offset += section.getCubeTotal();
        }
        return true;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        int offset = 0;
        while (true) {
            ChunkCubeSection section = readSectionFromStream(stream);
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
        ArrayList<ChunkCubeSection> sortedSections = new ArrayList<>(sections.values());
        sortedSections.sort(Comparator.comparing(ChunkCubeSection::getIndex));
        for (ChunkCubeSection section : sortedSections) {
            writeSectionToStream(section, stream);
        }
        writeSectionToStream(null, stream);
    }

    public SkinCubes readReferenceFromStream(ChunkInputStream stream) throws IOException {
        ArrayList<ChunkCubeSelector> selectors = new ArrayList<>();
        int count = stream.readVarInt();
        for (int i = 0; i < count; ++i) {
            int index = stream.readInt();
            int size = stream.readInt();
            ChunkCubeSection section = _sectionAt(index);
            if (section != null) {
                int offset = index - section.getIndex();
                selectors.add(new ChunkCubeSelector(section, offset, offset + size));
            }
        }
        return new ChunkCubeSlices(selectors, palette);
    }

    public void writeReferenceToStream(SkinCubes cubes, ChunkOutputStream streamIn) throws IOException {
        // for the fast encoder mode,
        // we will reuse the cube data.
        if (streamIn.getContext().isEnableFastEncoder() && cubes instanceof ChunkCubeSlices) {
            ChunkCubeSlices slices = ObjectUtils.unsafeCast(cubes);
            Collection<ChunkCubeSelector> selectors = pending.computeIfAbsent(cubes, k -> slices.getSelectors());
            palette.copyFrom(slices.getPalette());
            streamIn.writeVarInt(selectors.size());
            for (ChunkCubeSelector selector : selectors) {
                ChunkCubeSection section = selector.section;
                sections.put(_key(section), section);
                streamIn.writeVariable(selector);
            }
            return;
        }
        Collection<ChunkCubeSelector> selectors = pending.computeIfAbsent(cubes, k -> new ArrayList<>());
        // the cubes maybe will be occurred reused,
        // so we just need encode the cubes at first call.
        if (selectors.isEmpty()) {
            _encodeCubeData(cubes, streamIn.getContext());
            changes.forEach((buffer, from) -> selectors.add(new ChunkCubeSelector(buffer, from, buffer.getCubeTotal())));
            changes.clear();
        }
        // write all selector into the stream.
        streamIn.writeVarInt(selectors.size());
        for (ChunkCubeSelector selector : selectors) {
            streamIn.writeVariable(selector);
        }
    }

    private ChunkCubeSection.Immutable readSectionFromStream(ChunkInputStream stream) throws IOException {
        int cubeTotal = stream.readVarInt();
        if (cubeTotal == 0) {
            return null;
        }
        ISkinCubeType cubeType = SkinCubeTypes.byId(stream.readVarInt());
        int options = stream.readVarInt();
        ChunkCubeSection.Immutable section = new ChunkCubeSection.Immutable(cubeTotal, options, cubeType, palette);
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

    private void _encodeCubeData(SkinCubes cubes, ChunkContext context) throws IOException {
        int size = cubes.getCubeTotal();
        for (int i = 0; i < size; ++i) {
            ISkinCube cube = cubes.getCube(i);
            ISkinCubeType cubeType = cube.getType();
            ChunkCubeEncoder cubeEncoder = ChunkCubeCoders.createEncoder(cubeType);
            ChunkCubeSection.Mutable section = _mutableSectionAt(cubeType, cubeEncoder.begin(cube), context);
            changes.putIfAbsent(section, section.getCubeTotal());
            section.write(cubeEncoder, palette);
        }
    }

    private Integer _key(ChunkCubeSection section) {
        return _key(section.getCubeType(), section.getCubeOptions());
    }

    private Integer _key(ISkinCubeType cubeType, int options) {
        return cubeType.getId() << 24 | options;
    }

    private ChunkCubeSection _sectionAt(int index) {
        for (ChunkCubeSection section : sections.values()) {
            int startIndex = section.getIndex();
            int endIndex = section.getCubeTotal() + startIndex;
            if (startIndex <= index && index < endIndex) {
                return section;
            }
        }
        return null;
    }

    private ChunkCubeSection.Mutable _mutableSectionAt(ISkinCubeType cubeType, int options, ChunkContext context) {
        Integer key = _key(cubeType, options);
        ChunkCubeSection section = sections.computeIfAbsent(key, it -> new ChunkCubeSection.Mutable(options, cubeType, context));
        return (ChunkCubeSection.Mutable) section;
    }
}
