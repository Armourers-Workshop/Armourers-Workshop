package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.registry.IBlockBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.builder.block.AdvancedSkinBuilderBlock;
import moe.plushie.armourers_workshop.builder.block.ArmourerBlock;
import moe.plushie.armourers_workshop.builder.block.BoundingBoxBlock;
import moe.plushie.armourers_workshop.builder.block.ColorMixerBlock;
import moe.plushie.armourers_workshop.builder.block.OutfitMakerBlock;
import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterial;
import moe.plushie.armourers_workshop.compatibility.api.AbstractBlockMaterialColor;
import moe.plushie.armourers_workshop.core.block.DyeTableBlock;
import moe.plushie.armourers_workshop.core.block.HologramProjectorBlock;
import moe.plushie.armourers_workshop.core.block.SkinnableBlock;
import moe.plushie.armourers_workshop.core.block.SkinningTableBlock;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.block.GlobalSkinLibraryBlock;
import moe.plushie.armourers_workshop.library.block.SkinLibraryBlock;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ModBlocks {

    public static final IRegistryKey<Block> HOLOGRAM_PROJECTOR = normal(HologramProjectorBlock::new).lightLevel(lit(13)).strength(5f, 1200f).noOcclusion().build("hologram-projector");
    public static final IRegistryKey<Block> SKINNABLE = half(SkinnableBlock::new).dynamicShape().lightLevel(lit(15)).build("skinnable");

    public static final IRegistryKey<Block> DYE_TABLE = half(DyeTableBlock::new).build("dye-table");
    public static final IRegistryKey<Block> SKINNING_TABLE = half(SkinningTableBlock::new).build("skinning-table");

    public static final IRegistryKey<Block> SKIN_LIBRARY_CREATIVE = half(SkinLibraryBlock::new).build("skin-library-creative");
    public static final IRegistryKey<Block> SKIN_LIBRARY = half(SkinLibraryBlock::new).build("skin-library");
    public static final IRegistryKey<Block> SKIN_LIBRARY_GLOBAL = half(GlobalSkinLibraryBlock::new).build("skin-library-global");

    public static final IRegistryKey<Block> OUTFIT_MAKER = half(OutfitMakerBlock::new).build("outfit-maker");
    public static final IRegistryKey<Block> COLOR_MIXER = normal(ColorMixerBlock::new).bind(() -> RenderType::cutout).build("colour-mixer");
    public static final IRegistryKey<Block> ARMOURER = normal(ArmourerBlock::new).build("armourer");
    public static final IRegistryKey<Block> ADVANCED_SKIN_BUILDER = normal(AdvancedSkinBuilderBlock::new).build("advanced-skin-builder");

    public static final IRegistryKey<Block> SKIN_CUBE = half(SkinCubeBlock::new).build("skin-cube");
    public static final IRegistryKey<Block> SKIN_CUBE_GLASS = glass(SkinCubeBlock::new).build("skin-cube-glass");
    public static final IRegistryKey<Block> SKIN_CUBE_GLOWING = half(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glowing");
    public static final IRegistryKey<Block> SKIN_CUBE_GLASS_GLOWING = glass(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glass-glowing");

    public static final IRegistryKey<Block> BOUNDING_BOX = glass(BoundingBoxBlock::new).noDrops().noCollission().build("bounding-box");

    private static ToIntFunction<BlockState> lit(int level) {
        return state -> state.getValue(BlockStateProperties.LIT) ? level : 0;
    }

    private static <T extends Block> IBlockBuilder<Block> create(Function<BlockBehaviour.Properties, T> supplier, AbstractBlockMaterial material, AbstractBlockMaterialColor materialColor) {
        return ObjectUtils.unsafeCast(BuilderManager.getInstance().createBlockBuilder(supplier, material, materialColor).strength(1.5f, 6.f));
    }

    private static <T extends Block> IBlockBuilder<Block> normal(Function<BlockBehaviour.Properties, T> supplier) {
        return create(supplier, AbstractBlockMaterial.STONE, AbstractBlockMaterialColor.NONE);
    }

    private static <T extends Block> IBlockBuilder<Block> half(Function<BlockBehaviour.Properties, T> supplier) {
        return normal(supplier).noOcclusion().bind(() -> RenderType::cutout);
    }

    private static <T extends Block> IBlockBuilder<Block> glass(Function<BlockBehaviour.Properties, T> supplier) {
        return create(supplier, AbstractBlockMaterial.GLASS, AbstractBlockMaterialColor.NONE).noOcclusion().bind(() -> RenderType::translucent);
    }

    public static void init() {
    }
}
