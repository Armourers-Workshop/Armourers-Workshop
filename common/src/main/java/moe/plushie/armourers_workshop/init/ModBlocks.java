package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.other.builder.IBlockBuilder;
import moe.plushie.armourers_workshop.api.other.IRegistryObject;
import moe.plushie.armourers_workshop.builder.block.*;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.function.Function;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ModBlocks {

    public static final IRegistryObject<Block> HOLOGRAM_PROJECTOR = normal(HologramProjectorBlock::new).lightLevel(lit(13)).strength(5f, 1200f).build("hologram-projector");
    public static final IRegistryObject<Block> SKINNABLE = half(SkinnableBlock::new).lightLevel(lit(15)).build("skinnable");

    public static final IRegistryObject<Block> DYE_TABLE = half(DyeTableBlock::new).build("dye-table");
    public static final IRegistryObject<Block> SKINNING_TABLE = half(SkinningTableBlock::new).build("skinning-table");

    public static final IRegistryObject<Block> SKIN_LIBRARY_CREATIVE = half(SkinLibraryBlock::new).build("skin-library-creative");
    public static final IRegistryObject<Block> SKIN_LIBRARY = half(SkinLibraryBlock::new).build("skin-library");
    public static final IRegistryObject<Block> SKIN_LIBRARY_GLOBAL = half(GlobalSkinLibraryBlock::new).build("skin-library-global");

    public static final IRegistryObject<Block> OUTFIT_MAKER = half(OutfitMakerBlock::new).build("outfit-maker");
    public static final IRegistryObject<Block> COLOR_MIXER = normal(ColorMixerBlock::new).bind(() -> RenderType::cutout).build("colour-mixer");
    public static final IRegistryObject<Block> ARMOURER = normal(ArmourerBlock::new).build("armourer");

    public static final IRegistryObject<Block> SKIN_CUBE = half(SkinCubeBlock::new).build("skin-cube");
    public static final IRegistryObject<Block> SKIN_CUBE_GLASS = glass(SkinCubeBlock::new).build("skin-cube-glass");
    public static final IRegistryObject<Block> SKIN_CUBE_GLOWING = half(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glowing");
    public static final IRegistryObject<Block> SKIN_CUBE_GLASS_GLOWING = glass(SkinCubeBlock::new).lightLevel(15).build("skin-cube-glass-glowing");

    public static final IRegistryObject<Block> BOUNDING_BOX = glass(BoundingBoxBlock::new).noDrops().noCollission().build("bounding-box");

    private static ToIntFunction<BlockState> lit(int level) {
        return state -> state.getValue(BlockStateProperties.LIT) ? level : 0;
    }

    private static <T extends Block> IBlockBuilder<Block> create(Function<BlockBehaviour.Properties, T> supplier, Material material, MaterialColor materialColor) {
        return ObjectUtils.unsafeCast(BuilderManager.getInstance().createBlockBuilder(supplier, material, materialColor));
    }

    private static <T extends Block> IBlockBuilder<Block> normal(Function<BlockBehaviour.Properties, T> supplier) {
        return create(supplier, Material.STONE, MaterialColor.COLOR_RED).strength(1.5f, 6.f);
    }

    private static <T extends Block> IBlockBuilder<Block> half(Function<BlockBehaviour.Properties, T> supplier) {
        return normal(supplier).noOcclusion().bind(() -> RenderType::cutout);
    }

    private static <T extends Block> IBlockBuilder<Block> glass(Function<BlockBehaviour.Properties, T> supplier) {
        return create(supplier, Material.GLASS, MaterialColor.COLOR_BLACK).strength(1.5f, 6.f).noOcclusion().bind(() -> RenderType::translucent);
    }

    public static void init() {
    }
}
