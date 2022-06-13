package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.builder.block.*;
import moe.plushie.armourers_workshop.core.block.DyeTableBlock;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.block.SkinningTableBlock;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.block.SkinLibraryBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ModBlocks {

    private static final HashMap<ResourceLocation, Block> REGISTERED_BLOCKS = new HashMap<>();

    public static final Block HOLOGRAM_PROJECTOR = register("hologram-projector", HologramProjectorBlock::new, p -> p.lightLevel(litBlockEmission(13)).strength(5f, 1200f));
    public static final Block SKINNABLE = register("skinnable", SkinnableBlock::new, p -> p.lightLevel(litBlockEmission(15)).strength(1.5f, 6.f).noOcclusion());

    public static final Block DYE_TABLE = register("dye-table", DyeTableBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());
    public static final Block SKINNING_TABLE = register("skinning-table", SkinningTableBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());

    public static final Block SKIN_LIBRARY_CREATIVE = register("skin-library-creative", SkinLibraryBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());
    public static final Block SKIN_LIBRARY = register("skin-library", SkinLibraryBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());
    public static final Block SKIN_LIBRARY_GLOBAL = register("skin-library-global", GlobalSkinLibraryBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());

    public static final Block OUTFIT_MAKER = register("outfit-maker", OutfitMakerBlock::new, p -> p.requiresCorrectToolForDrops().strength(1.5f, 6.f).noOcclusion());
    public static final Block COLOR_MIXER = register("colour-mixer", ColorMixerBlock::new, p -> p.requiresCorrectToolForDrops().strength(1.5f, 6.f));
    public static final Block ARMOURER = register("armourer", ArmourerBlock::new, p -> p.requiresCorrectToolForDrops().strength(1.5f, 6.f));

    public static final Block SKIN_CUBE = register("skin-cube", SkinCubeBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());
    public static final Block SKIN_CUBE_GLASS = registerGlass("skin-cube-glass", SkinCubeBlock::new, p -> p.strength(1.5f, 6.f).noOcclusion());
    public static final Block SKIN_CUBE_GLOWING = register("skin-cube-glowing", SkinCubeBlock::new, p -> p.lightLevel(s -> 15).strength(1.5f, 6.f).noOcclusion());
    public static final Block SKIN_CUBE_GLASS_GLOWING = registerGlass("skin-cube-glass-glowing", SkinCubeBlock::new, p -> p.lightLevel(s -> 15).strength(1.5f, 6.f).noOcclusion());

    public static final Block BOUNDING_BOX = registerGlass("bounding-box", BoundingBoxBlock::new, p -> p.noDrops().noCollission());

    private static ToIntFunction<BlockState> litBlockEmission(int level) {
        return state -> state.getValue(BlockStateProperties.LIT) ? level : 0;
    }

    private static <T extends Block> T register(String name, Function<Block.Properties, T> factory) {
        return register(name, factory, null);
    }

    private static <T extends Block> T register(String name, Function<Block.Properties, T> factory, Consumer<Block.Properties> customizer) {
        Block.Properties properties = AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_RED);
        return register(name, properties, factory, customizer);
    }

    private static <T extends Block> T registerGlass(String name, Function<Block.Properties, T> factory, Consumer<Block.Properties> customizer) {
        Block.Properties properties = AbstractBlock.Properties.of(Material.GLASS, MaterialColor.COLOR_RED);
        return register(name, properties, factory, customizer);
    }

    private static <T extends Block> T register(String name, Block.Properties properties, Function<Block.Properties, T> factory, Consumer<Block.Properties> customizer) {
        if (customizer != null) {
            customizer.accept(properties);
        }
        ResourceLocation registryName = AWCore.resource(name);
        T block = factory.apply(properties);
        block.setRegistryName(registryName);
        REGISTERED_BLOCKS.put(registryName, block);
        return block;
    }

    public static void forEach(Consumer<Block> action) {
        REGISTERED_BLOCKS.values().forEach(action);
    }
}
