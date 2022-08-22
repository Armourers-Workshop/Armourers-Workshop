package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IBlockEntityKey;
import moe.plushie.armourers_workshop.api.common.IBlockEntitySupplier;
import moe.plushie.armourers_workshop.api.common.builder.IBlockEntityBuilder;
import moe.plushie.armourers_workshop.builder.blockentity.*;
import moe.plushie.armourers_workshop.builder.client.render.ArmourerBlockEntityRenderer;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.blockentity.DyeTableBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.render.HologramProjectorBlockEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinnableBlockEntityRenderer;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import moe.plushie.armourers_workshop.library.blockentity.GlobalSkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.blockentity.SkinLibraryBlockEntity;
import moe.plushie.armourers_workshop.library.client.render.GlobalSkinLibraryBlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@SuppressWarnings("unused")
public final class ModBlockEntities {

    public static final IBlockEntityKey<HologramProjectorBlockEntity> HOLOGRAM_PROJECTOR = create(HologramProjectorBlockEntity::new).of(ModBlocks.HOLOGRAM_PROJECTOR).bind(() -> HologramProjectorBlockEntityRenderer::new).build("hologram-projector");
    public static final IBlockEntityKey<OutfitMakerBlockEntity> OUTFIT_MAKER = create(OutfitMakerBlockEntity::new).of(ModBlocks.OUTFIT_MAKER).build("outfit-maker");
    public static final IBlockEntityKey<DyeTableBlockEntity> DYE_TABLE = create(DyeTableBlockEntity::new).of(ModBlocks.DYE_TABLE).build("dye-table");

    public static final IBlockEntityKey<ColorMixerBlockEntity> COLOR_MIXER = create(ColorMixerBlockEntity::new).of(ModBlocks.COLOR_MIXER).build("colour-mixer");
    public static final IBlockEntityKey<ArmourerBlockEntity> ARMOURER = create(ArmourerBlockEntity::new).of(ModBlocks.ARMOURER).bind(() -> ArmourerBlockEntityRenderer::new).build("armourer");
    public static final IBlockEntityKey<AdvancedSkinBuilderBlockEntity> ADVANCED_SKIN_BUILDER = create(AdvancedSkinBuilderBlockEntity::new).of(ModBlocks.ADVANCED_SKIN_BUILDER).build("advanced-skin-builder");

    public static final IBlockEntityKey<SkinLibraryBlockEntity> SKIN_LIBRARY = create(SkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY).build("skin-library");
    public static final IBlockEntityKey<GlobalSkinLibraryBlockEntity> SKIN_LIBRARY_GLOBAL = create(GlobalSkinLibraryBlockEntity::new).of(ModBlocks.SKIN_LIBRARY_GLOBAL).bind(() -> GlobalSkinLibraryBlockEntityRenderer::new).build("skin-library-global");

    public static final IBlockEntityKey<SkinnableBlockEntity> SKINNABLE_CUBE = create(SkinnableBlockEntity::new).of(ModBlocks.SKINNABLE).bind(() -> SkinnableBlockEntityRenderer::new).build("skinnable");
    public static final IBlockEntityKey<BoundingBoxBlockEntity> BOUNDING_BOX = create(BoundingBoxBlockEntity::new).of(ModBlocks.BOUNDING_BOX).bind(() -> SkinCubeBlockEntityRenderer::new).build("bounding-box");
    public static final IBlockEntityKey<SkinCubeBlockEntity> SKIN_CUBE = create(SkinCubeBlockEntity::new).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).bind(() -> SkinCubeBlockEntityRenderer::new).build("skin-cube");

    // legacy
    private static final IBlockEntityKey<SkinnableBlockEntity> SKINNABLE_CUBE_SR = createLegacy(SKINNABLE_CUBE).of(ModBlocks.SKINNABLE).build("skinnable-sr");
    private static final IBlockEntityKey<BoundingBoxBlockEntity> BOUNDING_BOX_SR = createLegacy(BOUNDING_BOX).of(ModBlocks.BOUNDING_BOX).build("bounding-box-sr");
    private static final IBlockEntityKey<SkinCubeBlockEntity> SKIN_CUBE_SR = createLegacy(SKIN_CUBE).of(ModBlocks.SKIN_CUBE).of(ModBlocks.SKIN_CUBE_GLASS).of(ModBlocks.SKIN_CUBE_GLASS_GLOWING).of(ModBlocks.SKIN_CUBE_GLOWING).build("skin-cube-sr");

    private static <T extends BlockEntity> IBlockEntityBuilder<T> create(IBlockEntitySupplier<T> supplier) {
        return BuilderManager.getInstance().createBlockEntityBuilder(supplier);
    }

    private static <T extends BlockEntity> IBlockEntityBuilder<T> createLegacy(IBlockEntityKey<T> entityType) {
        return create((entityType1, blockPos, blockState) -> entityType.create(null, blockPos, blockState));
    }

    public static void init() {
    }
}
