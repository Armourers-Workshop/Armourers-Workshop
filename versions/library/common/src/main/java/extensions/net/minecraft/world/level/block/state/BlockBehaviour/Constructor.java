package extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[1.20, )")
public class Constructor {

    private static final ImmutableMap<AbstractBlockMaterial, NoteBlockInstrument> INSTRUMENTS = ImmutableMap.<AbstractBlockMaterial, NoteBlockInstrument>builder()
            .put(AbstractBlockMaterial.STONE, NoteBlockInstrument.BASEDRUM)
            .put(AbstractBlockMaterial.GLASS, NoteBlockInstrument.HAT)
            .build();

    private static final ImmutableMap<AbstractBlockMaterial, SoundType> SOUNDS = ImmutableMap.<AbstractBlockMaterial, SoundType>builder()
            .put(AbstractBlockMaterial.STONE, SoundType.STONE)
            .put(AbstractBlockMaterial.GLASS, SoundType.GLASS)
            .build();

    private static final ImmutableMap<AbstractBlockMaterialColor, MapColor> MATERIAL_COLORS = ImmutableMap.<AbstractBlockMaterialColor, MapColor>builder()
            .put(AbstractBlockMaterialColor.NONE, MapColor.NONE)
            .build();

    @Extension
    public static class Properties {

        public static BlockBehaviour.Properties of(@ThisClass Class<?> clazz, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
            NoteBlockInstrument instrument = INSTRUMENTS.get(material);
            SoundType soundType = SOUNDS.get(material);
            MapColor mapColor = MATERIAL_COLORS.get(materialColor);
            return BlockBehaviour.Properties.of().instrument(instrument).sound(soundType).mapColor(mapColor);
        }
    }
}
