package moe.plushie.armourers_workshop.compat.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compat.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.function.Function;

@Available("[20, )")
public class AbstractBlockBuilder<T extends Block> {

    private static final FastMapper<AbstractBlockMaterial, NoteBlockInstrument> INSTRUMENTS = FastMapper.builder(builder -> {
        builder.put(AbstractBlockMaterial.STONE, NoteBlockInstrument.BASEDRUM);
        builder.put(AbstractBlockMaterial.GLASS, NoteBlockInstrument.HAT);
    });

    private static final FastMapper<AbstractBlockMaterial, SoundType> SOUNDS = FastMapper.builder(builder -> {
        builder.put(AbstractBlockMaterial.STONE, SoundType.STONE);
        builder.put(AbstractBlockMaterial.GLASS, SoundType.GLASS);
    });

    private static final FastMapper<AbstractBlockMaterialColor, MapColor> MATERIAL_COLORS = FastMapper.builder(builder -> {
        builder.put(AbstractBlockMaterialColor.NONE, MapColor.NONE);
    });

    protected final AbstractBlockMaterial material;
    protected final AbstractBlockMaterialColor materialColor;

    protected final Function<BlockBehaviour.Properties, T> factory;
    protected final ArrayList<Function<BlockBehaviour.Properties, BlockBehaviour.Properties>> updaters = new ArrayList<>();

    public AbstractBlockBuilder(Function<BlockBehaviour.Properties, T> factory, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        this.factory = factory;
        this.material = material;
        this.materialColor = materialColor;
    }

    public void apply(Function<BlockBehaviour.Properties, BlockBehaviour.Properties> updater) {
        this.updaters.add(updater);
    }

    public T build(OpenResourceKey registryName) {
        var properties = BlockBehaviour.Properties.of();
        properties = properties.instrument(INSTRUMENTS.getValue(material));
        properties = properties.sound(SOUNDS.getValue(material));
        properties = properties.mapColor(MATERIAL_COLORS.getValue(materialColor));
        for (var updater : updaters) {
            properties = updater.apply(properties);
        }
        return factory.apply(properties.setId(registryName));
    }
}
