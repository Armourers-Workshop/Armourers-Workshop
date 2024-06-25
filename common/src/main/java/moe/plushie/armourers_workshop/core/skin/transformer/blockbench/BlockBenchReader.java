package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackModelReader;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackReader;

import java.io.IOException;
import java.util.Collection;

public class BlockBenchReader extends SkinPackReader {

    public BlockBenchReader(String name, Collection<IResource> resources) {
        super(name, resources);
    }

    public static BlockBenchReader from(String name, Collection<IResource> resources) throws IOException {
        return new BlockBenchReader(name, resources);
    }

    @Override
    public void loadEntityModel(IOConsumer<SkinPackModelReader> consumer) throws IOException {
        var pack = BlockBenchPackLoader.load(this);
        consumer.accept(new BlockBenchModelReader(pack.getName(), pack));
    }
}
